
from pytorch_tokenizers import get_tokenizer

path = r"C:\Projects\Bharat-AI-Crop-Assistant\Android-App\BharatAI\app\src\main\assets\models\tokenizer.model"

tokenizer = get_tokenizer(path)

print("Tokenizer class:", type(tokenizer).__name__)

text = "Write one short sentence about mulberry leaves."

tokens = tokenizer.encode(
    text,
    bos=True,
    eos=False,
    allowed_special="all"
)

print("Token IDs:", tokens)
print("Token count:", len(tokens))
print("Decoded text:", tokenizer.decode(tokens))
