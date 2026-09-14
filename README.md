# FraudGuard — Credit Card Fraud Detection

A comprehensive, real-time credit card fraud detection system. This project includes a machine learning prediction service, a Spring Boot Java backend for data and user management, and a modern React (Vite) frontend.

## Project Structure

- `/frontend`: React + Vite web application with a modern dark glassmorphism design.
- `/backend`: Spring Boot (Java 21) REST API managing users, authentication (JWT), and transactions.
- `/ml-service`: Python FastAPI service hosting the trained machine learning model for predicting fraud probabilities.

## How to Run the Application Locally

To fully run this application on your local machine, you need to start all three services.

### 1. Start the ML Service (Python)
The ML service provides the prediction endpoints and runs on port `8000`.
```bash
cd ml-service
pip install -r requirements.txt
uvicorn src.app:app --reload
```

### 2. Start the Backend (Java)
The backend manages the database and routes, and runs on port `8080`.
```bash
cd backend
./mvnw spring-boot:run
```
*(Make sure your MySQL database is running and configured properly in `application.properties`)*

### 3. Start the Frontend (React)
The frontend is the user interface and runs on port `3000`.
```bash
cd frontend
npm install
npm run dev
```

## Accessing the App

Once all services are running, open your browser and navigate to:
**http://localhost:3000**

### Default Admin Credentials:
- **Email:** `admin@frauddetection.com`
- **Password:** `Admin@123`
