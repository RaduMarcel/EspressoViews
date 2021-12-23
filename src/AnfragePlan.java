import java.util.*;
import java.sql.*;
import javax.swing.JTextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class AnfragePlan {
	private static Logger logger = LogManager.getLogger(AnfragePlan.class.getName());
	AnfragePlan superSQLQuery;
	List<AnfragePlan> subQueries; //sqlQuery children 
	private List<String> resultColumns, allResultColumns;	//variable set of columns to be displayed
	private boolean dataGenerated, conditionsReceived, suppressDisplayIfNoData, executeOnFirstRun;
	private boolean emptyResultset;
	private String sqlQuery,sqlQueryLabel,rebuiltQuery, dynamicSqlQuery; //the SQL Query
	private Set<String> transmittedConditionColumns;//the column names supposed to be inherited by the children in the where-clause of this sqlQuery
	List<TransmittedCondition> transmittedConditions; //the where clause supposed to be inherited by the children of this sqlQuery	
	StringBuilder  inheritedConditions; //columns from the super-sqlQuery that will be added to the where clause of the sqlQuery   
	private int maxInheritanceDepth, maximumResultRows;

	private String tempTable;
	private String[][] resultRowsMatrix;
	
	AnfragePlan(String query,String queryLabel ) {
		this.sqlQuery = query;
		this.sqlQueryLabel = queryLabel;
		transmittedConditionColumns = new HashSet<String>();
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
	public String getSqlQuery() {
		return sqlQuery;
	}
	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}
	public String getDynamicSqlQuery() {
		return dynamicSqlQuery;
	}
	public void setDynamicSqlQuery(String dynamicSqlQuery) {
		this.dynamicSqlQuery = dynamicSqlQuery;
	}
	public AnfragePlan getSuperSQLQuery() {
		return superSQLQuery;
	}

	public boolean hasSuperSQLQuery() {
		if (this.superSQLQuery == null) return false;
		return true;
	}
	@SuppressWarnings("unchecked")
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
		int idx;
		for (TransmittedCondition cond:this.superSQLQuery.transmittedConditions){  
			String sternchen="";
			idx = this.transmittedConditions.indexOf(cond);
			if (idx>=0 && this.transmittedConditions.get(idx).getNotApplicableReason()==TransmittedCondition.MAX_DEPTH_EXCEEDED )
				sternchen="*";
			if (idx>=0 && this.transmittedConditions.get(idx).inheritanceIsNotApplicable()!=applicable  )		
				inheritance.append(cond.getIdentifier()+"("+cond.getDepth()+sternchen+"), ");
		}
			
	return inheritance.deleteCharAt(inheritance.length()-2).toString();
	}
	
	public Set<String> getInheritedApplicableConditions(){
		if (this.superSQLQuery==null || this.superSQLQuery.transmittedConditions==null || this.superSQLQuery.transmittedConditions.size()==0)		
			return null;	//wenn die Anfragedefinition keine Superquery hat, dann kann sie auch keine Bedingungen erben.  
	Set<String> inheritance=new HashSet<String>();
	int idx;
	for (TransmittedCondition cond:this.superSQLQuery.transmittedConditions){  //The child asks the parent for for instantiated conditions (column X = something )...  
		idx=this.transmittedConditions.indexOf(cond);
		if (idx >= 0 && this.transmittedConditions.get(idx).inheritanceIsNotApplicable()==false)		
			inheritance.add(cond.getIdentifier());//..and inherits every applicable condition 
	}		 
	return inheritance;
	}
	
	public Set<String> getTransmittedConditionColumns() {
		return transmittedConditionColumns;
	}
	
	public String getNewTransmittedConditionColumnsAsString() {//for the structure only view
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

	public Set<String> getNewTransmittedConditionColumns() {
		boolean hasInheritance = false;
		if (this.transmittedConditions==null ||this.transmittedConditions.size()==0)
			return null;
		Set<String> newTransmittedConditionColumn = new HashSet<String>();
		
		if (this.superSQLQuery!=null && this.superSQLQuery.transmittedConditions!=null && this.superSQLQuery.transmittedConditions.size()>0)
			hasInheritance = true;
		
		for (TransmittedCondition condi:this.transmittedConditions){
			 if (!hasInheritance || !this.superSQLQuery.transmittedConditions.contains(condi) )//TODO ist upperCase nötig? 
				 newTransmittedConditionColumn.add(condi.getIdentifier());
		}
		return newTransmittedConditionColumn; 
	}

	public void addTransmittedConditionAttribute(String transmittedConditionColumn) {
		this.transmittedConditionColumns.add(transmittedConditionColumn);
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
	public boolean isExecuteOnFirstRun() {
		return executeOnFirstRun;
	}
	//returns true if there is no Anfrageplan ancestors  or if all Anfrageplan ancestors have executeOnFirstRun == true    
	public boolean isAncestorExecuteOnFirstRun() {
		if (this.hasSuperSQLQuery()==false)
			return true;		
		return this.getSuperSQLQuery().rekIsAncestorExecuteOnFirstRun();
	}
	private boolean rekIsAncestorExecuteOnFirstRun() {
		if (executeOnFirstRun==false) 
			return false;
		if (this.hasSuperSQLQuery()==false)
			return true;
		return getSuperSQLQuery().rekIsAncestorExecuteOnFirstRun();
	}

	public void setExecuteOnFirstRun(boolean queryOnDemand) {
		this.executeOnFirstRun = queryOnDemand;
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
	public int getMaximumResultRows() {
		return maximumResultRows;
	}

	public void setMaximumResultRows(int maximumResultRows) {
		this.maximumResultRows = maximumResultRows;
	}
	
	public List<String> getAllResultColumns() {
		return allResultColumns;
	}

	public void setAllResultColumns(List<String> allResultColumns) {
		this.allResultColumns = allResultColumns;
	}

@SuppressWarnings("unchecked")
public boolean generateData(Connection conn, JTextArea txtProgressgMessage, boolean isFirstRun) throws Exception{

	if (dataGenerated && isFirstRun) return true;
logger.debug("Data generation for the query "+this.sqlQueryLabel+" begins.");
logger.debug("Using the SQL query provided in the definition file:\n"+this.sqlQuery);
txtProgressgMessage.append("Process the report entity '"+this.sqlQueryLabel+"'.");

if (isFirstRun==false) { 
	if  (this.isExecuteOnFirstRun()==false && this.getDynamicSqlQuery()!=null && this.getDynamicSqlQuery().length()>0 ){
		this.rebuiltQuery=this.getDynamicSqlQuery();
//		this.setDynamicSqlQuery(null);
	}
	else{
		if (this.isAncestorExecuteOnFirstRun()==false && this.inheritedConditions != null && this.inheritedConditions.length()>0)
			this.conditionsReceived = false;
			this.rebuiltQuery = this.sqlQuery;
	}
		
}
else	
this.rebuiltQuery = this.sqlQuery;
	
//-------------------------------------------------------------------------	
		if (!conditionsReceived && this.transmittedConditions!=null && this.subQueries.size()>0 && this.transmittedConditionColumns.size()==0) 
		{//Wenn eine Query keine Angabe macht, was an die Kinder vererbt werden soll, dann wird alles, was vererbt werden kann,vererbt. Es wird also überprüft, welche column_names mit den der Kinder übereinstimmen, und alle Treffer werden in das Erbe aufgenommen
			for (String columnName : this.getAllResultColumns()){
				for(AnfragePlan childQuery : this.subQueries){
					for (String childColumnName : childQuery.getAllResultColumns()){
						if( columnName.toUpperCase().equals(childColumnName.toUpperCase())) 
							this.transmittedConditionColumns.add(columnName.toUpperCase());
					}
				}
			}
			DataJumper.warnings.append("Report entity "+this.sqlQueryLabel+" has not specified an own inheritance.");
			logger.debug("Report entity "+this.sqlQueryLabel+" has not specified an own inheritance.");
			if (this.getTransmittedConditionColumns().size()>0){ 
				DataJumper.warnings.append(" Espresso View will now transmit these columns applicable to its children: "+this.getTransmittedConditionColumns()+"\n\n" );
				logger.debug(" Espresso View will now transmit these columns applicable to its children: "+this.getTransmittedConditionColumns()+"\n\n" );
			}
			else{
				DataJumper.warnings.append(" An applicable condition column to be transmitted to children was not found.\n\n" );
				logger.debug(" An applicable condition column to be transmitted to children was not found.\n\n" );
			}
		}
//-------------------------------------------------------------------------		
		if (!conditionsReceived && this.superSQLQuery !=null && this.superSQLQuery.transmittedConditions!=null && this.superSQLQuery.transmittedConditions.size()>0) 
		{//Die Query hat ein parent und ist bereit zur Übernahme. Der nicht-erste AnfragePlan übernimmt die übergebenen where-clauseln des parents
			DataJumper.warnings.append(validateAttributes(
					this.superSQLQuery.transmittedConditionColumns,
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
					
			for (TransmittedCondition inheritedConditionToTransmit: this.superSQLQuery.transmittedConditions){ //die übernommenen where-clausel werden schließlich in das eigene Erbe aufgenommen, um weitergegeben zu werden
				this.transmittedConditionColumns.add(inheritedConditionToTransmit.getIdentifier());
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
				//überprüfen, ob es die transmittedConditions schon gibt, und wenn ja, dann überschreibe die existierende mit der neuen, sonst ganz normal einfügen
				if (this.transmittedConditions.contains(newInheritedConditionToTransmit) == false)
					this.transmittedConditions.add(newInheritedConditionToTransmit );
				else
					{
					this.transmittedConditions.remove(transmittedConditions.get(this.transmittedConditions.indexOf(newInheritedConditionToTransmit)));
					this.transmittedConditions.add(newInheritedConditionToTransmit );
					}
					
			}
		}
//-------------------------------------------------------------------------
		//Das bewirkt einen leeren Ergebnissatz bei der automatischen Ausführung, wenn die Anfrage selbst oder einer seiner Vorfahren nicht automatisch mit ausgeführt werden soll    
		if 	(  (isFirstRun==true && (this.isExecuteOnFirstRun()==false || this.isAncestorExecuteOnFirstRun()==false)) 
				|| (isFirstRun==false && this.isExecuteOnFirstRun()==false && this.getDynamicSqlQuery()==null)//..es wird auch die nicht-erste Anfrage nicht ausgeführt, wenn sie nicht manuell ausgeführt wurde soll (nur be manueller Ausführung ist this.getDynamicSqlQuery() nicht null ) 
				)
		{
			this.rebuiltQuery ="select * from ("+this.rebuiltQuery+")b where 1=0";
		}
		this.rebuiltQuery = "insert into "+this.tempTable+" "+this.rebuiltQuery;
		this.setDynamicSqlQuery(null);
		try { 	
//			System.out.println(  this.rebuiltQuery);
			Statement stmnt = conn.createStatement();
			stmnt.execute(this.rebuiltQuery);
		    stmnt.close();
			logger.debug("Query "+this.getQueryName()+" rebuilt and sucessfully executed:");
			logger.debug(rebuiltQuery);
			String SQLExecutionFeedback="";
			SQLExecutionFeedback=generateDataMatrix(conn);
			if (SQLExecutionFeedback!=null && SQLExecutionFeedback!="")
				txtProgressgMessage.append(SQLExecutionFeedback);
			logger.debug("Retrieved "+ this.resultRowsMatrix[0].length +" rows with each "+(this.resultRowsMatrix.length-1)+" columns" );
			txtProgressgMessage.append("Done, "+this.resultRowsMatrix[0].length +" rows.\n");
			dataGenerated=true;
			if 	(!rebuiltQuery.endsWith("b where 1=0") && !transmittedConditionColumns.isEmpty() ){	 	
			setTransmittedConditions(); //das Erbe, also die where-clause Einschränkung auf column_names, wird, sofern möglich, mit Values aus dem eigenen Ergebnissatz aufgefrischt 
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
		boolean	success = this.subQueries.get(t).generateData(conn,txtProgressgMessage,isFirstRun);// TODO: isFirstRun sollte nicht propagiert werden sondern immer true sein   
		if (!success) return false;
		t++; 	
    	}
	}
return true;
}

public void setTransmittedConditions() throws SQLException{
//Diese Methode nimmt die zu vererbenden column_names(transmittedConditionColumns) und geht damit durch die Daten der Query durch und updated die zu vererbende Einschränkung für die nachfolgenden Kindern
//	Das wird gemacht um den Datensatz für die Kinder-Queries auf das Nötigste zu minimieren 
	Set<String>applicableConditionColumns= new HashSet<String>();   
	for (String trCond : this.transmittedConditionColumns){
		if (DataJumper.containsCaseInsensitive(this.allResultColumns,trCond)){   
			applicableConditionColumns.add(trCond);//Es kann vorkommen, dass eine Query Bedingungen weitervererbt, die auf sie selbst nicht angewendet werden können. Da die auch keine Daten haben können werden sie ausgeschlossen
		}
	}
	if (applicableConditionColumns.size()==0)return;	
	String derGuteRat = validateAttributes(
							 applicableConditionColumns,
						    "<TransmittedConditionColumns>",
						    "Zu vererbende Bedingungen sind nicht definiert",
						    "in der SQL-Abfrage NICHT selektiert");
if (!derGuteRat.equals("")){
	throw new SQLException (derGuteRat) ;
}

for (int t=0; t<resultRowsMatrix.length;t++  ) {
	//System.out.println(resultRowsMatrix[t][0] ); 
	if (DataJumper.containsCaseInsensitive(applicableConditionColumns,resultRowsMatrix[t][0] )){//resultRowsMatrix[t][0] ist der column name
	String columnName = resultRowsMatrix[t][0];	
		HashSet<String> conditionValues = new HashSet<String>();
		//System.out.println(resultRowsMatrix[t][0]);
		boolean hasMoreThan1000Expressions=false;
		if (resultRowsMatrix[t].length <1000)//wenn eine transmitted condition column mehr als 1000 Werte weiterverebt, dann entfällt diese Optimierung ganz, denn (zumindest Oracle) erlaubt nur 1000 Werte in der "in"-clause. Es kann natürlich sein, dass andere Anbieter andere Grenzen setzten, dann müsste man den Wert vom Datenbankanbieter abhängig machen
		{	
			for (int tt=1;tt<resultRowsMatrix[t].length;tt++){
				//System.out.println(resultRowsMatrix[t][tt]); 
				conditionValues.add("'"+resultRowsMatrix[t][tt]+"'");
			}
		}
		else 
			hasMoreThan1000Expressions=true;
		
		if (conditionValues.size()>0){
			if (!this.transmittedConditions.contains(new TransmittedCondition(columnName,null,0))){
				this.transmittedConditions.add(new TransmittedCondition(columnName, " in " +
											   "("+getElementsWithSeparator((AbstractCollection<String>)(conditionValues),",").
											   replaceAll("'null'", "null")//in where clauses soll null und nicht 'null' werwendet werden 
											   +")",1));
			}
			else
				this.transmittedConditions.get(
							this.transmittedConditions.indexOf(new TransmittedCondition(columnName,null,0))
							).setValue(" in ("+getElementsWithSeparator((AbstractCollection<String>)(conditionValues),",") +")");
		}
			if (conditionValues.size()==0 && hasMoreThan1000Expressions==false) 
				this.transmittedConditions.add(new TransmittedCondition(columnName, " = null",1));//damit wird der Ergebnissatz null, denn die applicable condition hat keine Werte zum weitergeben 
			if (hasMoreThan1000Expressions)
			{
				if (!this.transmittedConditions.contains(new TransmittedCondition(columnName,null,0)))
					this.transmittedConditions.add(new TransmittedCondition(columnName, " like '%'",1));
				else 
					this.transmittedConditions.get(this.transmittedConditions.indexOf(new TransmittedCondition(columnName,null,0))).setValue(" like '%'");	
			}
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

String generateDataMatrix(Connection conn) {
String getDisplayData=null,SQLExecutionFeedback=null;
	try{
	int maxRowNum = getNumberofRows("Select count(*) from "+this.tempTable, conn);
	getDisplayData="Select * from "+this.tempTable;
	//System.out.println("\n\n"+getDisplayData);
	PreparedStatement stmnt = conn.prepareStatement(getDisplayData, ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
	ResultSet rs = stmnt.executeQuery();
	ResultSetMetaData rsMeta = rs.getMetaData();
	String[][] resultRowsMatrix;
	if (maxRowNum>this.getMaximumResultRows())
		{
		SQLExecutionFeedback="\nThe query '"+this.getQueryName()+"' retrieves "+maxRowNum+" rows. This is more than the defined maximum of "+this.getMaximumResultRows()+" rows." +
				"\nThe first retrieved "+this.getMaximumResultRows()+" rows are further used and the other "+(maxRowNum-this.getMaximumResultRows())+" are not considered " +
				 "\n(use the attribute 'MaxResultRows' in the the XML tag SQLQueryDefinition to can set the maximum rows number)\n";
		maxRowNum=this.getMaximumResultRows()-1;
		logger.debug(SQLExecutionFeedback);
		}
	if (maxRowNum==0)  
		{
		resultRowsMatrix = new String[rsMeta.getColumnCount()][2];//der "leere" Ergebnisssatz hat zwei Zeilen, eine mit den column_names und eine mit leeren Feldern 
		this.emptyResultset = true;
		}
	else 
		{
		resultRowsMatrix = new String[rsMeta.getColumnCount()][maxRowNum+1];
		this.emptyResultset=false;
		}
	for (int t = 0; t<rsMeta.getColumnCount();t++){
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
			while (rowNum<=maxRowNum && rs.next()){
			try{	
				res =rs.getString(resultRowsMatrix[colNum][0]);//get result set string für ein column name
				resultRowsMatrix[colNum][rowNum]=res;
				if (res!=null && res.length() > 0) 
					 resultRowsMatrix[colNum][rowNum].replaceAll("\\*", ".*");
				else 
					if (conn.getMetaData().getDatabaseProductName().toLowerCase().contains("mysql") && res!= null && res.length() == 0 )
						//bei MySql gibt es einen Unterschied zwischen Empty String '' und null, demnach egibt die where clause a='' true wenn a='' 
						//Bei Oracle gibt's das nicht so, Empty String wird wie null behandelt uns so ergibt a='' sie wie auch bei null unknown, und also false      
						resultRowsMatrix[colNum][rowNum]="";
					else
						resultRowsMatrix[colNum][rowNum]="null";
			}catch (Exception e) {
				logger.error("rowNum "+rowNum+" colNum "+colNum +" rs.getString(resultRowsMatrix[colNum][0]) = "+res);
				logger.error(ErrorMessage.showStackTrace(e.getStackTrace()) );
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
	return SQLExecutionFeedback;
}

boolean hasData (){
	if (this.resultRowsMatrix == null || this.resultRowsMatrix.length <=0)
		return false;
	return true;
}

static String generateTempTable(Connection conn, AnfragePlan anfrage) {
	String tempTableName="";
	try{
		DatabaseMetaData db = conn.getMetaData();
		String colName;
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
			logger.debug("Created temporary table "+tempTableName);
			DataJumper.createdTables.add(tempTableName);
			rs = db.getColumns(null, db.getUserName(), tempTableName, "%");
			while (rs.next()) {
				colName = rs.getString("COLUMN_NAME");
				if (anfrage.allResultColumns.isEmpty() || !anfrage.allResultColumns.contains(colName)) 
				anfrage.allResultColumns.add(colName);
			}rs.close();
			logger.debug("Temporary Table Schema "+ anfrage.allResultColumns);
		}
		
		if (conn.getMetaData().getDatabaseProductName().toLowerCase().contains("mysql"))//create a temporary table in mysql
		{
			String[] types ={"TABLE"};
			ResultSet rs= db.getTables(null, db.getUserName(), "XVW$%",types);
			tempTableName="XVW$"+anfrage.sqlQueryLabel.replace(" ", "_").replace(".", "pkt").toUpperCase();
			if (tempTableName.length()>30) tempTableName=tempTableName.substring(0,29);
			PreparedStatement stmnt =conn.prepareStatement("create temporary table "+tempTableName+ " as select * from ("+anfrage.sqlQuery+"\n)a where 1=0");
			stmnt.execute();
			logger.debug("Created temporary table "+tempTableName);
			DataJumper.createdTables.add(tempTableName);
			PreparedStatement colNames = conn.prepareStatement("select * from "+tempTableName);//in mySQL you cannot retrieve the temporary table name and columns, so you have find them out the hard way  
			ResultSetMetaData rsMeta =colNames.executeQuery().getMetaData();
			for (int t =1; t<=rsMeta.getColumnCount();t++){
				colName = rsMeta.getColumnName(t);
				if (anfrage.allResultColumns.isEmpty() || !anfrage.allResultColumns.contains(colName)) 
				anfrage.allResultColumns.add(colName);
			}
			logger.debug("Temporary Table Schema "+ anfrage.allResultColumns);
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

String showAnfragePlan(boolean includeSubTree ){
	StringBuilder result = new StringBuilder();
	showAnfragePlanRek(1,includeSubTree,result);
	return result.toString();
	}

void showAnfragePlanRek(int level, boolean includeSubTree, StringBuilder result ){
	String spaces = DisplayData.multiplyChars(' ',(level-1)*10); 
	
	result.append(spaces + "SQL Query Label: "+this.getQueryName()+"\n");
	result.append(spaces + "Execute on first run: "+this.executeOnFirstRun + ", suppress display if no data: "+this.suppressDisplayIfNoData +"\n");
	result.append(spaces + "All Result Columns: "+this.allResultColumns+"\n");
	result.append(spaces + "SQL Query\n"+this.getSqlQuery()+"\n\n");	
	result.append(spaces + "Rebuilt SQL Query\n"+spaces+this.rebuiltQuery+"\n\n");
	result.append(spaces + "Data generated: "+this.dataGenerated + ", empty result set: "+this.emptyResultset + " conditions received: "+this.conditionsReceived +"\n");
	result.append(spaces + "Result Set\n");
	String ResultMatrix[][] = this.getResultRowsMatrix();
	int colNr=0, rowNr=0;
	for (rowNr=0;rowNr<ResultMatrix[0].length; rowNr++){ 
		result.append(spaces);
		for (colNr=0;colNr<ResultMatrix.length;colNr++ ){
			result.append(ResultMatrix[colNr][rowNr]+"\t");
		}
		result.append("\n");
	}
	result.append(spaces + "Inherited Conditions "+this.inheritedConditions+"\n");
		result.append(spaces + "Transmitted Conditions\n"+spaces+this.transmittedConditions+"\n");
	result.append(spaces + "Dynamic SQL Query\n"+spaces+this.dynamicSqlQuery+"\n");
	if (includeSubTree){
		for (AnfragePlan a:this.subQueries){
			a.showAnfragePlanRek(level+1, includeSubTree,result);
		}
	}
//	return result.toString();
}

}