
from pytorch_tokenizers.tiktoken import TiktokenTokenizer

tokenizer = TiktokenTokenizer(
    r"C:\Projects\Bharat-AI-Crop-Assistant\Android-App\BharatAI\app\src\main\assets\models\tokenizer.model"
)

special_tokens = [
    "<|begin_of_text|>",
    "<|start_header_id|>",
    "<|end_header_id|>",
    "<|eot_id|>",
    "<|end_of_text|>",
]

for token in special_tokens:
    expected_id = tokenizer.special_tokens[token]

    encoded = tokenizer.encode(
        token,
        bos=False,
        eos=False,
        allowed_special="all",
    )

    print(f"\nToken: {token}")
    print("Expected:", expected_id)
    print("Actual:", encoded)
    print("Correct:", encoded == [expected_id])