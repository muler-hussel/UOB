#include "crossword.h"
#include "cwddriver.h"
bool isEmpty(const crossword *cw, int i, int j);
bool isBlock(const crossword *cw, int i, int j);
bool leftStart(const crossword *cw, int i, int j);
bool topStart(const crossword *cw, int i, int j);
int traverseDown(const crossword *cw, int i, int j);
int traverseRight(const crossword *cw, int i, int j);

void test(void)
{
}

bool str2crossword(int sz, char *ip, crossword *cw)
{
  if (ip == NULL || cw == NULL)
  {
    return false;
  }
  int size = strlen(ip);
  if (sz < 0 || sz * sz != size)
  {
    return false;
  }
  cw->sz = sz;
  for (int i = 0; i < size; i++)
  {
    cw->arr[i / sz][i % sz] = ip[i];
  }
  return true;
}

void getcluestring(const crossword *c, char *str)
{
  int idxA = 1, idxD = 1, num = 1;
  char strD[BIGSTR];
  str[0] = 'A';
  strD[0] = 'D';

  for (int i = 0; i < c->sz; i++)
  {
    for (int j = 0; j < c->sz; j++)
    {
      if (isEmpty(c, i, j) && (leftStart(c, i, j) || topStart(c, i, j)))
      {
        if (leftStart(c, i, j))
        {
          str[idxA++] = '-';
          if (num >= 10)
          {
            str[idxA++] = num / 10 + '0';
          }
          str[idxA++] = num % 10 + '0';
        }
        if (topStart(c, i, j))
        {
          strD[idxD++] = '-';
          if (num >= 10)
          {
            strD[idxD++] = num / 10 + '0';
          }
          strD[idxD++] = num % 10 + '0';
        }
        num++;
      }
    }
  }
  str[idxA++] = '|';
  str[idxA] = '\0';
  strD[idxD] = '\0';
  strcat(str, strD);
}

// 参数类型不对
int getchecked(const crossword *c)
{
  int empty = 0, ret = 0;
  for (int i = 0; i < c.sz; i++)
  {
    for (int j = 0; j < c.sz; j++)
    {
      if (isEmpty(c, i, j))
      {
        empty++;
        if (leftStart(c, i, j))
        {
          ret += traverseRight(c, i, j);
        }
        if (topStart(c, i, j))
        {
          ret += traverseDown(c, i, j);
        }
      }
    }
  }
  return round((float)(ret - empty) / empty * 100);
}

bool isEmpty(const crossword *cw, int i, int j)
{
  if (cw->arr[i][j] == ' ' || cw->arr[i][j] == '.')
  {
    return true;
  }
  return false;
}

bool isBlock(const crossword *cw, int i, int j)
{
  if (cw->arr[i][j] == 'X' || cw->arr[i][j] == '*')
  {
    return true;
  }
  return false;
}

bool leftStart(const crossword *cw, int i, int j)
{
  if (j == 0 || isBlock(cw, i, j - 1))
  {
    if (j != cw->sz - 1 && isEmpty(cw, i, j + 1))
    {
      return true;
    }
  }
  return false;
}

bool topStart(const crossword *cw, int i, int j)
{
  if (i == 0 || isBlock(cw, i - 1, j))
  {
    if (i != cw->sz - 1 && isEmpty(cw, i + 1, j))
    {
      return true;
    }
  }
  return false;
}

int traverseRight(const crossword *cw, int i, int j)
{
  int ret = 0;
  while (j < cw->sz && isEmpty(cw, i, j))
  {
    ret++;
    j++;
  }
  return ret;
}

int traverseDown(const crossword *cw, int i, int j)
{
  int ret = 0;
  while (i < cw->sz && isEmpty(cw, i, j))
  {
    ret++;
    i++;
  }
  return ret;
}
