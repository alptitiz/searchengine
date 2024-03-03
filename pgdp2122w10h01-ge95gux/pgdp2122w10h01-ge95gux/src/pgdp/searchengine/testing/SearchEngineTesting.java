package pgdp.searchengine.testing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.UnknownHostException;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import pgdp.searchengine.networking.HTMLProcessing;
import pgdp.searchengine.networking.HTMLToken;
import pgdp.searchengine.networking.HTMLToken.TokenType;
import pgdp.searchengine.networking.HTTPRequest;
import pgdp.searchengine.networking.HTTPResponse;

class SearchEngineTesting {
	static HTTPRequest req;
	static HTTPRequest req2;
	static HTTPRequest req3;
	static String htmlShowCar;
	static String htmlTest;
	static String htmlTest2;
	static String htmlTest3;
	static String testText;
	static String testText2;
	static String testText3;
	static String testLinks;
	static String testTitle;
	static String testTitle3;
	static String testTitle4;
	static String testTokenize;
	static String testTitle5;
	
	static String testHTML;
	@BeforeAll
	static void setUp() {
		req = new HTTPRequest("man1.pgdp.sse.in.tum.de", "showchar.1.html");
		req2 = new HTTPRequest("man1.pgdp.sse.in.tum.de", "git-worktree.1.html");
		req3 = new HTTPRequest("man1.pgdp.sse.in.tum.de", "mtoolstest.1.html");
		
		
		testTokenize = "<html>\"<b1>\"</html>";
		htmlShowCar = "<HTML><HEAD><TITLE>Man page of SHOWCHAR</TITLE>\r\n"
				+ "</HEAD><BODY>\r\n"
				+ "<H1>SHOWCHAR</H1>\r\n"
				+ "Section: User Commands  (1)<BR>Updated: PSUtils Release 1 Patchlevel 17<BR><A HREF=\"#index\">Index</A>\r\n"
				+ "<A HREF=\"https://pgdp.sse.in.tum.de/INDEX.html\">Return to Main Contents</A><HR>";
		htmlTest = "<html>\"text<>\"'</HTML>";
		htmlTest2 = "<B><a what is wrong href=\"https://man1.pgdp.sse.in.tum.de/OPEnssL.1.html\" what is wrong>OPEnssl</a></B>(1),\r\n"
				+ "<B><a blalalal href=\"https://man1.PGDP.sse.in.tum.de/'crl.1.html\" blalalbala >CRl</a></B>(1).\"Today IS Monday\"\r\n"
				+ "<B><a href=\"https://man1.pgdp.SSE.in.tum.de/'x509.1.html\">X509</a></B>(1).\"how ARE You\"";
		htmlTest3 = "<br title=\"lbAB\">&nbsp;</A>\r\n" + "<A HREF=\"https://pgdp.sse.in.tum.de/index.html\">Return to Main Contents</A>\r\n" + "<A NAME=\"lbAB\">&nbsp;</A>\r\n" + "<A HREF=\"/cgi-bin/man/man2html\">man2html</A>\r\n" + "<A HREF=\"/super-man/superman/superMan2html\">man2html</A>\r\n";
		testText = "< hello what is your name\r\n" + ">< this is a beautiful day"; 
		testText2 = "<HTML><HEAD><TITLE></TITLE>\r\n"
				+ "</HEAD><BODY>\r\n"
				+ "<H1></H1>";
		testText3 = "<P>\r\n"
				+ "If any directories are named on the command line, then those are\r\n"
				+ "processed in turn. If not, then the <B></B><FONT SIZE=\"-1\"><B>SSL_CERT_DIR</B></FONT><B></B> environment variable\r\n"
				+ "is consulted; this should be a colon-separated list of directories,\r\n"
				+ "like the Unix <B></B><FONT SIZE=\"-1\"><B>PATH</B></FONT><B></B> variable.\r\n"
				+ "If that is not set then the default directory (installation-specific\r\n"
				+ "but often <B>/usr/local/ssl/certs</B>) is processed.\r\n"
				+ "<P>";
		testLinks = "<a href='https://man8.pgdp.sse.in.tum.de/halt.8.html'>";
		testTitle = "<HTML hi how are you><HEAD \"wonderful\"><hi TITLE 'how'>Man<br>page</br>of SHOWCHAR</TITLE>\r\n"
				+ "</HEAD><BODY>\r\n"
				+ "<H1>SHOWCHAR</H1>\r\n";
		testTitle3 =  "<HTML hi how are you><HEAD \"wonderful\"><TITLE></TITLE>\r\n"
				+ "</HEAD><BODY>\r\n"
				+ "<H1>SHOWCHAR</H1>\r\n";
		testHTML = "\"<html>\"text<>\"'</HTML> blablavlabal";
		testTitle4 = "<HTML>what a wonderful day<HEAD>showchar is boring<TITLE>Man page of SHOWCHAR</TITLE>\r\n"
				+ "</HEAD></html>";
		testTitle5 = "<HTML blblblbl>what a wonderful day<HEAD blblblbll>showchar is boring<TITLE blblblblb>Man page of SHOWCHAR</TITLE blblblblbl>\r\n"
				+ "</HEAD></html>";
		
		
	}

	
	
	
	// The upper case letters in a link, should not be converted to lower case.
	// So "INDEX" stays the same in the content of the tag.
	// Other tags names like "a", "html", "body" and "href" is converted to lower case.
	// Also texts like "showchar" and "man page of showchar" are converted to lower case.
	// Tested if the tags and texts are differentiated correctly in the tokenize() so that
	// their Token types are setted correctly.
	// Their content attributes should also be setted accordingly without "<>".
	
