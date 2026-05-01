#!/bin/bash
set -euo pipefail
PRODUCTION_ENV=.env
source ${PRODUCTION_ENV}
NAME=$1

RUNTIME=$(command -v podman 2>/dev/null || command -v docker 2>/dev/null || { echo "no podman or docker" >&2; exit 1; })

$RUNTIME build . -t ${NAME} --layers
$RUNTIME run --rm --name ${NAME} --env-file ${PRODUCTION_ENV} -p ${PORT}:${PORT} ${NAME}
