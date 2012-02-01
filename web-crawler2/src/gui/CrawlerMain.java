package gui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import page.Page;
import crawler.Crawler;
import crawler.CrawlerMulti;
import crawler.CrawlerSingle;

public class CrawlerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] keys = new String[] {"Hitler", "puppy", "the", "and"};
		
		Crawler c = new CrawlerMulti();
		try {
			
	    c.crawl(new Page(new URI("http://gamespot.com")), keys, 0);
	    
    } catch (URISyntaxException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
		
		while(true){
			int pages_crawled = 0;
			int words_found = 0;
			int urls_found = 0;
			
			
			pages_crawled = c.getPagesCrawled();
			words_found = c.getWordCount();
			urls_found = c.getUrlsFound();
			Map<String, Integer> keyword_counts = c.getKeywordCounts();
			
			
			StringBuilder output = new StringBuilder();
			output.append("\n\n\nPages Retrieved: ").append(pages_crawled);
			output.append("\nAverage words per page: ").append((double) words_found /(double) pages_crawled);
			output.append("\nAverage URLs per page: ").append((double) urls_found / (double) pages_crawled );
		  output.append("\nKeyword\t\tAvg. hits per page\tTotal hits");
		  
		  for(String k : keys){
		  	output.append("\n  ").append(k).append("\t\t").append(keyword_counts.get(k) / (double) pages_crawled).append("\t").append(keyword_counts.get(k));
		  }
      output.append("\nAverage parse time per page:").append((double)c.getParseTime() / (double)1000000000 / (double) pages_crawled);
      output.append("\nTotal running time:").append((double)c.getTimeElapsed() / (double)1000000000);
      
      System.out.print(output);
      
      try {
	      Thread.sleep(3000);
      } catch (InterruptedException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
      }
		}
	}

}
