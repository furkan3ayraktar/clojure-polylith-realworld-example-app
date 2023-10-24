FROM gitpod/workspace-java-17

ENV PATH="/home/gitpod/bin:/workspace/clojure-polylith-realworld-example-app/.gitpod:${PATH}"
ENV JAVA_HOME="/home/gitpod/.sdkman/candidates/java/current"
ENV JAVA_TOOL_OPTIONS="-Xmx3489m"

RUN mkdir /home/gitpod/bin
RUN wget -P /home/gitpod https://github.com/polyfy/polylith/releases/download/v0.2.17-alpha/poly-0.2.17-alpha.jar
RUN bash -c cat <<'EOF' > /home/gitpod/bin/poly && chmod +x /home/gitpod/bin/poly
#!/bin/bash

java $JVM_OPTS "-jar" "/home/gitpod/poly-0.2.17-alpha.jar" $@

EOF
