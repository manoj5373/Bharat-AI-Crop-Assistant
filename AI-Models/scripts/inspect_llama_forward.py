
from pathlib import Path
from executorch.runtime import Runtime

MODEL_PATH = Path(
    r"C:\Projects\Bharat-AI-Crop-Assistant\Android-App\BharatAI\app\src\main\assets\models\bharat_llama_3_2_1b_qc4.pte"
)

runtime = Runtime.get()
program = runtime.load_program(str(MODEL_PATH))
method = program.load_method("forward")

metadata = method.metadata

def inspect_tensor(label, info):
    print(f"\n{label}")
    print("Shape:", info.sizes())
    print("Data type:", info.dtype())
    print("Memory planned:", info.is_memory_planned())
    print("Bytes:", info.nbytes())

for i in range(metadata.num_inputs()):
    inspect_tensor(f"INPUT {i}", metadata.input_tensor_meta(i))

for i in range(metadata.num_outputs()):
    inspect_tensor(f"OUTPUT {i}", metadata.output_tensor_meta(i))
