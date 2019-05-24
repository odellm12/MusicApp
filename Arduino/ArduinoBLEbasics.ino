/*********************************************************************
  * Michael O'Dell
  * Adapted from BLE basics homework
  * Arduion Reciever for sound signal
*********************************************************************/
#include <Arduino.h>
#include <SPI.h>
#include "Adafruit_BLE.h"
#include "Adafruit_BluefruitLE_SPI.h"
#include "Adafruit_BluefruitLE_UART.h"
#include <Adafruit_CircuitPlayground.h>

#include "BluefruitConfig.h"

#if SOFTWARE_SERIAL_AVAILABLE
  #include <SoftwareSerial.h>
#endif

// Strings to compare incoming BLE messages
String start = "start";
String green = "green";
String blue = "blue";
String red = "red";
String readtemp = "readtemp";
String stp = "stop";
String invalid = "invalid";
String timer = "time";
int TIME_STEP = 1;
String beep = "beep";
String silent = "silent";
bool sound = false;
int rcol = 255;
int bcol = 255;
int gcol = 255;
bool leftButtonPressed;
bool rightButtonPressed;
int  sensorTemp = 0;

/*=========================================================================
    APPLICATION SETTINGS
    -----------------------------------------------------------------------*/
    #define FACTORYRESET_ENABLE         0
    #define MINIMUM_FIRMWARE_VERSION    "0.6.6"
    #define MODE_LED_BEHAVIOUR          "MODE"
/*=========================================================================*/

// Create the bluefruit object, either software serial...uncomment these lines
Adafruit_BluefruitLE_UART ble(BLUEFRUIT_HWSERIAL_NAME, BLUEFRUIT_UART_MODE_PIN);

void setup(void)
{
  CircuitPlayground.begin();
  
  Serial.begin(115200);
  Serial.println(F("Adafruit Bluefruit Command <-> Data Mode Example"));
  Serial.println(F("------------------------------------------------"));

  /* Initialise the module */
  Serial.print(F("Initialising the Bluefruit LE module: "));

  if ( !ble.begin(VERBOSE_MODE) )
  {
    error(F("Couldn't find Bluefruit, make sure it's in CoMmanD mode & check wiring?"));
  }
  Serial.println( F("OK!") );

  if ( FACTORYRESET_ENABLE )
  {
    /* Perform a factory reset to make sure everything is in a known state */
    Serial.println(F("Performing a factory reset: "));
    if ( ! ble.factoryReset() ){
      error(F("Couldn't factory reset"));
    }
  }

  /* Disable command echo from Bluefruit */
  ble.echo(false);

  Serial.println("Requesting Bluefruit info:");
  /* Print Bluefruit information */
  ble.info();

  Serial.println(F("Please use Adafruit Bluefruit LE app to connect in UART mode"));
  Serial.println(F("Then Enter characters to send to Bluefruit"));
  Serial.println();

  ble.verbose(false);  // debug info is a little annoying after this point!

  /* Wait for connection */
  while (! ble.isConnected()) {
      delay(500);
  }

  Serial.println(F("******************************"));

  // LED Activity command is only supported from 0.6.6
  if ( ble.isVersionAtLeast(MINIMUM_FIRMWARE_VERSION) )
  {
    // Change Mode LED Activity
    Serial.println(F("Change LED activity to " MODE_LED_BEHAVIOUR));
    ble.sendCommandCheckOK("AT+HWModeLED=" MODE_LED_BEHAVIOUR);
  }

  // Set module to DATA mode
  Serial.println( F("Switching to DATA mode!") );
  ble.setMode(BLUEFRUIT_MODE_DATA);

  Serial.println(F("******************************"));

  CircuitPlayground.setPixelColor(0,255,0,0);
 
  delay(1000);
}
/**************************************************************************/
/*!
    @brief  Constantly poll for new command or response data
*/
/**************************************************************************/
void loop(void)
{  
  // Save received data to string
  String received = "";
  while ( ble.available() )
  {
    int c = ble.read();
    Serial.print((char)c);
    received += (char)c;
    delay(60);
  }
  if(red == received){
    Serial.println("RECEIVED RED!!!!"); 
       for(int i = 0; i < 11; i++){
      CircuitPlayground.setPixelColor(i,255, 0, 0);
    }
    delay(50);
    bcol = 0;
    rcol = 255;
    gcol = 0;

  }
  else if(blue == received){
    Serial.println("RECEIVED BLUE!!!!"); 
       for(int i = 0; i < 11; i++){
      CircuitPlayground.setPixelColor(i,0, 0, 255);
    }
    delay(50);
    bcol = 255;
    rcol = 0;
    gcol = 0;
    
  }
    else if(green == received){
      Serial.println("RECEIVED GREEN!!!!"); 
       for(int i = 0; i < 11; i++){
      CircuitPlayground.setPixelColor(i,0, 255, 0);
    }
    delay(50);
    bcol = 0;
    rcol = 0;
    gcol = 255;
  }
 
  else if(readtemp == received){
        
    sensorTemp = CircuitPlayground.temperature(); // returns a floating point number in Centigrade
    Serial.println("Read temperature sensor"); 
    delay(10);

   //Send data to Android Device
    char output[8];
    String data = "";
    data += sensorTemp;
    Serial.println(data);
    data.toCharArray(output,8);
    ble.print(data);
  }
  else if(invalid == received)
  {
    Serial.println(invalid);
  }
  else if(beep == received)
  {
    sound = true;
  }
  else if(silent == received)
  {
    sound = false;
  }
  else if(received.startsWith(timer) || CircuitPlayground.leftButton())
  {
    if(!CircuitPlayground.leftButton())
    {
    TIME_STEP = 1;
    Serial.println(" ");
    String recievedtext = received.substring(4);
    int val = recievedtext.toInt();
    TIME_STEP = val*TIME_STEP;
    Serial.println(val);
    }
    
      CircuitPlayground.clearPixels();
      for(int i=0;i<10;i++)
      {
        CircuitPlayground.setPixelColor(i, rcol,   gcol,   bcol);
        delay(100*TIME_STEP);
      }
      if(sound == true)
      {
      CircuitPlayground.playTone(500,100);
      }      
  }
    
  }

 
