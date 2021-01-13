FROM debian:buster as compiler

RUN apt update \
    && apt install --no-install-recommends -y openjdk-11-jdk-headless \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/heinthanth/uit
COPY src ./

ENV JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
RUN javac -d build com/heinthanth/uit/Utils/GenerateNode.java \
	&& java -cp build com.heinthanth.uit.Utils.GenerateNode com/heinthanth/uit/Runtime \
    && javac -g:none -Werror -d build -cp . com/heinthanth/uit/Main.java


FROM debian:buster

RUN apt update \
    && apt install --no-install-recommends -y openjdk-11-jre-headless \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/heinthanth/uit
COPY --from=compiler /opt/heinthanth/uit/build .

ENTRYPOINT ["java", "-cp", ".", "com.heinthanth.uit.Main"]