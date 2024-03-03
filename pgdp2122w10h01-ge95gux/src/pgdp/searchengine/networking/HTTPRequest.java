package pgdp.searchengine.networking;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.IOException;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class HTTPRequest {
	private String host;
	private String path;

	public HTTPRequest(String host, String path) {
		this.host = host;
		this.path = path;
	}

	public String getHost() {
		return host;
	}

	public String getPath() {
		return path;
	}

	public HTTPResponse send(int port) throws UnknownHostException, IOException {
		HTTPResponse status = null;
		try {
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket = (SSLSocket) factory.createSocket(getHost(), port);

			/*
			 * send http request
			 *
			 * Before any application data is sent or received, the SSL socket will do SSL
			 * handshaking first to set up the security attributes.
			 *
			 * SSL handshaking can be initiated by either flushing data down the pipe, or by
			 * starting the handshaking by hand.
			 *
			 * Handshaking is started manually in this example because PrintWriter catches
			 * all IOExceptions (including SSLExceptions), sets an internal error flag, and
			 * then returns without rethrowing the exception.
			 *
			 * Unfortunately, this means any error messages are lost, which caused lots of
			 * confusion for others using this code. The only way to tell there was an error
			 * is to call PrintWriter.checkError().
			 */
			socket.startHandshake();

			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

			out.println("GET " + getPath() + " HTTP/1.1");
			out.println("Host: " + getHost());
			out.println();
			out.flush();

			/*
			 * Make sure there were no surprises
			 */
			if (out.checkError()) {
				System.out.println("SSLSocketClient:  java.io.PrintWriter error");
			}
			/* read response */
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			StringBuffer result = new StringBuffer();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				result.append(inputLine + "\r\n");
			}
			status = new HTTPResponse(result.toString());

			in.close();
			out.close();
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		HTTPRequest req = new HTTPRequest("man1.pgdp.sse.in.tum.de", "/showchar.1.html");
		HTTPResponse response = req.send(443);
		System.out.println(response.getHtml());
		// response.setHtml(
		// "<a href='https://man8.pgdp.sse.in.tum.de/halt.8.html'>Return to Main
		// Contents</a> <a href='/halt.8.html'> in sibirien ist es recht kühl<br>schrank
		// und schreibtisch stehen im zimmer");
		// List<HTMLToken> token = HTMLProcessing.tokenize(response.getHtml());

	}

}
