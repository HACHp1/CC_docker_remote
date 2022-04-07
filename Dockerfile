FROM ubuntu:18.04

RUN apt-get update
RUN apt-get install -y iputils-ping

ADD jdk-7u21-linux-x64.tar.gz /usr/local
ENV JAVA_HOME /usr/local/jdk1.7.0_21
ENV PATH $PATH:$JAVA_HOME/bin

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=10087,server=y,suspend=n

COPY deser-0.0.1-SNAPSHOT.jar /usr/src/deser.jar
EXPOSE 10086

CMD ["java", "-Dserver.address=0.0.0.0", "-Dserver.port=10086", "-jar", "/usr/src/deser.jar"]
