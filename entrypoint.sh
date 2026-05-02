#!/bin/sh
# used only in Dockerfile, LIBERATION_JAR_PATH is ENV set in it
exec java -jar "${LIBERATION_JAR_PATH}"
