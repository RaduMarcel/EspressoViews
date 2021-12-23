import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class ErrorMessage extends JDialog {


	private static final long serialVersionUID = 1L;

	public static void showException(String derGuteRat){
		showExceptionWindow(derGuteRat);		
	}
	
	public static void showException(Throwable ex, String derGuteRat){	
	StringBuilder allMessages = new StringBuilder("");
		StackTraceElement[] stacky=	ex.getStackTrace();
		for (int t=0;t<stacky.length && t<=6;t++ ){
			if (t<6)
			allMessages.append(stacky[t].toString()+"\n");
			else allMessages.append("..."+"\n"); 
		}		
		String theWholeMessage=derGuteRat+"\n"+ex.getMessage()+"\n"+allMessages.toString();		
		showExceptionWindow(theWholeMessage);
	}
	
	private static void showExceptionWindow(String theWholeMessage) {
		try {
			ErrorMessage dialog = new ErrorMessage(theWholeMessage);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			dialog.pack();
			if (dialog.isAlwaysOnTopSupported() ) dialog.setAlwaysOnTop(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static String showStackTrace( StackTraceElement[] stacky){
		StringBuilder allMessages = new StringBuilder();
		for (int t=0;t<stacky.length;t++ )
						allMessages.append(stacky[t].toString()+"\n");
		return allMessages.toString();	
	}
	
	public ErrorMessage(String ErrMessage) {
		setBounds(100, 100, 650, 500);
		this.getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBackground(new Color(144, 238, 144));
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("OK");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				cancelButton.setFont(new Font("Times New Roman", Font.BOLD, 12));
				cancelButton.setHorizontalAlignment(SwingConstants.LEFT);
				buttonPane.add(cancelButton);
			}
		}
		{
			JLabel label = new JLabel("ERROR!");
			label.setBackground(new Color(144, 238, 144));
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setForeground(new Color(139, 0, 0));
			label.setFont(new Font("Times New Roman", Font.BOLD, 20));
			getContentPane().add(label, BorderLayout.NORTH);
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			{
				JTextArea txtErrMessage = new JTextArea();
				txtErrMessage.setText(ErrMessage);
				txtErrMessage.setFont(new Font("Monospaced", Font.PLAIN, 13));
				txtErrMessage.setBackground(new Color(144, 238, 144));
				scrollPane.setViewportView(txtErrMessage);
				txtErrMessage.setCaretPosition(0);
				txtErrMessage.setEditable(false);
			}
		}
	}
}
