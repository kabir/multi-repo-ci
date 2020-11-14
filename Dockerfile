#FROM alpine:3.12.1
FROM registry.access.redhat.com/ubi8/ubi-minimal

COPY target/multi-repo-ci-tool-runner /multi-repo-ci-tool-runner
COPY docker/entrypoint.sh /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]

