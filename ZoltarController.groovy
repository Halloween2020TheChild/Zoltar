
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
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

import javax.net.ssl.SSLContext
import org.apache.commons.io.IOUtils
import org.apache.http.HttpEntity
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.config.Registry
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.BasicHttpClientConnectionManager
import org.apache.http.ssl.SSLContexts
import org.apache.http.ssl.TrustStrategy
import org.apache.http.util.EntityUtils

import com.neuronrobotics.bowlerstudio.BowlerStudio
import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import com.squareup.okhttp.Response


public class GPTInterface {

	public final String AI_MODEL_NAME = "davinci";

	private String API_KEY;
	private static final String CHATGPT_API_URL = "https://api.openai.com/v1/chat/completions";

	//
	//	final TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
	//		@Override
	//		public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
	//			return true;
	//		}
	//
	//	};
	//	final SSLContext sslContext = SSLContexts.custom()
	//	.loadTrustMaterial(null, acceptingTrustStrategy)
	//	.build();
	//	final SSLConnectionSocketFactory sslsf =
	//	new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
	//	final Registry<ConnectionSocketFactory> socketFactoryRegistry =
	//	RegistryBuilder.<ConnectionSocketFactory> create()
	//	.register("https", sslsf)
	//	.register("http", new PlainConnectionSocketFactory())
	//	.build();
	//
	//	final BasicHttpClientConnectionManager connectionManager =
	//	new BasicHttpClientConnectionManager(socketFactoryRegistry);
	public GPTInterface(String APIKey) {
		this.API_KEY = APIKey;
	}

	public String request(String phrase) throws IOException {
		return request(phrase, 0.7f);
	}

	public String request(String phrase, float randomness) throws IOException {
		String requestBody = String.format("{\"model\":\"%s\",\"prompt\":\"%s\",\"temperature\":%f}", AI_MODEL_NAME, phrase, randomness);
		
		OkHttpClient client = new OkHttpClient()

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestBody);

		Request request = new Request.Builder()
				.url(CHATGPT_API_URL)
				.method("POST", body)
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization", "Bearer " + API_KEY)
				.build();

		try {
			Response response = client.newCall(request).execute();
			return response.body().string();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
String keyLocation = ScriptingEngine.getWorkspace().getAbsolutePath()+"/gpt-key.txt"
println "Loading API key from "+keyLocation
String content = new String(Files.readAllBytes(Paths.get(keyLocation)));
println "API key: "+content
GPTInterface gpt = new GPTInterface(content)
String response  = gpt.request("Say this is a test")
println "\n\nResponse\n"+response
//BowlerStudio.speak(response)





