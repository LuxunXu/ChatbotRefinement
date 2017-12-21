import java.io.Serializable;

public class Response implements Serializable{
	
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
	
	public Response(String response) {
		this.response = response;
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
	}
	
	public Response(int a, int b, int c, int d) {
		this.response = "";
		this.totalLength = a;
		this.numberOfTokens = b;
		this.numberOfDigits = c;
		this.numberOfLetters = d;
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
	
	public boolean equals(Response r) {
		return this.response.equals(r.response);
	}
	
	public double eDistance(Response r) {
		return Math.pow(this.totalLength-r.getTotalLength(), 2) + Math.pow(this.numberOfTokens-r.getNumberOfTokens(), 2) + 
				Math.pow(this.numberOfDigits-r.getNumberOfDigits(), 2) + Math.pow(this.numberOfLetters-r.getNumberOfLetters(), 2);
	}
	
	public String toString() {
		return this.response;
	}
}
