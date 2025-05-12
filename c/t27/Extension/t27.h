#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <stdbool.h>
#include <string.h>
#include <ctype.h>

// 26 letters, plus the '
#define ALPHA 27
#define MAXSTR 50
#define INITCAP 4
#define SCALEFACTOR 2
#define HASH 5381
#define TIMES 33

struct entry {
  char* key;
  int value;
  struct entry* next;
};
typedef struct entry entry;

struct hashdict {
  entry** bucket;
  int size;
  int capacity;
};
typedef struct hashdict dict;

// Creates new dictionary
dict* dict_init(void);

/* Top of Dictionary = p,
   add word str. Return false
   if p or str is NULL, or if word
   is already in the dictionary.
   True otherwise.
*/
bool dict_addword(dict* p, const char* wd);

int dict_wordcount(const dict* p);

dict* dict_spell(const dict* p, const char* str);

/* Frees all memory used by dictionary p.
   Sets the original pointer back to NULL */
void dict_free(dict** p);

/* Returns number of times most common
   word in dictionary has been added
   (when you insert a word and it already
   exists, increment the count)
*/
int dict_mostcommon(const dict* p);

void test(void);
