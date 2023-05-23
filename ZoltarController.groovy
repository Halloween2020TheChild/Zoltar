@Grab(group='com.alphacephei', module='vosk', version='0.3.45')
@Grab(group='org.openpnp', module='opencv', version='4.7.0-0')

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
import com.neuronrobotics.bowlerstudio.BowlerStudioController
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

import ai.djl.inference.Predictor
import ai.djl.modality.cv.Image
import ai.djl.modality.cv.ImageFactory
import ai.djl.modality.cv.output.BoundingBox
import ai.djl.modality.cv.output.DetectedObjects
import ai.djl.modality.cv.output.DetectedObjects.DetectedObject
import ai.djl.pytorch.jni.JniUtils
import ai.djl.repository.zoo.ZooModel
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

import org.opencv.core.CvType;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Tab
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import org.opencv.videoio.VideoCapture;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;



import com.neuronrobotics.bowlerkernel.djl.ImagePredictorType;
import com.neuronrobotics.bowlerkernel.djl.PredictorFactory;

import org.opencv.videoio.VideoCapture;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;


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
//if(regen) {
//	MobileBaseCadManager get = MobileBaseCadManager.get( base)
//	get.setConfigurationViewerMode(false)
//	get.generateCad()
//	Thread.sleep(100);
//	while(get.getProcesIndictor().get()<1){
//		println "Waiting for cad to get to 1:"+get.getProcesIndictor().get()
//		ThreadUtil.wait(1000)
//	}
//}
DHParameterKinematics arm = base.getAllDHChains().get(0);
MobileBase head = arm.getSlaveMobileBase(5)
AbstractLink mouth =head.getAllDHChains().get(0).getAbstractLink(0)
try {
	nu.pattern.OpenCV.loadLocally()
}catch(Throwable t) {
	BowlerStudio.printStackTrace(t)
	return
}
public class GPTInterface {
	private int width
	private int height
	private double tiltAngle
	Alert a;
	public final String AI_MODEL_NAME = "gpt-3.5-turbo";
	Tab t=new Tab()

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
	VideoCapture capture = new VideoCapture(0);
	// face cascade classifier
	CascadeClassifier faceCascade = new CascadeClassifier();
	File fileFromGit = ScriptingEngine.fileFromGit("https://github.com/CommonWealthRobotics/harr-cascade-archive.git",
	"resources/haarcascades/haarcascade_frontalface_default.xml")
	int absoluteFaceSize=0;
	Mat matrix =new Mat();
	WritableImage img = null;

	ZooModel<Image, DetectedObjects> mlmodel;
	Predictor<BufferedImage, DetectedObjects> predictor;
	ImageFactory factory;
	int frames=0;
	ai.djl.modality.cv.output.Point noseCenterOfFace = null
	public GPTInterface(String APIKey) {
		this.API_KEY = APIKey;
		LibVosk.setLogLevel(LogLevel.DEBUG);
		faceCascade.load(fileFromGit.getAbsolutePath());
		capture.open(0)
		factory=ImageFactory.getInstance()
		mlmodel  = PredictorFactory.imageContentsFactory(ImagePredictorType.ultranet);
		predictor = mlmodel.newPredictor();
		getFaces()
	}

