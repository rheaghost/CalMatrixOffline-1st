import pandas as pd

# Load the Kaggle dataset
kaggle = pd.read_csv("exercise_dataset.csv")
print("Column names:")
print(kaggle.columns.tolist())
print("\nFirst few rows:")
print(kaggle.head())