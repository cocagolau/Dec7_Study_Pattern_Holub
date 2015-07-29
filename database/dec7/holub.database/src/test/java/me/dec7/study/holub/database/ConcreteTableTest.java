package me.dec7.study.holub.database;

import static org.junit.Assert.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.dec7.study.holub.database.Selector.Adapter;

import org.junit.Before;
import org.junit.Test;

public class ConcreteTableTest {
	
	private Table people, address;
	
	@Before
	public void before() {
		people = TableFactory.create("people", new String[] { "last", "first", "addrId" });
		address = TableFactory.create("address", new String[] { "addrId", "street", "city", "state", "zip" });
	}
	
	public void report(Throwable t, String message) {
		System.out.println(message + " FAILED with exception toss");
		t.printStackTrace();
	}

	
	public void test() {
		try { testInsert(); } catch(Throwable t) { report(t, "Insert"); }
		try { testUpdate(); } catch(Throwable t) { report(t, "Update"); }
		try { testDelete(); } catch(Throwable t) { report(t, "Delete"); }
		try { testSelect(); } catch(Throwable t) { report(t, "Select"); }
		try { testStore(); } catch(Throwable t) { report(t, "Store/Load"); }
		try { testJoin(); } catch(Throwable t) { report(t, "Join"); }
		try { testUndo(); } catch(Throwable t) { report(t, "Undo"); }
	}
	
	public void testInsert() {
		people.insert(new Object[] { "Holub", "Allen", "1" });
		people.insert(new Object[] { "Flintstone", "Wilma", "2" });
		people.insert(new String[] { "addrId", "first", "last" }, new Object[] { "2", "Fred", "Flintstone" });
		
		address.insert(new Object[] { "1", "123 MyStreet", "Berkeley", "CA", "99999" });
		
		List l = new ArrayList();
		l.add("2");
		l.add("123 Quarry Ln.");
		l.add("Bedrock ");
		l.add("XX");
		l.add("12345");
		assert(address.insert(l) == 1);
		
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
		assert(address.insert(c, l) == 1);
		
		System.out.println(people.toString());
		System.out.println(address.toString());
		
		try {
			people.insert(new Object[] { "X" });
			
			throw new AssertionError("insert wrong number of fields test failed");
		} catch(Throwable t) { }
		
		try {
			people.insert(new String[] { "?" }, new Object[] { "y" });
			
			throw new AssertionError("insert-nonexistent-field test failed");
		} catch (Exception t) { }
		
	}
	
	public void testUpdate() {
		System.out.println("update set state='YY' where state='XX'");
		int updated = address.update(new Selector() {

			@Override
			public boolean approve(Cursor[] tables) {
				
				return tables[0].column("state").equals("XX");
			}

			@Override
			public void modify(Cursor current) {
				current.update("state", "YY");
			}
			
		});
		
		print(address);
		System.out.println(updated + " rows affected\n");
	}
	
	public void testDelete() {
		System.out.println("delete where street = 'Bogus'");
		int deleted = address.delete(new Selector.Adapter() {

			@Override
			public boolean approve(Cursor[] tables) {
				
				return tables[0].column("street").equals("Bogus");
			}
			
		});
		
		print(address);
		System.out.println(deleted + " rows affected\n");
	}
	
	public void testSelect() {
		Selector flintstoneSelector = new Selector.Adapter() {

			@Override
			public boolean approve(Cursor[] tables) {
				
				return tables[0].column("last").equals("Flintstone");
			}
			
		};
		
		List columns = new ArrayList();
		columns.add("first");
		columns.add("last");
		
		Table result = people.select(flintstoneSelector, columns);
		print(result);
		
		result = people.select(flintstoneSelector);
		print(result);
		
		try {
			result.insert(new Object[] { "x", "y", "z" });
			
			throw new AssertionError("Insert to Immutable Table test failed");
		} catch (Exception e) { }
		
		try {
			result.update(flintstoneSelector);
			
			throw new AssertionError("Update of Immutable Table test failed");
		} catch (Exception e) { }
		
		try {
			result.delete(flintstoneSelector);
			
			throw new AssertionError("Delete fo Immutable Table test failed");
		} catch (Exception e) { }
	}
	
