package parse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import page.Page;



public class PageParser {
	/**
	 * A Queue of pages to parse that is thread-safe.
	 */
	private BlockingQueue<Page> my_pages_to_parse;
	/**
	 * A Queue of pages to retrieve that is thread-safe.
	 */
	private BlockingQueue<Page> my_pages_to_retrieve;
	/**
	 * A Queue of pages to analyze that is thread-safe.
	 */
	private BlockingQueue<Page> my_pages_to_analyze;
	/**
	 * The total number of pages parsed so far.
	 */
	private int my_pages_parsed;
	/**
	 * The total amount of time spent parsing.
	 */
	private long my_page_parse_time;
	/**
	 * The number of urls found on any page.
	 */
	private int my_urls_found;
	/**
	 * The number of words found on any page.
	 */
	private int my_word_count;
	/**
	 * 
	 * @param the_pages_to_parse
	 * @param the_pages_to_retrieve
	 * @param the_pages_to_analyze
	 */

	public PageParser(BlockingQueue<Page> the_pages_to_parse,
      BlockingQueue<Page> the_pages_to_retrieve,
      BlockingQueue<Page> the_pages_to_analyze) {
	  my_pages_to_parse = the_pages_to_parse;
	  my_pages_to_retrieve = the_pages_to_retrieve;
	  my_pages_to_analyze = the_pages_to_analyze;
	  my_pages_parsed = 0;
  }
	/**
	 * Gets the number of pages that have been parsed so far.
	 * @return The number of pages that have been parsed.
	 */
	public int getPagesParsed(){
		return my_pages_parsed;
	}
	/**
	 * Gets the number of nanoseconds spent parsing pages.
	 * @return the number of nanoseconds spent parsing pages.
	 */
	public long getPageParseTime(){
		return my_page_parse_time;
	}
	/**
	 * Get the total number of urls found on any page.
	 * @return The total number of urls found on any page.
	 */
	public int getUrlsFound(){
		return my_urls_found;
	}
	/**
	 * Gets the total number of words found on any page.
	 * @return The total number of words found on any page.
	 */
	public int getWordCount(){
		return my_word_count;
	}
	
	public void parse(){
		Page a_page;
    try {
	    a_page = my_pages_to_parse.poll(1, TimeUnit.SECONDS);
    } catch (InterruptedException e1) {
    	//Do nothing, we probably want to stop.
	    a_page = null;
    }
		if(a_page != null){
			
			//System.out.println("Trying to parse: " + a_page.getAddress());
			long start_time = System.nanoTime();
			
			a_page.getContents().fullSequentialParse();

			List<Element> linkElements = a_page.getContents()
					.getAllElements(HTMLElementName.A);
			for (Element linkElement : linkElements) {
				String href = linkElement.getAttributeValue("href");
				
				if (href == null) continue;
				try{
					a_page.addLink(a_page.getAddress().resolve(href).toString());
				} catch (IllegalArgumentException ill_arg_exc){
					//Was a bad link, just ignore it and move on.
				}
				
			}

			String page_text = a_page.getContents().getTextExtractor().toString();

			Pattern word_pat = Pattern.compile("\\b(\\w+)\\b");
			Matcher word_mat = word_pat.matcher(page_text);
			
			while (word_mat.find()) {
				a_page.addWord(word_mat.group(1));
				
			}
			
			long total_time = System.nanoTime() - start_time;
			
			synchronized(this){
				my_pages_parsed++;
				my_page_parse_time +=total_time;
				my_word_count += a_page.getWords().size();
				my_urls_found += a_page.getLinks().size();
			}
			
			for(String s:a_page.getLinks()){
				try {
	        my_pages_to_retrieve.put(new Page(new URI(s)));
        } catch (InterruptedException e) {
	        //Do nothing, we probably want to stop.
        } catch (URISyntaxException e) {
	        //Invalid url, just skip it.
        }
			}
			try {
	      my_pages_to_analyze.put(a_page);
      } catch (InterruptedException e) {
	      //Do nothing, we probably want to stop.
      }
			
		}
		
	}
	
}
