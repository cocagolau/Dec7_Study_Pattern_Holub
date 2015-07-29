package me.dec7.study.holub.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import me.dec7.study.holub.tools.ArrayIterator;

public class CSVImporter implements Table.Importer {
	
	private BufferedReader in;
	
	private String[] columnNames;
	
	private String tableName;
	
	public CSVImporter(Reader in) {
		this.in = in instanceof BufferedReader ? (BufferedReader) in : new BufferedReader(in);
	}

	@Override
	public void startTable() throws IOException {
		tableName = in.readLine().trim();
		columnNames = in.readLine().split("\\s*,\\s*");
	}

	@Override
	public String loadTableName() throws IOException {
		
		return tableName;
	}

	@Override
	public int loadWidth() throws IOException {
		
		return columnNames.length;
	}

	@Override
	public Iterator loadColumnNames() throws IOException {
		
		return new ArrayIterator(columnNames);
	}

	@Override
	public Iterator loadRow() throws IOException {
		Iterator row = null;
		if (in != null) {
			String line = in.readLine();
			if (line == null) {
				in = null;
			} else {
				row = new ArrayIterator(line.split("\\s*,\\s*"));
			}
		}
		
		return row;
	}

	@Override
	public void endTable() throws IOException { }
	
	

}
