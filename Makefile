default: uit man

uit: classes manifest
	unzip "lib/jline-3.18.0.jar" "org/*" -d "tmp"
	unzip "lib/jansi-2.1.0.jar" "org/*" -d "tmp"
	mkdir build
	jar cmvf "META-INF/MANIFEST.MF" build/uit.jar -C "tmp" .
	rm -rf "tmp"

classes: clean
	# create classes
	JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8 javac -g:none -Werror -d "tmp" -cp "lib/*:src" src/com/heinthanth/uit/Main.java

node:
	# create node generator
	javac -d "tmp" src/com/heinthanth/uit/Utils/GenerateNode.java
	# Generate Expression and Statement nodes
	java -cp "tmp" com.heinthanth.uit.Utils.GenerateNode src/com/heinthanth/uit/Runtime

manifest:
	mkdir -p "META-INF"
	echo "Manifest-Version: 1.0" > "META-INF/MANIFEST.MF"
	echo "Main-Class: com.heinthanth.uit.Main" >> "META-INF/MANIFEST.MF"
	echo "Build-Time: $$(date -u '+%h %d %Y %H:%M:%S %Z')" >> "META-INF/MANIFEST.MF"
	echo "Build-Number: $$(git ls-remote https://github.com/uitverse/uit-lang HEAD | awk '{ print $$1}' | cut -c1-8 | awk '{ print toupper($$0) }')" >> "META-INF/MANIFEST.MF"
	echo "Version: $$(cat .version)" >> "META-INF/MANIFEST.MF"
	echo "Built-On: $$(echo $$(whoami)@$$(hostname) | cut -d"." -f1)" >> "META-INF/MANIFEST.MF"

clean:
	rm -rf build tmp

man:
	test -d build || mkdir build
	pandoc man/uit.1.md -s -t man -o build/uit.1
	gzip build/uit.1

.PHONY: uit node man