	@Test
	void testTokenize() {
		
		String[] content = new String[21];
		content[0] = "html";
		content[1] = "head";
		content[2] = "title";
		content[3] = "man page of showchar";
		content[4] = "/title";
		content[5] = "/head";
		content[6] = "body";
		content[7] = "h1";
		content[8] = "showchar";
		content[9] = "/h1";
		content[10] = "section: user commands  (1)";
		content[11] = "br";
		content[12] = "updated: psutils release 1 patchlevel 17";
		content[13] = "br";
		content[14] = "a href=\"#index\"";
		content[15] = "index";
		content[16] = "/a";
		content[17] = "a href=\"https://pgdp.sse.in.tum.de/INDEX.html\"";
		content[18] = "return to main contents";
		content[19] = "/a";
		content[20] = "hr";
		
		
		List<HTMLToken> token = HTMLProcessing.tokenize(htmlShowCar);
		Iterator<HTMLToken> it1 = token.iterator();
		int i = 0;
		while(it1.hasNext()) {
	
			HTMLToken temp = it1.next();
			
			if(temp.getContentAsString().equals("")) {
				temp = it1.next();
			}
			if(isTag(temp.getContentAsString())) {
				assertEquals(true, TokenType.TAG.equals(temp.getTokenType()));
			}else {
				assertEquals(true, TokenType.TEXT.equals(temp.getTokenType()));
			}
			
			
			assertEquals(true,  content[i].equals(temp.getContentAsString()));
			i++;
		
		}
		
		
		
	}
	
