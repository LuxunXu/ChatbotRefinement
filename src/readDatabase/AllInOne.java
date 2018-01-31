package readDatabase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

public class AllInOne {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String dbURL = "jdbc:mysql://demo.smartbot360.com:3306";
		String currentDB = "CHATBOTDEMO";
		String username = "mwile001";
		String password = "SDFd3143!!!";
		String tableName = "DoctorAppointmentResponseCluster";
		
		
		Connection conn = null;
		PreparedStatement st = null;
		
		// Get Log
		String sql = "SELECT l.SDFuserid, l.message, l.sendReceive from CHATBOTDEMO.chatLog l " + 
				"JOIN (SELECT DISTINCT d.SDFuserid FROM CHATBOTDEMO.DoctorAppointmentExecution d) s on s.SDFuserid = l.SDFuserid " + 
				"WHERE l.chatbotFunctionId='DoctorAppointment';";
		String logFileName = "DALog.txt";
		BufferedWriter out = new BufferedWriter(new FileWriter(logFileName));
		out.write("SDFuserid|message|sendReceive\n");
		try {
			conn = DriverManager.getConnection(dbURL + "/" + currentDB, username, password);
			st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				out.write(rs.getString("SDFuserid").trim() + "|" + rs.getString("message").toLowerCase().trim() + "|" + rs.getString("sendReceive").trim() + "\n");
			}
			rs.close();
			st.close();
			out.close();
		} catch (SQLException el) {
			el.printStackTrace();
		}
		
		
		
		/*
		 * This is for saving to database
		sql = "DROP TABLE IF EXISTS " + tableName;
		try {
			conn = DriverManager.getConnection(dbURL + "/" + currentDB, username, password);
			
		} catch (SQLException el) {
			el.printStackTrace();
			System.out.println("SQLException when iserting save columns into the execution table");
		}*/
	}

}

class Response implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8129883885917500627L;
	private String response;
	
	//Parameters
	private int totalLength;
	private int numberOfTokens;
	private int numberOfDigits = 0;
	private int numberOfLetters = 0;
	private String jsonResponse;
	
	// Construct a normal response
	public Response(String response) throws IOException {
		this.response = response.toLowerCase();
		this.totalLength = response.length();
		String[] tokens = response.split("\\W");
		this.numberOfTokens = tokens.length;
		for (int i = 0; i < response.length(); i++) {
			if (Character.isDigit(response.charAt(i))) {
				this.numberOfDigits++;
			} else if (Character.isLetter(response.charAt(i))) {
				this.numberOfLetters++;
			}
		}
		this.jsonResponse = getJObject(response.replaceAll("\\s", "%20"));
	}
	
	// For calculating mean
	public Response(String s, int a, int b, int c, int d) {
		this.response = s;
		this.totalLength = a;
		this.numberOfTokens = b;
		this.numberOfDigits = c;
		this.numberOfLetters = d;
		this.jsonResponse = "{}";
	}
	
	public String getResponse() {
		return this.response;
	}
	
	public int getTotalLength() {
		return this.totalLength;
	}
	
	public int getNumberOfTokens() {
		return this.numberOfTokens;
	}
	
	public int getNumberOfDigits() {
		return this.numberOfDigits;
	}
	
	public int getNumberOfLetters() {
		return this.numberOfLetters;
	}
	
	public JSONObject getJSON() {
		return new JSONObject(this.jsonResponse);
	}
	
	public boolean equals(Response r) {
		return this.response.equals(r.response);
	}
	
	public double eDistance(Response r) {
		return Math.pow(this.totalLength-r.getTotalLength(), 2) + 2*Math.pow(this.numberOfTokens-r.getNumberOfTokens(), 2) + 
				Math.pow(this.numberOfDigits-r.getNumberOfDigits(), 2) + Math.pow(this.numberOfLetters-r.getNumberOfLetters(), 2);
	}
	
	public String toString() {
		return this.response;
	}
	
	private String getJObject(String response) throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		//System.out.println(dateFormat.format(date));
		String link = "https://api.wit.ai/message?v=" + dateFormat.format(date) + "&q=" + response;
		URL thePage = new URL(link);
        URLConnection yc = thePage.openConnection();
        yc.setRequestProperty("Authorization", "Bearer 7ECLPXMO67MDIUGVFETO3GP26WB3NMIH");
        //System.out.println(yc.getContentType());
        BufferedReader in = new BufferedReader(new InputStreamReader(
                                    yc.getInputStream(), "utf-8"));
        String inputLine;
        String output = "";
        while ((inputLine = in.readLine()) != null) {
        	output += inputLine + "\n";
        }
        //JSONObject get = new JSONObject(output);
        in.close();
        //System.out.println(output);
		return output;
	}
}
