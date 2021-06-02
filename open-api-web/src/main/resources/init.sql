DROP TABLE IF EXISTS `u_userinfo`;
CREATE TABLE `u_userinfo`
(
    `id`       int(11) NOT NULL AUTO_INCREMENT,
    `phone`    varchar(16)  DEFAULT NULL,
    `name`     varchar(32)  DEFAULT NULL,
    `password` varchar(256) DEFAULT NULL,
    `image`    mediumtext,
    `mail`     varchar(255)  DEFAULT NULL
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 10000001
  DEFAULT CHARSET = utf8mb4;

INSERT INTO u_userinfo (id, phone, name, password, image, mail)
VALUES (10000000, '13888888888', 'admin', 'admin', '', '');

DROP TABLE IF EXISTS `u_user_secret`;
CREATE TABLE `u_user_secret` (
  `app_id` varchar(16) NOT NULL,
  `private_key` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `public_key` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci,
  `ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`app_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;

INSERT INTO `u_user_secret` (`app_id`, `private_key`, `public_key`, `ip`)
VALUES ('101', 'MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAwmIk9grMDJ1/yIloZr5bfwAldNS4ZgnLBpLw9OFalHPbFU4GEQ4QrEemSX5LIcIjz80YJd7GByYgTmEM/yVxuwIDAQABAkB+lhmJ3Vgqsvq/ZrSFjFvbZgX+gnUfhisgOdsPLh6l43ZZMoeIdiwsULnw75rieNcVA9/HoX+Wst9iu202J5I5AiEA5PQlWQ7Qcazf2Vb701wCG+iOvz/nAvPWvGMEahn8hG0CIQDZWIwwTIHLJ2umYWb9qlDR13+UYtaK1Bj91M2yrdnlxwIgNfJZAnz9HGvRatKWD500WgMX73RNKGLwC/+AwMdSupkCIEhoGGPqyP6uBk3qew4c1EnkGeuMNd8QI7dEUrao9kN3AiEAhJL9Mq7rPnriC6ob8dQ9e/F+O3iXxf6aFaF9s4yRAyE=',
'MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMJiJPYKzAydf8iJaGa+W38AJXTUuGYJywaS8PThWpRz2xVOBhEOEKxHpkl+SyHCI8/NGCXexgcmIE5hDP8lcbsCAwEAAQ==', '127.0.0.1');
