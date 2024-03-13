@echo off

echo stop begin

ssh -p 22 moriaty@192.168.50.112 "mkdir -p /tmp/vuitton-plus"

scp stop.sh moriaty@192.168.50.112:/tmp/vuitton-plus/stop.sh

ssh -p 22 moriaty@192.168.50.112 "cd /tmp/vuitton-plus; sh /tmp/vuitton-plus/stop.sh"

echo stop done
