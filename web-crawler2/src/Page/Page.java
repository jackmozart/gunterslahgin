package Page;

import java.net.URI;
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
	 * Gets the contents of the page.
	 * @return The contents of the page.
	 */
	public String getContents() {
  	return my_contents;
  }
	/**
	 * Sets the contents of the page.
	 * @param the_contents The contents of the page.
	 */
	public void setContents(String the_contents) {
  	my_contents = the_contents;
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

	public Page(URI the_address){
		my_address = the_address;
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
