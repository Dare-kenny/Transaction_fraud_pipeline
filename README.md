# MediAdapt Backend

MediAdapt is a medication adherence platform that reminds patients when to take
their medication and **adapts reminder times to each patient's real behaviour**.
It watches how patients actually respond to reminders (on time, late, or
dismissed) and shifts future reminders earlier so they land closer to the
moment the patient is actually likely to act — while also detecting when a
patient's routine has drifted and re-learning from scratch.

The backend has two cooperating services:

| Service | Stack | Responsibility |
|---|---|---|
| **`mediAdapt-api`** | Java 17, Spring Boot | REST API, auth, domain data (users, medications, schedules, dose logs), push notifications |
| **`mediAdapt-engine`** (`Intelligent_agent/`) | Python, FastAPI | Adaptive scheduling engine — computes optimal reminder times from dose-log history |

## How the adaptive engine works

1. Patients log each dose as `TAKEN`, `DISMISSED`, or `MISSED` via the API.
2. Once a schedule has at least `MIN_LOGS_REQUIRED` qualifying logs (`TAKEN` +
   `DISMISSED`; `MISSED` is excluded since it carries no real response time),
   the engine computes the average delay between the scheduled time and the
   patient's response.
3. The reminder is shifted earlier by that average delay (capped at
   `MAX_SHIFT_MINUTES`) and written back as `adapted_time_of_day`.
4. **Drift detection**: once enough logs exist, the engine compares the
   patient's recent response delay against their all-time average. If it
   diverges by more than `DRIFT_THRESHOLD_MIN`, the adapted time is reset to
   `NULL` so the schedule re-learns from fresh data instead of staying pinned
   to a stale pattern.

The Spring Boot app calls the engine over HTTP (`POST /run-engine`) on a
schedule (`AdaptiveEngineScheduler`) and also exposes manual trigger endpoints.

## Domain model

- **Users** have one role: `PATIENT`, `DOCTOR`, or `HOSPITAL`.
- **Hospitals** invite doctors via invite codes; **doctors** invite/assign
  patients via invite codes.
- **Doctors** prescribe **medications** and **schedules** for their patients.
- **Patients** log doses against their schedules; the adaptive engine tunes
  reminder timing from that log history.
- **Push notifications** are delivered via Firebase Cloud Messaging (FCM).

## API overview

All endpoints are under `/api` and secured with JWT bearer auth (see
`SecurityConfig` / `JwtAuthFilter`) unless noted otherwise.

**Auth** — `/api/auth`
- `POST /login`
- `POST /register/patient`, `POST /register/doctor`, `POST /register/hospital`
- `POST /verify-invite`, `POST /verify-doctor-invite`

**Patient** — `/api/patient`
- `GET /me/schedules`, `GET /me/doctor`
- `POST /me/dose-logs`, `GET /me/dose-logs`
- `POST /me/adaptive/run`

**Doctor** — `/api/doctor`
- `POST /me/patients/{patientId}/medications`, `GET /me/patients/{patientId}/medications`
- `POST /me/patients/assign/{patientId}`, `DELETE /me/patients/unassign/{patientId}`
- `GET /me/patients`, `GET /me/hospital`, `POST /me/invite`
- `GET /me/patients/{patientId}/dose-logs`

**Hospital** — `/api/hospital/me`
- `GET /doctors`, `GET /doctors/{doctorId}/patients`
- `GET /active-schedule-count`
- `GET /doctor-invites`, `POST /doctor-invite`

**Users** — `/api/users`
- `POST /me/fcm-token`

**Adaptive engine** — `/api/adaptive`
- `POST /run`

## Tech stack

- Java 17, Spring Boot 4 (Web MVC, Data JPA, Validation, Security)
- MySQL (via `mysql-connector-j`), Liquibase for schema migrations
- JWT auth (`jjwt`), Firebase Admin SDK for push notifications
- Python 3, FastAPI, `mysql-connector-python` for the adaptive engine
- Docker for the API image; deployable to Render (`render.yaml`)

## Running locally

### 1. Database

Create a MySQL database (defaults to `medication_monitoring_system`) and let
Liquibase apply the schema on startup, or point `DB_URL` at your own instance.

### 2. Adaptive engine (Python)

```bash
cd Intelligent_agent
pip install -r requirements.txt
python engine_server.py   # serves on http://localhost:8000
```

Environment variables: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`,
`DB_PASSWORD`, `DB_SSL`, `MIN_LOGS_REQUIRED`, `MAX_SHIFT_MINUTES`,
`DRIFT_WINDOW_LOGS`, `DRIFT_THRESHOLD_MIN`.

### 3. Spring Boot API

```bash
./mvnw spring-boot:run
```

Runs on `http://localhost:8080` by default. Key environment variables:

| Variable | Purpose |
|---|---|
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | MySQL connection |
| `JWT_SECRET` | JWT signing secret |
| `ADAPTIVE_ENGINE_URL` | URL of the running Python engine (defaults to `http://localhost:8000`) |

Start the Python engine before the Spring Boot app so scheduled adaptation
runs succeed from the first tick.

### 4. Tests

```bash
./mvnw test
```

## Deployment

`render.yaml` deploys both services to [Render](https://render.com):
`mediAdapt-engine` (Python, free plan) and `mediAdapt-api` (Docker image built
from the root `Dockerfile`). Set the `sync: false` env vars (DB credentials,
`JWT_SECRET`, `ADAPTIVE_ENGINE_URL`) in the Render dashboard after the first
deploy.
