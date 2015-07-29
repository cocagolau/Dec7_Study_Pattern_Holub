package me.dec7.study.holub.text;

import org.junit.Test;

public class ScannerTest {
	
	private static TokenSet tokens = new TokenSet();
	
	private static final Token
	COMMA = tokens.create("',"),
	IN = tokens.create("'IN'"),
	INPUT = tokens.create("INPUT"),
	IDENTIFIER = tokens.create("[a-z_][a-z_0-9]*");

	@Test
	public void test() {
		
		assert COMMA instanceof SimpleToken: "Factory Failure 1";
		assert IN instanceof WordToken: "Factory Failure 2";
		assert INPUT instanceof WordToken: "Factory Failure 3";
		assert IDENTIFIER instanceof RegexToken: "Factory Failure 4";
	
	
		Scanner analyzer = new Scanner(tokens, ",aBc In input inputted");
		
		assert analyzer.advance() == COMMA : "COMMA unrecognized";
		assert analyzer.advance() == IDENTIFIER : "ID unrecognized";
		assert analyzer.advance() == IN : "IN unrecognized";
		assert analyzer.advance() == INPUT : "INPUT unrecognized";
		assert analyzer.advance() == IDENTIFIER : "ID unrecognized";
		
		analyzer = new Sacnner(tokens, "Abc IN\nCde");
		analyzer.advance();
		
		
		assert(analyzer.matchAdvance(IDENTIFIER).equals("Abc"));
		assert(analyzer.matchAdvance(IN).equals("in"));
		assert(analyzer.matchAdvance(IDENTIFIER).equals("Cde"));
		
		
		analyzer = new Sacnner(tokens, "xyz\nabc + def");
		analyzer.advance();
		analyzer.advance();
		
		try {
			analyzer.advance();
			assert false : "Error Detection Failure";
		} catch (ParseFailure e) {
			assert e.getErrorReport().equals("Line 2:\nabc + def\n____^\n");
		}
		
		System.out.println("Scanner PASSED");
	}

}
