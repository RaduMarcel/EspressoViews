//import java.awt.BorderLayout;
//import java.awt.FlowLayout;
//import javax.swing.JButton;
import javax.swing.JDialog;
//import javax.swing.JPanel;
//import javax.swing.border.EmptyBorder;
//import javax.swing.filechooser.FileFilter;
//import javax.swing.text.BadLocationException;
//import javax.swing.JTextField;
//import javax.swing.DropMode;
//import javax.swing.SwingConstants;
//import java.awt.Color;
//import javax.swing.GroupLayout;
//import javax.swing.GroupLayout.Alignment;
//import javax.swing.LayoutStyle.ComponentPlacement;
//import javax.swing.JPasswordField;
//import javax.swing.JLabel;
//import javax.swing.UIManager;
//
//import java.awt.Font;
//import javax.swing.AbstractAction;
//import java.awt.event.ActionEvent;
//import javax.swing.Action;
//import java.awt.Cursor;
//import javax.swing.JFileChooser;
//import javax.swing.JComboBox;
//import javax.swing.DefaultComboBoxModel;
//
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//
//import java.awt.event.ItemEvent;
//import java.awt.event.ItemListener;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.io.File;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
/**
 * TODO

- Den tag <RollUp> column_name_1,column_name_2,... </RollUp> einf�hren !!!

- Als subQuery soll der locator einer weiteren XML-Definitionsdatei angegeben werden k�nnen, die dann nat�rlich selbst andere subqueries haben kann OK
  Zus�tzliche Option: die letzte SQLdefinition ist der neue parent f�r alle untergeordnenten SQLdefinitonen !!    
  
- Im tag <SQLQuery>  sollte ein file locator angegeben werden k�nnen, damit grosse SQLs die �bersichtlichkeit der Definition nicht beeintr�chtigen!!

- kompakte Dastellung des Baumes implemetieren. Minimalimplementierung OK
  Eigene Methode implementieren, um mehr auszugestalten	!
  
- Namen der tempor�ren Tabellen nach dem Erzeugen im ini file schreiben + die dazugeh�rige db connection. Nach dem Droppen auch von dort wieder l�schen (anstelle des createdTables Set). 
  Damit k�nnten nicht gedroppte temp tables auch nach einem Programmneustart nachtr�glich noch gedroppt werden!!      

- Wenn innerhalb einer SQLQueryDefinition oder einer DBConnectionDefinition ein Tag auftaucht, der nicht bekannt ist, dann k�nnte man dies in einer Warningmessage kundgeben!

Swinglastig: 

- Ein Container implementieren der diese Swing-ObjeKte enth�lt: Login dialog und die Baumfenstern, die es zu jedem Zeitpunkt gibt. 
  Er muss dann listeners haben, die bemerken, wenn ein solches Objekt disposed wird !!!
   
- Neuer Tag <AskForValues> column_name_1,column_name_2,... </AskForValues>.!!! 
  Variablen in der SQL definition einf�hren, die dann vor der Baumerzeugung durch ein pop-up fenster von dem User abgefragt werden 
  AND und OR und NO Tund () anbieten
  Eingaben mit operatoren in der Anfragedefinition speichern und das Formular bei Neuanwendung wieder damit bef�llen

- 	Login-Fenster erweitern, 
	SID oder Service_Name anw�hlen k�nnen !!
	Passwort nach Eingabe enkrypten OK
  
-  Das Logindialog soll aber nach dem Erzeugen des Datenfensters nie diposed werden, sondern nur invisible. OK. 
  Wenn das Datenfenster geschlossen wird, soll das Logindialog wieder visible werden. OK  
  Aber wenn das Datenfenster mit dem X-Button geschlossen wird, bleibt das Logindialog denoch invisible. X-Button wurde ausgeschlatet, aber dies ist nicht die beste L�sung !!   

Sonst:
- Tutorial schreiben !!!

- icon kreieren !!

- Lizenz !!
 
BUGS
 
- Null als columnangabe wird nicht akeptiert!
	Momentan ist es so:
	create table xx as select NULL as nix  from dual
	Wenn ein column as null angegeben wird, wei� der create table statement nicht was f�r ein Datentyp das null-Feld sein soll. 
	so aber geht's:
	create table xx as select  cast(null as integer) as nix from dual

- Nach reload sollte der Focus auf dem Baum sein !

ZUKUNFTSMUSIK

- Verbindungen zu anderen Datenbanken als Oracle erlauben !!
  Als erstes: MySQL

- Als Definitionsdatei soll auch eine Golden-Workspace oder Toad oder SQL-Developer Workspace angegeben werden k�nnen. Diese wird dann in XML-Format umgewandelt.

- Logging level in ini file einf�hren, triggert log4J Funktionen. Zeiten messen, um Prozesse zu optimieren

- Tooltips mit Infos zur jeweiligen Tabellenzelle 

- Baumdaten speichern und wieder als Baum laden k�nnen.

- Der User soll sich Schriftart, Schriftgr��e und Farbe selbst aussuchen k�nnen. 

- Bei Rechtsklick auf ein Baumelement Edit-Formular f�r die entsprechende Anfragedefinition einblenden/Teilfenster �ffnen.
  Modifizierte Anfragedefinition nach Verlassen des Fensters anwenden.       

- Baumdaten als insert und/oder delete speichern k�nnen.


-------------------------------DONE!-------------------------------

- TransmittedConditions nicht mehr mit select distinct ermitteln sondern mit Java code aus der Datenmatrix OK

- Algorithmus f�r DataNode.determinetMaxColumnLength �berpr�fen OK

- Wenn keine transmitted columns angegeben werden, dann sollen alle Kriterien UND-Verkn�pft angewendet werden, die die subquery mit der superquery gemeinsam hat OK

- wenn eine inherited Condition nicht anwedbar ist, weil er an die maxinheritanceDepth-Bedingung scheitert, 
  dann soll dieser auch als nicht-anwerdbar in dem Structure-Modus angezeigt werden OK      

- Definitionstrukturdarstellung implementieren OK

- last directory in ini setting speichern und bei jedem programmstart setzen OK

- Warning-Strenge in ini file einf�hren OK. Der tag <WarningLevel> wurde im inisetting eingef�hrt. die m�glichen Werte sind low und high  

- Selectierte Definitionfile ohne connectiondetaily wird rot OK

- number separator richtet sich nach den settings des clients, soll sich nach den settings des users oder der instanz richten OK

- Nach reload wird der vorherige Baum anscheinend nicht disposed, memory wird unn�tig besetzt OK

- About menu item mit version control schreiben OK
- einen weiteren Tab im About einf�hren mit GPL licence agreement OK

- vollenden inisetting.xml funktionalit�t, OK
	inisetting.xml laden OK
	inisetting.xml speichern OK
	neue Verbindungsdetails sollten sich mit eigenem Verbindungsnamen speichern lassen OK
	Wenn es ein ini-setting file nicht gibt, muss automatisch eins angelegt werden OK 
	Error Message einblenden, wenn in der ini-setting Datei nicht gespeichert werden kann OK
	
in der Darstellung
- SQLs mit leerer Ergebnissmenge werden mit einer leeren Ergebniszeile dargestellt OK
- Ausgefahrene Labels, mit dunkelroter Schrift markieren OK 
- Toolbar in Baumfenstereinf�hren  OK
- den button Reload in der Toolbar einf�hren, der den Baum anhand derselben Definitionsdatei wieder erzeugt. 
Und/oder einen dropdown-menu bei rechtsklick einf�hren und Reload-Funktion anbieten (Reload auch mit F5) OK
- wenn ein Baumknoten ausgefahren wird und er hat nur ein Kindknoten, dann soll auch das Kindknoten ausgefahren werden und focus soll auf den ersten Enkel gehen  OK

- Durch ein zweites Fenster (schiebbar) den frame teilen und im unteren Teil warnings OK und statistics ablegen OK

- Neue Option in der transaction definintion einf�hren: Display no label if resultset is null OK    

Abfangen m�glicher Fehler im definition file: OK 
- kein Label: Error  OK
- Label nicht unique Error OK 
- keine Anfrage: Error OK
- erste Anfrage hat eine SuperQuery: Error OK
- nicht-Erste Anfrage hat keine SuperQuery: Error OK
- angegebene superQuery wurde noch nicht intstanziiert Fehler: Error OK

Validations:
- eine oder mehrere transmittedColumns sind nicht ein selektiertes Feld Error OK
- Zirkularit�t im subquery locator und parent definition file, Error OK 
- eine oder mehrere inheritedColumns sind nicht unter den selektierten Felder : 
            wende nur die an, die angewendet werden k�nnen, aber warne, wenn keine Column angewendet werden kann OK     
- wenn eine Anfrage kein Kriterium von der Oberanfrage �bernimmt: 
	kein Fehler, die Anfrage erf�hrt keine Einschr�nkung und jede Ergebniszeile aus der Oberanfrage hat als Kind das gesamte ResultSet der Unterabfrage OK.
- einige oder alle angegebenen resultColumns werden in der Anfrage nicht selektiert - Warnung OK
- wenn die nicht-unterste Query ein null-resultset hat: kein Fehler, Baumpfad endet dort OK         

- RAM-Belegung erh�ht sich mit jeder Baumerzeugung. OK. Memory leak gefunden. gell�st durch frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

Ausdruckm�glichkeit erweitern bei transmittedConditionColumns: 
	- nicht anwendbare Bedingungen werden ignoriert und im eigenem Erbe weitergereicht OK 
	- Oder-Verkn�pfung zulassen oder gleich logische operatoren einf�hren. 
	  Mindestens eines der order-verkn�pften Felder soll, sofern sie im schema der Kinder exitiert, angewandt werden.!
	- Anfrageergebnis im temptable speichern und dann nach gebrauch droppen, temptable muss unique pro user sein (Mehrbenutzermodus) OK       

- order by im den SQLs mit parent funktioniert nicht -- nicht OK
- ResultColumns Funktionalit�t implementieren OK
- Funktionalit�t mit not: funktioniert nur beim ersten Strang OK

- wenn der Wert der transmitted column null ist und die column anwendbar ist von den Kindern, dann wirkt sich der Bedingung "ist gleich null" nicht einschr�nkend aus, so als ob die transmitted column nicht anwendbar w�re. 
OK null-Werte als Erbe setzen sich nie fort. 

*/

