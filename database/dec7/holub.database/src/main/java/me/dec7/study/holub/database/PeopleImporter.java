package me.dec7.study.holub.database;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import me.dec7.study.holub.tools.ArrayIterator;

public class PeopleImporter implements Table.Importer {
	
	private LinkedList rows = new LinkedList();

	@Override
	public void startTable() throws IOException {
		getRowDataFromUser();
	}

	@Override
	public String loadTableName() throws IOException {
		
		return "people";
	}

	@Override
	public int loadWidth() throws IOException {
		
		return 3;
	}

	@Override
	public Iterator loadColumnNames() throws IOException {
		
		return new ArrayIterator(new String[]{ "first", "last", "addrID" });
	}

	@Override
	public Iterator loadRow() throws IOException {
		
		try {
			String[] row = (String[]) (rows.removeFirst());
			
			return new ArrayIterator(row);
		} catch (NoSuchElementException e) {
			
			return null;
		}
		
	}

	@Override
	public void endTable() throws IOException { }
	
	private void getRowDataFromUser() {
		
		final JTextField first = new JTextField(" ");
		final JTextField last = new JTextField(" ");
		final JDialog ui = new JDialog();
		
		ui.setModal(true);
		ui.getContentPane().setLayout(new GridLayout(3, 1));
		
		JPanel panel = new JPanel();
		
		panel.setLayout(new FlowLayout());
		panel.add(new JLabel("First Name: "));
		panel.add(first);
		ui.getContentPane().add(panel);
		
		panel.setLayout(new FlowLayout());
		panel.add(new JLabel("Last Name: "));
		panel.add(last);
		ui.getContentPane().add(panel);
		
		JButton done = new JButton("Done");
		JButton next = new JButton("Next");
		panel = new JPanel();
		
		done.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				rows.add(new String[] { first.getText().trim(), last.getText().trim() });
				ui.dispose();
			}
			
		});
		
		done.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				rows.add(new String[] { first.getText().trim(), last.getText().trim() });
				first.setText("");
				last.setText("");
			}
			
		});
		
		panel.add(next);
		panel.add(done);
		ui.getContentPane().add(panel);
		
		ui.pack();
		ui.show();
	}
	
	public static class Test {
		
		public static void main(String[] args) throws IOException {
			Table t = TableFactory.create(new PeopleImporter());
			System.out.println(t.toString());
			System.exit(0);
		}
		
	}

}
