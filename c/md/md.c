#include "mydefs.h"
#define MAXSTR (BRDSZ*BRDSZ+BRDSZ+2)
#define CHAR 26

bool colFinish(board bd, int i);
bool isSolve(board bd);
void push(state *s, int curIdx, int *lastIdx, int c);
bool inState(state *s, int lastIdx);
bool isSame(state *s, int idx, int lastIdx);
void print(state *s, int idx, int steps);
bool isImposs(board bd);
int pruning(state *s, int curIdx, int *lastIdx, bool verbose, bool pushed);
int notPruning(state *s, int curIdx, int *lastIdx, bool verbose);

bool file2str(const char* fname, char* str)
{
  if (fname == NULL || str == NULL) {
    return false;
  }
  FILE *file = fopen(fname, "r");
  if (file == NULL) {
    fprintf(stderr, "Cannot find file. \n");
    exit(EXIT_FAILURE);
  }
  char c;
  int idx = 0;
  while ((c = fgetc(file)) != EOF) {
    if (idx > MAXBRDS) {
      fprintf(stderr, "Reach the maxium length of string. \n");
      exit(EXIT_FAILURE);
    }
    if (c == '\n') {
      str[idx++] = '-';
    } else if (!isupper(c)) {
      fprintf(stderr, "Only uppercase character allowed. \n");
      exit(EXIT_FAILURE);
    }else {
      str[idx++] = c;
    }
  }
  str[idx - 1] = '\0';
  fclose(file);
  return true;
}

state* str2state(const char* str)
{
  state *s = malloc(sizeof(state));
  if (s == NULL) {
    exit(EXIT_FAILURE);
  }
  int idx = 0, j = 0, i = 0;
  s->board[0].hawk = str[idx++];
  s->board[0].parent = -1;
  s->board[0].step = 0;
  int len = strlen(str);
  while (++idx < len) {
    if (str[idx] == '-') {
      j++;
      i = 0;
    } else {
      s->board[0].tiles[j][i++] = str[idx];
    }
  }
  //s->board[0].tiles[j][i] = '\0';
  s->board[0].row = j + 1;
  s->board[0].col = i;
  return s;
}

int solve(state* s, bool verbose)
{
  int lastIdx = 1, curIdx = 0, ret = -1;
  if (isImposs(s->board[0])) {
    return ret;
  }
  if (isSolve(s->board[0])) {
    ret = s->board[0].step;
    if (verbose) {
      print(s, 0, ret);
    }
      return ret;
  }
  while (curIdx < lastIdx) {
    ret = pruning(s, curIdx, &lastIdx, verbose, false);
    if (ret > 0) {
      return ret;
    } else if (ret == -1) {
      ret = notPruning(s, curIdx, &lastIdx, verbose);
      if (ret != -1) {
        return ret;
      }
    }
    curIdx++;
  }
  return ret;
}

void test(void)
{
  char str[MAXSTR];
  state* s;

  strcpy(str, "A-BC-BC");
  s = str2state(str);
  assert(s);
  assert(solve(s, false)==0);
  free(s);

  strcpy(str, "A-BCCCC-CBBBB-DEFAD");
  s = str2state(str);
  assert(s);
  assert(solve(s, false)==-1);
  free(s);

  strcpy(str, "A-ABCDEF-BDCEFA-CEAFDB-DAEFBC-EFADBC-FDCABE");
  s = str2state(str);
  assert(s);
  solve(s, false);
  //solve(s, true);
  //assert(solve(s, false)==29);
  free(s);
}

/* Many of other functions, as required */
bool colFinish(board bd, int i) {
  for (int j = 1; j < bd.row; j++) {
    if (bd.tiles[j][i] != bd.tiles[0][i]) {
      return false;
    }
  }
  return true;
}

bool isSolve(board bd) {
  for (int i = 0; i < bd.col; i++) {
    if (!colFinish(bd, i)) {
      return false;
    }
  }
  return true;
}

void push(state *s, int curIdx, int *lastIdx, int c) {
  board *newBoard = &s->board[*lastIdx];
  *newBoard = s->board[curIdx];
  newBoard->parent = curIdx;
  newBoard->step = s->board[curIdx].step + 1;

  char hawk = s->board[curIdx].hawk;
  for (int j = 0; j < newBoard->row; j++) {
    char tmp = hawk;
    hawk = newBoard->tiles[j][c];
    newBoard->tiles[j][c] = tmp;
  }
  newBoard->hawk = hawk;
}

bool inState(state *s, int lastIdx) {
  for (int idx = 0; idx < lastIdx; idx++) {
    if (isSame(s, idx, lastIdx)) {
      return true;
    }
  }
  return false;
}

bool isSame(state *s, int idx, int lastIdx) {
  if (s->board[idx].hawk != s->board[lastIdx].hawk) return false;
  for (int j = 0; j < s->board[idx].row; j++) {
    for (int i = 0; i < s->board[idx].col; i++) {
      if (s->board[idx].tiles[j][i] != s->board[lastIdx].tiles[j][i]) {
        return false;
      }
    }
  }
  return true;
}

void print(state *s, int idx, int steps) {
  int list[MAXSTR];
  int id = steps;
  
  while (id >= 0) {
    list[id--] = idx;
    idx = s->board[idx].parent;
  }

  while (id++ < steps) {
    int bdId = list[id];
    for (int j = 0; j < s->board[bdId].row; j++) {
      for (int i = 0; i < s->board[bdId].col; i++) {
        printf("%c", s->board[bdId].tiles[j][i]);
      }
      printf("\n");
    }
    printf("\n");
  }
}

bool isImposs(board bd) {
  if (bd.row == 1) return false;
  int cnt[CHAR];
  for (int i = 0; i < CHAR; i++) {
    cnt[i] = 0;
  }
  int chars = 0;
  cnt[bd.hawk - 'A']++;
  for (int j = 0; j < bd.row; j++) {
    for (int i = 0; i < bd.col; i++) {
      cnt[bd.tiles[j][i] - 'A']++;
      if (cnt[bd.tiles[j][i] - 'A'] == bd.row) {
        chars++;
      }
    }
  }
  if (chars == bd.col) {
    return false;
  }
  return true;
}

int pruning(state *s, int curIdx, int *lastIdx, bool verbose, bool pushed) {
  for (int i = 0; i < s->board[curIdx].col; i++) {
    if (s->board[curIdx].tiles[0][i] == s->board[curIdx].hawk) {
      pushed = true;
      push(s, curIdx, lastIdx, i);
      if (isSolve(s->board[*lastIdx])) {
        int ret = s->board[*lastIdx].step;
        if (verbose) {
          print(s, *lastIdx, ret);
        }
        return ret;
      }
      if (!inState(s, *lastIdx)) {
        (*lastIdx)++;
      }
    }
  }
  return pushed ? 0 : -1;
}

int notPruning(state *s, int curIdx, int *lastIdx, bool verbose) {
  int ret = -1;
  for (int i = 0; i < s->board[curIdx].col; i++) {
    while (i < s->board[curIdx].col && colFinish(s->board[curIdx], i)) {
      i++;
    }
    push(s, curIdx, lastIdx, i);
    if (isSolve(s->board[*lastIdx])) {
      ret = s->board[*lastIdx].step;
      if (verbose) {
        print(s, *lastIdx, ret);
      }
      return ret;
    }
    if (!inState(s, *lastIdx)) {
      (*lastIdx)++;
    }
  }
  return ret;
}
