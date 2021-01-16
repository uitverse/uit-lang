FROM openjdk:11-jdk-slim-buster as compiler

RUN apt update && apt install -y make git unzip \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/heinthanth/uit

COPY ./lib ./lib
COPY ./src ./src
COPY .version .version
COPY Makefile .

RUN make uit

FROM openjdk:11-jre-slim-buster

COPY --from=compiler /opt/heinthanth/uit/build/uit.jar .
ENTRYPOINT [ "java", "-jar", "uit.jar" ]
