
from pytorch_tokenizers.tiktoken import TiktokenTokenizer

tokenizer = TiktokenTokenizer(
    "C:/Projects/Bharat-AI-Crop-Assistant/Android-App/BharatAI/app/src/main/assets/models/tokenizer.model"
)

special_ids = {
    "BOS": 128000,
    "EOS": 128001,
    "EOT": 128009,
    "START_HEADER": 128006,
    "END_HEADER": 128007,
}

print("SPECIAL TOKEN DIAGNOSTIC")
print("------------------------")

for name, token_id in special_ids.items():
    print(
        f"{name}: ID={token_id}, "
        f"decoded={repr(tokenizer.decode_token(token_id))}"
    )

print("\nEOS ID:", tokenizer.eos_id)

print("\nStop tokens:")
print(getattr(tokenizer, "stop_tokens", "Attribute not available"))
