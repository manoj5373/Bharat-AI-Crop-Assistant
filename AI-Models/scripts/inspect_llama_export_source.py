
import inspect
from executorch.examples.models.llama import export_llama_lib

print("EXPORT LLAMA LIBRARY:")
print(export_llama_lib.__file__)

source = inspect.getsource(export_llama_lib)

keywords = [
    "use_kv_cache",
    "input_pos",
    "dynamic_shape",
    "forward",
    "get_max_seq_len",
]

for keyword in keywords:
    print(f"\n{'=' * 50}")
    print("SEARCH:", keyword)

    lines = source.splitlines()

    matches = [
        (i, line)
        for i, line in enumerate(lines)
        if keyword.lower() in line.lower()
    ]

    for i, line in matches[:8]:
        start = max(0, i - 3)
        end = min(len(lines), i + 5)

        for j in range(start, end):
            print(f"{j + 1}: {lines[j]}")
        print("-" * 30)
