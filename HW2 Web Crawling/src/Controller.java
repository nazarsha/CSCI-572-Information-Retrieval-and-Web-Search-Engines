import java.net.URL;
import java.net.URLConnection;


import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {

	public static void main(String[] args) throws Exception {	 
		 
			int threads = 1;
			int maxDepthOfCrawling = 16;
			String folder = "~/CSCI572/HW2/data/crawl";
			int maxPagesToFetch = 20000;
			CrawlConfig conf = new CrawlConfig();
			int politenessDelay = 2500;
			String userAgentString = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
			
			conf.setCrawlStorageFolder(folder);
			conf.setMaxPagesToFetch(maxPagesToFetch);
			conf.setPolitenessDelay(politenessDelay);
			conf.setUserAgentString(userAgentString);
			conf.setMaxDepthOfCrawling(maxDepthOfCrawling);
			conf.setIncludeBinaryContentInCrawling(true);
			
			
			PageFetcher pf = new PageFetcher(conf);
			RobotstxtConfig robot_conf = new RobotstxtConfig();
			robot_conf.setEnabled(false);
			
			RobotstxtServer robot_server = new RobotstxtServer(robot_conf, pf);
			CrawlController cc = new CrawlController(conf, pf, robot_server);
			
			cc.addSeed("https://www.c-span.org");
			cc.start(MyCrawler.class, threads);
					 
			System.out.println("\n\n*****************************************************************\n\n");
			System.out.println("\n\nCrawling Finished.\n\n");
		 
	}

}
