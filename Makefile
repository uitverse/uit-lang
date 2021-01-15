default: node
	mvn clean package

node:
	javac -d tmp src/main/java/com/heinthanth/uit/Utils/GenerateNode.java
	java -cp tmp com.heinthanth.uit.Utils.GenerateNode src/main/java/com/heinthanth/uit/Runtime
	rm -rf tmp