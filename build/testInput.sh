#!/bin/bash


echo "Testing input to bank."
./bank -s..
echo "One period auth file: " $?

./bank -s.
echo "Two period auth file: " $?

./bank -p1023
echo "Port number 1023: " $?

./bank -p65536
echo "Port number 65536: " $?

echo "gibberish" > bank.auth
./bank
echo "bank.auth already exists: " $?
rm bank.auth

./bank -s
echo "No bank.auth given: " $?


