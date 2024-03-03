package pgdp.searchengine.networking;

import java.util.*;
import java.util.stream.Collectors;

import pgdp.searchengine.networking.HTMLToken.TokenType;

public final class HTMLProcessing {

	// Useless constructor for SCA
	private HTMLProcessing() {

	}

	public static List<HTMLToken> tokenize(String rawHTML) {
		List<HTMLToken> tokens = new ArrayList<HTMLToken>();
		String[] res = rawHTML.split("\r\n");

		for (int i = 0; i < res.length; i++) {

			List<HTMLToken> tags = findTag(res[i]);
			if (!tags.isEmpty()) {
				Iterator<HTMLToken> it = tags.iterator();
				while (it.hasNext()) {
					tokens.add(it.next());
				}

			}

		}

		return tokens;
	}

	public static List<HTMLToken> findTag(String temp) {

		HTMLToken token = new HTMLToken(TokenType.TAG);
		boolean upperCaseAlert = false;
		boolean upperCaseAlert2 = false;
		boolean starts = false;
		boolean alreadyFind = false;
		List<HTMLToken> tags = new ArrayList<HTMLToken>();

		for (int i = 0; i < temp.length(); i++) {
			if (starts && temp.charAt(i) == '"'  && i+1 < temp.length() && temp.substring(i+1).contains("\"")) {
				upperCaseAlert = true;
			}
			if (starts && temp.charAt(i) == '\'' && i+1 < temp.length() && temp.substring(i+1).contains("'")) {
				upperCaseAlert2 = true;
			}

			if (!starts && !alreadyFind) {
				List<HTMLToken> text = findText(temp.substring(i));
				if (!text.isEmpty()) {
					tags.addAll(text);
					alreadyFind = true;
				}

			}

			if (!upperCaseAlert && !upperCaseAlert2 && starts && temp.charAt(i) == '>') {

				tags.add(token);

				token = new HTMLToken(TokenType.TAG);
				alreadyFind = false;
				starts = false;
			}
			if (starts) {
				if (upperCaseAlert || upperCaseAlert2) {
					token.addCharacter(temp.charAt(i));
				} else {
					token.addCharacter(Character.toLowerCase(temp.charAt(i)));
				}
			}

			if (temp.charAt(i) == '<' && i+1 < temp.length() && temp.substring(i+1).contains(">")) {
				alreadyFind = false;
				starts = true;
			}
			if (upperCaseAlert && i+1 < temp.length() && temp.charAt(i + 1) == '"') {
				if(!alreadyFind) {
					token.addCharacter(temp.charAt(i+1));
				}
				
				i = i + 1;
				upperCaseAlert = false;
			}
			if (upperCaseAlert2 && i+1 < temp.length() && temp.charAt(i + 1) == '\'') {
				if(!alreadyFind) {
					token.addCharacter(temp.charAt(i+1));
				}
				i = i + 1;
				upperCaseAlert2 = false;
			}

		}

		return tags;
	}

	public static List<HTMLToken> findText(String temp) {
		boolean upperCaseAlert = false;
		boolean upperCaseAlert2 = false;
		
		
		
		HTMLToken token = new HTMLToken(TokenType.TEXT);
		boolean starts2 = false;
		List<HTMLToken> tags = new ArrayList<HTMLToken>();

		for (int k = 0; k < temp.length(); k++) {
			if (starts2 && temp.charAt(k) == '"' && k+1 < temp.length() && temp.substring(k+1).contains("\"")) {
				upperCaseAlert = true;
			}
			if (starts2 && temp.charAt(k) == '\''  && k+1 < temp.length() && temp.substring(k+1).contains("'")) {
				upperCaseAlert2 = true;
			}
			
				
			if (temp.charAt(k) == '<' && k+1 < temp.length() && temp.substring(k+1).contains(">")) {
				starts2 = true;
			}
			if (!starts2) {
				token.addCharacter(Character.toLowerCase(temp.charAt(k)));
			}
			if (!upperCaseAlert && !upperCaseAlert2 && starts2 && temp.charAt(k) == '>') {
				if (token.getContentAsString().length() != 0) {

					tags.add(token);
				}
				starts2 = false;
				return tags;
			}

		
			
			
			if (upperCaseAlert && k < temp.length() - 1 && temp.charAt(k + 1) == '"') {
				
				if(!starts2) {
				token.addCharacter(temp.charAt(k + 1));
				}
				k = k + 1;
				upperCaseAlert = false;
			}
			if (upperCaseAlert2 && k < temp.length() - 1 && temp.charAt(k + 1) == '\'') {
				if(!starts2) {
					token.addCharacter(temp.charAt(k + 1));
				}
				k = k + 1;
				upperCaseAlert2 = false;
			}

			if (k == temp.length() - 1 && token.getContentAsString().length() != 0) {
				tags.add(token);
			}

			
			
			
			
			
			
		}

		return tags;
	}

