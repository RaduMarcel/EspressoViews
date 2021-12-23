import java.util.*;

import javax.swing.tree.*;
class DataNode {
	private DataNode parent;
	private LinkedList<DataNode> children = new LinkedList<DataNode>();
	private boolean[] isResultColumn;
	String values,columns, viewOption;
	private String[] valueArray, resultValueArray;
	private String[] columnArray, resultColumnArray;	
	int childNumber; //Ordinalzahl. Gibt die Position des Nodes als Kind innerhalb der Kinderliste an. 0 ist das erste Kind 
	Map<String,Integer> maxColumnLength;
	boolean isLabel, queryOnDemand;
	AnfragePlan anfrage;

	DataNode(String cont) {
		values = cont;
		if (cont==null || cont.equals(""))
		values = "null";
		valueArray=this.values.split("@°@"); 
	}
	DataNode() {
		values = "null";	
		valueArray=this.values.split("@°@");
	}
	
	DataNode(String cont, DataNode parent) {
		this(cont);
		this.parent=parent;
	}

	DataNode(String columns, String cont,  DataNode parent) {
		this(cont,parent);
		this.columns=columns;
		this.columnArray=this.columns.split("@°@");
	}
	
	String getValues() {
		return values;
	}
	
	void setContent(String neu) {
		if (neu ==null) neu = "null";
		values = neu;
		valueArray=this.values.split("@°@");
	}
	
	public String getColumns() {
		return columns;
	}
	
	public void setColumns(String columns) {
		this.columns = columns;
		this.columnArray=this.columns.split("@°@");
	}
	
	DataNode getParent() {
		return parent;
	}
	boolean hasParent() {
		if (parent==null) return false;
		return true;
	}
	
	void setParent(DataNode p) {
		parent = p;
	}
	public boolean isQueryOnDemand() {
		return queryOnDemand;
	}
	public boolean isAncestorQueryOnDemand() {
		if (this.hasParent()==false)
			return false;		
		return getParent().rekIsAncestorQueryOnDemand();
	}
	private boolean rekIsAncestorQueryOnDemand() {
		if (queryOnDemand) 
			return true;
		if (this.hasParent()==false)
			return false;
		return getParent().rekIsAncestorQueryOnDemand();
	}
	
	
	public void setQueryOnDemand(boolean queryOnDemand) {
		this.queryOnDemand = queryOnDemand;
	}
	
	public String getViewOption() {
		return viewOption;
	}
	public void setViewOption(String viewOption) {
		this.viewOption = viewOption;
	}
	
	public void propagateViewOption(){
		if (this.children.size()>0 && this.viewOption!=null) { 
			for (DataNode kinderchen:this.children){
				kinderchen.setViewOption(this.viewOption);
				if (kinderchen.children.size()>0)
					for (DataNode enkelchen:kinderchen.children){
						enkelchen.setViewOption(viewOption);
						enkelchen.propagateViewOption();
					}
			}
		}	
	}
	
	public int getChildNumber() {
		return childNumber;
	}
	public void setChildNumber(int childNumber) {
		this.childNumber = childNumber;
	}
	LinkedList<DataNode> getChildren() {
		return children;
	}
	public void setChildren(LinkedList<DataNode>newChildren) {
		this.children= newChildren;
	}
	DataNode getFirstChild(){
		LinkedList<DataNode> children = this.getChildren();
		return children.getFirst();
	}

