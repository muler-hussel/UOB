#pragma once

#define INITSIZE 2
#define SCALEFACTOR 2

struct boolarr {
  unsigned char* ba;
  unsigned int capacity;
  unsigned int size;
  unsigned int numTrue;
}