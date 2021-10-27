package edu.hunnu.eduservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author hunnu/lzf
 * @Date 2021/5/31
 */
@SpringBootApplication
@ComponentScan(basePackages = {"edu.hunnu"})  //扫描common的service_base中的内容
@EnableDiscoveryClient
@EnableFeignClients  //feign服务调用
public class EduApplication {

	public static void main(String[] args) {
		SpringApplication.run(EduApplication.class, args);
	}
}
