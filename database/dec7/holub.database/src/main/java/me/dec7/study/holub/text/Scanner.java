package me.dec7.study.holub.text;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;

public class Scanner {
	
	private Token currentToken = new BeginToken();
	
	private BufferedReader inputReader = null;
	
	private int inputLineNumber = 0;
	
	private String inputLine = null;
	
	private int inputPosition = 0;
	
	private TokenSet tokens;
	
	public Scanner(TokenSet tokens, String input) {
		this(tokens, new StringReader(input));
	}

	public Scanner(TokenSet tokens, Reader inputReader) {
		this.tokens = tokens;
		this.inputReader = (inputReader instanceof BufferedReader) ? (BufferedReader) inputReader : new BufferedReader(inputReader);
		loadLine();
	}

	private boolean loadLine() {
		
		try {
			inputLine = inputReader.readLine();
			if (inputLine != null) {
				++inputLineNumber;
				inputPosition = 0;
			}
			
			return inputLine != null;
			
		} catch (Exception e) {
			
			return false;
		}
		
	}
	
	public boolean match(Token candidate) {
		
		return currentToken == candidate;
	}
	
	public Token advance() throws ParseFailure {
		
		try {
			
			if (currentToken != null) {
				inputPosition += currentToken.lexeme().length();
				currentToken = null;
				
				if (inputPosition == inputLine.length()) {
					if (!loadLine()) {
						
						return null;
					}
				}
				
				while (Character.isWhitespace(inputLine.charAt(inputPosition))) {
					if (++inputPosition == inputLine.length()) {
						if (!loadLine()) {
							
							return null;
						}
					}
				}
				
				for (Iterator i = tokens.iterator(); i.hasNext(); ) {
					Token t = (Token) (i.next());
					
					if (t.match(inputLine, inputPosition)) {
						currentToken = t;
						
						break;
					}
				}
				
				if (currentToken == null) {
					
					throw failure("Unrecognized Input");
				}
			}
		} catch (IndexOutOfBoundsException e) { }
		
		return currentToken;
	}

	public ParseFailure failure(String message) {
		
		return new ParseFailure(message, inputLine, inputPosition, inputLineNumber);
	}
	
	public String matchAdvance(Token candidate) throws ParseFailure {
		
		if (match(candidate)) {
			String lexeme = currentToken.lexeme();
			advance();
			
			return lexeme;
		}
		
		return null;
	}
	
	public final String required(Token candidate) throws ParseFailure {
		String lexeme = matchAdvance(candidate);
		
		if (lexeme == null) {
			throw failure("\"" + candidate.toString() + "\" expected.");
		}
		
		return lexeme;
	}

}
