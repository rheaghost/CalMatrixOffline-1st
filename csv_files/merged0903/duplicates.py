import pandas as pd

df = pd.read_csv("foods.csv")
print(f"Total rows: {len(df)}")
print(f"Unique names: {df['name'].nunique()}")