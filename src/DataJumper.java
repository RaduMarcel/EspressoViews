import java.sql.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.awt.event.*;

import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.awt.*;
import java.io.File;
public class DataJumper {
private static Logger logger = LogManager.getLogger(DataJumper.class.getName());	
//private static final long serialVersionUID = 1L;	
protected static Connection conn;
protected static Set<String> createdTables = new HashSet<String>();
protected static StringBuilder warnings = new StringBuilder(""); 
protected String viewOption ="detailed";
protected boolean dataSuccess;
List<AnfragePlan> allePlaene;
protected JTree theGUITree;
public boolean startDataJumper (
		final String dbType, final String connectionName, final String host,final String port,final String connType,final String serviceName, final String userName, 
		final String password, final String file, final JDialog dialog) {

final String defFileNameOnly=new File(file).getName();	
//das Hauptfenster
	final JFrame frame = new JFrame(defFileNameOnly+" : 	"+connectionName);
	frame.setVisible(true);
	frame.setSize(1000, 750);
	frame.setExtendedState(Frame.MAXIMIZED_BOTH );
	frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
//die zweiteilung des Hauptfensters in einem... 
	final JSplitPane splitPane = new JSplitPane();
	splitPane.setPreferredSize(new Dimension(292, 328));
	splitPane.setOneTouchExpandable(true);
	splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
	splitPane.setDividerLocation(10000);
	splitPane.setResizeWeight(0.86);
	splitPane.setDividerSize(13);
	splitPane.resetToPreferredSizes();	
//...oberen Teil, hier wird später der JTree eingepflanzt, davor aber wird er von dem progress message text besetzt  	
	final JScrollPane scrollPaneTop =  new JScrollPane();
	final JTextArea txtProgressMessage = new JTextArea();
	splitPane.setLeftComponent(scrollPaneTop);
    txtProgressMessage.setFont(new Font("Monospaced", Font.BOLD, 14));
    txtProgressMessage.setBackground(new Color(197,211,229)); 	  
    scrollPaneTop.setViewportView(txtProgressMessage);
	txtProgressMessage.setEditable(false);	
// und in einem Unterteil, hier werden nach dem report-Aufbau die progress messages und die warnings angezeigt	
	final JTabbedPane tabbedPaneBottom = new JTabbedPane();
	splitPane.setRightComponent(tabbedPaneBottom);
	final JScrollPane scrollPaneBottom =  new JScrollPane();
	tabbedPaneBottom.add("Warnings",scrollPaneBottom);
	final JTextArea txtwarningMessage = new JTextArea();
	scrollPaneBottom.setViewportView(txtwarningMessage);
	txtwarningMessage.setFont(new Font("Monospaced", Font.BOLD, 13));
	txtwarningMessage.setBackground(new Color(197,211,229)); 	      
	txtwarningMessage.setCaretPosition(0);
	txtwarningMessage.setEditable(false);		
	frame.setContentPane(splitPane);
	dialog.setVisible(false); //das log-in-Fenster verschwindet
	
	//Menu Bar		
			JMenuBar menuBar = new JMenuBar();
			menuBar.setBackground(new Color(0, 153, 204));
			frame.setJMenuBar(menuBar);
			Color menuColor = new Color(0, 153, 255);
			Font menuFont = new Font("Corbel", Font.BOLD, 16); 
			Font menuItemFont = new Font("Corbel", Font.BOLD, 14);
	//Menu File		
			JMenu dateiMenu = new JMenu("File");
				dateiMenu.setBackground(menuColor);
				dateiMenu.setFont(menuFont);
				menuBar.add(dateiMenu);
			
			JMenuItem loadFileMenuItem = new JMenuItem("Open Definition");
			loadFileMenuItem.setBackground(menuColor);
			loadFileMenuItem.setFont(menuItemFont);
			loadFileMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					dialog.setVisible(true);
					logger.info("User set the database login window to visible");	
				}
			});
			dateiMenu.add(loadFileMenuItem);
			JMenuItem exitMenuItem = new JMenuItem("Exit");
			exitMenuItem.setBackground(menuColor);
			exitMenuItem.setFont(menuItemFont);
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					logger.info("User closed the window "+defFileNameOnly+": "+connectionName );
					frame.dispose();
					dialog.setVisible(true);
				}
			});
			dateiMenu.add(exitMenuItem);

	//Menu View Mode
			JMenu ansichtMenu = new JMenu("View Mode");
			ansichtMenu.setBackground(menuColor);
			ansichtMenu.setFont(menuFont);
			menuBar.add(ansichtMenu);
			
			JMenuItem detailedMenuItem = new JMenuItem("Detailed");
			detailedMenuItem.setBackground(menuColor);
			detailedMenuItem.setFont(menuItemFont);
			detailedMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					removeAllTreeComponents(frame,1);
					viewOption="detailed";
					logger.info("View is generated with view option 'detailed'");
