package com.project.main;

import java.io.IOException;
import java.util.Dictionary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.Scraper.Scraper;
import com.Scraper.ZillowAPI;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

@SpringBootApplication
public class MainApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(MainApplication.class, args);
		// https://www.zillow.com/homedetails/156-Old-Church-Rd-Greenwich-CT-06830/240521150_zpid/
		// https://www.zillow.com/homedetails/100-Windham-Rd-Willimantic-CT-06226/59010343_zpid/
		// 3 kambariu, VIlnius, Gedimino pr. 3, 3 aukstas 10 aukstu name, A++ ekonomine
		// klase, pastatymas 2023, centrinis sildymas, kaina 500000
    	// https://www.aruodas.lt/butai-vilniuje-justiniskese-kopenhagos-g-talino-modernus-komfortiskas-naujas-1-3387648/?pos=3&search_pos=16
	}
}    