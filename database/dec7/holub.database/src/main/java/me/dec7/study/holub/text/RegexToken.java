package me.dec7.study.holub.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexToken implements Token {
	
	private Matcher matcher;
	
	private final Pattern pattern;
	
	private final String id;
	
	public RegexToken(String description) {
		id = description;
		pattern = Pattern.compile(description, Pattern.CASE_INSENSITIVE);
	}

	@Override
	public boolean match(String input, int offset) {
		matcher = pattern.matcher(input.substring(offset));
		
		return matcher.lookingAt();
	}

	@Override
	public String lexeme() {
		
		return matcher.group();
	}

	@Override
	public String toString() {
		
		return id;
	}

}