//					Thread queryThread = new Thread() {
//						public void run(){
							splitPane.setLeftComponent(generateTheTree (dbType,connectionName,host, port, connType,serviceName, userName, password, file, txtProgressMessage,txtwarningMessage,frame,dialog,tabbedPaneBottom,false));
//						}
//					};
//					queryThread.start();

				}
			});

			JMenuItem compactMenuItem = new JMenuItem("Compact");
			compactMenuItem.setBackground(menuColor);
			compactMenuItem.setFont(menuItemFont);
			compactMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					removeAllTreeComponents(frame,1);
					viewOption="compact";
					logger.info("View is generated with view option 'compact'");
//					Thread queryThread = new Thread() {
//						public void run(){
							splitPane.setLeftComponent(generateTheTree (dbType,connectionName,host, port, connType,serviceName, userName, password, file, txtProgressMessage,txtwarningMessage,frame,dialog,tabbedPaneBottom,false));
//						}
//					};
//					queryThread.start();
				}
			});
			JMenuItem structureMenuItem = new JMenuItem("Structure Only");
			structureMenuItem.setBackground(menuColor);
			structureMenuItem.setFont(menuItemFont);
			structureMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					removeAllTreeComponents(frame,1);
					logger.info("View is generated with view option 'structure'");
					viewOption="structure";
//					Thread queryThread = new Thread() {
//						public void run(){
							splitPane.setLeftComponent(generateTheTree (dbType,connectionName,host, port, connType,serviceName, userName, password, file, txtProgressMessage,txtwarningMessage,frame,dialog,tabbedPaneBottom,false));
//						}
//					};
//					queryThread.start();
				}
			});
			ansichtMenu.add(detailedMenuItem);
			ansichtMenu.add(compactMenuItem);
			ansichtMenu.add(structureMenuItem);
			
	//Menu Help
			JMenu helpMenu = new JMenu("Help");
			helpMenu.setBackground(menuColor);
			helpMenu.setFont(menuFont);
			menuBar.add(helpMenu);
			
			JMenuItem tutorialMenuItem = new JMenuItem("Tutorial");
			tutorialMenuItem.setBackground(menuColor);
			tutorialMenuItem.setFont(menuItemFont);
			JMenuItem aboutMenuItem = new JMenuItem("About");
			aboutMenuItem.setBackground(menuColor);
			aboutMenuItem.setFont(menuItemFont);
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						AboutWindow dialog = new AboutWindow();
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dialog.setVisible(true);
					} catch (Exception e) {
						logger.error(e);
						ErrorMessage.showException(e,"");
					}
					return;
				}
			});
			helpMenu.add(tutorialMenuItem);
			helpMenu.add(aboutMenuItem);
// Collapse Tree Button			
			final JButton btnCollapse= new JButton("Collapse Tree");						
						btnCollapse.setFont(menuFont);
						btnCollapse.setForeground(new Color(102, 0, 102));
						btnCollapse.setBackground(new Color(100, 204, 51));
						btnCollapse.setVisible(true);
						btnCollapse.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
						menuBar.add(btnCollapse);
