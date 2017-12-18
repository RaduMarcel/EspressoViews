import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.JTextField;
import javax.swing.DropMode;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import javax.swing.UIManager;
import java.awt.Font;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.Cursor;
import javax.swing.JFileChooser;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.BoxLayout;
import java.awt.event.ActionListener;
import org.apache.logging.log4j.*;
/**
 * TODO

BUGS


- Null als columnangabe wird nicht akeptiert!
	Momentan ist es so:
	create table xx as select NULL as nix  from dual
	Wenn ein column as null angegeben wird, weiß der create table statement nicht was für ein Datentyp das null-Feld sein soll. 
	so aber geht's:
	create table xx as select  cast(null as integer) as nix from dual

- Nach reload sollte der Focus auf dem Baum sein !

NEUES


- Logging level in ini file einführen, triggert log4J Funktionen. Zeiten messen, um Prozesse zu optimieren

- Verbindungen zu neuen Datenbanken erlauben !! 
  Als nächstes: Microsoft SQL Server
  

- Den tag <RollUp> column_name_1,column_name_2,... </RollUp> einführen !

  
- Namen der temporären Tabellen nach dem Erzeugen im ini file schreiben + die dazugehörige db connection. Nach dem Droppen auch von dort wieder löschen (anstelle des createdTables Set). 
  Damit könnten nicht gedroppte temp tables auch nach einem Programmneustart nachträglich noch gedroppt werden!!!

- Wenn während der SQL-Abfrage die DB-Verbindung verloren geht, versuche dreimal die DB-Verbindung wieder aufzubauen 
  und wenn der Verbindungsaufbau klappt, dann versuche bei der Abfrage weiterzumachen, bei der die Verbindung verlorengegangen ist.!!           

- Wenn innerhalb einer SQLQueryDefinition oder einer DBConnectionDefinition ein Tag auftaucht, der nicht bekannt ist, dann könnte man dies in einer Warningmessage kundgeben!

Swinglastig: 

- Bei Rechtsklick erkennen ob der Mauszeiger auf ein Label oder einer ResultLine steht und an der Stelle test drop down menu ausfahren!!!!  

- Ein Container implementieren der diese Swing-ObjeKte enthält: Login dialog und die Baumfenstern, die es zu jedem Zeitpunkt gibt. 
  Er muss dann listeners haben, die bemerken, wenn ein solches Objekt disposed wird !!
   
- Neuer Tag <AskForValues> column_name_1,column_name_2,... </AskForValues>.!!!!! 
  Variablen in der SQL definition einführen, die dann vor der Baumerzeugung durch ein pop-up fenster von dem User abgefragt werden 
  AND und OR, NOT und () anbieten
  Eingaben mit operatoren in der Anfragedefinition speichern und das Formular bei Neuanwendung wieder damit befüllen
  
-  Das Logindialog soll aber nach dem Erzeugen des Datenfensters nie diposed werden, sondern nur invisible. OK. 
  Wenn das Datenfenster geschlossen wird, soll das Logindialog wieder visible werden. OK  
  Aber wenn das Datenfenster mit dem X-Button geschlossen wird, bleibt das Logindialog denoch invisible. X-Button wurde ausgeschlatet, aber dies ist nicht die beste Lösung !!   

Sonst:


- icon kreieren !!


ZUKUNFTSMUSIK


- Als Definitionsdatei soll auch eine Golden-Workspace oder Toad oder SQL-Developer Workspace angegeben werden können. Diese wird dann in XML-Format umgewandelt.

- Tooltips mit Infos zur jeweiligen Tabellenzelle 

- Baumdaten speichern und wieder als Baum laden können.

- Baumdaten als insert und/oder delete speichern können.

- Der User soll sich Schriftart, Schriftgröße und Farbe selbst aussuchen können. 

- Als + und - Buttons in der Menueleiste einführen mit der man die Schriftgröße vergrößern oder verkleinern kann 

- Bei Rechtsklick auf ein Baumelement Edit-Formular für die entsprechende Anfragedefinition einblenden/Teilfenster öffnen.
  Modifizierte Anfragedefinition nach Verlassen des Fensters anwenden.       



-------------------------------DONE!-------------------------------
- die inherited columns werden auf Gleichheit geprüft, mit einem wildkey, zB *, in column der Kindzeile könnte man etwas ähnliches wie eine Oder-Operation einführen!!!
  was sollte man dann weiter nach unten vererben, den Wert mit oder ohne wildkey?
  replaceAll("\\*", ".*").replaceAll("%", ".*")       

- Als subQuery soll der locator einer weiteren XML-Definitionsdatei angegeben werden können, die dann natürlich selbst andere subqueries haben kann OK  
- Im tag <SQLQuery>  sollte ein file locator angegeben werden können, damit grosse SQLs die Übersichtlichkeit der Definition nicht beeinträchtigen!!
- In einem <SubQueryLocator> kann man mehrere report definition files einfügen.

- GPL 3 Lizenz 
- Tutorial schreiben !!!

- Verbindungen zu anderen Datenbanken als Oracle erlauben !! 
  DONE MySQL und Oracle
- GUI ist fähig mehrere Datenbank-technologien anzubieten OK
- der DB connection type dropdown im login-fenster sollte für mySQL nur das element "database" anbieten, für Oracle dageben die elemente service name und SID   

- die Volumensperre für den in Operator funtioniert nicht

- Der tag root kann jetzt heißen wie will, und die report definition kann auch in einem beliebigen sub-baum liegen

- die Einschränkung der untequery auf die vererbten werte der oberquery funktioniert nicht 100%

- 	Login-Fenster erweitern, 
	SID oder Service_Name anwählen können OK
	port angeben können, OK
	Passwort nach Eingabe enkrypten OK

- kompakte Dastellung des Baumes implementieren. Minimalimplementierung OK


- TransmittedConditions nicht mehr mit select distinct ermitteln sondern mit Java code aus der Datenmatrix OK

- Algorithmus für DataNode.determinetMaxColumnLength überprüfen OK

- Wenn keine transmitted columns angegeben werden, dann sollen alle Kriterien UND-Verknüpft angewendet werden, die die subquery mit der superquery gemeinsam hat OK

- wenn eine inherited Condition nicht anwedbar ist, weil er an die maxinheritanceDepth-Bedingung scheitert, 
  dann soll dieser auch als nicht-anwerdbar in dem Structure-Modus angezeigt werden OK      

- Definitionstrukturdarstellung implementieren OK

- last directory in ini setting speichern und bei jedem programmstart setzen OK

- Warning-Strenge in ini file einführen OK. Der tag <WarningLevel> wurde im inisetting eingeführt. die möglichen Werte sind low und high  

- Selectierte Definitionfile ohne connectiondetaily wird rot OK

- number separator richtet sich nach den settings des clients, soll sich nach den settings des users oder der instanz richten OK

- Nach reload wird der vorherige Baum anscheinend nicht disposed, memory wird unnötig besetzt OK

- About menu item mit version control schreiben OK
- einen weiteren Tab im About einführen mit GPL licence agreement OK

- vollenden inisetting.xml funktionalität, OK
	inisetting.xml laden OK
	inisetting.xml speichern OK
	neue Verbindungsdetails sollten sich mit eigenem Verbindungsnamen speichern lassen OK
	Wenn es ein ini-setting file nicht gibt, muss automatisch eins angelegt werden OK 
	Error Message einblenden, wenn in der ini-setting Datei nicht gespeichert werden kann OK
	
in der Darstellung
- SQLs mit leerer Ergebnissmenge werden mit einer leeren Ergebniszeile dargestellt OK
- Ausgefahrene Labels, mit dunkelroter Schrift markieren OK 
- Toolbar in Baumfenstereinführen  OK
- den button Reload in der Toolbar einführen, der den Baum anhand derselben Definitionsdatei wieder erzeugt. 
Und/oder einen dropdown-menu bei rechtsklick einführen und Reload-Funktion anbieten (Reload auch mit F5) OK
- wenn ein Baumknoten ausgefahren wird und er hat nur ein Kindknoten, dann soll auch das Kindknoten ausgefahren werden und focus soll auf den ersten Enkel gehen  OK

- Durch ein zweites Fenster (schiebbar) den frame teilen und im unteren Teil warnings OK und statistics ablegen OK

- Neue Option in der transaction definintion einführen: Display no label if resultset is null OK    

Abfangen möglicher Fehler im definition file: OK 
- kein Label: Error  OK
- Label nicht unique Error OK 
- keine Anfrage: Error OK
- erste Anfrage hat eine SuperQuery: Error OK
- nicht-Erste Anfrage hat keine SuperQuery: Error OK
- angegebene superQuery wurde noch nicht intstanziiert Fehler: Error OK

Validations:
- eine oder mehrere transmittedColumns sind nicht ein selektiertes Feld Error OK
- Zirkularität im subquery locator und parent definition file, Error OK 
- eine oder mehrere inheritedColumns sind nicht unter den selektierten Felder : 
            wende nur die an, die angewendet werden können, aber warne, wenn keine Column angewendet werden kann OK     
- wenn eine Anfrage kein Kriterium von der Oberanfrage übernimmt: 
	kein Fehler, die Anfrage erfährt keine Einschränkung und jede Ergebniszeile aus der Oberanfrage hat als Kind das gesamte ResultSet der Unterabfrage OK.
- einige oder alle angegebenen resultColumns werden in der Anfrage nicht selektiert - Warnung OK
- wenn die nicht-unterste Query ein null-resultset hat: kein Fehler, Baumpfad endet dort OK         

- RAM-Belegung erhöht sich mit jeder Baumerzeugung. OK. Memory leak gefunden. gellöst durch frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

Ausdruckmöglichkeit erweitern bei transmittedConditionColumns: 
	- nicht anwendbare Bedingungen werden ignoriert und im eigenem Erbe weitergereicht OK 
	- Oder-Verknüpfung zulassen oder gleich logische operatoren einführen. 
	  Mindestens eines der order-verknüpften Felder soll, sofern sie im schema der Kinder exitiert, angewandt werden.!
	- Anfrageergebnis im temptable speichern und dann nach gebrauch droppen, temptable muss unique pro user sein (Mehrbenutzermodus) OK       

- order by im den SQLs mit parent funktioniert nicht -- nicht OK
- ResultColumns Funktionalität implementieren OK
- Funktionalität mit not: funktioniert nur beim ersten Strang OK

- wenn der Wert der transmitted column null ist und die column anwendbar ist von den Kindern, dann wirkt sich der Bedingung "ist gleich null" nicht einschränkend aus, so als ob die transmitted column nicht anwendbar wäre. 
OK null-Werte als Erbe setzen sich nie fort. 

*/