	public String request(String phrase) throws IOException {
		return request(phrase, 0.7f,5);
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
	public double lookVector() {
		try {
			return noseCenterOfFace.getX()/((double)width)/2.0-0.5
		}catch(Exception ex) {
			return 0;
		}
	}

	public Rect[] getFaces() {
		if( capture.isOpened()) {
			//println "Camera Open"
			// If there is next video frame
			if (capture.read(matrix)) {
				frames++;
				//println "Frames "+frames
				MatOfRect faces = new MatOfRect();
				Mat grayFrame = new Mat();
				// face cascade classifier

				// convert the frame in gray scale
				Imgproc.cvtColor(matrix, grayFrame, Imgproc.COLOR_BGR2GRAY);
				// equalize the frame histogram to improve the result
				Imgproc.equalizeHist(grayFrame, grayFrame);

				// compute minimum face size (20% of the frame height, in our case)
				if (absoluteFaceSize == 0)
				{
					height = grayFrame.rows();
					width =grayFrame.cols();
					if (Math.round(height * 0.2f) > 0)
					{
						absoluteFaceSize = Math.round(height * 0.2f);
					}
				}
				// each rectangle in faces is a face: draw them!

				//println "Capture success"
				// Creating BuffredImage from the matrix
				BufferedImage image = new BufferedImage(matrix.width(),
						matrix.height(), BufferedImage.TYPE_3BYTE_BGR);

				WritableRaster raster = image.getRaster();
				DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
				byte[] data = dataBuffer.getData();
				matrix.get(0, 0, data);

				DetectedObjects detection = predictor.predict(factory.fromImage(image));
				List<DetectedObject> items = detection.items();
				Rect[] facesArray = new Rect[items.size()];
				for (int detectionIndex = 0; detectionIndex < items.size(); detectionIndex++) {
					DetectedObject c = items.get(detectionIndex);
					BoundingBox cGetBoundingBox = c.getBoundingBox();
					def topLeft = cGetBoundingBox.getPoint();
					def rect = cGetBoundingBox.getBounds();
					Iterator<ai.djl.modality.cv.output.Point> path = cGetBoundingBox.getPath().iterator();
					ArrayList<ai.djl.modality.cv.output.Point> list = new ArrayList<>();
					// sort into an ordered list
					for(ai.djl.modality.cv.output.Point p:path) {
						boolean added=false;
						for(int j=0;j<list.size();j++) {
							if(p.getY()<list.get(j).getY()) {
								list.add(j, p);
								added=true;
								break;
							}
						}
						if(!added)
							list.add(p)
					}
					if(list.size()>=5) {

						def left = list.get(0)
						def right=list.get(1)

						if(left.getY()!=right.getY()) {
							double y=left.getY()-right.getY()
							double x=left.getX()-right.getX()
							tiltAngle=Math.toDegrees(Math.atan2(y, x))
							if(tiltAngle<-90) {
								tiltAngle+=180
							}
							//println "Tilt angle = "+tiltAngle
						}else {
							// angle is 0, they are the same
							tiltAngle = 0
						}
						noseCenterOfFace = list.get(2)
					}
					//lm.get
					facesArray[detectionIndex]=new Rect(topLeft.getX()*matrix.width(),topLeft.getY()*matrix.height(),rect.getWidth()*matrix.width() ,rect.getHeight()*matrix.height())
					//System.out.println(c);
					//System.out.println("Name: "+c.getClassName() +" probability "+c.getProbability()+" center x "+topLeft.getX()+" center y "+topLeft.getY()+" rect h"+rect.getHeight()+" rect w"+rect.getWidth() );
					Imgproc.rectangle(matrix, facesArray[detectionIndex].tl(), facesArray[detectionIndex].br(), new Scalar(0, 255, 0), 3);
					Imgproc.putText(matrix, c.getClassName(), new Point(topLeft.getX()*matrix.width(),topLeft.getY()*matrix.height()-5), 3,1,  new Scalar(0, 255, 0));
					if(list.size()>3) {
						for(int j=0;j<2;j++) {
							ai.djl.modality.cv.output.Point p= list.get(j)
							Imgproc.circle(matrix, new Point(p.getX(),p.getY()), 3, new Scalar(255, 0, 0))
						}
						ai.djl.modality.cv.output.Point n= list.get(2)
						Imgproc.circle(matrix, new Point(n.getX(),n.getY()), 5, new Scalar(0, 0, 255))
						for(int j=list.size()-2;j<list.size();j++) {
							ai.djl.modality.cv.output.Point p= list.get(j)
							Imgproc.circle(matrix, new Point(p.getX(),p.getY()), 3, new Scalar(255, 0, 255))
						}
					}
				}

				matrix.get(0, 0, data);
				//println detection

				// Creating the Writable Image
				if(img==null) {
					img = SwingFXUtils.toFXImage(image, null);
					t=new Tab("Image capture");
					t.setContent(new ImageView(img))
					BowlerStudioController.addObject(t, null);
				}else{
					SwingFXUtils.toFXImage(image, img);
				}
				return facesArray
			}
		}
		return [] as Rect[]
	}

	/*
	 * '{
	 "model": "gpt-3.5-turbo",
	 "messages": [{"role": "user", "content": "Say this is a test!"}],
	 "temperature": 0.7
	 }'
	 */

	public String request(String phrase, float randomness,int retrys) throws IOException {
		if(retrys==0)
			return;
		try {
			if(Math.random()>0.5)
				phrase="Pretend you are a brittish fortune teller that only gives bad fortunes. Keep your response less than "+(maxSize*0.5)+" characters. make sure it is pg 13. if it is dark, make sure its dark humor. Respond to: "+phrase
			else
				phrase="Pretend you are a brittish Fortune teller that gives good fortunies. Keep your response less than "+(maxSize*0.5)+" charecters. Respond to: "+phrase

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

			Response response = client.newCall(request).execute();
			String jsonString = response.body().string();
			HashMap<String, Object> database = gson.fromJson(jsonString, TT_mapStringString);
			ArrayList<Object> choices = database.get("choices")
			HashMap<String, Object> firstChoice = choices.get(choices.size()-1)
			HashMap<String, Object> messageContent = firstChoice.get("message")
			String ret = messageContent.get("content").toString()
			println ret
			if(ret==null)
				throw new RuntimeException("No response!");
			return ret

		}catch(Throwable t) {
			BowlerStudio.printStackTrace(t)
			return request(phrase, randomness, retrys-1)
		}
	}
	public void close() {
		capture.release();
		BowlerStudioController.removeObject(t)
		println "Clean Exit from robot controller"
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
enum AnimationMode{
	spiritWorld,
	facetrack
}
GPTInterface gpt
try {
	println "Loading API key from "+keyLocation
	String content = new String(Files.readAllBytes(Paths.get(keyLocation)));
	println "API key: "+content
	gpt = new GPTInterface(content)

	running =true
	response=null
	msLoop=16
	indexAnimationLoop=0
	numStepsPerLoop=2000/msLoop



	AnimationMode mode =AnimationMode.facetrack;
	new Thread({
		JniUtils.setGraphExecutorOptimize(false);
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
			double tiltangle=0
			if(mode ==AnimationMode.facetrack) {
				Rect[] faces= gpt.getFaces()
				double look = gpt.lookVector()
				//println "Look "+look
				tiltangle = gpt.tiltAngle*1.5
				sinVal=look*2;
				cosVal=0
			}
			TransformNR changed=new TransformNR()
			changed.setX(136)


			def headRnage=30
			def analogy = 0
			def analogz = 35
			changed.setZ(260+analogz*cosVal)
			changed.setY(analogy)
			def analogup = sinVal*headRnage *1.5
			def rot = 179.96+analogup
			//println "Rotation "+rot
			changed.setRotation(new RotationNR(0,rot,-45))
			TransformNR tilted= new TransformNR(0,0,0, RotationNR.getRotationZ(-90 +tiltangle))
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
			}catch(Throwable tf) {
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

	ISpeakingProgress sp ={double percent,AudioStatus status->
		if(status==AudioStatus.release||status==AudioStatus.sustain)
			return
		gpt.status=status;
	}

	AudioPlayer.setThreshhold(600/65535.0)
	AudioPlayer.setLowerThreshhold(50/65535.0)
	double voice =864
	// 805 maybe?
	// 857 scottish?
	// 864 scottish??
	double echo = 0.85
	mode =AnimationMode.facetrack
	BowlerKernel.speak("What do you wish to know?", 100, 0, voice, 1, 1.0,sp)
	String prompt = gpt.promptFromMicrophone();
	mode =AnimationMode.spiritWorld
	Thread initialPrompt=new Thread({
		BowlerKernel.speak("Spirit World! Answer Me! "+prompt, 400, 0, voice, echo, 1.0,sp)
	})
	initialPrompt.start()
	response  = gpt.request(prompt,0.9,5)
	println "\n\nResponse\n"+response
	initialPrompt.join()
	mode =AnimationMode.facetrack
	BowlerKernel.speak(response, 100, 0, voice, 1, 1.0,sp)
}catch(Throwable tr) {
	BowlerStudio.printStackTrace(tr)
}
running=false
mouth.setTargetEngineeringUnits(0);
gpt.close()
//Platform.runLater( {gpt.a.close();})



