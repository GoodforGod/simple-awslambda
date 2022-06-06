FROM goodforgod/amazonlinux-graalvm:22.1.0-java17-amd64

ENV GRAALVM_HOME=/usr/lib/graalvm
ADD . .
RUN gradle nativeTest --no-daemon

ENTRYPOINT ["ps"]
