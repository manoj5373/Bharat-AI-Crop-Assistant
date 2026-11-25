
from pytorch_tokenizers.tiktoken import TiktokenTokenizer

tokenizer = TiktokenTokenizer(
    r"C:\Projects\Bharat-AI-Crop-Assistant\Android-App\BharatAI\app\src\main\assets\models\tokenizer.model"
)

print("\n--- TOKENIZER METHODS ---")

for name in dir(tokenizer):
    if not name.startswith("_"):
        try:
            attribute = getattr(tokenizer, name)
            if callable(attribute):
                print(name, "->", attribute)
        except Exception:
            pass

print("\n--- SPECIAL TOKEN DATA TYPE ---")
print(type(tokenizer.special_tokens))
print(tokenizer.special_tokens)

print("\n--- ENCODE METHOD SIGNATURE ---")
import inspect
print(inspect.signature(tokenizer.encode))

print("\n--- ENCODE METHOD SOURCE ---")
print(inspect.getsource(tokenizer.encode))
