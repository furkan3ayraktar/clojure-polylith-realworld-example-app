FROM gitpod/workspace-full-vnc

RUN brew install clojure/tools/clojure

# RUN echo export JAVA_TOOL_OPTIONS=\"\$JAVA_TOOL_OPTIONS -Dsun.java2d.xrender=false\" >> /home/gitpod/.bashrc
