@echo off

echo undeploy begin

ssh -p 22 moriaty@192.168.50.112 "mkdir -p /tmp/vuitton-plus"

scp undeploy.sh moriaty@192.168.50.112:/tmp/vuitton-plus/undeploy.sh

ssh -p 22 moriaty@192.168.50.112 "cd /tmp/vuitton-plus; sh /tmp/vuitton-plus/undeploy.sh"

echo undeploy done
