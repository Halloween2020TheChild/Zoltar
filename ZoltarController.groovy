
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets

import java.nio.file.Files
import java.nio.file.Paths

import com.neuronrobotics.bowlerstudio.BowlerKernel
import com.neuronrobotics.bowlerstudio.BowlerStudio
import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import com.squareup.okhttp.Response
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class GPTInterface {

	public final String AI_MODEL_NAME = "gpt-3.5-turbo";

	private String API_KEY;
	private static final String CHATGPT_API_URL = "https://api.openai.com/v1/chat/completions";
	Type TT_mapStringString = new TypeToken<HashMap<String, Object>>() {}.getType();
	Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
	int maxSize = 240
	
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
	/*
	 * '{
     "model": "gpt-3.5-turbo",
     "messages": [{"role": "user", "content": "Say this is a test!"}],
     "temperature": 0.7
   }'
	 */
	public String request(String phrase, float randomness) throws IOException {
		if(Math.random()>0.5)
			phrase="Pretend you are an inspirational Fortuine teller. Keep your response less than "+(maxSize*0.5)+" charectors. As a Fortuine teller respond to: "+phrase
		else
			phrase="Pretend you are an mean joke telling Fortuine teller. Keep your response less than "+(maxSize*0.5)+" charectors. As a Fortuine teller make a joke response to: "+phrase
			
		String requestBody = String.format("{\"model\":\"%s\",\"messages\":\"%s\",\"temperature\":%f}", AI_MODEL_NAME, phrase, randomness);
		HashMap<String,Object> message = new HashMap(); 
		HashMap<String,String> messages = new HashMap();
		messages.put("role", "user")
		messages.put("content", phrase)
		message.put("model", AI_MODEL_NAME)
		message.put("temperature", randomness)
		message.put("messages", Arrays.asList(messages))
		
		requestBody = gson.toJson(message, TT_mapStringString);
		
		
		println requestBody
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
			String jsonString = response.body().string();
			HashMap<String, Object> database = gson.fromJson(jsonString, TT_mapStringString);
			ArrayList<Object> choices = database.get("choices")
			HashMap<String, Object> firstChoice = choices.get(0)
			HashMap<String, Object> messageContent = firstChoice.get("message")
			String ret = messageContent.get("content").toString()
			println ret
			if(ret.length()>maxSize)
				ret=ret.substring(0, maxSize)
			return ret
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

String keyLocation = ScriptingEngine.getWorkspace().getAbsolutePath()+"/gpt-key.txt"
if(!new File(keyLocation).exists()) {
	
}
println "Loading API key from "+keyLocation
String content = new String(Files.readAllBytes(Paths.get(keyLocation)));
println "API key: "+content
GPTInterface gpt = new GPTInterface(content)
String response  = gpt.request("What is my fortune?",1)
println "\n\nResponse\n"+response
BowlerKernel.speak(response, 150, 0, 100, 1.0, 1.0)





