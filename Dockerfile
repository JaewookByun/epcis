FROM maven:3.9.3-eclipse-temurin-20

WORKDIR /project
COPY ./epcis-server/ /project/
RUN rm -rf /project/target

RUN apt update
RUN apt install zip unzip

RUN mvn clean
RUN mvn dependency:copy-dependencies
RUN mvn package

RUN cp /project/target/epcis-server-*.jar /project/target/epcis-server-light.jar

RUN mkdir ./target/jar-unzip
RUN find ./target/dependency -iname \*.jar -printf 'unzip -o %p -d ./target/jar-unzip\n' > jar-unzip-script.sh
RUN chmod +x jar-unzip-script.sh
RUN ./jar-unzip-script.sh

RUN unzip -o /project/target/epcis-server-light.jar -d ./target/jar-unzip
RUN unzip -o /project/target/epcis-server-light.jar -d ./target/jar-unzip

RUN cd ./target/jar-unzip/ && zip -r /project/target/epcis-server-fat.jar .

WORKDIR /app

RUN cp /project/target/epcis-server-light.jar /app/epcis-server-light.jar
RUN cp /project/target/epcis-server-fat.jar /app/epcis-server-fat.jar

RUN cp -r /project/src/main/resources/configuration.json /app/configuration.json  
RUN cp -r /project/src/main/resources/log4j.xml /app/log4j.xml

RUN rm -rf /project

ENTRYPOINT ["sh", "-c"]
CMD ["exec java -classpath /app/epcis-server-fat.jar org.oliot.epcis.server.EPCISServer /app/configuration.json"]
