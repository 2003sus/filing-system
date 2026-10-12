package com.example.filing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class FilingApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void genPassword() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String hash = encoder.encode("123456");
		System.out.println("HASH: " + hash);
		System.out.println("MATCH: " + encoder.matches("123456", hash));
	}
}