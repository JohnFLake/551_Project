#Make a new account
./atm -a john -n 100
./atm -a john -g

#Deposit 1000
./atm -a john -d 1000.00
./atm -a john -g

#Withdraw 1100
./atm -a john -w 1100.00
./atm -a john -g

#Deposit $33.23
./atm -a john -w  33.23
./atm -a john -g

#Deposit $0
./atm -a john -d  0
./atm -a john -g

#Deposit the max
./atm -a john -d 4294967295.99
./atm -a john -g

#Deposit the max
./atm -a john -d 4294967295.99
./atm -a john -g

#Deposit the max
./atm -a john -d 4294967295.99
./atm -a john -g
