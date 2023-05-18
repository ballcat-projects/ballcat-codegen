# BallCat-Codegen

执行启动脚本后，运行启动类

```java
com/hccake/ballcat/codegen/GeneratorApplication.java
```

- 启动地址：http://localhost:7777/
- 预览地址：http://codegen.ballcat.cn/
- 文档地址：http://www.ballcat.cn/codegen/

## SQL 脚本的执行

从 v1.1.0 版本开始，sql 表结构以及部分基本数据交由 flyway 管理。

用户只需修改 ballcat-codegen-backend/src/main/resources/application-dev.yml 中的数据源配置，项目启动后即可自动生成数据库和表结构。

> 注意：自动生成数据库依赖 jdbc 的连接参数：createDatabaseIfNotExist=true
> 建议使用一个全新的数据库，如果使用的数据库中已经存在其他表，则需要添加配置 spring.flyway.baseline-version=0 再进行启动

## 模板管理

模板组的 sql 不在 flyway 的管理之下，用户自己按需构建模板组。
**欢迎大家 pr 来共享自己创建的模板组**

目前在根目录下的 `template` 文件夹下，提供了一些默认模板的 SQL 文件，用户按需选择对应的 SQL 进行初始化：

### Ballcat Admin 单体项目骨架 (ballcat-admin-boot)
一键生成基于 Ballcat Admin 的单体后台管理应用

### Ballcat 业务增删改查模板 (ballcat-bussines-crud)
快速生成基于 ballcat framework 的增删改查的基础代码，包含前后端


## 如何在不启动前端项目的情况下使用

**必须先在父工程 ballcat-codegen 处执行 `mvn clean package` 打包命令**  

此命令会将前端进行 build，并将打包好的代码 copy 入后端项目的 resource 资源下。

第一次打包时会下载 node 以及前端项目依赖，耗时较久，可以先 cd 进 ballcat-codegen-frontend 前端项目处自行执行 npm install，切换使用淘宝镜像源以加快依赖下载速度。



##### Docker打包

采用 dockerfile-maven-plugin 插件生成 docker 镜像，所在路径 ballcat-codegen-backend/pom.xml

```
<plugin>
  <groupId>com.spotify</groupId>
  <artifactId>dockerfile-maven-plugin</artifactId>
  <executions>
    <execution>
      <id>default</id>
      <goals>
        <!-- 如果package时不想用docker打包,就注释掉这个goal -->
        <goal>build</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <repository>ballcat-codegen</repository>
    <tag>latest</tag>
    <buildArgs>
      <JAR_FILE>${project.build.finalName}.jar</JAR_FILE>
    </buildArgs>
  </configuration>
</plugin>
```

执行 `mvn clean package` 打包命令时会根据 ballcat-codegen-backend 根目录下的 Dockerfile 自动构建 docker 镜像。



##### docker-compose部署

打开 ballcat-codegen-backend 根目录下的 docker-compose.yml文件，修改环境变量配置你的数据库连接信息

```
version: "3.9"

services:
  codegen:
    image: ballcat-codegen:latest
    container_name: ballcat-codegen
    restart: always
    ports:
      - "7777:7777"
    volumes:
      - "./logs:/workspace/logs"
    environment:
      - TZ=Asia/Shanghai
      - LANG=en_US.UTF-8
      - JAVA_OPTS=-Xmx256m -Xms256m -Xmn256m -Xss1m
      - DB_HOST=192.168.1.66
      - DB_PORT=3306
      - DB_NAME=ballcat_codegen
      - DB_USER=root
      - DB_PASSWORD=root

```

终端切换到 docker-compose.yml 所在目录，执行 `docker-compose up -d` 一键部署。
