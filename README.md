# PepperMock

1) Commands to Pepper are exposed via ```PepperCommands```
2) Callbacks from Pepper are exposed via ```PepperCallbacks```
3) All componend communication is handled via ```Bus```
4) HumanDetection is done via calculating the current noise amplitude via ```MediaRecorder``` and comparing 
it to the average amplitude within the last 60 seconds.
5) Pepper will select random greetings to greet the user. If you have 10 greetings, no 5 consecutive greetings will be the same.
6) Pepper will actually read you the words via ```TextToSpeech``` API.
7) ```PepperRobotMockService``` is the service that mocks the devices on the robot such as ```PepperHumanSensor``` and ```PepperAudioSpeaker```
8) UI will appear when an human is detected
9) Once the user exits the UI, there will be a 7 second gap between now and HumanDetection can start the main activity again.
10) All greetings can be editted via ```EngineeringAccess```
