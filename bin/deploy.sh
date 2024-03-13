sh undeploy.sh

cd ../
mvn clean package -Dmaven.test.skip=true

IP_ADDRESS=$(ifconfig wlp2s0 | awk 'NR==2{print $2}')

docker build --build-arg FILE_SERVER_IP=$IP_ADDRESS --build-arg DATASOURCE_URL="jdbc:mysql://$IP_ADDRESS:3306/vuitton_plus?serverTimezone=GMT%2B8&useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&autoReconnect=true&failOverReadOnly=false&useSSL=false&allowPublicKeyRetrieval=true" \
  -t vuitton-plus .

docker run -di --name vuitton-plus -p 8000:8000 --restart=always vuitton-plus

docker ps | grep vuitton-plus
