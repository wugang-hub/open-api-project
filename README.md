项目介绍 :open-api-project 对外接口统一网关鉴权
Spring Boot + MyBatis-plus + freemarker + jdk1.8

设计思路:
公私钥对：公钥加密，私钥解密；  私钥生成签名，公钥验签。
1、生成公私钥对，绑定app_id
2、将app_id和私钥对外
3、对外根据app_id和私钥生成自己的签名sign
4、再结合其他参数一起放入接口传过来（参数是否需要公私钥加密解密传输，根据自己具体业务逻辑来处理）
5、拿到参数根据app_id获取对应的公钥去验签
注意：签名类型signType  和  签名sign  要保持一致，否则验签不过;

使用说明:
1、demo中使用的是静态数据，没有跟数据库交互，根据项目需求，可以将app_id绑定公私钥存入数据库，方便动态获取。
2、下载下来启动Appication就可以访问接口：http://localhost:8821/open/gateway,使用postman测试，传入参数即可。
