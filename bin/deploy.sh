DEPLOY_IP=$(ifconfig wlp2s0 | awk 'NR==2{print $2}')

docker build --build-arg DEPLOY_IP=$DEPLOY_IP -t vuitton-plus .

docker run -di --name vuitton-plus -p 8000:8000 --restart=always vuitton-plus

docker ps | grep vuitton-plus
