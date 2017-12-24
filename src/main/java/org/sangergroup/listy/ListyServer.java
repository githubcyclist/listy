package org.sangergroup.listy;

import static spark.Spark.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class ListyServer {
	
	private static ArrayList<User> users = new ArrayList<>();
	
	private static final String CWD =
			Paths.get(".").toAbsolutePath().normalize().toString();
	
	private static String token = "";
	
	private static final boolean TESTING = true;

	public static void main(String[] args) throws Exception {
		loadJson();
		externalStaticFileLocation(CWD + File.separator + "public");
		ipAddress(InetAddress.getLocalHost().getHostAddress());
		port(7070);
		get("/", (req, res) -> {
			if(req.session().attribute("username") != null) {
				res.redirect("/tasklist.html?listId=0");
			} else {
				res.redirect("/index.html");
			}
			return
				"<!DOCTYPE html>"
				+ "<html>"
				+ "If you see this, chances are your browser doesn't support redirect.<br>"
			    + "<a href=\"/index.html\">Click here</a> to redirect.</html>";
		});
		get("/jsonsaver", (req, res) -> {saveJson(); return ""; });
		patch("/applyTemplate/:templateId/:listId", (req, res) -> {
			User u = getUser(req.session().attribute("username"));
			Template t = u.getTemplate(Integer.parseInt(req.params("templateId")));
			List l = u.getList(Integer.parseInt(req.params("listId")));
			t.apply(l);
			return "";
		});
		get("/list/:listId", (req, res) -> {
			res.redirect("/tasklist.html?listId=" + req.params("listId"));
			return
					"<!DOCTYPE html>"
					+ "<html>"
					+ "If you see this, chances are your browser doesn't support redirect.<br>"
				    + "<a href=\"/index.html\">Click here</a> to redirect.</html>";
		});
		get("/template/:templateId", (req, res) -> {
			res.redirect("/tasklist.html?listId=" + req.params("templateId")
											      + "&isTemplate=true");
			return
					"<!DOCTYPE html>"
					+ "<html>"
					+ "If you see this, chances are your browser doesn't support redirect.<br>"
				    + "<a href=\"/index.html\">Click here</a> to redirect.</html>";
		});
		get("/signOut", (req, res) -> {
			req.session().attribute("username", "");
			req.session().attribute("password", "");
			res.redirect("/");
			saveJson();
			return "";
		});
		get("/testRoute", (req, res) -> {
			User testUser = new User("test", "testpass");
			users.add(testUser);
			testUser.addList(new List("Today"));
			testUser.getList(0).addTask(new Task("Test task", false));
			req.session().attribute("username", "test");
			req.session().attribute("password", "testpass");
			return "";
		});
		delete("/deleteTask/:targetList/:id", (req, res) -> {
			User u = getUser(req.session().attribute("username"));
			List l = u.getList(Integer.parseInt(req.params("targetList")));
			l.getTasks().remove(Integer.parseInt(req.params("id")));
			saveJson();
			return "";
		});
		patch("/editTask/:targetList/:taskId/:newName/:newDesc", (req, res) -> {
			User u = getUser(req.session().attribute("username"));
			List l = u.getList(Integer.parseInt(req.params("targetList")));
			Task t = l.getTasks().get(Integer.parseInt(req.params("taskId")));
			t.setName(req.params("newName"));
			t.setDescription(req.params("newDesc"));
			saveJson();
			return "";
		});
		get("/tasks/:listId", "application/json", (req, res) -> {
			User u = getUser(req.session().attribute("username"));
			StringBuilder sb = new StringBuilder();
			sb.append("{")
			  .append("\"tasks\": [[[");
			int i = 0;
			for(Task task : u.getList(Integer.parseInt(req.params("listId"))).getTasks()) {
				sb.append("{")
				  .append("\"name\": \"" + task.getName() + "\", ")
				  .append("\"description\": \"" + task.getDescription() + "\", ")
				  .append("\"checked\": " + task.getChecked() + ", \"subtasks\": []")
				  .append("}, ");
			}
			String arraySoFar = sb.toString();
			arraySoFar = arraySoFar.substring(0, arraySoFar.length() - 2);
			// Hacky workaround for "square-bracket-eating syndrome". :)
			if(arraySoFar.contains("\"tasks\": [[[")) {
				arraySoFar = arraySoFar.replace("\"tasks\": [[[", "\"tasks\": [");
			}
			return arraySoFar + "]}";
		});
		get("/templateTasks/:templateId", "application/json", (req, res) -> {
			User u = getUser(req.session().attribute("username"));
			StringBuilder sb = new StringBuilder();
			sb.append("{")
			  .append("\"tasks\": [[[");
			int i = 0;
			for(TemplateTask task : u.getTemplate(
					Integer.parseInt(req.params("templateId"))).getTasks()) {
				sb.append("{")
				  .append("\"name\": \"" + task.getName() + "\"")
				  .append("}, ");
			}
			String arraySoFar = sb.toString();
			arraySoFar = arraySoFar.substring(0, arraySoFar.length() - 2);
			// Hacky workaround for "square-bracket-eating syndrome". :)
			if(arraySoFar.contains("\"tasks\": [[[")) {
				arraySoFar = arraySoFar.replace("\"tasks\": [[[", "\"tasks\": [");
			}
			return arraySoFar + "]}";
		});
		get("/newTask/:targetList/:name/:description", (req, res) -> {
			User u = getUser(req.session().attribute("username"));
			List l = u.getList(Integer.parseInt(req.params("targetList")));
			Task t = new Task(req.params("name"), false);
			t.setDescription(req.params("description"));
			l.addTask(t);
			System.out.println("task: name=" + t.getName() + ", desc=" + t.getDescription());
			saveJson();
			return "";
		});
		get("/newTemplateTask/:targetTemplate/:name", (req, res) -> {
			User u = getUser(req.session().attribute("username"));
			Template temp = u.getTemplate(Integer.parseInt(req.params("targetTemplate")));
			TemplateTask t = new TemplateTask(req.params("name"));
			temp.addTask(t);
			saveJson();
			return "";
		});
		get("/currentuser", (req, res) -> {
			String username = req.session().attribute("username");
			if(username != null) {
				return username;
			} else {
				return ""; // Return empty string instead of null to prevent route mapping error
			}
		});
		get("/newList/:name", (req, res) -> {
			User u = getUser(req.session().attribute("username"));
			u.addList(new List(req.params("name")));
			saveJson();
			return "";
		});
		get("/newTemplate/:name", (req, res) -> {
			User u = getUser(req.session().attribute("username"));
			u.addTemplate(new Template(req.params("name")));
			saveJson();
			return "";
		});
		get("/lists", "application/json", (req, res) -> {
			User u = getUser(req.session().attribute("username"));
			StringBuilder sb = new StringBuilder();
			sb.append("{ \"listNames\": [");
			for(int i = 0; i < u.getLists().size(); i++) {
				sb.append("\"" + u.getList(i).getName() + "\", ");
			}
			sb.append("\"\"] }");
			return sb.toString();
		});
		get("/templates", "application/json", (req, res) -> {
			User u = getUser(req.session().attribute("username"));
			StringBuilder sb = new StringBuilder();
			sb.append("{ \"templateNames\": [");
			for(int i = 0; i < u.getTemplates().size(); i++) {
				sb.append("\"" + u.getTemplate(i).getName() + "\", ");
			}
			sb.append("\"\"] }");
			return sb.toString();
		});
		get("/signin", (req, res) -> {
			res.redirect("/signin.html");
			return
					"<!DOCTYPE html>"
					+ "<html>"
					+ "If you see this, chances are your browser doesn't support redirect.<br>"
				    + "<a href=\"/signin.html\">Click here</a> to redirect.</html>";
		});
		post("/toggle/:list/:id", (req, res) -> {
			User u = getUser(req.session().attribute("username"));
			int listNum = Integer.parseInt(req.params("list"));
			int id = Integer.parseInt(req.params("id"));
			u.getList(listNum).getTasks().get(id).toggle();
			saveJson();
			return "";
		});
		get("/signIn/:username/:password", (req, res) -> {
			String user = req.params("username");
			String pass = req.params("password");
			for(User u : users) {
				System.out.println(u.getUsername() + " " + u.getPassword());
				if(u.getUsername().equals(user) && u.getPassword().equals(pass)) {
					req.session().attribute("username", user);
					req.session().attribute("password", pass);
					System.err.println("Signin successful!");
					return "";
				}
			}
			res.redirect("/signin-error.html");
			return "";
		});
		post("/signup/:username/:password", (req, res) -> {
			String user = req.params("username");
			String pass = req.params("password");
			User newUser = new User(user, pass);
			newUser.addList(new List("Today"));
			newUser.getList(0).addTask(new Task("Welcome to Listy!", true));
			users.add(newUser);
			req.session().attribute("username", user);
			req.session().attribute("password", pass);
			System.err.println("Signup successful!");
			saveJson();
			return "";
		});
	}
	
	private static class TokenGenerator {
		
		public static String generate(int length) {
			String chars = "abcdefghijklmnopqrstuvwxyz" // lowercase letters
						 + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" // uppercase letters
						 + "1234567890" // numbers (single digits)
						 + "!@#$%^&*()-_+={}|\\[]*~`.,<>?/"; // assorted other chars
			String token = "";
			for(int i = 0; i < length; i++) {
				token += chars.charAt(new Random().nextInt(chars.length() - 1));
			}
			return token;
		}
		
	}
	
	private static final String SERVER_FILE = CWD + File.separator + "server.json";
	
	private static void loadJson() throws JSONException, FileNotFoundException {
		if(new File(SERVER_FILE).exists()) {
			JSONObject obj = new JSONObject(new Scanner(new File(SERVER_FILE)).nextLine());
			JSONArray usersArr = obj.getJSONArray("users");
			for(int i = 0; i < usersArr.length(); i++) {
				JSONObject user = usersArr.getJSONObject(i);
				User u = new User(user.getString("username"),
						          user.getString("password"));
				JSONArray lists = user.getJSONArray("lists");
				for(int i1 = 0; i1 < lists.length(); i1++) {
					JSONObject listObj = lists.getJSONObject(i1);
					JSONArray listArr = listObj.getJSONArray("tasks");
					List l = new List(listObj.getString("name"));
					for(int i2 = 0; i2 < listArr.length(); i2++) {
						JSONObject task = listArr.getJSONObject(i2);
						Task t = new Task("", false);
						t.setName(task.getString("name"));
						t.setDescription(task.getString("description"));
						t.setChecked(task.getBoolean("checked"));
						l.addTask(t);
					}
					u.addList(l);
				}
				JSONArray templates = user.getJSONArray("templates");
				for(int i1 = 0; i1 < templates.length(); i1++) {
					JSONObject templateObj = templates.getJSONObject(i1);
					JSONArray templateArr = templateObj.getJSONArray("templateTasks");
					Template t = new Template(templateObj.getString("name"));
					for(int i2 = 0; i2 < templateArr.length(); i2++) {
						JSONObject task = templateArr.getJSONObject(i2);
						TemplateTask t1 = new TemplateTask(task.getString("name"));
						t.addTask(t1);
					}
					u.addTemplate(t);
				}
				users.add(u);
			}
		} else {
			System.err.println("No conf file to load");
			return;
		}
	}
	
	private static void saveJson() throws FileNotFoundException {
		JSONStringer stringy = new JSONStringer();
		stringy.object() // main object
			   .key("settings")
			   .object()
			   .key("token").value(TokenGenerator.generate(32))
			   .endObject()
			   .key("users").array(); // users array
		for(User u : users) {
			stringy.object() // user object
				   .key("username").value(u.getUsername()) // User's username
				   .key("password").value(u.getPassword()) // User's password
				   .key("lists").array(); // lists array
			for(List l : u.getLists()) {
				stringy.object() // list object
					   .key("name").value(l.getName()) // List name
					   .key("tasks").array(); // tasks array
				for(Task t : l.getTasks()) {
					stringy.object()
						   .key("name").value(t.getName())
						   .key("description").value(t.getDescription())
						   .key("checked").value(t.getChecked())
						   .key("subtasks").array().endArray()
						   .endObject();
				}
				stringy.endArray() // end tasks array
					   .endObject(); // end list object
			}
			stringy.endArray() // end lists array
				   .key("templates").array(); // templates array
			for(Template t : u.getTemplates()) {
				stringy.object() // template object
					   .key("name").value(t.getName()) // Template name
					   .key("templateTasks").array(); // tasks array
				for(TemplateTask t1 : t.getTasks()) {
					stringy.object()
						   .key("name").value(t1.getName())
						   .key("subtasks").array().endArray()
						   .endObject();
				}
				stringy.endArray() // end tasks array
					   .endObject(); // end template object
			}
			stringy.endArray(); // end templates array
			stringy.endObject(); // end user object
		}
		stringy.endArray() // end users array
			   .endObject(); // end main object
		String finishedJson = stringy.toString();
		PrintWriter pWriter = new PrintWriter(new File(SERVER_FILE));
		pWriter.print(finishedJson);
		pWriter.close();
	}
	
	private static User getUser(String username) {
		for(User u : users) {
			if(u.getUsername().equals(username))
				return u;
		}
		return null;
	}

}
