#include <Arduino.h>
#include <HTTPUpdate.h>
#include <WiFi.h>

WiFiClient wifiClient;
void update();
void setup()
{
  // put your setup code here, to run once:
  pinMode(2, OUTPUT);
  WiFi.begin("BIT-ENERGY", "pic-embedded");
}

uint32_t updateCounter = 0;

void loop()
{
  // put your main code here, to run repeatedly:
  digitalWrite(2, 1);
  delay(250);
  digitalWrite(2 , 0);
  delay(250);


      update();
    }




void update()
{
  String url = "http://cibikomberi.local:8080/thing/update/677367065ee782724fdba1a4?version=2";

  httpUpdate.update(wifiClient, url);
}