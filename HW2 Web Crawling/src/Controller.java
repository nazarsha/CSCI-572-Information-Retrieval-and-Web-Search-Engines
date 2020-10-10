import java.net.URL;
import java.net.URLConnection;


import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
//		String crawlStorageFolder = "E:/programs/eclipse-jee-neon-3-win32-x86_64/workspace/CSCI572/HW2/data/crawl";
//		 int numberOfCrawlers = 1;
//		 CrawlConfig config = new CrawlConfig();
//		 config.setCrawlStorageFolder(crawlStorageFolder);
//		 int maxDepthOfCrawling = 16;
//		 //int maxPagesToFetch = 20000;
//		 int maxPagesToFetch = 1;
//		 int politenessDelay = 2500;
//		 String userAgentString = "Sol";
//		 
//
//		 config.setMaxDepthOfCrawling(maxDepthOfCrawling);
//		 config.setMaxPagesToFetch(maxPagesToFetch);
//		 config.setPolitenessDelay(politenessDelay);
//		 config.setUserAgentString(userAgentString);
//		 
//		 
//		 
//		 /*
//		 * Instantiate the controller for this crawl.
//		 */
//		 PageFetcher pageFetcher = new PageFetcher(config);
//		 RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
//		 RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
//		 CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
//		 /*
//		 * For each crawl, you need to add some seed urls. These are the first
//		 * URLs that are fetched and then the crawler starts following links
//		 * which are found in these pages
//		 */
//		 controller.addSeed("http://www.viterbi.usc.edu/");
//		
//		 /*
//		 * Start the crawl. This is a blocking operation, meaning that your code
//		 * will reach the line after this only when crawling is finished.
//		 */
//		 controller.start(MyCrawler.class, numberOfCrawlers);
		 
		 
		 
			int threads = 1;
			int maxDepthOfCrawling = 16;
			String folder = "E:/programs/eclipse-jee-neon-3-win32-x86_64/workspace/CSCI572/HW2/data/crawl";
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
