package com.brett.gadsbymain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Manager for saving and opening files.
 * @author brett
 *
 */
public class GadsbyFileManager {
	final static String SAVE = "SAVE";
	final static String OPEN = "OPEN";
	
	//open a dialog box to get a file to open or save
	public static File getFile(String option) {
		JFileChooser fc = new JFileChooser();
		
		int result = -1;
		if(option == SAVE) {
			result = fc.showSaveDialog(null);
		} else if (option == OPEN) {
			result = fc.showOpenDialog(null);
		}
		
		File file = null;
		if(result == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
		}
		
		return file;
	}
	
	//write the contents to a file
	public static void saveToFile(File file, String contents) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		out.print(contents);
		out.close();
	}
	
	//return the contents of a file
	public static String getFileContents(File file) throws FileNotFoundException {
			BufferedReader in = new BufferedReader(new FileReader(file));
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				while((line = in.readLine()) != null) {
					sb.append(line + "\n");
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error reading file contents.");
			}
			
			return sb.toString();

	}
}
