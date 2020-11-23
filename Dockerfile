FROM java:8
COPY ./build/libs/ /tmp
WORKDIR /tmp
CMD ["java", "-jar", "DJ-Keyren-all.jar"]
