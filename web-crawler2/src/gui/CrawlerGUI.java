package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import page.Page;
import crawler.Crawler;
import crawler.CrawlerSingle;
import crawler.CrawlerTuned;
import crawler.Stopbit;

/**
 * 
 * @author Steven Cozart
 * @version 1.0
 */
@SuppressWarnings("serial")
public class CrawlerGUI extends JFrame implements ActionListener {
	
	private static final DecimalFormat DF = new DecimalFormat("0.00##");

	private static final Color LABEL_COLOR = Color.BLUE;

	private static final int NUM_KEYWORDS = 10;

	public static final String DEFULT_START_PAGE = "http://css.insttech.washington.edu/~mealden/";

	public static final String DEFULT_SEED = "100";

	private static final int KEYWORD_LENGTH = 15;
	
	private static final long UPDATE_INTERVAL = 500;

	private JPanel my_runPanel;

	private JTextField my_input_url;

	private JTextField my_num_pages;

	private JButton my_runButton;

	private JCheckBox my_threadTogle;

	private JButton my_stopButton;

	private JPanel my_keywordPanel;

	private ArrayList<JTextField> my_keywordList;

	private ArrayList<JLabel> my_hitPerPage;

	private ArrayList<JLabel> my_totalHit;

	private JPanel my_contentPane;

	private JLabel my_totalTimeLabel;

	private JLabel my_pageTimeLabel;

	private JLabel my_pagesProccesedLabel;

	private JLabel my_avgUrlLabel;

	private Crawler my_crawler;

	private List<String> my_curKeywordsList;

	private JLabel my_avgWordLabel;

	private Stopbit my_stop;

	private JLabel my_statusLabel;

	public CrawlerGUI() {
		super();
		
		my_stop = new Stopbit();
		
		my_curKeywordsList = new ArrayList<String>();
		my_contentPane = new JPanel();
		my_contentPane.setLayout(new BorderLayout());
		this.setContentPane(my_contentPane);
		this.setResizable(false);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		// this.setLocationRelativeTo(null);

		my_keywordList = new ArrayList<JTextField>();

		my_hitPerPage = new ArrayList<JLabel>();

		my_totalHit = new ArrayList<JLabel>();

		// sets up the window
		this.setTitle("Gunterslagen the web since 1988");
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		// sets the icon in the os toolbar and window icon
		Image icon = Toolkit.getDefaultToolkit().createImage(
				"src/gui/crawler_icon.png");
		this.setIconImage(icon);

		// adds the keyword input pannel
		keyWordPanel();
		my_contentPane.add(my_keywordPanel, BorderLayout.NORTH);

		// add the stats panel
		this.add(createStatsPane(), BorderLayout.CENTER);

		// adds panel to the window
		my_runPanel = new JPanel(new FlowLayout());
		my_contentPane.add(my_runPanel, BorderLayout.SOUTH);
		my_runPanel.setBackground(Color.white);

		// make input spots
		// url input
		JLabel web_label = new JLabel();
		web_label.setText("Start Page:");
		web_label
				.setToolTipText("Insert a web addres to start search in format www.startaddres.org");
		my_runPanel.add(web_label);

		my_input_url = new JTextField(20);
		my_input_url.setName("input url");
		my_input_url.setText(DEFULT_START_PAGE);
		my_input_url.setBackground(Color.LIGHT_GRAY);
		my_input_url.setSize(my_input_url.getSize());
		my_runPanel.add(my_input_url);



		// make output label
		JLabel togleLabel = new JLabel();
		togleLabel.setText("Multi thread");
		togleLabel
				.setToolTipText("If checked the program will run muli threaded.");
		my_runPanel.add(togleLabel);

		my_threadTogle = new JCheckBox();
		my_threadTogle
				.setToolTipText("If checked the program will run muli threaded.");
		my_threadTogle.setSelected(true);
		my_runPanel.add(my_threadTogle);

		// make path button to do work
		my_runButton = new JButton("Run");
		my_runPanel.add(my_runButton);
		my_runButton.addActionListener(this);

		// makes the current run stop
		my_stopButton = new JButton("Stop");
		my_runPanel.add(my_stopButton);
		my_stopButton.addActionListener(this);
		my_stopButton.setEnabled(false);

		//makes the status bar
		JLabel statusLabel = new JLabel("Status", JLabel.RIGHT);
		statusLabel
				.setToolTipText("The status of the application");
		my_runPanel.add(statusLabel);
		
		my_statusLabel = new JLabel();
		my_statusLabel
				.setToolTipText("The status of the application");
		my_statusLabel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		my_statusLabel.setBackground(LABEL_COLOR);
		my_statusLabel.setText("Ready to run...     ");
		my_runPanel.add(my_statusLabel);
		
		
		this.pack();
	}

