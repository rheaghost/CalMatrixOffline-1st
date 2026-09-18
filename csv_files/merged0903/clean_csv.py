# https://chat.deepseek.com/a/chat/s/08f41547-8d50-4a5c-bd32-e490a49d0393

import pandas as pd

# Load CSV
df = pd.read_csv("foods.csv")

# Remove duplicates, keep first occurrence
df = df.drop_duplicates(subset=["name"], keep="first")

# Convert all names to lowercase (for consistency)
df["name"] = df["name"].str.lower()

# Sort by name
df = df.sort_values("name")

# Save cleaned CSV
df.to_csv("foods_cleaned.csv", index=False)

print(f"✅ Cleaned CSV: {len(df)} unique foods")