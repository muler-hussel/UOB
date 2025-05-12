#include "boolarr.h"
#include "specific.h"

#define BYTELEN 8
#define BYTEINDEX(n) (n / BYTELEN)
#define BITMASK(n) (1 << (BYTELEN - n % BYTEINDEX - 1))  // 00000001 7%8=7 8-7=1
/* All elements initialised to "0" */
boolarr* boolarr_init(void) {
  boolarr* boolarr = ncalloc(1, sizeof(boolarr));
  boolarr->ba = ncalloc(INITSIZE * sizeof(unsigned char));
  boolarr->size = 0;
  boolarr->capacity = INITSIZE * BYTELEN;
  boolarr->numTure = 0;
  return boolarr;
}

/* Create boolarr based on string e.g. "1100001" */
boolarr* boolarr_initstr(const char* str) {
  boolarr* boolarr = ncalloc(1, sizeof(boolarr));
  boolarr->ba = ncalloc(INITSIZE * sizeof(unsigned char));
  boolarr->capacity = INITSIZE * BYTELEN;
  unsigned int t = 0, len = strlen(str);
  while (len > boolarr->capacity) {
    boolarr->ba = nremalloc(boolarr->ba, sizeof(unsigned char) * boolarr->capacity * SCALEFACTOR);
    boolarr->capacity = boolarr->capacity * SCALEFACTOR;
  }
  for (unsigned int i = 0; i < len; i++) {
    if (str[i] == '1') {
      t++;
      boolarr->ba[BYTEINDEX(i)] |= BITMASK(i);
    }
  }
  boolarr->numTure = t;
  boolarr->size = len;
  return boolarr;
}

/* Return a deep copy */
boolarr* boolarr_clone(const boolarr* ba) {
  unsigned int ca = ba->capacity;
  boolarr newBa = ncalloc(1, sizeof(boolarr));
  newBa->ba = ncalloc(ca * sizeof(unsigned char));
  newBa->capacity = ca;
  newBa->size = ba.size;
  for (unsigned int i = 0; i < ca / BYTELEN; i++) {
    newBa->ba[i] = ba->ba[i];
  }
  return newBa;
}

/* Get number of bits in array */
unsigned int boolarr_size(const boolarr* ba) {
  return ba->size;
}

/* Return number of bits that are set true */
unsigned int boolarr_count1s(const boolarr* ba) {
  return ba->numTure;
}

/* Set nth bit on/off */
bool boolarr_set(boolarr* ba, const unsigned int n, const bool b) {
  if (!ba) return false;

  while (n >= ba-> capacity) {
    ba->ba = nremalloc(ba->ba, sizeof(unsigned char) * ba->capacity * SCALEFACTOR);
    ba->capacity = ba->capacity * SCALEFACTOR;
  }

  if (b) {
    ba->ba[BYTEINDEX(n)] &= BITMASK(n);
    ba->numTure++;
  } else {
    ba->ba[BYTEINDEX(n)] |= BITMASK(n);
  }
}
/* Get nth bit */
bool boolarr_get(const boolarr* ba, const unsigned int n, bool* b) {
  if (!ba || n >= ba->size) return false;
  *b = ba->ba[BYTEINDEX(n)] & BITMASK(n);
  return true;
}

/* Return if two arrays are the same (bitwise) */
bool boolarr_issame(const boolarr* b1, const boolarr* b2) {
  if (!b1 || !b2 || b1->capacity != b2->capacity) return false;
  unsigned int ca = b1->capacity;
  for (unsigned int i = 0; i < ca; i++) {
    if (b1->ba[i] ^ b2->ba[i] != 0) return false;
  }
  return true;
}

/* Store to string - rightmost bit is LSB */
bool boolarr_tostring(const boolarr* ba, char* str) {
  if (!ba || !str) return false;
  for (unsigned int i = 0; i < ba->size; i++) {
    str[i] = ba->ba[BYTEINDEX(i)] & BITMASK(i) ? '1' : '0';
  }
}

/* Print out array & meta info */
bool boolarr_print(const boolarr* ba) {
  if (!ba) return false;

  char* str = malloc(ba->size + 1);
  boolarr_tostring(ba, str);

  for (unsigned int i = 0; i < ba->size; i++) {
    printf("%c", str[i]);
  }
  printf("\n");
  free(str);
  return true;
}

/* Flip all bits */
bool boolarr_negate(boolarr* ba) {
  if (!ba) return false;

  //size = 8 8/8=1 8+8-1 / 8 = 2  7+8-1 / 8 = 1
  for (unsigned int i = 0; i < (ba->size + BYTELEN - 1) / BYTELEN; i++) {
    ba->ba[i] = ~ba->ba[i];
  }
  return true;
}

/* Functions dealing with 2 bitwise-arrays */
/* Must be the same length */
boolarr* boolarr_bitwise(const boolarr* ba1, const boolarr* ba2, const logicalop l) {
  if (!ba1 || !ba2 || ba1->size != ba2->size) return NULL;

  boolarr* ret = boolarr_clone(ba1);

  for (unsigned int i = 0; i < (ba->size + BYTELEN - 1) / BYTELEN; i++) {
    switch (l) {
      case or:
        ret->ba[i] |= b2->ba[i];
        break;
      case and:
        ret->ba[i] &= b2->ba[i];
        break;
      case xor:
        ret->ba[i] ^= b2->ba[i];
        break;
    }
  }
  return ret;
}

/* Clears all space */
bool boolarr_free(boolarr* p) {
  if (!p) return false;

  free(p->ba);
  free(p);
  return true;
}