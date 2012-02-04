package retrieve;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;

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
	
	private int my_mutex;
	
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
		private static final int PAGE_TIMEOUT = 1000;
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
	    	synchronized(this){
	    	a_page = my_pages_to_retrieve.poll(1, TimeUnit.SECONDS);
		    up();
	    	}
	    } catch (InterruptedException e1) {
		    a_page = null;
	    }
	    
			if(a_page != null){
				if(add_seen_page(a_page)){
					
					//System.out.println("Trying to get: " + a_page.getAddress().toString());
					try {
						String html = Jsoup.parse(a_page.getAddress().toURL(), PAGE_TIMEOUT).html();
						a_page.setContents(html);
						//System.err.format("\nFound html:", html.trim().replace("\n", " "));
						my_pages_to_parse.put(a_page);
          } catch (MalformedURLException e) {
	          //We were given a bad url.
          } catch (IOException e) {
	          //Could not get the page.          
          } catch (InterruptedException e) {
          	//we prob just want to exit
          } //in any case if an excpetiong is thrown just move on to the next page.
					
				}
			}
			down();
		}
	}

	private synchronized void up(){
		my_mutex++;
	}
	private synchronized void down(){
		my_mutex--;
	}
	
	public synchronized boolean isRunning() {
	  return my_mutex > 0;
  }

}
