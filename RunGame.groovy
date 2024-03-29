import com.neuronrobotics.bowlerstudio.creature.MobileBaseLoader
import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.neuronrobotics.sdk.addons.kinematics.MobileBase
import com.neuronrobotics.sdk.common.DeviceManager
import edu.wpi.SimplePacketComs.BytePacketType;
import edu.wpi.SimplePacketComs.FloatPacketType;
import edu.wpi.SimplePacketComs.*;
import edu.wpi.SimplePacketComs.phy.UDPSimplePacketComs;
import edu.wpi.SimplePacketComs.device.gameController.*;
import edu.wpi.SimplePacketComs.device.*

import java.util.Arrays;
// test comment
MobileBase base=DeviceManager.getSpecificDevice( "zoltar",{
	//If the device does not exist, prompt for the connection

	MobileBase m = MobileBaseLoader.fromGit(
			"https://github.com/Halloween2020TheChild/Zoltar.git",
			"zoltar.xml"
			)
	if(m==null)
		throw new RuntimeException("Arm failed to assemble itself")
	println "Connecting new device robot arm "+m
	regen=true;
	return m
})

class Manager{
	UDPSimplePacketComs myDevice
	int IDOcCommand = 1811
	Manager(def device){
		myDevice=(UDPSimplePacketComs)device
		setGPIO(false,true)
		PacketType p = myDevice.getPacket(IDOcCommand)
		p.pollingMode()
	}

	def setGPIO(boolean value, boolean val2) {
		def down = new byte[4]
		down[2]=value?1:0
		down[3]=val2?1:0
		myDevice.writeBytes(IDOcCommand, down)
	}
	def getGPIO(int index) {
		PacketType p = myDevice.getPacket(IDOcCommand)
		return (p.getUpstream()[index].byteValue()>0)
	}
}
// remove this and the script faults out BowlerStudio... wtf...
ScriptingEngine.gitScriptRun("https://github.com/Halloween2020TheChild/Zoltar.git", "ResnetWithCamera.groovy")

def buttonManager = DeviceManager.getSpecificDevice("Zoltar");
if(buttonManager==null)
	throw new RuntimeException("Zoltar Device Missing!")
Manager manager = new Manager(buttonManager)
try {
	manager.setGPIO(false,true)
	while(!Thread.interrupted()) {
		Thread.sleep(100);
		try{
		if(manager.getGPIO(1)==false) {
			println "Running Zoltar Script"
			manager.setGPIO(false,false)
			ScriptingEngine.gitScriptRun("https://github.com/Halloween2020TheChild/Zoltar.git", "ZoltarController.groovy")
			manager.setGPIO(false,true)
			good
		}
		}catch(Throwable tr){
			Bowlerstudio.printStackTrace(tr);
		}
	}
}catch(Throwable t) {
	
}
manager.setGPIO(false,true)


return null