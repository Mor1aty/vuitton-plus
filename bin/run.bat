@echo off

echo run begin

ssh -p 22 moriaty@192.168.50.112 "mkdir -p /tmp/vuitton-plus"

scp run.sh moriaty@192.168.50.112:/tmp/vuitton-plus/run.sh

ssh -p 22 moriaty@192.168.50.112 "cd /tmp/vuitton-plus; sh /tmp/vuitton-plus/run.sh"

echo run done
