default: build
	echo '#!/bin/bash\n\njava -cp build com.heinthanth.uit.Main "$$@"' > uit.sh && chmod +x uit.sh

build: clean
	javac -d build -cp build src/com/heinthanth/uit/Main.java

clean:
	rm -rf build uit.sh