	@Override
	public void actionPerformed(ActionEvent the_event) {
		// run button presed begin a run
		if (the_event.getSource() == my_runButton) {

			Page seed = normalizeUrl();

			// ensure that input is valid
			if ( seed != null) {
				
				// get the keywords from the user
				String [] keywords = new String [my_keywordList.size()];
				int i = 0;
				for (JTextField a_keyWord : my_keywordList) {
					String temp = a_keyWord.getText().trim().toLowerCase();			
					keywords[i] = temp;
					my_curKeywordsList.add(temp);
					a_keyWord.setEnabled(false);
					i++;
				}

				if (my_threadTogle.isSelected()) {// multi threaded is enabled
					my_crawler = new CrawlerTuned();
				} else {// run single threaded
					my_crawler = new CrawlerSingle() ;
				}
				my_crawler
						.crawl(seed, keywords);
				
				my_stop.stop = false;
				runUpdater();
				my_statusLabel.setText("Running...          ");
				my_stopButton.setEnabled(true);
				my_runButton.setEnabled(false);
			}
		} else if (the_event.getSource() == my_stopButton) {
			if (my_crawler == null) {
				errorWindow("No run is active");
			} else {
				my_statusLabel.setText("Shutting down...    ");
				my_crawler.stop();
				
				my_stop.stop = true;
				my_stopButton.setEnabled(false);
				my_runButton.setEnabled(true);
				my_statusLabel.setText("Stopped...          ");
				// wait for the thread to stop.
				try {
					Thread.sleep(UPDATE_INTERVAL);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// display final output.
				displayResults();

				// re enable all labels
				for (JTextField a_keyWord : my_keywordList) {
					a_keyWord.setEnabled(true);
				}
				
				my_curKeywordsList.clear();
				
			}
		}
		this.pack();
	}
	
	private void runUpdater(){
		(new DisplayUpdateThread(my_stop)).start();
	}

	private void displayResults() {
		my_totalTimeLabel.setText((double)my_crawler.getTimeElapsed() / 1000000000.0+ "");
		int pages_paresed = my_crawler.getPagesParsed();
		
		if(pages_paresed == 0){//protect against divide by zero eror
			
			pages_paresed = 1;
		}
		
		my_pagesProccesedLabel.setText(pages_paresed + "");
		my_pageTimeLabel.setText(  DF.format(((double) my_crawler.getParseTime() / 1000000000.0) / pages_paresed )+ " ");
		my_avgUrlLabel.setText( ( "" +DF.format( (double) my_crawler.getUrlsFound()/(double) pages_paresed)) );
		my_avgWordLabel.setText((""+DF.format( (double) my_crawler.getWordCount()/(double) pages_paresed)));
		
		
		Map<String, Integer> wordCounts = my_crawler.getKeywordCounts();
		for(int i = 0; i < my_curKeywordsList.size(); i++){
			String a_word = my_curKeywordsList.get(i);
			Integer count = wordCounts.get(a_word);
			if(count != null){
				my_hitPerPage.get(i).setText("" + DF.format(((double) count / (double)pages_paresed)) );
				my_totalHit.get(i).setText("" + count);
			}else{
				System.out.print(":(");
			}

			
			
		}
		
	}

	/**
	 * Keyword pannel that creates a 11 by 3 grid that displays text fields for
	 * input and 2 labels for row
	 */
	private void keyWordPanel() {
		my_keywordPanel = new JPanel(new GridLayout(0, 3));
		my_keywordPanel.setBackground(Color.white);

		// add labels
		JLabel keywordLabel = new JLabel();
		keywordLabel.setText("Keywords");
		my_keywordPanel.add(keywordLabel);

		JLabel hitLabel = new JLabel();
		hitLabel.setText("Hits/Page");
		my_keywordPanel.add(hitLabel);

		JLabel sumHits = new JLabel();
		sumHits.setText("Total Hits");
		my_keywordPanel.add(sumHits);

		// add rows of input space
		for (int i = 0; i < NUM_KEYWORDS; i++) {
			JTextField key_text1 = new JTextField(KEYWORD_LENGTH);
			key_text1.setBackground(Color.lightGray);
			my_keywordPanel.add(key_text1);
			my_keywordList.add(key_text1);

			JLabel hitPageLabel = new JLabel();
			hitPageLabel.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.LOWERED));
			hitPageLabel.setBackground(LABEL_COLOR);
			my_keywordPanel.add(hitPageLabel);
			my_hitPerPage.add(hitPageLabel);

			JLabel hitSumLabel = new JLabel();
			hitSumLabel.setBorder(BorderFactory
					.createEtchedBorder(EtchedBorder.LOWERED));
			hitSumLabel.setBackground(LABEL_COLOR);
			my_keywordPanel.add(hitSumLabel);
			my_totalHit.add(hitSumLabel);
		}
	}

	/**
	 * @return Creates a panel with 3 buttons and 3 labels in a grid layout to
	 *         display total time, time per page, and current number of pages
	 *         paresed.
	 */
	private JPanel createStatsPane() {
		JPanel statsPane = new JPanel();
		statsPane.setBackground(Color.white);
		statsPane.setLayout(new GridLayout());

		// current pages parsed
		JLabel pagesLabel = new JLabel("Pages Paresed ",JLabel.RIGHT);
		statsPane.add(pagesLabel);

		my_pagesProccesedLabel = new JLabel();
		my_pagesProccesedLabel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		my_pagesProccesedLabel.setBackground(LABEL_COLOR);
		statsPane.add(my_pagesProccesedLabel);

		// Time per page
		JLabel pageTimeLabel = new JLabel("Time to Parse ",JLabel.RIGHT);
		statsPane.add(pageTimeLabel);

		my_pageTimeLabel = new JLabel();
		my_pageTimeLabel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		my_pageTimeLabel.setBackground(LABEL_COLOR);
		statsPane.add(my_pageTimeLabel);

		// Total elapsed time labels
		JLabel timeLabel = new JLabel("Total Time ",JLabel.RIGHT);
		statsPane.add(timeLabel);

		my_totalTimeLabel = new JLabel();
		my_totalTimeLabel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		my_totalTimeLabel.setBackground(LABEL_COLOR);
		statsPane.add(my_totalTimeLabel);

		// avg urls per page
		JLabel avgUrlLabel = new JLabel("Avg Url/page ",JLabel.RIGHT);
		avgUrlLabel.setToolTipText("Average urls found on a page.");
		statsPane.add(avgUrlLabel);

		my_avgUrlLabel = new JLabel();
		my_avgUrlLabel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		my_avgUrlLabel.setBackground(LABEL_COLOR);
		statsPane.add(my_avgUrlLabel);
		
		//words per page
		JLabel avgWordsLabel = new JLabel("Avg Words/Page ",JLabel.RIGHT);
		avgWordsLabel.setToolTipText("Average words found on a page.");
		statsPane.add(avgWordsLabel);

		my_avgWordLabel = new JLabel();
		my_avgWordLabel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		my_avgWordLabel.setBackground(LABEL_COLOR);
		statsPane.add(my_avgWordLabel);

		return statsPane;
	}

	/**
	 * Sets the frame to true.
	 */
	public void start() {
		this.setVisible(true);
	}

	/**
	 * Trims input url and attempts to make a page. If the attempt the method
	 * will display error message and return null.
	 * 
	 * @return
	 */
	private Page normalizeUrl() {
		String input = my_input_url.getText();
		input.trim();

		// checkformat
		try {
			URI seed_uri = new URI(input);
			Page seed_page = new Page(seed_uri);
			return seed_page;
		} catch (Exception e) {
			// TODO: handle exception
			errorWindow("You did not enter a valid seed addres.");
		}
		return null;

	}


	/**
	 * 
	 * @param message
	 *            The message to display on a java message dialog.
	 */
	public void errorWindow(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message,
				"Does not compute", JOptionPane.ERROR_MESSAGE);
	}
	
	
	private class DisplayUpdateThread extends Thread{
		
		private Stopbit my_stop;
		
		private DisplayUpdateThread(Stopbit the_stop){
			my_stop = the_stop;
		}
		
		public void run(){
			while(!my_stop.stop){
				
				displayResults();
				
				try {
	        Thread.sleep(UPDATE_INTERVAL);
        } catch (InterruptedException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
			}
		}
	}
}
