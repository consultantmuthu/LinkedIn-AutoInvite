/*
 * Copyright(c) 2014 TECHSERS(TM), Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of TECHSERS(TM).
 * Use is subject to license terms.
 */
package com.techsers.automation.linkedin.config;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.techsers.automation.linkedin.util.Fileutil;
import com.techsers.automation.linkedin.util.Logutil;

/**
 * This class is responsible to show file open dialog for user to select a
 * configuration file
 *   
 * @author mmuthukumaran
 *
 */
@SuppressWarnings("serial")
public class FileReader extends JFileChooser {

	/** FQCN */
	private String FQCN = FileReader.class.getName();

	/* Location of configuration file */
	private String configFilePath 					= null;
	
	/* We support only XLSX file */
	private static String SUPPORTED_FILE_FORMAT 	= ".xlsx";

	/* The dialog we will be using */
	JDialog customJDialog = null;

	private static String DATABASE					= System.getProperty("user.home") + "/.LIAutoInvite.properties";
	private static String CONFIG_FILE_LOCATION_KEY	= "CONFIG_FILE_LOCATION";
	
	/** The logger to use */
	Logutil logutil = Logutil.getInstance();
	
	int state = JFileChooser.ERROR_OPTION;
	
	/**
	 * Default constructor
	 */
	public FileReader() {
		super();		
	}

	/*
	 * This function will render "file open" dialog for user to select a CSV file. 
	 */
	public boolean selectFile() {
		String storedFile = getConfigFilePath();
		if (storedFile != null) {
			Logutil.getInstance().print("Use configuration file from " + storedFile + " [Y/N]");
			Scanner input = new Scanner(System.in);
			String userSelection = input.next();
			if (userSelection == null || userSelection.isEmpty() || userSelection.equalsIgnoreCase("Y")) {
				return true;
			} 
		} 
		// Default system dialog
		try { 
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
		} catch (Exception e) { 
			e.printStackTrace();
			return false;
		} 

		// Its time to show user to select his config and input 
		// file from system		
		final JFileChooser jFileChooser = new JFileChooser();
		jFileChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (JFileChooser.CANCEL_SELECTION.equals(e.getActionCommand())) {
					state = JFileChooser.CANCEL_OPTION;
					SwingUtilities.windowForComponent((JFileChooser) e.getSource()).dispose();
				} else if (JFileChooser.APPROVE_SELECTION.equals(e.getActionCommand())) {
					state = JFileChooser.APPROVE_OPTION;
					SwingUtilities.windowForComponent((JFileChooser) e.getSource()).dispose();
				}
			}
		});
		JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(true);
		dialog.setTitle("LinkedIn Auto Invite");
		dialog.setModal(true);
		dialog.add(jFileChooser);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
		switch (state) {
		case JFileChooser.APPROVE_OPTION:
			break;
		case JFileChooser.CANCEL_OPTION:
			break;
		default:
			break;

		}
		if (state == JFileChooser.APPROVE_OPTION) { 
			File file = jFileChooser.getSelectedFile(); 
			if (file.getName().endsWith(SUPPORTED_FILE_FORMAT)) { 
				setConfigFilePath(file.getPath()); 				 
			} else { 
				logutil.error(FQCN, "We support only " + SUPPORTED_FILE_FORMAT + ", other file formats are not supported");
				return false;
			} 
		} else { 
			logutil.error(FQCN, "User cancelled operation");
			return false;
		}
		return true;

	}

	/**
	 * This method is overridden from <code>JFileChooser</code> so that 
	 * we get dialog control to display it on top of other windows. With 
	 * out this function user will have to search the dialog window
	 */
	protected JDialog createDialog(Component parent) throws HeadlessException {
		logutil.log(FQCN, "Creating dialog");
		customJDialog = super.createDialog(parent);
		customJDialog.setLocation(0, 0);
		customJDialog.addWindowFocusListener( new WindowFocusListener(){
            public void windowGainedFocus( WindowEvent e ){}
            public void windowLostFocus( WindowEvent e ){
                if( !(e.getOppositeWindow() instanceof JDialog) ){
                	customJDialog.toFront();
                }
            }
        });

        return customJDialog;
    }
	
	/**
	 * @return - Returns configuration file selected by user
	 */
	public String getConfigFilePath() {
		if (configFilePath == null) {
			// Check whether we have it in user home
			try {
				configFilePath = Fileutil.getStringProperty(DATABASE, CONFIG_FILE_LOCATION_KEY);
			} catch (Exception e) {
				// e.printStackTrace();
				configFilePath = null;
			}
		}
		return configFilePath;
	}

	/**
	 * Use <code>readCsvFile</code> function to set configuration file path
	 * 
	 * @param configFilePath
	 */
	private void setConfigFilePath(String configFilePath) {
		try {
			Fileutil.deleteFile(DATABASE);
			Fileutil.putStringProperty(DATABASE, CONFIG_FILE_LOCATION_KEY, configFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.configFilePath = configFilePath;
	}
}
