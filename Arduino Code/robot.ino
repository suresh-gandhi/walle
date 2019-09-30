#include <Servo.h> 
#include <SoftwareSerial.h>

SoftwareSerial BTserial(0, 1); // RX | TX

 
Servo servo_head;
Servo servo_left;
Servo servo_right;

int SERVO_HEAD_PIN = 5;
int SERVO_LEFT_PIN = 10;
int SERVO_RIGHT_PIN = 6;

int Left_LED_PIN = 13;
int Right_LED_PIN = 12;

int head_pos = 0; 
int left_pos = 0;
int right_pos = 0;

String readString;

void setup() 
{ 
  Serial.begin(9600);
  BTserial.begin(9600);  

  servo_head.attach(SERVO_HEAD_PIN);
  servo_left.attach(SERVO_LEFT_PIN);
  servo_right.attach(SERVO_RIGHT_PIN );

  pinMode(Left_LED_PIN,OUTPUT);
  pinMode(Right_LED_PIN,OUTPUT);


  //BELOW ARE DEFAULTS
  digitalWrite(Left_LED_PIN,HIGH);
  digitalWrite(Right_LED_PIN,HIGH);
}
 
void loop() {  
  readString = "";
  while (BTserial.available()) {
    delay(3);  //delay to allow buffer to fill 
    if (BTserial.available() >0) {
      char c = BTserial.read();  //gets one byte from serial buffer
      readString += c; //makes the string readString
    } 
  }
  if(readString.length() >0)
    Serial.println("RECEIVED: "+readString);
  
  /*
  if(readString.equals("LeftOff")){
    digitalWrite(Left_LED_PIN,LOW);
    return;
  }
  if(readString.equals("LeftOn")){
    digitalWrite(Left_LED_PIN,HIGH);
    return;
  }
  if(readString.equals("RightOff")){
    digitalWrite(Right_LED_PIN,LOW);
    return;
  }
  if(readString.equals("RightOn")){
    digitalWrite(Right_LED_PIN,HIGH);
    return;
  }*/
  if(readString.equals("TO")){
    digitalWrite(Left_LED_PIN,HIGH);
    digitalWrite(Right_LED_PIN,HIGH);
    Serial.println("Turning On");
    return;
  }
  if(readString.equals("TF")){
    digitalWrite(Left_LED_PIN,LOW);
    digitalWrite(Right_LED_PIN,LOW);
    Serial.println("Turning Off");
    return;
  }

  if(readString.indexOf("N") > -1){
    int firstColon = readString.indexOf("N");
    head_pos = readString.substring(firstColon+1).toInt();
    Serial.println("Moving head to: "+String(head_pos));
  }
  servo_head.write(head_pos);   

  if(readString.indexOf("L") > -1){
    int firstColon = readString.indexOf("L");
    left_pos = readString.substring(firstColon+1).toInt();
    Serial.println("Moving LEFT to: "+String(left_pos));
  }
  servo_left.write(left_pos);   

  if(readString.indexOf("R") > -1){
    int firstColon = readString.indexOf("R");
    right_pos = readString.substring(firstColon+1).toInt();
    Serial.println("Moving RIGHT to: "+String(right_pos));
  }
  servo_right.write(right_pos);   

  /*
  for (head_pos = 0; head_pos  <= 180; head_pos  += 1) { // goes from 0 degrees to 180 degrees
    // in steps of 1 degree
    servo_head.write(head_pos );              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
  }
  for (head_pos = 0 ; head_pos  >= 0; head_pos  -= 1) { // goes from 180 degrees to 0 degrees
    servo_head.write(head_pos );              // tell servo to go to position in variable 'pos'
    delay(15);                       // waits 15ms for the servo to reach the position
  }*/
}
