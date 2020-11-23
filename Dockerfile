FROM java:8
WORKDIR /
ADD . .
CMD java - jar Bot.jar
