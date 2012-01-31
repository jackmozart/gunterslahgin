package analyze;

import crawler.Stopbit;

public class PageAnalyzerThread extends Thread {
	
	private PageAnalyzer my_page_analyzer;
	private Stopbit my_stop;

	public PageAnalyzerThread(PageAnalyzer the_analyzer, Stopbit the_stop){
		my_page_analyzer = the_analyzer;
		my_stop = the_stop;
	}
	
	@Override
	public void run() {
		while(!my_stop.stop){
			my_page_analyzer.analyze();
		}
	}

}
