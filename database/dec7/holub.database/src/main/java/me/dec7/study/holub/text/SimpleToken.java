package me.dec7.study.holub.text;

public class SimpleToken implements Token {
	
	private final String pattern;
	
	public SimpleToken(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean match(String input, int offset) {
		
		return input.toLowerCase().startsWith(pattern, offset);
	}

	@Override
	public String lexeme() {
		
		return pattern;
	}

	@Override
	public String toString() {
		
		return pattern;
	}

}
