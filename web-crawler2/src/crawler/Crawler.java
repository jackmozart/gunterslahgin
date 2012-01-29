package crawler;

import page.Page;
import statistics.Statistics;

public interface Crawler {
	/**
	 * Starts the crawling process.
	 */
	public void crawl(Page seed_page);
	
	/**
	 * Gets the statistics about the crawl.
	 */
	public Statistics getStatistics();
}
