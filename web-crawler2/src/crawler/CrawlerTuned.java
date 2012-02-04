package crawler;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import page.Page;
import parse.PageParseController;
import retrieve.PageRetrieveController;
import analyze.PageAnalyzeController;

public class CrawlerTuned implements Crawler {
	
	private PageAnalyzeController my_page_analyzer;
	private PageParseController my_page_parser;
	private PageRetrieveController my_page_retriever;
	
	private BlockingQueue<Page> my_pages_to_retrieve;
	private BlockingQueue<Page> my_pages_to_parse;
	private BlockingQueue<Page> my_pages_to_analyze;
	
	private Stopbit my_stop;
	
	private long my_start_time;
	
	private final int my_max_threads;
	
	private int my_used_threads;
	
	public CrawlerTuned(){
		my_max_threads = Runtime.getRuntime().availableProcessors() * 6 + 2;
		//each controller gets two threads right off the bat, they can then request more later.
		my_used_threads = 6;
		
		my_stop = new Stopbit();
		
		my_pages_to_analyze = new LinkedBlockingQueue<Page>();
		my_pages_to_parse = new LinkedBlockingQueue<Page>();
		my_pages_to_retrieve = new LinkedBlockingQueue<Page>();
		
		
	}
	
	@Override
	public void crawl(Page the_seed_page, String[] the_keywords, int the_max_pages) {
		my_start_time = System.nanoTime();
		
		try {
	    my_pages_to_retrieve.put(the_seed_page);
    } catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
		
		my_page_analyzer = new PageAnalyzeController(this, my_pages_to_analyze, the_keywords, my_stop);
		my_page_parser = new PageParseController(this, my_pages_to_parse, my_pages_to_retrieve, my_pages_to_analyze, my_stop);
		my_page_retriever = new PageRetrieveController(this, my_pages_to_retrieve, my_pages_to_parse, my_stop);
		
		my_page_analyzer.start();
		my_page_parser.start();
		my_page_retriever.start();

	}

	@Override
	public Map<String, Integer> getKeywordCounts() {
		return my_page_analyzer.getKeywordCounts();
	}

	@Override
	public int getPagesCrawled() {
		return my_page_analyzer.getPagesAnalyzed();
	}

	@Override
	public int getPagesParsed() {
		return my_page_parser.getPagesParsed();
	}

	@Override
	public int getWordCount() {
		return my_page_parser.getWordCount();
	}

	@Override
	public int getUrlsFound() {
		return my_page_parser.getUrlsFound();
	}

	@Override
	public long getParseTime() {
		return my_page_parser.getParseTime();
	}

	@Override
	public long getTimeElapsed() {
		return System.nanoTime() - my_start_time;
	}

	@Override
	public void stop() {
		my_stop.stop = true;
	}
	
	public int getNumRet(){
		return my_page_retriever.getNumThreads();
	}
	public int getNumPar(){
		return my_page_parser.getNumThreads();
	}
	public int getNumAna(){
		return my_page_analyzer.getNumThreads();
	}
	
	public synchronized boolean requestThread(){
		boolean result = false;
		if(my_used_threads < my_max_threads){
			my_used_threads++;
			result = true;
		}
		return result;
	}
	
	public synchronized void releaseThread(){
		my_used_threads--;
	}

	@Override
  public boolean isDone() {
	  boolean result = false;
	  if(!my_page_analyzer.isRunning() && !my_page_parser.isRunning() && !my_page_retriever.isRunning() &&
	  		my_pages_to_analyze.isEmpty() && my_pages_to_parse.isEmpty() && my_pages_to_retrieve.isEmpty()){
	  	result = true;
	  }
	  return result;
  }
	
}
