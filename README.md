# CIS551 project2 - Attack and Defend
## Pwn1ng N00b C0de Bank (PNC Bank)

## Possible attacks

#### ATM with correct card (more of a bug)
- Withdraw more money than the account has in it
- Underflow/overflow the account balance
- Open a duplicate account (clobbering the existing value)
- Crash the bank with a fuzzed input

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
- Derive a malicious but valid transaction to the bank by seeing a real valid transaction
- Change a value in a valid transaction
- Change the bank's response from a valid transaction to wrongly indicate success/failure
- Respond to the ATM as if it is the bank (bank never sees)

