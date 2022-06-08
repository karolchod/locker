#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>

//WiFi network credentials
const char* ssid = "ssidssid";
const char* password = "haslohaslo";

//Full URL to /api/box/boxesstatus endpoint (backend server address)
String serverName = "http://192.168.100.106:8080/api/box/boxesstatus";
//Number of box in backend database
String boxNumber = "1";

// Time of last API request (ms)
unsigned long lastTime = 0;
// Delay until next request (ms)
unsigned long timerDelay = 2000;

//Table of pins to which locker relays are connected (lockers in boxNumber box, ordered by locker id ascending)
int pins[]={D1,D2,D3,D4,D5,D6,D7,D8};

void setup() {
  for (int i =0; i<sizeof(pins); i++){
    pinMode(pins[i], OUTPUT);
    digitalWrite(pins[i], HIGH);
  }

  Serial.begin(9600); 
  
  WiFi.begin(ssid, password);
  Serial.println("Connecting");
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());
}

void loop() {

// Send another request to APi if correct time period passed
  if ((millis() - lastTime) > timerDelay) {
    // Send request only if connected to WiFi
    if(WiFi.status()== WL_CONNECTED){
      WiFiClient client;
      HTTPClient http;
      //Selecting correct box
      String serverPath = serverName + "?id="+boxNumber; 
      
      http.begin(client, serverPath.c_str());
      int httpResponseCode = http.GET();

      //If request successful
      if (httpResponseCode>0) {
        Serial.print("HTTP Response code: ");
        Serial.println(httpResponseCode);
        String payload = http.getString();
        Serial.println(payload);

        //Set lockers status according to response from API
        for(int i = 0; i<sizeof(pins)/sizeof(pins[0]); i++){
          digitalWrite(pins[i], payload[i]-'0');
        }

      } else {
        Serial.print("Error code: ");
        Serial.println(httpResponseCode);
      }
      // Free resources
      http.end();
    } else {
      //No connection to network
      Serial.println("WiFi Disconnected");
    }
    //Save timestamp of last request
    lastTime = millis();
  }
}
