#include <Wire.h>
#include <TimerOne.h>
#include "SparkFunBME280.h"
#include <MPU9250_asukiaaa.h>

#define CALIB_SEC 3 // magnetometer calibration (20s preffered)

MPU9250_asukiaaa mySensor;
BME280 mySensorA;

volatile bool intFlag = false;
long int flight_time = 0;
float aX, aY, aZ, aSqrt, gX, gY, gZ, mDirection, mX, mY, mZ;

struct {
  uint8_t flight_time;
} times;

void setup() {
  
  Wire.begin();
  Serial.begin(9600);

  mySensorA.setI2CAddress(0x76);
  if(mySensorA.beginI2C() == false) Serial.println("Sensor A connect failed");

  mySensor.beginAccel();
  mySensor.beginGyro();
  mySensor.beginMag();

  // Calibrate magnetometer
  Serial.println("calibration");
  setMagMinMaxAndSetOffset(&mySensor, CALIB_SEC);
  
  Timer1.initialize(10000);
  Timer1.attachInterrupt(interrupt);
}

void interrupt() {
  intFlag = true;
  if (times.flight_time) times.flight_time--;
}


void loop() {
  update_flight_time();
  calculate_and_send_data();
}

void update_flight_time() {
  if (times.flight_time) return;
  flight_time++;
  times.flight_time = 100;
}

void calculate_and_send_data() {
  while (!intFlag);
  intFlag = false;
  
  mySensor.accelUpdate();
  aX = mySensor.accelX();
  aY = mySensor.accelY();
  aZ = mySensor.accelZ();
  aSqrt = mySensor.accelSqrt();

  mySensor.gyroUpdate();
  gX = mySensor.gyroX();
  gY = mySensor.gyroY();
  gZ = mySensor.gyroZ();

  mySensor.magUpdate();
  mDirection = mySensor.magHorizDirection();

  sendCompressedData();
//  sendData();
}

void setMagMinMaxAndSetOffset(MPU9250_asukiaaa* sensor, int seconds) {
  unsigned long calibStartAt = millis();
  float magX, magXMin, magXMax, magY, magYMin, magYMax, magZ, magZMin, magZMax;

  sensor->magUpdate();
  magXMin = magXMax = sensor->magX();
  magYMin = magYMax = sensor->magY();
  magZMin = magZMax = sensor->magZ();

  while(millis() - calibStartAt < (unsigned long) seconds * 1000) {
    delay(100);
    sensor->magUpdate();
    magX = sensor->magX();
    magY = sensor->magY();
    magZ = sensor->magZ();
    if (magX > magXMax) magXMax = magX;
    if (magY > magYMax) magYMax = magY;
    if (magZ > magZMax) magZMax = magZ;
    if (magX < magXMin) magXMin = magX;
    if (magY < magYMin) magYMin = magY;
    if (magZ < magZMin) magZMin = magZ;
  }

  sensor->magXOffset = - (magXMax + magXMin) / 2;
  sensor->magYOffset = - (magYMax + magYMin) / 2;
  sensor->magZOffset = - (magZMax + magZMin) / 2;
}

void sendData() {
  Serial.print(";");
  // Gyro
  Serial.print(aX * 1000, 0);
  Serial.print(" ");
  Serial.print(aY * 1000, 0);
  Serial.print(" ");
  Serial.print(aZ * 1000, 0);
  Serial.print(" ");

  // Altitude and Temperature
  Serial.print(mySensorA.readFloatAltitudeMeters(), 2);
  Serial.print(" ");
  Serial.print(mySensorA.readFloatPressure() / 100, 2);
  Serial.print(" ");
  Serial.print(mySensorA.readTempC(), 2);
  Serial.print(" ");
  
  // Accelerometer
  Serial.print(aSqrt);
  Serial.print(" ");

  // Flight time
  Serial.print(flight_time);
  Serial.print(" ");
  
  // Magnetometer
  Serial.print(mDirection);

  Serial.println(";");
}

String formatTwoDigit(int toFormat) {
  String toPrint = "";
  if (abs(toFormat) <= 9) (toFormat >= 0) ? toPrint = "0" + String(toFormat, 0) : toPrint = "-0" + String(abs(toFormat), 0);
  else if (abs(toFormat) > 9 && abs(toFormat) <= 99) (toFormat >= 0) ? toPrint = String(toFormat, 0) : toPrint = "-" + String(abs(aX), 0);
  else if (abs(toFormat) > 99) (toFormat >= 0) ? toPrint = "99" : toPrint = "-99";
  toPrint.replace(" ", "");
  return toPrint;
}

void sendCompressedData() {
  char bytes[4];

  bytes[0] = 11;
  bytes[1] = 12;
  bytes[2] = 0x1;
  bytes[3] = bytes[0] + bytes[1] + bytes[2];

  Serial.write(bytes);

//  Serial.println(sizeof(bytes));
  
  /*
  char toPrint;
  aX = (int)(aX * 100);

  toPrint = (abs(aX) <= 99) ? aX : (aX >= 0) ? toPrint = 99 : toPrint = -99;
  
//  Serial.print(toPrint);
  int a = Serial.write(999);
  Serial.println("l: " + String(a));
  */



  
  /*
  // Gyro
  Serial.print(aX * 10, 0);
  Serial.print(" ");
  Serial.print(aY * 10, 0);
  Serial.print(" ");
  Serial.print(aZ * 10, 0);
  Serial.print(" ");

  // Altitude and Temperature
  Serial.print(mySensorA.readFloatAltitudeMeters(), 2);
  Serial.print(" ");
  Serial.print(mySensorA.readFloatPressure() / 100, 2);
  Serial.print(" ");
  Serial.print(mySensorA.readTempC(), 2);
  Serial.print(" ");
  
  // Accelerometer
  Serial.print(aSqrt);
  Serial.print(" ");

  // Flight time
  Serial.print(flight_time);
  Serial.print(" ");
  
  // Magnetometer
  Serial.print(mDirection);

  */
//  Serial.println();
}
