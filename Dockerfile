FROM debian:buster as compiler

RUN apt update \
    && apt install --no-install-recommends -y openjdk-11-jdk-headless maven git

WORKDIR /opt/heinthanth/uit

COPY ./pom.xml .
RUN mvn dependency:go-offline

COPY . .

RUN JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8 javac -d tmp src/main/java/com/heinthanth/uit/Utils/GenerateNode.java \
	&& java -cp tmp com.heinthanth.uit.Utils.GenerateNode src/main/java/com/heinthanth/uit/Runtime \
	&& rm -rf tmp \
    && mvn package

FROM debian:buster

RUN apt update \
    && apt install --no-install-recommends -y openjdk-11-jre-headless \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/heinthanth/uit
COPY --from=compiler /opt/heinthanth/uit/build/uit-*.jar .

ENTRYPOINT ["java", "-jar", "uit-1.0.0-alpha.jar"]