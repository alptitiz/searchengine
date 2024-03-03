package pgdp.searchengine.networking;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.IOException;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ConnectException;
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
		BufferedReader in = null;
		PrintWriter out = null;
		SSLSocket socket = null;
		try {
			SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			socket = (SSLSocket) factory.createSocket(getHost(), port);

		
			socket.startHandshake();

			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

			out.println("GET /" + getPath() + " HTTP/1.1");
			out.println("Host: " + getHost());
			out.println();
			out.flush();

		
			if (out.checkError()) {
				System.out.println("SSLSocketClient:  java.io.PrintWriter error");
			}
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			StringBuffer result = new StringBuffer();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				result.append(inputLine + "\r\n");
			}
			status = new HTTPResponse(result.toString());


		}catch (NumberFormatException e) {
			System.out.println("Port ungültig/keine Zahl, versuche es erneut!");
		} catch (UnknownHostException e) {
			System.out.println("Host unbekannt, versuche es erneut!");
		} catch (BindException e) {
			System.out.println("Port Binding fehlgeschlagen, Prozess läuft bereits?");
		} catch (ConnectException e) {
			System.out.println("Verbindung abgelehnt, versuche es erneut!");
		} catch (IOException e) {
			System.out.println("Ein-/Ausgabefehler, versuche es erneut!");
		}finally {
			try {
				if(in != null) {
				in.close();
				}
				if(out != null) {
				out.close();
				}
				if(socket != null) {
				socket.close();
				}
				}catch(IOException e) {
				 System.err.print("Exception on socket.close()");
				 e.printStackTrace();
			}
			
	
			
			
		}
		
		
		return status;
	}


}
