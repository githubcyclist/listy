package org.sangergroup.listy;

import java.util.ArrayList;

public class TemplateTask {
	
	private String name;
	private ArrayList<TemplateSubtask> templateSubtasks = new ArrayList<>();
	
	public TemplateTask() {}
	
	public TemplateTask(String name) {
		this.name = name;
	}
	
	public void setName(String name) { this.name = name; }
	
	public String getName() { return name; }
	
	public ArrayList<TemplateSubtask> getSubtasks() { return templateSubtasks; }

}
