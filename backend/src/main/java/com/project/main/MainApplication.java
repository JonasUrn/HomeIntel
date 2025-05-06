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
	}
}    