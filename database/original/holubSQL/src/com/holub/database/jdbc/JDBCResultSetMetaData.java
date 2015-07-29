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
package com.holub.database.jdbc;

import java.sql.*;
import java.util.*;

import com.holub.database.*;
import com.holub.database.jdbc.adapters.*;

/** A limited version of the result-set metadata class. All methods
 *  not shown throw a {@link SQLException} if called.
 *
 *  @include /etc/license.txt
 */
public class JDBCResultSetMetaData extends ResultSetMetaDataAdapter
{
	private final Cursor cursor;

	public JDBCResultSetMetaData(Cursor cursor)
	{	this.cursor = cursor;
	}

	public int getColumnType(int column) throws java.sql.SQLException
	{	return java.sql.Types.VARCHAR;
	}

	public String getColumnTypeName(int column)throws java.sql.SQLException
	{	return "VARCHAR";
	}

	public String getColumnName(int index) throws java.sql.SQLException
	{	// The Cursor is zero indexed, which makes sense from a Java
		// perspective. JDBC is 1 indexed, however; thus the -1.

		return cursor.columnName( index-1 );
	}

	public int getColumnCount() throws java.sql.SQLException
	{	return cursor.columnCount();
	}

	public String getTableName() throws java.sql.SQLException
	{	return cursor.tableName();
	}
}
