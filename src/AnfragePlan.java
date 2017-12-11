import java.util.*;
import java.io.PrintStream;
import java.sql.*;

import javax.swing.JTextArea;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class AnfragePlan {
	private static Logger logger = LogManager.getLogger(AnfragePlan.class.getName());
	AnfragePlan superSQLQuery;
	List<AnfragePlan> subQueries; //sqlQuery children 
	private List<String> resultColumns, allResultColumns;	//variable set of columns to be displayed
	private boolean dataGenerated, conditionsReceived, suppressDisplayIfNoData;
	private boolean emptyResultset;
	private String sqlQuery,sqlQueryLabel,rebuiltQuery; //the SQL Query
	private Set<String> transmittedConditionAttributes;//the column names supposed to be inherited by the children in the where-clause of this sqlQuery
	List<TransmittedCondition> transmittedConditions; //the where clause supposed to be inherited by the children of this sqlQuery	
	StringBuilder  inheritedConditions; //columns from the super-sqlQuery that will be added to the where clause of the sqlQuery   
	private int maxInheritanceDepth;
	private String tempTable;
	private String[][] resultRowsMatrix;
	
	AnfragePlan(String query,String queryLabel ) {
		this.sqlQuery = query;
		this.sqlQueryLabel = queryLabel;
		transmittedConditionAttributes = new HashSet<String>();
		this.transmittedConditions=new LinkedList<TransmittedCondition>();
		subQueries = new LinkedList<AnfragePlan>();
		resultColumns = new LinkedList<String>();
		allResultColumns = new LinkedList<String>();
	}
	
	AnfragePlan(String query, String queryName, AnfragePlan superQuery, int maxInheritanceDepth) {
		this(query,queryName);
		if (superQuery != null) {//Super-Query wird mit Sub-Query verlinkt
			this.superSQLQuery = superQuery;
			superQuery.subQueries.add(this);
		}
		if (maxInheritanceDepth>=0) this.maxInheritanceDepth=maxInheritanceDepth;
		else this.maxInheritanceDepth=99;
	}
	@Override
	public String toString() {
		return this.sqlQueryLabel;
	}
	
	public AnfragePlan getSuperSQLQuery() {
		return superSQLQuery;
	}

	public boolean hasSuperSQLQuery() {
		if (this.superSQLQuery == null) return false;
		return true;
	}
	public String getResultColumns(boolean asSelectClause) {
		if(asSelectClause) return getElementsWithSeparator((AbstractCollection<String>)(resultColumns),",");
		return "X";
	}
	public List<String> getResultColumns() {
		return resultColumns;
	}

	public void addResultColumn(String resultColumn) {
	String resCol=resultColumn;
	if (resCol=="") return;
	if (resCol.contains("."))
			resCol= resCol.substring(resCol.indexOf(".")+1);//Der Tabellenname oder der Alias sollen nicht Teil des Bezeichners sein 
	resultColumns.add(resCol.toUpperCase().trim());
	}
	
	public String getQueryName() {
		return sqlQueryLabel;
	}

	public String getInheritedConditionsAsString(boolean applicable){//used for the structure only display
		StringBuilder inheritance=new StringBuilder("  ");
		if (this.superSQLQuery==null || this.superSQLQuery.transmittedConditions==null || this.superSQLQuery.transmittedConditions.size()==0)		
			return " ";	//wenn die Anfragedefinition keine Superquery hat, dann kann sie auch keine Bedingungen erben.  
	for (TransmittedCondition condi:this.superSQLQuery.transmittedConditions){  
		String sternchen="";
		if (this.transmittedConditions.get(this.transmittedConditions.indexOf(condi)).getNotApplicableReason()==TransmittedCondition.MAX_DEPTH_EXCEEDED )
			sternchen="*";
		if (this.transmittedConditions.get(this.transmittedConditions.indexOf(condi)).inheritanceIsNotApplicable()!=applicable  )		
			inheritance.append(condi.getIdentifier()+"("+condi.getDepth()+sternchen+"), ");
	}
			
	return inheritance.deleteCharAt(inheritance.length()-2).toString();
	}
	
	public Set<String> getInheritedConditions(){
		if (this.superSQLQuery==null || this.superSQLQuery.transmittedConditions==null || this.superSQLQuery.transmittedConditions.size()==0)		
			return null;	//wenn die Anfragedefinition keine Superquery hat, dann kann sie auch keine Bedingungen erben.  
	Set<String> inheritance=new HashSet<String>();
	for (TransmittedCondition condi:this.superSQLQuery.transmittedConditions){  //The child asks the parent for for instantiated conditions (column X = something )...  

		if (this.transmittedConditions.get(this.transmittedConditions.indexOf(condi)).inheritanceIsNotApplicable()==false)		
			inheritance.add(condi.getIdentifier());//..and inherits every applicable condition 
	}		 
	return inheritance;
	}
	
	public Set<String> getTransmittedConditionAttributes() {
		return transmittedConditionAttributes;
	}
	
	public String getNewTransmittedConditionAttributesAsString() {//for the structure only view
		StringBuilder transmitted = new StringBuilder("  ");
		boolean hasInheritance =false;
		if (this.transmittedConditions==null ||this.transmittedConditions.size()==0)
			return " ";
		
		if (this.superSQLQuery!=null && this.superSQLQuery.transmittedConditions!=null && this.superSQLQuery.transmittedConditions.size()>0)
			hasInheritance = true;
		
		for (TransmittedCondition condi:this.transmittedConditions){
			 if (!hasInheritance || !this.superSQLQuery.transmittedConditions.contains(condi) )
				 transmitted.append(condi.getIdentifier()+",");
		}
		return transmitted.deleteCharAt(transmitted.length()-1).toString(); 
	}

	public Set<String> getNewTransmittedConditionAttributes() {
		boolean hasInheritance = false;
		if (this.transmittedConditions==null ||this.transmittedConditions.size()==0)
			return null;
		Set<String> newTransmittedConditionAttributes = new HashSet<String>();
		
		if (this.superSQLQuery!=null && this.superSQLQuery.transmittedConditions!=null && this.superSQLQuery.transmittedConditions.size()>0)
			hasInheritance = true;
		
		for (TransmittedCondition condi:this.transmittedConditions){
			 if (!hasInheritance || !this.superSQLQuery.transmittedConditions.contains(condi) )//TODO ist uperCase nötig? 
				 newTransmittedConditionAttributes.add(condi.getIdentifier());
		}
		return newTransmittedConditionAttributes; 
	}

	public void addTransmittedConditionAttribute(String transmittedConditionAttribute) {
		this.transmittedConditionAttributes.add(transmittedConditionAttribute);
	}
	
	public boolean isSuppressDisplayIfNoData() {
		return suppressDisplayIfNoData;
	}

	public void setSuppressDisplayIfNoData(boolean suppressLabelIfNoData) {
		this.suppressDisplayIfNoData = suppressLabelIfNoData;
	}

	public boolean isEmptyResultset() {
		return emptyResultset;
	}

	public boolean isDataGenerated() {
		return dataGenerated;
	}
	
	public String[][] getResultRowsMatrix() {
		return resultRowsMatrix;
	}

	public void setResultRowsMatrix(String[][] resultRowsMatrix) {
		this.resultRowsMatrix = resultRowsMatrix;
	}
	
	public int getMaxInheritanceDepth() {
		return maxInheritanceDepth;
	}
	public List<String> getAllResultColumns() {
		return allResultColumns;
	}

	public void setAllResultColumns(List<String> allResultColumns) {
		this.allResultColumns = allResultColumns;
	}

public boolean generateData(Connection conn, JTextArea txtProgressgMessage) throws Exception{

	if (dataGenerated) return true;
logger.info("Data generation for the query "+this.sqlQueryLabel+" begins.\nThe query as provided in the definition file:\n"+this.sqlQuery);
txtProgressgMessage.append("Process the report entity "+this.sqlQueryLabel+".");
	this.rebuiltQuery = this.sqlQuery;		
		if (!conditionsReceived && this.transmittedConditions!=null && this.subQueries.size()>0 && this.transmittedConditionAttributes.size()==0) 
		{//Wenn eine Query keine Angabe macht, was an die Kinder vererbt werden soll, dann wird alles, was vererbt werden kann,vererbt. Es wird also überprüft, welche column_names mit den der Kinder übereinstimmen, und alle Treffer werden in das Erbe aufgenommen
			for (String columnName : this.getAllResultColumns()){
				for(AnfragePlan childQuery : this.subQueries){
					for (String childColumnName : childQuery.getAllResultColumns()){
						if( columnName.toUpperCase().equals(childColumnName.toUpperCase())) 
							this.transmittedConditionAttributes.add(columnName.toUpperCase());
					}
				}
			}
			DataJumper.warnings.append("Report entity "+this.sqlQueryLabel+" has not specified an own inheritance.");
			logger.info("Report entity "+this.sqlQueryLabel+" has not specified an own inheritance.");
			if (this.getTransmittedConditionAttributes().size()>0){ 
				DataJumper.warnings.append(" Espresso View will now transmit these columns applicable to its children: "+this.getTransmittedConditionAttributes()+"\n\n" );
				logger.info(" Espresso View will now transmit these columns applicable to its children: "+this.getTransmittedConditionAttributes()+"\n\n" );
			}
			else{
				DataJumper.warnings.append(" An applicable condition column to be transmitted to children was not found.\n\n" );
				logger.info(" An applicable condition column to be transmitted to children was not found.\n\n" );
			}
		}	
		if (!conditionsReceived && this.superSQLQuery !=null &&this.superSQLQuery.transmittedConditions!=null && this.superSQLQuery.transmittedConditions.size()>0) 
		{//Query hat ein parent und ist bereit zur Übernahme. Der nicht-erste AnfragePlan übernimmt die übergebenen where-clauseln des parents
			DataJumper.warnings.append(validateAttributes(
					this.superSQLQuery.transmittedConditionAttributes,
					"the inherited columns",
//					"<TransmittedConditionColumns> der oberen Definitionen",
//					"Vererbte Bedingungen können nicht angewendet werden",
//					"in der SQL-Abfrage der Anfragedefinition >"+this.sqlQueryLabel+"< NICHT angeführt")
					"One or more inherited columns are not applicable"
					,"not applicable")
					);
			List<String> rawInheritedCondition = new LinkedList<String>();
			for (TransmittedCondition toInherit : this.superSQLQuery.transmittedConditions){//jetzt wird überpüft, ob die übernommenen where-clauseln des parents auf die Anfrage anwendbar sind
				if (toInherit.getDepth()<=this.maxInheritanceDepth && DataJumper.containsCaseInsensitive(this.allResultColumns,toInherit.getIdentifier())
					&& !columnHasWildKey(toInherit.getIdentifier(),this.rebuiltQuery)	
					)
					rawInheritedCondition.add(toInherit.getIdentifier()+toInherit.getValue());
			}
			if( rawInheritedCondition.size()>0  ){ 
				//Die Übernahme der where-clauseln des parents passiert hier
				this.inheritedConditions= new StringBuilder(getElementsWithSeparator( (AbstractCollection<String>)(rawInheritedCondition),"AND") );//die möglicherweise vielen übernommen conditions werde und-gebunden	 
				conditionsReceived=true;
				this.rebuiltQuery="select * from ("+this.rebuiltQuery+"\n) 	a where "+this.inheritedConditions;//die übernommenen where-clauseln werden in die SQL-Anfrage übertragen  
			}
					
			for (TransmittedCondition inheritedConditionToTransmit: this.superSQLQuery.transmittedConditions){ //die übernommenen where-clausel werden auch in das eigene Erbe aufgenommen, um weitergegeben zu werden
				this.transmittedConditionAttributes.add(inheritedConditionToTransmit.getIdentifier());
				TransmittedCondition newInheritedConditionToTransmit = new TransmittedCondition(
																								inheritedConditionToTransmit.getIdentifier(),
																								inheritedConditionToTransmit.getValue(), 
																								inheritedConditionToTransmit.getDepth()+1);
				if (inheritedConditionToTransmit.getDepth()>this.maxInheritanceDepth)
				{
					newInheritedConditionToTransmit.setInheritanceIsNotApplicable(true);//Flagged den Sachverhalt, ob eine vererbte Bedingung angewendet werden kann
					newInheritedConditionToTransmit.setNotApplicableReason(TransmittedCondition.MAX_DEPTH_EXCEEDED);
				}
				if (!DataJumper.containsCaseInsensitive(this.getAllResultColumns(),newInheritedConditionToTransmit.getIdentifier())){
					newInheritedConditionToTransmit.setInheritanceIsNotApplicable(true);//Flagged den Sachverhalt, ob eine vererbte Bedingung angewendet werden kann
					newInheritedConditionToTransmit.setNotApplicableReason(TransmittedCondition.COLUMN_NOT_FOUND);
				}
				//else newInheritedConditionToTransmit.setInheritanceIsNotApplicable(false); 
				this.transmittedConditions.add(newInheritedConditionToTransmit );
			}
		}
		this.rebuiltQuery = "insert into "+this.tempTable+" "+this.rebuiltQuery;
		try { 	
			Statement stmnt = conn.createStatement();
			stmnt.execute(this.rebuiltQuery);stmnt.close();
			logger.info("Query "+this.getQueryName()+" rebuilt and sucessfully executed:");
			logger.info(rebuiltQuery);
			generateDataMatrix(conn);
			logger.info("Retrieved "+ this.resultRowsMatrix[0].length +"rows with each "+(this.resultRowsMatrix.length-1)+" columns" );
			txtProgressgMessage.append("Done, "+this.resultRowsMatrix[0].length +" rows.\n");
			dataGenerated=true;
			if (!transmittedConditionAttributes.isEmpty() ){	 	
			setTransmittedConditions(); //das Erbe, aslo die where-clause Einschränkung auf column_names, wird mit Daten aus der Anfrage ausgestaltet 
			}
	}catch (SQLException sq) {
		ErrorMessage.showException(sq,"Database server has thrown an SQL error while executing the query "+this.sqlQueryLabel+":\n"+this.sqlQuery+"\n");
		logger.error("Database server has thrown an SQL error while executing the query:\n"+this.sqlQueryLabel+":\n"+this.rebuiltQuery+"\n"+sq.getMessage()+"\n"+ErrorMessage.showStackTrace(sq.getStackTrace()));
		DBConnections.dropAllTempTables();
		return false;
	}
	//System.out.println(  transmittedConditions+"\n");
	
	int t=0;
	if (!this.subQueries.isEmpty()){
		while (this.subQueries.size()>t){
		boolean	success = this.subQueries.get(t).generateData(conn,txtProgressgMessage);
		if (!success) return false;
		t++; 	
    	}
	}
return true;
}

public void setTransmittedConditions() throws SQLException{
//Diese methode nimmt die zu vererbenden column_names(transmittedConditionAttributes) und geht damit durch die daten der Query durch und updated die zu vererbende Einschränkung  
	Set<String>applicableConditionAttributes= new HashSet<String>();   
	for (String trCond : this.transmittedConditionAttributes){
		if (DataJumper.containsCaseInsensitive(this.allResultColumns,trCond)){   
			applicableConditionAttributes.add(trCond);//Es kann vorkommen, dass eine query Bedingungen weitervererbt, die auf sie selbst nicht angewendet werden können. Da die auch keine Daten haben können werden sie ausgeschlossen
		}
	}
	if (applicableConditionAttributes.size()==0)return;	
	String derGuteRat = validateAttributes(
							 applicableConditionAttributes,
						    "<TransmittedConditionColumns>",
						    "Zu vererbende Bedingungen sind nicht definiert",
						    "in der SQL-Abfrage NICHT selektiert");
if (!derGuteRat.equals("")){
	throw new SQLException (derGuteRat) ;
}

for (int t=0; t<resultRowsMatrix.length;t++  ) {
	//System.out.println(resultRowsMatrix[t][0] ); 
	if (DataJumper.containsCaseInsensitive(applicableConditionAttributes,resultRowsMatrix[t][0] )){//resultRowsMatrix[t][0] ist der column name
	String columnName = resultRowsMatrix[t][0];	
		HashSet<String> conditionValues = new HashSet<String>();
		//System.out.println(resultRowsMatrix[t][0]);
		boolean hasMoreThan1000Expressions=false;
		if (resultRowsMatrix[t].length >=1000) 
			hasMoreThan1000Expressions=true;
		for (int tt=1;tt<resultRowsMatrix[t].length && tt<=1000 ;tt++){
			//System.out.println(resultRowsMatrix[t][tt]); 
			conditionValues.add("'"+resultRowsMatrix[t][tt]+"'");
		}
		if (conditionValues.size()>0){
			if (!this.transmittedConditions.contains(new TransmittedCondition(columnName,null,0))){
				this.transmittedConditions.add(new TransmittedCondition(columnName, " in " +
											   "("+getElementsWithSeparator((AbstractCollection<String>)(conditionValues),",").
											   replaceAll("'null'", "null")//in where clauses soll null und nicht 'null' werwendet werden 
											   +")",1));
				if 	(hasMoreThan1000Expressions)
					this.transmittedConditions.get(this.transmittedConditions.size()-1).setHasMoreThen1000Expressions(true);
			}
			else
				this.transmittedConditions.get(
							this.transmittedConditions.indexOf(new TransmittedCondition(columnName,null,0))
							).setValue(" in ("+getElementsWithSeparator((AbstractCollection<String>)(conditionValues),",") +")");
		}
		else this.transmittedConditions.add(new TransmittedCondition(columnName, " = null",1)); 	
	}
}
//old implementation with select distinct 
//String getTransmittedConditions ="Select distinct "+getElementsWithSeparator((AbstractCollection<String>)(applicableConditionAttributes),",")+ "from "+dTable;
//PreparedStatement stmnt = conn.prepareStatement(getTransmittedConditions, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
//	ResultSet rs = stmnt.executeQuery(); 
//	for (String columnName : applicableConditionAttributes) {
//		HashSet<String> conditionValues = new HashSet<String>();
//			while (rs.next()){
//				conditionValues.add("'"+rs.getString(columnName)+"'");
//			}
//		if (conditionValues.size()>0){
//			if (!this.transmittedConditions.contains(new TransmittedCondition(columnName,null,0)))
//				this.transmittedConditions.add(new TransmittedCondition(columnName, " in ("+getElementsWithSeparator((AbstractCollection<String>)(conditionValues),",") +")",1));
//			else
//				this.transmittedConditions.get(
//							this.transmittedConditions.indexOf(new TransmittedCondition(columnName,null,0))
//							).setValue(" in ("+getElementsWithSeparator((AbstractCollection<String>)(conditionValues),",") +")");
//		}
//		else this.transmittedConditions.add(new TransmittedCondition(columnName, " = null",1)); 
//		rs.beforeFirst();
//	}
//	stmnt.close();
//	rs.close();
}


public String  validateAttributes( Collection <String> conditionAttributes,String tagName,String missstandGrob, String missstandDetail) {
	String derGuteRat="";
	List<String> badConditions= new LinkedList<String>(); 	
	for (String condAttr:conditionAttributes){
		boolean attributeExists = false; 	
				if (DataJumper.containsCaseInsensitive(this.allResultColumns,condAttr.replaceAll("NOT:", "").trim() ) )
					attributeExists=true;					
			if (attributeExists == false) badConditions.add(condAttr);
		}	
	if (badConditions.size()>0 && DataBaseLogin3.warningLevel.equals("HIGH")) {
		String MessageNumerusForm="";
			if (badConditions.size()==1 ) MessageNumerusForm = "One column name part of "+tagName+" is" ;
			else MessageNumerusForm = "Following column names part of "+tagName+" are";
		
		if (badConditions.size()==conditionAttributes.size() ) MessageNumerusForm = "All column names of "+tagName+" are" ;
		if (!MessageNumerusForm.equals(""))
			derGuteRat= missstandGrob+ " in report entity "+ this.getQueryName()+"\n"+MessageNumerusForm+" "+missstandDetail +
						": "+badConditions.toString().replace("[", "").replace("]", "")+"\n\n" ;
	}
return derGuteRat; 
}

public boolean columnHasWildKey(String columnName, String query ){
// returns true if the given column name contains the wildkey * 
query=query.toUpperCase().replaceAll(" ", "").replaceAll("\n", "");
if (!query.contains(columnName+",") && !query.contains(columnName+"FROM")) // a column name is followed either by , or by the word from
	return false;
String previousColumn;
int currCol=0;
for (currCol=0;currCol<this.allResultColumns.size();currCol++){
	if (this.allResultColumns.get(currCol).toUpperCase().equals(columnName)){
		break;
	}
}
if (currCol==0)
	previousColumn="SELECT";
else
	previousColumn = this.allResultColumns.get(currCol-1).toUpperCase()+",";

if (query.contains(columnName+","))
	columnName=columnName+",";
else
	columnName=columnName+"FROM";
String relevantPart ="";
relevantPart = query.substring(query.indexOf(previousColumn)+previousColumn.length(), query.indexOf(columnName));

if (relevantPart.contains("*"))	
	return true;
	else return false;
}

void generateDataMatrix(Connection conn){ 
String getDisplayData=null;
	try{
	int maxRowNum = getNumberofRows("Select count(*) from "+this.tempTable, conn);
	getDisplayData="Select * from "+this.tempTable;
	//System.out.println("\n\n"+getDisplayData);
	PreparedStatement stmnt = conn.prepareStatement(getDisplayData, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
	ResultSet rs = stmnt.executeQuery();
	ResultSetMetaData rsMeta = rs.getMetaData();
	String[][] resultRowsMatrix;
	if (maxRowNum==0)  
		{resultRowsMatrix = new String[rsMeta.getColumnCount()][2];//der "leere" Ergebnisssatz hat zwei Zeilen, eine mit den column_names und eine mit leeren Feldern 
		this.emptyResultset = true;
		}
	else 
		resultRowsMatrix = new String[rsMeta.getColumnCount()][maxRowNum+1];
	for (int t =0; t<rsMeta.getColumnCount();t++){
		resultRowsMatrix[t][0] = rsMeta.getColumnName(t+1);//die erste Zeile enthält die Feldnamen
	}
	int rowNum=1, colNum=0;
	if (maxRowNum==0) {//wenn der resultset leer ist...
		for (colNum=0; colNum<rsMeta.getColumnCount() ; colNum++) {
			resultRowsMatrix[colNum][1]="null";//... wird eine Ergebniszeile kreiert, und alle Felder enthalten das Wort null 
		}
	}
	else 
		for (colNum=0; colNum<rsMeta.getColumnCount() ; colNum++) {
			String res="";
			while (rs.next()){
			try{	
				res =rs.getString(resultRowsMatrix[colNum][0]);
				resultRowsMatrix[colNum][rowNum]=res;
				if (res!=null && res !="") 
						res.replaceAll("\\*", ".*");
			}catch (Exception e) {
				logger.error("rowNum "+rowNum+" colNum "+colNum +" rs.getString(resultRowsMatrix[colNum][0]) = "+res);
				logger.error(e.getStackTrace());
			}	
				//System.out.println(resultRowsMatrix[colNum][rowNum]+" "+(colNum)+" "+rowNum);
				rowNum++;}
			rs.beforeFirst();	
			rowNum=1;
		}
	stmnt.close();
	rs.close();
	conn.createStatement().execute("drop table "+tempTable);
	this.setResultRowsMatrix(resultRowsMatrix);
	
	}catch (SQLException sq) {
	ErrorMessage.showException("Database vendor thrown an error while executing the query:\n"+getDisplayData+"\n"+sq.getMessage()+"\n"+ErrorMessage.showStackTrace(sq.getStackTrace()));
logger.error("Database thrown error while executing the query:\n"+getDisplayData+"\n"+sq.getMessage()+"\n"+ErrorMessage.showStackTrace(sq.getStackTrace()));
	}
}

static String generateTempTable(Connection conn, AnfragePlan anfrage) {
	String tempTableName="";
	try{
		DatabaseMetaData db = conn.getMetaData();
		
		if (conn.getMetaData().getDatabaseProductName().toLowerCase()
				.contains("oracle"))//create a temporary table in Oracle
		{
			String[] types ={"TABLE"};
			ResultSet rs= db.getTables(null, db.getUserName(), "XVW$%",types);
			int tabCounter=1;
			while (rs.next()) {
				tabCounter++;
			}rs.close();
			tempTableName="XVW$"+tabCounter+anfrage.sqlQueryLabel.replace(" ", "_").replace(".", "pkt").toUpperCase();
			if (tempTableName.length()>30) tempTableName=tempTableName.substring(0,29);
			PreparedStatement stmnt =conn.prepareStatement("create global temporary table "+tempTableName+ " as select * from ("+anfrage.sqlQuery+"\n)a where 1=0");
			stmnt.execute();
			logger.info("Created temporary table "+tempTableName);
			DataJumper.createdTables.add(tempTableName);
			rs= db.getColumns(null, db.getUserName(), tempTableName, "%");
			while (rs.next()) {
				anfrage.allResultColumns.add(rs.getString("COLUMN_NAME"));
			}rs.close();
			logger.info("Temporary Table Schema "+ anfrage.allResultColumns);
		}
		
		if (conn.getMetaData().getDatabaseProductName().toLowerCase()
				.contains("mysql"))//create a temporary table in mysql
		{
			String[] types ={"TABLE"};
			ResultSet rs= db.getTables(null, db.getUserName(), "XVW$%",types);
			tempTableName="XVW$"+anfrage.sqlQueryLabel.replace(" ", "_").replace(".", "pkt").toUpperCase();
			if (tempTableName.length()>30) tempTableName=tempTableName.substring(0,29);
			PreparedStatement stmnt =conn.prepareStatement("create temporary table "+tempTableName+ " as select * from ("+anfrage.sqlQuery+"\n)a where 1=0");
			stmnt.execute();
			logger.info("Created temporary table "+tempTableName);
			DataJumper.createdTables.add(tempTableName);
			PreparedStatement colNames = conn.prepareStatement("select * from "+tempTableName);//in mySQL you cannot retrieve the temporary table name and columns, so you have find them out the hard way  
			ResultSetMetaData rsMeta =colNames.executeQuery().getMetaData();
			for (int t =1; t<=rsMeta.getColumnCount();t++){
				anfrage.allResultColumns.add(rsMeta.getColumnName(t));
			}
			logger.info("Temporary Table Schema "+ anfrage.allResultColumns);
			while (rs.next()) {
				anfrage.allResultColumns.add(rs.getString("COLUMN_NAME"));
			}rs.close();
		}
		
	}catch (SQLException sq) {
		ErrorMessage.showException(sq,"Database thrown error while executing the query "+anfrage.sqlQueryLabel+":\n"+anfrage.sqlQuery+"\n");
		logger.error("Database thrown error while executing the query:\n"+anfrage.sqlQueryLabel+":\n"+anfrage.sqlQuery+"\n"+sq.getMessage()+"\n"+ErrorMessage.showStackTrace(sq.getStackTrace()));
		DBConnections.dropAllTempTables();
		tempTableName = null;
	}
	DataJumper.warnings.append(
			anfrage.validateAttributes(anfrage.resultColumns,
								"<ResultColumns>",
								"Defined report column names cannot be displayed",
								"not mentioned in the Select-clause of the SQL-Query")
								) ;
	if (anfrage.resultColumns.size()==0)
		anfrage.resultColumns=anfrage.allResultColumns;
	return tempTableName;
}

static boolean generateAllTempTables(Connection conn, AnfragePlan anfrage){
//Das Schema für die Anfrageergebnisse wird erzeugt als temporäre Tabelle
	boolean success=true;
	anfrage.tempTable= AnfragePlan.generateTempTable(conn,anfrage);
	if (anfrage.tempTable==null) 
		return false;

	if (anfrage.subQueries.size()>0){
		for (AnfragePlan kindAnfrage : anfrage.subQueries){
			success = generateAllTempTables(conn, kindAnfrage);
			if (!success)
				break;
		}
	}
	return success;
}

String getElementsWithSeparator (AbstractCollection<String> coll, String separator){
	if (coll.size()==0) return "";  
	String elements ="";
	for (String str:coll){
		 elements=elements+str+" "+separator+ " ";
	 }
	 elements=elements.substring(0, elements.lastIndexOf(separator));
	   return elements;
}

int getNumberofRows (String query, Connection conn ){
int numberOfRows=0;	
	try{
		PreparedStatement stmnt=conn.prepareStatement(query);		
		ResultSet rs =stmnt.executeQuery();
		while (rs.next())
		numberOfRows= rs.getInt(1);
		stmnt.close();
		rs.close();
	}catch (SQLException sq) {
		ErrorMessage.showException(sq,"Database thrown error while executing the query:\n"+query);
		logger.error("Database thrown error while executing the query:\n"+query+"\n"+sq.getMessage()+"\n"+ErrorMessage.showStackTrace(sq.getStackTrace()));
		numberOfRows=0;
	}
	return numberOfRows;
}

}