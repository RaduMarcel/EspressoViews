
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


public class WarningMessage extends JDialog {

	private static final long serialVersionUID = 1L;

	public static void showWarning(String derGuteRat){
		showWarningWindow(derGuteRat);		
	}
	
	private static void showWarningWindow(String theWholeMessage) {
		try {
			WarningMessage dialog = new WarningMessage(theWholeMessage);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			dialog.pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public WarningMessage(String ErrMessage) {
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
			JLabel label = new JLabel("Warning!");
			label.setBackground(new Color(144, 238, 144));
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setForeground(new Color(100, 0, 0));
			label.setFont(new Font("Times New Roman", Font.BOLD, 24));
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
