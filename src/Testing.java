import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.*;

public class Testing {

	public static void main(String[] args) throws IOException {
		String response = "nothing in the afternoon?".replaceAll("\\s", "%20");
		JSONObject test = getJObject(response);
		System.out.println(test.getJSONObject("entities").keySet().toString());
	}
	
	public static JSONObject getJObject(String response) throws IOException {
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
        JSONObject get = new JSONObject(output);
        in.close();
        System.out.println(output);
		return get;
	}
}