	public static String[] filterLinks(List<HTMLToken> tokens, String host) {
	
		List<HTMLToken> tags = tokens.stream().filter(HTMLToken -> HTMLToken.getTokenType().equals(TokenType.TAG))
				.collect(Collectors.toList());
		List<HTMLToken> links = tags.stream().filter(HTMLToken -> HTMLToken.getContentAsString().contains("href=\"") || HTMLToken.getContentAsString().contains("href='") && HTMLToken.getContentAsString().startsWith("a ")).collect(Collectors.toList());
		
		links.stream().forEach(HTMLToken -> HTMLToken.getContent().replace(0, HTMLToken.getContentAsString().indexOf("href=") +5, ""));
				
		List<HTMLToken> starting1 = links.stream().filter(HTMLToken -> HTMLToken.getContentAsString().startsWith("\"")).collect(Collectors.toList());
		starting1.stream().forEach(HTMLToken -> HTMLToken.getContent().deleteCharAt(0));
		List<HTMLToken> starting2 = links.stream().filter(HTMLToken -> HTMLToken.getContentAsString().startsWith("'")).collect(Collectors.toList());
		starting2.stream().forEach(HTMLToken -> HTMLToken.getContent().deleteCharAt(0));
		
		starting1.stream().filter(HTMLToken -> HTMLToken.getContentAsString().contains("\"")).forEach(HTMLToken -> HTMLToken.getContent().replace(HTMLToken.getContentAsString().indexOf("\""), HTMLToken.getContentAsString().length(), ""));
		starting2.stream().filter(HTMLToken -> HTMLToken.getContentAsString().contains("\'")).forEach(HTMLToken -> HTMLToken.getContent().replace(HTMLToken.getContentAsString().indexOf("\'"), HTMLToken.getContentAsString().length(), ""));
				
		links.stream().filter(HTMLToken -> HTMLToken.getContentAsString().startsWith("#")).forEach(HTMLToken -> HTMLToken.getContent().insert(0, "/"));
		
		

		links.stream().filter(HTMLToken -> HTMLToken.getContentAsString().startsWith("https://")).forEach(HTMLToken -> HTMLToken.getContent().replace(0,8, ""));
		
		
		links.stream().filter(HTMLToken -> HTMLToken.getContentAsString().startsWith("/")).forEach(HTMLToken -> HTMLToken.getContent().insert(0, host));
		
		
		
		List<String> result = links.stream().map(HTMLToken::getContentAsString).collect(Collectors.toList());
		

		return result.toArray(new String[result.size()]);
	}

	public static String filterText(List<HTMLToken> tokens) {
		List<HTMLToken> texts = tokens.stream().filter(HTMLToken -> HTMLToken.getTokenType().equals(TokenType.TEXT))
				.collect(Collectors.toList());

		List<String> result = texts.stream().map(HTMLToken::getContentAsString).collect(Collectors.toList());

		String concat = result.stream().map(String::valueOf).collect(Collectors.joining(" "));

		return concat;
	}

	public static String filterTitle(List<HTMLToken> tokens) {
		
		if (tokens.size() < 5) {
			return "";
		}

		List<HTMLToken> tags = tokens.stream().filter(HTMLToken -> HTMLToken.getTokenType().equals(TokenType.TAG))
				.collect(Collectors.toList());
		
		
		
		Iterator<HTMLToken> firstTokens = tags.iterator();
		while (firstTokens.hasNext()) {
			HTMLToken first = firstTokens.next();
			HTMLToken second = firstTokens.next();
			HTMLToken third = firstTokens.next();
			

			if (first.getContentAsString().contains("html") && second.getContentAsString().contains("head")
					&& third.getContentAsString().contains("title")) {
				
				int index = tokens.indexOf(third) +1;
				
				
				List<HTMLToken> list = new ArrayList<HTMLToken>();
				
				for(int i = index; i < tokens.size(); i++) {
					
					if(tokens.get(i).getContentAsString().contains("/title")) {
						break;
					}
					list.add(tokens.get(i));
					
				}
				
				
				
				String result = list.stream().filter(HTMLToken -> HTMLToken.getTokenType().equals(TokenType.TEXT)).map(HTMLToken -> HTMLToken.getContentAsString()).collect(Collectors.joining(" "));
				
				return result;
				

			}

		}

		return "";
	}
	
}