	// Tested if another Tag is created, when "<>" occurs in "". Since there can't be any "<" in "", it must be
	// recognized as a starting tag. "" and '' only plays a role in a tag.
	// Also tested when only one ' comes after closing ", in this case other characters shouldn't be interpreted 
	// as a text and the tag </html> must be recognized.
	// Also "/html" shouldn't be written in upper case, since ' can activate upper case
	// in a wrong way, when it comes only one time.
	// So both "" and '' must only be recognized, when there is a starting " and a ending ".
	@Test
	void testTokenize2() {
		String[] content2 = new String[5];
		content2[0] = "html";
		content2[1] = "\"text";
		content2[2] = "";
		content2[3] = "\"'";
		content2[4] = "/html";
		
		String[] content = new String[5];
		content[0] = "html";
		content[1] = "\"";
		content[2] = "b1";
		content[3] = "\"";
		content[4] = "/html";
		
		List<HTMLToken> token = HTMLProcessing.tokenize(testTokenize);
		Iterator<HTMLToken> it = token.iterator();
		int j = 0;
		while(it.hasNext()) {
			
			HTMLToken temp = it.next();
	
			assertEquals(true,  content[j].equals(temp.getContentAsString()));
			j++;
		
		}
		
		
		
		List<HTMLToken> token2 = HTMLProcessing.tokenize(htmlTest);
		Iterator<HTMLToken> it2 = token2.iterator();
		int k = 0;
		while(it2.hasNext()) {
			
			HTMLToken temp = it2.next();
	
			assertEquals(true,  content2[k].equals(temp.getContentAsString()));
			k++;
		
		}
		
		
		
	}
	
	
	// "" in texts should behave as any other character and therefore all the characters in "how ARE you", must be lower case.
	// Also all the characters inside "Today IS Monday" should be lower case.
	@Test
	void testTokenize3() {
		
		String[] content3 = new String[18];
		content3[0] = "b";
		content3[1] = "a what is wrong href=\"https://man1.pgdp.sse.in.tum.de/OPEnssL.1.html\" what is wrong";
		content3[2] = "openssl";
		content3[3] = "/a";
		content3[4] = "/b";
		content3[5] = "(1),";
		content3[6] = "b";
		content3[7] = "a blalalal href=\"https://man1.PGDP.sse.in.tum.de/'crl.1.html\" blalalbala ";
		content3[8] = "crl";
		content3[9] = "/a";
		content3[10] = "/b";
		content3[11] = "(1).\"today is monday\"";
		content3[12] = "b";
		content3[13] = "a href=\"https://man1.pgdp.SSE.in.tum.de/'x509.1.html\"";
		content3[14] = "x509";
		content3[15] = "/a";
		content3[16] = "/b";
		content3[17] = "(1).\"how are you\"";
		
		
		
		
		List<HTMLToken> token2 = HTMLProcessing.tokenize(htmlTest2);
		Iterator<HTMLToken> it2 = token2.iterator();
		int k = 0;
		while(it2.hasNext()) {
			
			HTMLToken temp = it2.next();
			
			if(temp.getContentAsString().equals("")) {
				temp = it2.next();
			}

			assertEquals(true,  content3[k].equals(temp.getContentAsString()));
			k++;
		
		}
	
		
		
		
	}
	
	
	// filterLinks() should return all the links in correct order according to the html order.
	// Handled <a href="#index">, so that the returned link is valid. Added the given host 
	// at the beginning and a "/" to write it as a path.
	// Other links starting with https:// should be returned without "https://". (they have an different host as the parameter)
	// Also tested when there is no links in the htmlTest string, so returned array must have an length 0.
	@Test
	void testFilterLinks() {
		
		List<HTMLToken> token = HTMLProcessing.tokenize(htmlShowCar);
		String[] links = HTMLProcessing.filterLinks(token, "man1.pgdp.sse.in.tum.de");
		assertEquals(true, "man1.pgdp.sse.in.tum.de/#index".equals(links[0]));
		assertEquals(true, "pgdp.sse.in.tum.de/INDEX.html".equals(links[1]));
	
		
		List<HTMLToken> token2 = HTMLProcessing.tokenize(htmlTest);
		String[] links2 = HTMLProcessing.filterLinks(token2, "man1.pgdp.sse.in.tum.de");
		assertEquals(true, links2.length == 0);
		
		List<HTMLToken> token3 = HTMLProcessing.tokenize(testLinks);
		String[] links3 = HTMLProcessing.filterLinks(token3, "man6.pgdp.sse.in.tum.de");
		assertEquals(true, "man8.pgdp.sse.in.tum.de/halt.8.html".equals(links3[0]));
		
		
		
		
		
		
		
	}
	
	
	// The first 3 links starting with https:// are in correct order.
	// Added more letters in a tag before and after the link, so that filter links recognize
	// the link from it's href attribute, and take the link only inside "".
	// For example in the first line there are words "what is wrong" before and after the link.
	// In the second line there are randomly added words like " blalalbala " before and after the link.
	@Test
	void testFilterLinks2() {
		
		List<HTMLToken> token = HTMLProcessing.tokenize(htmlTest2);
		String[] links = HTMLProcessing.filterLinks(token, "man2.pgdp.sse.in.tum.de");
		assertEquals(true, "man1.pgdp.sse.in.tum.de/OPEnssL.1.html".equals(links[0]));
		assertEquals(true, "man1.PGDP.sse.in.tum.de/'crl.1.html".equals(links[1]));
		assertEquals(true, "man1.pgdp.SSE.in.tum.de/'x509.1.html".equals(links[2]));
		
		
	}
	// First the links without "href" attribute must be ignored, so links starting with "NAME" and "title" are not in the String[]
	// The path "/cgi-bin/man/man2html" and "/super-man/superman/superMan2html"must be added to the host given in the parameter.
	@Test
	void testFilterLinks3() {
		List<HTMLToken> token2 = HTMLProcessing.tokenize(htmlTest3);
		String[] links = HTMLProcessing.filterLinks(token2, "man1.pgdp.sse.in.tum.de");
		assertEquals(true, "pgdp.sse.in.tum.de/index.html".equals(links[0]));
		assertEquals(true, "man1.pgdp.sse.in.tum.de/cgi-bin/man/man2html".equals(links[1]));
		assertEquals(true, "man1.pgdp.sse.in.tum.de/super-man/superman/superMan2html".equals(links[2]));

		
		
	}
	
	// Tested if each text is recognized correctly and there is an empty space between each text content.(There are both tags and texts in htmlShowChar)
	// The text's in tags shouldn't be included in filterText(), so we can only see the contents of the texts.
	@Test
	void testFilterText() {
		List<HTMLToken> token = HTMLProcessing.tokenize(htmlShowCar);
		String text = HTMLProcessing.filterText(token);
		String compare = "man page of showchar showchar section: user commands  (1) updated: psutils release 1 patchlevel 17 index return to main contents";
		assertEquals(true, text.equals(compare));
		
	
	}
	
