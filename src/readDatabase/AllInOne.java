package readDatabase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONObject;

import test.Message;
import test.Response;
import test.ResponseVector;

public class AllInOne {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		int choice = 2;
		if (choice == 1) {
			String dbURL = "jdbc:mysql://demo.smartbot360.com:3306";
			String currentDB = "CHATBOTDEMO";
			String username = "mwile001";
			String password = "SDFd3143!!!";
			//String tableName = "DoctorAppointmentResponseCluster";
			
			
			Connection conn = null;
			PreparedStatement st = null;
			
			// Get Log
			
			String sql = "SELECT l.SDFuserid, l.message, l.sendReceive from CHATBOTDEMO.chatLog l " + 
					"JOIN (SELECT DISTINCT d.SDFuserid FROM CHATBOTDEMO.DoctorAppointmentExecution d) s on s.SDFuserid = l.SDFuserid " + 
					"WHERE l.chatbotFunctionId='DoctorAppointment' ORDER BY l.SDFuserid, l.sentTime;";
			String logFileName = "DALog.txt";
			BufferedWriter out = new BufferedWriter(new FileWriter(logFileName));
			out.write("SDFuserid|message|sendReceive\n");
			try {
				conn = DriverManager.getConnection(dbURL + "/" + currentDB, username, password);
				st = conn.prepareStatement(sql);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					out.write(rs.getString("SDFuserid").trim() + "|" + rs.getString("message").toLowerCase().trim().replaceAll("\n", " ") + "|" + rs.getString("sendReceive").trim() + "\n");
				}
				rs.close();
				st.close();
				out.close();
			} catch (SQLException el) {
				el.printStackTrace();
			}
			
