import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;



import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.url.WebURL;


//public class MyCrawler extends WebCrawler {
//	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
//			 + "|png|mp3|mp3|zip|gz))$");
//			 /**
//			 * This method receives two parameters. The first parameter is the page
//			 * in which we have discovered this new url and the second parameter is
//			 * the new url. You should implement this function to specify whether
//			 * the given url should be crawled or not (based on your crawling logic).
//			 * In this example, we are instructing the crawler to ignore urls that
//			 * have css, js, git, ... extensions and to only accept urls that start
//			 * with "http://www.viterbi.usc.edu/". In this case, we didn't need the
//			 * referringPage parameter to make the decision.
//			 */
//			 @Override
//			
//			 public boolean shouldVisit(Page referringPage, WebURL url) {
//			 String href = url.getURL().toLowerCase();
//			 return !FILTERS.matcher(href).matches()
//			 && href.startsWith("http://www.viterbi.usc.edu/");
//			 }
//
//
//
//
///**
// * This function is called when a page is fetched and ready
// * to be processed by your program.
// */
// @Override
// public void visit(Page page) {
//	 String url = page.getWebURL().getURL();
//	 System.out.println("URL: " + url);
//	 if (page.getParseData() instanceof HtmlParseData) {
//		 HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
//		 String text = htmlParseData.getText();
//		 String html = htmlParseData.getHtml();
//		 Set<WebURL> links = htmlParseData.getOutgoingUrls();
//		 System.out.println("Text length: " + text.length());
//		 System.out.println("Html length: " + html.length());
//		 System.out.println("Number of outgoing links: " + links.size());
//	 }
// }
// 
// 
//}


