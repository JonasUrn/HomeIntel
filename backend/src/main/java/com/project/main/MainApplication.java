package com.project.main;

import java.util.Dictionary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.Scraper.Scraper;

@SpringBootApplication
public class MainApplication {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(MainApplication.class, args);

		// String[] dataLinks = new String[] {
		// 	"https://www.aruodas.lt/butai-vilniuje-santariskese-dangerucio-g-jaukus-erdvus-uzdaras-ir-zalias-naujuju-1-3521884/",
		// 	"https://www.aruodas.lt/butai-kaune-sanciuose-a-juozapaviciaus-pr-butas-yra-isskirtiniame-www-1-3389919/?search_pos=3",
		// 	"https://www.aruodas.lt/butai-kaune-vilijampoleje-raudondvario-pl-parduodami-ivairiu-plotu-butai-butai-irengti-1-3500483/?search_pos=4#gallery-thumb8",
		// 	"https://www.aruodas.lt/butai-kaune-zaliakalnyje-algirdo-g-brokeriams-aciu-uz-bendradarbiavima-taciau-1-3481297/?search_pos=11",
		// 	"https://www.aruodas.lt/butai-kaune-zaliakalnyje-sirvintu-g-prestiziniame-kauno-rajone-zaliakalnyje-1-3513953/?search_pos=15",
		// 	"https://www.aruodas.lt/butai-kaune-zaliakalnyje-vaizganto-g-kauno-centre-parku-apsupty-yra-ypatigas-1-3387304/?search_pos=20",
		// 	"https://www.aruodas.lt/butai-kaune-zaliakalnyje-savanoriu-pr-pardavineja-savininkas-brokeriu-paslaugos-1-3529185/?search_pos=26",
		// 	"https://www.aruodas.lt/butai-kaune-zaliakalnyje-partizanu-g-parduodamas-vieno-kambario-butas-partizanu-g-1-3526556/?search_pos=42",
		// 	"https://www.aruodas.lt/butai-kaune-zaliakalnyje-ievu-tak-isskirtine-galimybe-nusipirkti-a-energines-1-3427994/?search_pos=47",
		// 	"https://www.aruodas.lt/butai-kaune-zaliakalnyje-savanoriu-pr-naujai-suremontuotas-ir-dar-negyventas-butas-1-3530495/?search_pos=49"
		// };

		String[] dataLinks = new String[] {
			"https://www.zillow.com/homedetails/545-Indian-Field-Rd-Greenwich-CT-06830/57311584_zpid/",
			"https://www.zillow.com/homedetails/72-Leverett-Ave-Staten-Island-NY-10308/32342141_zpid/",
			"https://www.zillow.com/homedetails/36-Sunnyside-Ter-Staten-Island-NY-10301/32286725_zpid/",
			"https://www.zillow.com/homedetails/410-Crystal-Ave-Staten-Island-NY-10314/32284039_zpid/",
			"https://www.zillow.com/homedetails/177-Benedict-Rd-Staten-Island-NY-10304/32294383_zpid/",
			"https://www.zillow.com/homedetails/457-Field-Point-Rd-Greenwich-CT-06830/2063444243_zpid/",
			"https://www.zillow.com/homedetails/72-Leverett-Ave-Staten-Island-NY-10308/32342141_zpid/",
			"https://www.zillow.com/homedetails/18-Jules-Dr-Staten-Island-NY-10314/32305422_zpid/"
		};

		for(int i = 0; i < dataLinks.length; i++){
			Scraper scraper = new Scraper(dataLinks[i]);
			scraper.GetResults();
			Thread.sleep(2000);
		}
	}
}