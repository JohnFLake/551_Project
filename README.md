# CIS551 project2 - Attack and Defend

## Possible attacks

#### ATM with correct card (more of a bug)
- Withdraw more money than the account has in it
- Underflow/overflow the account balance
- Open a duplicate account (clobbering the existing value)

#### ATM without the right card (either with another card or with nothing)
- See the balance of the account
- Withdraw from the account
- Derive the atm card from one the attacker has
- Deposit into the account (weird attack)

#### No ATM with or without card (forge transactions without bank.auth)
- Deposit money
- Withdraw money
- See balance (probably not a big deal)

### Man in the middle
- Derive a malicious but valid transaction by seeing a real valid transaction
- Change a value in a valid transaction
- Change the response from the bank to a valid transaction

