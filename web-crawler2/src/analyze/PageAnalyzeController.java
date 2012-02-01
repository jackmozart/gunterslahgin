package analyze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import page.Page;
import crawler.CrawlerTuned;
import crawler.Stopbit;

public class PageAnalyzeController extends Thread{

	private CrawlerTuned my_crawler;
	
	private BlockingQueue<Page> my_pages_to_analyze;
	private Stopbit my_stop;
	private HashMap<String, Integer> my_keywords;
	private int my_pages_analyzed;
	
	private List<PageAnalyzer> my_analyzers;
	
	public PageAnalyzeController(CrawlerTuned the_crawler,BlockingQueue<Page> the_pages_to_analyze, String[] the_keywords,
      Stopbit the_stop) {
		my_crawler = the_crawler;
	  my_pages_to_analyze = the_pages_to_analyze;
	  my_keywords = new HashMap<String, Integer>();
	  for(String k:the_keywords){
	  	my_keywords.put(k, 0);
	  }
	  my_stop = the_stop;
	  
	  my_analyzers = new ArrayList<PageAnalyzer>();
  }
	public int getNumThreads(){
		return my_analyzers.size();
	}
	public void run() {
	  //start the initial 2 threads
		my_analyzers.add(new PageAnalyzer());
		my_analyzers.add(new PageAnalyzer());
		for(PageAnalyzer pa : my_analyzers){
			pa.start();
		}
		while(!my_stop.stop){
			if(my_pages_to_analyze.size() > 10){
				if(my_crawler.requestThread()){
					PageAnalyzer pa = new PageAnalyzer();
					my_analyzers.add(pa);
					pa.start();
				}
			}
			else if(my_pages_to_analyze.isEmpty() && my_analyzers.size() > 2){
				my_analyzers.get(0).requestStop();
				my_analyzers.remove(0);
			}
			try {
	      Thread.sleep(3000);
      } catch (InterruptedException e) {
	      break;
      }
		}
		for(PageAnalyzer pa : my_analyzers){
			pa.requestStop();
		}
  }
	
	public int getPagesAnalyzed() {
		return my_pages_analyzed;
  }

	public Map<String, Integer> getKeywordCounts() {
	  return my_keywords;
  }
	
	private class PageAnalyzer extends Thread{
		private Stopbit t_stop;
		
		private PageAnalyzer(){
			t_stop = new Stopbit();
		}
		
		@Override
		public void run(){
			while(!t_stop.stop){
				analyze();
			}
		}
		
		private void requestStop(){
			t_stop.stop = true;
		}
		
		private synchronized void addKeyword(String key){
			my_keywords.put(key, my_keywords.get(key) + 1);
		}
		
		private void analyze(){
			Page a_page;
			try{
				a_page = my_pages_to_analyze.poll(1, TimeUnit.SECONDS);
			}catch(InterruptedException e){
				//Do nothing, we probably want to stop.
				a_page = null;
			}
			
			if(a_page != null){
				//System.out.println("Trying to analyze: " + a_page.getAddress());
				for(String word:a_page.getWords()){
					if(my_keywords.containsKey(word)){
						addKeyword(word);
					}
				}
			  synchronized(this){
			  	my_pages_analyzed++;
			  }
			}
		}
	}
	
}
