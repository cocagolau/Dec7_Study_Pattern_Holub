/*  (c) 2004 Allen I. Holub. All rights reserved.
 *
 *  This code may be used freely by yourself with the following
 *  restrictions:
 *
 *  o Your splash screen, about box, or equivalent, must include
 *    Allen Holub's name, copyright, and URL. For example:
 *
 *      This program contains Allen Holub's SQL package.<br>
 *      (c) 2005 Allen I. Holub. All Rights Reserved.<br>
 *              http://www.holub.com<br>
 *
 *    If your program does not run interactively, then the foregoing
 *    notice must appear in your documentation.
 *
 *  o You may not redistribute (or mirror) the source code.
 *
 *  o You must report any bugs that you find to me. Use the form at
 *    http://www.holub.com/company/contact.html or send email to
 *    allen@Holub.com.
 *
 *  o The software is supplied <em>as is</em>. Neither Allen Holub nor
 *    Holub Associates are responsible for any bugs (or any problems
 *    caused by bugs, including lost productivity or data)
 *    in any of this code.
 */
package com.holub.database;

import java.io.*;
import java.util.*;
import com.holub.tools.ArrayIterator;

/** A concrete implementation of the {@link Table} interface that
 *  implements an in-memory table. Most of the methods of this
 *  class are documented in the {@link Table} class.
 *  <p>
 *  It's best to
 *  create instances of this class using the {@link TableFactory}
 *  rather than <code>new</code>.
 *  <p>Note that a ConcreteTable is both serializable and "Cloneable",
 *  so you can easily store it onto the disk in binary form
 *  or make a copy of it. Clone implements a shallow copy, however,
 *  so it can be used to implement a rollback of an insert or delete,
 *  but not an update.
 *  <p>
 *  This class is not thread safe.
 *
 * @include /etc/license.txt
 */