//Reload Button		
			final JButton btnReload = new JButton("Reload!");
			btnReload.setFont(menuFont);
			btnReload.setForeground(new Color(102, 0, 102));
			btnReload.setBackground(new Color(100, 204, 51));
			btnReload.setVisible(true);
			btnReload.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
			menuBar.add(btnReload);
			
			frame.setVisible(true);

		Thread queryThread = new Thread() {
			public void run(){
				final JScrollPane scrollPane = generateTheTree(dbType,connectionName,host, port, connType,serviceName, userName, password, file, txtProgressMessage,txtwarningMessage,frame,dialog,tabbedPaneBottom,true);
//			if (scrollPane==null) {
//				frame.dispose();
//				return false;	
//			}			
				splitPane.setLeftComponent(scrollPane);
			}
		};
		queryThread.start();	

			
			btnReload.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					removeAllTreeComponents(frame,1);
					allePlaene=null;
					//btnReload.setVisible(false);
					logger.info("View defintion file is reloaded upon user request");
					splitPane.setLeftComponent(generateTheTree (dbType,connectionName,host,port, connType,serviceName, userName, password, file, txtProgressMessage,txtwarningMessage,frame,dialog,tabbedPaneBottom,true));
					theGUITree.setSelectionRow(1);
					btnReload.setVisible(true);
				}
			});
			btnCollapse.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					btnCollapse.setVisible(false);
					collapseTree(theGUITree);
					theGUITree.expandRow(0);theGUITree.expandRow(1);
					theGUITree.setSelectionRow(1);
					btnCollapse.setVisible(true);
				}
			});
	return true;		
} // Ende Main

