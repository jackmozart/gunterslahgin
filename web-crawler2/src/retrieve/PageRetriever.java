package retrieve;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import page.Page;
import statistics.Statistics;


public class PageRetriever {
	/**
	 * A set of all pages that have been retrieved.
	 */
	private Set<Page> pages_retrieved;
	private BlockingQueue<Page> my_pages_to_retrieve;
	private BlockingQueue<Page> my_pages_to_parse;
	private Statistics my_stats;
	
	public PageRetriever(BlockingQueue<Page> the_pages_to_retrieve, BlockingQueue<Page> the_pages_to_parse, Statistics the_stats){
		my_pages_to_retrieve = the_pages_to_retrieve;
		my_pages_to_parse = the_pages_to_parse;
		my_stats = the_stats;
	}
	
	/**
	 * Retrieve the contents of the page if I haven't already retrieved it.
	 * @param the_page
	 */
	public void retrieve(){
		Page a_page;
    try {
	    a_page = my_pages_to_retrieve.poll(1, TimeUnit.SECONDS);
    } catch (InterruptedException e1) {
	    a_page = null;
    }
		if(a_page != null){
			if(pages_retrieved.add(a_page)){
				StringBuilder html = new StringBuilder();
				
				BufferedReader in;
				try {
					in = new BufferedReader(new InputStreamReader(a_page.getAddress().toURL().openStream()));
					while(in.ready()){
						html.append(in.readLine().trim()).append(" ");
					}
				} catch (IOException e) {
					html.append("***ERROR***");
				}
				
				a_page.setContents(html.toString());	
				
				try {
	        my_pages_to_parse.put(a_page);
        } catch (InterruptedException e) {
	        //Do nothing, just return, we probably want to stop!
        }
			}
		}
	}
	
}
