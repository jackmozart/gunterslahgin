package retrieve;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import page.Page;


public class PageRetriever {
	/**
	 * A set of all pages that have been retrieved.
	 */
	private Set<Page> my_pages_retrieved;
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
			if(my_pages_retrieved.add(a_page)){
				
				//System.out.println("Trying to get: " + a_page.getAddress().toString());
				
				StringBuilder html = new StringBuilder();
				
				html.append("");
				
				BufferedReader in;
				try {
					URLConnection url_conn =  a_page.getAddress().toURL().openConnection();
					if(url_conn.getContentType().toLowerCase().contains("text/html") || url_conn.getContentType().toLowerCase().contains("text/plain")){
						in = new BufferedReader(new InputStreamReader(a_page.getAddress().toURL().openStream()));
						
						while(in.ready()){
							html.append(in.readLine().trim()).append(" ");
						}
					} else {
						//System.out.println("Page did not contain text or html: " + a_page.getAddress().toString() + " was " + url_conn.getContentType());
					}
					
				} catch (IOException e) {
					//Do nothing, just discard the page.
				}
				
				//If the page actually returned anything, then pass it on to the parser.
				if(!html.toString().equals(""))
				{
					a_page.setContents(html.toString());	
					
					try {
						
		        my_pages_to_parse.put(a_page);
		        //System.out.println("Placing this on the parse queue: " + a_page.getAddress().toString());
	        } catch (InterruptedException e) {
		        //Do nothing, just return, we probably want to stop!
	        }
				}
			}
		}
	}
	
}
