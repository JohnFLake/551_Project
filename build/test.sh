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

./bank &
last_pid=$!
sleep 3
kill -KILL $last_pid > /dev/null

./bank
echo "bank.auth already exists: " $?

./bank -s
echo "No bank.auth given: " $?
