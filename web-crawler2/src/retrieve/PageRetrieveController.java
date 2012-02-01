package retrieve;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.htmlparser.Parser;
import org.htmlparser.beans.StringBean;
import org.htmlparser.util.ParserException;

import page.Page;
import crawler.CrawlerTuned;
import crawler.Stopbit;

public class PageRetrieveController extends Thread {
	
	private CrawlerTuned my_crawler;
	private BlockingQueue<Page> my_pages_to_retrieve;
	private BlockingQueue<Page> my_pages_to_parse;
	private Set<Page> my_pages_seen;
	private Stopbit my_stop;
	private List<PageRetriever> my_retrievers;

	public PageRetrieveController(CrawlerTuned the_crawler, BlockingQueue<Page> the_pages_to_retrieve,
      BlockingQueue<Page> the_pages_to_parse, Stopbit the_stop) {
	  my_crawler = the_crawler;
	  my_pages_to_retrieve = the_pages_to_retrieve;
	  my_pages_to_parse = the_pages_to_parse;
	  my_stop = the_stop;
	  my_retrievers = new ArrayList<PageRetriever>();
	  my_pages_seen = new HashSet<Page>();
  }
	
	public int getNumThreads(){
		return my_retrievers.size();
	}
	
	public void run() {
	//start the initial 2 threads
			my_retrievers.add(new PageRetriever());
			my_retrievers.add(new PageRetriever());
			for(PageRetriever pt : my_retrievers){
				pt.start();
			}
			while(!my_stop.stop){
				if(my_pages_to_retrieve.size() > 10){
					if(my_crawler.requestThread()){
						PageRetriever pt = new PageRetriever();
						my_retrievers.add(pt);
						pt.start();
					}
				}
				else if(my_pages_to_retrieve.isEmpty() && my_retrievers.size() > 2){
					my_retrievers.get(0).requestStop();
					my_retrievers.remove(0);
				}
				try {
		      Thread.sleep(3000);
	      } catch (InterruptedException e) {
		      break;
	      }
			}
			for(PageRetriever pt : my_retrievers){
				pt.requestStop();
			}	  
	  
  }
	
	private class PageRetriever extends Thread{
		private Stopbit t_stop;
		
		private PageRetriever(){
			t_stop = new Stopbit();
		}
		
		private void requestStop(){
			t_stop.stop = true;
		}
		
		public void run(){
			while(!t_stop.stop){
				parse();
			}
		}
		
		private synchronized boolean add_seen_page(Page the_page){
			return my_pages_seen.add(the_page);
		}
		
		private void parse(){
			Page a_page;
	    try {
		    a_page = my_pages_to_retrieve.poll(1, TimeUnit.SECONDS);
	    } catch (InterruptedException e1) {
		    a_page = null;
	    }
			if(a_page != null){
				if(add_seen_page(a_page)){
					
					boolean good_page = true;
					//System.out.println("Trying to get: " + a_page.getAddress().toString());
					try {
						Parser a_parser = new Parser(a_page.getAddress().toString());
						StringBean sb = new StringBean();
						sb.setURL(a_page.getAddress().toString());
						a_page.setContents(sb.getStrings());
		        a_page.setParser(a_parser);
	        } catch (ParserException e) {
	          good_page = false;
          }
					
					//System.out.println("Source Found: " + a_page.getContents().toString());
					if(good_page){
						try {
			        my_pages_to_parse.put(a_page);
		        } catch (InterruptedException e) {
			        //Prob just trying to exit
		        }
					}
				}
			}
		}
	}

}
