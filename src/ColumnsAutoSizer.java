import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.Component;
import java.awt.FontMetrics;
 
public class ColumnsAutoSizer {
    public static void sizeColumnsToFit(JTable table) {
        sizeColumnsToFit(table, 5);
    }
 

    public static void sizeColumnsToFit2(JTable table, int columnMargin,DataNode line){
    	JTableHeader tableHeader = table.getTableHeader();
    	FontMetrics lineFontMetrics = table.getFontMetrics(table.getFont());
    	FontMetrics headerFontMetrics = tableHeader.getFontMetrics(tableHeader.getFont());
    	
    	//System.out.println(line+"   MaxColumnLength: "+line.getMaxColumnLength());    	
    	for (int t=0; t<table.getRowCount();t++){
    		for (int tt=0; tt<table.getColumnCount();tt++){
    			table.getColumnModel().getColumn(tt).setPreferredWidth(
    					(columnMargin*2)+line.getMaxColumnLength().get(table.getColumnName(tt))*lineFontMetrics.charsWidth("ABCD1834".toCharArray(),0,8)/8
    					);
    			tableHeader.getColumnModel().getColumn(tt).setPreferredWidth(
    					(columnMargin*2)+line.getMaxColumnLength().get(table.getColumnName(tt))*headerFontMetrics.charsWidth("ABCDabcd1834".toCharArray(),0,12)/12
    					);
    			//System.out.println("Zeile "+t+" Spalte "+tt+" "+" getPreferredWidth"+table.getColumnModel().getColumn(tt).getPreferredWidth()+" line.getMaxColumnLength"+line.getMaxColumnLength().get(table.getColumnName(t)));	
    		}
    	}
    }
    
    public static void sizeColumnsToFit(JTable table, int columnMargin) {
        JTableHeader tableHeader = table.getTableHeader();
        if(tableHeader == null) {
            return;
        } 
        FontMetrics headerFontMetrics = tableHeader.getFontMetrics(tableHeader.getFont());
        int[] minWidths = new int[table.getColumnCount()];
        int[] maxWidths = new int[table.getColumnCount()];
 
        for(int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
            int headerWidth = headerFontMetrics.stringWidth(table.getColumnName(columnIndex));
            minWidths[columnIndex] = headerWidth + columnMargin;
            int maxWidth = getMaximalRequiredColumnWidth(table, columnIndex, headerWidth);
            maxWidths[columnIndex] = Math.max(maxWidth, minWidths[columnIndex]) + columnMargin;
        }
        adjustMaximumWidths(table, minWidths, maxWidths);
        for(int i = 0; i < minWidths.length; i++) {
            if(minWidths[i] > 0) {
                table.getColumnModel().getColumn(i).setMinWidth(minWidths[i]);
            }
            if(maxWidths[i] > 0) {
                table.getColumnModel().getColumn(i).setMaxWidth(maxWidths[i]);
                table.getColumnModel().getColumn(i).setWidth(maxWidths[i]);
                table.getColumnModel().getColumn(i).setMinWidth(maxWidths[i]);                
            }
        }
    }
 
    private static void adjustMaximumWidths(JTable table, int[] minWidths, int[] maxWidths) {
        if(table.getWidth() > 0) {
            // to prevent infinite loops in exceptional situations
            int breaker = 0;
            // keep stealing one pixel of the maximum width of the highest column until we can fit in the width of the table
            while(sum(maxWidths) > table.getWidth() && breaker < 10000) {
                int highestWidthIndex = findLargestIndex(maxWidths);
                maxWidths[highestWidthIndex] -= 1;
                maxWidths[highestWidthIndex] = Math.max(maxWidths[highestWidthIndex], minWidths[highestWidthIndex]);
                breaker++;
            }
        }
    }
    private static int getMaximalRequiredColumnWidth(JTable table, int columnIndex, int headerWidth) {
        int maxWidth = headerWidth;
        TableColumn column = table.getColumnModel().getColumn(columnIndex);
        TableCellRenderer cellRenderer = column.getCellRenderer();
        if(cellRenderer == null) {
            cellRenderer = new DefaultTableCellRenderer();
        }
         for(int row = 0; row < table.getModel().getRowCount(); row++) {
            Component rendererComponent = cellRenderer.getTableCellRendererComponent(table,
                table.getModel().getValueAt(row, columnIndex),
                false,
                false,
                row,
                columnIndex);
            double valueWidth = rendererComponent.getPreferredSize().getWidth();
            maxWidth = (int) Math.max(maxWidth, valueWidth);
        }
         return maxWidth;
    }
 
    private static int findLargestIndex(int[] widths) {
        int largestIndex = 0;
        int largestValue = 0;
        for(int i = 0; i < widths.length; i++) {
            if(widths[i] > largestValue) {
                largestIndex = i;
                largestValue = widths[i];
            }
        }
         return largestIndex;
    }
 
    private static int sum(int[] widths) {
        int sum = 0;
        for(int width : widths) {
            sum += width;
        }
        return sum;
    }
 
  public static int getMaxTableRowWith(JTable table){
	 int summe=0; 
	  for (int t=0; t<table.getColumnCount();t++){
		  summe=summe+table.getColumnModel().getColumn(t).getPreferredWidth();  
	  }
	  return summe;
  }
}