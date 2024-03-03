package pgdp.searchengine.networking;

public class HTMLToken {
	private TokenType tokenType;
	private StringBuilder content;

	public enum TokenType {
		TAG, TEXT
	}

	public HTMLToken(TokenType tokenType) {
		this.tokenType = tokenType;
		this.content = new StringBuilder();
	}

	public TokenType getTokenType() {
		return tokenType;
	}

	public String getContentAsString() {
		return content.toString();
	}

	public void addCharacter(char c) {
		content.append(c);
	}

	public String toString() {
		if (tokenType.equals(TokenType.TAG)) {
			return "Tag: " + getContentAsString();
		}

		else if (tokenType.equals(TokenType.TEXT)) {
			return "Text: " + getContentAsString();
		}

		return "";
	}

}
