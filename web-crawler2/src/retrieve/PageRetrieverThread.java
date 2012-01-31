package retrieve;

import crawler.Stopbit;

public class PageRetrieverThread extends Thread {
	
	private PageRetriever my_page_retriever;
	private Stopbit my_stop;

	public PageRetrieverThread(PageRetriever the_retriever, Stopbit the_stop){
		my_page_retriever = the_retriever;
		my_stop = the_stop;
	}
	
	@Override
	public void run() {
		while(!my_stop.stop){
			my_page_retriever.retrieve();
		}
	}

}
