FROM gitpod/workspace-java-21

ENV PATH="/home/gitpod/bin:/workspace/clojure-polylith-realworld-example-app/.gitpod:${PATH}"
ENV JAVA_HOME="/home/gitpod/.sdkman/candidates/java/current"
ENV JAVA_TOOL_OPTIONS="-Xmx3489m"

# Install Node.js 20
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash - \
    && sudo apt-get install -y nodejs \
    && node -v && npm -v

RUN mkdir /home/gitpod/bin
RUN wget -P /home/gitpod https://github.com/polyfy/polylith/releases/download/v0.2.22/poly-0.2.22.jar
RUN bash -c cat <<'EOF' > /home/gitpod/bin/poly && chmod +x /home/gitpod/bin/poly
#!/bin/bash

java $JVM_OPTS "-jar" "/home/gitpod/poly-0.2.22.jar" $@

EOF
RUN curl -sL https://raw.githubusercontent.com/borkdude/deps.clj/master/install > install_clojure && chmod +x install_clojure && ./install_clojure --dir /home/gitpod/bin --as-clj && clojure -Sdeps '{:deps {nrepl/nrepl {:mvn/version,"1.0.0"},cider/cider-nrepl {:mvn/version,"0.28.5"}}}' -P
