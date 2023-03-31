#!/bin/bash

while getopts "e:" opt; do
  case $opt in
  e)
    environment="$OPTARG"
    ;;
  \?)
    echo "Invalid option: -$OPTARG" >&2
    exit 1
    ;;
  :)
    echo "Option -$OPTARG requires an argument." >&2
    exit 1
    ;;
  esac
done

# Check if -e parameter is provided and is either "dev" or "prod"
if [[ -z "$environment" ]]; then
  echo "Error: -e parameter is required. Valid options are \"dev\" or \"prod\"."
  exit 1
elif [[ "$environment" != "dev" && "$environment" != "prod" ]]; then
  echo "Error: Invalid value for -e parameter. Valid options are \"dev\" or \"prod\"."
  exit 1
fi

# 删除旧的container
docker stop mysql_gpt && docker rm mysql_gpt
docker stop java_chatgpt && docker rm java_chatgpt

# 本地编译java代码
mvn clean package  --settings settings.xml

docker-compose -f docker-compose-${environment}.yaml build
docker-compose -f docker-compose-${environment}.yaml up
