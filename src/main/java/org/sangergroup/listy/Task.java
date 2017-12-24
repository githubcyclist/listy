package org.sangergroup.listy;

import java.util.ArrayList;

public class Task {
	
	public Task(String taskName, boolean checked) {
		this.taskName = taskName;
		this.checked = checked;
	}
	
	public Task() {} 
	
	private String taskName = "";
	private boolean checked = false;
	
	private String taskDescription = "";
	
	private ArrayList<Subtask> subtasks = new ArrayList<>();
	
	public void toggle() { checked = (checked ? false : true); } 
	
	public void setChecked(boolean checked) { this.checked = checked; }
	
	public Subtask getSubtask(int index) {
		return subtasks.get(index);
	}
	
	public String getName() {
		return taskName;
	}
	
	public void setName(String name) {
		taskName = name;
	}
	
	public String getDescription() {
		return taskDescription;
	}
	
	public void setDescription(String description) {
		taskDescription = description;
	}
	
	public boolean getChecked() {
		return checked;
	}

}
