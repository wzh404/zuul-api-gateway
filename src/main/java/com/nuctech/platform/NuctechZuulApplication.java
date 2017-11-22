package com.nuctech.platform;

import com.nuctech.platform.util.JSnowFlake;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableFeignClients
@EnableZuulProxy
@SpringBootApplication
public class NuctechZuulApplication {
	public final static JSnowFlake snowFlake = new JSnowFlake(1);

	public static void main(String[] args) {
		SpringApplication.run(NuctechZuulApplication.class, args);
	}
}
