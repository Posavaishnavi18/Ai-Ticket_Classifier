# AI-Powered Support Ticket Classifier

A Spring Boot REST API that automatically classifies incoming support tickets by **category** and **urgency** using Google's Gemini API, then persists them in MySQL via JPA.

## Overview

Support teams receive tickets in free text. Instead of manually tagging each one, this service sends the subject and description to Gemini, gets back a category (`Billing`, `Technical`, `Account`, `General`) and an urgency level (`Low`, `Medium`, `High`), and stores the classified ticket in the database — all in one API call.

## Tech Stack

- **Java 25** / **Spring Boot 4**
- **Spring Data JPA** + **Hibernate** for persistence
- **MySQL** as the database
- **Jackson 3** (`tools.jackson`) for JSON processing
- **Google Gemini API** (Gemini 3.6 Flash) for AI classification
- **HTML/CSS/JavaScript** frontend, served directly from Spring Boot
- Built with **Eclipse IDE** and **Maven**

## Project Structure

```
com.vaishnavi.ticketclassifier
├── controller
│   └── TicketController.java        # REST endpoints
├── service
│   ├── TicketService.java           # CRUD business logic
│   └── GeminiClassifierService.java # Gemini API integration
├── repository
│   └── TicketRepository.java        # Spring Data JPA repository
└── model
    └── Ticket.java                  # JPA entity

src/main/resources/static/index.html # Web dashboard (served at localhost:8081)
```

## API Endpoints

| Method | Endpoint             | Description                                      |
|--------|-----------------------|---------------------------------------------------|
| POST   | `/api/tickets`         | Create a ticket — classifies it via Gemini and saves it |
| GET    | `/api/tickets`         | Get all tickets                                  |
| GET    | `/api/tickets/{id}`    | Get a single ticket by id (404 if not found)      |
| PUT    | `/api/tickets/{id}`    | Update a ticket's fields (404 if not found)       |
| DELETE | `/api/tickets/{id}`    | Delete a ticket (404 if not found)                |

### Example: Create a ticket

**Request**
```http
POST /api/tickets
Content-Type: application/json

{
  "subject": "Payment failed",
  "description": "My credit card was charged but the payment failed"
}
```

**Response**
```json
{
  "id": 1,
  "subject": "Payment failed",
  "description": "My credit card was charged but the payment failed",
  "category": "Billing",
  "urgency": "High",
  "createdAt": "2026-09-17T13:41:56.896422"
}
```

## Setup

1. **Clone the repo** and open it in Eclipse (or your preferred IDE).
2. **Create a MySQL database:**
   ```sql
   CREATE DATABASE ticketclassifier;
   ```
3. **Create `src/main/resources/application.properties`** using `application.properties.example` as a template, filling in your own values:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ticketclassifier
   spring.datasource.username=YOUR_MYSQL_USERNAME
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   spring.jpa.hibernate.ddl-auto=update

   gemini.api.key=YOUR_GEMINI_API_KEY
   ```
4. **Get a Gemini API key** from [Google AI Studio](https://aistudio.google.com/).
5. **Run the app:** Right-click `TicketclassifierApplication.java` → Run As → Spring Boot App.
6. Open a browser and go to **`http://localhost:8081`** — the web dashboard loads automatically.

## Testing the API

You can test endpoints with `curl`, Postman, or any REST client.

```bash
# Create a ticket
curl -X POST http://localhost:8081/api/tickets \
  -H "Content-Type: application/json" \
  -d "{\"subject\":\"App crashing\",\"description\":\"The app crashes on settings screen\"}"

# Get all tickets
curl http://localhost:8081/api/tickets

# Get one ticket
curl http://localhost:8081/api/tickets/1

# Update a ticket
curl -X PUT http://localhost:8081/api/tickets/1 \
  -H "Content-Type: application/json" \
  -d "{\"subject\":\"Updated subject\"}"

# Delete a ticket
curl -X DELETE http://localhost:8081/api/tickets/1
```

## Notes

- The Gemini model name used in `GeminiClassifierService.java` may need periodic updates, as Google regularly deprecates older model versions. If you get a `404` error mentioning a model is "no longer available," check Gemini's model list and update the model name in the request URL.
- This project uses **Jackson 3.x**, which renamed its base package from `com.fasterxml.jackson` to `tools.jackson`, and requires `ObjectMapper` to be constructed via `JsonMapper.builder().build()` rather than `new ObjectMapper()`.
- The web dashboard (`index.html`) is served directly by Spring Boot from `src/main/resources/static`, so there's no separate frontend server or CORS configuration needed.

## Possible Future Improvements

- Add pagination and filtering (e.g. get all tickets by category or urgency)
- Add user authentication so only authorized users can create/manage tickets
- Deploy to a cloud host so the dashboard is reachable outside localhost
- Write unit tests for `TicketService` and `GeminiClassifierService`
