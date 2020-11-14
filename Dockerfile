FROM registry.access.redhat.com/ubi8/ubi-minimal

COPY docker/entrypoint.sh /entrypoint.sh
COPY target/multi-repo-ci-tool-runner /multi-repo-ci-tool-runner

ENTRYPOINT ["/entrypoint.sh"]

