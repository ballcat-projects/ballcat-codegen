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

## 模板文件

模板组的 sql 不在 flyway 的管理之下，用户再启动后按需选择自己需要的模板组进行插入。

模板组的 sql 在 `ballcat-codegen-backend/src/main/resources/db/template` 下

目前只有 ballcat-admin 的模板，用户可在插入后，自行在 web 界面中复制模板组然后进行模板定制

## 如何在不启动前端项目的情况下使用

**必须先在父工程 ballcat-codegen 处执行 `mvn clean package` 打包命令**  

此命令会将前端进行 build，并将打包好的代码 copy 入后端项目的 resource 资源下。

第一次打包时会下载 node 以及前端项目依赖，耗时较久，可以先 cd 进 ballcat-codegen-frontend 前端项目处自行执行 npm install，切换使用淘宝镜像源以加快依赖下载速度。



