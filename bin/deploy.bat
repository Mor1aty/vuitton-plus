@echo off

call undeploy.bat

echo deploy begin

cd ..

set JAVA_HOME=E:\language\jdk-21.0.1

call mvn clean package -Dmaven.test.skip=true

ssh -p 22 moriaty@192.168.50.112 "mkdir -p /tmp/vuitton-plus/target"

scp target/vuitton-plus.jar moriaty@192.168.50.112:/tmp/vuitton-plus/target/vuitton-plus.jar

scp Dockerfile moriaty@192.168.50.112:/tmp/vuitton-plus/Dockerfile

scp bin/deploy.sh moriaty@192.168.50.112:/tmp/vuitton-plus/deploy.sh

ssh -p 22 moriaty@192.168.50.112 "cd /tmp/vuitton-plus; sh /tmp/vuitton-plus/deploy.sh"

echo deploy done
