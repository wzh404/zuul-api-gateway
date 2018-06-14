package nuctech;

import com.google.common.hash.Hashing;

import com.nuctech.platform.util.CryptoUtil;
import com.nuctech.platform.util.KeyPool;
import com.nuctech.platform.util.TokenUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Created by wangzunhui on 2017/8/12.
 */
public class CryptoTests {
    private final Logger logger = LoggerFactory.getLogger(CryptoTests.class);

    @org.junit.Test
    public void testKeyPool() {

        String key = KeyPool.getKey("wangzh-ok");
        logger.info("key is " + key);
        /*for (int i = 0; i < 32; i++){
            System.out.println("\"" + UUID.randomUUID().toString() + "\",");
        }*/
        String as = Hashing.sha256().newHasher().putString("wangzh0000001", Charset.forName("utf-8")).hash().toString();
        logger.info(as);

        Assert.assertTrue(key != null);
    }

    @Test
    public void testAES() throws Exception {
        String key = KeyPool.getKey("wangzh-ok");
        String encrypted = CryptoUtil.encrypt("nuctech.com", key);
        logger.info(encrypted);
        Assert.assertTrue(encrypted != null);
    }

    @Test
    public void testToken() throws Exception {
        //String token = TokenUtil.generatorToken("wangzh");
        //int len = 10;
        //double r = (1+ new Random().nextDouble()) * Math.pow(10, len);

        //String token = String.valueOf(Math.round(r)).substring(0,len);

        //AbstractQueuedSynchronizer
        String token = TokenUtil.generateXCSRFToken();
        logger.info("token is {}", token);
        //Thread.sleep(13000L);
        //TokenUtil.TokenResult r = TokenUtil.checkAndGetUid(token);

        //Assert.assertTrue(r.getCode() >= 0);
        Assert.assertTrue(token.length() == 32);
        Assert.assertTrue(TokenUtil.checkXCSRFToken(token));

        String scsrf = TokenUtil.generateSCSRFToken(token);
        Assert.assertTrue(TokenUtil.checkSCSRFToken(token, scsrf));
    }
}
