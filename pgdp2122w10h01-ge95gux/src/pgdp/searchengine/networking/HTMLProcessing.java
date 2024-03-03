package pgdp.searchengine.networking;

import java.util.*;
import java.util.stream.Collectors;

import pgdp.searchengine.networking.HTMLToken.TokenType;

public final class HTMLProcessing {

	// Useless constructor for SCA
	private HTMLProcessing() {

	}

	public static List<HTMLToken> tokenize(String rawHTML) {
		List<HTMLToken> tokens = new ArrayList();
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
		List<HTMLToken> tags = new ArrayList();

		for (int i = 0; i < temp.length(); i++) {
			if (temp.charAt(i) == '"') {
				upperCaseAlert = true;
			}
			if (temp.charAt(i) == '\'') {
				upperCaseAlert2 = true;
			}

			if (!starts && !alreadyFind) {
				List<HTMLToken> text = findText(temp.substring(i));
				if (!text.isEmpty()) {
					tags.addAll(text);
					alreadyFind = true;
				}

			}

			if (!upperCaseAlert && !upperCaseAlert2 && temp.charAt(i) == '>') {

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

			if (temp.charAt(i) == '<') {
				starts = true;
			}
			if (upperCaseAlert && i < temp.length() - 1 && temp.charAt(i + 1) == '"') {
				token.addCharacter(temp.charAt(i + 1));
				i = i + 1;
				upperCaseAlert = false;
			}
			if (upperCaseAlert2 && i < temp.length() - 1 && temp.charAt(i + 1) == '\'') {
				token.addCharacter(temp.charAt(i + 1));
				i = i + 1;
				upperCaseAlert2 = false;
			}

		}

		return tags;
	}

	public static List<HTMLToken> findText(String temp) {
		HTMLToken token = new HTMLToken(TokenType.TEXT);
		boolean starts = false;
		List<HTMLToken> tags = new ArrayList();

		for (int i = 0; i < temp.length(); i++) {
			if (temp.charAt(i) == '<') {
				starts = true;
			}
			if (!starts) {
				token.addCharacter(Character.toLowerCase(temp.charAt(i)));
			}
			if (temp.charAt(i) == '>') {
				if (token.getContentAsString().length() != 0) {

					tags.add(token);
				}
				starts = false;
				return tags;
			}

			if (i == temp.length() - 1 && token.getContentAsString().length() != 0) {
				tags.add(token);
			}

		}

		return tags;
	}

	public static String[] filterLinks(List<HTMLToken> tokens, String host) {
		List<HTMLToken> tags = tokens.stream().filter(HTMLToken -> HTMLToken.getTokenType().equals(TokenType.TAG))
				.collect(Collectors.toList());
		List<HTMLToken> links = tags.stream().filter(HTMLToken -> HTMLToken.getContentAsString().startsWith("a href="))
				.collect(Collectors.toList());
		List<String> result = links.stream().map(HTMLToken::getContentAsString).collect(Collectors.toList());
		List<String> result2 = result.stream().map(String -> String.replace("a href=", ""))
				.collect(Collectors.toList());
		List<String> result3 = result2.stream().map(String -> String.replace("\"", "")).collect(Collectors.toList());
		List<String> result4 = result3.stream().map(String -> String.replace("'", "")).collect(Collectors.toList());
		List<String> result5 = result4.stream().filter(String -> String.startsWith("https://"))
				.collect(Collectors.toList());
		List<String> path = result4.stream().filter(String -> !String.startsWith("https://") && !String.startsWith("#"))
				.collect(Collectors.toList());
		List<String> path2 = result4.stream().filter(String -> String.startsWith("#")).collect(Collectors.toList());
		List<String> pathEdit = path2.stream().map(String -> "/" + String).collect(Collectors.toList());
		path.addAll(pathEdit);
		List<String> path3 = path.stream().map(String -> host + String).collect(Collectors.toList());
		List<String> result6 = result5.stream().map(String -> String.replace("https://", ""))
				.collect(Collectors.toList());
		result6.addAll(path3);
		List<String> result7 = result6.stream().map(String -> String.replace("\"", "")).collect(Collectors.toList());

		return result7.toArray(new String[result7.size()]);
	}

	public static String filterText(List<HTMLToken> tokens) {
		List<HTMLToken> texts = tokens.stream().filter(HTMLToken -> HTMLToken.getTokenType().equals(TokenType.TEXT))
				.collect(Collectors.toList());

		List<String> result = texts.stream().map(HTMLToken::getContentAsString).collect(Collectors.toList());

		String concat = result.stream().map(String::valueOf).collect(Collectors.joining(" "));

		return concat;
	}

	public static String filterTitle(List<HTMLToken> tokens) {
		List<HTMLToken> firstLine = new ArrayList();
		if (tokens.size() < 5) {
			return "";
		}

		firstLine.add(tokens.get(0));
		firstLine.add(tokens.get(1));
		firstLine.add(tokens.get(2));
		firstLine.add(tokens.get(3));

		Iterator<HTMLToken> firstTokens = firstLine.iterator();
		while (firstTokens.hasNext()) {
			HTMLToken first = firstTokens.next();
			HTMLToken second = firstTokens.next();
			HTMLToken third = firstTokens.next();
			HTMLToken fourth = firstTokens.next();

			if (first.getContentAsString().equals("html") && second.getContentAsString().equals("head")
					&& third.getContentAsString().equals("title")) {

				return fourth.getContentAsString();

			}

		}

		return "";
	}

}
