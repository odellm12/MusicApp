#include <Adafruit_CircuitPlayground.h>
#include <Adafruit_Circuit_Playground.h>

void setup() {
  // put your setup code here, to run once:
  CircuitPlayground.begin();
}

float ydat = 0;
int dly = 1;
bool record = false;

void loop() {
  if(CircuitPlayground.rightButton()){
    record = true;
  }
  if(CircuitPlayground.leftButton()){
    record = false;
  }
  if(record){
      ydat = CircuitPlayground.motionY();
      Serial.println(ydat);
      delay(dly);
  }
}
