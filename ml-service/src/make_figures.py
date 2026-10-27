
import os
import joblib
import pandas as pd
import matplotlib.pyplot as plt

from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import AdaBoostClassifier, RandomForestClassifier
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import (
    confusion_matrix,
    ConfusionMatrixDisplay,
    roc_curve,
    roc_auc_score,
    precision_recall_curve,
    average_precision_score,
    accuracy_score,
    precision_score,
    recall_score,
    f1_score,
)
from sklearn.utils.class_weight import compute_sample_weight


# =========================================================
# 1. CONFIGURATION
# Run this script from the ml-service directory.
# =========================================================

os.makedirs("figures", exist_ok=True)

plt.rcParams.update({
    "font.family": "serif",
    "font.serif": ["Times New Roman", "DejaVu Serif"],
    "font.size": 9,
    "axes.labelsize": 9,
    "legend.fontsize": 8,
    "savefig.dpi": 300,
    "savefig.bbox": "tight",
})


def save_figure(fig, filename):
    """Save a figure as high-resolution PNG and vector PDF."""
    fig.savefig(
        os.path.join("figures", f"{filename}.png"),
        dpi=300,
    )
    fig.savefig(
        os.path.join("figures", f"{filename}.pdf"),
    )
    plt.close(fig)


# =========================================================
# 2. LOAD DATA AND REPRODUCE THE TRAIN/TEST SPLIT
# =========================================================

print("Loading dataset...")

df = pd.read_csv("data/creditcard.csv")
X = df.drop(columns=["Class"])
y = df["Class"]

X_train, X_test, y_train, y_test = train_test_split(
    X,
    y,
    test_size=0.20,
    random_state=42,
    stratify=y,
)

print("Dataset loaded:", df.shape)
print("Training samples:", len(X_train))
print("Testing samples:", len(X_test))


# =========================================================
# 3. LOAD THE SAVED ENSEMBLE
# =========================================================

print("\nLoading saved ensemble...")

ensemble = joblib.load(
    "models/fraud_detection_model.joblib"
)


# =========================================================
# 4. TRAIN INDIVIDUAL COMPARISON MODELS
# These settings match the supplied train.py.
# =========================================================

print("\nTraining AdaBoost...")

sample_weights = compute_sample_weight(
    class_weight="balanced",
    y=y_train,
)

ada = Pipeline([
    ("scaler", StandardScaler()),
    ("classifier", AdaBoostClassifier(
        estimator=DecisionTreeClassifier(
            max_depth=1,
            random_state=42,
        ),
        n_estimators=200,
        learning_rate=0.5,
        random_state=42,
    )),
])

ada.fit(
    X_train,
    y_train,
    classifier__sample_weight=sample_weights,
)

print("AdaBoost completed.")

print("\nTraining Random Forest...")

rf = Pipeline([
    ("scaler", StandardScaler()),
    ("classifier", RandomForestClassifier(
        n_estimators=100,
        random_state=42,
        class_weight="balanced",
    )),
])

rf.fit(X_train, y_train)

print("Random Forest completed.")

print("\nTraining Logistic Regression...")

lr = Pipeline([
    ("scaler", StandardScaler()),
    ("classifier", LogisticRegression(
        max_iter=1000,
        class_weight="balanced",
        random_state=42,
    )),
])

lr.fit(X_train, y_train)

print("Logistic Regression completed.")


# All four models
models = {
    "AdaBoost": ada,
    "Random Forest": rf,
    "Logistic Regression": lr,
    "Proposed Ensemble": ensemble,
}


# =========================================================
# 5. ENSEMBLE PREDICTIONS
# =========================================================

print("\nEvaluating proposed ensemble...")

y_pred = ensemble.predict(X_test)
y_prob = ensemble.predict_proba(X_test)[:, 1]


# =========================================================
# FIG. 2: CONFUSION MATRIX
# =========================================================

print("Generating Fig. 2...")

cm = confusion_matrix(y_test, y_pred)

fig, ax = plt.subplots(figsize=(3.5, 3.2))

ConfusionMatrixDisplay(
    confusion_matrix=cm,
    display_labels=["Legitimate", "Fraud"],
).plot(
    ax=ax,
    cmap="Blues",
    values_format="d",
    colorbar=False,
)

