package me.dec7.study.holub.text;

public interface Token {
	
	boolean match(String input, int offset);
	
	String lexeme();

}
