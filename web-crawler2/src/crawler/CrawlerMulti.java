package crawler;

import analyze.PageAnalyzer;
import analyze.PageAnalyzerThread;
import page.Page;
import parse.PageParserThread;
import retrieve.PageRetrieverThread;

public class CrawlerMulti extends CrawlerGeneric {

	@Override
  public void run() {
	  PageAnalyzerThread pat = new PageAnalyzerThread(my_page_analyzer, my_stop_bit);
	  PageParserThread ppt = new PageParserThread(my_page_parser, my_stop_bit);
	  PageRetrieverThread prt = new PageRetrieverThread(my_page_retriever, my_stop_bit);
	  
	  pat.start();
	  ppt.start();
	  prt.start();
	  
  }

}
