@Grab(group='net.java.dev.jna', module='jna', version='5.7.0')
@Grab(group='com.alphacephei', module='vosk', version='0.3.45')

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine
import javax.sound.sampled.SourceDataLine
import javax.sound.sampled.TargetDataLine
import javax.sound.sampled.UnsupportedAudioFileException;

import org.vosk.LogLevel;
import org.vosk.Recognizer;

import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine

import org.vosk.LibVosk;
import org.vosk.Model;

import java.applet.AudioClip
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets

import java.nio.file.Files
import java.nio.file.Paths
import java.util.logging.Level
import java.util.logging.Logger

import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.LineEvent
import javax.sound.sampled.LineListener

import com.neuronrobotics.bowlerstudio.AudioPlayer
import com.neuronrobotics.bowlerstudio.AudioStatus
import com.neuronrobotics.bowlerstudio.ISpeakingProgress
import com.neuronrobotics.bowlerstudio.creature.MobileBaseCadManager
import com.neuronrobotics.bowlerstudio.creature.MobileBaseLoader
import com.neuronrobotics.bowlerstudio.BowlerKernel
import com.neuronrobotics.bowlerstudio.BowlerStudio
import com.neuronrobotics.sdk.addons.kinematics.AbstractLink
import com.neuronrobotics.sdk.addons.kinematics.DHParameterKinematics
import com.neuronrobotics.sdk.addons.kinematics.MobileBase
import com.neuronrobotics.sdk.addons.kinematics.math.RotationNR
import com.neuronrobotics.sdk.addons.kinematics.math.TransformNR
import com.neuronrobotics.sdk.common.DeviceManager
import com.neuronrobotics.sdk.util.ThreadUtil
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import com.squareup.okhttp.Response

import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.TextInputDialog
import marytts.LocalMaryInterface
import marytts.MaryInterface
import marytts.datatypes.MaryData
import marytts.exceptions.SynthesisException

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
boolean regen=false;
MobileBase base=DeviceManager.getSpecificDevice( "Standard6dof",{
	//If the device does not exist, prompt for the connection

	MobileBase m = MobileBaseLoader.fromGit(
			"https://github.com/Halloween2020TheChild/GroguMechanicsCad.git",
			"hephaestus.xml"
			)
	if(m==null)
		throw new RuntimeException("Arm failed to assemble itself")
	println "Connecting new device robot arm "+m
	regen=true;
	return m
})
if(regen) {
	MobileBaseCadManager get = MobileBaseCadManager.get( base)
	get.setConfigurationViewerMode(false)
	get.generateCad()
	Thread.sleep(100);
	while(get.getProcesIndictor().get()<1){
		println "Waiting for cad to get to 1:"+get.getProcesIndictor().get()
		ThreadUtil.wait(1000)
	}
}
DHParameterKinematics arm = base.getAllDHChains().get(0);
MobileBase head = arm.getSlaveMobileBase(5)
AbstractLink mouth =head.getAllDHChains().get(0).getAbstractLink(0)

public class GPTInterface {
	Alert a;
	public final String AI_MODEL_NAME = "gpt-3.5-turbo";

	private String API_KEY;
	private static final String CHATGPT_API_URL = "https://api.openai.com/v1/chat/completions";
	Type TT_mapStringString = new TypeToken<HashMap<String, Object>>() {}.getType();
	Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
	int maxSize = 240
	AudioStatus status;
	AudioStatus laststatus



	AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 60000, 16, 2, 4, 44100, false);
	DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
	TargetDataLine microphone;
	SourceDataLine speakers;
	// model downloaded from https://alphacephei.com/vosk/models
	//Model model = new Model(ScriptingEngine.getWorkspace().getAbsolutePath()+"/vosk-model-en-us-0.22/");
	//Model model = new Model(ScriptingEngine.getWorkspace().getAbsolutePath()+"/vosk-model-en-us-daanzu-20200905-lgraph/");
	Model model = new Model(ScriptingEngine.getWorkspace().getAbsolutePath()+"/vosk-model-en-us-daanzu-20200905/");

	Recognizer recognizer = new Recognizer(model, 120000)


	public GPTInterface(String APIKey) {
		this.API_KEY = APIKey;
		LibVosk.setLogLevel(LogLevel.DEBUG);
	}

	public String request(String phrase) throws IOException {
		return request(phrase, 0.7f);
	}

	public String promptFromMicrophone() {
		microphone = (TargetDataLine) AudioSystem.getLine(info);
		microphone.open(format);
		microphone.start();

		//ByteArrayOutputStream out = new ByteArrayOutputStream();
		int numBytesRead;
		int CHUNK_SIZE = 1024;
		int bytesRead = 0;

		//DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
		//speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
		//speakers.open(format);
		//speakers.start();
		byte[] b = new byte[4096];
		println "Listening..."
		String result=null;
		long start = System.currentTimeMillis()
		Type STTType = new TypeToken<HashMap<String, String>>() {}.getType();
		try{
			while (((System.currentTimeMillis()-start)<30000) && !Thread.interrupted()) {
				Thread.sleep(1);
				numBytesRead = microphone.read(b, 0, CHUNK_SIZE);
				bytesRead += numBytesRead;

				//out.write(b, 0, numBytesRead);

				//speakers.write(b, 0, numBytesRead);

				if (recognizer.acceptWaveForm(b, numBytesRead)) {
					result=recognizer.getResult()
					HashMap<String, String> db = gson.fromJson(result, STTType);
					result = db.get("text")
					if(result.length()>2)
						break;
					else {
						println "Listening..."
					}
				} else {
					//System.out.println(recognizer.getPartialResult());
				}
			}
		}catch(Throwable t){
			t.printStackTrace()
		}
		System.out.println(result);
		//speakers.drain();
		//speakers.close();
		microphone.close();

		if(result==null)
			result="What is my fortune?"
		return result;
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
			phrase="Pretend you are a fortuine teller that only gives bad fortunes. Keep your response less than "+(maxSize*0.5)+" characters. Respond to: "+phrase
		else
			phrase="Pretend you are a Fortune teller that gives good fortunies. Keep your response less than "+(maxSize*0.5)+" charecters. Respond to: "+phrase

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
			HashMap<String, Object> firstChoice = choices.get(choices.size()-1)
			HashMap<String, Object> messageContent = firstChoice.get("message")
			String ret = messageContent.get("content").toString()
			println ret

			return ret
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

String keyLocation = ScriptingEngine.getWorkspace().getAbsolutePath()+"/gpt-key.txt"
if(!new File(keyLocation).exists()) {
	BowlerStudio.runLater({
		TextInputDialog dialog = new TextInputDialog("your OpenAI API Key here");
		dialog.setTitle("Enter your OpenAI Key");
		dialog.setHeaderText("Create key here - https://platform.openai.com/account/api-keys");
		dialog.setContentText("Please enter your key:");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			String resultGet = result.get()
			System.out.println("Your key: " + resultGet);
			new Thread({
				try {
					File myObj = new File(keyLocation);
					if (myObj.createNewFile()) {
						System.out.println("File created: " + myObj.getName());
					} else {
						System.out.println("File already exists.");
					}
					FileWriter myWriter = new FileWriter(keyLocation);
					myWriter.write(resultGet);
					myWriter.close();
					System.out.println("Successfully wrote key to your local file.");
				} catch (IOException e) {
					System.out.println("An error occurred.");
					e.printStackTrace();
				}


			}).start()
		}

	})
	return;
}
def fixVector(double[] jointSpaceVect,DHParameterKinematics arm ) {
	for (int i = 0; i < 6; i++) {
		AbstractLink link = arm.factory.getLink(arm.getLinkConfiguration(i));
		double val = jointSpaceVect[i];
		Double double1 = new Double(val);
		if(double1.isNaN() ||double1.isInfinite() ) {
			jointSpaceVect[i]=0;
		}
		if (val > link.getMaxEngineeringUnits()) {
			jointSpaceVect[i]=link.getMaxEngineeringUnits()-Double.MIN_VALUE;
			//println "Link "+i+" u-limit "+jointSpaceVect[i]
		}
		if (val < link.getMinEngineeringUnits()) {
			jointSpaceVect[i]=link.getMinEngineeringUnits()+Double.MIN_VALUE;
			//println "Link "+i+" l-limit "+jointSpaceVect[i]
		}
	}
}
println "Loading API key from "+keyLocation
String content = new String(Files.readAllBytes(Paths.get(keyLocation)));
println "API key: "+content
GPTInterface gpt = new GPTInterface(content)
running =true
response=null
msLoop=16
indexAnimationLoop=0
numStepsPerLoop=2000/msLoop
enum AnimationMode{
	spiritWorld,
	facetrack
}

