package crawler;

import page.Page;

public class CrawlerSingle extends CrawlerGeneric implements  Runnable {

	@Override
  public void run() {
	  while(true){
	  	System.out.println("retrieve queue now has: " + my_pages_to_retrieve.size());
	  	my_page_retriever.retrieve();
	  	System.out.println("parse queue now has: " + my_pages_to_parse.size());
	  	my_page_parser.parse();
	  	System.out.println("analyze queue now has: " + my_pages_to_analyze.size());
	  	my_page_analyzer.analyze();
	  	
	  }
	  
  }


	
}
