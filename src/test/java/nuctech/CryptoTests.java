package nuctech;

import com.nuctech.platform.auth.User;
import com.nuctech.platform.util.CryptoUtil;
import com.nuctech.platform.util.KeyPool;
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
    public void testKeyPool(){
        String key = KeyPool.getKey("wangzh-ok");
        logger.info("key is " + key);
        /*for (int i = 0; i < 32; i++){
            System.out.println("\"" + UUID.randomUUID().toString() + "\",");
        }*/
        Assert.assertTrue(key != null);
    }

    @Test()
    public void testAES() throws Exception{
        String key = KeyPool.getKey("wangzh-ok");
        String encrypted = CryptoUtil.encrypt("nuctech.com", key);
        logger.info(encrypted);
        Assert.assertTrue(encrypted != null);

        User u1 = new User();
        User u2 = new User();
        u2.s();
        u1.p();
        u2.p();

        //long a = 1L * b * 2;
        logger.info("long = {}", t());
    }

    public long t(){
        int b = Integer.MAX_VALUE;
        return b * 2;
    }
}
