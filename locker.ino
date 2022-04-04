#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>

//Dane dostepu do sieci WiFi
const char* ssid = "ssidssid";
const char* password = "haslohaslo";

//Adres pod ktory wysylane jest zapytanie http
String serverName = "http://192.168.100.106:8080/api/box/boxesstatus";
String boxNumber = "1";

// Czas ostatniego zapytania
unsigned long lastTime = 0;

// Czas po którym wysylany jest kolejny pakiet
unsigned long timerDelay = 2000;

//Tabela kolejnych wyjsc podlaczonych do przekaznikow
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

// Jesli uplynal odpowiedni czas, wysylamy kolejne zapytanie
  if ((millis() - lastTime) > timerDelay) {
    //Sprawdzenie stanu polaczenia sieciowego
    if(WiFi.status()== WL_CONNECTED){
      WiFiClient client;
      HTTPClient http;

      String serverPath = serverName + "?id="+boxNumber; //wybor skrytki
      
      http.begin(client, serverPath.c_str());
      int httpResponseCode = http.GET();

      //Jesli zapytanie sie powiodlo
      if (httpResponseCode>0) {
        Serial.print("HTTP Response code: ");
        Serial.println(httpResponseCode);
        String payload = http.getString();
        Serial.println(payload);

        //przetwarzanie odebranej wiadomosci
        for(int i = 0; i<sizeof(pins)/sizeof(pins[0]); i++){
          digitalWrite(pins[i], payload[i]-'0');
        }

      } else {
        Serial.print("Error code: ");
        Serial.println(httpResponseCode);
      }
      // Zwolnienie zasobow
      http.end();
    } else {
      //brak polaczenia sieciowego
      Serial.println("WiFi Disconnected");
    }
    //zapamietanie czasu ostatniej proby polaczenia
    lastTime = millis();
  }
}
