FROM gitpod/workspace-java-11

RUN apt-get -y update
RUN apt-get -y install curl
RUN curl -L -O https://github.com/clojure/brew-install/releases/latest/download/linux-install.sh
RUN chmod +x linux-install.sh
RUN ./linux-install.sh

# RUN echo export JAVA_TOOL_OPTIONS=\"\$JAVA_TOOL_OPTIONS -Dsun.java2d.xrender=false\" >> /home/gitpod/.bashrc
