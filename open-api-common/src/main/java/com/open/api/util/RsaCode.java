package com.open.api.util;

import com.alipay.api.internal.util.codec.Base64;
import com.google.common.collect.Maps;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.regex.Pattern;

public class RsaCode {

    /** 非对称加密算法 */
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * 密钥长度，DH算法的默认密钥长度为 1024
     * 密钥长度必须是64的倍数，在512到65536位之间
     * DH算法是一种建立密钥的方法，并非加密方法，但其产生的密钥可用于加密、密钥管理或任何其它的加密方式。
     */
    private static final int KEY_SIZE = 512;

    /** 公钥 */
    private static final String PUBLIC_KEY = "tianwanggaidihu";

    /** 私钥 */
    private static final String PRIVATE_KEY = "12345";

    /** 签名算法 */
    public static final String SIGN_ALGORITHM = "SHA256withRSA";

    /**
     * 初始化密钥对
     * @return map 密钥的map
     */
    public static Map<String, Object> initKey() throws NoSuchAlgorithmException {
        // 实例化 RSA密钥生成器
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        // 初始化密钥生成器
        keyPairGenerator.initialize(KEY_SIZE);
        // 生成密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // 公钥
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        // 私钥
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 密钥存储在 map 中
        Map<String, Object> keyMap = Maps.newHashMap();
        keyMap.put(PUBLIC_KEY, rsaPublicKey);
        keyMap.put(PRIVATE_KEY, rsaPrivateKey);

        return keyMap;
    }

    /**
     * 公钥加密
     * @param data 待加密数据
     * @param key 密钥
     * @return byte[] 加密数据
     * */
    public static byte[] encryptByPublicKey(byte[] data, byte[] key)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        // 实例化密钥工厂
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        //初始化公钥
        //密钥材料转换
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key);
        // 生成公钥
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);

        // 数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptData = cipher.doFinal(data);
        return encryptData;
    }

    /**
     * 私钥解密
     * @param data 待解密数据
     * @param key 密钥
     * @return byte[] 解密数据
     */
    public static byte[] decryptByPrivateKey(byte[] data, byte[] key)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        // 取得私钥
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(key);
        // 生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        // 数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptData = cipher.doFinal(data);
        return decryptData;
    }

    /**
     * 取得私钥
     * @param keyMap 密钥 map
     * @return byte[] 私钥
     */
    public static byte[] getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return key.getEncoded();
    }

    /**
     * 取得公钥
     * @param keyMap 密钥 map
     * @return byte[] 公钥
     */
    public static byte[] getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return key.getEncoded();
    }

    public static void main(String[] args) throws Exception{
        // 初始化密钥，生成密钥对
        Map<String, Object> keyMap = RsaCode.initKey();
        // 公钥
        byte[] publicKey = RsaCode.getPublicKey(keyMap);
        // 私钥
        byte[] privateKey = RsaCode.getPrivateKey(keyMap);

        System.out.println("公钥：" + new String(Base64.encodeBase64(publicKey)));
        System.out.println("私钥：" + new String(Base64.encodeBase64(privateKey)));

        System.out.println("================数据传输：公钥加密，私钥解密  start =============");
        // 乙方要发送的数据
        String data2 = "我是乙方";
        System.out.println("原文:" + data2);
        //乙方使用 甲方公钥对数据进行加密
        byte[] code2 = RsaCode.encryptByPublicKey(data2.getBytes(), publicKey);
        System.out.println("===========乙方使用 甲方公钥对数据进行加密==============");
        System.out.println("加密后的数据："+Base64.encodeBase64(code2));
        // 甲方使用私钥对数据进行解密
        byte[] decode2 = RsaCode.decryptByPrivateKey(code2, privateKey);
        System.out.println("甲方解密后的数据：" + new String(decode2));
        System.out.println("================数据传输：公钥加密，私钥解密  end  =============");


        System.out.println("===========签名： 私钥签名，公钥验签 start ==============");
        String data3 = "签名及验签";
        // 签名
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign((PrivateKey) keyMap.get(PRIVATE_KEY));
        signature.update(data3.getBytes("UTF-8"));
        byte[] signData = Base64.encodeBase64(signature.sign());
        // 验签
        Signature signature2 = Signature.getInstance(SIGN_ALGORITHM);
        signature2.initVerify((PublicKey) keyMap.get(PUBLIC_KEY));
        signature2.update(data3.getBytes("UTF-8"));
        boolean verify = signature2.verify(Base64.decodeBase64(signData));
        System.out.println(verify);
        if (verify) {
            System.out.println("验签成功,数据没有被更改");
        } else {
            System.out.println("验签失败,数据被更改了");
        }
        System.out.println("===========签名： 私钥签名，公钥验签 end ==============");
    }
}
