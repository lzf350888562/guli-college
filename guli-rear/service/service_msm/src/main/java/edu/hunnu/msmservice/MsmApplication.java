package edu.hunnu.msmservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author hunnu/lzf
 * @Date 2021/6/10
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan({"edu.hunnu"})
//@MapperScan("edu.hunnu.msmservice.mapper")
public class MsmApplication {
	public static void main(String[] args){
		SpringApplication.run(MsmApplication.class,args);
	}
}
