// https://mvnrepository.com/artifact/tangrammer/java-net-http-client
@Grapes(
	@Grab(group='tangrammer', module='java-net-http-client', version='0.1.2')
)


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

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
		try {
			//generate JSON request body
			String requestBody = String.format("{\"model\":\"%s\",\"prompt\":\"%s\",\"temperature\":%f}", AI_MODEL_NAME, phrase, randomness);
			
			//configure request
			HttpRequest request = HttpRequest.newBuilder()
					.uri(new URI("https://api.openai.com/v1/completions"))
					.method("POST", BodyPublishers.ofString(requestBody))
					.header("content-type", "application/json")
					.header("Authorization", String.format("Bearer %s", APIKey))
					.build();
			
			//Send request
			HttpClient client = HttpClient.newHttpClient();
			HttpResponse<String> reseponse = client.send(request, BodyHandlers.ofString());
			
			//Read response
			String responseBody = reseponse.body();
			String response;
			
			if (responseBody.indexOf("\"error\"") == 1)
				throw new IOException("OpenAI responded with an error\n" + responseBody);
			
			int start = responseBody.indexOf("\"text\"") + 8;
			response = responseBody.substring(start);
			response = response.substring(0, response.indexOf("\""));
			
			return response;
		} catch (URISyntaxException e) {
		} catch (InterruptedException e) {
		}
		return "";
	}
}