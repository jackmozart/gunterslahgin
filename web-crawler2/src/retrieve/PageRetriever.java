package retrieve;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;

import net.htmlparser.jericho.Source;
import page.Page;


public class PageRetriever {
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
				
				boolean good_page = true;
				//System.out.println("Trying to get: " + a_page.getAddress().toString());
				try {
					Parser a_parser = new Parser(a_page.getAddress().toString());
					StringBean sb = new StringBean();
					sb.setURL(a_page.getAddress().toString());
					a_page.setContents(sb.getStrings());
	        a_page.setParser(a_parser);
        } catch (ParserException e) {
          good_page = false;
        }
				
				//System.out.println("Source Found: " + a_page.getContents().toString());
				if(good_page){
					try {
		        my_pages_to_parse.put(a_page);
	        } catch (InterruptedException e) {
		        //Prob just trying to exit
	        }
				}
			}
		}
	}
	
}
