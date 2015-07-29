package me.dec7.study.holub.database;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class CSVExporter implements Table.Exporter {
	
	private final Writer out;
	
	private int width;
	
	public CSVExporter(Writer out) {
		this.out = out;
	}

	@Override
	public void startTable() throws IOException { }

	@Override
	public void storeMetadata(String tableName, int width, int height, Iterator columnNames) throws IOException {
		this.width = width;
		out.write(tableName == null ? "<anonymous>" : tableName);
		out.write("\n");
		storeRow(columnNames);
	}

	@Override
	public void storeRow(Iterator data) throws IOException {
		int i = width;
		while (data.hasNext()) {
			Object datum = data.next();
			
			if (datum != null) {
				out.write(datum.toString());
				out.write(",\t");
			}
			out.write("\n");
		}
	}

	@Override
	public void endTable() throws IOException { }

}
