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

import java.util.Iterator;
import java.util.NoSuchElementException;

/** The Cursor provides you with a way of examining a
 *  {@link Table}, both the ones that you create and
 *  the ones that are created
 *  as a result of a select or join operation. This is
 *  an "updateable" cursor, so you can modify columns or
 *  delete rows via the cursor without problems. (Updates
 *  and deletes done through the cursor <em>are</em> handled
 *  properly with respect to the transactioning system, so
 *  they can be committed or rolled back.)
 *  <p>The class is not thread safe, however. It's a serious
 *  error for a thread to be modifying a table, either via a
 *  Cursor or directly, while another thread is examining
 *  or modifying the same table.
 *
 * <p>
 * <b>Modifications Since Publication of Holub on Patterns:</b>
 * <table border="1" cellspacing="0" cellpadding="3">
 * <tr><td valign="top">9/24/02</td>
 * 		<td>
 * 		Added a few methods to make it possible to get
 * 		column names for the JDBC 
 * 		{@link java.sql.ResultSetMetaData} class.
 * 		</td>
 * </tr>
 * </table>
 *
 * @include /etc/license.txt
 */

public interface Cursor
{
	/** Metadata method required by JDBC wrapper--Return the name
	 *  of the table across which we're iterating. I am deliberately
	 *  not allow access to the Table itself, because this would
	 *  allow uncontrolled modification of the table via the
	 *  iterator.
	 *  @return the name of the table or null if we're iterating
	 *  		across a nameless table like the one created by
	 *  		a select operation.
	 */
	String tableName();

	/** Advances to the next row, or if this iterator has never
	 *  been used, advances to the first row. That is, the Cursor
	 *  is initially positioned above the first row and the
	 *  first call to <code>advance()</code> moves it to the
	 *  first row.
	 *  @throws NoSuchElementException if this call would advance
	 *  		past the last row.
	 *  @return true if the iterator is positioned at a valid
	 *  		row after the advance.
	 */
	boolean advance() throws NoSuchElementException;

	/** Return the number of columns in the table that we're
	 *  traversing.
	 */
	int columnCount();

	/** Return the name of the column at the indicated index.
	 *  Note that this is a zero-referenced index---the
	 *  leftmost column is columnName(0); The JDBC
	 *  ResultSet class is 1 indexed, so don't get confused.
	 */
	String columnName(int index);

	/** Return the contents of the requested column of the current
	 *  row. You should
	 *  treat the cells accessed through this method as read only
	 *  if you ever expect to use the table in a thread-safe
	 *  environment. Modify the table using {@link Table#update}.
	 *
	 *  @throws IndexOutOfBoundsException --- the requested column
	 *  	doesn't exist.
	 */

	Object column( String columnName );

	/** Return a java.util.Iterator across all the columns in
	 *  the current row.
	 */
	Iterator columns();

	/** Return true if the iterator is traversing the
	 *  indicated table.
	 */
	boolean isTraversing( Table t );

	/** Replace the value of the indicated column of the current
	 *  row with the indicated new value.
	 *
	 *  @throws IllegalArgumentException if the newValue is
	 *  		the same as the object that's being updated.
	 *
	 *  @return the former contents of the now-modified cell.
	 */
	Object update( String columnName, Object newValue );

	/** Delete the row at the current cursor position.
	 */
	void delete();
}
