#include <Arduino.h>
#include "device_constants.h"

#include <Rect.h>
#include <WiFi.h>
#include <WifiClient.h>

  WiFiClient client;
  Rect rect;

void dsaHandler(float val) {
  Serial.println(val);
  analogWrite(2, val);
}

std::string commandHandler(String command) {
  Serial.println(command);
  return "OK";
}

void setup() {
  Serial.begin(115200);
  pinMode(2, OUTPUT);
  WiFi.begin("test", "12345678");
  rect.begin(client, RECT_API_KEY, RECT_DEVICE_ID, "0.1");
  rect.registerCallback("dsa", dsaHandler);
  rect.registerCommandProcessor(commandHandler);
}

void loop() {
  rect.loop();
  rect.put("dsa4", String(150 - touchRead(4)));
  delay(500);
}