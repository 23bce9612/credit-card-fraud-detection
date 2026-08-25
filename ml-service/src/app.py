from fastapi import FastAPI
from pydantic import BaseModel
import joblib
import os
import pandas as pd


app = FastAPI()


# -----------------------------------
# Load application-compatible model
# -----------------------------------

BASE_DIR = os.path.dirname(
    os.path.dirname(os.path.abspath(__file__))
)

MODEL_PATH = os.path.join(
    BASE_DIR,
    "models",
    "app_fraud_detection_model.joblib"
)

model = joblib.load(MODEL_PATH)


# -----------------------------------
# Request model
# -----------------------------------

class TransactionRequest(BaseModel):

    amount: float

    historicalAverageAmount: float

    recentTransactionCount: int

    recentTotalAmount: float

    isNewLocation: int

    isNewDevice: int

    isNewIpAddress: int


# -----------------------------------
# Basic endpoints
# -----------------------------------

@app.get("/")
def home():

    return {
        "message": "Credit Card Fraud Detection ML Service is running"
    }


@app.get("/health")
def health():

    return {
        "status": "UP",
        "model_loaded": True
    }


# -----------------------------------
# Prediction endpoint
# -----------------------------------

@app.post("/predict")
def predict(transaction: TransactionRequest):

    # Convert request into DataFrame
    data = pd.DataFrame([
        transaction.model_dump()
    ])

    # Get prediction
    prediction = model.predict(data)[0]

    # Get fraud probability
    probabilities = model.predict_proba(data)[0]

    fraud_probability = float(
        probabilities[1]
    )

    # Convert prediction
    result = (
        "FRAUD"
        if prediction == 1
        else "GENUINE"
    )

    return {
        "prediction": result,
        "fraudProbability": fraud_probability
    }