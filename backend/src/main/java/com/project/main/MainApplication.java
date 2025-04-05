package com.project.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(MainApplication.class, args);
		String[] dataArr = new String[] {
			"https://www.aruodas.lt/butai-vilniuje-santariskese-dangerucio-g-jaukus-erdvus-uzdaras-ir-zalias-naujuju-1-3521884/",
			"https://en.aruodas.lt/butai-kaune-romainiuose-girios-g-parduodamas-puikus-kambario-butas-i-ryte-1-3532913/",
			"https://en.aruodas.lt/butai-kaune-zaliakalnyje-ausros-g-parduodamas-kamabriu-butas-prestiziniame-1-3527669/",
			"https://en.aruodas.lt/butai-kaune-zemutiniuose-kaniukuose-rimties-skg-parduodamas-kambariu-butas-netoli-lampedziu-1-3530413/",
			// "https://www.zillow.com/homedetails/294-Riverside-Ave-Riverside-CT-06878/177294093_zpid/",
			// "https://www.zillow.com/homedetails/545-Indian-Field-Rd-Greenwich-CT-06830/57311584_zpid/"
		};
		for(int i = 0; i < dataArr.length; i++){
			Scraper scraper = new Scraper(dataArr[i]);
			scraper.GetResults();
		}
	}
}