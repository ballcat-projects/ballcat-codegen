FROM openjdk:8
MAINTAINER john

ENV TZ 'Asia/Shanghai'
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

VOLUME /tmp

ARG JAR_FILE=ballcat-codegen.jar
ADD target/${JAR_FILE} /workspace/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","-Dfile.encoding=UTF-8","-Dsun.jnu.encoding=UTF-8","/workspace/app.jar"]