/*package*/ class ConcreteTable implements Table
{
	// Supporting clone() complicates the following declarations. In
	// particular, the fields can't be final because they're modified
	// in the clone() method. Also, the rows field has to be declared
	// as a Linked list (rather than a List) because Cloneable is made
	// public at the LinkedList level. If you declare it as a list,
	// you'll get an error message because clone()---for reasons that
	// are mysterious to me---is declared protected in Object.
	//
	// Be sure to change the clone() method if you modify anything about
	// any of these fields.

	private LinkedList	rowSet = new LinkedList();
	private String[]	columnNames;
	private String		tableName;

	private transient boolean	 isDirty			= false;
	private transient LinkedList transactionStack	= new LinkedList();

	/**********************************************************************
	 *  Create a table with the given name and columns.
	 *  @param tableName the name of the table.
	 *  @param an array of Strings that specify the column names.
	 */
	public ConcreteTable( String tableName, String[] columnNames )
	{	this.tableName   = tableName;
		this.columnNames = (String[]) columnNames.clone();
	}
	/**********************************************************************
	 * Return the index of the named column. Throw an
	 * IndexOutOfBoundsException if the column doesn't exist.
	 */
	private int indexOf( String columnName )
	{	for( int i = 0; i < columnNames.length; ++i )
			if( columnNames[i].equals( columnName ) )
					return i;

		throw new IndexOutOfBoundsException(
					"Column ("+columnName+") doesn't exist in " + tableName );
	}
	//@simple-construction-end
	//
	/**********************************************************************
	 * Create a table using an importer. See {@link CSVImporter} for
	 * an example.
	 */
	public ConcreteTable( Table.Importer importer ) throws IOException
	{	importer.startTable();

		tableName		 =	importer.loadTableName();
		int width		 =  importer.loadWidth();
		Iterator columns =	importer.loadColumnNames();

		this.columnNames = new String[ width ];
		for(int i = 0; columns.hasNext() ;)
			columnNames[i++] = (String) columns.next();

		while( (columns = importer.loadRow()) != null )
		{	Object[] current = new Object[width];
			for(int i = 0; columns.hasNext() ;)
				current[i++] = columns.next();
			this.insert( current );
		}
		importer.endTable();
	}
	//----------------------------------------------------------------------
	public void export( Table.Exporter exporter ) throws IOException
	{	exporter.startTable();
		exporter.storeMetadata( tableName,
		                        columnNames.length,
		                        rowSet.size(),
								new ArrayIterator(columnNames) );

		for( Iterator i = rowSet.iterator(); i.hasNext(); )
			exporter.storeRow( new ArrayIterator((Object[]) i.next()) );

		exporter.endTable();
		isDirty = false;
	}
	//@import-export-end
	//----------------------------------------------------------------------
	// Inserting
	//
	public int insert( String[] intoTheseColumns, Object[] values )
	{
		assert( intoTheseColumns.length == values.length )
				:"There must be exactly one value for "
				+"each specified column" ;

		Object[] newRow = new Object[ width() ];

		for( int i = 0; i < intoTheseColumns.length; ++i )
			newRow[ indexOf(intoTheseColumns[i]) ] = values[i];

		doInsert( newRow );
		return 1;
	}
	//----------------------------------------------------------------------
	public int insert( Collection intoTheseColumns, Collection values )
	{	assert( intoTheseColumns.size() == values.size() )
				:"There must be exactly one value for "
					+"each specified column" ;

		Object[] newRow = new Object[ width() ];

		Iterator v = values.iterator();
		Iterator c = intoTheseColumns.iterator();
		while( c.hasNext() && v.hasNext() )
			newRow[ indexOf((String)c.next()) ] = v.next();

		doInsert( newRow );
		return 1;
	}
	//----------------------------------------------------------------------
	public int insert( Map row )
	{	// A map is considered to be "ordered,"  with the order defined
		// as the order in which an iterator across a "view" returns
		// values. My reading of this statement is that the iterator
		// across the keySet() visits keys in the same order as the
		// iterator across the values() visits the values.

		return insert ( row.keySet(), row.values() );
	}
	//----------------------------------------------------------------------
	public int insert( Object[] values )
	{	assert values.length == width()
			: "Values-array length (" + values.length + ") "
			+ "is not the same as table width (" + width() +")";

		doInsert( (Object[]) values.clone() );
		return 1;
	}
	//----------------------------------------------------------------------
	public int insert( Collection values )
	{	return insert( values.toArray() );
	}
	//----------------------------------------------------------------------
	private void doInsert( Object[] newRow )
	{
		rowSet.add( newRow );
		registerInsert( newRow );
		isDirty = true;
	}
	//@insert-end
	//----------------------------------------------------------------------
	// Traversing and cursor-based Updating and Deleting
	//
	public Cursor rows()
	{	return new Results();
	}
	//----------------------------------------------------------------------
	private final class Results implements Cursor
	{	private final Iterator rowIterator	= rowSet.iterator();
		private 	  Object[] row			= null;

		public String tableName()
		{	return ConcreteTable.this.tableName;
		}

		public boolean advance()
		{	if( rowIterator.hasNext() )
			{	row = (Object[]) rowIterator.next();
				return true;
			}
			return false;
		}

		public int columnCount()
		{	return columnNames.length;
		}

		public String columnName(int index)
		{	return columnNames[index];
		}

		public Object column( String columnName )
		{	return row[ indexOf(columnName) ];
		}

		public Iterator columns()
		{	return new ArrayIterator( row );
		}

		public boolean isTraversing( Table t )
		{	return t == ConcreteTable.this;
		}

		// This method is for use by the outer class only, and is not part
		// of the Cursor interface.
		private Object[] cloneRow()
		{	return (Object[])( row.clone() );
		}

		public Object update( String columnName, Object newValue )
		{	
			int index = indexOf(columnName);

			// The following test is required for undo to work correctly.
			if( row[index] == newValue )
				throw new IllegalArgumentException(
									"May not replace object with itself");

			Object oldValue = row[index];
			row[index]		= newValue;
			isDirty			= true;

			registerUpdate( row, index, oldValue );
			return oldValue;
		}


		public void delete()
		{	Object[] oldRow = row;
			rowIterator.remove();
			isDirty = true;

			registerDelete( oldRow );
		}
	}
	//@cursor-end
	//----------------------------------------------------------------------
	// Undo subsystem.
	//
	private interface Undo
	{	void execute();
	}
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	private class UndoInsert implements Undo
	{	private final Object[] insertedRow;
		public UndoInsert( Object[] insertedRow )
		{	this.insertedRow = insertedRow;
		}
		public void execute()
		{	rowSet.remove( insertedRow );
		}
	}
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	private class UndoDelete implements Undo
	{	private final Object[] deletedRow;
		public UndoDelete( Object[] deletedRow )
		{	this.deletedRow = deletedRow;
		}
		public void execute()
		{	rowSet.add( deletedRow );
		}
	}
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	private class UndoUpdate implements Undo
	{
		private	Object[] row;
		private	int cell;
		private	Object oldContents;

		public UndoUpdate( Object[] row, int cell, Object oldContents )
		{	this.row 		 = row;
			this.cell		 = cell;
			this.oldContents = oldContents;
		}

		public void execute()
		{	row[ cell ] = oldContents;
		}
	}
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	public void begin()
	{	transactionStack.addLast( new LinkedList() );
	}
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	private void register( Undo op )
	{	((LinkedList) transactionStack.getLast()).addLast( op );
	}
	private void registerUpdate(Object[] row, int cell, Object oldContents)
	{	if( !transactionStack.isEmpty() )
			register( new UndoUpdate(row, cell, oldContents) );
	}
	private void registerDelete( Object[] oldRow )
	{	if( !transactionStack.isEmpty() )
			register( new UndoDelete(oldRow) );
	}
	private void registerInsert( Object[] newRow )
	{	if( !transactionStack.isEmpty() )
			register( new UndoInsert(newRow) );
	}
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	public void commit( boolean all ) throws IllegalStateException
	{	if( transactionStack.isEmpty() )
			throw new IllegalStateException("No BEGIN for COMMIT");
		do
		{	LinkedList currentLevel = 
							(LinkedList) transactionStack.removeLast();

			if( !transactionStack.isEmpty() )
				((LinkedList)transactionStack.getLast())
												.addAll(currentLevel);

		} while( all && !transactionStack.isEmpty() );
	}
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	public void rollback( boolean all ) throws IllegalStateException
	{	if( transactionStack.isEmpty() )
			throw new IllegalStateException("No BEGIN for ROLLBACK");
		do
		{	LinkedList currentLevel =
							(LinkedList) transactionStack.removeLast();

			while( !currentLevel.isEmpty() )
				((Undo) currentLevel.removeLast()).execute();

		} while( all && !transactionStack.isEmpty() );
	}
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	//@undo-end
	//-----------------------------------------------------------------
	public int  update( Selector where )
	{	
		Results  currentRow	= (Results)rows();
		Cursor[] envelope 	= new Cursor[]{ currentRow };
		int		 updated	= 0;

		while( currentRow.advance() )
		{	if( where.approve(envelope) )
			{	where.modify(currentRow);
				++updated;
			}
		}

		return updated;
	}
	//----------------------------------------------------------------------
	public int  delete( Selector where )
	{	int deleted = 0;

		Results	 currentRow	= (Results) rows();
		Cursor[] envelope	= new Cursor[]{ currentRow };

		while( currentRow.advance() )
		{	if( where.approve( envelope) )
			{	currentRow.delete();
				++deleted;
			}
		}
		return deleted;
	}
	//@select-start
	//----------------------------------------------------------------------
	public Table select( Selector where )
	{	Table resultTable = new ConcreteTable( null,
										(String[]) columnNames.clone() );

		Results		 	currentRow	= (Results) rows();
		Cursor[] envelope	= new Cursor[]{ currentRow };

		while( currentRow.advance() )
		{	if( where.approve(envelope) )
				resultTable.insert( (Object[]) currentRow.cloneRow() );
		}
		return new UnmodifiableTable(resultTable);
	}
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	public Table select(Selector where, String[] requestedColumns )
	{	if( requestedColumns  == null )
			return select( where );

		Table resultTable = new ConcreteTable( null,
									(String[]) requestedColumns.clone() );

		Results		 	currentRow	= (Results) rows();
		Cursor[] envelope	= new Cursor[]{ currentRow };

		while( currentRow.advance() )
		{	if( where.approve(envelope) )
			{	Object[] newRow = new Object[ requestedColumns.length ];
				for( int column=0; column < requestedColumns.length;
																++column )
				{	newRow[column]=
							currentRow.column(requestedColumns[column]);
				}
				resultTable.insert( newRow );
			}
		}
		return new UnmodifiableTable(resultTable);
	}

	/**
	 * This version of select does a join
	 */
	public Table select( Selector where, String[]	requestedColumns, //{=ConcreteTable.select.default}
										 Table[]	otherTables )
	{
		// If we're not doing a join, use the more efficient version
		// of select().

		if( otherTables == null || otherTables.length == 0)
			return select( where, requestedColumns );

		// Make the current table not be a special case by effectively
		// prefixing it to the otherTables array.

		Table[] allTables = new Table[ otherTables.length + 1 ];
		allTables[0] = this;
		System.arraycopy(otherTables, 0, allTables, 1, otherTables.length );

		// Create places to hold the result of the join and to hold
		// iterators for each table involved in the join.

		Table	 resultTable = new ConcreteTable(null,requestedColumns);
		Cursor[] envelope	 = new Cursor[allTables.length];

		// Recursively compute the Cartesian product, adding to the
		// resultTable all rows that the Selector approves

		selectFromCartesianProduct( 0, where, requestedColumns,
										allTables, envelope, resultTable );

		return new UnmodifiableTable(resultTable);
	}

	/**
	 * Think of the Cartesian product as a kind of tree. That is
	 * given one table with rows A and B, and another table with rows
	 * C and D, you can look at the product like this:
	 *
	 * 			 root
	 * 		______|______
	 * 		|			|
	 * 		A  			B
	 *  ____|____   ____|____
	 *  |		|	|		|
	 * 	C		D   C		D
	 *
	 * The tree is as deep as the number of tables we're joining.
	 * Every possible path from the root to a leaf represents one row
	 * in the Cartesian product. The current method effectively traverses
	 * this tree recursively without building an actual tree. It
	 * assembles an array of iterators (one for each table) positioned
	 * at the current place in the set of rows as it recurses to a leaf,
	 * and then asks the selector whether or not to approve that row.
	 * It then goes up a notch, advances the correct iterator, and
	 * recurses back down.
	 */
	private static void selectFromCartesianProduct(
										int 			level,
										Selector 		where,
										String[] 		requestedColumns,
										Table[] 		allTables,
										Cursor[] allIterators,
										Table 			resultTable )
	{
		allIterators[level] = allTables[level].rows();

		while( allIterators[level].advance() )
		{	// If we haven't reached the tips of the branches yet,
			// go down one more level.

			if( level < allIterators.length - 1 )
				selectFromCartesianProduct(level+1, where,
									requestedColumns,
									allTables, allIterators, resultTable);

			// If we are at the leaf level, then get approval for
			// the fully-assembled row, and add the row to the table
			// if it's approved.

			if( level == allIterators.length - 1 )
			{	if( where.approve(allIterators) )
					insertApprovedRows( resultTable,
									requestedColumns, allIterators );
			}
		}
	}

	/**
	 * Insert an approved row into the result table:
	 * <PRE>
	 * 		for( every requested column )
	 * 			for( every table in the join )
	 * 				if the requested column is in the current table
	 * 					add the associated value to the result table
	 *
	 *	</PRE>
	 *	Only one column with a given name is added, even if that column
	 *	appears in multiple tables. Columns in tables at the beginning
	 *	of the list take precedence over identically named columns that
	 *	occur later in the list.
	 */
	private static void insertApprovedRows( Table	 resultTable,
											String[] requestedColumns,
											Cursor[] allTables )
	{

		Object[] resultRow = new Object[ requestedColumns.length ];

		for( int i = 0; i < requestedColumns.length; ++i )
		{	for( int table = 0; table < allTables.length; ++table )
			{	try
				{	resultRow[i] = 
						allTables[table].column(requestedColumns[i]);
					break;	// if the assignment worked, do the next column
				}
				catch(Exception e)
				{	// otherwise, try the next table
				}
			}
		}
		resultTable.insert( /*requestedColumns,*/ resultRow );
	}
	
	/**
	 * A collection variant on the array version. Just converts the collection
	 * to an array and then chains to the other version
	 * ({@linkplain #select(Selector,String[],Table[]) see}).
	 * @param requestedColumns the value returned from the {@link #toString}
	 * 			method of the elements of this collection are used as the
	 * 			column names.
	 * @param other Collection of tables to join to the current one,
	 * 			<code>null</code>if none.
	 * @throws ClassCastException if any elements of the <code>other</code>
	 * 			collection do not implement the {@link Table} interface.
	 */
	public Table select(Selector where, Collection requestedColumns,
															Collection other)
	{
		String[] columnNames = null;
		Table[]	 otherTables = null;

		if( requestedColumns != null )  // SELECT *
		{
			// Can't cast an Object[] to a String[], so make a copy to ensure
			// type safety.

			columnNames		= new String[ requestedColumns.size() ];
			int 	 i		= 0;
			Iterator column = requestedColumns.iterator();

			while( column.hasNext() )
				columnNames[i++] = column.next().toString();
		}

		if( other != null )
			otherTables = (Table[]) other.toArray( new Table[other.size()] );

		return select( where, columnNames, otherTables );
	}
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	public Table select(Selector where, Collection requestedColumns)
	{	return select( where, requestedColumns, null );
	}
	//@select-end
	//----------------------------------------------------------------------
	// Housekeeping stuff
	//
	public  String	name()				{ return tableName; 		 }
	public  void	rename(String s)	{ tableName = s;			 }
	public  boolean	isDirty()			{ return isDirty;			 }
	private int		width()				{ return columnNames.length; }
	//----------------------------------------------------------------------
	public Object clone() throws CloneNotSupportedException
	{	ConcreteTable copy	= (ConcreteTable)  super.clone();
		copy.rowSet			= (LinkedList)	   rowSet.clone();
		copy.columnNames 	= (String[]) columnNames.clone();
		copy.tableName		= tableName;
		return copy;
	}
	//----------------------------------------------------------------------
	public String toString()
	{	StringBuffer out = new StringBuffer();

		out.append( tableName == null ? "<anonymous>" : tableName );
		out.append( "\n" );

		for( int i = 0; i < columnNames.length; ++i )
			out.append(columnNames[i] + "\t");
		out.append("\n----------------------------------------\n");

		for( Cursor i = rows(); i.advance(); )
		{	Iterator columns = i.columns();
			while( columns.hasNext() )
			{	Object next = columns.next();
				if( next == null )
					out.append( "null\t" );
				else
					out.append( next.toString() + "\t" );
			}
			out.append('\n');
		}
		return out.toString();
	}

	//----------------------------------------------------------------------
	public final static class Test
	{
		public static void main(String[] args)
		{	new Test().test();
		}

		Table people  = TableFactory.create(
			"people",  new String[]{"last", "first", "addrId" } );

		Table address = TableFactory.create(
			"address", new String[]{"addrId","street","city","state","zip"});

		public void report( Throwable t, String message )
		{	System.out.println( message + " FAILED with exception toss" );
			t.printStackTrace();
			System.exit(1);
		}

		public void test()
		{	try{ testInsert(); }catch(Throwable t){ report(t,"Insert"); }
			try{ testUpdate(); }catch(Throwable t){ report(t,"Update"); }
			try{ testDelete(); }catch(Throwable t){ report(t,"Delete"); }
			try{ testSelect(); }catch(Throwable t){ report(t,"Select"); }
			try{ testStore();  }catch(Throwable t){ report(t,"Store/Load");}
			try{ testJoin();   }catch(Throwable t){ report(t,"Join"  ); }
			try{ testUndo();   }catch(Throwable t){ report(t,"Undo"  ); }
		}

		public void testInsert()
		{	people.insert(new Object[]{"Holub",	    "Allen","1" 		});
			people.insert(new Object[]{"Flintstone","Wilma","2"		 	});
			people.insert(new String[]{"addrId",	"first","last" 	 	},
						  new Object[]{"2",		    "Fred", "Flintstone"});

			address.insert( new Object[]{"1","123 MyStreet", 
												"Berkeley","CA","99999" } );

			List l = new ArrayList();
			l.add("2");
			l.add("123 Quarry Ln.");
			l.add("Bedrock ");
			l.add("XX");
			l.add("12345");
			assert( address.insert(l) == 1 );

			l.clear();
			l.add("3");
			l.add("Bogus");
			l.add("Bad");
			l.add("XX");
			l.add("12345");

			List c = new ArrayList();
			c.add("addrId");
			c.add("street");
			c.add("city");
			c.add("state");
			c.add("zip");
			assert( address.insert( c, l ) == 1);

			System.out.println( people.toString() );
			System.out.println( address.toString() );

			try
			{	people.insert( new Object[]{ "x" } );
				throw new AssertionError(
							"insert wrong number of fields test failed");
			}
			catch(Throwable t){ /* Failed correctly, do nothing */ }

			try
			{	people.insert( new String[]{ "?" }, new Object[]{ "y" });
				throw new AssertionError(
								"insert-nonexistent-field test failed");
			}
			catch(Exception t){ /* Failed correctly, do nothing */ }
		}

		public void testUpdate()
		{	System.out.println("update set state='YY' where state='XX'" );
			int updated = address.update
			(	new Selector()
				{	public boolean approve( Cursor[] tables )
					{	return tables[0].column("state").equals("XX");
					}
					public void modify( Cursor current )
					{	current.update("state", "YY");
					}
				}
			);
			print( address );
			System.out.println( updated + " rows affected\n" );
		}

		public void testDelete()
		{
			System.out.println("delete where street='Bogus'" );
			int deleted =
			address.delete
			(	new Selector.Adapter()
				{	public boolean approve( Cursor[] tables )
					{	return tables[0].column("street").equals("Bogus");
					}
				}
			);
			print( address );
			System.out.println( deleted + " rows affected\n" );
		}

		public void testSelect()
		{	Selector flintstoneSelector =
				new Selector.Adapter()
				{	public boolean approve( Cursor[] tables )
					{ return tables[0].column("last").equals("Flintstone");
					}
				};

			// SELECT first, last FROM people WHERE last = "Flintstone"
			// The collection version chains to the string version, so the
			// following call tests both versions

			List columns = new ArrayList();
			columns.add("first");
			columns.add("last");

			Table result = people.select(flintstoneSelector, columns);
			print( result );

			// SELECT * FROM people WHERE last = "Flintstone"
			result = people.select( flintstoneSelector );
			print( result );

			// Check that the result is indeed unmodifiable

			try
			{	result.insert( new Object[]{ "x", "y", "z" } );
				throw new AssertionError(
								"Insert to Immutable Table test failed");
			}
			catch( Exception e ){ /*it failed correctly*/ }

			try
			{	result.update( flintstoneSelector );
				throw new AssertionError(
								"Update of Immutable Table test failed");
			}
			catch( Exception e ){ /*it failed correctly*/ }

			try
			{	result.delete( flintstoneSelector );
				throw new AssertionError(
								"Delete of Immutable Table test failed");
			}
			catch( Exception e ){ /*it failed correctly*/ }
		}

		public void testStore() throws IOException, ClassNotFoundException
		{	// Flush the table to disk, then reread it.
			// Subsequent tests that use the "people" table will
			// fail if this operation fails.

			Writer out = new FileWriter( "people" );
			people.export( new CSVExporter(out) );
			out.close();

			Reader in = new FileReader( "people" );
			people = new ConcreteTable( new CSVImporter(in) );
			in.close();
		}

		public void testJoin()
		{
			// First test a two-way join

			System.out.println("\nSELECT first,last,street,city,state,zip"
								+" FROM people, address"
								+" WHERE people.addrId = address.addrId");

		 	// Collection version chains to String[] version,
			// so this code tests both:
			List columns = new ArrayList();
			columns.add("first");
			columns.add("last");
			columns.add("street");
			columns.add("city");
			columns.add("state");
			columns.add("zip");

			List tables = new ArrayList();
			tables.add( address );

			Table result=	// WHERE people.addrID = address.addrID
				people.select
				(	new Selector.Adapter()
					{	public boolean approve( Cursor[] tables )
						{	return 		 tables[0].column("addrId")
								.equals( tables[1].column("addrId") );
						}
					},
					columns,
					tables
				);

			print( result );
		 	System.out.println("");

			// Now test a three-way join
			//
			System.out.println(
					"\nSELECT first,last,street,city,state,zip,text"
					+" FROM people, address, third"
					+" WHERE (people.addrId = address.addrId)"
					+" AND (people.addrId = third.addrId)");

			Table third = TableFactory.create(
								"third", new String[]{"addrId","text"} );
			third.insert ( new Object[]{ "1", "addrId=1" } );
			third.insert ( new Object[]{ "2", "addrId=2" } );

			result=
			people.select
		  	( new Selector.Adapter()
			  {	public boolean approve( Cursor[] tables )
				{ return
				  (tables[0].column("addrId")
				   				.equals(tables[1].column("addrId"))
				   &&
				   tables[0].column("addrId")
				   				.equals(tables[2].column("addrId"))
				  );
				}
			  },

			  new String[]{"last", "first", "state", "text"},
			  new Table[]{ address, third }
			);

			System.out.println( result.toString() + "\n" );
		}

		public void testUndo()
		{
			// Verify that commit works properly
			people.begin();
			System.out.println(
							"begin/insert into people (Solo, Han, 5)");

			people.insert( new Object[]{ "Solo", "Han", "5" } );
			System.out.println( people.toString() );

			people.begin();
			System.out.println(
					"begin/insert into people (Lea, Princess, 6)");

			people.insert( new Object[]{ "Lea", "Princess", "6" } );
			System.out.println( people.toString() );

			System.out.println(  "commit(THIS_LEVEL)\n"
								+"rollback(Table.THIS_LEVEL)\n");
			people.commit		(Table.THIS_LEVEL);
			people.rollback		(Table.THIS_LEVEL);
			System.out.println	( people.toString() );

			// Now test that nested transactions work correctly.

			System.out.println( people.toString() );

			System.out.println("begin/insert into people (Vader,Darth, 4)");
			people.begin();
			people.insert( new Object[]{ "Vader","Darth", "4" } );
			System.out.println( people.toString() );

			System.out.println(
				"begin/update people set last=Skywalker where last=Vader");

			people.begin();
			people.update
			(	new Selector()
				{	public boolean approve( Cursor[] tables )
					{	return tables[0].column("last").equals("Vader");
					}
					public void modify(Cursor current)
					{	current.update( "last", "Skywalker" );
					}
				}
			);
			System.out.println( people.toString() );

			System.out.println("delete from people where last=Skywalker");
			people.delete
			(	new Selector.Adapter()
				{	public boolean approve( Cursor[] tables )
					{ return tables[0].column("last").equals("Skywalker");
					}
				}
			);
			System.out.println( people.toString() );

			System.out.println(
						"rollback(Table.THIS_LEVEL) the delete and update");
			people.rollback(Table.THIS_LEVEL);
			System.out.println( people.toString() );

			System.out.println("rollback(Table.THIS_LEVEL) insert");
			people.rollback(Table.THIS_LEVEL);
			System.out.println( people.toString() );
		}

		public void print( Table t )
		{	// tests the table iterator
			Cursor current = t.rows();
			while( current.advance() )
			{	for(Iterator columns = current.columns();columns.hasNext();)
					System.out.print( (String) columns.next() + " " );
				System.out.println("");
			}
		}
	}
}
