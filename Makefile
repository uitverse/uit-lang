default: all

all: jar script man

script: class
	echo '#!/bin/bash\n\njava -cp build com.heinthanth.uit.Main "$$@"' > build/bin/uit.sh
	chmod +x build/bin/uit.sh

jar: class
	jar cmvf META-INF/MANIFEST.MF build/bin/uit.jar -C build .

class: clean
	javac -g:none -Werror -d build -cp src src/com/heinthanth/uit/Main.java
	mkdir build/bin

man:
	mkdir -p build/man/
	pandoc man/uit.1.md -s -t man -o build/man/uit.1
	gzip build/man/uit.1

clean:
	rm -rf build

.PHONY: all script jar class man clean