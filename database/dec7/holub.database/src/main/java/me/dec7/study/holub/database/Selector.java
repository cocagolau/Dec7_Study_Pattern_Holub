package me.dec7.study.holub.database;

interface Selector {
	
	boolean approve(Cursor[] tables);
	
	void modify(Cursor current);
	
	public static class Adapter implements Selector {

		@Override
		public boolean approve(Cursor[] tables) {
			
			return true;
		}

		@Override
		public void modify(Cursor current) {
			
			throw new UnsupportedOperationException("Can't use a Selector.Adapter in an update");
		}
		
	}
	
	public static final Selector ALL = new Selector.Adapter();

}
