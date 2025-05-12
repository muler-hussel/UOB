#include "t27.h"
#define MAXLEN 100

// 'Internal' function prototypes
// ...
unsigned len(dict *p);
int dfs(const dict* end, char* ret, char* tmp, int level, int maxFreq);
int intToChar(int i);
dict* endStr(const dict* p, const char* str);

dict* dict_init(void)
{
  dict* d = malloc(sizeof(dict));

  if (!d) {
    fprintf(stderr, "Fail to initialize");
    exit(EXIT_FAILURE);
  }

  for (int i = 0; i < ALPHA; i++) {
    d->dwn[i] = NULL;
  }
  d->up = NULL;
  d->terminal = false;
  d->freq = 0;

  return d;
}

bool dict_addword(dict* p, const char* wd)
{
  if (wd == NULL) {
    return false;
  }
  int len = strlen(wd);
  for (int i = 0; i < len; i++) {
    if (wd[i] != '\'' && !isalpha(wd[i])) {
      return false;
    }
    char c = tolower(wd[i]);
    if (c != '\'') {
      if (p->dwn[c - 'a'] == NULL) {
        dict* d = dict_init();
        p->dwn[c - 'a'] = d;
        d->up = p;
      }
      p = p->dwn[c - 'a'];
    } else {
      if (p->dwn[ALPHA - 1] == NULL) {
        dict* d = dict_init();
        p->dwn[ALPHA - 1] = d;
        d->up = p;
      }
      p = p->dwn[ALPHA - 1];
    }
  }
  p->freq++;
  if (p->terminal == true) {
    return false;
  } else {
    p->terminal = true;
  }
  return true;
}

void dict_free(dict** d)
{
  if (d == NULL || *d == NULL) {
    return;
  }

  for (int i = 0; i < ALPHA; i++) {
    if ((*d)->dwn[i] != NULL) {
      dict_free(&((*d)->dwn[i]));
    }
  }

  free(*d);
  *d = NULL;
  return;
}

int dict_wordcount(const dict* p)
{
  int cnt = 0;
  if (p == NULL) {
    return 0;
  }
  if (p->terminal == true) {
    cnt += p->freq;
  }
  for (int i = 0; i < ALPHA; i++) {
    cnt += dict_wordcount(p->dwn[i]);
  }
  return cnt;
}

int dict_nodecount(const dict* p)
{
  if (p == NULL) {
    return 0;
  }
  int cnt = 1;
   for (int i = 0; i < ALPHA; i++) {
    cnt += dict_nodecount(p->dwn[i]);
  }
  return cnt;
}

dict* dict_spell(const dict* p, const char* str)
{
  dict* end = endStr(p, str);
  if (end == NULL || end->terminal == false) {
    return NULL;
  }
  return end;
}

dict* endStr(const dict* p, const char* str)
{
  if (str == NULL || p == NULL) {
    return NULL;
  }

  int len = strlen(str);
  for (int i = 0; i < len; i++) {
    char c = tolower(str[i]);
    if (islower(c) && p->dwn[c - 'a'] != NULL) {
      p = p->dwn[c - 'a'];
    } else if (c == '\'' && p->dwn[ALPHA - 1] != NULL){
      p = p->dwn[ALPHA - 1];
    } else {
      return NULL;
    }
  }
  return (dict*) p;
}

int dict_mostcommon(const dict* p)
{
  if (p == NULL) {
    return 0;
  }
  int max = 0;

  if (p->terminal == true && p->freq > max) {
    max = p->freq;
  }
  for (int i = 0; i < ALPHA; i++) {
    int common = dict_mostcommon(p->dwn[i]);
    max = common > max ? common : max;
  }
  return max;
}

// CHALLENGE1
unsigned dict_cmp(dict* p1, dict* p2)
{
  if (p1 == NULL || p2 == NULL) {
    return 0;
  }
  unsigned int len1 = len(p1), len2 = len(p2), diff = 0;
  while(len1 > len2) {
    p1 = p1->up;
    len1--;
    diff++;
  }
  while (len1 < len2) {
    p2 = p2->up;
    len2--;
    diff++;
  }

  while (p1 != p2 && p1 != NULL) {
    p1 = p1->up;
    p2 = p2->up;
    diff += 2;
  }
  return diff;
}

unsigned len(dict *p) {
  unsigned int len = 0;
  while (p->up != NULL) {
    len++;
    p = p->up;
  }
  return len;
}

// CHALLENGE2
void dict_autocomplete(const dict* p, const char* wd, char* ret)
{
  dict* end = endStr(p, wd);
  if (end == NULL) {
    ret[0] = '\0';
    return;
  }

  int level = 0, maxFreq = 0;
  char tmp[MAXLEN];
  dfs(end, ret, tmp, level, maxFreq);
}

int dfs(const dict* end, char* ret, char* tmp, int level, int maxFreq) {
  if (end == NULL) {
    return maxFreq;
  }

  for (int i = 0; i < ALPHA; i++) {
    if (end->dwn[i] != NULL) {
      tmp[level] = intToChar(i);
      if (end->dwn[i]->terminal == true && end->dwn[i]->freq > maxFreq) {
        maxFreq = end->dwn[i]->freq;
        tmp[level + 1] = '\0';
        strcpy(ret, tmp);
      }
      maxFreq = dfs(end->dwn[i], ret, tmp, level + 1, maxFreq);
    }
  }
  return maxFreq;
}

int intToChar(int i) {
  return i == ALPHA - 1 ? '\'' : 'a' + i;
}

void test(void)
{
  char str[MAXLEN];
  assert(dict_cmp(NULL, NULL)==0);
  dict_autocomplete(NULL, "", str);
  assert(strcmp(str, "")==0);

  dict* d = dict_init();
  assert(dict_addword(d, "car"));
  //dict_addword(d, "car");
  dict* q1 = dict_spell(d, "car");
  dict* q2 = dict_spell(d, "car");
  assert(dict_cmp(q1, q2)==0);
  assert(dict_addword(d, "card"));
  assert(dict_addword(d, "carding"));
  assert(dict_addword(d, "carded"));

  dict_autocomplete(d, "car", str);
  assert(strcmp(str, "d")==0);
  dict_autocomplete(d, "dog", str);
  assert(strcmp(str, "")==0);
  dict_free(&d);
}
