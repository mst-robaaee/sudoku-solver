package net.mostow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@Configuration
@ComponentScan(basePackages = "net.mostow")
public class SudokuApplication {
	public static void main(String[] args) {
		SpringApplication.run(SudokuApplication.class, args);
	}
}
