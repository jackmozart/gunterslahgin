package analyze;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import page.Page;
import statistics.Statistics;

public class PageAnalyzer {
	/**
	 * A Queue of pages to analyze that is thread-safe.
	 */
	private BlockingQueue<Page> my_pages_to_analyze;
	/**
	 * A Queue of pages that are completed that is thread-safe.
	 */
	private BlockingQueue<Page> my_completed_pages;
	/**
	 * The statistics that will be reported to the user.
	 */
	private Statistics my_stats;

	public PageAnalyzer(BlockingQueue<Page> the_pages_to_analyze,
      BlockingQueue<Page> the_completed_pages, Statistics the_stats) {
	  my_pages_to_analyze = the_pages_to_analyze;
	  my_completed_pages = the_completed_pages;
	  my_stats = the_stats;
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
			//TODO replace pretend with a reference to the keywords map in statistics.
			Map<String, Integer> pretend_keywords = new HashMap<String, Integer>();
			
			for(String word:a_page.getWords()){
				if(pretend_keywords.containsKey(word)){
					pretend_keywords.put(word, pretend_keywords.get(word) + 1);
				}
			}
			
		  //TODO update statistics.
			
			try{
				my_completed_pages.put(a_page);
			}catch (InterruptedException e1) {
				//Do nothing, we probably want to stop.
			}
		}
	}
	
	
}
