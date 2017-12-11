import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Dialog.ModalityType;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AboutWindow extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private static Logger logger = LogManager.getLogger(AboutWindow.class.getName());
//	public static void main(String[] args) {
//		try {
//			AboutWindow dialog = new AboutWindow();
//			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	public AboutWindow() {
		setType(Type.POPUP);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setTitle("About Espresso Views");
		getContentPane().setBackground(Color.WHITE);
		setBounds(100, 100, 637, 403);
		getContentPane().setLayout(new BorderLayout());
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.addTab("About", null, contentPanel, null);
		tabbedPane.setEnabledAt(0, true);
		
		contentPanel.setFont(new Font("Times New Roman", Font.PLAIN, 16));
		contentPanel.setBackground(Color.WHITE);
		contentPanel.setBorder(null);
		//getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JEditorPane dtrpnhalloconvertMe = new JEditorPane();
		dtrpnhalloconvertMe.setContentType("text/html");
		dtrpnhalloconvertMe.setText("<html>\r\n</head>\r\n" +
								    "<body style=\"background: transparent\">\r\n" +
								    "<p align=\"center\" style=\"margin-bottom: 0cm; line-height: 100%; text-decoration: none\">\r\n<font face=\"Times New Roman, serif\"><font size=\"6\" style=\"font-size: 24pt\"><b>Espresso\r\nViews</b></font></font>" +
								    "</p>\r\n<p align=\"center\" style=\"margin-bottom: 0cm; line-height: 100%; text-decoration: none\">\r\nVersion 0.5 \r\n" +
								    "</p>\r\n<p align=\"center\" style=\"margin-bottom: 0cm; line-height: 100%; text-decoration: none\">\r\nlast changed: Marburg, Germany, Oktober 2017</p>\r\n" +
								    "<p align=\"center\" style=\"margin-bottom: 0cm; line-height: 100%; text-decoration: none\">\r\nCopyright <font face=\"Liberation Serif, serif\">&copy; Radu-Marcel\r\nDumitru</font>" +
								    "</p>\r\n<p align=\"center\" style=\"margin-bottom: 0cm; line-height: 100%; text-decoration: none\">\r\n<font face=\"Liberation Serif, serif\">" +
								    "This program is free software; you can redistribute it and/or modify it under the terms of the GNU GENERAL PUBLIC LICENSE, Version 3 as published by the Free Software Foundation on 29 June 2007</font></p>"+
								    "</p>\r\n<p align=\"center\" style=\"margin-bottom: 0cm; line-height: 100%; text-decoration: none\">\r\n<font face=\"Liberation Serif, serif\">" +
								    "This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.</font></p>\r\n</body>\r\n</html>" 
				);
		
		dtrpnhalloconvertMe.setBounds(0, 0, 616, 304);
		dtrpnhalloconvertMe.setEditable(false);
		contentPanel.add(dtrpnhalloconvertMe);
		{
			JScrollPane panel = new JScrollPane();
			tabbedPane.addTab("Licence", null, panel, null);
				BufferedReader file=null;
				StringBuffer licenseText =	new StringBuffer("");
				try {
					file = new BufferedReader (new FileReader(new File("LICENSE")));
				while (file.ready()) {	
					licenseText.append(new String( file.readLine())+"\n" );
				   }
			} 
		    catch (FileNotFoundException e) {
		    	licenseText.append("You should have received a copy of the GNU General Public License along with this program;\n if not, see <http://www.gnu.org/licenses/>.");
		    	logger.error("File LICENCE not found\n"+ErrorMessage.showStackTrace (e.getStackTrace()) );
				}
			catch (IOException e) {
		    	licenseText.append("You should have received a copy of the GNU General Public License along with this program;\n if not, see <http://www.gnu.org/licenses/>.");
		    	logger.error("IO Exception occured while trying to load the file LICENCE\n"+ErrorMessage.showStackTrace (e.getStackTrace() )); 	
				}	
				JTextArea txtrTralala = new JTextArea();
				txtrTralala.setText(licenseText.toString());
				txtrTralala.setEditable(false);
				panel.setViewportView(txtrTralala);
				txtrTralala.setCaretPosition(0);
				panel.setBackground(Color.WHITE);
				{
					JTextArea textArea = new JTextArea();
					panel.setColumnHeaderView(textArea);
				}
				{
					JTextArea textArea = new JTextArea();
					textArea.setFont(new Font("Liberation Serif", Font.PLAIN, 14));
					textArea.setText("                         ");
					panel.setRowHeaderView(textArea);
				}		
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBackground(Color.WHITE);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
}