			Map<String, LinkedList<Response>> qAndAMap = processLog(logFileName);
			convertToVector(qAndAMap);
		} else if (choice == 2) {
			String dbURL = "jdbc:mysql://demo.smartbot360.com:3306";
			String currentDB = "CHATBOTDEMO";
			String username = "mwile001";
			String password = "SDFd3143!!!";
			
			
			Connection conn = null;
			PreparedStatement st = null;
			
			//QNumber, Response, Cluster Number, Cluster Meaning, IsRepresentative
			String sql = "INSERT INTO CHATBOTDEMO.DAResponseCluster VALUES (?, ?, ?, ?, ?);";
			try {
				conn = DriverManager.getConnection(dbURL + "/" + currentDB, username, password);
				st = conn.prepareStatement(sql);
				st.setInt(1, 2);
				st.setString(2, "Hello");
				st.setInt(3, 1);
				st.setString(4, "Greetings");
				st.setInt(5, 1);
				st.execute();
				st.close();
			} catch (SQLException el) {
				el.printStackTrace();
			}
		}
	}
	
	public static Map<String, LinkedList<Response>> processLog(String fileName) throws IOException {
		File f = new File(fileName);
		Scanner sc = new Scanner(f);
		String line = sc.nextLine();
		int currentID = 0;
		Map<Integer, Message> idMap= new HashMap<Integer, Message>();
		LinkedList<String> qaPair = new LinkedList<String>();
		while(sc.hasNextLine()) {
			line = sc.nextLine();
			String[] tokens = line.split("\\|");
			//System.out.println(line);
			int id = Integer.parseInt(tokens[0]);
			if (currentID != id) {
				System.out.println(id);
				currentID = id;
				idMap.put(id, new Message());
				qaPair.clear();
			}
			if (qaPair.size() < 2) {
				if (tokens[2].equals("0")) {
					qaPair.clear();
					qaPair.add(tokens[1].trim());
				} else if (tokens[2].equals("1") && !qaPair.isEmpty()) {
					qaPair.add(tokens[1].trim());
				}
			}
			if (qaPair.size() == 2) {
				if (!idMap.get(id).getMap().containsKey(qaPair.get(0))) {
					idMap.get(id).getMap().put(qaPair.get(0), new LinkedList<Response>());
					Response r = new Response(qaPair.get(1));
					//System.out.println(r.getJSON().toString());
					idMap.get(id).getMap().get(qaPair.get(0)).add(r);
				} else {
					Response r = new Response(qaPair.get(1));
					idMap.get(id).getMap().get(qaPair.get(0)).add(r);
				}
				qaPair = new LinkedList<String>();
			}
		}
		
		//System.out.println(idMap);
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("DAResponsesMap.txt"));
		Map<String, LinkedList<Response>> overallMap = new HashMap<String, LinkedList<Response>>();
		for (int i : idMap.keySet()) {
			Message m = idMap.get(i);
			Map<String, LinkedList<Response>> qaMap = m.getMap();
			for (String q : qaMap.keySet()) {
				String temp = q;
				if (q.startsWith("q1:") || q.startsWith("q2:") || q.startsWith("q3:")) {
					continue;
				}
				if (q.contains("congrats")) {
					continue;
				} else if (q.contains("please select a time slot")) {
					temp = "Please select a time slot for XXXX-XX-XX.";
				} else if (q.contains("which department would you like to visit?")) {
					temp = "XXX, which department would you like to visit?";
				} else if (q.contains("please tell me your date of birth")) {
					temp = "Thanks. XXX, please tell me your date of birth.";
				} else if (q.contains("may I know your age")) {
					temp = "Good to see you, XXX. May I know your age?";
				}
				if (!overallMap.keySet().contains(temp)) {
					overallMap.put(temp, new LinkedList<Response>());
				}
				for (Response r : qaMap.get(q)) {
					overallMap.get(temp).add(r);
					//writer.write(r.toString());
					//writer.newLine();
				}
			}
		}
		//writer.close();
		
		String toPrint = "";
		for (String key : overallMap.keySet()) {
			toPrint += key + "\t" + overallMap.get(key).toString() + "\n";
		}
		//System.out.println(toPrint);
		writer.write(toPrint);
		writer.close();
		
		return overallMap;
	}
	
	public static void convertToVector(Map<String, LinkedList<Response>> qAndAMap) throws IOException {
		BufferedWriter writer = null;
		BufferedWriter writer1 = null;
		LinkedList<Response> l = null;
		ResponseVector rv = null;
		int count = 0;
		for (String s : qAndAMap.keySet()) {
			//System.out.println(s.substring(0,s.length() - 1));
			writer = new BufferedWriter(new FileWriter(s.substring(0,s.length() - 1) + "-Vector"));
			writer1 = new BufferedWriter(new FileWriter(s.substring(0,s.length() - 1) + "-ResponseList"));
			l = qAndAMap.get(s);
			count = 0;
			rv = new ResponseVector(l);
			for (Response r : l) {
				//System.out.println(rv.getVector(r.toString()));
				//System.out.println(count + "\t" + r.toString());
				writer1.write(r.toString() + "\n");
				count++;
				//writer.write(r.toString() + ": ");
				writer.write(r.getNumberOfDigits() + " " + r.getNumberOfLetters() + " " + r.getNumberOfTokens() + " " + r.getTotalLength() + " ");
				JSONObject j = r.getJSON();
				//System.out.println(j.toString());
				//yesno, datatime, department, phone_number
				if (j.has("entities") && j.getJSONObject("entities").has("yesno")) {
					writer.write(j.getJSONObject("entities").getJSONArray("yesno").getJSONObject(0).getDouble("confidence") + " ");
				} else {
					writer.write("0 ");
				}
				if (j.has("entities") && j.getJSONObject("entities").has("datetime")) {
					writer.write(j.getJSONObject("entities").getJSONArray("datetime").getJSONObject(0).getDouble("confidence") + " ");
				} else {
					writer.write("0 ");
				}
				if (j.has("entities") && j.getJSONObject("entities").has("department")) {
					writer.write(j.getJSONObject("entities").getJSONArray("department").getJSONObject(0).getDouble("confidence") + " ");
				} else {
					writer.write("0 ");
				}
				if (j.has("entities") && j.getJSONObject("entities").has("phone_number")) {
					writer.write(j.getJSONObject("entities").getJSONArray("phone_number").getJSONObject(0).getDouble("confidence") + " ");
				} else {
					writer.write("0 ");
				}
				for (Integer i : rv.getVector(r.toString())) {
					writer.write(i + " ");
				}
				writer.newLine();
			}
			//break;
			writer.close();
			writer1.close();
		}
	}
}

