package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * 
 * @author Steven Cozart
 * @version 1.0
 */
@SuppressWarnings("serial")
public class CrawlerGUI extends JFrame implements ActionListener {

	private static final int NUM_KEYWORDS = 10;

	public static final String DEFULT_START_PAGE = "www.joindota.com";

	public static final String DEFULT_SEED = "100";

	private static final int KEYWORD_LENGTH = 15;

	private JPanel my_runPanel;

	private JTextField my_input_url;

	private JTextField my_num_pages;

	private JButton my_runButton;

	private JLabel my_output;

	private JButton my_stopButton;

	private JPanel my_keywordPanel;

	private ArrayList<JTextField> my_keywordList;

	private ArrayList<JLabel> my_hitPerPage;

	private ArrayList<JLabel> my_totalHit;

	private JPanel my_contentPane;

	public CrawlerGUI() {
		super();
		
		my_contentPane = new JPanel();
		my_contentPane.setLayout(new BorderLayout());
		this.setContentPane(my_contentPane);
		
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

		// adds panel to the window
		my_runPanel = new JPanel(new FlowLayout());
		my_contentPane.add(my_runPanel,BorderLayout.SOUTH);
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
		my_input_url.setBackground(Color.GRAY);
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
		my_num_pages.setBackground(Color.GRAY);
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
		my_output = new JLabel();
		my_output.setFont(new Font("Helvetica", Font.BOLD, 14));
		my_runPanel.add(my_output);

		this.pack();
	}

	@Override
	public void actionPerformed(ActionEvent the_event) {
		if (the_event.getSource() == my_runButton) {
			System.out.print("run button pressed");

		} else if (the_event.getSource() == my_stopButton) {
			System.out.print("Stop button pressed");
		}
		this.pack();
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
			key_text1.setBackground(Color.GRAY);
			key_text1.setBorder(border);
			my_keywordPanel.add(key_text1);
			my_keywordList.add(key_text1);

			JLabel hitPageLabel = new JLabel();
			hitPageLabel.setBackground(Color.green);
			my_keywordPanel.add(hitPageLabel);
			my_hitPerPage.add(hitPageLabel);

			JLabel hitSumLabel = new JLabel();
			hitSumLabel.setBackground(Color.green);
			my_keywordPanel.add(hitSumLabel);
			my_totalHit.add(hitSumLabel);
		}
	}

	public void start() {
		this.setVisible(true);
	}

	public String normalizeUrl() {
		String input = my_input_url.getText();
		input.trim();

		// checkformat

		return input;
	}

	public int normalizeNumPages() {
		String input = my_num_pages.getText();
		input.trim();
		// make sure its a number
		int result = -1;
		try {
			result = java.lang.Integer.parseInt(input);

		} catch (NumberFormatException e) {

		}
		return result;
	}

	public void errorWindow(String message) {

	}

}
