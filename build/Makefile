all: serv
serv:
	chmod +x bank
	javac -d makebin -cp ./lib/commons-cli-1.4.jar:./src/utility ./src/utility/Crypto.java
	javac -d makebin -cp ./lib/commons-cli-1.4.jar:./src/utility ./src/utility/InputChecker.java
	javac -d makebin -cp ./lib/commons-cli-1.4.jar:./src/ ./src/bank_server/Server.java
	javac -d makebin -cp ./lib/commons-cli-1.4.jar:./src/ ./src/bank_server/Main.java

clean: 
	rm -rf makebin
