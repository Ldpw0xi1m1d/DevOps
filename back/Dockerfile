FROM openjdk
COPY /back/ /tmp
WORKDIR /tmp
RUN javac SimpleServer.java
CMD ["java", "SimpleServer.java"]
