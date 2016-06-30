package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	
	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
		
		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
		    
		    BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		    String line = "";
		    String url = "";
		    
		    while (!"".equals(line = br.readLine())) {
		      if (line == null) { return; }
		      
		      log.debug(line);
		      
		      if (line.contains("GET")) {
		        url = line.split(" ")[1];
		      }
		    }
		    
		    log.debug("URL: " + url);
		    
		    String id = "";
		    String password = "";
		    String name = "";
		    String email = "";
		    String separator = "&";
		    
		    //회원가입 url을 날렸을경우 
		    if (url.contains("?")) {
		      int index = url.indexOf("?");
		      String requestPath = url.substring(0, index);
		      String params = url.substring(index+1);
		      
		      String[] tokens = params.split(separator);
		      for (String token : tokens) {
		        if (token.contains("userId")) {
		          id = token.split("=")[1];
		        }
		        if (token.contains("password")) {
		          password = token.split("=")[1];
		        }
		        if (token.contains("name")) {
		          name = token.split("=")[1];
		        }
		        if (token.contains("email")) {
		          email = token.split("=")[1];
		        }
		      }
		      model.User user = new model.User(id, password, name, email);
		      
		      log.debug("USER INFORMATION");
		      log.debug(user.getUserId());
		      log.debug(user.getPassword());
		      log.debug(user.getName());
		      log.debug(user.getEmail());
		    }
		    
		    DataOutputStream dos = new DataOutputStream(out);
		    byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
		    
			response200Header(dos, body.length);
			responseBody(dos, body);
			
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}


  private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
	
	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
