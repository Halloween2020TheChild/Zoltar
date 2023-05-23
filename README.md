# Zoltar

A Zoltar machine made from a Loth Cat, Chat GPT and BowlerStudio

# System Requirements

Linux 

Docker (with user able to run docker commands without sudo)

Download  the Vosk speech recognizer model from https://alphacephei.com/vosk/models

get vosk-model-en-us-daanzu-2020090

and put it in ~/bowler-workspace/

The first time it runs, it could take up  to 4 hours to download the 12gb DOcker container for the advanced voice synthesis. 

To pre-empt this step, you can pre-build the Docker into your systems cache. 

```
git clone https://github.com/Halloween2020TheChild/CoquiDocker.git

cd CoquiDocker

docker build .

```

You will need a ChatGPT API key. It will prompt you to enter it when it is needed. 

to generate one for yourself, go to: https://platform.openai.com/account/api-keys

# Technologies

IK/FK control of robot (BowlerStudio)

Realtime Robot coms (SimplePacketComs)

TTS Legacy (MaryTTS)

TTS Advanced (Coqui->Docker->OkHTTP)

Fast Face detect (OpenCV Haar Cascade)

Face orientation (Ultranet)

Unique Face identification (face_feature)

Scene Object identification (Yolov5)

Voice detection (Vosk)

Q/A response engine (openAI's API, ChatGPT-3.5) This is the only off-board neural network


