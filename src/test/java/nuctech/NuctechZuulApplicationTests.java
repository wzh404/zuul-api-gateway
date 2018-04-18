package nuctech;

import com.nuctech.platform.NuctechZuulApplication;
import com.nuctech.platform.util.TokenUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;
import javax.servlet.http.Cookie;

import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = NuctechZuulApplication.class)
public class NuctechZuulApplicationTests {
    private final Logger logger = LoggerFactory.getLogger(NuctechZuulApplicationTests.class);

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RedisTemplate redisTemplate;

    //@Autowired
    //protected User user;

    //@Mock
    //private UrpmService userService;

    private MockMvc mockMvc;

    private static String token;

    @Before
    public void setupMockMvc() throws Exception {
        Collection<Filter> filterCollection = context.getBeansOfType(Filter.class).values();
        Filter[] filters = filterCollection.toArray(new Filter[filterCollection.size()]);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(filters).build();

        MockitoAnnotations.initMocks(this);
        //User u = new User();
        //u.addRole("admin", new String[]{"a", "b"});
        //u.addRole("user", new String[]{"u1", "u2"});
        //u.setResultCode("OK");
        //Mockito.when(userService.login("wzh", "123456")).thenReturn(u);
        //Mockito.when(userService.getUser("wzh")).thenReturn(u);

        //UserRealm ur = (UserRealm)context.getBean("userRealm");
       // ReflectionTestUtils.setField(ur, "userService", userService);
    }

//    @Test
//	public void testExtensionLoader() {
//        logger.info("----spring boot unit test-------");
//        CacheService cacheService = ExtensionLoader.getExtensionLoader(CacheService.class).getExtension("local");
//        Assert.assertTrue(cacheService instanceof LocalCacheService);
//        Assert.assertFalse(cacheService instanceof RedisCacheService);
//        cacheService.run();
//    }

    @Test
    public void testRedis(){
        ValueOperations vo = redisTemplate.opsForValue();
        String key = "user:authority:1";

        vo.set(key, "url1,url2");
        String a = (String)vo.get(key);
        logger.info(a);
        Assert.assertTrue(true);
    }
/*
    @Test
    public void testSloginController() throws Exception{
        token = mockMvc.perform(get("/slogin")
                .param("name", "wzh")
                .param("pwd", "123456")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.cookie().exists("token"))
                .andExpect(MockMvcResultMatchers.cookie().exists(TokenUtil.CSRF_TOKEN))
                .andReturn()
                .getResponse().getCookie("token")
                .getValue();
        logger.info("token - " + token);
    }

    @Test
    public void testHiController()  throws Exception{
        logger.info("-hi-token - " + token);

        mockMvc.perform(get("/i18n")
                .param("v1", "wzh")
                .cookie(new Cookie("token",token))
                .header("X-Real-IP", "192.168.10.209")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testHomesController()  throws Exception{
        logger.info("-home - " + token);

        mockMvc.perform(get("/test/home")
                .cookie(new Cookie("token",token))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andDo(MockMvcResultHandlers.print());
    }*/

    @Test
    public void testToken() throws Exception {
        String token = TokenUtil.generateXCSRFToken();
        Assert.assertTrue(token.length() == 32);
        Assert.assertTrue(TokenUtil.checkXCSRFToken(token));

        String scsrf = TokenUtil.generateSCSRFToken(token);
        Assert.assertTrue(TokenUtil.checkSCSRFToken(token, scsrf));
    }
}
