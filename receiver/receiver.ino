#include <TimerOne.h>

void setup() {
  Serial.begin(9600);
  Timer1.initialize(10000);
  Timer1.attachInterrupt(interrupt);
}

void interrupt() {
  
}


void loop() {
  
}
