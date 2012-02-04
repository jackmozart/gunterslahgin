package crawler;

import java.util.Map;

import page.Page;

public interface Crawler {
	
	void crawl(Page the_seed_page, String[] the_keywords, int the_max_pages);
	
	/**
	 * @return Total number of keyword matches found for each keyword
	 */
	Map<String, Integer> getKeywordCounts();
	
	/**
	 * 
	 * @return Total number of pages crawled
	 */
	int getPagesCrawled();
	
	/**
	 * 
	 * @return Total number of pages parsed.
	 */
	int getPagesParsed();
	
	
	int getWordCount();
	
	/**
	 * 
	 * @return Total number of urls found.
	 */
	int getUrlsFound();
	
	/**
	 * @return The average time it takes to parse a page.  
	 */
	long getParseTime();
	
	/**
	 * 
	 * @return Total run time of the program.  
	 */
	long getTimeElapsed();
	
	/**
	 * Stops the crawler by activating the stop bit.  
	 */
	void stop();
	/**
	 * Whether or not the crawler is done crawling.
	 * @return  Whether or not the crawler is done crawling.
	 */
	boolean isDone();
}
