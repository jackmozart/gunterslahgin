package parse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import page.Page;
import crawler.CrawlerTuned;
import crawler.Stopbit;

public class PageParseController extends Thread {

	private CrawlerTuned my_crawler;
	private BlockingQueue<Page> my_pages_to_parse;
	private BlockingQueue<Page> my_pages_to_retrieve;
	private BlockingQueue<Page> my_pages_to_analyze;
	private Stopbit my_stop;
	
	private List<PageParser> my_parsers;
	
	private long my_parse_time;
	private int my_pages_parsed;
	private int my_words_found;
	private int my_urls_found;
	
	private int my_mutex;
	
	public PageParseController(CrawlerTuned the_crawler, BlockingQueue<Page> the_pages_to_parse,
      BlockingQueue<Page> the_pages_to_retrieve, BlockingQueue<Page> the_pages_to_analyze,
      Stopbit the_stop) {
	  my_crawler = the_crawler;
	  my_pages_to_parse = the_pages_to_parse;
	  my_pages_to_retrieve = the_pages_to_retrieve;
	  my_pages_to_analyze = the_pages_to_analyze;
	  my_stop = the_stop;
	  
	  my_parsers = new ArrayList<PageParser>();
  }
	
	public int getNumThreads(){
		return my_parsers.size();
	}
	
	public void run() {
		//start the initial 2 threads
		my_parsers.add(new PageParser());
		my_parsers.add(new PageParser());
		for(PageParser pa : my_parsers){
			pa.start();
		}
		while(!my_stop.stop){
			if(my_pages_to_parse.size() > 10){
				if(my_crawler.requestThread()){
					PageParser pp = new PageParser();
					my_parsers.add(pp);
					pp.start();
				}
			}
			else if(my_pages_to_parse.isEmpty() && my_parsers.size() > 2){
				my_parsers.get(0).requestStop();
				my_parsers.remove(0);
			}
			try {
	      Thread.sleep(3000);
      } catch (InterruptedException e) {
	      break;
      }
		}
		for(PageParser pp : my_parsers){
			pp.requestStop();
		}	  
  }

	public long getParseTime() {
		return my_parse_time;
  }

	public int getUrlsFound() {
	  return my_urls_found;
  }

	public int getWordCount() {
	  return my_words_found;
  }

	public int getPagesParsed() {
	  return my_pages_parsed;
  }
	
	private class PageParser extends Thread{
		private Stopbit t_stop;
		
		private PageParser(){
			t_stop = new Stopbit();
		}
		
		private void requestStop(){
			t_stop.stop = true;
		}
		
		@Override
		public void run(){
			while(!t_stop.stop){
				parse();
			}
		}
		
		private void parse(){
			Page a_page;
	    try {
	    	synchronized(this){
		    a_page = my_pages_to_parse.poll(1, TimeUnit.SECONDS);
		    up();
	    	}
	    } catch (InterruptedException e1) {
	    	//Do nothing, we probably want to stop.
		    a_page = null;
	    }
	    
			if(a_page != null){
				
				//System.out.println("Trying to parse: " + a_page.getAddress());
				long start_time = System.nanoTime();
				
				Document doc = Jsoup.parse(a_page.getContents(), a_page.getAddress().toString());
				
				Elements links = doc.select("a[href]");
				
				for(Element link : links){
					a_page.addLink(link.absUrl("href"));
					//System.err.format("\nAdding \"%s\" to the queue", link.absUrl("href"));
				}
				
				String plain_text = doc.body().text();
				//System.err.format("\nBody text of %s is %s:", a_page.getAddress().toString(), plain_text);
				
				Pattern word_pat = Pattern.compile("\\b\\w+\\b");
				Matcher word_mat = word_pat.matcher(plain_text);
				while(word_mat.find()){
					a_page.addWord(word_mat.group());
					//System.err.format("\nAdding \"%s\" to the word_list", word_mat.group());
				}
				
				
				long total_time = System.nanoTime() - start_time;
				
				synchronized(this){
					my_pages_parsed++;
					my_parse_time +=total_time;
					my_words_found += a_page.getWords().size();
					my_urls_found += a_page.getLinks().size();
				}
				
				for(String s:a_page.getLinks()){
					try {
		        my_pages_to_retrieve.put(new Page(new URI(s)));
	        } catch (InterruptedException e) {
		        //Do nothing, we probably want to stop.
	        } catch (URISyntaxException e) {
		        //Invalid url, just skip it.
	        }
				}
				try {
		      my_pages_to_analyze.put(a_page);
	      } catch (InterruptedException e) {
		      //Do nothing, we probably want to stop.
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
