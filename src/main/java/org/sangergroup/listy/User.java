package org.sangergroup.listy;

import java.util.ArrayList;

public class User {
	
	private ArrayList<List> lists = new ArrayList<>();
	
	private ArrayList<Template> templates = new ArrayList<>();
	
	private List trash = new List();
	
	private String username = "";
	private String password = "";
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public User() {} 
	
	public ArrayList<List> getLists() {
		return lists;
	}
	
	public ArrayList<Template> getTemplates() {
		return templates;
	}
	
	public List getTrash() { return trash; }
	
	public void trash(int listIndex, int taskIndex) {
		ArrayList<Task> tasks = getList(listIndex).getTasks();
		Task task = tasks.get(taskIndex);
		getList(listIndex).getTasks().remove(taskIndex);
	}
	
	public List getList(int index) {
		return lists.get(index);
	}
	
	public void setList(int index, List element) {
		lists.set(index, element);
	}
	
	public Template getTemplate(int index) {
		return templates.get(index);
	}
	
	public void setTemplate(int index, Template element) {
		templates.set(index, element);
	}
	
	public void addList(List element) {
		lists.add(element);
	}
	
	public void addTemplate(Template element) {
		templates.add(element);
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setUsername(String newUsername) {
		this.username = newUsername;
	}
	
	public void setPassword(String newPassword) {
		this.password = newPassword;
	}

}
