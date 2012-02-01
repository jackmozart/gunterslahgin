package analyze;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import page.Page;

public class PageAnalyzer {
	/**
	 * A Queue of pages to analyze that is thread-safe.
	 */
	private BlockingQueue<Page> my_pages_to_analyze;
  /**
   * The list of keywords to look for, with their counts.
   */
	private Map<String, Integer> my_keywords;
	/**
	 * Total number of pages that have been analyzed.
	 */
	private int my_pages_analyzed;
	
	public PageAnalyzer(BlockingQueue<Page> the_pages_to_analyze, Map<String, Integer> the_keywords) {
	  my_pages_to_analyze = the_pages_to_analyze;
	  my_keywords = the_keywords;
  }
	
	public void analyze(){
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
					my_keywords.put(word, my_keywords.get(word) + 1);
				}
			}
			
		  synchronized(this){
		  	my_pages_analyzed++;
		  }
		}
	}

	public int getPagesAnalyzed() {
	  return my_pages_analyzed;
  }
	
	
}
