package com.hcx.hcxprovider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@SpringBootApplication
@RestController
public class HcxproviderApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(HcxproviderApplication.class, args);
	}
	@GetMapping("/")
	public ResponseEntity<String> checkHcxProvider() {
		return  ResponseEntity.ok("Test provider application");
	}
}
