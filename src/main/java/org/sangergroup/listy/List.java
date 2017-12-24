package org.sangergroup.listy;

import java.util.ArrayList;

public class List {
	
	ArrayList<Task> tasks = new ArrayList<>();
	
	String name = "";
	
	public List() {}
	
	public List(String listName) {
		this.name = listName;
	}
	
	public List(ArrayList<Task> tasks) {
		this.tasks = tasks;
	}

	public ArrayList<Task> getTasks() {
		return tasks;
	}
	
	public void addTask(Task t) {
		this.tasks.add(t);
	}
	
	public String getName() { return name; }

}
