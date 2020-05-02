import matplotlib.pyplot as plt
import pandas as pd

df = pd.read_csv("./benchmarkResults.csv")

df['file_name'] = df['file_name'].apply(lambda name: name.split("-")[1])
df = df.sort_values(by='file_name')

ax = df.plot.bar(x='file_name', y='backtracking_ms', figsize=(10,5))
ax.get_figure().savefig("backtracking.png")

ax = df.plot.bar(x='file_name', y='forward_checking_ms', figsize=(10,5))
ax.get_figure().savefig("forwardchecking.png")

ax = df.plot.bar(x='file_name', y='FC_DO_ms', figsize=(10,5))
ax.get_figure().savefig("forwardchecking_do.png")

df1 = df[:10]
df2 = df[11:20]
df3 = df[21:30]
df4 = df[31:]

ax = df1.plot.bar(x='file_name', rot=0, log=True, figsize=(10,5))
ax.get_figure().savefig("all_1.png")

ax = df2.plot.bar(x='file_name', rot=0, log=True, figsize=(10,5))
ax.get_figure().savefig("all_2.png")

ax = df3.plot.bar(x='file_name', rot=0, log=True, figsize=(10,5))
ax.get_figure().savefig("all_3.png")

ax = df4.plot.bar(x='file_name', rot=0, log=True, figsize=(10,5))
ax.get_figure().savefig("all_4.png")

print(df)
