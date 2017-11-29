package nuctech;

import com.nuctech.platform.auth.User;
import com.nuctech.platform.util.CryptoUtil;
import com.nuctech.platform.util.KeyPool;
import com.nuctech.platform.util.TokenUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Created by wangzunhui on 2017/8/12.
 */
public class CryptoTests {
    private final Logger logger = LoggerFactory.getLogger(CryptoTests.class);

    @Test
    public void testKeyPool() {
        String key = KeyPool.getKey("wangzh-ok");
        logger.info("key is " + key);
        /*for (int i = 0; i < 32; i++){
            System.out.println("\"" + UUID.randomUUID().toString() + "\",");
        }*/
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
        String token = TokenUtil.generatorToken("wangzh");
        logger.info("token is [" + token +"]");
        Thread.sleep(13000L);
        TokenUtil.TokenResult r = TokenUtil.checkAndGetUid(token);

        Assert.assertTrue(r.getCode() >= 0);
        Assert.assertTrue(r.getCode() == 0);
    }
}
