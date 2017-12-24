package org.sangergroup.listy;

public class Subtask {
	
	private String taskName = "";
	private boolean checked = false;
	
	public boolean getChecked() { return checked; }
	
	public void toggle() { checked = (checked ? false : true); } 

}
