#include "ext.h"

// 'Internal' function prototypes
// ...
int hash(int capacity, char* key);
int get(const dict* p, char* wd);
dict* resize(dict* p);
dict* newdict(int capacity);
void addEntry(dict* m, int idx, char* key, int v);
void validWord(const char* wd, char* str);

dict* dict_init(void)
{
  dict* m = newdict(INITCAP);
  return m;
}

bool dict_addword(dict* p, const char* wd)
{
  if (wd == NULL) {
    return false;
  }
  char str[MAXSTR];
  validWord(wd, str);
  if (str == NULL) {
    return false;
  }
  //resize
  if (++p->size > p->capacity) {
    dict* m = resize(p);
    for (int i = 0; i < p->capacity; i++) {
      entry* e = p->bucket[i];
      while (e != NULL) {
        entry* temp = e;
        e = e->next;
        free(temp->key);
        free(temp);
      }
      free(e);
    }
    free(p->bucket);

    p->bucket = m->bucket;
    p->capacity = m->capacity;
    p->size = m->size;
    free(m);
  }
  
  int idx = hash(p->capacity, str);
  entry* e = p->bucket[idx];
  int val = get(p, str);
  if (val != -1) {
    while (e != NULL) {
      if (strcmp(str, e->key) == 0) {
        e->value++;
      }
      e = e->next;
    }
    return false;
  }
  addEntry(p, idx, str, 1);
  return true;
}

void dict_free(dict** d)
{
  if (d == NULL || *d == NULL) {
    return;
  }

  for (int i = 0; i < (*d)->capacity; i++) {
    entry* e = (*d)->bucket[i];
    while (e != NULL) {
      free(e->key);
      entry* tmp = e;
      e = e->next;
      free(tmp);
    }
    free(e);
  }
  free((*d)->bucket);
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
  for (int i = 0; i < p->capacity; i++) {
    entry* e = p->bucket[i];
    while (e != NULL) {
      cnt += e->value;
      e = e->next;
    }
  }
  return cnt;
}

dict* dict_spell(const dict* p, const char* str)
{
  if (p == NULL || str == NULL) {
    return NULL;
  }
  char wd[MAXSTR];
  validWord(str, wd);
  if (wd == NULL || get(p, wd) == -1) {
    return NULL;
  }
  return (dict*)p;
}

int dict_mostcommon(const dict* p)
{
  if (p == NULL) {
    return 0;
  }
  int max = 0;

  for (int i = 0; i < p->capacity; i++) {
    entry* e = p->bucket[i];
    while (e != NULL) {
      max = e->value > max ? e->value : max;
      e = e->next;
    }
  }
  return max;
}

void test(void)
{
}

dict* newdict(int capacity)
{
  dict* m = malloc(sizeof(dict));

  if (!m) {
    fprintf(stderr, "Fail to initialize");
    exit(EXIT_FAILURE);
  }

  m->bucket = malloc(capacity * sizeof(entry*));
  m->size = 0;
  m->capacity = capacity;
  for (int i = 0; i < capacity; i++) {
    m->bucket[i] = NULL;
  }
  return m;
}

void validWord(const char* wd, char* str)
{
  int len = strlen(wd);
  for (int i = 0; i < len; i++) {
    if (!isalpha(wd[i]) && wd[i] != '\'') {
      str = NULL;
    }
    str[i] = tolower(wd[i]);
  }
  str[len] = '\0';
  return;
}

int hash(int capacity, char* key)
{
  unsigned long hash = HASH;
  int c;
  while ((c = (*key++))) {
    hash += TIMES * hash ^ c;
  }
  return (int)(hash % capacity);
}

int get(const dict* p, char* wd)
{
  int idx = hash(p->capacity, wd);
  entry* e = p->bucket[idx];
  if (e == NULL) {
    return -1;
  }
  while (e != NULL) {
    if (strcmp(wd, e->key) == 0) {
      return e->value;
    }
    e = e->next;
  }
  return -1;
}

//resize hashdict
dict* resize(dict* p)
{
  int newCap = p->capacity * SCALEFACTOR;
  dict* m = newdict(newCap);

  for (int i = 0; i < p->capacity; i++) {
    entry* e = p->bucket[i];
    while (e != NULL) {
      int idx = hash(newCap, e->key);
      addEntry(m, idx, e->key, e->value);
      e = e->next;
    }
  }
  return m;
}

void addEntry(dict* m, int idx, char* key, int v)
{
  entry* e = m->bucket[idx];
  if (e == NULL) {
    e = malloc(sizeof(entry));
    if (!e) {
      fprintf(stderr, "Fail to initialize");
      exit(EXIT_FAILURE);
    }
    e->key = malloc(strlen(key) + 1);
    if (!e->key) {
      fprintf(stderr, "Fail to initialize");
      exit(EXIT_FAILURE);
    }
    strcpy(e->key, key);
    e->value = v;
    e->next = NULL;
  } else {
    entry* tmp = e;
    while (tmp->next != NULL) {
      tmp = tmp->next;
    }
    entry* node = malloc(sizeof(entry));
    if (!node) {
      fprintf(stderr, "Fail to initialize");
      exit(EXIT_FAILURE);
    }
    node->key = malloc(strlen(key) + 1);
    if (!node->key) {
      fprintf(stderr, "Fail to initialize");
      exit(EXIT_FAILURE);
    }
    strcpy(node->key, key);
    node->value = v;
    tmp->next = node;
    node->next = NULL;
  }
  m->bucket[idx] = e;
}
