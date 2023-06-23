import com.neuronrobotics.bowlerstudio.creature.MobileBaseLoader
import com.neuronrobotics.sdk.addons.kinematics.MobileBase
import com.neuronrobotics.sdk.common.DeviceManager
import edu.wpi.SimplePacketComs.BytePacketType;
import edu.wpi.SimplePacketComs.FloatPacketType;
import edu.wpi.SimplePacketComs.*;
import edu.wpi.SimplePacketComs.phy.UDPSimplePacketComs;
import edu.wpi.SimplePacketComs.device.gameController.*;
import edu.wpi.SimplePacketComs.device.*

import java.util.Arrays;


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
	BytePacketType gpio = new BytePacketType(1811, 64)

	Manager(def device){
		myDevice=(UDPSimplePacketComs)device
		gpio.pollingMode()
		myDevice.addPollingPacket(gpio)
	}

	def setGPIO(boolean value) {
		def down = new byte[3]
		down[2]=value?1:0
		myDevice.writeBytes(gpio.idOfCommand, down)
	}
	def getGPIO(int index) {
		return (gpio.getUpstream()[index].byteValue()>0)
	}
}

def buttonManager = DeviceManager.getSpecificDevice("Zoltar");
if(buttonManager==null)
	throw new RuntimeException("Zoltar Device Missing!")
Manager manager = new Manager(buttonManager)
manager.setGPIO(true)
Thread.sleep(1000)
manager.setGPIO(false)
Thread.sleep(1000)
manager.setGPIO(true)
Thread.sleep(1000)
manager.setGPIO(false)
Thread.sleep(1000)

println "Pin 1: "+manager.getGPIO(0)+", pin 2: "+manager.getGPIO(1)

return null