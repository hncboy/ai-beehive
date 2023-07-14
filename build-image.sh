#!/bin/bash


####注注注注注注注注注注注注注注注注注注注注注注注注注注注注注注

# 因为我暂时没有办法处理命令行直接运行clean 和 install，会报错 《无效的目标发行版: 17 》
# 所以需要先用idea的maven工具进行clean和install再执行下面的docker命令
#                 请先执行idea工具的clean和install

####注注注注注注注注注注注注注注注注注注注注注注注注注注注注注注

#定义变量
IMAGE_NAME="lizhiheng1995/pigtalk:latest"
# 注意，因为我的机器是m1的，所以我要指定打amd64的镜像
docker build --platform linux/amd64 -t $IMAGE_NAME .

# 检查构建是否成功
if [ $? -eq 0 ]; then
    echo "Docker 镜像构建成功！"
    # 推送docker镜像
    docker push $IMAGE_NAME
else
    echo "Docker 镜像构建失败。"
fi



