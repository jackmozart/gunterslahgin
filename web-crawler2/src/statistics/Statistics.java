package statistics;

import java.util.List;

public class Statistics {
	
	/**
	 * Number of pages already crawled
	 */
	private int my_totalPages;
	
	/**
	 * Average time spent parsing a page.
	 */
	private int my_avgPageTime;
	
	/**
	 * Total hits per word
	 */
	private List<Integer> my_totalHits;
	
	/**
	 * Hits per page.
	 */
	private List<Integer> my_hitAvg;
	
	/**
	 * Last url parsed.  
	 */
	private String my_page;

	public void setMy_totalPages(int my_totalPages) {
		this.my_totalPages = my_totalPages;
	}

	public int getMy_totalPages() {
		return my_totalPages;
	}

	public void setMy_avgPageTime(int my_avgPageTime) {
		this.my_avgPageTime = my_avgPageTime;
	}

	public int getMy_avgPageTime() {
		return my_avgPageTime;
	}

	public void setMy_totalHits(List<Integer> my_totalHits) {
		this.my_totalHits = my_totalHits;
	}

	public List<Integer> getMy_totalHits() {
		return my_totalHits;
	}

	public void setMy_hitAvg(List<Integer> my_hitAvg) {
		this.my_hitAvg = my_hitAvg;
	}

	public List<Integer> getMy_hitAvg() {
		return my_hitAvg;
	}

	public void setMy_page(String my_page) {
		this.my_page = my_page;
	}

	public String getMy_page() {
		return my_page;
	}
	
}
