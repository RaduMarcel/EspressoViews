import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class QueryDefinitionParser {
	private static Logger logger = LogManager.getLogger(QueryDefinitionParser.class.getName());
	private static Node result=null;
	private static String definitionFilePath="";
	private static LinkedList<String> defFiles;

public static void showMeTheDoc(Node node, int level ){
	//if (node.getNodeType()==1)
	if (node.hasAttributes()) System.out.println(node.getAttributes());
    System.out.println("Level "+level+DisplayData.multiplyChars(' ',(level-1)*10)+" Der Knoten "+node.getNodeName()
    		//+" mit dem Inhalt "+" Node Text Content: "+node.getTextContent()
    		+" und mit dem Node Type "+node.getNodeType()+" und node value: "+node.getNodeValue()
    		+" hat "+node.getChildNodes().getLength()+" Kinder "
    		);
    	if (node.hasChildNodes()){
			 NodeList children=  node.getChildNodes();
		 for (int t = 0;t<children.getLength();t++){
			showMeTheDoc(children.item(t), level+1);
		 }
	   }
}

public static Node getElementByTagName(Node node, String tagName){
	//Der Gleichheitsvergleich is case insenstive und gibt den zuerst gefundenen Node  
	 //System.out.println(node.getNodeName().toUpperCase()+" = "+tagName.toUpperCase() +" "+node.getNodeName().toUpperCase().equals(tagName.toUpperCase()));
	if (node.getNodeName().toUpperCase().equals(tagName.toUpperCase())) return node;
	else
		{
		result=null;
		rekGetElementByTagName(node,tagName);
		return result;
		}
}
public static void rekGetElementByTagName(Node node, String tagName){
	 for (int t = 0; t<node.getChildNodes().getLength(); t++){
		 if (node.getChildNodes().item(t).getNodeName().toUpperCase().equals(tagName.toUpperCase())) 
			 result = node.getChildNodes().item(t);
		 else
			 rekGetElementByTagName(node.getChildNodes().item(t),tagName);
	 }
}

public static Node getElementByTagName(Node node, String tagName,String tagValue){
	//Der Gleichheitsvergleich is case insenstive und gibt den zuerst gefundenen Node  
	 //System.out.println(node.getNodeName().toUpperCase()+" = "+tagName.toUpperCase() +" "+node.getNodeName().toUpperCase().equals(tagName.toUpperCase()));
	if ( node.getNodeType()==1 &&  
		 node.getNodeName().toUpperCase().equals(tagName.toUpperCase()) && 
		 node.getFirstChild().getNodeValue().trim().equals(tagValue.trim()) ) 
		return node;
	else
		{
		result=null;
		rekGetElementByTagName(node,tagName,tagValue);
		return result;
		}
}
public static void rekGetElementByTagName(Node node, String tagName,String tagValue){
	
	if ( node.getNodeType()==1 &&
		 node.getNodeName().toUpperCase().equals(tagName.toUpperCase()) && 
		 node.hasChildNodes()
		)
		{			 
		for (int t = 0; t<node.getChildNodes().getLength(); t++){
			if (node.getChildNodes().item(t).getNodeValue().trim().equals(tagValue.trim())  ) 
				result = node.getChildNodes().item(t);
			else
				rekGetElementByTagName(node.getChildNodes().item(t),tagName,tagValue);
		}
	}
	else 
		for (int t = 0; t<node.getChildNodes().getLength(); t++){
			rekGetElementByTagName(node.getChildNodes().item(t),tagName,tagValue);
		}
}


public static  List<Map<String,String>> loadConnectionDetails(String filePath){ 
	Document doc = getXmlDocument(filePath);
	List<Map<String,String>> xmlExtrakt = new LinkedList<Map<String,String>>();
	Map<String,String> tagsAndContents = new HashMap<String,String>();
	extractQueryDefinition(doc, 0,tagsAndContents,xmlExtrakt,false,"DBCONNECTIONDEFINITION");
	if (tagsAndContents.size()>0) {
		 xmlExtrakt.add(new HashMap<String,String>(tagsAndContents));
		 tagsAndContents.clear();
	}//System.out.println(xmlExtrakt);
    return xmlExtrakt;
}


public static  LinkedList<Map<String,String>> loadFileLocators(String filePath){ 
	LinkedList<Map<String,String>> xmlExtrakt = new LinkedList<Map<String,String>>();
	Document doc= getXmlDocument(filePath);	
	//showMeTheDoc(doc,0);
	Map<String,String> tagsAndContents = new HashMap<String,String>();

	extractQueryDefinition(doc, 0,tagsAndContents,xmlExtrakt,false,"DEFINITIONFILELOCATORS" );
	if (tagsAndContents.size()>0) {
		 xmlExtrakt.add(new HashMap<String,String>(tagsAndContents));
		 tagsAndContents.clear();
	}
	//System.out.println(xmlExtrakt);
    return xmlExtrakt;
}

public static  LinkedList<Map<String,String>> loadIniSettings(String filePath){ 
	LinkedList<Map<String,String>> xmlExtrakt = new LinkedList<Map<String,String>>();
	Document doc= getXmlDocument(filePath);	
	Map<String,String> tagsAndContents = new HashMap<String,String>();

	Node lastDir = getElementByTagName(doc,"LASTDIR" );
	if (lastDir!=null &&lastDir.hasChildNodes()){
		String lastDirLocator = lastDir.getFirstChild().getNodeValue();
		if (lastDirLocator!=null && lastDirLocator.trim()!=null){
		tagsAndContents.put("LASTDIR",lastDirLocator);
		xmlExtrakt.add(tagsAndContents);
		}
	}
	
	Node warnLvlNode = getElementByTagName(doc,"WARNINGLEVEL" );
	if (warnLvlNode!=null && warnLvlNode.hasChildNodes()){
		String warnLvl = warnLvlNode.getFirstChild().getNodeValue();
		if (warnLvl!=null && warnLvl.trim()!=null){
		tagsAndContents.put("WARNINGLEVEL",warnLvl);
		xmlExtrakt.add(tagsAndContents);
		}
	}
	
	//System.out.println(xmlExtrakt);
    return xmlExtrakt;
}

public static void storeIniSettings( String iniFile, String iniSettingName,String iniSettingValue){
	Document doc= getXmlDocument((new File (iniFile)).getAbsolutePath() );	
	Node lastDir = getElementByTagName(doc,iniSettingName );
	if (lastDir==null ){//neues ini setting tag kreieren
		Element neuesElement = doc.createElement(iniSettingName);
		neuesElement.appendChild(doc.createTextNode(iniSettingValue));
		getElementByTagName(doc,"ROOT" ).appendChild(neuesElement);
	}
	else 
		if (lastDir.hasChildNodes()){//altes ini setting überschreiben
			lastDir.getFirstChild().setTextContent(iniSettingValue); 
		}
	if (lastDir!= null && !lastDir.hasChildNodes()){//ini setting im leeren tag schreiben 
		lastDir.appendChild(doc.createTextNode(iniSettingValue)); 
	}
	storeXmlDocToFile(iniFile, doc);	
}

public static void storeFileLocator (LinkedList<String> storedFileLocators, String iniFile ){
	Document doc= getXmlDocument(iniFile);
//	doc.getElementsByTagName(arg0)
	//System.out.println(storedFileLocators);
	Node fileLocatorNode = getElementByTagName (doc,"DEFINITIONFILELOCATORS");
	boolean tagUpdated=false;
	for (String fileLocator: storedFileLocators){//check if the file locator list contains file locator which are not stored in the ini setting 
	boolean fileLocatorIsStored=false; 
		for (int t = 0; t<fileLocatorNode.getChildNodes().getLength(); t++){
			 	if ( fileLocator.equals(fileLocatorNode.getChildNodes().item(t).getTextContent()) )
			 		fileLocatorIsStored=true;  
			 }
		if (!fileLocatorIsStored) {
			for (int tt = 0; tt<fileLocatorNode.getChildNodes().getLength(); tt++){
			 	if (fileLocatorNode.getChildNodes().item(tt).getNodeType()==1 ) 
			 		doc.renameNode( fileLocatorNode.getChildNodes().item(tt)
			 				,null,
			 				"FileLocator"+String.valueOf(Integer.valueOf(fileLocatorNode.getChildNodes().item(tt).getNodeName().substring(11))+1)  ); 
			 }
			Stack<Node> nodeStapel = new Stack<Node>(); 
			int childrenCount = fileLocatorNode.getChildNodes().getLength();
			for (int ttt = 0; ttt<childrenCount; ttt++){
			if (fileLocatorNode.getChildNodes().item( fileLocatorNode.getChildNodes().getLength()-1 ).getNodeType() == 1)//
				nodeStapel.push(fileLocatorNode.getChildNodes().item( fileLocatorNode.getChildNodes().getLength()-1 ));//Immer wird der Letzte genommen und auf dem Stack gesetzt, damit wird das letzte Kind auch das Letze in der neuen Reihe sein 
			fileLocatorNode.removeChild(fileLocatorNode.getChildNodes().item( fileLocatorNode.getChildNodes().getLength()-1 )); 
			//wird ein Kind removed und er hintelässt danach eine Index-Lücke, wird die Kinder-Indexierung neu vergeben und das alte Kind 1 wird Kind 0  
			}
			Element neuesElement = doc.createElement("FileLocator0");
			neuesElement.appendChild(doc.createTextNode(fileLocator)  );
//			fileLocatorNode.appendChild(neuesElement);
			nodeStapel.push( neuesElement );//das neueste Element wird der erste FileLocator-tag sein  
			tagUpdated=true;
			childrenCount=1;
			while (!nodeStapel.isEmpty()&&childrenCount<=10){
				fileLocatorNode.appendChild(nodeStapel.pop());
				childrenCount++;
			}				
			//showMeTheDoc(fileLocatorNode, 0);
		}
	}
if (tagUpdated){
storeXmlDocToFile(iniFile, doc); 	  
 }
}

public static void amendConnectionDetailsXML(String defFile, Map<String,String> connDet, String oldConnectionName ){
	showMeTheDoc(getXmlDocument(defFile), 1 );
	Document doc = getXmlDocument(defFile);
	
	if (oldConnectionName==null || oldConnectionName==""){
		oldConnectionName=connDet.get("CONNECTIONNAME");
	} 
	
	Node connName = getElementByTagName(doc,"CONNECTIONNAME",oldConnectionName);	
	Node connDetailToBeReplaced = connName.getParentNode().getParentNode();
	Element newConnection= doc.createElement("DBConnectionDefinition");
	newConnection.appendChild(doc.createElement("connectionName"));
//	Attr moft = doc.createAttribute("Mofturi");
//  moft.setValue("Da");
//	newConnection.setAttributeNode(moft);
		newConnection.getLastChild().appendChild(doc.createTextNode(connDet.get("CONNECTIONNAME")));
	newConnection.appendChild(doc.createElement("connectionType"));
		newConnection.getLastChild().appendChild(doc.createTextNode(connDet.get("CONNECTIONTYPE")));		
		newConnection.appendChild(doc.createElement("dbType"));
		newConnection.getLastChild().appendChild(doc.createTextNode(connDet.get("DBTYPE")));
		newConnection.appendChild(doc.createElement("serviceName"));
		newConnection.getLastChild().appendChild(doc.createTextNode(connDet.get("SERVICENAME")));
	newConnection.appendChild(doc.createElement("host"));
		newConnection.getLastChild().appendChild(doc.createTextNode(connDet.get("HOST")));
		newConnection.appendChild(doc.createElement("port"));
		newConnection.getLastChild().appendChild(doc.createTextNode(connDet.get("PORT")));		
	newConnection.appendChild(doc.createElement("userName"));
		newConnection.getLastChild().appendChild(doc.createTextNode(connDet.get("USERNAME")));
		
	connDetailToBeReplaced.getParentNode().replaceChild(newConnection,connDetailToBeReplaced);
	storeXmlDocToFile(defFile, doc);
}


public static void addConnectionDetailsToDefFile(String filePath, Map<String,String> connDet ){
	Document doc = getXmlDocument(filePath);
	Element newConnection= doc.createElement("DBConnectionDefinition");
	newConnection.appendChild(doc.createElement("connectionName"));
		newConnection.getLastChild().appendChild(doc.createTextNode(connDet.get("CONNECTIONNAME")));
	newConnection.appendChild(doc.createElement("connectionType"));
		newConnection.getLastChild().appendChild(doc.createTextNode(connDet.get("CONNECTIONTYPE")));		
	newConnection.appendChild(doc.createElement("dbType"));
		newConnection.getLastChild().appendChild(doc.createTextNode(connDet.get("DBTYPE")));
	newConnection.appendChild(doc.createElement("serviceName"));
		newConnection.getLastChild().appendChild(doc.createTextNode(connDet.get("SERVICENAME")));
	newConnection.appendChild(doc.createElement("host"));
		newConnection.getLastChild().appendChild(doc.createTextNode(connDet.get("HOST")));
	newConnection.appendChild(doc.createElement("userName"));
		newConnection.getLastChild().appendChild(doc.createTextNode(connDet.get("USERNAME")));
	
	getElementByTagName(doc,"DBConnectionDefinition").getParentNode().appendChild(newConnection); 	
	//getElementByTagName(doc,"ROOT").appendChild(newConnection);	
	storeXmlDocToFile(filePath, doc);		
	showMeTheDoc(newConnection, 1);
}

private static void storeXmlDocToFile(String iniFile, Document doc){
	
	try {	 
		File f = new File(iniFile);
		  TransformerFactory tFactory = TransformerFactory.newInstance();
		  Transformer transformer = tFactory.newTransformer();
	//	  for (String prop :transformer.getOutputProperties().stringPropertyNames()) {//Zeigt alle output-Einstellungen und ihre default-werte 
	//		  System.out.println(prop+":  " +transformer.getOutputProperty(prop) );  
	//	  }
		  transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		  DOMSource source = new DOMSource(doc);
		  StreamResult result = new StreamResult(f);
		  transformer.transform(source, result);
		} catch (TransformerConfigurationException tce) {
//			ErrorMessage.showException(tce,"Fehler beim Parsen der XML-Daten"); 
			ErrorMessage.showException(tce,"Error thrown during parsing of the XML data");
			logger.error("Error thrown during parsing of the XML data"+"\n"+ErrorMessage.showStackTrace(tce.getStackTrace()) );
			
		} catch (TransformerException te) {
//			ErrorMessage.showException(tce,"Fehler beim Parsen der XML-Daten"); 
			ErrorMessage.showException(te,"Error thrown during parsing of the XML data");
			logger.error("Error thrown during parsing of the XML data"+"\n"+ErrorMessage.showStackTrace(te.getStackTrace()) );
			} catch (Exception e) {
//			ErrorMessage.showException(e,"Fehler beim Sichern von Daten in der XML-Datei "+iniFile);
				ErrorMessage.showException(e,"Error thrown during laoding the XML file "+iniFile);
				logger.error("Error thrown during laoding the XML file "+iniFile+"\n"+ErrorMessage.showStackTrace(e.getStackTrace()) );
	}
}

public static  List<AnfragePlan>  generateAnfragePlan(String filePath)  {
	if (defFiles==null)
		defFiles= new LinkedList<String>();
	try {
		definitionFilePath= (new File(filePath).getCanonicalPath()).replaceAll(new File(filePath).getName(),"");//nur das Verzeichnis
		defFiles.add(new File(filePath).getCanonicalPath());
	} catch (IOException e) {
		ErrorMessage.showException(e,"I/O Error thrown during laoding the report definition file "+filePath);
		logger.error("I/O Error thrown during laoding the report definition file "+filePath+"\n"+ErrorMessage.showStackTrace(e.getStackTrace()) );
	}
	
	Document doc = getXmlDocument(filePath);
	List<Map<String,String>> xmlExtrakt = new LinkedList<Map<String,String>>();
	Map<String,String> tagsAndContents = new HashMap<String,String>();
	extractQueryDefinition(doc, 0,tagsAndContents,xmlExtrakt,false,"SQLQUERYDEFINITION" );
	if (tagsAndContents.size()>0) {
		xmlExtrakt.add(new HashMap<String,String>(tagsAndContents));
		tagsAndContents.clear();
	}
	//System.out.println(xmlExtrakt);
	String validationMessages = validateXMLAnfragePlan(xmlExtrakt);
	if (!validationMessages.equals("")){
		ErrorMessage.showException(validationMessages);
		logger.error("Fatal validation problems in"+filePath+"\n"+validationMessages );
		return null;
	}
	
	List<AnfragePlan> result = convertXMLExtraktToAnfragePlan(xmlExtrakt);
	defFiles.removeLast();
	return result;
}

protected static Document getXmlDocument(String filePath) {
		//soll auch ohne filePath auskommen und ein leeres doc erzeugen
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();	    
	    DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			ErrorMessage.showException(e,"Error thrown during the initialisation of the XML parser");
			logger.error("Error thrown during the initialisation of the XML parser"+"\n"+ErrorMessage.showStackTrace(e.getStackTrace()) );
			//ErrorMessage.showException(e,"Fehler beim Initialisieren des XML-Parsers");
		}
		Document doc=null;
		
		try {
			if (filePath!=null &&  new File(filePath).length()>0) {
				doc = builder.parse(new File(filePath));
			}else{ 
				doc = builder.newDocument();
				Element theRoot = doc.createElement("ROOT");
				Element fileLocDef = doc.createElement("DEFINITIONFILELOCATORS");
				theRoot.appendChild(fileLocDef);
				doc.appendChild(theRoot);
			}	
		} catch (SAXException e) {
			ErrorMessage.showException(e,"Error thrown during parsing of the XML data in"+filePath);
			logger.error("Error thrown during parsing of the XML data in"+filePath+"\n"+ErrorMessage.showStackTrace(e.getStackTrace()) );			
//			ErrorMessage.showException(e,"Fehler beim Parsen der XML Datei");
		} catch (IOException e) {
			ErrorMessage.showException(e,"I/O thrown during loading the data from "+filePath);
			logger.error("I/O thrown during loading the data from "+filePath+"\n"+ErrorMessage.showStackTrace(e.getStackTrace()) );			
		}
		return doc;
	}
	
  private static void extractQueryDefinition(Node node, int level, Map<String,String> tagsAndContents, List<Map<String,String>> xmlExtrakt, boolean definitionBegins, String mainXmlTag) {
      //System.out.println(level+"\n Node Text Content: "+node.getTextContent());
		 if (node.hasChildNodes()){
		  NodeList children = node.getChildNodes();
		 //System.out.println(DisplayData.multiplyChars(' ',(level-1)*10)+node.getNodeName()+" hat "+children.getLength()+" Kinder ");
		 if (node.getNodeName().toUpperCase().equals(mainXmlTag) && children.getLength()>1){
			 definitionBegins=true; 
			 if (tagsAndContents.size()>0) {
				 xmlExtrakt.add(new HashMap<String,String>(tagsAndContents));
				 tagsAndContents.clear();
			 }
			 if (node.hasAttributes() ){
				 for (int t=0;t<node.getAttributes().getLength();t++ ){
					 tagsAndContents.put(node.getAttributes().item(t).getNodeName().toUpperCase(), node.getAttributes().item(t).getNodeValue().toUpperCase());
				//	 System.out.println(node.getAttributes().item(t).getNodeName()+" "+node.getAttributes().item(t).getNodeValue()); 
				 }
			 }
		 }	 
		 if (children.getLength()==1 && definitionBegins && !node.getNodeName().toUpperCase().equals(mainXmlTag)) {
			 //System.out.println(DisplayData.multiplyChars(' ',(level-1)*10)+"Content  "+node.getTextContent()+"\n");
			 tagsAndContents.put(node.getNodeName().toUpperCase(), node.getTextContent().trim());
		 }
		 for (int t = 0;t<children.getLength();t++){
			 extractQueryDefinition(children.item(t), level+1,tagsAndContents,xmlExtrakt,definitionBegins,mainXmlTag);
		 }
	   }
	 }	  
	 
 static String validateXMLAnfragePlan(List<Map<String,String>> xmlExtrakt){
	int anfrageCounter=0;
	StringBuilder validationComments= new StringBuilder("");
	LinkedList<String> queryLabels = new LinkedList<String>();
	for (Map<String,String> queryDef: xmlExtrakt){
		anfrageCounter++;	
		String queryLabel="";
		if (queryDef.get("SQLQUERYLABEL")==null || queryDef.get("SQLQUERYLABEL").trim().equals("")){ 
//			validationComments.append("Die Anfragedefinition "+ queryLabel+" hat keinen Namen.\nGeben Sie der Anfragedefinition einen Namen und tragen Sie diesen bitte in den XML-tag SQLQueryLabel wie folgt ein:" +
//					"\n <SQLQueryLabel>passender Name</SQLQueryLabel>.\n\n");
			validationComments.append("The report entity number "+anfrageCounter+" has no name.\nPlease assign it a name in the XML tag SQLQueryLabel:" +
					"\n<SQLQueryLabel>An unique Name</SQLQueryLabel>.\n\n");
		}
		else 
			queryLabel =queryDef.get("SQLQUERYLABEL");

		if (queryDef.get("SQLQUERY")==null || queryDef.get("SQLQUERY").trim().equals(""))
//			validationComments.append("Für die Anfragedefinition "+queryLabel+" wurde keine SQL Anfrage angegeben.\nTragen Sie eine SQL Anfrage in den XML-tag SQLQuery wie folgt ein:\n" +
//					"<SQLQuery>select something from somewhere where...</SQLQuery>\n\n");
			validationComments.append("The report entity "+queryLabel+" has no SQL query.\nPlease enter the SQL query in den XML-tag SQLQuery:\n" +
					"<SQLQuery>select something from somewhere where an expression is true</SQLQuery>\n\n");

		if ( (queryDef.get("SUPERSQLQUERY")==null || queryDef.get("SUPERSQLQUERY").trim().equals("")) && anfrageCounter>1 )
//			validationComments.append("Die Anfragedefinition "+queryLabel+" wurde keiner anderen Anfragedefinition untergeordnet.\nTragen Sie den Namen einer übergeordneten Anfragedefinition in den XML-tag SuperQuery wie folgt ein:\n" +
//					"<SuperSQLQuery>Another Query Label</SuperSQLQuery>.\n\n");
			validationComments.append("The report entity "+queryLabel+" was not subordinated to any another report entity.\nPlease enter the report entity name of an preceding entity in den XML tag SuperQuery:\n" +
					"<SuperSQLQuery>Another Query Label</SuperSQLQuery>.\n\n");

		if ( ( anfrageCounter==1 && queryDef.get("SUPERSQLQUERY")!=null && !queryDef.get("SUPERSQLQUERY").trim().equals(""))  )
//			validationComments.append("Die erste Anfragedefinition darf keiner anderen Anfragedefinition untergeordnet werden.\nLöschen Sie den XML-tag SuperSQLQuery aus der Anfragedefinition "+queryLabel+"\n\n");
			validationComments.append("The first report entity cannot be subordinated under other report entity.\nDelete the XML tag SuperSQLQuery from the report entity "+queryLabel+"\n\n");
	
		if (  anfrageCounter>1 
			   && queryDef.get("SUPERSQLQUERY")!=null 
			   && !queryDef.get("SUPERSQLQUERY").trim().equals("") 
			   && !queryLabels.contains(queryDef.get("SUPERSQLQUERY"))  
		   )
//			validationComments.append("Die übergeordnete Anfragedefinition "+queryDef.get("SUPERSQLQUERY") +" wurde nicht gefunden.\n" +
//					("Die Anfragedefinition "+queryLabel+" kann folgenden Anfragedefintionen untergeordnet werden:\n"+queryLabels +"\n\n").replace("[", "").replace("]", "")  );
			validationComments.append("The report entity "+queryLabel+" specifies a super query which is not one of the preceding report entities: "+queryDef.get("SUPERSQLQUERY") +".\n" +
			("The report entity "+queryLabel+" can be subordinated only to one of the following report entities:\n"+queryLabels +"\n\n").replace("[", "").replace("]", "")  );

			
//		if (queryDef.get("SQLQUERYLABEL")!=null && !queryDef.get("SQLQUERYLABEL").trim().equals("") && queryLabels.contains(queryDef.get("SQLQUERYLABEL"))){
//			validationComments.append("Der Name einer Anfragedefinition darf nur einmal innerhalb einer Definitionsdatei vergeben werden." +	
//					"\nÜberprüfen sie den tag <QueryLabel>"+queryDef.get("SQLQUERYLABEL")+"</QueryLabel> in der "+ (queryLabels.indexOf(queryDef.get("SQLQUERYLABEL"))+1)	+". und in der "+anfrageCounter+". Anfragedefinition\n\n"  ); 
//		}
		queryLabels.add(queryLabel);
	}	 
	return validationComments.toString();
	 }
	 static List<AnfragePlan> convertXMLExtraktToAnfragePlan (List<Map<String,String>> xmlExtrakt){
		List<AnfragePlan> result = new LinkedList<AnfragePlan>();		
		for (Map<String,String> queryDef: xmlExtrakt){		
			String query = queryDef.get("SQLQUERY").replace(";", ""); 
			String queryLabel = queryDef.get("SQLQUERYLABEL").trim(); 
			String suppressDisplayIfNoData = queryDef.get("SUPPRESSDISPLAYIFNODATA"); 
			AnfragePlan superQuery=null;
			int maxInheritanceDepth=10000;
			if (queryDef.get("MAXINHERITANCEDEPTH")!=null)
				maxInheritanceDepth = new Integer(queryDef.get("MAXINHERITANCEDEPTH")); 
			if (result.size()>0 && queryDef.get("SUPERSQLQUERY")!=null){
				for (AnfragePlan sQuery :result){
					if (sQuery.getQueryName().equals(queryDef.get("SUPERSQLQUERY").trim())) {
						superQuery=sQuery;
						break;
					}
				}
			}
		//AnfragePlan(String query, String queryName, AnfragePlan superQuery, int maxInheritanceDepth)	
			result.add(new AnfragePlan(query,queryLabel,superQuery,maxInheritanceDepth));
			if (queryDef.get("TRANSMITTEDCONDITIONCOLUMNS")!=null){
				String[] transmittedCondCols = queryDef.get("TRANSMITTEDCONDITIONCOLUMNS").split(",");	 
				for (int x=0; x<transmittedCondCols.length; x++)
					result.get(result.size()-1).addTransmittedConditionAttribute(transmittedCondCols[x].toUpperCase()) ;
			}
			if (queryDef.get("RESULTCOLUMNS")!=null){
				String[] resultCols = queryDef.get("RESULTCOLUMNS").split(",");	 
				for (int x=0; x<resultCols.length; x++)
					result.get(result.size()-1).addResultColumn(resultCols[x].toUpperCase());
			}
			if (suppressDisplayIfNoData!=null && (suppressDisplayIfNoData.equals("TRUE")||suppressDisplayIfNoData.equals("YES")||suppressDisplayIfNoData.equals("1")) )
				result.get(result.size()-1).setSuppressDisplayIfNoData(true);
			
			if (queryDef.get("SUBQUERYLOCATOR")!=null){
				AnfragePlan parentQuery= result.get(result.size()-1);
				StringBuffer subFileErorrs = new StringBuffer(""); 
				boolean circularity=false, fileExists=true;			
				String [] theFiles = queryDef.get("SUBQUERYLOCATOR").split(",");
				for (String theSubQueryFile:theFiles){
					if (!theSubQueryFile.trim().equals("")){
						File subqueryLocator = new File (theSubQueryFile.trim());
						if (!subqueryLocator.isAbsolute()){
							try {
								theSubQueryFile=subqueryLocator.getCanonicalPath();
							} catch (IOException e) {
								ErrorMessage.showException(e,"I/O error thrown during loading the data from"+theSubQueryFile);
								logger.error("I/O error thrown during loading the data from"+theSubQueryFile+"\n"+ErrorMessage.showStackTrace(e.getStackTrace()) );
							}
						}
						if  (!subqueryLocator.exists() || !subqueryLocator.isFile() )
						{
							subFileErorrs.append("The SQLQueryDefinition tag "+queryDef.get("SQLQUERYLABEL")+" is pointing to a EsspresoView Report definition file which could not be found: "+theSubQueryFile+"\nThis sub-report will be ignored");
							//"In der Anfragedefinition "+queryLabel+" verweist der tag SubQueryLocator auf die nicht auffindbare Datei "+subqueryLocator.getAbsolutePath()+ "\n\n");
							logger.error("The SQLQueryDefinitionTag"+queryDef.get("SQLQUERYLABEL")+" is pointing to a EsspresoView Report definition file which could not be found "+theSubQueryFile+"\n");
							fileExists=false;
						}	
						for (String parentFile: defFiles){
							if (theSubQueryFile.equals(parentFile) ){
								subFileErorrs.append(
										"\nThe report definition contains a circular reference between report definition file "+defFiles.getLast()
										+ " and the report definition file "+theSubQueryFile+"!!\n\n" +
										"The SubQueryLocator "+queryDef.get("SUBQUERYLOCATOR")+" in SQLQueryDefinition "+queryDef.get("SQLQUERYLABEL")+
										" and in the report definition file "+defFiles.getLast()+" will be ignored");
								//									"Achtung! \n\nDie Anfragedefinition is Zirkulär!\nDie Definitionsdatei "+defFiles.getLast()
								//									+ " definiert als Unteranfrage die übergeordnete AnfrageDefinitionsdatei "+queryDef.get("SUBQUERYLOCATOR")+"\n" +
								//									"Der tag <SubQueryLocator>"+queryDef.get("SUBQUERYLOCATOR")+"</SubQueryLocator> in der Definitiosdatei "+defFiles.getLast()+" wird ignoriert");
								logger.error("\nThe report definition contains a circular reference between report definition file "+defFiles.getLast()
										+ " and the report definition file "+theSubQueryFile+"!!\n\n" +
										"The SubQueryLocator "+queryDef.get("SUBQUERYLOCATOR")+" in SQLQueryDefinition "+queryDef.get("SQLQUERYLABEL")+
										" and in the report definition file "+defFiles.getLast()+" will be ignored");
								circularity=true;
							}
						}
						if (fileExists && !circularity ){
							List<AnfragePlan> subQueryFile = generateAnfragePlan(theSubQueryFile);
							if (subQueryFile.isEmpty()){ 
								subFileErorrs.append("Could not integrate the embedded report definition file  "+queryDef.get("SUBQUERYLOCATOR")+"!");
							}
							else{ 	
								subQueryFile.get(0).superSQLQuery=parentQuery;
								parentQuery.subQueries.add(subQueryFile.get(0));
								result.addAll(subQueryFile);
							}
						}
					}
				}
				if (subFileErorrs.length()>0){ 
					ErrorMessage.showException(subFileErorrs.toString());
					logger.error(subFileErorrs.toString());
					subFileErorrs.delete(0, subFileErorrs.length()-1);
					}
				}			
		}		
		return result; 
	 }
}