private JScrollPane generateTheTree(final String dbType, final String connectionName, final String host,final String port, final String connType,
		final String serviceName, final String userName, final String password,
		final String file, final JTextArea txtProgressgMessage, JTextArea txtwarningMessage, final JFrame frame, final JDialog dialog, final JTabbedPane tabbedPaneBottom, boolean refreshFlag) {
//JTree tree = null;
	//this.allePlaene=null;
	DefaultMutableTreeNode wurzel = null; 
	DefaultTreeModel model=null;
	TreeCellRenderer renderer =null;

//Daten werden abgefragt und der Datenbaum wird erzeugt  
txtProgressgMessage.setText("Processing the report definition file "+file+" on datatabase connection "+ connectionName+"...\n\n");
long startTime =  new Date().getTime();  
if (refreshFlag){
conn = DBConnections.getNewConnection(dbType,serviceName, host, port, connType,userName, password);
if (conn==null) {
	frame.dispose();
	dialog.setVisible(true);
	return null;}
txtProgressgMessage.append("Successfully connected to the datatabase.\n");
	try {
		if (allePlaene==null)
			allePlaene = QueryDefinitionParser.generateAnfragePlan(file);
		if (allePlaene==null||allePlaene.isEmpty()){ 
			txtProgressgMessage.append("\n\nReport definition file validation ended with error!");
			return new JScrollPane(txtProgressgMessage); 	
		}
		//******************************+überprüft die uniquness der query labels innerhlad der gesamten report definition
		LinkedList<String> allQueryLabels = new LinkedList<String>();
		HashSet<String> hashQueryLabels = new HashSet<String>();
		for (AnfragePlan basePlan:allePlaene) {
			allQueryLabels.add(basePlan.getQueryName());
			hashQueryLabels.add(basePlan.getQueryName());
		}
		for (String label: hashQueryLabels){
			allQueryLabels.remove(label);
		}
		if (allQueryLabels.size()>0){
			ErrorMessage.showException("The name of a report entity must be unique within one EspressoViews report definition!\n" +
					"The report entity name(s) "+allQueryLabels.toString().replace("]", "").replace("[", "")+" is(are) not unique");

			txtProgressgMessage.append("The name of a report entity must be unique within one EspressoViews report definition!\n" +
					"The report entity name(s) "+allQueryLabels.toString().replace("]", "").replace("[", "")+" is(are) not unique.\nCorrect the definition file and reload.\n");
			
			logger.error("The name of a report entity must be unique within one EspressoViews report definition!\n" +
			"The report entity name(s) "+allQueryLabels.toString()+" is(are) not unique");
			warnings.delete(0, warnings.length());
			txtProgressgMessage.append("\n\nReport definition file validation ended with error!");
			return new JScrollPane(txtProgressgMessage); 	
			}
		
	} catch (Exception e) {
		ErrorMessage.showException(e,"The reading and processing of the definition file terminated with error");
		logger.error("The reading and processing of the definition file terminated with error\n"+e);
		warnings.delete(0, warnings.length());
		txtProgressgMessage.append("\n\nReport definition file validation ended with error!");
		return new JScrollPane(txtProgressgMessage); 			
	}	
}
long defintionFileParsed =  new Date().getTime();
txtProgressgMessage.append("Report definition file was loaded and validated in "+( (float)(defintionFileParsed - startTime) /1000)+" seconds\n");
txtProgressgMessage.append("Begin to query the datatabase\n");
	
	if (refreshFlag){
	try{
		boolean success= AnfragePlan.generateAllTempTables(conn,allePlaene.get(0));
		if (!success) {
			warnings.delete(0, warnings.length());
			DBConnections.dropAllTempTables();
			txtProgressgMessage.append("\n\nData retrieval preparation ended with error!");
			return new JScrollPane(txtProgressgMessage); 	
			}
	}catch (Exception e) {
		warnings.delete(0, warnings.length());		
		ErrorMessage.showException(e,"An unexpected error occured during the initial stage of the data retrieval");
		logger.error("An unexpected error occured during the initial stage of the data retrieval\n"+e);
		txtProgressgMessage.append("\n\nData retrieval preparation ended with error!");
		return new JScrollPane(txtProgressgMessage); 	
	}
	try{		
		dataSuccess=true;
		dataSuccess= allePlaene.get(0).generateData(conn,txtProgressgMessage);
		if (!dataSuccess) {
			warnings.delete(0, warnings.length());
			txtProgressgMessage.append("\n\nData retrieval ended with error!");
			return new JScrollPane(txtProgressgMessage); 	
			}
		logger.info("Data retrieval ended successfully");
		//DisplayData.generateBlockDisplay(allePlaene.get(0));
	}catch (Exception e) {
		warnings.delete(0, warnings.length());
		ErrorMessage.showException(e,"An unexpected error occured while retrieving and caching the data");
		logger.error("An unexpected error occured while retrieving and caching the data\n"+e);
		txtProgressgMessage.append("\n\nData retrieval ended with error!");
		return new JScrollPane(txtProgressgMessage); 	
	}
}
long dataRetrivalFinished =  new Date().getTime();	
txtProgressgMessage.append("Database retrieval ended without errors in "+((float)(dataRetrivalFinished - defintionFileParsed)/1000)+" seconds\n");
logger.info("Data display phase starts producing the view option "+viewOption);
txtProgressgMessage.append("Start to generate the report display.\n");
//An diesem Punkt existiert eine Anfrage-hierarchie mit angehängten Datenblöcken. Ab jetzt werden die einzelnen Datenzeilen zu einem Datenbaum verschachtelt.  
	try{		
		if (viewOption.equals("detailed")||viewOption.equals("compact")){
			wurzel = DisplayData.generateJointDisplay(allePlaene.get(0),"Definition File "+new File(file).getName()+ " applied on "+connectionName+". Captured on "+Calendar.getInstance().getTime().toString(),viewOption);
			logger.info("Raw display data generated for Definition File "+new File(file).getName()+ " applied on "+connectionName+". Captured on "+Calendar.getInstance().getTime().toString());
		}
		if (viewOption.equals("structure")){
			wurzel = DisplayData.generateStructDisplay(allePlaene.get(0),"Overview of the definition file "+file,viewOption);
			logger.info("Generated raw data for the overview of the definition file "+file,viewOption);
		}
long dataTreeGenerated =  new Date().getTime();		
		int[] totalChildren = DataNode.getTreeNodeCount(wurzel.getNextNode());
		txtProgressgMessage.append("Data tree generated in "+(float)(dataTreeGenerated-dataRetrivalFinished)/1000  +" seconds\n");
		logger.info("The generation of the graphical user interface started");
		model = new DefaultTreeModel(wurzel);
		final JTree tree = new JTree(model);
		renderer = new ResultLineRenderer();
		//  renderer.setClosedIcon(null);
		tree.setCellRenderer(renderer);
		
		final JPopupMenu popupTreeChoice = new JPopupMenu(); //Popup menue for right klick 
        JMenuItem mi = new JMenuItem("Add a restriction");
        mi.setActionCommand("addRestriction");
        popupTreeChoice.add(mi);
        mi = new JMenuItem("Open in a table");
        mi.setActionCommand("tableView");
        popupTreeChoice.add(mi);  
        popupTreeChoice.setOpaque(true);
        popupTreeChoice.setLightWeightPopupEnabled(false);
        
        tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (1==2 && arg0.getButton()==MouseEvent.BUTTON3){//if right-clicked, but switched off for the moment 
					int selRow = tree.getRowForLocation(arg0.getX(), arg0.getY());
			         TreePath selPath = tree.getPathForLocation(arg0.getX(), arg0.getY());
			         DataNode selectedDataNodeLabel =null;
			                 if (selRow>-1){
			                	 popupTreeChoice.show( (JComponent)arg0.getSource(), arg0.getX(), arg0.getY() );
			                	 
			                	tree.setSelectionPath(selPath); 
			                    tree.setSelectionRow(selRow);
			                    String nextLabel = "";
			                    DefaultMutableTreeNode currNode= (DefaultMutableTreeNode)(selPath.getLastPathComponent());
			                    if (currNode.getUserObject() instanceof DataNode){
			                    	selectedDataNodeLabel = (DataNode)(currNode.getUserObject()); 
			                    	if (selectedDataNodeLabel.isLabel && selectedDataNodeLabel.getParent()!=null)
			                    		nextLabel= selectedDataNodeLabel.values;
			                    	else{
			                    		if (selectedDataNodeLabel.getParent()!=null ){
			                    			selectedDataNodeLabel=selectedDataNodeLabel.getParent();
			                    			nextLabel= selectedDataNodeLabel.values;
			                    		}			                    		
			                    	}
			                    }
			                    if (currNode.getUserObject() instanceof String){
			                    	if (currNode.getNextNode().getUserObject() instanceof DataNode){
			                    		selectedDataNodeLabel = ((DataNode)(currNode.getNextNode().getUserObject())).getParent();
			                    		nextLabel=selectedDataNodeLabel.values;
			                    	//			(String)(currNode.getUserObject());	
			                    	}
			                    	//tableModel= new DefaultTableModel			                    	
			                    }
			                  if (selectedDataNodeLabel!=null){
			                	  String[] columnName = selectedDataNodeLabel.getChildren().get(0).getColumnArray(true);
			                	  String[][]columnData = new String [selectedDataNodeLabel.getChildren().size()-1][columnName.length];
			                	  for (int t=1;t<selectedDataNodeLabel.getChildren().size()-1;t++){
			                		  columnData[t-1]= selectedDataNodeLabel.getChildren().get(t).getValueArray(true);
			                	  }
			                	  JTable dataTable = new JTable(columnData, columnName);
			                	  dataTable.setFillsViewportHeight(true);
			                	  JScrollPane scrollPane = new JScrollPane(dataTable);
			                	  tabbedPaneBottom.add(nextLabel ,scrollPane);
			                	  dataTable.setRowSelectionAllowed(false);
			                	  dataTable.setCellSelectionEnabled(true);
			                	  dataTable.setColumnSelectionAllowed(true);
//			                	  int index = tabbedPaneBottom.indexOfTab(nextLabel);
//			                	  JPanel pnlTab = new JPanel(new GridBagLayout());
//			                	  pnlTab.setOpaque(false);
//			                	  JLabel lblTitle = new JLabel(nextLabel);
//			                	  JButton btnClose = new JButton("x");
//			                	  GridBagConstraints gbc = new GridBagConstraints();
//			                	  gbc.gridx = 0;
//			                	  gbc.gridy = 0;
//			                	  gbc.weightx = 1;
//			                	  pnlTab.add(lblTitle, gbc);
//			                	  gbc.gridx++;
//			                	  gbc.weightx = 0;
//			                	  pnlTab.add(btnClose, gbc);
//			                	  tabbedPaneBottom.setTabComponentAt(index, pnlTab);
			                	  for (int t=0; t< frame.  getComponentCount();t++){
			                		  System.out.println(t+"  "+frame.getComponent(t));  
			                		  
			                	  }
			                	  
			                	  
			                    }
								
			                 }
				}
			}
		});
		final JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setOpaque(true);
		//tree.setBackground(new Color(204, 204, 250));
		//tree.setBackground(new Color(135, 206, 250));
		//tree.setBackground(new Color(0, 163, 255));
		tree.setBackground(new Color(197,211,229));
		tree.setFocusCycleRoot(true);
