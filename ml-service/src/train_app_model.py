import os
import joblib
import pandas as pd
import numpy as np

from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline

from sklearn.ensemble import RandomForestClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import VotingClassifier

from sklearn.metrics import (
    classification_report,
    confusion_matrix,
    roc_auc_score
)


# --------------------------------------------------
# 1. Load original dataset
# --------------------------------------------------

DATA_PATH = "data/creditcard.csv"

df = pd.read_csv(DATA_PATH)

print("Original dataset shape:", df.shape)


# --------------------------------------------------
# 2. Create application-compatible features
# --------------------------------------------------

print("\nCreating application-compatible features...")


# Amount
amount = df["Amount"]


# Historical average amount
# Simulated baseline spending behavior
historical_average_amount = (
    amount.rolling(
        window=50,
        min_periods=1
    ).mean()
)


# Recent transaction count
# Derived synthetic transaction frequency
recent_transaction_count = np.random.randint(
    1,
    15,
    size=len(df)
)


# Recent total amount
recent_total_amount = (
    historical_average_amount
    * recent_transaction_count
)


# New location
# Higher probability for fraud transactions
is_new_location = np.where(
    df["Class"] == 1,
    np.random.choice(
        [0, 1],
        size=len(df),
        p=[0.30, 0.70]
    ),
    np.random.choice(
        [0, 1],
        size=len(df),
        p=[0.90, 0.10]
    )
)


# New device
is_new_device = np.where(
    df["Class"] == 1,
    np.random.choice(
        [0, 1],
        size=len(df),
        p=[0.25, 0.75]
    ),
    np.random.choice(
        [0, 1],
        size=len(df),
        p=[0.92, 0.08]
    )
)


# New IP address
is_new_ip_address = np.where(
    df["Class"] == 1,
    np.random.choice(
        [0, 1],
        size=len(df),
        p=[0.30, 0.70]
    ),
    np.random.choice(
        [0, 1],
        size=len(df),
        p=[0.90, 0.10]
    )
)


# --------------------------------------------------
# 3. Create new feature DataFrame
# --------------------------------------------------

X = pd.DataFrame({

    "amount": amount,

    "historicalAverageAmount":
        historical_average_amount,

    "recentTransactionCount":
        recent_transaction_count,

    "recentTotalAmount":
        recent_total_amount,

    "isNewLocation":
        is_new_location,

    "isNewDevice":
        is_new_device,

    "isNewIpAddress":
        is_new_ip_address
})


y = df["Class"]


print("\nNew feature shape:", X.shape)

print("\nFeatures:")

print(X.columns.tolist())


# --------------------------------------------------
# 4. Train / Test Split
# --------------------------------------------------

X_train, X_test, y_train, y_test = train_test_split(
    X,
    y,
    test_size=0.20,
    random_state=42,
    stratify=y
)


# --------------------------------------------------
# 5. Random Forest
# --------------------------------------------------

random_forest = RandomForestClassifier(
    n_estimators=150,
    random_state=42,
    class_weight="balanced"
)


# --------------------------------------------------
# 6. Logistic Regression
# --------------------------------------------------

logistic_regression = Pipeline([

    (
        "scaler",
        StandardScaler()
    ),

    (
        "classifier",
        LogisticRegression(
            max_iter=1000,
            class_weight="balanced"
        )
    )
])


# --------------------------------------------------
# 7. Soft Voting Ensemble
# --------------------------------------------------

voting_model = VotingClassifier(

    estimators=[

        ("rf", random_forest),

        ("lr", logistic_regression)

    ],

    voting="soft"
)


# --------------------------------------------------
# 8. Train model
# --------------------------------------------------

print("\nTraining application fraud model...")


voting_model.fit(
    X_train,
    y_train
)


print("Training completed.")


# --------------------------------------------------
# 9. Prediction
# --------------------------------------------------

predictions = voting_model.predict(
    X_test
)

probabilities = voting_model.predict_proba(
    X_test
)

fraud_probabilities = probabilities[:, 1]


# --------------------------------------------------
# 10. Evaluation
# --------------------------------------------------

print("\n==============================")
print("APPLICATION FRAUD MODEL RESULTS")
print("==============================")


print("\nClassification Report:")

print(
    classification_report(
        y_test,
        predictions,
        target_names=[
            "Genuine",
            "Fraud"
        ]
    )
)


print("\nROC-AUC Score:")

print(
    roc_auc_score(
        y_test,
        fraud_probabilities
    )
)


print("\nConfusion Matrix:")

print(
    confusion_matrix(
        y_test,
        predictions
    )
)


# --------------------------------------------------
# 11. Save application-compatible model
# --------------------------------------------------

os.makedirs(
    "models",
    exist_ok=True
)


MODEL_PATH = (
    "models/app_fraud_detection_model.joblib"
)


joblib.dump(
    voting_model,
    MODEL_PATH
)


print("\nApplication fraud model saved successfully:")

print(MODEL_PATH)