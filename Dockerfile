FROM registry.access.redhat.com/ubi8/ubi-minimal

RUN microdnf install -y git jq && \
     git config --global user.email "ci@example.com" && \
     git config --global user.name "CI Action"


COPY docker/entrypoint.sh /entrypoint.sh
COPY target/multi-repo-ci-tool-runner /multi-repo-ci-tool-runner

ENTRYPOINT ["/entrypoint.sh"]

