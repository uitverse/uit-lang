default: all

all: jar man docker

jar: class
	unzip lib/jline-3.18.0.jar "org/*" -d "build"
	jar cmvf META-INF/MANIFEST.MF build/uit.jar -C build .

class: clean node
	JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8 javac -g:none -Werror -d build -cp "lib/jline-3.18.0.jar:src" src/com/heinthanth/uit/Main.java

node:
	javac -d build src/com/heinthanth/uit/Utils/GenerateNode.java
	java -cp build com.heinthanth.uit.Utils.GenerateNode src/com/heinthanth/uit/Runtime

clean:
	rm -rf build

man:
	mkdir -p build/man/
	pandoc man/uit.1.md -s -t man -o build/man/uit.1
	gzip build/man/uit.1

docker:
	sudo docker build -t uit .

.PHONY: all jar class man clean