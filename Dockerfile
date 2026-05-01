ARG LIBERATION_JAR_PATH="/app/liberation.jar" # app jar

FROM docker.io/library/ubuntu:24.04 AS builder

# installs
ARG SCALA_CLI_RELEASE="https://github.com/VirtusLab/scala-cli/releases/download/v1.13.0/scala-cli-x86_64-pc-linux.gz"
ARG PROTOC_RELEASE="https://github.com/scalapb/ScalaPB/releases/download/v1.0.0-alpha.3/protoc-gen-scala-1.0.0-alpha.3-linux-x86_64.zip"
ARG INSTALL_PREFIX="/usr/local/bin"

# java
RUN apt-get update && apt-get install -y protobuf-compiler make curl gzip unzip \
&& rm -rf /var/lib/apt/lists/*

# scala
ARG SCALA_INSTALL_PREFIX="${INSTALL_PREFIX}/scala"
RUN curl -fLo ${SCALA_INSTALL_PREFIX}.gz ${SCALA_CLI_RELEASE}   \
    && gzip -d ${SCALA_INSTALL_PREFIX}.gz                       \
    && chmod +x ${SCALA_INSTALL_PREFIX}                         \
    && chmod +x ${SCALA_INSTALL_PREFIX}


# scala protobuf plugin
ARG PROTOBUF_SCALA="protoc-gen-scala"
RUN curl -fLo ${PROTOBUF_SCALA}.zip ${PROTOC_RELEASE}    \
    && unzip ${PROTOBUF_SCALA}.zip                       \
    && mv ${PROTOBUF_SCALA} ${INSTALL_PREFIX}/           \
    && chmod +x ${INSTALL_PREFIX}/${PROTOBUF_SCALA}      \
    && rm ${PROTOBUF_SCALA}.zip

WORKDIR /app

COPY Makefile ./
COPY proto/ ./proto/
RUN make generate

COPY . .
ARG LIBERATION_JAR_PATH
RUN scala --power package --assembly -o ${LIBERATION_JAR_PATH} .

FROM eclipse-temurin:21-jre-jammy

# libre office
RUN apt-get update && apt-get install -y --no-install-recommends    \
    libreoffice-core  libreoffice-writer libreoffice-calc           \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
ARG LIBERATION_JAR_PATH
COPY --from=builder ${LIBERATION_JAR_PATH} /app/liberation.jar

ENV LIBERATION_JAR_PATH=${LIBERATION_JAR_PATH}
ENTRYPOINT java -jar ${LIBERATION_JAR_PATH}
