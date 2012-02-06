package gui;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import page.Page;
import crawler.Crawler;
import crawler.CrawlerSingle;
import crawler.CrawlerTuned;

public class CrawlerMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] keys = new String[] {"coconuts", "emeralds", "cliff", "rock"};
		
		Crawler c = new CrawlerTuned();
		try {
			
	    c.crawl(new Page(new URI("http://en.wikipedia.org/")), keys, 0);
	    
    } catch (URISyntaxException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
		
		while(true){
			int pages_crawled = c.getPagesCrawled();
			int words_found = c.getWordCount();
			int urls_found = c.getUrlsFound();
			Map<String, Integer> keyword_counts = c.getKeywordCounts();
			long time_parsed = c.getParseTime();
			long time_elapsed= c.getTimeElapsed();
			
			StringBuilder output = new StringBuilder();
			output.append("\n\n\nPages Retrieved: ").append(pages_crawled);
			output.append("\nAverage words per page: ").append((double) words_found /(double) pages_crawled);
			output.append("\nAverage URLs per page: ").append((double) urls_found / (double) pages_crawled );
		  output.append("\nKeyword\t\tAvg. hits per page\tTotal hits");
		  
		  for(String k : keys){
		  	output.append("\n  ").append(k).append("\t\t").append(keyword_counts.get(k) / (double) pages_crawled).append("\t").append(keyword_counts.get(k));
		  }
      output.append("\nAverage parse time per page:").append((double)time_parsed / 1000000000.0 / (double) pages_crawled);
      output.append("\nTotal running time:").append((double)time_elapsed / 1000000000.0);
      output.append("\nPages per second: ").append((double)pages_crawled / ((double)time_elapsed / 1000000000.0));
      //output.append("\nRetrievers: ").append(c.getNumRet()).append("\tParsers: ").append(c.getNumPar()).append("\tAnalyzers: ").append(c.getNumAna());
      System.out.print(output);
      if(c.isDone() && time_elapsed > 10000000000l){
      	c.stop();
      	break;
      }
      try {
	      Thread.sleep(3000);
      } catch (InterruptedException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
      }
		}
	}

}