//		tree.setScrollsOnExpand(true);
		this.theGUITree=tree;
		if (!viewOption.equals("structure")){
			tree.expandRow(1);
			tree.setSelectionRow(2);
		}
		else{
			expandTree(tree);
		}
		DBConnections.dropAllTempTables();
		tree.requestFocus();
long GUIGenerated =  new Date().getTime();
//		tree.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyPressed(KeyEvent arg0) {
//				//System.out.println(arg0.getKeyCode());
//				if (arg0.getKeyCode() == 116){ 
//				frame.remove(scrollPane);
//				generateDetailedTree (host,serviceName, userName, password, file, frame);
//				}
//			}
//		});		
		//Text zum appenden
//		if (warnings.length()>0) {
//			WarningMessage.showWarning(warnings.toString());
//			warnings.delete(0, warnings.length());
//		}
		if (warnings.length()>0) 
			warnings.insert(0, "Report validation warnings:\n\n");
		txtwarningMessage.setText(txtProgressgMessage.getText()+"\n"+				
				"Graphical User Interface generated in "+ (float)(GUIGenerated-dataTreeGenerated)/1000+" seconds.\n"+
				"Total number of report lines: "+totalChildren[1]+". Total number of report labels: "+totalChildren[0]+".\n\n"+warnings.toString()+
				"\nTo increase or decrease the warning level of the report validations use the tag <WARNINGLEVEL> in iniSettings.xml and the values high or low (and restart application after each change).\nCurrent warning level is set to "+DataBaseLogin3.warningLevel);
		

		warnings.delete(0, warnings.length());
		
		//Baum wird im Frame eingepflanzt

		logger.info("The generation of the graphical user interface ended");
		return scrollPane; 

	}catch(Exception ex){
		DBConnections.dropAllTempTables();
		ErrorMessage.showException(ex,"Error encountered during the generation of the graphical user interface ended");
		logger.error("Error encountered during the generation of the graphical user interface ended\n"+ex);
		frame.dispose();
		return null;
	}
}
	
	
static void  removeAllTreeComponents(Container comp, int level ){
	Component[] comps=comp.getComponents();
	for (int t=0;t<comps.length;t++ ){
		//System.out.println(level+"  " + comps[t].getAccessibleContext().getAccessibleDescription()+" "+comps[t].getAccessibleContext().getAccessibleName() +" "+comps[t].getAccessibleContext());
		if (comps[t] instanceof Container && ((Container)comps[t]).getComponents().length>0)
		{
			removeAllTreeComponents(((Container)comps[t]),level+1);
			}
		if (comps[t] instanceof JTree) 
			{
			//System.out.println("Peng!");
			comp.remove(comps[t]);
			}
	}
}

