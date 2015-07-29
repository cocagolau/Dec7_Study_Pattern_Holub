package me.dec7.study.holub.text;


public class WordToken implements Token {
	
	private final String pattern;
	
	public WordToken(String pattern) {
		this.pattern = pattern;
	}

	@Override
	public boolean match(String input, int offset) {
		
		if ((input.length() - offset) < pattern.length()) {
			return false;
		}
		
		String candidate = input.substring(offset, offset+pattern.length());
		if (!candidate.equalsIgnoreCase(pattern)) {
			return false;
		}
		
		return ((input.length() - offset) == pattern.length()) || (!Character.isLetterOrDigit(input.charAt(offset + pattern.length())));
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
