FROM openjdk:21

ADD target/vuitton-plus.jar vuitton-plus.jar

EXPOSE 8000

ARG DATASOURCE_URL
ENV DATASOURCE_URL $DATASOURCE_URL

ARG FILE_SERVER_IP
ENV FILE_SERVER_IP $FILE_SERVER_IP

ENTRYPOINT java -jar vuitton-plus.jar --spring.datasource.url="$DATASOURCE_URL"