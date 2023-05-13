// https://mvnrepository.com/artifact/tangrammer/java-net-http-client

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpRequest.BodyPublishers;
//import java.net.http.HttpResponse;
//import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files
import java.nio.file.Paths

import org.apache.commons.io.IOUtils
import org.apache.http.HttpRequest
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

import com.neuronrobotics.bowlerstudio.BowlerStudio
import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine

public class GPTInterface {
	
	public final String AI_MODEL_NAME = "davinci";
	
	private String APIKey;
	
	public GPTInterface(String APIKey) {
		this.APIKey = APIKey;
	}
	
	public String request(String phrase) throws IOException {
		return request(phrase, 0.7f);
	}
	
	public String request(String phrase, float randomness) throws IOException {

			//generate JSON request body
			String requestBody = String.format("{\"model\":\"%s\",\"prompt\":\"%s\",\"temperature\":%f}", AI_MODEL_NAME, phrase, randomness);
			
			//configure request
//			HttpRequest request = HttpRequest.newBuilder()
//					.uri(new URI("https://api.openai.com/v1/completions"))
//					.method("POST", BodyPublishers.ofString(requestBody))
//					.header("content-type", "application/json")
//					.header("Authorization", String.format("Bearer %s", APIKey))
//					.build();
			HttpPost post = new HttpPost("https://api.openai.com/v1/completions");
			post.setEntity(new StringEntity(requestBody));
			post.addHeader("content-type", "application/json")
			post.addHeader("Authorization", String.format("Bearer %s", APIKey))
			
			try  {
				CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = httpClient.execute(post);
				
			   InputStream responseGetEntityGetContent = response.getEntity().getContent();
			   return IOUtils.toString(responseGetEntityGetContent, StandardCharsets.UTF_8.name());;
		   }catch(Throwable t) {
			   BowlerStudio.printStackTrace(t)
			   return "";
		   }
   
//		   return result;
			
//			//Send request
//			HttpClient client = HttpClient.newHttpClient();
//			HttpResponse<String> reseponse = client.send(request, BodyHandlers.ofString());
//			
//			//Read response
//			String responseBody = reseponse.body();
//			String response;
//			
//			if (responseBody.indexOf("\"error\"") == 1)
//				throw new IOException("OpenAI responded with an error\n" + responseBody);
//			
//			int start = responseBody.indexOf("\"text\"") + 8;
//			response = responseBody.substring(start);
//			response = response.substring(0, response.indexOf("\""));
//			
//			return response;

		return "";
	}
}

String content = new String(Files.readAllBytes(Paths.get(ScriptingEngine.getWorkspace().getAbsolutePath()+"/gpt-key.txt")));

GPTInterface gpt = new GPTInterface(content)
String response  = gpt.request("What is my fate?")
println response
BowlerStudio.speak(response)





