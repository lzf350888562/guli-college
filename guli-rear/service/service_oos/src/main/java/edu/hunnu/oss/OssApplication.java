package edu.hunnu.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * oss上传
 * @author hunnu/lzf
 * @Date 2021/6/4
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//不需要使用数据库 排除自动配置
@ComponentScan(basePackages = {"edu.hunnu"})
@EnableDiscoveryClient
public class OssApplication {
	public static void main(String[] args) {
		SpringApplication.run(OssApplication.class, args);
	}
}
