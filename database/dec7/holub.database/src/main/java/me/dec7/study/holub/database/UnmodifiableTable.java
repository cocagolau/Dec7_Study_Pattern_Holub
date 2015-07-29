package me.dec7.study.holub.database;

import java.io.IOException;
import java.util.Collection;

public class UnmodifiableTable implements Table {
	
	private Table wrapped;
	
	public UnmodifiableTable(Table wrapped) {
		this.wrapped = wrapped;
	}
	
	public Table extract() {
		
		return wrapped;
	}
	
	private final void illegal() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String name() {
		
		return wrapped.name();
	}

	@Override
	public void rename(String newName) {
		wrapped.rename(newName);
	}

	@Override
	public boolean isDirty() {
		
		return wrapped.isDirty();
	}

	@Override
	public int insert(String[] columnNames, Object[] values) {
		illegal();
		return 0;
	}

	@Override
	public int insert(Collection columnNames, Collection values) {
		illegal();
		return 0;
	}

	@Override
	public int insert(Object[] values) {
		illegal();
		return 0;
	}

	@Override
	public int insert(Collection values) {
		illegal();
		return 0;
	}

	@Override
	public int update(Selector where) {
		illegal();
		return 0;
	}

	@Override
	public int delete(Selector where) {
		illegal();
		return 0;
	}

	@Override
	public void begin() {
		illegal();
	}

	@Override
	public void commit(boolean all) throws IllegalStateException {
		illegal();
	}

	@Override
	public void rollback(boolean all) throws IllegalStateException {
		illegal();
	}

	@Override
	public Table select(Selector where, String[] requestedColumns, Table[] other) {
		
		return wrapped.select(where, requestedColumns, other);
	}

	@Override
	public Table select(Selector where, String[] requestedColumns) {
		
		return wrapped.select(where, requestedColumns);
	}

	@Override
	public Table select(Selector where) {
		
		return wrapped.select(where);
	}

	@Override
	public Table select(Selector where, Collection requestedColumns, Collection other) {
		
		return wrapped.select(where, requestedColumns, other);
	}

	@Override
	public Table select(Selector where, Collection requestedColumns) {
		
		return wrapped.select(where, requestedColumns);
	}

	@Override
	public Cursor rows() {
		
		return wrapped.rows();
	}

	@Override
	public void export(Exporter exporter) throws IOException {
		wrapped.export(exporter);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		UnmodifiableTable copy = (UnmodifiableTable) super.clone();
		copy.wrapped = (Table) (wrapped.clone());
		
		return copy;
	}
	
	@Override
	public String toString() {
		
		return wrapped.toString();
	}

}
