#!/bin/bash

export LANG=C.UTF-8

appname=$(ls | grep .jar | sort -rn)
#appname=$(find ./ -type f -name "*.jar" | sort -rn)
name=$(echo $appname|cut -d ' ' -f1)

confName=$(ls | grep .properties | sort -rn)
confFile=$(echo $confName|cut -d ' ' -f1)

echo "cmd: ./jre/bin/java -jar $name -Duser.timezone=GMT+08 --spring.config.location=$confFile >> ./logs/$(echo "$name" | cut -f 1 -d '.').log"

mkdir -p ./logs 

./jre/bin/java -jar \
  $name \
  -Duser.timezone=GMT+08 \
  --spring.config.location=$confFile \
  >> ./logs/$(echo "$name" | cut -f 1 -d '.').log
