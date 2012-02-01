package parse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.htmlparser.Node;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

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
		    a_page = my_pages_to_parse.poll(1, TimeUnit.SECONDS);
	    } catch (InterruptedException e1) {
	    	//Do nothing, we probably want to stop.
		    a_page = null;
	    }
			if(a_page != null){
				
				//System.out.println("Trying to parse: " + a_page.getAddress());
				long start_time = System.nanoTime();
				
				try {
	        NodeList nodes = a_page.getParser().parse(new AndFilter(new TagNameFilter("A"), new HasAttributeFilter("HREF")));
	        
	        for(Node node : nodes.toNodeArray()){
	        	if(node instanceof LinkTag){
	        		LinkTag linknode = (LinkTag)node;
	        		if(linknode.isHTTPLikeLink() && !linknode.isJavascriptLink()){
	        			a_page.addLink(a_page.getAddress().resolve(linknode.extractLink()).toString());
	        		}
	        	}
	        }
	        
        } catch (ParserException e1) {
	        //No hard feelings if the parser fails.
        } catch (IllegalArgumentException e2){
        	//The link couldn't be resolved, probably had bad chars in it.
        }
				
				for(String word : a_page.getContents().split(" ")) {
					a_page.addWord(word);
					
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
		}
	}
	
}
