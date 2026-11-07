package br.com.gs.springboot.gs_springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GsSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(GsSpringbootApplication.class, args);
	}

}