ax.set_title("")
ax.set_xlabel("Predicted label")
ax.set_ylabel("True label")

fig.tight_layout()
save_figure(fig, "fig2_confusion_matrix")


# =========================================================
# FIG. 3: ROC CURVE
# =========================================================

print("Generating Fig. 3...")

fpr, tpr, _ = roc_curve(y_test, y_prob)
roc_auc = roc_auc_score(y_test, y_prob)

fig, ax = plt.subplots(figsize=(3.5, 3.2))

ax.plot(
    fpr,
    tpr,
    linewidth=1.8,
    label=f"Ensemble (AUC = {roc_auc:.4f})",
)

ax.plot(
    [0, 1],
    [0, 1],
    linestyle="--",
    color="gray",
    label="Random baseline",
)

ax.set_xlabel("False Positive Rate")
ax.set_ylabel("True Positive Rate")
ax.set_xlim(0, 1)
ax.set_ylim(0, 1.05)
ax.legend(loc="lower right", frameon=True)
ax.grid(alpha=0.3)

fig.tight_layout()
save_figure(fig, "fig3_roc_curve")


# =========================================================
# FIG. 4: PRECISION-RECALL CURVE
# =========================================================

print("Generating Fig. 4...")

pr_precision, pr_recall, _ = precision_recall_curve(
    y_test,
    y_prob,
)

average_precision = average_precision_score(
    y_test,
    y_prob,
)

fig, ax = plt.subplots(figsize=(3.5, 3.2))

ax.plot(
    pr_recall,
    pr_precision,
    linewidth=1.8,
    label=f"Ensemble (AP = {average_precision:.4f})",
)

ax.axhline(
    y_test.mean(),
    linestyle="--",
    color="gray",
    label="Positive-class baseline",
)

ax.set_xlabel("Recall")
ax.set_ylabel("Precision")
ax.set_xlim(0, 1)
ax.set_ylim(0, 1.05)
ax.legend(loc="lower left", frameon=True)
ax.grid(alpha=0.3)

fig.tight_layout()
save_figure(fig, "fig4_pr_curve")


# =========================================================
# FIG. 5: FOUR-MODEL PERFORMANCE COMPARISON
# =========================================================

print("Generating Fig. 5...")

metric_functions = {
    "Accuracy": accuracy_score,
    "Precision": lambda yt, yp: precision_score(
        yt, yp, zero_division=0
    ),
    "Recall": recall_score,
    "F1-score": f1_score,
}

results = {}

for name, model in models.items():
    predictions = model.predict(X_test)

    results[name] = {
        metric: function(y_test, predictions)
        for metric, function in metric_functions.items()
    }

results_df = pd.DataFrame(results).T

print("\nModel comparison:")
print(results_df.round(4).to_string())

# Use one group per model and four bars per group.
fig, ax = plt.subplots(figsize=(7.16, 3.5))

results_df.plot(
    kind="bar",
    ax=ax,
    width=0.8,
    color=[
        "#4472C4",
        "#ED7D31",
        "#70AD47",
        "#A5A5A5",
    ],
)

ax.set_ylabel("Score")
ax.set_xlabel("")
ax.set_ylim(0, 1.05)
ax.tick_params(axis="x", rotation=12)

ax.legend(
    ncol=4,
    loc="lower center",
    bbox_to_anchor=(0.5, 1.01),
    frameon=False,
)

ax.grid(
    axis="y",
    linestyle="--",
    alpha=0.3,
)

ax.set_axisbelow(True)
fig.tight_layout()

save_figure(fig, "fig5_model_comparison")

# Save exact comparison metrics
results_df.to_csv(
    "figures/model_comparison_metrics.csv",
    float_format="%.6f",
)


# =========================================================
# 6. PRINT FINAL EVALUATION RESULTS
# =========================================================

print("\nEnsemble ROC-AUC:", round(roc_auc, 4))
print(
    "Ensemble Average Precision:",
    round(average_precision, 4),
)

print("\nConfusion matrix:")
print(cm)

tn, fp, fn, tp = cm.ravel()

print("\nConfusion matrix counts:")
print("True Negatives:", tn)
print("False Positives:", fp)
print("False Negatives:", fn)
print("True Positives:", tp)

print("\nAll figures saved in the figures/ directory.")
print("Formats: PNG (300 DPI) and PDF.")
