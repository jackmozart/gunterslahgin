package retrieve;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import page.Page;


public class PageRetriever {
	private Set<Page> pages_retrieved;
	
	/**
	 * Retrieve the contents of the page if I haven't already retrieved it.
	 * @param the_page
	 */
	public void retrieve(Page the_page){
		if(pages_retrieved.add(the_page)){
			StringBuilder html = new StringBuilder();
			
			BufferedReader in;
			try {
				in = new BufferedReader(new InputStreamReader(the_page.getAddress().toURL().openStream()));
				while(in.ready()){
					html.append(in.readLine().trim()).append(" ");
				}
			} catch (IOException e) {
				html.append("***ERROR***");
			}
			
			the_page.setContents(html.toString());	
		}
	}
	
}
