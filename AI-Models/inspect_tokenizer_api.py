
from pytorch_tokenizers.tiktoken import TiktokenTokenizer

tokenizer = TiktokenTokenizer(
    "C:/Projects/Bharat-AI-Crop-Assistant/Android-App/BharatAI/app/src/main/assets/models/tokenizer.model"
)

print("TOKENIZER CLASS:")
print(type(tokenizer))

print("\nAVAILABLE TOKENIZER METHODS:")
for name in dir(tokenizer):
    if not name.startswith("_"):
        print(name)

print("\nTOKENIZER EOS/BOS:")
print("EOS:", tokenizer.eos_id)
print("BOS:", tokenizer.bos_id)
print("\nSPECIAL TOKENS CONTENT:")
print(repr(tokenizer.special_tokens))

print("\nSPECIAL TOKENS TYPE:")
print(type(tokenizer.special_tokens))