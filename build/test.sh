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
