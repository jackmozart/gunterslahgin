package crawler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import page.Page;
import parse.PageParser;
import retrieve.PageRetriever;
import analyze.PageAnalyzer;

public abstract class CrawlerGeneric implements Crawler, Runnable{
	protected BlockingQueue<Page> my_pages_to_retrieve;
	protected BlockingQueue<Page> my_pages_to_parse;
	protected BlockingQueue<Page> my_pages_to_analyze;
	protected Map<String, Integer> my_keyword_counts;
	
	protected PageRetriever my_page_retriever;
	protected PageParser my_page_parser;
	protected PageAnalyzer my_page_analyzer;
	
	protected long my_crawl_start_time;
	
	protected int my_max_pages;
	
	protected Stopbit my_stop_bit;
	
	public CrawlerGeneric(){
		my_pages_to_retrieve = new LinkedBlockingQueue<Page>();
		my_pages_to_parse = new LinkedBlockingQueue<Page>();
		my_pages_to_analyze = new LinkedBlockingQueue<Page>();
		
		my_keyword_counts = new HashMap<String, Integer>();
		
		my_page_retriever = new PageRetriever(my_pages_to_retrieve, my_pages_to_parse);
		my_page_parser = new PageParser(my_pages_to_parse, my_pages_to_retrieve, my_pages_to_analyze);
		my_page_analyzer = new PageAnalyzer(my_pages_to_analyze, my_keyword_counts);
		
		my_stop_bit = new Stopbit();
		
		
	}
	
	@Override
	public void crawl(Page the_seed_page, String[] the_keywords, int the_max_pages){
		my_crawl_start_time = System.nanoTime();
		
		try {
	    my_pages_to_retrieve.put(the_seed_page);
    } catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
		for(String k:the_keywords){
			my_keyword_counts.put(k.toLowerCase(), 0);
		}
		my_max_pages = the_max_pages;
		
		Thread me = new Thread(this);
		me.start();
	}
	
	@Override
	public void stop(){
		my_stop_bit.stop = true;
	}
	
	@Override
	public Map<String, Integer> getKeywordCounts(){
		return my_keyword_counts;
	}
	
	@Override
	public int getPagesCrawled(){
		return my_page_analyzer.getPagesAnalyzed();
	}
	@Override
	public int getPagesParsed(){
		return my_page_parser.getPagesParsed();
	}
	@Override
	public long getParseTime(){
		return my_page_parser.getPageParseTime();
	}

	@Override
	public long getTimeElapsed(){
		return System.nanoTime() - my_crawl_start_time;
	}
	@Override
	public int getUrlsFound(){
		return my_page_parser.getUrlsFound();
	}
	@Override
	public int getWordCount(){
		return my_page_parser.getWordCount();
	}
	
}