	DataNode insertOnLevel(DataNode child) {
		children.add(child);
		child.setChildNumber(children.lastIndexOf(child));
		return this;
	}
	DataNode insertDeep(DataNode child) {
		this.children.add(child);
		child.setChildNumber(children.lastIndexOf(child));
		child.setParent(this);
		return child;
	}
	public boolean[] getIsResultColumn() {
		return isResultColumn;
	}
	public void setIsResultColumn(boolean[] isResultColumn) {
		this.isResultColumn = isResultColumn;
	}
	public Map<String, Integer> getMaxColumnLength() {
		return maxColumnLength;
	}
//	public void determinetMaxColumnLengthOld(DataNode node) {//ermittelt die längste Zeichenkette in jeder Spalte für jede Menge von Ergebniszeilen, die an einem Knoten/Label hängen
////		DisplayData.showTree(this, 0);
//		if (node.children.size()>0){//Unter einem label sollte es immer entweder eine oder mehrere Ergebniszeilen oder nichts geben. Da ein label keine Ergeniszeile ist, hat er auch keine Spalten.
//			if (node.children.get(0).isLabel){//Ergebniszeilen und labels sind nicht auf der gleichen Ebene, aber falls unerwarteterweise sich unter einem Label noch ein Label befindet, geht das Ganze mit diesem Label noch mal von vorne los.
//				determinetMaxColumnLengthOld(node.children.get(0));
//				return;
//			}
//			Map<String,Integer> columnsWidth = new HashMap<String,Integer>();//diese Map wird den Wert der längsten Zeichenkette pro Spalte tragen. Dieses Objekt wird an jedem der Kinder eines labels in die Variable maxColumnLength eingehakt     
//			String[] columnArray = node.children.get(0).getColumnArray(true); 
//			for (int t=0; t<columnArray.length; t++){
//				columnsWidth.put(
//						columnArray[t], 
//						Math.max(columnArray[t].length(), node.children.get(0).getValueArray(true)[t].length()) 
//						);//die Map wird mit den Maximalwerten zwischen columnName und Value gefüllt. Falls es nur eine Ergebnisszeile gibt, enthält diese Map schon die Maximalwerte   
//			}
//			node.children.get(0).maxColumnLength=columnsWidth;//Map wird an die Objektvariable dieses ersten DatenKnotens eingehakt (als Startwert), Es wird sich dieses Objekt mit ihren Brüderknoten teilen.  
//			for (int t=0; t<node.children.size();t++ ){
//				if (node.children.get(t).getChildren().size()>0) {//falls einer der Kinder auch Kinder hat
//					determinetMaxColumnLengthOld(node.children.get(t));
//					//return;
//				}
//				determinetMaxColumnLengthSiblingsOld(node.children.get(t),columnsWidth);//die extra Methode wurde übersichtshalber eingeführt 
//			}
//		}
//	}
//	
//	public void determinetMaxColumnLengthSiblingsOld(DataNode node,Map<String,Integer> columnsWidth) {
//		String[] columnArray = node.getColumnArray(true);
//		for (int t=0; t<columnArray.length; t++){
//			columnsWidth.put(
//					columnArray[t], 
//					Math.max( columnArray[t].length(), columnsWidth.get(columnArray[t]).intValue() ) 
//					);//Der Maximalwert wird bei jedem Knotenkind neu ermittelt  
//		}
//		node.maxColumnLength=columnsWidth;
//		if (node.children.size()>=0) {//Falls der Ergebnisszeilenknoten selbst Kinder hat und diese Kinder erwartungsgemäß label sind, 
//			for (DataNode kinderchen:node.children){
//				if (kinderchen.isLabel||kinderchen.children.size()>0)
//					determinetMaxColumnLengthOld(kinderchen);//...dann beginnt für jedes Labelkind das Ganze von vorn. Dieser Methodenaufruf ist eigentlich rekursiv, denn determinetMaxColumnLengthChildren ist Teil der Kinder-Schleife in der Methode determinetMaxColumnLength
//			}
//		}				
//	}

void determineMaxColumnLength(DataNode node){		
		if (node.children!=null ){
		 for (DataNode kindchen:node.children)//Preorder Baumdurchlauf
				determineMaxColumnLength(kindchen);
		 }
			
	if (!node.isLabel){ 
		if (node.getChildNumber()==0 )//Der Node ist das erste Kind seines Parents
		{
			//System.out.print("Erster!!!!!");
			Map<String,Integer> columnsWidth = new HashMap<String,Integer>();//diese Map wird den Wert der längsten Zeichenkette pro Spalte tragen. Dieses Objekt wird an jedem der Kinder eines labels in die Variable maxColumnLength eingehakt     
			String[] columnArray = node.getColumnArray(true); 
			for (int t=0; t<columnArray.length; t++){
				columnsWidth.put(
						columnArray[t], 
						Math.max(columnArray[t].length(), node.getValueArray(true)[t].length()) 
						);//die Map wird mit den Maximalwerten zwischen columnName und Value gefüllt. Falls es nur eine Ergebnisszeile gibt, enthält diese Map schon die Maximalwerte   
			}
			node.maxColumnLength=columnsWidth;//Map wird an die Objektvariable dieses ersten DatenKnotens eingehakt (als Startwert), Es wird sich dieses Objekt mit ihren Brüderknoten teilen	
		}
		else {//der node ist nicht erste das Kind  
			node.maxColumnLength = node.getParent().getChildren().get(node.getChildNumber()-1).maxColumnLength; //also bildet es ein Link zum maxColumnLength-Objekt des ersten Kindes  
			String[] columnArray = node.getColumnArray(true); 
			for (int t=0; t<columnArray.length; t++){//...und macht dann den Längen-Vergleich zwischen seinem String und dem bisher längsten String, und zwar für jede Spalte. 
				node.maxColumnLength.put(
						columnArray[t], 
						Math.max(node.maxColumnLength.get(columnArray[t]), node.getValueArray(true)[t].length()) );
			}
		}			
	}
}	
	
