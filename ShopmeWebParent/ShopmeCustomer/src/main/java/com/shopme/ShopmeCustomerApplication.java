package com.shopme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = {"com.shopme.common","com.shopme"})
@EnableCaching
public class ShopmeCustomerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopmeCustomerApplication.class, args);
	}

}
