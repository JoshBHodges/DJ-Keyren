FROM java:8
COPY ./out/artifacts/DJ_Keyren_jar/ /tmp
WORKDIR /tmp
CMD ["java", "-jar", "DJ-Keyren.jar"]
