package crawler;

import java.util.Map;

import page.Page;

public interface Crawler {
	void crawl(Page the_seed_page, String[] the_keywords, int the_max_pages);
	Map<String, Integer> getKeywordCounts(); 
	int getPagesCrawled();
	int getPagesParsed();
	int getWordCount();
	int getUrlsFound();
	long getParseTime();
	long getTimeElapsed();
	void stop();
}
