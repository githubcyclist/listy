package org.sangergroup.listy;

import java.util.ArrayList;

public class Template {

	ArrayList<TemplateTask> tasks = new ArrayList<>();
	
	String name = "";
	
	public Template() {}
	
	public Template(String listName) {
		this.name = listName;
	}
	
	public Template(ArrayList<TemplateTask> tasks) {
		this.tasks = tasks;
	}

	public ArrayList<TemplateTask> getTasks() {
		return tasks;
	}
	
	public void addTask(TemplateTask t) {
		this.tasks.add(t);
	}
	
	public String getName() { return name; }
	
	public void apply(List l) {
		for(TemplateTask task : this.getTasks()) {
			Task translationTask = new Task();
			translationTask.setName(task.getName());
			l.addTask(translationTask);
		}
	}
	
}