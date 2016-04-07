package com.brett.gadsbymain;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DocumentFilter;

/**
 * GUI for the word processor
 * @author brett
 *
 */
public class GadsbyGUI extends JFrame implements ActionListener{
	
	
	private DefaultHighlighter.DefaultHighlightPainter highlighter = 
			new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
	
	private JTextArea text;		//text area where user types
	private JScrollPane scroll;		//add a scroll panel for long documents
	
	private JMenuBar menuBar;	
	private JMenu menuFile;
	private JMenuItem menuSaveItem, menuSaveAsItem, menuOpenItem;
	
	private File file = null;		//the current file to save to
	public GadsbyGUI() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("Gadsby Word Processor");
		
		/**Set up the menu bar**/
		menuBar = new JMenuBar();
		menuFile = new JMenu("File");
		menuBar.add(menuFile);

		
		menuSaveItem = new JMenuItem("Save");
		menuSaveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menuSaveItem.addActionListener(this);
		
		menuSaveAsItem = new JMenuItem("Save As...");
		menuSaveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
		menuSaveAsItem.addActionListener(this);
		
		menuOpenItem = new JMenuItem("Open...");
		menuOpenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menuOpenItem.addActionListener(this);
		
		menuFile.add(menuSaveItem);
		menuFile.add(menuSaveAsItem);
		menuFile.add(menuOpenItem);
		menuBar.add(menuFile);
		this.setJMenuBar(menuBar);
		
		
		/**Set up the text area**/
		text = new JTextArea(30, 60);
		text.setLineWrap(true);
		((AbstractDocument) text.getDocument()).setDocumentFilter(new HighlightDocFilter());
		text.getDocument().addDocumentListener(new GadsbyDocListener());

		
		//Add a scroll panel
		scroll = new JScrollPane(text, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		//create the window
		this.add(scroll);
		this.pack();
		this.setVisible(true);
	}

	
	/**
	 * Document listener for highlighting e's when text is copied and pasted
	 * @author brett
	 *
	 */
	private class GadsbyDocListener implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			
			//copy and pasting, have to look and highlight more than one character
			if(e.getLength() > 1) {
				//speed up, only look at inserted text
				scanAndHighlight();
			}
			
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
		}
		
	}
	
	/**
	 * Highlight e's while the user is typing
	 * @author brett
	 *
	 */
	private class HighlightDocFilter extends DocumentFilter {

		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
				throws BadLocationException {
			super.replace(fb, offset, length, text, attrs);
			String match = "e";
			int startIndex = offset - 1;
			
			//if the user just typed an 'e', highlight it
			if(startIndex >= 0) {
				String last = fb.getDocument().getText(startIndex, 1);
				if(last.equalsIgnoreCase(match)) {
					highlightE(startIndex, highlighter);
				}
			}
		}
		
	}

	private void highlightE(int startIndex, DefaultHighlighter.DefaultHighlightPainter highlighter)
			throws BadLocationException {
		text.getHighlighter().addHighlight(startIndex, startIndex + 1, highlighter);
		
	}
	
	//scan the whole document and highlight
	private void scanAndHighlight() {
		String theText = text.getText();
		for(int i = 0; i < theText.length(); i++) {
			if(theText.charAt(i) == 'e') {
				try {
					highlightE(i, highlighter);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//handle the Save, Save as, and Open features
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(menuOpenItem)) {
			file = GadsbyFileManager.getFile(GadsbyFileManager.OPEN);
			
			//open the file and highlight the e's
			if(file != null) {
				try {
					text.setText(GadsbyFileManager.getFileContents(file));
					scanAndHighlight();
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(null, "File not found!");
				}
			}
		} else if (e.getSource().equals(menuSaveItem)) {
			
			//user hasn't saved yet, find a new place to save to
			if(file == null) {
				saveAs();
			} 
			
			//user has already saved, write the contents to that file
			else {
				try {
					GadsbyFileManager.saveToFile(file, text.getText());
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Could not save file");
				}
			}
		}  else if(e.getSource().equals(menuSaveAsItem)) {
			saveAs();
		} 
		
	}
	
	
	//get a new file to write the contents of the text area to
	private void saveAs() {
		
		File currFile = GadsbyFileManager.getFile(GadsbyFileManager.SAVE);
		
		if(currFile != null) {
			file = currFile;
			if(!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Could not create new file.");
				}
			}
			try {
				GadsbyFileManager.saveToFile(file, text.getText());
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Could not save file.");
			}
			
		}
	}

}