	// Tested if both texts are recognized correctly, when there is no tags and texts start with "<" or "><".
	// Also tested when there is no text in the given html and just tags, so an empty string is returned from filterText()
	@Test
	void testFilterText2() {
		List<HTMLToken> token = HTMLProcessing.tokenize(testText);
		String text = HTMLProcessing.filterText(token);
		String compare = "< hello what is your name >< this is a beautiful day";
		assertEquals(true, text.equals(compare));
		
		List<HTMLToken> token2 = HTMLProcessing.tokenize(testText2);
		String text2 = HTMLProcessing.filterText(token2);
		String compare2 = "";
		assertEquals(true, text2.equals(compare2));
			
	}
	
	// Tested filterText() with longer texts, which are interrupted by more tags.
	// When there is a tag between texts there should be an empty space between the texts and the tags must be excluded.
	@Test
	void testFilterText3() {
		List<HTMLToken> token = HTMLProcessing.tokenize(testText3);
		String text = HTMLProcessing.filterText(token);
		String compare = "if any directories are named on the command line, then those are processed in turn. if not, then the  ssl_cert_dir  environment variable is consulted; this should be a colon-separated list of directories, like the unix  path  variable. if that is not set then the default directory (installation-specific but often  /usr/local/ssl/certs ) is processed.";
		assertEquals(true, text.equals(compare));
		
		
	}
	
	// Testing filterTitle() with an title between the tags <title> and </title>, which are directly an child of <head> and <html> at the top.
	// tested the same title "man page of showchar" but this time there are also texts between the tags <html>, <head> and <title>
	// so filterTitle() should return "man page of showchar" in both cases.
	// Also tested when there are tags between <title> and </title>, to test if the text is correctly extracted.
	@Test
	void testFilterTitle() {
		List<HTMLToken> token = HTMLProcessing.tokenize(htmlShowCar);
		List<HTMLToken> token3 = HTMLProcessing.tokenize(testTitle4);
		String text3 = HTMLProcessing.filterTitle(token3);
	
		String text = HTMLProcessing.filterTitle(token);
		String compare = "man page of showchar";
		assertEquals(true, text.equals(compare));
		assertEquals(true, text3.equals(compare));
		
		
		
		
		List<HTMLToken> token2 = HTMLProcessing.tokenize(testTitle);
		String text2 = HTMLProcessing.filterTitle(token2);
		String compare2 = "man page of showchar";
		assertEquals(true, text2.equals(compare2));
		
		
	
	}
	
	// First tested when there is no text between <title> and </title>, so "" must be returned.
	// Also tested if the title of an document in the address "https://man1.pgdp.sse.in.tum.de/git-worktree.1.html", is 
	// returned correctly.

	@Test
	void testFilterTitle2() {
		
		List<HTMLToken> token2 = HTMLProcessing.tokenize(testTitle3);
		String text2 = HTMLProcessing.filterTitle(token2);
		String compare2 = "";
		assertEquals(true, text2.equals(compare2));
		
		
		HTTPResponse response = null;
		try {
			response = req2.send(443);
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		List<HTMLToken> token = HTMLProcessing.tokenize(response.getHtml());
		String text = HTMLProcessing.filterTitle(token);
		String compare = "man page of git-worktree";
		assertEquals(true, text.equals(compare));
		
	}	
	
	// This time tested if the title of an document in the address "https://man1.pgdp.sse.in.tum.de/mtoolstest.1.html", is 
	// returned correctly.
	// Not just the starting <html> but also <head + attributes> and <title + attributes> can have it's own attributes.
	// In this case the title must also be recognized. (testTitle5)
	@Test
	void testFilterTitle3() {
		
		List<HTMLToken> token2 = HTMLProcessing.tokenize(testTitle5);
		String text2 = HTMLProcessing.filterTitle(token2);
		
		String compare2 = "man page of showchar";
		assertEquals(true, text2.equals(compare2));
		
		
		HTTPResponse response = null;
		try {
			response = req3.send(443);
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		List<HTMLToken> token = HTMLProcessing.tokenize(response.getHtml());
		String text = HTMLProcessing.filterTitle(token);
		String compare = "man page of mtoolstest";
		assertEquals(true, text.equals(compare));
		
	}	
	
	
	
	
	
	
	// Just to use it in testTokenize(), where it is clear that tags start with strings given in the if statement.
	public boolean isTag(String temp) {
		
		if(temp.equals("b") || temp.equals("/b") || temp.equals("html") || temp.equals("/html")|| temp.equals("a") || temp.equals("br") || temp.equals("/a") || temp.equals("/br") || temp.equals("title") || temp.equals("/title") || temp.equals("head") || temp.equals("/head") || temp.equals("body") || temp.equals("/body") || temp.equals("h1") || temp.equals("/h1") || temp.equals("hr") || temp.contains("a href")) {
			return true;
		}
		
		return false;
		
		
	}
	
	
	
	
}
