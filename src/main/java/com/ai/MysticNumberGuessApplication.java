package com.ai; // 启动类在根包下

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // 默认扫描当前包及所有子包
public class
MysticNumberGuessApplication {
	public static void main(String[] args) {
		SpringApplication.run(MysticNumberGuessApplication.class, args);
	}
}