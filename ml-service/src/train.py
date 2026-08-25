import os
import joblib
import pandas as pd

from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline

from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import (
    AdaBoostClassifier,
    RandomForestClassifier,
    VotingClassifier
)

from sklearn.linear_model import LogisticRegression

from sklearn.metrics import (
    classification_report,
    confusion_matrix,
    roc_auc_score
)

from sklearn.utils.class_weight import compute_sample_weight


# --------------------------------------------------
# 1. Load dataset
# --------------------------------------------------

DATA_PATH = "data/creditcard.csv"

df = pd.read_csv(DATA_PATH)

print("Dataset shape:", df.shape)


# --------------------------------------------------
# 2. Separate features and target
# --------------------------------------------------

X = df.drop(columns=["Class"])
y = df["Class"]

print("\nClass distribution:")
print(y.value_counts())


# --------------------------------------------------
# 3. Train / Test Split
# --------------------------------------------------

X_train, X_test, y_train, y_test = train_test_split(
    X,
    y,
    test_size=0.20,
    random_state=42,
    stratify=y
)

print("\nTraining data:", X_train.shape)
print("Testing data:", X_test.shape)


# --------------------------------------------------
# 4. Sample weights for AdaBoost
# --------------------------------------------------

sample_weights = compute_sample_weight(
    class_weight="balanced",
    y=y_train
)


# --------------------------------------------------
# 5. AdaBoost
# --------------------------------------------------

ada_boost = Pipeline([
    (
        "scaler",
        StandardScaler()
    ),
    (
        "classifier",
        AdaBoostClassifier(
            estimator=DecisionTreeClassifier(
                max_depth=1,
                random_state=42
            ),
            n_estimators=200,
            learning_rate=0.5,
            random_state=42
        )
    )
])


# --------------------------------------------------
# 6. Random Forest
# --------------------------------------------------

random_forest = Pipeline([
    (
        "scaler",
        StandardScaler()
    ),
    (
        "classifier",
        RandomForestClassifier(
            n_estimators=100,
            random_state=42,
            class_weight="balanced"
        )
    )
])


# --------------------------------------------------
# 7. Logistic Regression
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
# 8. Train individual models
# --------------------------------------------------

print("\nTraining AdaBoost...")

ada_boost.fit(
    X_train,
    y_train,
    classifier__sample_weight=sample_weights
)

print("AdaBoost completed.")

print("\nTraining Random Forest...")

random_forest.fit(
    X_train,
    y_train
)

print("Random Forest completed.")

print("\nTraining Logistic Regression...")

logistic_regression.fit(
    X_train,
    y_train
)

print("Logistic Regression completed.")


# --------------------------------------------------
# 9. Majority Voting Ensemble
# --------------------------------------------------

voting_model = VotingClassifier(
    estimators=[
        ("ada", ada_boost),
        ("rf", random_forest),
        ("lr", logistic_regression)
    ],
    voting="soft"
)


# --------------------------------------------------
# 10. Train Voting Model
# --------------------------------------------------

print("\nTraining Majority Voting model...")

voting_model.fit(
    X_train,
    y_train
)

print("Majority Voting completed.")


# --------------------------------------------------
# 11. Prediction
# --------------------------------------------------

voting_pred = voting_model.predict(X_test)

# --------------------------------------------------
# Fraud Probability
# --------------------------------------------------

voting_probabilities = voting_model.predict_proba(X_test)

fraud_probabilities = voting_probabilities[:, 1]

print("\nSample Fraud Probabilities:")

print(fraud_probabilities[:10])


# --------------------------------------------------
# 12. Evaluation
# --------------------------------------------------

print("\n==============================")
print("MAJORITY VOTING RESULTS")
print("==============================")

print("\nClassification Report:")

print(
    classification_report(
        y_test,
        voting_pred,
        target_names=[
            "Genuine",
            "Fraud"
        ]
    )
)


# --------------------------------------------------
# ROC-AUC Score
# --------------------------------------------------

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
        voting_pred
    )
)


# --------------------------------------------------
# 13. Save model
# --------------------------------------------------

os.makedirs(
    "models",
    exist_ok=True
)

MODEL_PATH = "models/fraud_detection_model.joblib"

joblib.dump(
    voting_model,
    MODEL_PATH
)

print("\nModel saved successfully:")
print(MODEL_PATH)