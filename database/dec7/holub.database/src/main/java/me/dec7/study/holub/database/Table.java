package me.dec7.study.holub.database;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

public interface Table extends Serializable, Cloneable {

	Object clone() throws CloneNotSupportedException;
	
	String name();
	
	void rename(String newName);
	
	boolean isDirty();
	
	int insert(String[] columnNames, Object[] values);
	
	int insert(Collection columnNames, Collection values);
	
	int insert(Object[] values);
	
	int insert(Collection values);
	
	int update(Selector where);
	
	int delete(Selector where);
	
	public void begin();
	
	public void commit(boolean all) throws IllegalStateException;
	
	public void rollback(boolean all) throws IllegalStateException;
	
	public static final boolean THIS_LEVEL = false;
	
	public static final boolean ALL = true;
	
	Table select(Selector where, String[] requestedColumns, Table[] other);
	
	Table select(Selector where, String[] requestedColumns);
	
	Table select(Selector where);
	
	Table select(Selector where, Collection requestedColumns, Collection other);
	
	Table select(Selector where, Collection requestedColumns);
	
	Cursor rows();
	
	void export(Table.Exporter exporter) throws IOException;
	
	
	public interface Exporter {
		
		public void startTable() throws IOException;
		
		public void storeMetadata(String tableName, int width, int height, Iterator columnNames) throws IOException;
		
		public void storeRow(Iterator data) throws IOException;
		
		public void endTable() throws IOException;
		
	}
	
	public interface Importer {
		
		void startTable() throws IOException;
		
		String loadTableName() throws IOException;
		
		int loadWidth() throws IOException;
		
		Iterator loadColumnNames() throws IOException;
		
		Iterator loadRow() throws IOException;
		
		void endTable() throws IOException;
		
	}
}
