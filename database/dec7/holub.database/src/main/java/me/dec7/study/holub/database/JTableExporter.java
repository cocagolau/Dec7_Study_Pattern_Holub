package me.dec7.study.holub.database;

import java.io.IOException;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class JTableExporter implements Table.Exporter {
	
	private String[] columnHeads;
	
	private Object[][] contents;
	
	private int rowIndex = 0;

	@Override
	public void startTable() throws IOException {
		rowIndex = 0;
	}

	@Override
	public void storeMetadata(String tableName, int width, int height, Iterator columnNames) throws IOException {
		contents = new Object[height][width];
		columnHeads = new String[width];
		
		int columnIndex = 0;
		while (columnNames.hasNext()) {
			columnHeads[columnIndex++] = columnNames.next().toString();
		}
	}

	@Override
	public void storeRow(Iterator data) throws IOException {
		int columnIndex = 0;
		while (data.hasNext()) {
			contents[rowIndex][columnIndex++] = data.next();
			++rowIndex;
		}
	}

	@Override
	public void endTable() throws IOException { }
	
	
	public JTable getJtable() {
		
		return new JTable(contents, columnHeads);
	}
	
	public static class Test {
		
		public static void main(String[] args) throws IOException {
			Table people = TableFactory.create("people)", new String[]{ "First", "Last" });
			people.insert(new String[]{ "Allen", "Holub" });
			people.insert(new String[]{ "Ichabod", "Crane" });
			people.insert(new String[]{ "Rip", "VanWinkle" });
			people.insert(new String[]{ "Goldie", "Locks" });
			
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			JTableExporter tableBuilder = new JTableExporter();
			people.export(tableBuilder);
			
			frame.getContentPane().add(new JScrollPane(tableBuilder.getJtable()));
			frame.pack();
			frame.show();
		}
		
	}

}