private static void expandTree(JTree tree) {
    TreeNode root = (TreeNode) tree.getModel().getRoot();
    expandTree(tree, new TreePath(root));
}
private static void expandTree(JTree tree, TreePath path) {
    TreeNode node = (TreeNode) path.getLastPathComponent();

    if (node.getChildCount() >= 0) {
        Enumeration<TreeNode> enumeration =  node.children();
        while (enumeration.hasMoreElements()) {
            TreeNode n =  enumeration.nextElement();
            TreePath p = path.pathByAddingChild(n);
            expandTree(tree, p);
        }
    }
    tree.expandPath(path);
 }

 
private static void collapseTree(JTree tree) {
    TreeNode root = (TreeNode) tree.getModel().getRoot();
    collapseTree(tree, new TreePath(root)); 
}
private static void collapseTree(JTree tree, TreePath path) {
    TreeNode node = (TreeNode) path.getLastPathComponent();

    if (node.getChildCount() >= 0) {
        Enumeration<TreeNode> enumeration =  node.children();
        while (enumeration.hasMoreElements()) {
            TreeNode n =  enumeration.nextElement();
            TreePath p = path.pathByAddingChild(n);
            collapseTree(tree, p);
        }
    }
    tree.collapsePath(path);
 }
static boolean containsCaseInsensitive (Collection<String> haufen, String teil ){
	boolean result= false;
	for (String str:haufen){
		if (str.toUpperCase().equals(teil.toUpperCase())){
			result=true;
			break;
		}
	}
	return result;
}


}//end class DataJumper

