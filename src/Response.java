import java.io.Serializable;

public class Response implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8129883885917500627L;
	private String response;
	private int totalLength;
	private int numberOfTokens;
	
	public Response(String response) {
		this.response = response;
		this.totalLength = response.length();
		String[] tokens = response.split("\\W");
		this.numberOfTokens = tokens.length;
	}
	
	public int getTotalLength() {
		return this.totalLength;
	}
	
	public int getNumberOfTokens() {
		return this.numberOfTokens;
	}
	
	public String toString() {
		return this.response;
	}
}
