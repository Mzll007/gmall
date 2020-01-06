package com.mzll.gmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = "com.mzll.gmall.mapper")
@SpringBootApplication
public class GmallUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallUserServiceApplication.class, args);
	}

}
