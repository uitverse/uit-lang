default: all

all: jar script

script: class
	echo '#!/bin/bash\n\njava -cp build com.heinthanth.uit.Main "$$@"' > build/bin/uit.sh && chmod +x build/bin/uit.sh

jar: class
	jar cmvf META-INF/MANIFEST.MF build/bin/uit.jar -C build .

class: clean
	javac -g:none -Werror -d build -cp src src/com/heinthanth/uit/Main.java
	mkdir build/bin

clean:
	rm -rf build
