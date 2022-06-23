#!/bin/bash
# 此命令在linux上时，建议通过 vim /yourdir/docker-entrypoint.sh编辑，拷贝，否则运行时可能会报错
export   LANG=C.UTF-8
source /etc/profile
echo "Asia/shanghai" > /etc/timezone
cd /opt/apps
appname=$(ls | grep .jar | sort -rn)
#appname=$(find ./ -type f -name "*.jar" | sort -rn)
name=$(echo $appname|cut -d ' ' -f1)

configname=$(ls | grep application. | sort -rn)
configfile=$(echo $configname|cut -d ' ' -f1)

mkdir -p logs && chmod 755 logs

exec java -jar \
  -Duser.timezone=GMT+08 \
  -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=15001   \
  $name \
  --spring.config.location=$configfile \
  > ./logs/$(echo "$name" | cut -f 1 -d '.').log