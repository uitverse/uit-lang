default: uit

all: uit man

uit: classes manifest
	@echo "[x] Unpacking External Dependencies."
	@unzip "lib/jline-3.18.0.jar" "org/*" -d "tmp" >/dev/null
	@unzip "lib/jansi-2.1.0.jar" "org/*" -d "tmp" >/dev/null
	@mkdir build
	@echo "[x] Packing Jar."
	@jar cmvf "META-INF/MANIFEST.MF" build/uit.jar -C "tmp" . >/dev/null
	@rm -rf "tmp" "META-INF"

classes: clean
	@echo "[x] Compiling Java."
	@bash -c 'JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8 javac -Werror -d "tmp" -cp "lib/*:src" src/com/heinthanth/uit/Main.java 2> >(grep -v "^Picked up JAVA_TOOL_OPTIONS:" >&2)'

node:
	@echo "[x] Generating Runtime Nodes."
	@javac -d "tmp" src/com/heinthanth/uit/Utils/GenerateNode.java >/dev/null
	@java -cp "tmp" com.heinthanth.uit.Utils.GenerateNode src/com/heinthanth/uit/Runtime >/dev/null

manifest:
	@echo "[x] Generating Manifest File."
	@mkdir -p "META-INF"
	@echo "Manifest-Version: 1.0" > "META-INF/MANIFEST.MF"
	@echo "Main-Class: com.heinthanth.uit.Main" >> "META-INF/MANIFEST.MF"
	@echo "Build-Time: $$(date -u '+%h %d %Y %H:%M:%S %Z')" >> "META-INF/MANIFEST.MF"
	@echo "Build-Number: $$(tar c src | shasum -a 256 | awk '{print $1}' | cut -c1-8 | awk '{ print toupper($$0) }')" >> "META-INF/MANIFEST.MF"
	@echo "Version: $$(cat .version)" >> "META-INF/MANIFEST.MF"
	@echo "Built-On: $$(echo $$(whoami)@$$(hostname) | cut -d"." -f1)" >> "META-INF/MANIFEST.MF"

clean:
	@rm -rf build tmp "META-INF"

man:
	@echo "Generating MAN page."
	@test -d build || mkdir build
	@pandoc man/uit.1.md -s -t man -o build/uit.1
	@gzip build/uit.1

.PHONY: uit node man