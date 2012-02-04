package retrieve;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;

import page.Page;


public class PageRetriever {
	private static final int PAGE_TIMEOUT = 1000;
	/**
	 * A set of all pages that have been retrieved.
	 */
	private Set<Page> my_pages_retrieved;
	/**
	 * A set of all pages looked at.
	 */
	private Set<Page> my_pages_seen;
	/**
	 * A Queue of pages to retrieve that is thread-safe.
	 */
	private BlockingQueue<Page> my_pages_to_retrieve;
	/**
	 * A Queue of pages to parse that is thread-safe.
	 */
	private BlockingQueue<Page> my_pages_to_parse;

	
	public PageRetriever(BlockingQueue<Page> the_pages_to_retrieve, BlockingQueue<Page> the_pages_to_parse){
		my_pages_to_retrieve = the_pages_to_retrieve;
		my_pages_to_parse = the_pages_to_parse;
		my_pages_retrieved = new HashSet<Page>();
		my_pages_seen = new HashSet<Page>();
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
			if(my_pages_seen.add(a_page)){
				
			//System.out.println("Trying to get: " + a_page.getAddress().toString());
				try {
					String html = Jsoup.parse(a_page.getAddress().toURL(), PAGE_TIMEOUT).html();
					a_page.setContents(html);
					//System.err.format("\nFound html:", html.trim().replace("\n", " "));
					my_pages_to_parse.put(a_page);
        } catch (MalformedURLException e) {
          //We were given a bad url.
        } catch (IOException e) {
          //Could not get the page.          
        } catch (InterruptedException e) {
        	//we prob just want to exit
        } //in any case if an excpetiong is thrown just move on to the next page.
			}
		}
	}
	
}
