import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import org.json.*;

public class Response implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8129883885917500627L;
	private String response;
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