	public void testStore() throws IOException, ClassNotFoundException {
		Writer out = new FileWriter("people");
		people.export(new CSVExporter(out));
		out.close();
		
		Reader in = new FileReader("people");
		people = new ConcreteTable(new CSVImporter(in));
		in.close();
	}
	
	public void testJoin() {
		System.out.println("\nSELECT first,last,street,city,state,zip FROM people, address WHERE prople.addrId = address.addrId");
		
		List columns = new ArrayList();
		columns.add("first");
		columns.add("last");
		columns.add("street");
		columns.add("city");
		columns.add("state");
		columns.add("zip");
		
		List tables = new ArrayList();
		tables.add(address);
		
		// WHERE people.addrId = address.addrId
		Table result = people.select(new Selector.Adapter() {

			@Override
			public boolean approve(Cursor[] tables) {
				
				return tables[0].column("addrId").equals(tables[1].column("addrId"));
			}
			
		}, columns, tables);
		
		print(result);
		System.out.println("");
		
		System.out.println("\nSELECT first,last,street,city,state,zip,text FROM people,address,third WHERE (people.addrId = address.addrId) AND (people.addrId = third.addrId)");
		Table third = TableFactory.create("thrid", new String[] { "addrId", "text" });
		third.insert(new Object[] { "1", "addrId=1" });
		third.insert(new Object[] { "2", "addrId=2" });
		
		result = people.select(new Selector.Adapter() {

			@Override
			public boolean approve(Cursor[] tables) {
				
				return tables[0].column("addrId").equals(tables[1].column("addrId"))
						&& tables[0].column("addrId").equals(tables[2].column("addrId"));
			}
			
		},
		new String[] { "last", "first", "state", "text" },
		new Table[] { address, third });
		
		System.out.println(result.toString() + "\n");
	}
	
	public void testUndo() {
		people.begin();
		System.out.println("begin/insert into people (Solo, Han, 5)");
		people.insert(new Object[] { "Solo", "Han", "5" });
		System.out.println(people.toString());
		
		people.begin();
		System.out.println("begin/insert into people (Lea, Princess, 6)");
		people.insert(new Object[] { "Lea", "Princess", "6" });
		System.out.println(people.toString());
		
		System.out.println("commit(THIS_LEVEL)\nrollback(Table.THIS_LEVEL)\n");
		people.commit(Table.THIS_LEVEL);
		people.rollback(Table.THIS_LEVEL);
		System.out.println(people.toString());
		
		
		System.out.println(people.toString());
		System.out.println("begin/insert into peopel (Vader, Darth, 4)");
		people.begin();
		people.insert(new Object[] { "Varder", "Darth", "4" });
		System.out.println(people.toString());

		System.out.println("begin/update people set last=Skywalker where last=Vader");
		people.update(new Selector() {

			@Override
			public boolean approve(Cursor[] tables) {
				
				return tables[0].column("last").equals("Vader");
			}

			@Override
			public void modify(Cursor current) {
				current.update("last", "Skywalker");
			}
			
		});
		System.out.println(people.toString());
		
		
		System.out.println("delete from people where last=Skywalker");
		people.delete(new Selector.Adapter() {

			@Override
			public boolean approve(Cursor[] tables) {
				
				return tables[0].column("last").equals("Skywalker");
			}
			
		});
		System.out.println(people.toString());
		
		System.out.println("rollback(Table.THIS_LEVEL) the delete and udpate");
		people.rollback(Table.THIS_LEVEL);
		System.out.println(people.toString());
		
		System.out.println("rollback(Table.THIS_LEVEL) insert");
		people.rollback(Table.THIS_LEVEL);
		System.out.println(people.toString());
	}
	
	public void print(Table t) {
		Cursor current = t.rows();
		while (current.advance()) {
			for (Iterator columns = current.columns(); columns.hasNext(); ) {
				System.out.print((String) columns.next() + " ");
			}
			System.out.println("");
		}
	}

}
