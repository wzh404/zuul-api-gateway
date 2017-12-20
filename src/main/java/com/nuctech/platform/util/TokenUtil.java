package com.nuctech.platform.util;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.UUID;

/**
 * Created by wangzunhui on 2017/7/17.
 */
public class TokenUtil {
    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);
    private static final long EXPIRED_TIME = 1000 * 60 * 30L;

    public static final String TOKEN = "token";
    public static final String CSRF_TOKEN = "csrf-token";
    public static final String X_CSRF_TOKEN = "x-csrf-token";

    private TokenUtil() {
        throw new IllegalStateException("Utility Token");
    }

    /**
     * 根据uid进行签名，并生成token
     *
     * uid:
     *     AES(uid)
     *
     * data:
     *     uid.time.uuid
     *     -------------
     *
     * sign:
     *     signature(data)
     *
     * token:
     *     BASE64(data).sign
     *
     * @param uid 用户认证ID
     * @return token值
     */
    public static String generatorToken(String uid) {
        assert (uid != null);

        long expired = System.currentTimeMillis() + TokenUtil.EXPIRED_TIME;
        String aesKey = KeyPool.getKey(expired);

        String token = CryptoUtil.encrypt(uid, aesKey) +
                "." + expired +
                "." + UUID.randomUUID();

        return Base64.getUrlEncoder().encodeToString(token.getBytes()) +
                "." +
                CryptoUtil.signature(token, KeyPool.DEFAULT_KEY);
    }

    /**
     * 检查token并获取token中的uid,Token重置条件大于1分钟
     *
     * @param token token值
     * @return result
     *      -1 : expired or invalid
     *       1 : reset token
     *       0 : ok
     */
    public static TokenResult checkAndGetUid(String token) {
        assert (token != null);

        String[] tokens = token.split("\\.");
        if (tokens == null || tokens.length != 2) {
            logger.error("invalid token");
            return new TokenResult(-1, null);
        }
        String signature = tokens[1];
        String payload = new String(Base64.getUrlDecoder().decode(tokens[0].getBytes()));
        String[] data = payload.split("\\.");
        if (data == null || data.length != 3) {
            logger.error("invalid token data");
            return new TokenResult(-1, null);
        }

        // check signature
        String mac = CryptoUtil.signature(payload, KeyPool.DEFAULT_KEY);
        if (!signature.equalsIgnoreCase(mac)) {
            logger.error("signature failed");
            return new TokenResult(-1, null);
        }

        // check time
        long expired = 0L;
        try {
            expired = Long.parseLong(data[1]);
        } catch (NumberFormatException e) {
            logger.error("exception: ", e);
            return new TokenResult(-1, null);
        }
        String aesKey = KeyPool.getKey(expired);
        String encryptedUid = data[0];
        String uid = CryptoUtil.decrypt(encryptedUid, aesKey);

        long expiredTime = expired - System.currentTimeMillis();
        if (expiredTime <= 0) {
            logger.error("token expired");
            return new TokenResult(-1, null);
        }

        long diffTime = (EXPIRED_TIME - expiredTime) / 1000L;
        logger.info("diff " + diffTime);
        if (diffTime > 1 * 60L) {
            logger.warn("reset token");
            return new TokenResult(1, uid);
        }

        return  new TokenResult(0, uid);
    }

    @Data
    public static class TokenResult {
        private int code;
        private String value;

        public TokenResult(int code, String value){
            this.code = code;
            this.value = value;
        }
    }

    /**
     * 根据单点登录token生成csrf token
     *
     * uid.current_time
     * ----------------
     *     signature
     */
    public static String generatorCSRFToken(String uid) {
        String currentTime = Long.toString(System.currentTimeMillis());
        String signature = CryptoUtil.signature(KeyPool.DEFAULT_KEY, uid, currentTime);

        return currentTime + "." + signature;
    }

    /**
     * 检查csrf token是否合法
     *
     * @param uid     user id
     * @param csrfToken 需要检查的csrf token
     * @return true 合法, false 无效
     */
    public static boolean checkCSRFToken(String uid, String csrfToken) {
        if (uid == null || csrfToken == null) {
            return false;
        }

        String[] tokens = csrfToken.split("\\.");
        if (tokens == null || tokens.length != 2) {
            return false;
        }

        String currentTime = tokens[0];
        String signature = tokens[1];

        String encrypted = CryptoUtil.signature(KeyPool.DEFAULT_KEY, uid, currentTime);
        return signature.equalsIgnoreCase(encrypted);
    }
}
