# Dockerfile

FROM scratch
COPY target/*-runner /app
ENTRYPOINT ["/app"]
