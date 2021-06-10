# open-api-project
一、项目介绍 : open-api-project 对外接口统一安全验证及鉴权
Spring Boot + MyBatis-plus + MyBatis-plus + freemarker + jdk1.8

maven repository详情已上传到我的GitHub: https://github.com/wugang-hub/maven-repo.git

二、设计思路：
公私钥对：公钥加密，私钥解密；  私钥生成签名，公钥验签。：
1、生成公私钥对，绑定app_id，存入数据库
2、将app_id和私钥对外
3、对外根据app_id和私钥生成自己的签名sign
4、再结合其他参数一起放入接口传过来（参数是否需要公私钥加密解密传输，根据自己具体业务逻辑来处理）
5、拿到参数根据app_id获取对应的公钥去验签
6、同时针对访问者IP校验（根据具体项目要求做处理）
注意：签名类型signType  和  签名sign  要保持一致，否则验签不过;
如果需要IP校验，可以将IP设计为app_id的value，去绑定公私钥对。

详细设计请看对外接口服务设计文档：
https://github.com/wugang-hub/open-api-project/blob/master/%E5%AF%B9%E5%A4%96%E6%8E%A5%E5%8F%A3%E6%9C%8D%E5%8A%A1%E6%96%87%E6%A1%A3%E8%AE%BE%E8%AE%A1.docx

三、配置文件说明
1、application-common.yml：服务端口设置、日志配置、mybatis-plus配置。
2、application-dev.yml：数据库信息配置、静态参数设置。
3、init.sql：初始化数据库表结构及数据   

四、使用说明：
1、demo中简单设计了用户和密码表，后面根据具体业务需求往里面填充内同。
2、下载下来启动Appication就可以访问接口：http://localhost:8821/open/gateway ,使用postman测试，传入参数即可。
