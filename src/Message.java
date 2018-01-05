import java.io.Serializable;
import java.util.*;

public class Message implements Serializable{
	/**
	 * Message is the map of Question and List of its corresponding Responses.
	 */
	private static final long serialVersionUID = 874087594914412994L;
	public Map<String, LinkedList<Response>> qAndAMap;
	
	public Message() {
		qAndAMap = new HashMap<String, LinkedList<Response>>();
	}
	
	public Map<String, LinkedList<Response>> getMap() {
		return qAndAMap;
	}
	
	public String toString() {
		String toPrint = "";
		for (String key : qAndAMap.keySet()) {
			toPrint += key + "\t" + qAndAMap.get(key).toString() + "\n";
		}
		return toPrint;
	}
}
