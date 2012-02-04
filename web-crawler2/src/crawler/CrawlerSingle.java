package crawler;

import page.Page;

public class CrawlerSingle extends CrawlerGeneric implements  Runnable {
	
	private boolean isDone = false;
	
	@Override
  public void run() {
	  while(true){
	  	my_page_retriever.retrieve();
	  	my_page_parser.parse();
	  	my_page_analyzer.analyze();
	  	if(my_pages_to_analyze.isEmpty() && my_pages_to_parse.isEmpty() && my_pages_to_retrieve.isEmpty()){
	  		isDone = true;
	  		break;
	  	}
	  }
  }
	
	@Override
	public boolean isDone(){
		return isDone;
	}


	
}
