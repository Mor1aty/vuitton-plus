FROM openjdk:21

ADD target/vuitton-plus.jar vuitton-plus.jar

EXPOSE 8000

ARG DEPLOY_IP
ENV DEPLOY_IP $DEPLOY_IP

ENTRYPOINT java -jar vuitton-plus.jar --server.deploy-ip="$DEPLOY_IP"