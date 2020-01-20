## 以辰社区

## 部署
- Git
- JDK
- Maven
- MySQL

## 部署 CentOS 5.7
- yum update
- yum install git
- mkdir App; cd App
- git co https://github.com/cyt574/community.git
- yum install maven;mvn -v
- mvn clean compile package
- java -jar -Dspring.profiles.active=prod target/community-0.0.1-SNAPSHOT.jar
- git pull


## 资料
[Spring文档](https://spring.io/docs/reference)

[Spring MVC](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html)

[SpringBoot](https://docs.spring.io/spring-boot/docs/2.1.7.RELEASE/reference/html/)

[ES](https://elasticsearch.cn/)

[JQuery.js](https://jquery.com/download/)

[Bootstrap](https://v3.bootcss.com/)

[elaticsearch](https://elasticsearch.cn/)

[Github OAuth](https://developer.github.com/v3/guides/managing-deploy-keys/#deploy-keys)

[okhttp](https://square.github.io/okhttp/)

[mybatis](http://www.mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)

[thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)

[Github OAuth](https://developer.github.com/apps/building-oauth-apps/authorizing-oauth-apps/)

[Markdown Editor](https://pandao.github.io/editor.md/)

[ufile](https://github.com/ucloud/ufile-sdk-java/blob/master)

## 工具

[Git](https://git-scm.comg)
[Flyway](https://flywaydb.org/getstarted/firststeps/maven)
[lombok](https://www.projectlombok.org/features/all)

## 脚本
```sql DDL
classpath:db.migration/*
```

```bash
# database version control
mvn flyway:migrate
# mybatis code generator
mvn -Dmybatis.generator.overwrite=true mybatis-generator:generate   
# third part jar ==> maven repository
mvn install:install-file -DgroupId=imsdriver（jar包的groupId） -DartifactId=imsdriver（jar包的artifactId） -Dversion=1.0（jar的版本号） -Dpackaging=jar -Dfile=D:\jar\imsdriver.jar（jar包的具体路径）
```

 
