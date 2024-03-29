package page;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a page class!
 * @author jim
 *
 */
public class Page {
	/**
	 * The contents of the page, whether it be html or plain text.
	 */
	private String my_contents;

	/**
	 * The address of the page.
	 */
	private URI my_address;
	/**
	 * How long the parser took to parse this page.
	 */
	private long my_parse_time;
	/**
	 * List of all links found on the page.
	 */
	private List<String> my_links;
	/**
	 * List of all words found on the page.
	 */
	private List<String> my_words;
	/**
	 * the number of keyword hits on this page.
	 */
	private Map<String, Integer> my_keyword_matches;
	
	/**
	 * Creates a page with the given address.
	 * @param the_address The internet address of the page.
	 */
	public Page(URI the_address){
		my_address = the_address;
		my_links = new ArrayList<String>();
		my_words = new ArrayList<String>();
		my_keyword_matches = new HashMap<String, Integer>();
	}
	
	/**
	 * Gets the contents of the page.
	 * @return The contents of the page.
	 */
	public String getContents() {
  	return my_contents;
  }
	/**
	 * Sets the contents of the page.
	 * @param string The contents of the page.
	 */
	public void setContents(String string) {
  	my_contents = string;
  }
	
	/**
	 * Gets the address of the page.
	 * @return The address of the page.
	 */
	public URI getAddress() {
  	return my_address;
  }
	/**
	 * Gets the list of all links found on the page found in <b>a</b> elements. 
	 * <p style="color:red">WARNING: May contain duplicates</p>
	 * @return List of all links found on the page.
	 */
	public List<String> getLinks() {
  	return my_links;
  }
	/**
	 * Gets the map of keywords and how often they were found in the page.
	 * @return A map of keywords and how often they were found in the page.
	 */
	public Map<String, Integer> getKeywordMatches() {
	  return my_keyword_matches;
  }
	
	/**
	 * Adds a link to the list of links found on the page.
	 * @param the_link A link found on the page.
	 */
	public void addLink(String the_link){
		my_links.add(the_link);
	}
	/**
	 * Gets the list of all words found on the page. If html, it only gets the words found when rendering the page.
	 * <p style="color:red">WARNING: May contain duplicates</p>
	 * @return List of all words found on the page.
	 */
	public List<String> getWords() {
  	return my_words;
  }
	/**
	 * Adds a word to the list of words found on the page.
	 * @param the_word A Word found on the page.
	 */
	public void addWord(String the_word){
		my_words.add(the_word);
	}
	/**
	 * Gets the amount of time it took to parse the page.
	 * @param the_parse_time The amount of time it took to parse the page.
	 */
	public void setParseTime(long the_parse_time) {
	  my_parse_time = the_parse_time;
	  
  }
	
	
	
	
	@Override
	public int hashCode(){
		return my_address.hashCode();
	}
	
	@Override
	public boolean equals(Object o){
		boolean result = false;
		if(o != null && o.getClass() == Page.class){
			Page the_other = (Page) o;
			if(my_address.equals(the_other.my_address)){
				result = true;
			}
		}
		return result;
	}
	
	@Override
  public String toString(){
		StringBuilder s = new StringBuilder();
		s.append("Address: ").append(my_address).append("\nContents: ").append(my_contents);
		return s.toString();
	}

	
}
