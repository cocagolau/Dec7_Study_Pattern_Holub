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

/** This decorator of the Table class just wraps another table,
 *  but restricts access to methods that don't modify the table.
 *  The following methods toss an
 *  {@link UnsupportedOperationException} when called:
 *  <PRE>
 *	public void  insert( String[] columnNames, Object[] values )
 *	public void  insert( Object[] values )
 *	public void  update( Selector where )
 *	public void  delete( Selector where )
 *	public void  store ()
 *  </PRE>
 *  Other methods delegate to the wrapped Table. All methods of
 *  the {@link Table} that are declared to return a
 *  <code>Table</code> actually return an
 *  <code>UnmodifiableTable</code>.
 *  <p>
 *  Refer to the {@link Table} interface for method documentation.
 *
 * @include /etc/license.txt
 */

public class UnmodifiableTable implements Table
{	private Table wrapped;

	public UnmodifiableTable( Table wrapped )
	{	this.wrapped = wrapped;
	}

	/** Return an UnmodifieableTable that wraps a clone of the
	 *  currently wrapped table. (A deep copy is used.)
	 */
	public Object clone() throws CloneNotSupportedException
	{	UnmodifiableTable copy = (UnmodifiableTable) super.clone();
		copy.wrapped = (Table)( wrapped.clone() );
		return copy;
	}

	public int  insert(String[] c, Object[] v 	 ){	illegal(); return 0;}
	public int  insert(Object[] v			   	 ){	illegal(); return 0;}
	public int  insert(Collection c,Collection v ){	illegal(); return 0;}
	public int  insert(Collection v			 	 ){	illegal(); return 0;}
	public int  update(Selector w				 ){	illegal(); return 0;}
	public int  delete( Selector w				 ){	illegal(); return 0;}

	public void begin	 (			 ){ illegal(); }
	public void commit	 (boolean all){ illegal(); }
	public void rollback (boolean all){ illegal(); }

	private final void illegal()
	{	throw new UnsupportedOperationException();
	}

	public Table select(Selector w,String[] r,Table[] o)
	{	return wrapped.select( w, r, o );
	}
	public Table select(Selector where, String[] requestedColumns)
	{	return wrapped.select(where, requestedColumns );
	}
	public Table select(Selector where)
	{	return wrapped.select(where);
	}
	public Table select(Selector w,Collection r,Collection o)
	{	return wrapped.select( w, r, o );
	}
	public Table select(Selector w, Collection r)
	{	return wrapped.select(w, r);
	}
	public Cursor rows()
	{	return wrapped.rows();
	}
	public void  export(Table.Exporter exporter) throws IOException
	{	wrapped.export(exporter);
	}

	public String	toString() 		{ return wrapped.toString();	}
	public String	name()			{ return wrapped.name(); 		}
	public void		rename(String s){ 		 wrapped.rename(s);		}
	public boolean	isDirty()		{ return wrapped.isDirty();		}

	/** Extract the wrapped table. The existence of this method is
	 *  problematic, since it allows someone to defeat the unmodifiability
	 *  of the table. On the other hand, the wrapped table came in from
	 *  outside, so external access is possible through the reference
	 *  that was passed to the constructor. Use the method with care.
	 */
	public Table extract(){ return wrapped;	}
}
