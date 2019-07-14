import java.util.*;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class DisplayData {
	private static Logger logger = LogManager.getLogger(DisplayData.class.getName());
	static void showTree(DataNode node, int level){
		String maxRowsLength= "";
		if (node.getMaxColumnLength()!=null)
			maxRowsLength=node.getMaxColumnLength().toString();
		System.out.println(multiplyChars(' ',(level-1)*10)+
				node+" "+maxRowsLength);
		if (node.getChildren()!=null && node.getChildren().size()>0){
			for (DataNode n : node.getChildren()){
				showTree(n,level+1); 
			}
		}	
	}	
	
	static void  generateBlockDisplay(AnfragePlan anfrage){ 
		generateBlockDisplayRek(anfrage,1);	
 }
	static void  generateBlockDisplayRek(AnfragePlan anfrage,int level){
		String ResultMatrix[][] = anfrage.getResultRowsMatrix();
		System.out.println("\n"+multiplyChars(' ',(level-1)*10)+anfrage.getQueryName());
		int colNr=0, rowNr=0;
				
		for (rowNr=0;rowNr<ResultMatrix[0].length; rowNr++){ 
			System.out.print(multiplyChars(' ',(level-1)*10));
			for (colNr=0;colNr<ResultMatrix.length;colNr++ ){
				System.out.print(ResultMatrix[colNr][rowNr]+"\t");
			}
			System.out.println();
		}
		int t=0;
		if (!anfrage.subQueries.isEmpty()){
			while (anfrage.subQueries.size()>t){
				generateBlockDisplayRek(anfrage.subQueries.get(t), level+1); 
			t++; 	
	    	}
		}	
	}
	
	static DefaultMutableTreeNode generateStructDisplay(AnfragePlan anfrage,String titleText,String viewOption){ 
		DataNode anfang = new DataNode(titleText);
		anfang.viewOption=viewOption;
		anfang.isLabel=true;
		generateStructDisplayRek(anfrage,anfang,1);
		anfang.determinetMaxColumnLength(anfang);
		anfang.propagateViewOption();
		//showTree(anfang,0);
		return anfang.composeTree();
		
 }
	static void  generateStructDisplayRek(AnfragePlan anfrage,DataNode node,int level){
		String ResultMatrix[][] = anfrage.getResultRowsMatrix();
		String mhd ="";
		if (anfrage.getMaxInheritanceDepth()<1000)
				mhd = " (max depth "+Integer.toString(anfrage.getMaxInheritanceDepth())+")";
		String notApplicableColumns = anfrage.getInheritedConditionsAsString(false);
		if (notApplicableColumns.contains("*")&& mhd.length()>0)
			mhd=mhd+"*"; 
		DataNode newChild = node.insertDeep(
					new DataNode("Query Definition@°@Applicable Criteria@°@NOT Applicable Criteria@°@Transmitted Criteria@°@Number of Rows",
					anfrage.getQueryName()+mhd+"@°@"+anfrage.getInheritedConditionsAsString(true)+"@°@"+notApplicableColumns+"@°@"+anfrage.getNewTransmittedConditionAttributesAsString()+"@°@"+(ResultMatrix[0].length-1) 
					,node));
//		System.out.println("\n"+multiplyChars(' ',(level-1)*10)+"Query Definition, Number of Rows");
//		System.out.println("\n"+multiplyChars(' ',(level-1)*10)+anfrage.getQueryName()+","+(ResultMatrix[0].length-1));
		int t=0;
		if (!anfrage.subQueries.isEmpty()){
			while (anfrage.subQueries.size()>t){
				generateStructDisplayRek(anfrage.subQueries.get(t),newChild,level+1); 
			t++; 	
	    	}
		}	
	}	
	
	//static void  generateStructDisplay(AnfragePlan anfrage, int level, DataNode result ){
	
	static DefaultMutableTreeNode generateJointDisplay (AnfragePlan anfrage,String titelText,String viewOption){
		DataNode anfang = new DataNode(titelText);
		anfang.viewOption=viewOption;
		anfang.isLabel=true;
		generateJointDisplayRek(anfrage, null,1, anfang,viewOption);
		//showTree(anfang,1);
		anfang.determinetMaxColumnLength(anfang);
		//showTree(anfang,1);
		anfang.propagateViewOption();
		return anfang.composeTree();
	}
	//hier in dieser Methode findet der eigentümliche DataJoin statt
	static void  generateJointDisplayRek(AnfragePlan anfrage, HashMap<String,TransmittedCondition> inheritedJointCond, int level, DataNode result, String viewOption ){
		if (anfrage.isSuppressDisplayIfNoData()&& anfrage.isEmptyResultset()) return;//Wenn die Option isSuppressDisplayIfNoData verwendet wird, dann werden leere Ergebnisssätze nicht dargestellt 
		String resultMatrix[][] = anfrage.getResultRowsMatrix();
		StringBuilder resultLine = new StringBuilder();
		StringBuilder resultLineColumnNames = new StringBuilder();
		List <String> resultColumns = new LinkedList<String>();
		resultColumns.addAll(anfrage.getResultColumns());
		//System.out.println(multiplyChars(' ',(level-1)*10)+anfrage.getQueryName());
		DataNode newChild = result.insertDeep(new DataNode(anfrage.getQueryName()));
		newChild.isLabel = true;
		HashMap<String,TransmittedCondition> forwardJointCond = new HashMap<String,TransmittedCondition>();
			for (TransmittedCondition condi: anfrage.transmittedConditions){//Erzeugt Liste von zu vererbenden Joint Bedingungen für eine ResultMatrix 
				TransmittedCondition forwardCondi = new TransmittedCondition (condi.getIdentifier(),null,condi.getDepth(),condi.inheritanceIsNotApplicable()) ;		
				if (
					condi.inheritanceIsNotApplicable() //es gibt verebte Bedingungen, die nicht auf sich selbst angewendet werden können..
					&& inheritedJointCond!=null 
					&& inheritedJointCond.containsKey(condi.getIdentifier())  
					)
					{
						forwardCondi.setValue (inheritedJointCond.get(condi.getIdentifier()).getValue()  );//...In diesem Fall wird der nicht anwedbare Wert weitergegeben
					}
				forwardJointCond.put(forwardCondi.getIdentifier(),forwardCondi);	
			}
			HashMap <Integer,String> columnNamesUpperCase = new HashMap <Integer,String>();//Die Join-Operation ist zum Teil sehr rechenaufwendig. Um die upperCase operationen zu minimieren, werden die column name einmal zu upperCase umgewandelt    	
		boolean included =true;
		if (resultColumns.size() != 0 && resultColumns.get(0).trim().toUpperCase().startsWith("NOT:")){
			included=false;//Wenn der erste Eintrag im Resultcolumn mit not: Anfängt, dann werden alle angebenen Felder aus der Anzeige ausgeschlossen und die übrigen Felder ausgewählt 
			resultColumns.set(0, resultColumns.get(0).toUpperCase().replace("NOT:", "").trim());
		}			
		// Hier unten wird erkannt, ob die Spaltennamen aus der ermittelten resultColumns-liste mit einem Spaltennamen aus der SQL übereinstimmt. 
		// Die Spalten des DataNodes werden dann entsprechend "markiert", da die Indexnummer des boolean arrays mit der Indexnummer im columnArray übereinstimmt 
		boolean[] isResultColumn = new boolean[resultMatrix.length];  
		if (viewOption.equals("detailed")){
			for (int t=0; t<resultMatrix.length; t++){
				columnNamesUpperCase.put(t, resultMatrix[t][0].toUpperCase());
				resultLineColumnNames.append(resultMatrix[t][0]+"@°@"); //Zeigt den Namen der columns
				if (resultColumns.size() == 0 || DataJumper.containsCaseInsensitive(resultColumns,resultMatrix[t][0])==included )
					isResultColumn[t]=true;
//				else isResultColumn[t]=false;   
			}
		}
		else {
			Set<String> inheritedCondAttr = anfrage.getInheritedConditions();
			Set<String> forwardedCondAttr = anfrage.getNewTransmittedConditionAttributes();
			for (int t=0; t<resultMatrix.length; t++){
				resultLineColumnNames.append(resultMatrix[t][0]+"@°@"); //Zeigt den Namen der columns
				columnNamesUpperCase.put(t, resultMatrix[t][0].toUpperCase());
				if (inheritedCondAttr != null && DataJumper.containsCaseInsensitive(inheritedCondAttr,resultMatrix[t][0]))
				{
					resultLineColumnNames.insert(resultLineColumnNames.lastIndexOf(resultMatrix[t][0]), "Inherits "); 
					isResultColumn[t] = true;
				}						
				if (forwardedCondAttr !=null && DataJumper.containsCaseInsensitive(forwardedCondAttr,resultMatrix[t][0])){
					resultLineColumnNames.insert(resultLineColumnNames.lastIndexOf(resultMatrix[t][0]), "Forwards "); 
					isResultColumn[t] = true; 
				}						
			}
		}
		if (!Arrays.toString(isResultColumn).contains("true"))//wenn aber sämtliche Spaltennamen aus dem resultColumns-liste mit keinem Spaltennamen aus der SQL übereinstimmen, werden alle SQL Spaltennamen angezeigt
			for (int t=0; t<isResultColumn.length;t++)
				isResultColumn[t]=true;
		if (anfrage.getInheritedConditions() != null)
		logger.debug("Try sub-ordinate rows of "+anfrage.getQueryName()+" to "+anfrage.getSuperSQLQuery().getQueryName()+"\n"+resultLineColumnNames.toString().replace("@°@", ","));
		//												HIER BEGINNT DER RECHENINTENSIVE BEREICH!!!!! 		
		int colNr=0, rowNr=1;     
		for (rowNr=1;rowNr < resultMatrix[0].length; rowNr++){//Anfang Schleife durch rows
		boolean rowMatchesInheritedConditions=true;	
 			for (colNr=0;colNr<resultMatrix.length;colNr++ ){//Anfang Schleife durch die einzelnen columns 	
				if ( inheritedJointCond!=null  
					&&  inheritedJointCond.containsKey(columnNamesUpperCase.get(colNr)) //oder wenn der result column name mit einem der condition column names übereinstimmen,
					){//Joint-Bedingungen aus dem aufrufenden Record werden angewandt  
					TransmittedCondition inhertedCondi = inheritedJointCond.get(columnNamesUpperCase.get(colNr)	); 
					if(
					  inhertedCondi.getValue()==null || //wenn die inherited condition aus der oberen Datanode null ist, dann scheitert der Gleichheitsvergleich 
					  (
					  anfrage.getMaxInheritanceDepth()>=inhertedCondi.getDepth()		 	  //und Vererbungstiefe nicht zu tief ist 	
					  //&& !resultMatrix[colNr][rowNr].equals(inhertedCondi.getValue()))
					  && !Pattern.matches(resultMatrix[colNr][rowNr],inhertedCondi.getValue()) )//und die Werte nicht gleich sind 		  
					  //Wildkey-funktion mit * ist unterstützt     
					  ){
						rowMatchesInheritedConditions=false;
						break;//Für einen Ausschluss der Zeile reicht, dass eine Bedingung nicht erfüllt wird. Bedingungen werden also und-verknüpft.
						}
				}
				//if (!anfrage.hasSuperSQLQuery()){ 
				for (TransmittedCondition condi:forwardJointCond.values() ){
					if(condi.getIdentifier().equals(columnNamesUpperCase.get(colNr)) )
						//Wenn alle Bedingungen erfüllt: Werte aus dieser Spalte werden in die Liste der zu vererbenden Joint Bedingungen aufgenommen 
						condi.setValue(resultMatrix[colNr][rowNr]);					
				//}
				}
				resultLine.append(resultMatrix[colNr][rowNr]+"@°@");//Die Werte einer Ergebniszeile werden zusammengefügt
			}//Ende Schleife durch die einzelnen columns
			//rows, die vererbte Bedingungen nicht erfüllen, werden nicht angezeigt und von der Weitergabe ausgeschlossen
			if (rowMatchesInheritedConditions){
				//System.out.println(multiplyChars(' ',(level-1)*10)+resultLineColumnNames.toString().substring(0, resultLineColumnNames.length()-1)); 
				//System.out.println(multiplyChars(' ',(level-1)*10)+resultLine.toString().substring(0, resultLine.length()-1));
				DataNode newestChild =new DataNode (resultLineColumnNames.toString().substring(0, resultLineColumnNames.length()-3),
													resultLine.toString().substring(0, resultLine.length()-3),newChild);
				logger.debug("The Line "+newestChild.getValues().replace("@°@", ",")+" MATCHED these conditions: "+ inheritedJointCond);
				newestChild.setIsResultColumn(isResultColumn);
				newChild.insertOnLevel(newestChild);
				if (!anfrage.subQueries.isEmpty()){
					int t=0;
					while (anfrage.subQueries.size()>t){
						generateJointDisplayRek(anfrage.subQueries.get(t),forwardJointCond,level+1,newestChild,viewOption); 
						t++; 	
					}
				}	
			}			
			resultLine.delete(0, resultLine.length()); 
		}//Ende Schleife durch rows
//												HIER ENDET DER RECHENINTENSIVE BEREICH!!!!!
//		if
//			logger.info("DENIED!! "+resultLine.toString().replace("@°@", ","));
	if (anfrage.isSuppressDisplayIfNoData()&& newChild.getChildren().size()== 0){  
			result.getChildren().remove(newChild);//Label-Stummel werden beseitigt, wenn die Option isSuppressDisplayIfNoData verwendet wird
		}	
	if (!anfrage.isSuppressDisplayIfNoData()&& newChild.getChildren().size()== 0){
		for (colNr=0;colNr<resultMatrix.length;colNr++ ){
			resultLine.append("null"+"@°@");
		}
		DataNode newestChild =new DataNode (resultLineColumnNames.toString().substring(0, resultLineColumnNames.length()-3),
										    resultLine.toString().substring(0, resultLine.length()-3),newChild);
		newestChild.setIsResultColumn(isResultColumn);
		newChild.insertOnLevel(newestChild);
		resultLine.delete(0, resultLine.length()); 
	}
 }

static String generateTableDisplay (DataNode node, String separator, boolean includeSubTrees, int level){
	StringBuilder result=new StringBuilder("");
	String firstDelimiter="";
	firstDelimiter=multiplyChars(separator.charAt(0),level);
	//Table Title
	String title=node.values;
	result.append(firstDelimiter+title+"\n");
	//Column Names
	StringBuilder columnNames=new StringBuilder(); 
	String []columns = node.getFirstChild().getColumnArray();
	boolean []isResultColumn = node.getFirstChild().getIsResultColumn();
	for (int t=0;t<columns.length;t++){
		if (isResultColumn[t])
			columnNames.append(columns[t].replace(separator, ".")+separator);
	}
	columnNames.deleteCharAt(columnNames.length()-1);
	result.append(firstDelimiter+columnNames+"\n");
	//Values
	for(int n=0; n< node.getChildren().size();n++ ){
		DataNode aNode = node.getChildren().get(n);
		String []values= aNode.getValueArray();
		result.append(firstDelimiter);
		for (int t=0;t<values.length;t++){
			if (isResultColumn[t])
				result.append(values[t].replace(separator, ".")+separator);
		}
		result.deleteCharAt(result.length()-1).append("\n");
		if (includeSubTrees && !aNode.getChildren().isEmpty())
		{
			for (DataNode subNode: aNode.getChildren()){
			result.append(generateTableDisplay(subNode,separator,includeSubTrees,level+1));
			if (node.getChildren().size()> n+1 ) {//Der ColumnNames für die nächsten record werden dargestellt, aber nicht, wenn es der letzte Node ist. Denn er hat ja keinen Nächsten  
				result.append(firstDelimiter+title+"\n");
				result.append(firstDelimiter+columnNames+"\n");
				}
			}
		}	
	}
	return result.toString();
}

static String generateSelectionDisplay (TreePath [] tp, String separator, boolean includeSubTrees){

	if (tp==null ||tp.length==0) return null;
	if (tp.length==1 && ((DefaultMutableTreeNode)(tp[0].getLastPathComponent())).getUserObject() instanceof String)
		return ((DefaultMutableTreeNode)(tp[0].getLastPathComponent())).getUserObject().toString();
	StringBuilder result=new StringBuilder("");
	String firstDelimiter="";
	String title=null;
	boolean []isResultColumn = null;
	StringBuilder columnNames=new StringBuilder();
	LinkedList<DataNode> selectedNodes = new LinkedList<DataNode>();
int levelOffset=9999, level=0;
	   for (int t=0;t<tp.length;t++){
		   DefaultMutableTreeNode selNode= (DefaultMutableTreeNode)(tp[t].getLastPathComponent()); 
		   if  (selNode.getUserObject() instanceof DataNode){
			   selectedNodes.add( (DataNode)(selNode.getUserObject()) );
			   if (levelOffset >((DefaultMutableTreeNode)(tp[t].getLastPathComponent())).getLevel() )
				   levelOffset=((DefaultMutableTreeNode)(tp[t].getLastPathComponent())).getLevel(); //Der der Wurzel nächste Level wird gesucht, dieser wird dann allen levels abegezogen damit Selektionen mit hohen Level-Zahl nicht zuviele Separatoren links von ihnen bekommen und dadurch zu weit rechts angezeigt werden     
		   }
	   }
	for (int i=0;i<selectedNodes.size();i++){
		level = (((DefaultMutableTreeNode)(tp[i].getLastPathComponent())).getLevel()-levelOffset)/2; //Geteilt durch zwei, weil jede Ebene aus zwei levels besteht Label+Daten
		firstDelimiter=multiplyChars(separator.charAt(0),level+1);
		
		if (!selectedNodes.get(i).isLabel && selectedNodes.get(i).getParent()!=null){
			if (title==null || title.equals(selectedNodes.get(i).getParent().values) == false){
				//Table Title
				title=selectedNodes.get(i).getParent().values;
				result.append(firstDelimiter+title+"\n");
				//Column Names
				isResultColumn = selectedNodes.get(i).getIsResultColumn();
				String[] columns = selectedNodes.get(i).getColumnArray();
				for (int t=0;t<columns.length;t++){
					if (isResultColumn[t])
						columnNames.append(columns[t].replace(separator, ".")+separator);
					}
				columnNames.deleteCharAt(columnNames.length()-1);
				result.append(firstDelimiter+columnNames+"\n");
			}
			//Values
			result.append(firstDelimiter);
			String[] values = selectedNodes.get(i).getValueArray();
			for (int t=0;t<values.length;t++){
				if (isResultColumn[t])
					result.append(values[t].replace(separator, ".")+separator);
			}
			result.deleteCharAt(result.length()-1).append("\n");
			if (includeSubTrees && !selectedNodes.get(i).getChildren().isEmpty())
				{
					for (DataNode subNode : selectedNodes.get(i).getChildren()){
					result.append(generateTableDisplay(subNode,separator,includeSubTrees,level+2));
					}
				}	
			}
	}	
		return result.toString();
	}


	static String multiplyChars(char ch, int multiplier){
		StringBuilder result = new StringBuilder(ch);  	
		for (int t=1;t<multiplier; t++){
			result.append(ch);
		}	
return result.toString();	
}
	
}
