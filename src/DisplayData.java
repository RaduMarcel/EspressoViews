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
		anfang.determineMaxColumnLength(anfang);
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
					anfrage.getQueryName()+mhd+"@°@"+anfrage.getInheritedConditionsAsString(true)+"@°@"+notApplicableColumns+"@°@"+anfrage.getNewTransmittedConditionColumnsAsString()+"@°@"+(ResultMatrix[0].length-1) 
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
	
	static DefaultMutableTreeNode generateJointDisplay (AnfragePlan anfrage,String titelText,String viewOption,boolean isFirstRun){
		DataNode anfang = new DataNode(titelText);
		anfang.viewOption=viewOption;
		anfang.isLabel=true;
		HashMap<String,TransmittedCondition> forwardJointCond = null;
		if (!isFirstRun && anfrage.transmittedConditions!=null && !anfrage.transmittedConditions.isEmpty()){//Die erste Anfrage bei einem nicht-ersten Run kann Bedingungen übernommen haben, die müssen auch weitergegeben werden.
			forwardJointCond= new HashMap<String,TransmittedCondition>();
			for (TransmittedCondition transmittedCondition : anfrage.transmittedConditions){
				if (transmittedCondition.getDepth()>1 && transmittedCondition.getValue().replace(" ", "").contains("','") == false ){
			  /*Eine einzelne transmitted Conditions auf der Klasse Anfrage kommend kann viele Werte zulassen,z.B.: in (a,b,c,d...). 
				In einer dynamischen Ausführung vererben die parents der aktuellen Anfrage nur einen Wert pro trasmitted Condition, und da die aktuelle Anfrage aber mehere values pro transmitted Condition haben kann, wird sie daher hier ausgeschlossen, also depth >1 !!
				 */
				 TransmittedCondition transmittedConditionClone = TransmittedCondition.cloneCondition(transmittedCondition);
				 transmittedConditionClone.setValue(transmittedConditionClone.getValue().replace("in ('","").replace("' )","").replace("')","").trim());
				forwardJointCond.put(transmittedCondition.getIdentifier(), transmittedConditionClone);
				}
			}
		}
		
		generateJointDisplayRek(anfrage, forwardJointCond,1, anfang,viewOption,isFirstRun);
		anfang.propagateViewOption();
		if (!isFirstRun && anfang.getChildren()!=null && anfang.getChildren().size()>0)  
			anfang = anfang.getFirstChild(); //der erste Label mit dem titleText ist für nicht-firstRuns unnötig und wird gelöscht
		//showTree(anfang,1);
		anfang.determineMaxColumnLength(anfang);
		//showTree(anfang,1);
		if (!isFirstRun && anfang.getChildren() == null)
			return null;
		return anfang.composeTree();
	}
	//hier in dieser Methode findet der eigentümliche DataJoin statt
	static void  generateJointDisplayRek(AnfragePlan anfrage, HashMap<String,TransmittedCondition> inheritedJointCond, int level, DataNode result, String viewOption, boolean isFirstRun ){
		if (anfrage.isSuppressDisplayIfNoData() && anfrage.isEmptyResultset()
				//&& !(isFirstRun && !anfrage.isExecuteOnFirstRun()) 
				&& ( anfrage.isExecuteOnFirstRun() || anfrage.isDataRetrievalTriggeredByGUI())
				) 
				return;//Wenn die Option isSuppressDisplayIfNoData verwendet wird, dann werden leere Ergebnisssätze nicht dargestellt 
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
			logger.debug("AnfragePlan vor der Generierung des Datenbaums\n"+anfrage.showAnfragePlan(true) );
			Set<String> inheritedCondAttr = anfrage.getInheritedApplicableConditions();
			Set<String> forwardedCondAttr = anfrage.getNewTransmittedConditionColumns();
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
		if (anfrage.getInheritedApplicableConditions() != null)
		logger.debug("Try sub-ordinate rows of "+anfrage.getQueryName()+" to "+anfrage.getSuperSQLQuery().getQueryName()+"\n"+resultLineColumnNames.toString().replace("@°@", ","));
		//												HIER BEGINNT DER RECHENINTENSIVE BEREICH!!!!! 		
		int colNr=0, rowNr=1;     
		for (rowNr=1;rowNr < resultMatrix[0].length; rowNr++){//Anfang Schleife durch rows
		boolean rowMatchesInheritedConditions=true;	
 			for (colNr=0;colNr<resultMatrix.length;colNr++ ){//Anfang Schleife durch die einzelnen columns 	
				if ( inheritedJointCond!=null //&& anfrage.isExecuteOnFirstRun() 
					&&  inheritedJointCond.containsKey(columnNamesUpperCase.get(colNr)) //oder wenn der result column name mit einem der condition column names übereinstimmen,
					){//Joint-Bedingungen aus dem aufrufenden Record werden angewandt  
					TransmittedCondition inhertedCondi = inheritedJointCond.get(columnNamesUpperCase.get(colNr)	); 
					if(
					  inhertedCondi.getValue()==null || //wenn die inherited condition aus der oberen Datanode null ist, dann scheitert der Gleichheitsvergleich 
					  (
					  anfrage.getMaxInheritanceDepth()>=inhertedCondi.getDepth()		 	  //...oder wenn Vererbungstiefe höher ist als die maximal zulässige  	
					  //&& !resultMatrix[colNr][rowNr].equals(inhertedCondi.getValue()))
					  && !Pattern.matches(resultMatrix[colNr][rowNr],inhertedCondi.getValue()) )//...und die Werte nicht gleich sind 		  
					  //Wildkey-funktion mit * ist unterstützt     
					  ){
						rowMatchesInheritedConditions=false;
						break;//Für einen Ausschluss der Zeile reicht, dass eine Bedingung nicht erfüllt wird. Bedingungen werden also und-verknüpft.
						}
				}
				//if (!anfrage.hasSuperSQLQuery()){ 
				for (TransmittedCondition cond : forwardJointCond.values() ){
					if(cond.getIdentifier().equals(columnNamesUpperCase.get(colNr)) )
						//Wenn alle Bedingungen erfüllt: Werte aus dieser Spalte werden in die Liste der zu vererbenden Joint Bedingungen aufgenommen 
						cond.setValue(resultMatrix[colNr][rowNr]);					
				//}
				}
				resultLine.append(resultMatrix[colNr][rowNr]+"@°@");//Die Werte einer Ergebniszeile werden zusammengefügt
			}//Ende Schleife durch die einzelnen columns
			//rows, die vererbte Bedingungen nicht erfüllen, werden nicht angezeigt und von der Weitergabe ausgeschlossen
			if (rowMatchesInheritedConditions){
				//System.out.println(multiplyChars(' ',(level-1)*10)+resultLineColumnNames.toString().substring(0, resultLineColumnNames.length()-1)); 
				//System.out.println(multiplyChars(' ',(level-1)*10)+resultLine.toString().substring(0, resultLine.length()-1));
				DataNode newestChild = new DataNode (resultLineColumnNames.toString().substring(0, resultLineColumnNames.length()-3),
													resultLine.toString().substring(0, resultLine.length()-3),newChild);
				logger.debug("The Line "+newestChild.getValues().replace("@°@", ",")+" MATCHED these conditions: "+ inheritedJointCond);
				newestChild.setIsResultColumn(isResultColumn);
				newChild.insertOnLevel(newestChild);
				if (!anfrage.subQueries.isEmpty()){
					int t=0;
					while (anfrage.subQueries.size()>t){
						generateJointDisplayRek(anfrage.subQueries.get(t),forwardJointCond,level+1,newestChild,viewOption,isFirstRun); 
						t++; 	
					}
				}	
			}			
			resultLine.delete(0, resultLine.length()); 
		}//Ende Schleife durch rows
//												HIER ENDET DER RECHENINTENSIVE BEREICH!!!!!

		if (newChild.getChildren().size()== 0){ 
			if (anfrage.isSuppressDisplayIfNoData() && (  anfrage.isExecuteOnFirstRun() ||anfrage.isDataRetrievalTriggeredByGUI() )   ) {  
				result.getChildren().remove(newChild);//Label-Stummel werden beseitigt, wenn die Option isSuppressDisplayIfNoData verwendet wird, außer wenn execute on first run benutzt wird
			}	
			else{
				for (colNr=0;colNr<resultMatrix.length;colNr++ ){
				resultLine.append("null"+"@°@");
				}
			DataNode newestChild = new DataNode (resultLineColumnNames.toString().substring(0, resultLineColumnNames.length()-3),
										    resultLine.toString().substring(0, resultLine.length()-3),newChild);
			newestChild.setIsResultColumn(isResultColumn);
			newChild.insertOnLevel(newestChild);
	
			if (//isFirstRun && 
					anfrage.isExecuteOnFirstRun()==false)
				newestChild.setQueryOnDemand(true);
			}
	}
		resultLine.delete(0, resultLine.length()); 
	
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
	StringBuilder columnNames = new StringBuilder();
	LinkedList<DataNode> selectedNodes = new LinkedList<DataNode>();
	int levelOffset = 9999, level = 0;
	   for (int t=0;t<tp.length;t++){
		   DefaultMutableTreeNode selNode = (DefaultMutableTreeNode)(tp[t].getLastPathComponent()); 
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

static String showTreeStructureDebug(DefaultMutableTreeNode tree,boolean includeSubTrees){
	StringBuilder result=new StringBuilder("");
	result.append(rekShowTreeStructureDebug(tree,includeSubTrees, 1));
	return result.toString();
}

static String rekShowTreeStructureDebug(DefaultMutableTreeNode tree,boolean includeSubTrees, int level){
	StringBuilder result=new StringBuilder("");
	String spaces = DisplayData.multiplyChars(' ',(level-1)*10);
	DataNode node;
	String label;
	if (tree.getUserObject() instanceof DataNode){
		node = (DataNode) tree.getUserObject();
		result.append(spaces + node.toString() + "\n");
		//result.append(spaces + node.getValueArray() + "\n");
		//result.append(spaces + node.getIsResultColumn() + "\n");
		result.append(spaces + "Is query on demand: " + node.isQueryOnDemand() + ", Is ancestor query on demand: " + node.isAncestorQueryOnDemand() +  "\n");
		result.append(spaces + "Parent Node: " + node.getParent() +  "\n");
		result.append(spaces + "First Child: " + node.getChildren() +  "\n");
	}
	if (tree.getUserObject() instanceof String){
		label = (String) tree.getUserObject();
		result.append(spaces + "Label: "+label + "\n");
	}
	if (includeSubTrees && tree.getChildCount()>0)
		for (int t=0; t<tree.getChildCount(); t++) {
			result.append(rekShowTreeStructureDebug( (DefaultMutableTreeNode)tree.getChildAt(t),includeSubTrees, level+1) );
		}
	return result.toString();
}


static List<TransmittedCondition> getApplicableConditionFromNodeParents (DataNode currNode, List<TransmittedCondition> transmittedConditions, boolean includeNotApplicableCond){
	List<TransmittedCondition>applicableConditions = new LinkedList<TransmittedCondition>();
	
	if (currNode==null || transmittedConditions==null || currNode.getColumnArray().length==0 || transmittedConditions.size()==0)
		return applicableConditions;
	
	for (TransmittedCondition transmCondition : transmittedConditions){
	DataNode currNodeCopy = currNode;	
		if (transmCondition.inheritanceIsNotApplicable()==false || includeNotApplicableCond == true ){
			while (currNodeCopy != null){	
				int applCondCount =applicableConditions.size(); 	
					for (int t=0;t<currNodeCopy.getColumnArray().length;t++){
						if(transmCondition.getIdentifier().equals(currNodeCopy.getColumnArray()[t].toUpperCase())){
							applicableConditions.add(new TransmittedCondition(transmCondition.getIdentifier(), currNodeCopy.getValueArray()[t], transmCondition.getDepth(),transmCondition.inheritanceIsNotApplicable()));
						}
					}
					if (applCondCount==applicableConditions.size()//wenn nichts hinzugefügt wurde, dann suche nach Werten für applicable condition in der Node-Hierarchy nach oben  
					&& currNodeCopy.getParent() != null 
					&& currNodeCopy.getParent().getParent() !=null 
					&& currNodeCopy.getParent().getParent() instanceof DataNode
					&& currNodeCopy.getParent().getParent().getColumnArray()!=null)
						currNodeCopy = currNodeCopy.getParent().getParent();
					else 
						currNodeCopy = null;
			}
		}
	}
	return applicableConditions;
}



static String generateSqlQueryFromTreeElement(
		DefaultMutableTreeNode currNode,  List<AnfragePlan> allePlaene) {
	String selection;
	{
		String dataNodeLabel="";
		boolean labelSelection=false;
		DataNode criteriaNode = null;
		List<TransmittedCondition>transmConditions=null;
		String originalSql="", orderBy="";
		String finalSql="";
		StringBuilder whereClause=new StringBuilder();
		String operator="=";
		String varcharDelimiter="'";
		if (currNode.getUserObject() instanceof DataNode){
			criteriaNode=(DataNode)(currNode.getUserObject());
			dataNodeLabel=criteriaNode.getParent().getValues();
		}
		
		if (currNode.getUserObject() instanceof String){
			dataNodeLabel=(String)(currNode.getUserObject());
			labelSelection=true;
			if ( ((DefaultMutableTreeNode) currNode.getParent()).getUserObject() instanceof DataNode )
				criteriaNode=(DataNode)(((DefaultMutableTreeNode) currNode.getParent()).getUserObject());
		}

		for (AnfragePlan a : allePlaene){
			if (a.getQueryName().equals(dataNodeLabel) ){//query label is gleich dem label des ausgewählten nodes
				originalSql=a.getSqlQuery();
				transmConditions=a.transmittedConditions;
				break;
			}
		}
		if (originalSql.toLowerCase().lastIndexOf("order by")>0){//Die order by clauses is entnommen, um am Ende der neuen Query angehängt zu werden  
			orderBy=" "+originalSql.substring(originalSql.toLowerCase().lastIndexOf("order by"));
			if ( !orderBy.toLowerCase().contains("where ") || !orderBy.toLowerCase().contains("from ") || !orderBy.toLowerCase().contains("select ") ){
				originalSql=originalSql.replace(orderBy, "");
			}
		}
		
		if (labelSelection==false){//Node Selection
			for (int t=0;t<criteriaNode.getColumnArray().length && t<15;t++){
				if (criteriaNode.getValueArray()[t].equals("null")){
					operator="is";
					varcharDelimiter="";
					}
				
				else{
					operator="=";
					varcharDelimiter="'";
					}
				whereClause.append("\n\tAND "+criteriaNode.getColumnArray()[t]+" "+operator+" "+varcharDelimiter+criteriaNode.getValueArray()[t]+varcharDelimiter);
			}
		}
		else{//Label Selection
			List<TransmittedCondition> applCond = getApplicableConditionFromNodeParents (criteriaNode, transmConditions,false); 	
			for (TransmittedCondition retrivedCondition: applCond){
				if (retrivedCondition.getValue().equals("null")){
					operator="is";
					varcharDelimiter="";
					}
				else{
					operator="=";
					varcharDelimiter="'";
					}
				whereClause.append("\n\tAND "+retrivedCondition.getIdentifier()+" "+operator+" "+varcharDelimiter+retrivedCondition.getValue()+varcharDelimiter);
			}
		}
		finalSql="select * from (\n"+originalSql+"\n)a\nWhere " + whereClause.toString().replaceFirst("AND ", "");
//		System.out.println(a.getSqlQuery()+"\n"+a.getTransmittedConditionAttributes()+"\n"+finalSql	);
		if (whereClause.length()>0)
			selection=finalSql+ " " +orderBy;
		else
			selection = originalSql;
	}
	return selection;
}

	static String multiplyChars(char ch, int multiplier){
		StringBuilder result = new StringBuilder(ch);  	
		for (int t=1;t<multiplier; t++){
			result.append(ch);
		}	
return result.toString();	
}
	
}
