package me.dec7.study.holub.database;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface Cursor {

	String tableName();
	
	boolean advance() throws NoSuchElementException;
	
	Object column(String columnName);
	
	Iterator columns();
	
	boolean isTraversing(Table t);
	
	Object update(String columnName, Object newValue);
	
	void delete();
}