public class MyCrawler extends WebCrawler {
	
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|swf|svg|mp3|mp4|zip|gz|txt))$");
	PrintWriter writer1, writer2, writer3, logWriter;
	int counter = 0;
	//int totalUniqueURLs = 0;
	int total_OKs = 0;
	int total_N_OKs = 0;
	//public static Set<WebURL> allUniqueURLS = new HashSet<WebURL>();
	public static Set<String> allUniqueURLS = new HashSet<String>();
	int totalDuplicatesFound = 0;
	int totalFetch = 0;
	int total_Successful_fetch = 0;
	int total_aborted_fetch = 0;
	int total_failed_fetch = 0;
	int total_200 = 0;
	int total_301 = 0;
	int total_302 = 0;
	int total_401 = 0;
	int total_403 = 0;
	int total_404 = 0;
	int total_504 = 0;
	int total_1K = 0;
	int total_10K = 0;
	int total_100K = 0;
	int total_1M = 0;
	int total_large = 0;
	int KB = 1024;
	
	
	public MyCrawler() throws FileNotFoundException, UnsupportedEncodingException {
		long r = System.nanoTime();
		writer1 = new PrintWriter(String.format("data/stats_%d_fetch.csv", r), "UTF-8");
		writer2 = new PrintWriter(String.format("data/stats_%d_visit.csv", r), "UTF-8");
		writer3 = new PrintWriter(String.format("data/stats_%d_urls.csv", r), "UTF-8");
		logWriter = new PrintWriter (String.format("data/stats_%d_log.txt", r), "UTF-8");
		//logWriter = new PrintWriter (new FileOutputStream(String.format("data/stats_%d_log.txt", r), false ) );
	}
	
	@Override
	public void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		//writer1.printf("%s,%d,%s\r\n", webUrl.getURL(), statusCode, statusDescription);
		writer1.printf("%s,%d\r\n", webUrl.getURL(), statusCode);
		writer1.flush();
		totalFetch ++;
		if (statusCode == 200){
			total_Successful_fetch++;
			total_200++;
		} else {
			if (statusCode == 303)
				total_aborted_fetch++;
			else total_failed_fetch++;
			
			if (statusCode == 301)
				total_301++;
			else if (statusCode == 302)
				total_302++;
			else if (statusCode == 401)
				total_401++;
			else if (statusCode == 403)
				total_403++;
			else if (statusCode == 404)
				total_404++;
			else if (statusCode == 504)
				total_504++;

			
		}
	}
	
	@Override
	public void visit(Page page) {
		String ref = page.getWebURL().getURL();
		logWriter.print("[SOHEIL] VISITING URL: "+ref+ "\n");
				
		ParseData pd = page.getParseData();
		if(pd instanceof HtmlParseData)
		{
			HtmlParseData hpd = (HtmlParseData) pd;
			
			String html = hpd.getHtml();
			String text = hpd.getText();
			Set<WebURL> urls = hpd.getOutgoingUrls();
			String regex = "\\s*\\bcharset=UTF-8\\b\\s*";
			//totalUniqueURLs += urls.size();
			//allUniqueURLS.addAll(urls);
			int urlSize = html.length() / KB;
			if (urlSize < 1) total_1K++;
			else if (1 <= urlSize && urlSize < 10) total_10K++;
			else if (10 <= urlSize && urlSize < 100) total_100K++;
			else if (100 <= urlSize && urlSize < 1000) total_1M++;
			else total_large++;
			
			writer2.printf("%s,%d,%d,%s\r\n", ref, html.length(), urls.size(), page.getContentType().replaceAll(regex, ""));
			writer2.flush();
			logWriter.printf("HTML Length: %d, TEXT Length: %d, Number of outgoing links: %d\n", html.length(), text.length(), urls.size());
			//System.out.printf("Number of total unique urls: %d\n\n", totalUniqueURLs);
			//System.out.printf("Number of total items unique set: %d\n\n", allUniqueURLS.size());
			
			logWriter.printf("\n\n************************************************************\n\n");
			logWriter.printf("\t\tStats\n");
			logWriter.printf("\nFile Sizes:\n===========\n< 1KB: %d\n1KB ~ <10KB: %d\n10KB ~ <100KB: %d\n100KB ~ <1MB: %d\n>= 1MB: %d\n", 
					total_1K, total_10K, total_100K, total_1M, total_large);
			
			logWriter.printf("\nStatus Codes:\n===========\n200 OK: %d\n301 Moved Permanently: %d\n"
					+ "401 Unauthorized: %d\n403 Forbidden: %d\n404 Not Found: %d\n", 
					total_200, total_301, total_401, total_403, total_404);	
			logWriter.printf("\n===========\n302 : %d\n504: %d\n", total_302, total_504);
			
			logWriter.printf("\nFetch Statistics:\n===========\n"
					+ "# fetches attempted: %d\n# fetches succeeded: %d\n# fetches aborted: %d\n"
					+ "# fetches failed: %d\n", totalFetch, total_Successful_fetch, total_aborted_fetch, 
					total_failed_fetch);
			logWriter.printf("Number of OK and N_OK items : %d----%d\n\n", total_OKs, total_N_OKs);
			logWriter.flush();
		}
		else
		{
			System.out.println();
		}
	}
	
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String ref = url.getURL().toLowerCase();
		//boolean b = ref.startsWith("http://www.c-span.org/") || ref.startsWith("https://www.c-span.org/") 
		//		|| ref.startsWith("https://images.c-span.org/");
		boolean b = ref.startsWith("http://www.c-span.org/") || 
				ref.startsWith("https://www.c-span.org/"); 
		
		if (b) total_OKs++;
		else total_N_OKs++;
		
		writer3.printf("\"%s\",%s\r\n", ref, b ? "OK" : "N_OK");
		writer3.flush();
				
		
		if(FILTERS.matcher(ref).matches())
			return false;
		
		if (b){
			if ( allUniqueURLS.contains(ref)){
				totalDuplicatesFound++;
				logWriter.printf("This url %s is already visited. Skipping. TotalDuplicatesFound: %d\n\n", 
						ref, totalDuplicatesFound);
				
				return false;
			}
			else allUniqueURLS.add(ref);			
		}
		
		
		return b;
	}
}
