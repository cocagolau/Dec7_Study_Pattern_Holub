package me.dec7.study.holub.tools;

public class ThrowableContainer extends RuntimeException {
	
	private final Throwable contents;
	
	public ThrowableContainer(Throwable contents) {
		this.contents = contents;
	}
	
	public Throwable contents() {
		
		return contents;
	}

}
