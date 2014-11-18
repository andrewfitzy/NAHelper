package com.about80minutes.helpers.na;

import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.csv.CSVPrinter;
import org.apache.hadoop.thirdparty.guava.common.collect.Lists;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/**
 * Customized model used by the results JTable
 */
@SuppressWarnings("serial")
public class NATableModel extends AbstractTableModel {
	
	public static final String PROPERTY_COLUMN = "Property";
	public static final String COUNT_COLUMN = "Count";
	public static final String MIN_COLUMN = "Min";
	public static final String MAX_COLUMN = "Max";
	public static final String MEAN_COLUMN = "Mean";
	public static final String MEDIAN_COLUMN = "Median";
	public static final String MODE_COLUMN = "Mode";
	public static final String SUM_COLUMN = "Sum";
	public static final String RANGE_COLUMN = "Range";
	public static final String STANDARD_DEVIATION_COLUMN = "Std. Dev.";
	
	private static final String[] COLUMN_NAMES = new String[]{PROPERTY_COLUMN,COUNT_COLUMN,MIN_COLUMN,MAX_COLUMN,MEAN_COLUMN,MEDIAN_COLUMN,MODE_COLUMN,SUM_COLUMN,RANGE_COLUMN,STANDARD_DEVIATION_COLUMN};
	private Table<String, String, String> valueTable = HashBasedTable.create();
	private List<String> properties = Lists.newArrayList();
	
	/**
	 * Sets the table data value
	 * 
	 * @param data a {@link com.google.common.collect.Table} containing the data
	 * to set
	 */
	public void setTableData(Table<String, String, String> data) {
		valueTable = data;
		properties.clear();
		properties.addAll(valueTable.rowKeySet());
		Collections.sort(properties);
		
		this.fireTableDataChanged();
	}
	
	/**
	 * Returns the name of the given column
	 * 
	 * @param colNum an int containing the column number
	 */
	public String getColumnName(int colNum) {
	    return COLUMN_NAMES[colNum];
	}

	/**
	 * Gets a count of the number of columns
	 * 
	 * @return an int containing the number of columns
	 */
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	/**
	 * Gets a count of the number of rows
	 * 
	 * @return an int containing the number of rows
	 */
	public int getRowCount() {
		return properties.size();
	}

	/**
	 * Gets the cell value at a given co-ordinate
	 * 
	 * @param row an int containing the row number
	 * @param column an int containing the column number 
	 * 
	 * @return a {@link java.lang.Object} containing the cell value 
	 */
	public Object getValueAt(int row, int column) {
		String value = properties.get(row);
		if(column > 0) { //property column
			value = valueTable.get(value, COLUMN_NAMES[column]);
		} 
		return value;
	}

	/**
	 * Prints the table data to the given output stream. This method does not
	 * close the stream after processing
	 * 
	 * @param stream a {@link java.io.OutputStream} to write the data to
	 */
	public void toCSV(OutputStream stream) {
		CSVPrinter printer = new CSVPrinter(stream);
		//print header first
		printer.println(COLUMN_NAMES);

		//then print row data
		String[] tmpRow = new String[COLUMN_NAMES.length];
		
		for(String row : valueTable.rowKeySet()) {
			tmpRow[0] = row;
			for(int i = 1;i < COLUMN_NAMES.length;i++) {
				String col = COLUMN_NAMES[i];
				String tmpCellVal = valueTable.get(row, col);
				if(tmpCellVal == null) {
					tmpCellVal = "";
				}
				tmpRow[i] = tmpCellVal;
			}
			printer.println(tmpRow);
		}
	}
}