public class DataBaseLogin3 extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final String defaultMySQLPort="3306";
	private static final String defaultOraclePort="1521";
	private static DataBaseLogin3 dialog;
	private final JPanel contentPanel = new JPanel();
	private JTextField textUserName;
	private JTextField textHost;
	private JPasswordField passwordField;
	private final JComboBox<String> comboBoxStoredConns;
	private JTextField txtServiceName;
	private final Action action = new SwingAction();
	private final Action action_1 = new SwingAction_1();
	private static File lastDir= new File("");
	protected static String warningLevel="HIGH";
	private static String selectedFile;
	private static List<Map<String,String>> connectionDetails=null; 
	private static LinkedList<String> storedFileLocators;
	private static final String iniFile ="iniSettings.xml"; 
	private JPanel buttonPane;
	private final JTextField portTextField;
	private final JComboBox<String> connTypeComboBox;
	private final JComboBox<String> dbTypeComboBox;
	private static Logger logger = LogManager.getLogger(DataBaseLogin3.class.getName());
    protected static byte[] kb = new byte[16];

public static void main(String[] args) {
	logger.info("*************************************** Espresso Views Database Login Application started ***************************************");
	new Random().nextBytes(kb);
	try {
			File iniFileLocator = new File (iniFile);
			//System.out.println(iniFileLocator.getAbsolutePath());
			if (!iniFileLocator.exists()){
				try {
					iniFileLocator.createNewFile();
				} catch (Exception e) {
					ErrorMessage.showException(e,"Problems occured during the creation of the file iniSettings.xml");
					logger.error("Problems occured during the creation of the file iniSettings.xml\n"+e.getMessage()+"\n"+ErrorMessage.showStackTrace(e.getStackTrace()));
				}
				logger.info("The user setting file was not found.\nA new file with default settings will be created: "+iniFileLocator.getAbsolutePath());
			} 
			
			LinkedList<Map<String,String>> fileLocators = QueryDefinitionParser.loadFileLocators(iniFile);
			storedFileLocators = new LinkedList<String>();
			if (fileLocators.size()>0){
				for (int t=0; t<fileLocators.size();t++ ){
					int tt=0;
					while (storedFileLocators.size()<fileLocators.get(t).size() && storedFileLocators.size()<11 && tt<11){
//TODO implement the removal of the file locators from the ini file if fileLocators.get(t).get("FILELOCATOR"+tt)!=null UND new File(fileLocators.get(t).get("FILELOCATOR"+tt)).exists() == false	
					if (fileLocators.get(t).get("FILELOCATOR"+tt)!=null && new File(fileLocators.get(t).get("FILELOCATOR"+tt)).exists() ) {
						storedFileLocators.add(fileLocators.get(t).get("FILELOCATOR"+tt));
					}
					tt++;
					//System.out.println(fileLocators.get(t).get("FILELOCATOR"+tt));
					} 
				}	
			}
			logger.info(storedFileLocators.size()+" stored file locators found in the user settings file");
			LinkedList<Map<String,String>> iniSettings = QueryDefinitionParser.loadIniSettings(iniFile);
			String lastDirTag="";
			String warningLevelTag="";
			if (iniSettings.size()>0){
				for (int t=0; t<iniSettings.size();t++ ){
					lastDirTag=iniSettings.get(t).get("LASTDIR");
					warningLevelTag=iniSettings.get(t).get("WARNINGLEVEL");
				}
			}
			if (lastDirTag!=null && lastDirTag.length()>0 && new File(lastDirTag).isDirectory()) 
				 lastDir= new File(lastDirTag);
			if (warningLevelTag!=null && warningLevelTag.length()>0 && (warningLevelTag.toUpperCase().equals("LOW")||warningLevelTag.toUpperCase().equals("HIGH")) )
				warningLevel=warningLevelTag.toUpperCase();
			else {
				QueryDefinitionParser.storeIniSettings( iniFile, "WARNINGLEVEL","HIGH");//default warning level is High wenn kein warning level angegeben ist
				warningLevel="HIGH";
			}
			logger.info("User warning level set to "+warningLevel);
			//System.out.println(Arrays.toString(UIManager.getInstalledLookAndFeels()));
			 UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			 logger.info("Available look-and-feel \n"+Arrays.toString(UIManager.getInstalledLookAndFeels()));
			dialog = new DataBaseLogin3();
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.setVisible(true); 
		} catch (Exception e) {
			ErrorMessage.showException(e,"Ups! Ein unerwarteter Fehler.");
			logger.error("Unhandled Exception!\n"+e.getMessage()+"\n"+ErrorMessage.showStackTrace(e.getStackTrace()));
		}
	}
	/**
	 * Create the dialog.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DataBaseLogin3() {
		setResizable(false);
		logger.info("************************************ Starting the DB login window ***************************************");
		setTitle("Database Log-in");
		setBounds(100, 100, 569, 427);
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		contentPanel.setBackground(UIManager.getColor("CheckBox.focus"));
		contentPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		{
			textUserName = new JTextField();
			textUserName.setFont(new Font("Dialog", Font.PLAIN, 14));
			textUserName.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			textUserName.setHorizontalAlignment(SwingConstants.LEFT);
			textUserName.setDropMode(DropMode.INSERT);
			textUserName.setColumns(25);
			textUserName.setBackground(new Color(240, 230, 140));
		}
		{
			textHost = new JTextField();
			textHost.setFont(new Font("Dialog", Font.PLAIN, 14));
			textHost.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			textHost.setActionCommand("");
			textHost.setHorizontalAlignment(SwingConstants.LEFT);
			textHost.setDropMode(DropMode.INSERT);
			textHost.setColumns(25);
			textHost.setBackground(new Color(240, 230, 140));
		}
		
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Dialog", Font.PLAIN, 14));
		passwordField.setBackground(new Color(240, 230, 140));
		
		JLabel lblNewLabel = new JLabel("User Name");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JLabel lblHostUrl = new JLabel("Host URL");
		lblHostUrl.setHorizontalAlignment(SwingConstants.RIGHT);
		lblHostUrl.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		txtServiceName = new JTextField();
		txtServiceName.setFont(new Font("Dialog", Font.PLAIN, 14));
		txtServiceName.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		txtServiceName.setAutoscrolls(false);
		txtServiceName.setHorizontalAlignment(SwingConstants.LEFT);
		txtServiceName.setDropMode(DropMode.INSERT);
		txtServiceName.setColumns(25);
		txtServiceName.setBackground(new Color(240, 230, 140));
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPassword.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JLabel lblQueryDefintion = new JLabel("Stored Connections ");
		lblQueryDefintion.setHorizontalAlignment(SwingConstants.RIGHT);
		lblQueryDefintion.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		comboBoxStoredConns = new JComboBox<String>();
		//Auswahl von gespeicherten DB-Verbindungen. Eine definition file kann mehrere DB-Verbindungen vorlegen. Die gefundenen DB-Verbindungen werden in dieser ComboBox übergeben.    
		comboBoxStoredConns.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {// Wird aktiviert, wenn der Benutzer eine DB-Verbindung aus der ComboBox-Liste auswählt 
				//System.out.println("Selected Item  "+(String)(comboBoxStoredConns.getSelectedItem()) );
				if (connectionDetails!=null) //enthält Verbingsdetails, die aus der ausgewählten definition file geladen wurden 
				for (int t=0; t<connectionDetails.size();t++ ){
					if (connectionDetails.get(t).get("CONNECTIONNAME").equals( (String)(comboBoxStoredConns.getSelectedItem()) ) ){
					textHost.setText(connectionDetails.get(t).get("HOST"));
					txtServiceName.setText(connectionDetails.get(t).get("SERVICENAME"));
					portTextField.setText(connectionDetails.get(t).get("PORT"));
					textUserName.setText(connectionDetails.get(t).get("USERNAME"));
					connTypeComboBox.getModel().setSelectedItem(connectionDetails.get(t).get("CONNECTIONTYPE"));
					dbTypeComboBox.getModel().setSelectedItem(connectionDetails.get(t).get("DBTYPE"));
					}
				}
	
			}
		});
		comboBoxStoredConns.setBackground(new Color(240, 230, 140));
		comboBoxStoredConns.setModel(new DefaultComboBoxModel<String>(new String[]{""})); 
		comboBoxStoredConns.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));	
		comboBoxStoredConns.setEditable(false); 
		//
		//Hier gibt  man den file locator für file definition ein oder man wählt einen der locators aus, der aus der ini-setting Datei geladen wurde.
		final JComboBox<String> comboBoxFileDef = new JComboBox<String>(); 	 
		comboBoxFileDef.setFont(new Font("Dialog", Font.BOLD, 13));
		if (storedFileLocators.size()>0){
			DefaultComboBoxModel<String> theModel = new DefaultComboBoxModel( storedFileLocators.toArray() );
			theModel.insertElementAt("", 0);//das erste dropdown menu element kann man selbst eingeben
			comboBoxFileDef.setModel(theModel);
			comboBoxFileDef.setSelectedIndex(0);
		}
		comboBoxFileDef.setEditable(true);
		comboBoxFileDef.setBackground(new Color(240, 230, 140));
		comboBoxFileDef.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {// wenn der Benutzer in der ComboBox-Liste ein definition file auswählt ...
				if ( comboBoxFileDef.getSelectedItem() !=null ){
					File storedLocator = new File ( (String)(comboBoxFileDef.getSelectedItem()) ); 
					if (!storedLocator.exists()) 
						comboBoxFileDef.setBackground(Color.RED);//gibt's die Datei nicht errötet die TypeComboBox   					
					//System.out.println("comboBox_1.getSelectedItem() ="+comboBoxFileDef.getSelectedItem()+"| storedLocator ="+storedLocator+"|"+storedLocator.exists() );
					if ( (selectedFile == null || !storedLocator.getAbsolutePath().equals(new File (selectedFile)) ) &&  storedLocator.exists() ){
						//...und wenn die Auswahl zu einer existierenden Datei hinführt, die nicht die aktuell ausgewählte Datei ist...
						comboBoxFileDef.setBackground(new Color(204, 204, 102));
						selectedFile=storedLocator.getAbsolutePath();
						connectionDetails = QueryDefinitionParser.loadConnectionDetails(storedLocator.getAbsolutePath()); //...werden erstmal die Verbindungsdetails samt Bezeichner werden aus der definition file ausgelesen
						//QueryDefinitionParser.showMeTheDoc(QueryDefinitionParser.getXmlDocument(storedLocator.getAbsolutePath()), 1 );
						String[] storedConnectionNames = new String[connectionDetails.size()];//und angenommen es gibt Verbindungsdetails in dieser definition file...
						comboBoxStoredConns.setEditable(true);
						for (int t=0; t<connectionDetails.size();t++ ){
							storedConnectionNames[t]= connectionDetails.get(t).get("CONNECTIONNAME");}//...wird eine Liste mit den Bezeichnern aller gefundeden DB-Verbindungen erstellt   
						if (storedConnectionNames.length>0)
							{
							comboBoxFileDef.setBackground(new Color(204, 204, 102));
							comboBoxStoredConns.setModel(new DefaultComboBoxModel(storedConnectionNames));//und die Liste der DB-Verbingsbezeichner ist jetzt die neue liste derVerbindungsdetails-ComboBox
							for (int t=0; t<connectionDetails.size();t++ ){//Doch damit nicht genug, die erste Vebindung wird auf die login-Felder verteilt  
								if (connectionDetails.get(t).get("CONNECTIONNAME").equals((String)(comboBoxStoredConns.getSelectedItem()) )  ) {
									textHost.setText(connectionDetails.get(t).get("HOST"));
									txtServiceName.setText(connectionDetails.get(t).get("SERVICENAME"));
									portTextField.setText(connectionDetails.get(t).get("PORT"));
									textUserName.setText(connectionDetails.get(t).get("USERNAME"));
									connTypeComboBox.getModel().setSelectedItem(connectionDetails.get(t).get("CONNECTIONTYPE"));
									dbTypeComboBox.getModel().setSelectedItem(connectionDetails.get(t).get("DBTYPE"));
								}
							}
							//wenn die definition file neu ist, also vom Benutzer eingegeben wurde, kommt sie in die defnition file locators Liste rein, die dann in inisettng.xml gespeichert wird
							if (!storedFileLocators.contains(storedLocator.getAbsolutePath()) ) 
								storedFileLocators.push(storedLocator.getAbsolutePath());
							      
						}else {
							String[] emptyString = {" "};
							//comboBoxFileDef.setBackground(Color.RED);//gibt die definition file keine DB-Verbindungsdetails her, dann errötet die ComboBox
							comboBoxStoredConns.setModel(new DefaultComboBoxModel(emptyString));
						}
					}	
				}
			}
		});
				
		JButton btnNewButton = new JButton("Select Definition File");//Den definition file locator kann man auch mit dem FileChoser-Fenster suchen 
		btnNewButton.setBorder(UIManager.getBorder("Button.border"));
		btnNewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 13));
		btnNewButton.setBackground(new Color(240, 230, 140));
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {//Button angeklickt, Fenster geht auf
				//System.out.println("Click!");
				FileFilt filter = new  FileFilt(); 
				JFileChooser c = new JFileChooser();
				c.addChoosableFileFilter(filter);
				c.setAcceptAllFileFilterUsed(false); 
				if (lastDir!=null)c.setCurrentDirectory(lastDir); 
				c.setDialogTitle("Please select the XML file containing the Report Definition"); 
				c.setApproveButtonText("OK");
				int rVal = c.showOpenDialog(DataBaseLogin3.this);
				if(rVal == JFileChooser.APPROVE_OPTION) {
					lastDir   = new File(c.getCurrentDirectory().toString());
					QueryDefinitionParser.storeIniSettings( iniFile, "LASTDIR",lastDir.getAbsolutePath());
					selectedFile=c.getSelectedFile().getAbsolutePath();
					if (!storedFileLocators.contains(c.getSelectedFile().getAbsolutePath()))
						logger.info("User selected a known definition file locator: "+c.getSelectedFile().getAbsolutePath());
						comboBoxFileDef.addItem(c.getSelectedFile().getAbsolutePath() );//Ausgewählte Datei wird, falls sie noch nicht bekannt ist, in  die fileDef Combobox-Auswahlliste hinzugefügt...
					comboBoxFileDef.getModel().setSelectedItem(c.getSelectedFile().getAbsolutePath() );//...und wie von Geisterhand selektiert  
					connectionDetails = QueryDefinitionParser.loadConnectionDetails(selectedFile);//ab hier wird praktisch der code wiederholt der in fileDef ComboBox event-listerer stattfinded  
					String[] storedConnections = new String[connectionDetails.size()];
					for (int t=0; t<connectionDetails.size();t++ ){
						storedConnections[t]= connectionDetails.get(t).get("CONNECTIONNAME");
						}
					if (storedConnections.length>0)	
					{
						comboBoxFileDef.setBackground(new Color(204, 204, 102));
						comboBoxStoredConns.setModel(new DefaultComboBoxModel(storedConnections));//die gefundenen Connections werden in das dropdown-menu für verfügbare connections geladen 
						for (int t=0; t<connectionDetails.size();t++ ){
							if (connectionDetails.get(t).get("CONNECTIONNAME").equals((String)(comboBoxStoredConns.getSelectedItem()) )  ) {
							textHost.setText(connectionDetails.get(t).get("HOST"));
							txtServiceName.setText(connectionDetails.get(t).get("SERVICENAME"));
							portTextField.setText(connectionDetails.get(t).get("PORT"));
							textUserName.setText(connectionDetails.get(t).get("USERNAME"));
							connTypeComboBox.getModel().setSelectedItem(connectionDetails.get(t).get("CONNECTIONTYPE"));
							dbTypeComboBox.getModel().setSelectedItem(connectionDetails.get(t).get("DBTYPE"));
							}
						}
						if (!storedFileLocators.contains(selectedFile)) 
							storedFileLocators.push(selectedFile);
					}else comboBoxFileDef.setBackground(Color.RED);
				}
				if(rVal == JFileChooser.CANCEL_OPTION) {
					lastDir=c.getCurrentDirectory();
				}
			}
		});

		JLabel lblDefitionFilPath = new JLabel("Definition File Path");
		lblDefitionFilPath.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		JLabel lblDatabase = new JLabel("Database");
		lblDatabase.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDatabase.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		dbTypeComboBox = new JComboBox<String>();
		dbTypeComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if ( dbTypeComboBox.getSelectedItem()!=null && ((String)(dbTypeComboBox.getSelectedItem())).equals("Oracle")  ) 
				{	
					try {
						if (portTextField.getDocument().getText(0,portTextField.getDocument().getLength()).equals("")  
							|| portTextField.getDocument().getText(0,portTextField.getDocument().getLength()).equals(defaultMySQLPort)	
							)
						portTextField.setText(defaultOraclePort);
					} catch (BadLocationException e) {
						logger.error(e.getMessage()+"\n"+ErrorMessage.showStackTrace(e.getStackTrace()));
						ErrorMessage.showException(e,"");
					}
					connTypeComboBox.setModel(new DefaultComboBoxModel(new String[] {"Service_Name", "SID"}));
					//if (((String)(connTypeComboBox.getModel().getSelectedItem()))!=null)						
				}
				else {
					try {
						if (portTextField.getDocument().getText(0,portTextField.getDocument().getLength()).equals("")
							|| portTextField.getDocument().getText(0,portTextField.getDocument().getLength()).equals(defaultOraclePort)
							)
							portTextField.setText(defaultMySQLPort);
					} catch (BadLocationException e) {
						logger.error(e.getMessage()+"\n"+ErrorMessage.showStackTrace(e.getStackTrace()));
						ErrorMessage.showException(e,"");
					}					
					connTypeComboBox.setModel(new DefaultComboBoxModel(new String[] {"Database"}));
				}
			}
		});
		dbTypeComboBox.setModel(new DefaultComboBoxModel(new String[] {"Oracle", "MySQL"}));
		dbTypeComboBox.setEditable(false);
		dbTypeComboBox.setBackground(new Color(240, 230, 140));
		{
			buttonPane = new JPanel();
			buttonPane.setBackground(UIManager.getColor("CheckBox.focus"));
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
					}
				});
				okButton.setBackground(new Color(204, 204, 102));
				okButton.setAction(action_1);
				//okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("EXIT");
				cancelButton.setBackground(new Color(204, 204, 102));
				cancelButton.setAction(action);
				//cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		JLabel lblPort = new JLabel("Port");
		lblPort.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPort.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		portTextField = new JTextField();
		portTextField.setText(defaultOraclePort);
		portTextField.setFont(new Font("Dialog", Font.PLAIN, 14));
		portTextField.setDropMode(DropMode.INSERT);
		portTextField.setColumns(25);
		portTextField.setBackground(new Color(240, 230, 140));
		portTextField.setActionCommand("");
		connTypeComboBox = new JComboBox();
		if (((String)(dbTypeComboBox.getSelectedItem()))!=null){
			if (((String)(dbTypeComboBox.getSelectedItem())).equals("Oracle") )		
				connTypeComboBox.setModel(new DefaultComboBoxModel(new String[] {"Service_Name", "SID"}));
			if (((String)(dbTypeComboBox.getSelectedItem())).equals("MySQL") )		
			connTypeComboBox.setModel(new DefaultComboBoxModel(new String[] {"Database"}));
		}else connTypeComboBox.setModel(new DefaultComboBoxModel(new String[] {""}));
		
		connTypeComboBox.setEditable(false);
		connTypeComboBox.setBackground(UIManager.getColor("CheckBox.focus"));
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
								.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING, false)
									.addComponent(connTypeComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(lblHostUrl, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(lblDatabase, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(lblQueryDefintion, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addGroup(gl_contentPanel.createSequentialGroup()
										.addGap(8)
										.addComponent(lblDefitionFilPath)))
								.addComponent(lblPassword, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
								.addComponent(comboBoxFileDef, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 296, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
									.addComponent(passwordField, Alignment.LEADING)
									.addComponent(txtServiceName, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
										.addComponent(textHost, 0, 0, Short.MAX_VALUE)
										.addComponent(dbTypeComboBox, Alignment.LEADING, 0, 190, Short.MAX_VALUE)
										.addComponent(comboBoxStoredConns, Alignment.LEADING, 0, 190, Short.MAX_VALUE)
										.addComponent(textUserName, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
									.addGap(3)
									.addComponent(lblPort, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(portTextField, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)))
							.addGap(114))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(buttonPane, GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
							.addContainerGap())))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnNewButton)
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(comboBoxFileDef, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblDefitionFilPath))
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(lblQueryDefintion)
						.addComponent(comboBoxStoredConns, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(dbTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblDatabase, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(textUserName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(textHost, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblHostUrl)
						.addComponent(lblPort, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
						.addComponent(portTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtServiceName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(connTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(12)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(passwordField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPassword))
					.addGap(26)
					.addComponent(buttonPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(363))
		);
		contentPanel.setLayout(gl_contentPanel);
		logger.info("************************************* Database login window is up and running ***************************************");
	}	private class SwingAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public SwingAction() {
			putValue(NAME, "EXIT");
			putValue(SHORT_DESCRIPTION, "Exit");
		}
		public void actionPerformed(ActionEvent e) {
			logger.info("User ends the application");
			DBConnections.finish();
		}
	}
	private class SwingAction_1 extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public SwingAction_1() {
			putValue(NAME, "OK");
			putValue(SHORT_DESCRIPTION, "tries to establish a DB Connection with JDBC using the credentials you entered");
		}
		public void actionPerformed(ActionEvent e) {
			try {
				QueryDefinitionParser.storeFileLocator (storedFileLocators, iniFile );				
				boolean  succes;
				String host = textHost.getDocument().getText(0,textHost.getDocument().getLength());
				String dbType=(String)(dbTypeComboBox.getSelectedItem());
				String serviceName = txtServiceName.getDocument().getText(0,txtServiceName.getDocument().getLength());
				String userName = textUserName.getDocument().getText(0,textUserName.getDocument().getLength() );
				String connectionName=((String)(comboBoxStoredConns.getModel().getSelectedItem()));
				String port = portTextField.getDocument().getText(0,portTextField.getDocument().getLength());
				String connType = ((String)(connTypeComboBox.getModel().getSelectedItem())); 
				//succes=DataJumper2.startDataJumper(host,serviceName ,userName ,passwordField.getDocument().getText(0,passwordField.getDocument().getLength()),selectedFile, dialog);
				
				try {
					if (selectedFile==null ||  new File ( selectedFile).exists()==false)
					{
					succes=false;
					logger.error("The definition file '"+selectedFile+"' cannot be found\n" );
					ErrorMessage.showException("The definition file '"+selectedFile+"' cannot be found\n" );
					}
					else//startet die application
					{		
						succes=new DataJumper().startDataJumper(
								dbType,connectionName,host,port,connType,serviceName ,userName, 
								Encrpt.encrypt(passwordField.getDocument().getText(0,passwordField.getDocument().getLength()),kb),selectedFile, dialog
								);
					passwordField.getDocument().remove(0,passwordField.getDocument().getLength());
					}
				} catch (Exception e1) {
					ErrorMessage.showException(e1,"Error encountered when while connecting to database");
					logger.error("Error encountered when while connecting to database usign these credentials:\n" +
					"host="+host+", port="+port+", "+connType+"="+serviceName+",user="+userName+"\n"+e1);
					succes=false;
				}
				
				if(succes)	
				{//Nachdem die Loginsdetails erfolgreich angewendet wurden, wird ausgelotet ob etwaige Ergänzungen der Logindetails stattgefunden haben
				logger.info("The view defnition file was sucessfully executed using the connection "+connectionName);
				Map<String,String> newConnDet = new HashMap<String,String>();
				newConnDet.put("DBTYPE",dbType);
				newConnDet.put("CONNECTIONNAME",connectionName);
				newConnDet.put("HOST",host);
				newConnDet.put("PORT",port);
				newConnDet.put("CONNECTIONTYPE",connType);
				newConnDet.put("SERVICENAME",serviceName);
				newConnDet.put("USERNAME",userName);

					//host, dbType, serviceName, userName, connectionName, connType, port
				if (connectionDetails!=null && connectionDetails.size()>0)
						{
						boolean newConnectionNameEntered = true;
						for (int t=0; t<comboBoxStoredConns.getModel().getSize();t++){
							if (    
									//((String) ( comboBoxStoredConns.getModel().getSelectedItem())).trim().length()==0 ||//...Wenn der User den ConnectionNamen gelöscht hat	
								//oder wenn der möglicherweise manuell veränderte DB-Verbindungsname gleich ist irgendeinem der gespeicherten Verbindungsnamen...  
								((String)(comboBoxStoredConns.getModel().getElementAt(t))).equals(connectionName)
							   ){//...und somit keine neue Verbindungsdetails wurde, dann Prüfe ob sich vielleicht die login-Details verändert haben
								newConnectionNameEntered=false;
								logger.info("The used database connection name "+connectionName+" already exists in the definition file");
							 if ( !(dbType+host+port+connType+serviceName+userName).
										equals((connectionDetails.get(t).get("DBTYPE")+
												connectionDetails.get(t).get("HOST")+
												connectionDetails.get(t).get("PORT")+
												connectionDetails.get(t).get("CONNECTIONTYPE")+
												connectionDetails.get(t).get("SERVICENAME")+
												connectionDetails.get(t).get("USERNAME")
												) )
								   )
								{//Ja, die login-Details wurden manuell geändert. Und wenn diese Logindaten nicht schon unter einem anderen ConnectionNamen existieren... 
									boolean newConnDetailsEntered=true; 
//									for (Map<String,String> conns : connectionDetails){
//										if ( (host+serviceName+userName).toLowerCase().equals( 
//												(conns.get("HOST")+
//												 conns.get("SERVICENAME")+
//												 conns.get("USERNAME")).toLowerCase() ) )
//										{
//											newConnDetailsEntered=false;
//											break;
//										}
//									}
									if(newConnDetailsEntered)// ...dann könnte man sie in der Defnitionsdatei die neuen Logindaten unter denselben ConnectionNamen speichern"
										{
										logger.info("The stored connection credentials "+connectionDetails.get(t)+"\nare not matching the last used connection credentials "+newConnDet);
										//System.out.println("Jetzt könnte man in der Defnitionsdatei die neuen Logindaten unter denselben ConnectionNamen speichern\n"+(host+serviceName+userName)+" != "+ connectionDetails.get(t).get("HOST")+connectionDetails.get(t).get("SERVICENAME")+connectionDetails.get(t).get("USERNAME")  );
										QueryDefinitionParser.amendConnectionDetailsXML(selectedFile,newConnDet,"");
										logger.info("The stored connection credentials were sucessfully updated");
										connectionDetails = QueryDefinitionParser.loadConnectionDetails(selectedFile);
										}
								}	
								else 
									logger.info("The last used connection credentials:\n"+newConnDet+"\nare are allready stored in the database connection named "+connectionName);
									//System.out.println("Name und connectionDetails sind gleich geblieben, nichts passiert");
								break;
								}
							} 
						if (newConnectionNameEntered==true  ){//Ab hier muss der connection name neu oder leer sein 
							//System.out.println("Element "+comboBoxStoredConns.getModel().getSelectedItem()+" is neu.
							if (newConnDet.get("CONNECTIONNAME").trim().length()==0){//wenn der ConnectionName nicht eingegeben wurde, wird er auf username + service name zusammengesetzt
								newConnDet.put("CONNECTIONNAME",newConnDet.get("USERNAME")+" on "+newConnDet.get("SERVICENAME")+" "+(new SimpleDateFormat("hmmss").format(new Date())) );
								connectionName=newConnDet.get("CONNECTIONNAME");
							}
							
							logger.info("New database connection name found:"+ connectionName );
							boolean newConnDetailsEntered=true; 
							for (Map<String,String> conns : connectionDetails){//Prüfe nun die, ob connectionDetails mit dem neuen Namen mit den Details irgendeiner stored connection übereinstimmen "
								if ((dbType+host+port+connType+serviceName+userName).
										equals((conns.get("DBTYPE")+
												conns.get("HOST")+
												conns.get("PORT")+
												conns.get("CONNECTIONTYPE")+
												conns.get("SERVICENAME")+
												conns.get("USERNAME")
												)) 
									)
								{
									newConnDetailsEntered=false;
									//System.out.println("Überscheibe den Namen der Connection "+conns.get("CONNECTIONNAME")+" mit "+comboBoxStoredConns.getModel().getSelectedItem() );
									logger.info("The connection credentials are already stored in the definition file with the connection name "+ conns.get("CONNECTIONNAME"));
									logger.info("The connection name "+ conns.get("CONNECTIONNAME")+" will be renamed to "+connectionName);
									QueryDefinitionParser.amendConnectionDetailsXML(selectedFile, newConnDet,conns.get("CONNECTIONNAME") );//Änderung speichern
									connectionDetails = QueryDefinitionParser.loadConnectionDetails(selectedFile);//...und anwenden
									comboBoxStoredConns.removeItem(conns.get("CONNECTIONNAME"));//Der alte Name wird entfernt
									comboBoxStoredConns.addItem( connectionName);//und der Neue hinzugefügt
									comboBoxStoredConns.setSelectedItem(connectionName);//
									break;
								}
							}
							//Wenn die Verbindungsdetails neu und der ConnectionName auch neu sind dann speichere diese Neue connection."  );
							if(newConnDetailsEntered)
								{
								if (newConnDet.get("CONNECTIONNAME").trim().length()==0){//wenn der ConnectionName nicht eingegeben wurde, wird er auf username + service name zusammengesetzt
									newConnDet.put("CONNECTIONNAME",newConnDet.get("USERNAME")+" on "+newConnDet.get("SERVICENAME")+" "+ (new SimpleDateFormat("hmmss").format(new Date())) );
									connectionName=newConnDet.get("CONNECTIONNAME");
								}
									//System.out.println("Erzeuge eine neue Connection mit Namen "+comboBoxStoredConns.getModel().getSelectedItem()+" und den Verbindungs details "+host+" "+serviceName+" "+userName);
									logger.info("The sucessfully used database connection credentials must be new since they were not found in the definition file");
									logger.info("The following connection credentials are added in the definition file:\n"+newConnDet);
									QueryDefinitionParser.addConnectionDetailsToDefFile( selectedFile, newConnDet );
									connectionDetails = QueryDefinitionParser.loadConnectionDetails(selectedFile);
									comboBoxStoredConns.addItem(connectionName);	
									comboBoxStoredConns.setSelectedItem(connectionName);
								}
							}
						}else  {//Wenn keine Verbindungsdetails in Definition file gespeichert wurden und es wurde erfolgreich eine Verbindung hergestellt, dann muss der user die Verbindungsdetails eingegeben haben. Diese werden nun gespeichert.     
							if (newConnDet.get("CONNECTIONNAME").trim().length()==0)//wenn der ConnectionName nicht eingegeben wurde, wird er auf username + service name zusammengesetzt
								newConnDet.put("CONNECTIONNAME",newConnDet.get("USERNAME")+" on "+newConnDet.get("SERVICENAME"));
							QueryDefinitionParser.addConnectionDetailsToDefFile( selectedFile, newConnDet );
							connectionDetails = QueryDefinitionParser.loadConnectionDetails(selectedFile);
							comboBoxStoredConns.removeAllItems();
							comboBoxStoredConns.addItem( newConnDet.get("CONNECTIONNAME"));
						}
				}	
			} catch (BadLocationException e1) {
				logger.error(e1.getMessage()+"\n"+ErrorMessage.showStackTrace(e1.getStackTrace()));
				ErrorMessage.showException(e1,"");
			}
		}
	}
}

class FileFilt extends FileFilter {

String extension= ".xml";
String description ="XML file";
    public boolean accept(File f) {
	if(f != null) {
	    if(f.isDirectory())	return true;
	    if (f != null) return (f.getName().contains(extension) );
  		};
	return false;
	}
    public String getDescription() {
		return this.description;
		}
    public void setDescription(String description) {
	this.description = description;
 }
}