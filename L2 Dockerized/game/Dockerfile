#OpenJDK 14 based on Alpine Linux
FROM openjdk:14-alpine 

RUN apk update
RUN apk --no-cache add wget unzip git maven

#Create folder to extract gameserver files
RUN mkdir -p /usr/gameserver

#Clone gameserver & datapack files into /usr/gameserver
RUN git clone https://github.com/gsargo/L2Medusa/tree/c35cca9589f5efd1e6165c0456d7d6dfcd9b0db8/aCis_gameserver_new /usr/gameserver
RUN git clone https://github.com/gsargo/L2Medusa/tree/c35cca9589f5efd1e6165c0456d7d6dfcd9b0db8/acis_datapack_new /usr/gameserver

#Set default work dir
WORKDIR /usr/gameserver

#Make sure the running script will have appropriate rights
RUN chmod 755 startGameServer.sh

#Run the Java server,set min RAM 512MB, set max RAM 2GB, run Lineage 2 game server
WORKDIR /usr/gameserver
CMD ["java", "-Xms512m", "-Xmx2g", "-jar", "l2jserver.jar"]