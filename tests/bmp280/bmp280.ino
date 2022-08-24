#include <Wire.h>
#include "SparkFunBME280.h"


BME280 mySensorA;

void setup() {
  Serial.begin(9600);
  Wire.begin();

  mySensorA.setI2CAddress(0x76);
  if(mySensorA.beginI2C() == false) Serial.println("Sensor A connect failed");
}

void loop() {
  Serial.print("Altitude: ");
  Serial.print(mySensorA.readFloatAltitudeMeters(), 0);

  Serial.print(" TempA: ");
  Serial.print(mySensorA.readTempC(), 2);

  Serial.println();

  delay(50);
}
