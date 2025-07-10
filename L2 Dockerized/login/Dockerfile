FROM openjdk:14-alpine

RUN apk update
RUN apk --no-cache add wget unzip git maven

RUN mkdir -p /usr/loginserver

RUN git clone https://github.com/gsargo/L2Medusa/tree/e7b041d45551e5003e134e384e468e1ed0fb7c81/acis_loginserver_new /usr/loginserver

WORKDIR /usr/loginserver
RUN chmod startLoginServer.sh

CMD ["java", "-Xms128m", "-Xmx256m", "-jar", "l2jlogin.jar"]