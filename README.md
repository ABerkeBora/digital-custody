# Digital Custody - Hauck Aufhäuser Coding Challenge

This Java application analyzes Ethereum wallet activity using Etherscan and CoinMarketCap APIs.

It fetches:

- ETH balance and total USD volume
- Recent transactions
- ERC-20 token transfers and total token USD value
- Swap detection (Uniswap-style via method signatures)

---

## How to Run

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/digital-custody.git
cd digital-custody
```

### 2. Set up environment variables

```bash
export INFURA_PROJECT_URL=https://mainnet.infura.io/v3/YOUR_INFURA_KEY
export ETHERSCAN_API_KEY=YOUR_ETHERSCAN_KEY
export COINMARKETCAP_API_KEY=YOUR_CMC_KEY
```

You can also run in one line:

```bash
INFURA_PROJECT_URL=https://... ETHERSCAN_API_KEY=... COINMARKETCAP_API_KEY=... mvn exec:java
```

---

## Dependencies

- Java 17
- Maven
- web3j
- OkHttp
- Gson

---

## Example Output

```
ETH Balance: 3.245 ETH
Total USD Volume (approx): $8,392.23
ERC-20 Token Value (USD): $2,151.12
TOTAL USD Value (ETH + Tokens): $10,543.35
```

---

## Notes

- Swap detection is based on method signatures (`swapExactTokensForTokens`, etc.)
- Token USD prices are fetched from CoinMarketCap (by symbol)
- Supports up to 10,000 transactions and token transfers per run
