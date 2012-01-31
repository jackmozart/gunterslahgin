package parse;

import crawler.Stopbit;

public class PageParserThread extends Thread  {
	
	private PageParser my_page_parser;
	private Stopbit my_stop;

	public PageParserThread(PageParser the_parser, Stopbit the_stop){
		my_page_parser = the_parser;
		my_stop = the_stop;
	}
	
	@Override
	public void run() {
		while(!my_stop.stop){
			my_page_parser.parse();
		}
	}

}
