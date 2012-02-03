package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
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
import crawler.CrawlerMulti;
import crawler.CrawlerSingle;
import crawler.CrawlerTuned;

/**
 * 
 * @author Steven Cozart
 * @version 1.0
 */
@SuppressWarnings("serial")
public class CrawlerGUI extends JFrame implements ActionListener {

	private static final Color LABEL_COLOR = Color.BLUE;

	private static final int NUM_KEYWORDS = 10;

	public static final String DEFULT_START_PAGE = "http://en.wikipedia.org/wiki/Seppuku";

	public static final String DEFULT_SEED = "100";

	private static final int KEYWORD_LENGTH = 15;

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

	public CrawlerGUI() {
		super();
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

		// num pages input spot
		JLabel nup_pages_label = new JLabel();
		nup_pages_label.setText("Max Pages:");
		nup_pages_label
				.setToolTipText("Insert numper of pages to search.  If no input is provided "
						+ DEFULT_SEED + " pages will be searched.");
		my_runPanel.add(nup_pages_label);

		my_num_pages = new JTextField(5);
		my_num_pages.setName("input seed");
		my_num_pages.setText(DEFULT_SEED);
		my_num_pages.setBackground(Color.LIGHT_GRAY);
		my_num_pages.setSize(my_num_pages.getSize());
		my_runPanel.add(my_num_pages);

		// make path button to do work
		my_runButton = new JButton("Run");
		my_runPanel.add(my_runButton);
		my_runButton.addActionListener(this);

		// makes the current run stop
		my_stopButton = new JButton("Stop");
		my_runPanel.add(my_stopButton);
		my_stopButton.addActionListener(this);

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

		//

		this.pack();
	}

	@Override
	public void actionPerformed(ActionEvent the_event) {
		// run button presed begin a run
		if (the_event.getSource() == my_runButton) {

			int max_pages = normalizeNumPages();
			Page seed = normalizeUrl();

			// ensure that input is valid
			if (max_pages != -1 && seed != null) {

				// get the keywords from the user
				String [] keywords = new String [my_keywordList.size()];
				int i = 0;
				for (JTextField a_keyWord : my_keywordList) {
					String temp = a_keyWord.getText();
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
						.crawl(seed, keywords, max_pages);
			}
		} else if (the_event.getSource() == my_stopButton) {
			if (my_crawler == null) {
				errorWindow("No run is active");
			} else {
				my_crawler.stop();
				// display final output.
				displayResults();
				
				//re enable all labels
				for (JTextField a_keyWord : my_keywordList) {
					a_keyWord.setEnabled(true);
				}
				
				my_curKeywordsList.clear();
			}
		}
		this.pack();
	}

	private void displayResults() {
		my_totalTimeLabel.setText((double)my_crawler.getTimeElapsed() / 1000000000.0+ "");
		int pages_paresed = my_crawler.getPagesParsed();
		
		if(pages_paresed == 0){//protect against divide by zero eror
			
			pages_paresed = 1;
		}
		
		my_pagesProccesedLabel.setText(pages_paresed + "");
		my_pageTimeLabel.setText(((double) my_crawler.getParseTime() / 1000000000.0) / pages_paresed+ " ");
		my_avgUrlLabel.setText( ( "" +(double) my_crawler.getUrlsFound()/(double) pages_paresed) );
		
		Map<String, Integer> wordCounts = my_crawler.getKeywordCounts();
		for(int i = 0; i < my_curKeywordsList.size(); i++){
			String a_word = my_curKeywordsList.get(i);
			Integer count = wordCounts.get(a_word);
			if(count != null){
				my_hitPerPage.get(i).setText("" + ((double) count / (double)pages_paresed));
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
		JLabel pagesLabel = new JLabel();
		pagesLabel.setText("Pages Paresed:");
		statsPane.add(pagesLabel);

		my_pagesProccesedLabel = new JLabel();
		my_pagesProccesedLabel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		my_pagesProccesedLabel.setBackground(LABEL_COLOR);
		statsPane.add(my_pagesProccesedLabel);

		// Time per page
		JLabel pageTimeLabel = new JLabel();
		pageTimeLabel.setText("Time to Parse:");
		statsPane.add(pageTimeLabel);

		my_pageTimeLabel = new JLabel();
		my_pageTimeLabel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		my_pageTimeLabel.setBackground(LABEL_COLOR);
		statsPane.add(my_pageTimeLabel);

		// Total elapsed time labels
		JLabel timeLabel = new JLabel();
		timeLabel.setText("Total Time:");
		statsPane.add(timeLabel);

		my_totalTimeLabel = new JLabel();
		my_totalTimeLabel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		my_totalTimeLabel.setBackground(LABEL_COLOR);
		statsPane.add(my_totalTimeLabel);

		// avg urls per page
		JLabel avgUrlLabel = new JLabel();
		avgUrlLabel.setText("Avg Url/page");
		avgUrlLabel.setToolTipText("Average urls found on a page.");
		statsPane.add(avgUrlLabel);

		my_avgUrlLabel = new JLabel();
		my_avgUrlLabel.setBorder(BorderFactory
				.createEtchedBorder(EtchedBorder.LOWERED));
		my_avgUrlLabel.setBackground(LABEL_COLOR);
		statsPane.add(my_avgUrlLabel);

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

	public int normalizeNumPages() {
		String input = my_num_pages.getText();
		input.trim();
		// make sure its a number
		int result = -1;
		try {
			result = java.lang.Integer.parseInt(input);
			if (result < 0) {
				throw new NumberFormatException();
			}

		} catch (NumberFormatException e) {
			errorWindow("Max pages must be a Integer > 0");
		}
		return result;
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

}
