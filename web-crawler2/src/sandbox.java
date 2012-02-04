import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class sandbox {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Document doc;
    try {
	    doc = Jsoup.parse(new URL("http://en.wikipedia.com"), 1000);

			String text = doc.body().text();
			
			System.out.println(text);
    } catch (MalformedURLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    } catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
    }
		

	}

}
