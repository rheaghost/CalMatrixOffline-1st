# https://chat.deepseek.com/a/chat/s/08f41547-8d50-4a5c-bd32-e490a49d0393
# kaggle exercise table at https://www.kaggle.com/datasets/aadhavvignesh/calories-burned-during-exercise-and-activities
# 2609031732

# exercises calories csv
# original exercises.csv(name,met)
# kaggle exercise_dataset.csv

import pandas as pd

# 1. Load existing exercises.csv (your current file)
existing = pd.read_csv("exercises.csv")
print(f"Existing exercises: {len(existing)}")

# 2. Load Kaggle dataset
kaggle = pd.read_csv("exercise_dataset.csv")
print(f"Kaggle exercises: {len(kaggle)}")

# 3. Convert Kaggle data to MET format
# MET = calories per pound × 2.20462
kaggle["met"] = kaggle["calories per pound"] * 2.20462

# 4. Rename columns to match existing format
kaggle_clean = kaggle[["Activity", "met"]].copy()
kaggle_clean.columns = ["name", "met"]

# 5. Remove duplicates (keep first occurrence)
kaggle_clean = kaggle_clean.drop_duplicates(subset=["name"], keep="first")

# 6. Convert names to lowercase for consistency
kaggle_clean["name"] = kaggle_clean["name"].str.lower()

# 7. Merge with existing (keep existing, add new ones)
merged = pd.concat([existing, kaggle_clean]).drop_duplicates(subset=["name"], keep="first")
merged = merged.sort_values("name")

# 8. Save merged file
merged.to_csv("exercises_merged.csv", index=False)
print(f"Merged exercises: {len(merged)}")
print("✅ Saved as exercises_merged.csv")