## 以辰社区
## 资料
[Spring文档](https://spring.io/guides)

[Spring MVC](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html)

[ES](https://elasticsearch.cn/)

[JQuery.js](https://jquery.com/download/)

[Bootstrap](https://v3.bootcss.com/)

[elaticsearch](https://elasticsearch.cn/)

[Github OAuth](https://developer.github.com/v3/guides/managing-deploy-keys/#deploy-keys)

[okhttp](https://square.github.io/okhttp/)

[mybatis](http://www.mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)

[thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html)

[Github OAuth](https://developer.github.com/apps/building-oauth-apps/authorizing-oauth-apps/)

## 工具

[Git](https://git-scm.comg)
[Flyway](https://flywaydb.org/getstarted/firststeps/maven)
[lombok](https://www.projectlombok.org/features/all)

## 脚本
```sql
create table USER
(
	ID INTEGER auto_increment,
	ACCOUNT_ID VARCHAR(100),
	NAME VARCHAR(50),
	TOKEN CHAR(36),
	GMT_CREATE BIGINT,
	GMT_MODIFIED BIGINT,
	constraint USER_PK
		primary key (ID)
);
```

```bash
mvn flyway:migrate
mvn -Dmybatis.generator.overwrite=true mybatis-generator:generate   
```

 
