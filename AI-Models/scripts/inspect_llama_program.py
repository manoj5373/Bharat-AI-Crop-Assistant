
from pathlib import Path
from executorch.runtime import Runtime

MODEL_PATH = Path(
    r"C:\Projects\Bharat-AI-Crop-Assistant\Android-App\BharatAI\app\src\main\assets\models\bharat_llama_3_2_1b_qc4.pte"
)

runtime = Runtime.get()
program = runtime.load_program(str(MODEL_PATH))

methods_to_check = [
    "get_vocab_size",
    "get_n_layers",
    "get_max_seq_len",
    "get_max_context_len",
    "use_kv_cache",
    "use_sdpa_with_kv_cache",
    "enable_dynamic_shape",
]

print("LLAMA MODEL CONFIGURATION")
print("=" * 45)

for name in methods_to_check:
    try:
        method = program.load_method(name)
        result = method.execute([])
        print(f"{name}: {result}")
    except Exception as error:
        print(f"{name}: ERROR - {error}")
