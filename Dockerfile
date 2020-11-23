FROM java:8
COPY ./out/production/DockerJavaApp/ /tmp
WORKDIR /tmp
CMD java - jar DJ-Keyren.jar
