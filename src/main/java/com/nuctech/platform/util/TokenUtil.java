package com.nuctech.platform.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by wangzunhui on 2017/7/17.
 */
public class TokenUtil {
    private static final  Logger logger = LoggerFactory.getLogger(TokenUtil.class);
    private static final long EXPIRED_TIME = 1000 * 60 * 30L;
    public static final  String TOKEN = "token";
    public static final  String CSRF_TOKEN = "csrf-token";
    public static final  String X_CSRF_TOKEN = "x-csrf-token";

    private TokenUtil() {
        throw new IllegalStateException("Utility Token");
    }

    /**
     * 根据uid进行签名，并生成token
     *
     * @param uid 用户认证ID
     * @return token值
     */
    public static String generatorToken(String uid){
        assert(uid != null);
        try {
            long expired = System.currentTimeMillis() + TokenUtil.EXPIRED_TIME;
            String aesKey = KeyPool.getKey(expired);

            String token = CryptoUtil.encrypt(uid, aesKey) +
                    "." + expired +
                    "." + UUID.randomUUID();

            return  Base64.getUrlEncoder().encodeToString(token.getBytes()) +
                    "." +
                    CryptoUtil.signature(token, KeyPool.DEFAULT_KEY);
        }catch (Exception e){
            logger.error("exception: ", e);
            return null;
        }
    }

    /**
     * 检查token并获取token中的uid
     *
     * @param token  token值
     * @return uid
     */
    public static Optional<String> checkAndGetUid(String token){
        assert(token != null);

        String[] tokens = token.split("\\.");
        if (tokens == null || tokens.length != 2){
            logger.error("invalid token");
            return Optional.empty();
        }

        String signature = tokens[1];
        String payload = new String(Base64.getUrlDecoder().decode(tokens[0].getBytes()));
        String[] data = payload.split("\\.");
        if (data == null || data.length != 3){
            logger.error("invalid token");
            return Optional.empty();
        }

        try {
            String encryptedUid = data[0];
            long expired = Long.parseLong(data[1]);
            String aesKey = KeyPool.getKey(expired);

            String mac = CryptoUtil.signature(payload, KeyPool.DEFAULT_KEY);
            if (signature.equalsIgnoreCase(mac)){
                String uid = CryptoUtil.decrypt(encryptedUid, aesKey);
                long time = expired - System.currentTimeMillis();
                if (time <= 0) {
                    logger.error("token expired");
                    return Optional.ofNullable(null);
                }
                return Optional.of(uid);
            } else {
                logger.error("signature failed");
            }
        } catch (Exception e) {
            logger.error("exception: ", e);
        }

        return Optional.ofNullable(null);
    }

    /**
     * 根据单点登录token生成csrf token
     *
     */
    public static String generatorCSRFToken(String token){
        String currentTime = Long.toString(System.currentTimeMillis());
        String signature = CryptoUtil.signature(KeyPool.DEFAULT_KEY, token, currentTime);

        return currentTime + "." + signature;
    }

    /**
     * 检查csrf token是否合法
     *
     * @param token jwt单点token
     * @param csrfToken 需要检查的csrf token
     * @return true 合法, false 无效
     */
    public static boolean checkCSRFToken(String token, String csrfToken){
        if (token == null || csrfToken == null) {
            return false;
        }

        String[] tokens = csrfToken.split("\\.");
        if (tokens == null || tokens.length != 2){
            return false;
        }

        String currentTime = tokens[0];
        String signature = tokens[1];
        long time = Long.parseLong(currentTime);
        if (((System.currentTimeMillis() - time) / 1000) > 60 ){
            return false;
        }

        String encrypted = CryptoUtil.signature(KeyPool.DEFAULT_KEY, token, currentTime);
        return signature.equalsIgnoreCase(encrypted);
    }
}
