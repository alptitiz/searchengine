package pgdp.searchengine.networking;

import java.io.IOException;

import java.util.List;

import pgdp.searchengine.pagerepository.AbstractLinkedDocument;
import pgdp.searchengine.pagerepository.DummyLinkedDocument;
import pgdp.searchengine.pagerepository.LinkedDocument;
import pgdp.searchengine.pagerepository.LinkedDocumentCollection;
import pgdp.searchengine.util.Date;

public final class PageCrawling {

	// Another useless constructor for SCA
	private PageCrawling() {

	}

	public static void crawlPages(LinkedDocumentCollection collection, int number) {

		int amountOfCrawled = 0;

		while (amountOfCrawled != number) {
			String address = collection.getNextUncrawledAddress();
			if (crawlPage(collection, address)) {
				amountOfCrawled++;
			} else {
				AbstractLinkedDocument doc = collection.find(address);
				collection.removeDummy((DummyLinkedDocument) doc);
			}

		}

	}

	public static void crawlPages(LinkedDocumentCollection collection, int number, String startingAddress) {
		int amountOfCrawled = 0;
		if (crawlPage(collection, startingAddress)) {
			amountOfCrawled++;
		} else {
			AbstractLinkedDocument doc = collection.find(startingAddress);
			collection.removeDummy((DummyLinkedDocument) doc);
		}
		while (amountOfCrawled != number) {
			String address = collection.getNextUncrawledAddress();
			if (crawlPage(collection, address)) {
				amountOfCrawled++;
			} else {
				AbstractLinkedDocument doc = collection.find(address);
				collection.removeDummy((DummyLinkedDocument) doc);
			}

		}

	}

	public static boolean crawlPage(LinkedDocumentCollection collection, String address) {

		String[] hostAndPath = getLinkAndPath(address);
		HTTPRequest req = new HTTPRequest(hostAndPath[0], hostAndPath[1]);
		HTTPResponse response = null;
		try {
			response = req.send(443);
		} catch (IOException e) {

			e.printStackTrace();
			return false;
		}
		if (response.getStatus().equals(HTTPStatus.OK)) {
			List<HTMLToken> token = HTMLProcessing.tokenize(response.getHtml());
			String[] links = HTMLProcessing.filterLinks(token, hostAndPath[0]);
			String content = HTMLProcessing.filterText(token);
			String title = HTMLProcessing.filterTitle(token);
			LinkedDocument temp = new LinkedDocument(title, "", content, new Date(1, 1, 2002), null, address, 10);
			if (collection.addToResultCollection(temp, links)) {
				return true;
			}

		} else {
			return false;
		}

		return false;
	}

	public static String[] getLinkAndPath(String address) {
		StringBuilder add = new StringBuilder();
		StringBuilder path = new StringBuilder();
		boolean pathStarts = false;
		for (int i = 0; i < address.length(); i++) {
			if (pathStarts) {
				path.append(address.charAt(i));
			}

			else if (address.charAt(i) == '/') {
				pathStarts = true;
				path.append(address.charAt(i));
			}

			if (!pathStarts) {
				add.append(address.charAt(i));
			}

		}

		String[] result = new String[2];
		result[0] = add.toString();
		result[1] = path.toString();

		return result;

	}

	// -------- main() zum Testen -------- //

	public static void main(String... args) {
		String host = "man7.pgdp.sse.in.tum.de";
		String path = "iso_8859_1.7.html";

		LinkedDocumentCollection ldc = new LinkedDocumentCollection(1000);
		crawlPages(ldc, 5, host + "/" + path);

		crawlPage(ldc, "man1.pgdp.sse.in.tum.de/showchar.1.html");

	}

}
