
public class TransmittedCondition implements Comparable<TransmittedCondition>{
private String identifier,value ;
private int depth;
private boolean inheritanceIsNotApplicable;
private int notApplicableReason;
private boolean hasMoreThan1000Expressions;

static final int COLUMN_NOT_FOUND = 1;
static final int MAX_DEPTH_EXCEEDED = 2;

public TransmittedCondition(String identifier, String value, int depth) {
	super();
	this.identifier = identifier.toUpperCase();
	this.value=value;	
	this.depth = depth;
}
public TransmittedCondition(String identifier, String value, int depth,boolean isNotApplicable) {
	this(identifier,value,depth);
	this.inheritanceIsNotApplicable=isNotApplicable;
}
public String getValue() {
	return value;
}

public void setValue(String value) {
	this.value = value;
}

public String getIdentifier() {
	return identifier;
}
@Override
public String toString() {
	return "identifier=" + identifier + ", value=" + value + ", depth=" + depth+", inheritanceIsNotApplicable="+inheritanceIsNotApplicable;
}

public void setIdentifier(String identifier) {
	this.identifier = identifier.toUpperCase();
}
public int getDepth() {
	return depth;
}
public void setDepth(int depth) {
	this.depth = depth;
}
public boolean inheritanceIsNotApplicable() {
	return inheritanceIsNotApplicable;
}

public void setInheritanceIsNotApplicable(boolean isNotApplicable) {
	this.inheritanceIsNotApplicable = isNotApplicable;
}
protected int getNotApplicableReason() {
	return notApplicableReason;
}
protected  void setNotApplicableReason(int notApplicableReason) {
	this.notApplicableReason = notApplicableReason;
}
protected boolean hasMoreThan1000Expressions() {
	return hasMoreThan1000Expressions;
}
protected void setHasMoreThen1000Expressions(boolean hasMoreThen1000Expressions) {
	this.hasMoreThan1000Expressions = hasMoreThen1000Expressions;
}
@Override
public int compareTo(TransmittedCondition t) {
 if (this.getIdentifier().equals(t.getIdentifier()) )
	return 0;
 else return -1;
}
public boolean equals(Object anObject) {
    if (this == anObject) {
        return true;
    }
    if (anObject instanceof TransmittedCondition) {
    	TransmittedCondition antherTransCond = (TransmittedCondition)anObject;	 
    	return this.getIdentifier().toUpperCase().equals(antherTransCond.getIdentifier().toUpperCase());
    }
    return false;
}

}
