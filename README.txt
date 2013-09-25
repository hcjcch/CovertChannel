Name: Steven Lee
EID/CSID: SCL346

*** Notes ***
Covert Channel implemented is the same as indicated in the assignment. Subjects save previously read information in TEMP
member variable. RUN adds this TEMP value to the signal member variable used to keep track of bits until readCount member
variable has hit 8 (which means a byte has been fully formed). Hal's RUN command does nothing, the covert channel for Hal
is implemented in the main class.

*** Machine ***
model name      : Intel(R) Xeon(R) CPU           X3460  @ 2.80GHz
cpu MHz         : 2793.107

*** BANDWIDTH ***
Ulysses:	   1,573,150 bytes	7993ms    bandwidth: 197 bytes/ms
HuckleBerry Finn:  610,157 bytes	3335ms   bandwidth: 183bytes/ms
TestList:	   260 bytes		62ms    bandwidth: 4.2 bytes/ms
