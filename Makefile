default: all

all: jar
	echo '#!/bin/bash\n\njava -cp build com.heinthanth.uit.Main "$$@"' > build/bin/uit.sh && chmod +x build/bin/uit.sh

build: clean
	javac -g:none -Werror -d build -cp src src/com/heinthanth/uit/Main.java
	mkdir build/bin

jar: build
	jar cmvf META-INF/MANIFEST.MF build/bin/uit.jar -C build .

clean:
	rm -rf build uit.sh
