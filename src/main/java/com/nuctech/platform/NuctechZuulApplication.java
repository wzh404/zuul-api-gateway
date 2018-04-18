package com.nuctech.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableFeignClients
@EnableZuulProxy
@SpringBootApplication
public class NuctechZuulApplication {
	public static void main(String[] args) {
		SpringApplication.run(NuctechZuulApplication.class, args);
	}
}
