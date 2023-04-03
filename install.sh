#!/bin/bash



# 删除旧的container
docker stop mysql_gpt && docker rm mysql_gpt
docker stop java_chatgpt && docker rm java_chatgpt

docker-compose -f docker-compose-dev.yaml build
docker-compose -f docker-compose-dev.yaml up
