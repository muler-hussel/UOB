#include "../arr.h"
#include "specific.h"

/* Creates the empty array */
arr* arr_init(void) {
  arr* arr = ncalloc(1, sizeof(arr));
  arr->a = ncalloc(INITSIZE, sizeof(int));
  arr->capacity = INITSIZE;
  return arr;
}
/* Similar to l[n] = i, safely resizing if required */
void arr_set(arr* l, int n, int i) {
  if (l) {
    while (n >= l->capacity) {
      l->a = nremalloc(l->a, sizeof(int) * l->capacity * SCALEFACTOR);
      l->capacity = l->capacity * SCALEFACTOR;
    }
    l->a[n] = i;
  }
}
/* Similar to = l[n] */
int arr_get(arr* l, int n) {
  if (n < l->capacity) {
    return l->a[n];
  } else {
    return -1;
  }
}
/* Clears all space used, and sets pointer to NULL */
void arr_free(arr** l) {
  if (l) {
    free((*l)->a);
    free(*l);
    *l = NULL;
  }
  return;
}