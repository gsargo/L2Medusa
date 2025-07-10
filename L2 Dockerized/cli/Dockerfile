FROM openjdk:14-alpine

RUN apk update
RUN apk --no-cache add wget unzip git maven

RUN wget "https://l2jserver.com/files/binary/cli/l2jcli-1.0.7.zip" -P /tmp
RUN mkdir -p /usr/cli
RUN unzip /tmp/l2jcli-*.zip -d /usr/cli

RUN git clone https://bitbucket.org/l2jserver/l2j-server-login.git /tmp/loginserver
RUN git clone https://bitbucket.org/l2jserver/l2j-server-datapack.git /tmp/gameserver/datapack

WORKDIR /usr/cli

RUN mkdir login
RUN mv /tmp/loginserver/src/main/resources/sql login

RUN mkdir -p game
RUN mv /tmp/gameserver/datapack/src/main/resources/sql game

RUN sed -i 's/\/\/localhost/\/\/db/g' config/login-server.properties
RUN sed -i 's/\/\/localhost/\/\/db/g' config/game-server.properties

CMD ["java", "-jar", "./l2jcli.jar"]
