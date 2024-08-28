import pandas as pd
from statds.no_parametrics import wilconxon, friedman, nemenyi
# dataset = pd.read_csv("dataset_time_minur.csv")
# dataset = pd.read_csv("dataset_time_minra.csv")
# dataset = pd.read_csv("dataset_time_delta.csv")
# dataset = pd.read_csv("dataset_memory_minur.csv")
# dataset = pd.read_csv("dataset_memory_minra.csv")
dataset = pd.read_csv("dataset_memory_delta.csv")
columns = list(dataset.columns)
selected_columns = [columns[1], columns[2]]
alphas = [0.05, 0.01]
for alpha in alphas:
    statistic, p_value, rejected_value, hypothesis = wilconxon(dataset[selected_columns], alpha)
    print(hypothesis)
    print(f"Statistic {statistic}, Rejected Value {rejected_value}, p−value {p_value}")

    # rankings, statistic, p_value, critical_value, hypothesis = friedman(dataset, alpha, minimize=False)
    # print(hypothesis)
    # print(f"Statistic {statistic}, Rejected Value {rejected_value}, p−value {p_value}")
    # print(rankings)
    # num_cases = dataset.shape[0]
    # ranks_values, critical_distance_nemenyi, figure = nemenyi(rankings, num_cases, alpha)
    # print(ranks_values)
    # print(critical_distance_nemenyi)
    # figure.show()

