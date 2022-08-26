#include <Wire.h>
#include <TimerOne.h>
#include "SparkFunBME280.h"

#define    MPU9250_ADDRESS            0x68
#define    MAG_ADDRESS                0x0C

#define    GYRO_FULL_SCALE_250_DPS    0x00  
#define    GYRO_FULL_SCALE_500_DPS    0x08
#define    GYRO_FULL_SCALE_1000_DPS   0x10
#define    GYRO_FULL_SCALE_2000_DPS   0x18

#define    ACC_FULL_SCALE_2_G        0x00  
#define    ACC_FULL_SCALE_4_G        0x08
#define    ACC_FULL_SCALE_8_G        0x10
#define    ACC_FULL_SCALE_16_G       0x18

BME280 mySensorA;

// This function read Nbytes bytes from I2C device at address Address. 
// Put read bytes starting at register Register in the Data array. 
void I2Cread(uint8_t Address, uint8_t Register, uint8_t Nbytes, uint8_t* Data) {
  // Set register address
  Wire.beginTransmission(Address);
  Wire.write(Register);
  Wire.endTransmission();
  
  // Read Nbytes
  Wire.requestFrom(Address, Nbytes); 
  uint8_t index=0;
  while (Wire.available())
    Data[index++]=Wire.read();
}


// Write a byte (Data) in device (Address) at register (Register)
void I2CwriteByte(uint8_t Address, uint8_t Register, uint8_t Data) {
  // Set register address
  Wire.beginTransmission(Address);
  Wire.write(Register);
  Wire.write(Data);
  Wire.endTransmission();
}



// Initial time
long int ti;
volatile bool intFlag=false;

// Initializations
void setup() {
  // Arduino initializations
  
  Wire.begin();
  Serial.begin(9600);

  mySensorA.setI2CAddress(0x76);
  if(mySensorA.beginI2C() == false) Serial.println("Sensor A connect failed");

  // Set accelerometers low pass filter at 5Hz
  I2CwriteByte(MPU9250_ADDRESS, 29, 0x06);
  // Set gyroscope low pass filter at 5Hz
  I2CwriteByte(MPU9250_ADDRESS, 26, 0x06);
 
  
  // Configure gyroscope range
  I2CwriteByte(MPU9250_ADDRESS, 27, GYRO_FULL_SCALE_1000_DPS);
  // Configure accelerometers range
  I2CwriteByte(MPU9250_ADDRESS, 28, ACC_FULL_SCALE_16_G);
  // Set by pass mode for the magnetometers
  I2CwriteByte(MPU9250_ADDRESS, 0x37, 0x02);
  
  // Request continuous magnetometer measurements in 16 bits
  I2CwriteByte(MAG_ADDRESS,0x0A,0x16);
  
  Timer1.initialize(10000);
  Timer1.attachInterrupt(interrupt);
  
  ti = millis();
}

// Counter
long int cpt = 0;

void interrupt() {
  intFlag = true;
}

int16_t ax, ay, az, gy, gx, gz, mx, my, mz;

int16_t maxG, minG;

void loop() {
  while (!intFlag);
  intFlag = false;
 
  uint8_t Buf[14];
  I2Cread(MPU9250_ADDRESS, 0x3B, 14, Buf);
  
  // Accelerometer
  ax =- (Buf[0]<<8 | Buf[1]);
  ay =- (Buf[2]<<8 | Buf[3]);
  az = Buf[4]<<8 | Buf[5];

  // Gyroscope
  gx =- (Buf[8]<<8 | Buf[9]);
  gy =- (Buf[10]<<8 | Buf[11]);
  gz = Buf[12]<<8 | Buf[13];
  
  // Magnetometer
  uint8_t ST1;
  do {
    I2Cread(MAG_ADDRESS,0x02,1,&ST1);
  } while (!(ST1&0x01));
  // Read data
  uint8_t Mag[7];  
  I2Cread(MAG_ADDRESS,0x03,7,Mag);
  
  // Magnetometer
  mx =- (Mag[3]<<8 | Mag[2]);
  my =- (Mag[1]<<8 | Mag[0]);
  mz =- (Mag[5]<<8 | Mag[4]);

  sendData();
}

void sendData() {
  // Gyro
  Serial.print(";");
  Serial.print (ax,DEC); 
  Serial.print (" ");
  Serial.print (ay,DEC);
  Serial.print (" ");
  Serial.print (az,DEC);  
  Serial.print (" ");

  // Altitude and Temperature
  Serial.print(mySensorA.readFloatAltitudeMeters(), 2);
  Serial.print(" ");
  Serial.print(mySensorA.readFloatPressure() / 100, 2);
  Serial.print(" ");
  Serial.print(mySensorA.readTempC(), 2);
  Serial.print(" ");
  
  // Accelerometer
  Serial.print(gz);
  
  /*
  // Gyroscope
  Serial.print (gx,DEC); 
  Serial.print (" ");
  Serial.print (gy,DEC);
  Serial.print (" ");
  Serial.print (gz,DEC);  
  */

/*
  // Magnetometer
  Serial.print (mx+200,DEC); 
  Serial.print (" ");
  Serial.print (my-70,DEC);
  Serial.print (" ");
  Serial.print (mz-700,DEC);  
*/

  Serial.println(";");
}
