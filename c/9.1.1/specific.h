#pragma once

#define COLLTYPE "Realloc"
#define INITSIZE 16
#define SCALEFACTOR 2

struct arr {
  int* a;
  int capacity;
};