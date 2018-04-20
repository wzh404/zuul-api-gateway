package com.nuctech.platform.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * Created by @author wangzunhui on 2017/7/17.
 */
public class TokenUtil {
    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);

    public static final String TOKEN = "_user_token";
    public static final String S_CSRF_TOKEN = "S-CSRF-TOKEN";
    public static final String X_CSRF_TOKEN = "X-CSRF-TOKEN";

    public static final int X_CSRF_TOKEN_SIZE = 32;

    private TokenUtil() {
        throw new IllegalStateException("Utility Token");
    }

    /**
     * generate x-csrf-token.
     *
     * @return
     */
    public static String generateXCSRFToken() {
        String rand = fixedRandom(10);
        long time = System.currentTimeMillis();
        String key = KeyPool.getKey(time);

        String signature = CryptoUtil.signature(key, rand, Long.toString(time));
        StringBuilder token = new StringBuilder(rand);
        token.append(time);
        token.append(signature.substring(signature.length() - 9));

        return token.toString();
    }

    /**
     * check if x-csrf-token is valid.
     *
     * @param xCsrfToken
     * @return
     */
    public static boolean checkXCSRFToken(String xCsrfToken){
        if (xCsrfToken == null || xCsrfToken.length() != X_CSRF_TOKEN_SIZE){
            return false;
        }

        long time = getTokenTime(xCsrfToken);
        String key = KeyPool.getKey(time);
        String signed = xCsrfToken.substring(xCsrfToken.length() - 9);
        String data = xCsrfToken.substring(0, 23);
        String newSigned = CryptoUtil.signature(key, data);

        return newSigned.substring(newSigned.length() - 9).equalsIgnoreCase(signed);
    }

    /**
     * generate s-csrf-token by x-csrf-token.
     *
     * @param xCsrfToken
     * @return
     */
    public static String generateSCSRFToken(String xCsrfToken){
        if (xCsrfToken == null || xCsrfToken.length() != X_CSRF_TOKEN_SIZE){
            return null;
        }

        long time = getTokenTime(xCsrfToken);
        String key = KeyPool.getKey(time);

        return CryptoUtil.signature(key, xCsrfToken);
    }

    /**
     * check if s-csrf-token is valid.
     *
     * @param xCsrfToken
     * @param sCsrfToken
     * @return
     */
    public static boolean checkSCSRFToken(String xCsrfToken, String sCsrfToken){
        if (xCsrfToken == null || xCsrfToken.length() != X_CSRF_TOKEN_SIZE){
            return false;
        }

        if (!checkXCSRFToken(xCsrfToken)){
            return false;
        }

        long time = getTokenTime(xCsrfToken);
        String key = KeyPool.getKey(time);
        String signed = CryptoUtil.signature(key, xCsrfToken);

        return signed.equalsIgnoreCase(sCsrfToken);
    }

    /**
     * get timestampe from x-csrf-token string.
     *
     * @param xCsrfToken
     * @return
     */
    private static long getTokenTime(String xCsrfToken){
        return Long.valueOf(xCsrfToken.substring(10, 23));
    }

    /**
     * create jwt token by json string of user.
     *
     * @param json
     * @return
     */
    public static Optional<String> createJwtToken(String json){
        if (json == null){
            return Optional.empty();
        }

        String payload = new String(Base64.getEncoder().encode(json.getBytes()));
        StringBuilder token = new StringBuilder(payload);
        String signed = CryptoUtil.signature(payload, KeyPool.DEFAULT_KEY);
        token.append(".");
        token.append(signed);

        return Optional.of(token.toString());
    }

    /**
     * decode jwt payload
     *
     * @param token
     * @return jwt payload(json string of user)
     */
    public static Optional<String> decodeJwt(String token){
        if (token == null) {
            logger.error("Jwt token is null");
            return Optional.empty();
        }

        String[] tokens = token.split("\\.");
        if (tokens == null || tokens.length != 2) {
            logger.error("Invalid jwt token");
            return Optional.empty();
        }

        String signature = tokens[1];
        String signed = CryptoUtil.signature(tokens[0], KeyPool.DEFAULT_KEY);
        if (!signature.equalsIgnoreCase(signed)) {
            logger.error("Invalid signature data");
            return Optional.empty();
        }

        String payload = new String(Base64.getUrlDecoder().decode(tokens[0].getBytes()));
        return Optional.of(payload);
    }

    /*
    public static String createSessionId(String uid){
        ByteBuffer bb = ByteBuffer.wrap(new byte[32]);

        byte[] hash = Hashing.sha256()
                .hashString(uid, Charset.forName("utf-8"))
                .asBytes();
        byte[] uuid = asBytes(UUID.randomUUID());

        bb.put(hash, 0, 16);
        bb.put(uuid, 0, 16);

        return byteArrayToHexStr(bb.array());
    }
*/

    /**
     * generate user login session id.
     *
     * @return
     */
    public static String createSessionId(){
        return byteArrayToHexStr(asBytes(UUID.randomUUID()));
    }

    /**
     * convert uuid to byte array.
     *
     * @param uuid
     * @return
     */
    public static byte[] asBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    /**
     * convert byte array to hex string.
     *
     * @param byteArray
     * @return
     */
    public static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null){
            return null;
        }

        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * get random string by length.
     *
     * @param len
     * @return
     */
    public static String fixedRandom(int len){
        double r = (1+ new Random().nextDouble()) * Math.pow(10, len);
        return String.valueOf(Math.round(r)).substring(0,len);
    }
}