public class DataBaseLogin extends JDialog {

//	private static final long serialVersionUID = 1L;
//	private static DataBaseLogin dialog;
//	private final JPanel contentPanel = new JPanel();
//	private JTextField textUserName;
//	private JTextField textHost;
//	private JPasswordField passwordField;
//	private final JComboBox<String> comboBoxStoredConns;
//	private JTextField txtServiceName;
//	private final Action action = new SwingAction();
//	private final Action action_1 = new SwingAction_1();
//	private static File lastDir= new File("");
//	protected static String warningLevel="HIGH";
//	private static String selectedFile;
//	private static List<Map<String,String>> connectionDetails=null; 
//	private static LinkedList<String> storedFileLocators;
//	private static final String iniFile ="iniSettings.xml"; 
//	/**
//	 * Launch the application.
//	 */
//	public static void main(String[] args) {
//		
//		try {
//			File iniFileLocator = new File (iniFile);
//			//System.out.println(iniFileLocator.getAbsolutePath());
//			if (!iniFileLocator.exists()){
//				try {
//					iniFileLocator.createNewFile();
//				} catch (Exception e) {
//					ErrorMessage.showException(e,"Probleme bei der Erzeugung der Datei iniSettings.xml");
//				}
//				if (iniFileLocator.exists())
//				WarningMessage.showWarning("Die Datei iniSettings.xml konnte nicht gefunden werden. \nEs wurde eine neue Datei erzeugt: "+iniFileLocator.getAbsolutePath());
//			} 
//			
//			LinkedList<Map<String,String>> fileLocators = QueryDefinitionParser.loadFileLocators(iniFile);
//			storedFileLocators = new LinkedList<String>();
//			if (fileLocators.size()>0){
//				for (int t=0; t<fileLocators.size();t++ ){
//					int tt=0;
//					while (storedFileLocators.size()<fileLocators.get(t).size() && storedFileLocators.size()<11 && tt<11){	
//					if (fileLocators.get(t).get("FILELOCATOR"+tt)!=null){	
//						storedFileLocators.add(fileLocators.get(t).get("FILELOCATOR"+tt));
//					}
//					tt++;
//					//System.out.println(fileLocators.get(t).get("FILELOCATOR"+tt));
//					}
//				}
//			}
//			
//			LinkedList<Map<String,String>> iniSettings = QueryDefinitionParser.loadIniSettings(iniFile);
//			String lastDirTag="";
//			String warningLevelTag="";
//			if (iniSettings.size()>0){
//				for (int t=0; t<iniSettings.size();t++ ){
//					lastDirTag=iniSettings.get(t).get("LASTDIR");
//					warningLevelTag=iniSettings.get(t).get("WARNINGLEVEL");
//				}
//			}
//			if (lastDirTag!=null && lastDirTag.length()>0 && new File(lastDirTag).isDirectory()) 
//				 lastDir= new File(lastDirTag);
//			if (warningLevelTag!=null && warningLevelTag.length()>0 && (warningLevelTag.toUpperCase().equals("LOW")||warningLevelTag.toUpperCase().equals("HIGH")) )
//				warningLevel=warningLevelTag.toUpperCase();
//			else 
//				QueryDefinitionParser.storeIniSettings( iniFile, "WARNINGLEVEL","HIGH");
//			
//			System.out.println(Arrays.toString(UIManager.getInstalledLookAndFeels()));
//			 UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//			dialog = new DataBaseLogin();
//			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
//			dialog.setVisible(true); 
//		} catch (Exception e) {
//			ErrorMessage.showException(e,"Ups! Ein unerwarteter Fehler.");
//		}
//	}
//	/**
//	 * Create the dialog.
//	 */
//	public DataBaseLogin() {
//		setTitle("Database Log-in");
//		setBounds(100, 100, 638, 397);
//		getContentPane().setLayout(new BorderLayout());
//		contentPanel.setBackground(new Color(153, 153, 204));
//		contentPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
//		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
//		getContentPane().add(contentPanel, BorderLayout.CENTER);
//		{
//			textUserName = new JTextField();
//			textUserName.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//			textUserName.setHorizontalAlignment(SwingConstants.LEFT);
//			textUserName.setDropMode(DropMode.INSERT);
//			textUserName.setColumns(25);
//			textUserName.setBackground(new Color(204, 204, 102));
//		}
//		{
//			textHost = new JTextField();
//			textHost.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//			textHost.setActionCommand("");
//			textHost.setHorizontalAlignment(SwingConstants.LEFT);
//			textHost.setDropMode(DropMode.INSERT);
//			textHost.setColumns(25);
//			textHost.setBackground(new Color(204, 204, 102));
//		}
//		
//		passwordField = new JPasswordField();
//		passwordField.setBackground(new Color(204, 204, 102));
//		
//		JLabel lblNewLabel = new JLabel("User Name");
//		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
//		
//		JLabel lblHostUrl = new JLabel("Host URL");
//		lblHostUrl.setFont(new Font("Tahoma", Font.BOLD, 11));
//		
//		txtServiceName = new JTextField();
//		txtServiceName.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
//		txtServiceName.setAutoscrolls(false);
//		txtServiceName.setHorizontalAlignment(SwingConstants.LEFT);
//		txtServiceName.setDropMode(DropMode.INSERT);
//		txtServiceName.setColumns(25);
//		txtServiceName.setBackground(new Color(204, 204, 102));
//		
//		JLabel lblServiceName = new JLabel("Service Name");
//		lblServiceName.setFont(new Font("Tahoma", Font.BOLD, 11));
//		
//		JLabel lblPassword = new JLabel("Password");
//		lblPassword.setFont(new Font("Tahoma", Font.BOLD, 11));
//		
//		JLabel lblQueryDefintion = new JLabel("Stored Connections ");
//		lblQueryDefintion.setFont(new Font("Tahoma", Font.BOLD, 11));
//		
//		comboBoxStoredConns = new JComboBox();
//		//Auswahl von gespeicherten DB-Verbindungen. Eine definition file kann mehrere DB-Verbindungen vorlegen. Die gefundenen DB-Verbindungen werden in dieser ComboBox �bergeben.    
//		comboBoxStoredConns.addItemListener(new ItemListener() {
//			public void itemStateChanged(ItemEvent arg0) {// Wird aktiviert wenn der Benutzer in der ComboBox-Liste etwas ausw�hlt 
//				//System.out.println("Selected Item  "+(String)(comboBoxStoredConns.getSelectedItem()) );
//				if (connectionDetails!=null) //enth�lt Verbingsdetails, so wie sie im ausgew�hlten definition file vorgefunden werden 
//				for (int t=0; t<connectionDetails.size();t++ ){
//					if (connectionDetails.get(t).get("CONNECTIONNAME").equals( (String)(comboBoxStoredConns.getSelectedItem()) ) ){
//					textHost.setText(connectionDetails.get(t).get("HOST"));
//					txtServiceName.setText(connectionDetails.get(t).get("SERVICENAME"));
//					textUserName.setText(connectionDetails.get(t).get("USERNAME"));
//					}
//				}
//	
//			}
//		});
//		comboBoxStoredConns.setBackground(new Color(204, 204, 102));
//		comboBoxStoredConns.setModel(new DefaultComboBoxModel<String>(new String[]{""})); 
//		comboBoxStoredConns.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
//		comboBoxStoredConns.setEditable(false); 
//
//		final JComboBox<String> comboBoxFileDef = new JComboBox<String>();
//		//In dieser ComboBox gibt man den file locator der file definition ein oder man w�hlt einen der locators aus, der im inisetting file vorgegeben wurde.
//		if (storedFileLocators.size()>0){
//			DefaultComboBoxModel<String> theModel = new DefaultComboBoxModel( storedFileLocators.toArray() );
//			theModel.insertElementAt("", 0);
//			comboBoxFileDef.setModel(theModel);
//			comboBoxFileDef.setSelectedIndex(0);
//		}
//		comboBoxFileDef.setEditable(true);
//		comboBoxFileDef.setBackground(new Color(204, 204, 102));
//		comboBoxFileDef.addItemListener(new ItemListener() {
//			public void itemStateChanged(ItemEvent arg0) {
//				if ( comboBoxFileDef.getSelectedItem() !=null ){// wenn der Benutzer in der ComboBox-Liste eine definition file ausw�hlt oder es erst eingibt und dann ausw�hlt...
//					File storedLocator = new File ( (String)(comboBoxFileDef.getSelectedItem()) ); 
//					if (!storedLocator.exists()) 
//						comboBoxFileDef.setBackground(Color.RED);//gibt's die Datei nicht wird die comboBox rot  					
//					//System.out.println("comboBox_1.getSelectedItem() ="+comboBoxFileDef.getSelectedItem()+"| storedLocator ="+storedLocator+"|"+storedLocator.exists() );
//					if ( (selectedFile == null || !storedLocator.getAbsolutePath().equals(new File (selectedFile)) ) &&  storedLocator.exists() ){
//						//...und wenn die Auswahl zu einer existierenden Datei hinf�hrt, die nicht die aktuell ausgew�hlte Datei ist...
//						comboBoxFileDef.setBackground(new Color(204, 204, 102));
//						selectedFile=storedLocator.getAbsolutePath();
//						connectionDetails = QueryDefinitionParser.loadConnectionDetails(storedLocator.getAbsolutePath()); //...werden erstmal die Verbindungsdetails samt Bezeichner werden aus der definition file ausgelesen
//						//QueryDefinitionParser.showMeTheDoc(QueryDefinitionParser.getXmlDocument(storedLocator.getAbsolutePath()), 1 );
//						String[] storedConnectionNames = new String[connectionDetails.size()];//und angenommen es gibt Verbindungsdetails in dieser definition file...
//						comboBoxStoredConns.setEditable(true);
//						for (int t=0; t<connectionDetails.size();t++ ){
//							storedConnectionNames[t]= connectionDetails.get(t).get("CONNECTIONNAME");}//...wird eine Liste mit den Bezeichnern aller gefundeden DB-Verbindungen erstellt   
//						if (storedConnectionNames.length>0)
//							{
//							comboBoxFileDef.setBackground(new Color(204, 204, 102));
//							comboBoxStoredConns.setModel(new DefaultComboBoxModel(storedConnectionNames));//und die Liste der DB-Verbingsbezeichner ist jetzt die neue liste derVerbindungsdetails-ComboBox
//							for (int t=0; t<connectionDetails.size();t++ ){//Doch damit nicht genug, die erste Vebindung wird auf die login-Felder verteilt  
//								if (connectionDetails.get(t).get("CONNECTIONNAME").equals((String)(comboBoxStoredConns.getSelectedItem()) )  ) {
//									textHost.setText(connectionDetails.get(t).get("HOST"));
//									txtServiceName.setText(connectionDetails.get(t).get("SERVICENAME"));
//									textUserName.setText(connectionDetails.get(t).get("USERNAME"));
//								}
//							}
//							//wenn die definition file neu ist, also vom Benutzer eingegeben wurde, kommt sie in die defnition file locators Liste rein, die dann in inisettng.xml gespeichert wird
//							if (!storedFileLocators.contains(storedLocator.getAbsolutePath()) ) 
//								storedFileLocators.push(storedLocator.getAbsolutePath());
//							      
//						}else {
//							String[] emptyString = {" "};
//							//comboBoxFileDef.setBackground(Color.RED);//gibt die definition file keine DB-Verbindungsdetails her, dann err�tet die ComboBox
//							comboBoxStoredConns.setModel(new DefaultComboBoxModel(emptyString));
//						}
// 
//					}	
//				}
//			}
//		});
//				
//		JButton btnNewButton = new JButton("Search Definition File");//Den definition file locator kann man auch mit dem FileChoser-Fenster suchen 
//		btnNewButton.setBorder(UIManager.getBorder("Button.border"));
//		btnNewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 11));
//		btnNewButton.setBackground(new Color(204, 204, 102));
//		btnNewButton.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent arg0) {//Button angeklickt, Fenster geht auf
//				//System.out.println("Click!");
//				FileFilt filter = new  FileFilt(); 
//				JFileChooser c = new JFileChooser();
//				c.addChoosableFileFilter(filter);
//				c.setAcceptAllFileFilterUsed(false); 
//				if (lastDir!=null)c.setCurrentDirectory(lastDir); 
//				c.setDialogTitle("Please select the file containing the XML Defnition"); 
//				c.setApproveButtonText("OK");
//				int rVal = c.showOpenDialog(DataBaseLogin.this);
//				if(rVal == JFileChooser.APPROVE_OPTION) {
//					lastDir   = new File(c.getCurrentDirectory().toString());
//					QueryDefinitionParser.storeIniSettings( iniFile, "LASTDIR",lastDir.getAbsolutePath());
//					selectedFile=c.getSelectedFile().getAbsolutePath();
//					if (!storedFileLocators.contains(c.getSelectedFile().getAbsolutePath()))
//						comboBoxFileDef.addItem(c.getSelectedFile().getAbsolutePath() );//Ausgew�hlte Datei wird, falls sie noch nicht bekannt ist, in  die fileDef Combobox-Auswahlliste hinzugef�gt...
//					comboBoxFileDef.getModel().setSelectedItem(c.getSelectedFile().getAbsolutePath() );//...und wie von Geisterhand selektiert  
//					connectionDetails = QueryDefinitionParser.loadConnectionDetails(selectedFile);//ab hier wird praktisch der code wiederholt der in fileDef ComboBox event-listerer stattfinded  
//					String[] storedConnections = new String[connectionDetails.size()];
//					for (int t=0; t<connectionDetails.size();t++ ){
//						storedConnections[t]= connectionDetails.get(t).get("CONNECTIONNAME");}
//					if (storedConnections.length>0)	
//					{
//						comboBoxFileDef.setBackground(new Color(204, 204, 102));
//						comboBoxStoredConns.setModel(new DefaultComboBoxModel(storedConnections)); 
//						for (int t=0; t<connectionDetails.size();t++ ){
//							if (connectionDetails.get(t).get("CONNECTIONNAME").equals((String)(comboBoxStoredConns.getSelectedItem()) )  ) {
//							textHost.setText(connectionDetails.get(t).get("HOST"));
//							txtServiceName.setText(connectionDetails.get(t).get("SERVICENAME"));
//							textUserName.setText(connectionDetails.get(t).get("USERNAME"));
//							}
//						}
//						if (!storedFileLocators.contains(selectedFile)) 
//							storedFileLocators.push(selectedFile);
//					}else comboBoxFileDef.setBackground(Color.RED);
//				}
//				if(rVal == JFileChooser.CANCEL_OPTION) {
//					lastDir=c.getCurrentDirectory();
//				}
//			}
//		});
//
//		JLabel lblDefitionFilPath = new JLabel("Definition File Path");
//		lblDefitionFilPath.setFont(new Font("Tahoma", Font.BOLD, 11));
//		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
//		gl_contentPanel.setHorizontalGroup(
//			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
//				.addGroup(gl_contentPanel.createSequentialGroup()
//					.addContainerGap()
//					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
//						.addComponent(lblPassword, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
//						.addGroup(gl_contentPanel.createSequentialGroup()
//							.addPreferredGap(ComponentPlacement.RELATED)
//							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
//								.addComponent(lblHostUrl, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
//								.addComponent(lblServiceName)
//								.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
//								.addComponent(lblQueryDefintion)
//								.addComponent(lblDefitionFilPath))))
//					.addPreferredGap(ComponentPlacement.RELATED)
//					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
//						.addComponent(btnNewButton, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
//						.addComponent(comboBoxFileDef, GroupLayout.PREFERRED_SIZE, 375, GroupLayout.PREFERRED_SIZE)
//						.addGroup(Alignment.LEADING, gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
//							.addComponent(passwordField, Alignment.LEADING)
//							.addComponent(txtServiceName, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
//							.addComponent(textUserName, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
//							.addComponent(comboBoxStoredConns, Alignment.LEADING, 0, 195, Short.MAX_VALUE)
//							.addComponent(textHost, Alignment.LEADING, 0, 0, Short.MAX_VALUE)))
//					.addGap(97))
//		);
//		gl_contentPanel.setVerticalGroup(
//			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
//				.addGroup(gl_contentPanel.createSequentialGroup()
//					.addContainerGap()
//					.addComponent(btnNewButton)
//					.addGap(18)
//					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
//						.addComponent(comboBoxFileDef, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//						.addComponent(lblDefitionFilPath))
//					.addPreferredGap(ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
//					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
//						.addComponent(lblQueryDefintion)
//						.addComponent(comboBoxStoredConns, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//					.addGap(18)
//					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
//						.addComponent(lblNewLabel)
//						.addComponent(textUserName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//					.addPreferredGap(ComponentPlacement.UNRELATED)
//					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
//						.addComponent(lblHostUrl)
//						.addComponent(textHost, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//					.addGap(18)
//					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
//						.addComponent(lblServiceName)
//						.addComponent(txtServiceName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//					.addGap(18)
//					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
//						.addComponent(lblPassword)
//						.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//					.addGap(14))
//		);
//		contentPanel.setLayout(gl_contentPanel);
//		{
//			JPanel buttonPane = new JPanel();
//			buttonPane.setBackground(new Color(153, 153, 204));
//			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
//			getContentPane().add(buttonPane, BorderLayout.SOUTH);
//			{
//				JButton okButton = new JButton("OK");
//				okButton.setBackground(new Color(204, 204, 102));
//				okButton.setAction(action_1);
//				//okButton.setActionCommand("OK");
//				buttonPane.add(okButton);
//				getRootPane().setDefaultButton(okButton);
//			}
//			{
//				JButton cancelButton = new JButton("EXIT");
//				cancelButton.setBackground(new Color(204, 204, 102));
//				cancelButton.setAction(action);
//				//cancelButton.setActionCommand("Cancel");
//				buttonPane.add(cancelButton);
//			}
//		}
//	}	private class SwingAction extends AbstractAction {
//		private static final long serialVersionUID = 1L;
//		public SwingAction() {
//			putValue(NAME, "EXIT");
//			putValue(SHORT_DESCRIPTION, "Exit");
//		}
//		public void actionPerformed(ActionEvent e) {
//			DBConnections.finish();
//		}
//	}
//	private class SwingAction_1 extends AbstractAction {
//		private static final long serialVersionUID = 1L;
//		public SwingAction_1() {
//			putValue(NAME, "OK");
//			putValue(SHORT_DESCRIPTION, "tries to establish a DB Connection with JDBC using the credentials you entered");
//		}
//		public void actionPerformed(ActionEvent e) {
//			try {
//				QueryDefinitionParser.storeFileLocator (storedFileLocators, iniFile );				
//				boolean succes;
//				String host = textHost.getDocument().getText(0,textHost.getDocument().getLength());
//				String serviceName = txtServiceName.getDocument().getText(0,txtServiceName.getDocument().getLength());
//				String userName = textUserName.getDocument().getText(0,textUserName.getDocument().getLength() );
//				String connectionName=((String)(comboBoxStoredConns.getModel().getSelectedItem()));
//				//succes=DataJumper2.startDataJumper(host,serviceName ,userName ,passwordField.getDocument().getText(0,passwordField.getDocument().getLength()),selectedFile, dialog);
//				
//				try {
//					succes=new DataJumper().startDataJumper(connectionName,host,serviceName ,userName ,Encrpt.encrypt(passwordField.getDocument().getText(0,passwordField.getDocument().getLength())),selectedFile, dialog);
//					passwordField.getDocument().remove(0,passwordField.getDocument().getLength());
//				} catch (Exception e1) {
//					ErrorMessage.showException(e1,"Fehler beim Verbindungsaufbau");
//					succes=false;
//				}
//				
//				if(succes)
//				{//Wenn die Loginsdetails erfolgreich angewendet wurden, dann wird ausgelotet ob etwaige Erg�nzungen der Logindetails stattgefunden haben 
//					Map<String,String> newConnDet= new HashMap<String,String>();
//					newConnDet.put("HOST", host);
//					newConnDet.put("SERVICENAME", serviceName);
//					newConnDet.put("USERNAME", userName);
//					newConnDet.put("CONNECTIONNAME", connectionName );
//					newConnDet.put("CONNECTIONTYPE", "BASE");
//					if (connectionDetails!=null && connectionDetails.size()>0)
//						{
//						boolean newConnectionNameEntered = true;
//						for (int t=0; t<comboBoxStoredConns.getModel().getSize();t++){
//							if (            
//								//wenn das m�glicherweise manuell ver�nderte Dropdown-Element	gleich ist einem der geladenen Dropdown-Elementen...  
//								    ((String)(comboBoxStoredConns.getModel().getElementAt(t))).equals((String) ( comboBoxStoredConns.getModel().getSelectedItem()))
//								 || ((String) ( comboBoxStoredConns.getModel().getSelectedItem())).trim().length()==0 //...oder auch wenn der User den ConnectionNamen gel�scht hat
//							   ){//...und somit nicht ver�ndert wurde dann Pr�fe ob sich vielleicht die login-Details ver�ndert haben
//								//System.out.println("Element "+comboBoxStoredConns.getModel().getSelectedItem()+" existiert." );
//								newConnectionNameEntered=false;
//								if ( !(host+serviceName+userName).toLowerCase().
//										equals((connectionDetails.get(t).get("HOST")+connectionDetails.get(t).get("SERVICENAME")+connectionDetails.get(t).get("USERNAME")).toLowerCase() )
//								   )
//								{//Ja, die login-Details wurden manuell ge�ndert. Und wenn diese Logindaten nicht schon unter einem anderen ConnectionNamen existieren... 
//									boolean newConnDetailsEntered=true; 
//									for (Map<String,String> conns : connectionDetails){
//										if ( (host+serviceName+userName).toLowerCase().equals( (conns.get("HOST")+conns.get("SERVICENAME")+conns.get("USERNAME")).toLowerCase() ) ){
//											newConnDetailsEntered=false;
//											break;
//										}
//									}
//									if(newConnDetailsEntered)// ...dann k�nnte man in der Defnitionsdatei die neuen Logindaten unter denselben ConnectionNamen speichern"
//										{
//										System.out.println("Jetzt k�nnte man in der Defnitionsdatei die neuen Logindaten unter denselben ConnectionNamen speichern\n"+(host+serviceName+userName)+" != "+ connectionDetails.get(t).get("HOST")+connectionDetails.get(t).get("SERVICENAME")+connectionDetails.get(t).get("USERNAME")  );
//										QueryDefinitionParser.amendConnectionDetailsXML(selectedFile, connectionDetails.get(t).get("CONNECTIONNAME"), newConnDet );
//										connectionDetails = QueryDefinitionParser.loadConnectionDetails(selectedFile);
//										}
//									else System.out.println("die ver�nderten connectionDetails gibt es unter einem anderen Namen, nichts passiert.");
//								}	
//								else System.out.println("Name und connectionDetails sind gleich geblieben, nichts passiert");
//								break;
//								}
//							} 
//						if (newConnectionNameEntered==true){
//							//System.out.println("Element "+comboBoxStoredConns.getModel().getSelectedItem()+" is neu. 
//							//Pr�fe nun die, ob connectionDetails aus den Eingabefeldern neu sind oder mir irgendeiner Connection �bereinstimmen " 
//							boolean newConnDetailsEntered=true; 
//							for (Map<String,String> conns : connectionDetails){
//								if ( (host+serviceName+userName).toLowerCase().equals( (conns.get("HOST")+conns.get("SERVICENAME")+conns.get("USERNAME")).toLowerCase() ) ){
//									newConnDetailsEntered=false;
//									System.out.println("�berscheibe den Namen der Connection "+conns.get("CONNECTIONNAME")+" mit "+comboBoxStoredConns.getModel().getSelectedItem() );
//									QueryDefinitionParser.amendConnectionDetailsXML(selectedFile, conns.get("CONNECTIONNAME"), newConnDet );//�nderung speichern
//									connectionDetails = QueryDefinitionParser.loadConnectionDetails(selectedFile);//...und anwenden
//									comboBoxStoredConns.removeItem(conns.get("CONNECTIONNAME"));//Der alte Name wird entfernt
//									comboBoxStoredConns.addItem( (String)(comboBoxStoredConns.getModel().getSelectedItem()));//und der Neue hinzugef�gt
//									break;
//								}
//							}
//							//Wenn die Verbindungsdetails neu und der ConnectionName auch neu sind dann speichere diese Neue connection."  );
//							if(newConnDetailsEntered)
//								{
//									System.out.println("Erzeuge eine neue Connection mit Namen "+comboBoxStoredConns.getModel().getSelectedItem()+" und den Verbindungs details "+host+" "+serviceName+" "+userName);
//									QueryDefinitionParser.addConnectionDetailsToDefFile( selectedFile, newConnDet );
//									connectionDetails = QueryDefinitionParser.loadConnectionDetails(selectedFile);
//									comboBoxStoredConns.addItem( (String)(comboBoxStoredConns.getModel().getSelectedItem()));	
//								}
//							}
//						}else  {//Wenn keine Verbindungsdetails in Definition file gespeichert wurden und es wurde erfolgreich eine Verbindung hergestellt, dann muss der user die Verbindungsdetails eingegeben haben. Diese werden nun gespeichert.     
//							if (newConnDet.get("CONNECTIONNAME").trim().length()==0)//wenn der ConnectionName nicht eingegeben wurde, wird er auf username + service name zusammengesetzt
//								newConnDet.put("CONNECTIONNAME",newConnDet.get("USERNAME")+" on "+newConnDet.get("SERVICENAME"));
//							QueryDefinitionParser.addConnectionDetailsToDefFile( selectedFile, newConnDet );
//							connectionDetails = QueryDefinitionParser.loadConnectionDetails(selectedFile);
//							comboBoxStoredConns.removeAllItems();
//							comboBoxStoredConns.addItem( newConnDet.get("CONNECTIONNAME"));
//						}
//				}	
//			} catch (BadLocationException e1) {
//				ErrorMessage.showException(e1,"");
//			}
//		}
//	}
//}
//
//class FileFilt extends FileFilter {
//
//String extension= ".xml";
//String description ="XML file";
//    public boolean accept(File f) {
//	if(f != null) {
//	    if(f.isDirectory())	return true;
//	    if (f != null) return (f.getName().contains(extension) );
//  		};
//	return false;
//	}
//    public String getDescription() {
//		return this.description;
//		}
//    public void setDescription(String description) {
//	this.description = description;
// }
}