AnimationMode mode =AnimationMode.facetrack;
new Thread({

	while(running) {
		Thread.sleep(msLoop)
		if(gpt.status != gpt.laststatus) {
			gpt.laststatus=gpt.status;
			boolean isMouthOpen = (gpt.laststatus==AudioStatus.attack)
			mouth.setTargetEngineeringUnits(isMouthOpen?-20.0:0);
			mouth.flush(0);
		}
		double unitVextorOfNow=((double) indexAnimationLoop)/((double) numStepsPerLoop)
		double sinVal = Math.sin(unitVextorOfNow*Math.PI*2)
		double cosVal = Math.cos(unitVextorOfNow*Math.PI*2)

		if(mode ==AnimationMode.facetrack) {
			sinVal=0
			cosVal=0
		}
		TransformNR changed=new TransformNR()
		changed.setX(170+(30))


		def headRnage=30
		def analogy = 0
		def analogz = 35
		changed.setZ(200+analogz*cosVal)
		changed.setY(analogy)
		def analogup = sinVal*headRnage *1.5

		changed.setRotation(new RotationNR(0,179.96+analogup,-50.79))
		TransformNR tilted= new TransformNR(0,0,0, RotationNR.getRotationZ(-90))
		changed=changed.times(tilted)

		double[] jointSpaceVect = arm.inverseKinematics(arm.inverseOffset(changed));

		fixVector(jointSpaceVect,arm)
		double bestsecs = arm.getBestTime(jointSpaceVect);
		double normalsecs = ((double)msLoop)/1000.0
		def vect;
		if(bestsecs>normalsecs) {
			double percentpossible = normalsecs/bestsecs*2

			TransformNR starttr=arm.getCurrentTaskSpaceTransform()
			TransformNR delta = starttr.inverse().times(changed);
			TransformNR scaled = delta.scale(percentpossible)
			TransformNR newTR= starttr.times(scaled)
			vect = arm.inverseKinematics(arm.inverseOffset(newTR));
			fixVector(vect,arm)
			TransformNR finaltr= arm.forwardOffset( arm.forwardKinematics(vect))
			if(!arm.checkTaskSpaceTransform(finaltr)) {
				println "\n\npercentage "+percentpossible
				println "Speed capped\t"+jointSpaceVect
				println "to\t\t\t"+vect
				println "changed"+changed
				println "starttr"+starttr
				println "delta"+delta
				println "scaled"+scaled
				println "newTR"+newTR
				println "ERROR, cant get to "+newTR
				//continue;
			}
		}else
			vect = jointSpaceVect
		msActual=normalsecs*1000
		try {
			//vect[6]=trig;
		}catch(Throwable t) {
			//BowlerStudio.printStackTrace(t)
		}
		arm.setDesiredJointSpaceVector(vect, normalsecs);

		indexAnimationLoop+=1;
		if(indexAnimationLoop>=numStepsPerLoop) {
			indexAnimationLoop=0;
		}
	}
	println "Zoltar animation thread exit clean"
}).start()
try {
	ISpeakingProgress sp ={double percent,AudioStatus status->
		if(status==AudioStatus.release||status==AudioStatus.sustain)
			return
		gpt.status=status;
	}

	AudioPlayer.setThreshhold(600/65535.0)
	AudioPlayer.setLowerThreshhold(100/65535.0)
	double voice =300
	double echo = 0.85
	mode =AnimationMode.facetrack
	BowlerKernel.speak("What do you wish to know?", 100, 0, voice, 1, 1.0,sp)
	String prompt = gpt.promptFromMicrophone();
	mode =AnimationMode.spiritWorld
	Thread initialPrompt=new Thread({
		BowlerKernel.speak("Spirit World! Answer Me! "+prompt, 200, 0, voice, echo, 1.0,sp)
	})
	initialPrompt.start()
	response  = gpt.request(prompt,0.9)
	mode =AnimationMode.facetrack
	println "\n\nResponse\n"+response
	initialPrompt.join()
	BowlerKernel.speak(response, 100, 0, voice, echo, 1.0,sp)
}catch(Throwable t) {
	t.printStackTrace()
}
running=false
mouth.setTargetEngineeringUnits(0);

//Platform.runLater( {gpt.a.close();})