	public String[] getValueArray() {return valueArray;}
	
	public String[] getValueArray(boolean resultSetColumnsOnly){
		if (this.resultValueArray == null)
			setValueArray(resultSetColumnsOnly);
	return this.resultValueArray; 
	}
	
	private void setValueArray(boolean resultSetColumnsOnly) {//only user selected columns are selected

	if(this.isResultColumn==null){
	this.isResultColumn= new boolean[valueArray.length];
	for (int t=0;t<valueArray.length; t++){
		this.isResultColumn[t]=true;
		}
	}
	int counter=0;
	for (int t=0;t<this.isResultColumn.length;t++)
		if (this.isResultColumn[t]==resultSetColumnsOnly)
			counter++;
	String [] adjustedValueArray= new String [counter];  		
	counter=0;
	for (int t=0;t<this.isResultColumn.length;t++) 
		if (this.isResultColumn[t]==resultSetColumnsOnly){
			adjustedValueArray[counter]=this.getValueArray()[t];
			counter++;
		}
	this.resultValueArray = adjustedValueArray;
	}
	
	public String[] getColumnArray() {return columnArray;}
	
	public String[] getColumnArray(boolean resultSetColumnsOnly){
		if (this.resultColumnArray == null)
			setColumnArray(resultSetColumnsOnly);
	return this.resultColumnArray; 
	}
	
	private void setColumnArray(boolean resultSetColumnsOnly) {
		
	if(this.isResultColumn==null){//nur für alle Fälle 
		this.isResultColumn= new boolean[valueArray.length];
		for (int t=0;t<valueArray.length; t++){
			this.isResultColumn[t]=true;
		}
	}
	int counter=0;
	for (int t=0;t<this.isResultColumn.length;t++)
		if (this.isResultColumn[t]==resultSetColumnsOnly)
			counter++;
	String [] adjustedColumnArray = new String [counter];  		
	counter=0;
	for (int t=0;t<this.isResultColumn.length;t++) 
		if (this.isResultColumn[t]==resultSetColumnsOnly){
			adjustedColumnArray[counter]=this.getColumnArray()[t];
			counter++;
		}
	this.resultColumnArray = adjustedColumnArray;
	}
	
	public int getRowLength(){
		int rowLength=0;
		if (isLabel) values.length();
		else {
			for(String column_name : this.getColumnArray(true) )
				rowLength += maxColumnLength.get(column_name);
		}
		return rowLength;  
	}
	
	public static int[] getTreeNodeCount(DefaultMutableTreeNode treeNode){
		int [] result = new int[2];
		if (treeNode == null) {
			result[0]++;
			return result;
		}
		Object treeObject = ((DefaultMutableTreeNode) treeNode).getUserObject();
		if (treeObject instanceof DataNode )
			result[1]++;
		else result[0]++;
		Enumeration<DefaultMutableTreeNode> kinderchen=treeNode.children(); 
		while (kinderchen.hasMoreElements()){
				rekGetTreeNodeCount(result, kinderchen.nextElement());
			}
		return result;
	}
	
	public static void rekGetTreeNodeCount(int[]result, DefaultMutableTreeNode treeNode){
		
		Object treeObject = ((DefaultMutableTreeNode) treeNode).getUserObject();
		if (treeObject instanceof DataNode )
			result[1]++;
		else result[0]++;
		Enumeration<DefaultMutableTreeNode> kinderchen=treeNode.children(); 
		while (kinderchen.hasMoreElements()){
				rekGetTreeNodeCount(result, kinderchen.nextElement());
			}
	}
	
	DefaultMutableTreeNode composeTree() {
		return rekComposeTree(this);
	}
	
	DefaultMutableTreeNode rekComposeTree(DataNode et) {
	if (et.children.isEmpty()){
		if (et.isLabel) return new DefaultMutableTreeNode(et.getValues());
		if (!et.isLabel)return new DefaultMutableTreeNode(et);
	}
	DefaultMutableTreeNode baum;
	if (et.isLabel)
		 baum = new DefaultMutableTreeNode(et.getValues() );
	else 
		baum = new DefaultMutableTreeNode(et);
		Iterator<DataNode> it = et.children.iterator();
		while (it.hasNext()) {
			DataNode child = it.next();
			baum.add(rekComposeTree(child));
		}
		return baum;
	}
	
	@Override
	public String toString() {
		return  "columnArray=" + Arrays.toString(columnArray) +"  "+ 
				"valueArray=" + Arrays.toString(valueArray) ;
		
	}
}