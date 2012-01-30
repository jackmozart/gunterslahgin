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

	public PageParser(BlockingQueue<Page> the_pages_to_parse,
      BlockingQueue<Page> the_pages_to_retrieve,
      BlockingQueue<Page> the_pages_to_analyze) {
	  my_pages_to_parse = the_pages_to_parse;
	  my_pages_to_retrieve = the_pages_to_retrieve;
	  my_pages_to_analyze = the_pages_to_analyze;
	  my_pages_parsed = 0;
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
			long start_time = System.nanoTime();
			
			Source page_source = new Source(a_page.getContents());

			page_source.fullSequentialParse();

			List<Element> linkElements = page_source
					.getAllElements(HTMLElementName.A);
			for (Element linkElement : linkElements) {
				String href = linkElement.getAttributeValue("href");
				if (href == null)
					continue;

				if (href.endsWith(".html") || href.endsWith(".txt")) {
					a_page.addLink(a_page.getAddress().resolve(href).toString());
				}
			}

			String page_text = page_source.getTextExtractor().toString();

			Pattern word_pat = Pattern.compile("\\b(\\w+)\\b");
			Matcher word_mat = word_pat.matcher(page_text);
			
			while (word_mat.find()) {
				a_page.addWord(word_mat.group(1));
			}
			
			long total_time = System.nanoTime() - start_time;
			
			synchronized(this){
				my_pages_parsed++;
				my_page_parse_time +=total_time;
			}
			
			for(String s:a_page.getLinks()){
				try {
	        my_pages_to_retrieve.put(new Page(new URI(s)));
	        my_pages_to_analyze.put(a_page);
        } catch (InterruptedException e) {
	        //Do nothing, we probably want to stop.
        } catch (URISyntaxException e) {
	        //Invalid url, just skip it.
        }
			}
			
			
		}
		
	}
	
}
