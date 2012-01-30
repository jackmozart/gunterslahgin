package crawler;

import page.Page;

public interface Crawler {
	/**
	 * Starts the crawling process.
	 */
	public void crawl(Page seed_page);
	
	
}
