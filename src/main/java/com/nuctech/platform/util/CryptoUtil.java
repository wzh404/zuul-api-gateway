package com.nuctech.platform.util;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.nuctech.platform.exception.NuctechPlatformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES算法加解密工具类
 *
 * Created by wangzunhui on 2017/6/13.
 */
public class CryptoUtil {
    private static final  Logger logger = LoggerFactory.getLogger(CryptoUtil.class);
    private static final  String AES_ALG = "AES";
    private static final  String AES_MODE = "AES/ECB/PKCS5Padding";
    private static final  int AES_KEY_LENGTH = 128;

    private CryptoUtil() {
        throw new IllegalStateException("Utility Crypto");
    }

    private static SecretKeySpec generatorKey(String key) {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance(CryptoUtil.AES_ALG);
            keygen.init(CryptoUtil.AES_KEY_LENGTH, new SecureRandom(key.getBytes()));
            SecretKey secretKey = keygen.generateKey();
            byte[] raw = secretKey.getEncoded();
            return new SecretKeySpec(raw, CryptoUtil.AES_ALG);
        }catch (NoSuchAlgorithmException e){
            logger.error("exception: ", e);
            return null;
        }
    }

    /**
     * AES加密
     * @param val 加密数据
     * @return 加密后的密文
     * @throws Exception
     */
    public static String encrypt(String val, String key) {
        assert(key != null);
        SecretKeySpec keySpec = generatorKey(key);
        if (keySpec == null || val == null || val.length() == 0){
            throw new IllegalArgumentException("encrypt: invalid key or value!");
        }

        try {
            Cipher cipher = Cipher.getInstance(CryptoUtil.AES_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(val.getBytes());
            return Base64.getUrlEncoder().encodeToString(encrypted);
        }catch (Exception e){
            throw new NuctechPlatformException("encrypt failed", e);
        }
    }

    /**
     * AES解密
     * @param val 加密后的密文
     * @return 解密后的明文
     * @throws Exception
     */
    public static String decrypt(String val, String key){
        assert(key != null);
        SecretKeySpec keySpec = generatorKey(key);
        if (keySpec == null || val == null || val.length() == 0){
            throw new IllegalArgumentException("decrypt: invalid key or value!");
        }

        try {
            Cipher cipher = Cipher.getInstance(CryptoUtil.AES_MODE);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decrypted = cipher.doFinal(Base64.getUrlDecoder().decode(val));
            return new String(decrypted);
        }catch (Exception e){
            throw new NuctechPlatformException("decrypt failed", e);
        }
    }

    /**
     * 对字符列表进行散列
     *
     * @param salt 盐
     * @param args 签名列表
     *
     * @return 散列结果
     */
    public static String signature(String salt, String... args){
        assert(salt != null);

        Hasher hasher = Hashing.sha256().newHasher();
        hasher.putString(salt, Charset.forName("utf-8"));
        for (String arg : args){
            if (!StringUtils.isEmpty(arg)){
                hasher.putString(arg, Charset.forName("utf-8"));
            }
        }

        return hasher.hash().toString();
    }
}
