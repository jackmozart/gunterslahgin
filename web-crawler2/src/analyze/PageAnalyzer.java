package analyze;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import page.Page;
import statistics.Statistics;

public class PageAnalyzer {
	private BlockingQueue<Page> my_pages_to_analyze;
	private BlockingQueue<Page> my_completed_pages;
	
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
			
			
			try{
				my_completed_pages.put(a_page);
			}catch (InterruptedException e1) {
				//Do nothing, we probably want to stop.
			}
		}
	}
	
	
}
