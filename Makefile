default: uit man

uit: node
	mvn clean package

node:
	javac -d tmp src/main/java/com/heinthanth/uit/Utils/GenerateNode.java
	java -cp tmp com.heinthanth.uit.Utils.GenerateNode src/main/java/com/heinthanth/uit/Runtime
	rm -rf tmp

man:
	test -d build || mkdir build
	pandoc man/uit.1.md -s -t man -o build/uit.1
	gzip build/uit.1

.PHONY: uit node man