//class WindowHandlings extends WindowAdapter {
//	@Override
//	public void windowClosing(WindowEvent e) {
//  frame.getBufferStrategy().dispose();
//	e.getWindow().dispose();
//	DataJumper.finish();
//	}
//	@Override
//	public void windowClosed(WindowEvent e) {
//		e.getWindow().dispose();
//		DataJumper.finish();
//	}
//}


class ResultLineRenderer extends DefaultTreeCellRenderer implements TreeCellRenderer{
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JPanel renderer;
	  JTable table;
	  JLabel label;
	  int lastRowNumber;
	  boolean lastIsExpanded=false;
	  DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();	  
	  
	  public ResultLineRenderer() {
	    renderer = new JPanel(new GridLayout(0, 1));
	    table = new JTable();
	    setClosedIcon(null);
		setOpenIcon(null);
//		JScrollPane scrollPane = new JScrollPane(table);
	  }

	  public Component getTreeCellRendererComponent(JTree tree, Object value,
	      boolean selected, boolean expanded, boolean leaf, int row,
	      boolean hasFocus) {
		  Component returnValue = null;
	      //System.out.println(tree.getSelectionPath()+" Row "+tree.getRowForPath(tree.getSelectionPath())  );
	    if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
	      Object treeObject = ((DefaultMutableTreeNode) value).getUserObject();
	      if (treeObject instanceof DataNode && !((DataNode) treeObject).isLabel && ((DataNode) treeObject).columns!=null) {//ist ein Ergebniszeile mit mindestens eine column
	    	renderer.add(table);
	    	DataNode line = (DataNode) treeObject;
	        //System.out.println("Parent--> "+line + "\nund die "+line.childNumber+" Kinderchen:\n "+line.getChildren());
	        DefaultTableModel tableModel;
	        if ( line.getChildNumber()!=0  && !line.getViewOption().equals("structure") )//Ist die Ergebniszeile nicht das 1. Kind. (viewOption müsste Teil der DataNode line-Information sein, um extra Behandlungen zu implementieren) 
	        	tableModel = new DefaultTableModel(new Object[][] {line.getValueArray(true)},line.getColumnArray(true));//...dann werden nur die Werte angezeigt, nicht auch die column names 	
	        else	
	        	{tableModel= new DefaultTableModel(new Object[][] {line.getColumnArray(true),line.getValueArray(true),},line.getColumnArray(true));//... ansonsten werden Werte und column names angezeigt 
	        	}
	        table.setModel(tableModel);
	        table.setSize(new Dimension(2+ColumnsAutoSizer.getMaxTableRowWith(table),table.getRowHeight(0)*table.getRowCount() ));
	        //table.setFillsViewportHeight(true);
	        table.setFont(new Font("Monospaced", Font.PLAIN, 13));
	        for (int t=0;t<table.getColumnCount();t++){
	        	table.getColumnModel().getColumn(t).setCellRenderer(new DefaultTableCellRenderer(){
					JLabel labl=new JLabel();//Der Inhalt der Tabllenzellen sind JLabels
	                	public Component getTableCellRendererComponent(JTable table,
	                            Object value,
	                            boolean isSelected,
	                            boolean hasFocus,
	                            int row,
	                            int column){
	                		if ( row==0 && table.getRowCount()>1){
	                			labl.setFont(new Font("Tahoma", Font.BOLD, 13));//Column names
	                			labl.setText((String)(table.getModel().getValueAt(row, column)) );
	                			return labl;
	                		}
	                		else {
	                			labl.setFont(new Font("Tahoma", Font.PLAIN, 13)); ////Values
	                			labl.setText((String)(table.getModel().getValueAt(row, column)) );
	                			return labl;
	                		}
	                	}
	                }) ;
	        }
	        ColumnsAutoSizer.sizeColumnsToFit2(table,5,line);
	        renderer.setPreferredSize(
	        		new Dimension((
	        			table.getColumnCount())+line.getRowLength()	 * 2*renderer.getFontMetrics(table.getFont()).charsWidth("ABCdefgxyz".toCharArray(),0,10)/20	
	        			,table.getRowHeight(0)*table.getRowCount())
	        		);
	        if (selected) {
	          //renderer.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
	        	renderer.setBorder(new MatteBorder(1, 1, 1, 1, (Color) Color.RED));
	        	renderer.setBackground(Color.RED);
	        } else {
	          renderer.setBorder(null);
	        }
	        renderer.setEnabled(tree.isEnabled());
	        returnValue = renderer;
	        //System.out.println("  hasFocus:"+ hasFocus+ "  is selected:"+selected+"  is expanded:"+expanded+"  is leaf:"+ leaf+"  row:"+row+ "  lastRowNumber:"+lastRowNumber);
	        if (lastRowNumber==row && expanded && hasFocus &&lastIsExpanded==false && line.getChildren().size()==1){//wenn ein Baumknoten ausgefahren wird und er hat nur ein Kindknoten, dann soll auch das Kindknoten ausgefahren werden 	
	        //System.out.println(" Jetzt!!\n");
	        	tree.expandRow(row+1);
	        }
	      }
	      else
	      if (treeObject instanceof String && ((String) treeObject)!=null) {//Das ist der renderer für Label
	    	String line = (String) treeObject;
	        label = new JLabel(line);
	        //System.out.println(tree.isExpanded(row)+" row:"+row);
    		if (selected) label.setForeground((Color) Color.RED );
    		else 
    			if (tree.isExpanded(row) && row!=0) label.setForeground(new Color(178, 34, 34));
    			else label.setForeground(Color.black);
	        label.setFont(new Font("Tahoma", Font.BOLD, 13));
	        label.setBorder(new MatteBorder(0, 1, 0, 0,  new Color(0, 0, 0)));
	        returnValue = label;
	      }
	    }
	    if (returnValue == null) {//wenn der Datanode etwas anderes als label oder Ergebnisszeile beinhaltet, dann default-Darstellung
	      returnValue = defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	    }
	    if (hasFocus && !leaf) {
	    	lastIsExpanded=expanded;
	    	lastRowNumber=row;
	    	}
	    return returnValue;
	  }
}
