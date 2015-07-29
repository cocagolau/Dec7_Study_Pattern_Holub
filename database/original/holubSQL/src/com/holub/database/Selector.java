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

/** A Selector is a Strategy object that is used by
 * {@link Table#select} to determine
 * whether a particular row should be included in the result.
 * The passed Cursor is positioned at the correct row;
 * and attempts to advance it will fail.
 *
 * @include /etc/license.txt
 */

interface Selector
{
	/** This method is passed rows from the tables being joined
	 *  and returns true if the aggregate row is approved for the
	 *  current operation. In a select, for example, "aproval"
	 *  means that the aggregate row should be included in the
	 *  result-set Table. 
	 *  @param rows An array of iterators, one for the current
	 *  		row in each table to be examined (The array will
	 *  		have only one element unless a you're approving
	 *  		rows in a join.) These iterators are already
	 *  		positioned at the correct row. Attempts to
	 *  		advance the iterator result in an exception
	 *  		toss ({@link java.lang.IllegalStateException}).
	 *  @return true if the aggregate row should has been approved
	 *  		for the current operation.
	 */
	boolean approve( Cursor[] rows );

	/** This method is called only when an update request for a
	 *  row is approved by {@link #approve approve(...)}. It should
	 *  replace the required cell with a new value.
	 *  You must do the replacement using the iterator's
	 *  {@link Cursor#update} method. A typical implementation
	 *  takes this form:
	 *  <PRE>
	 *	public Object modify( Cursor current )
	 *	{  return current.update( "columnName", "new-value" );
	 *	}
	 *  </PRE>
	 *  @param current 	    Iterator positioned at the row to modify
	 */
	void modify( Cursor current );

	/** An implementation of {@link Selector} whose approve method
	 *  approves everything, and whose replace() method throws an
	 *  {@link UnsupportedOperationException} if called. Useful
	 *  for creating selectors on the fly with anonymous inner classes.
	 */
	public static class Adapter implements Selector
	{	public boolean approve( Cursor[] tables )
		{	return true;
		}
		public void modify( Cursor current )
		{	throw new UnsupportedOperationException(
					"Can't use a Selector.Adapter in an update");
		}
	}

	/** An instance of {@link Selector.Adapter),
	 *  pass Selector.ALL to the {@link Table}'s
	 *  {@link Table#select select(...)} or
	 *  {@link Table#delete delete(...)}  methods to select all rows
	 *  of the table. May not be used in an update operation.
	 */
	public static final Selector ALL = new Selector.Adapter();

}
