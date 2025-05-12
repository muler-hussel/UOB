#include "crushit.h"
#include "mydefs.h"

bool allDots(const state *s, int i);
bool initialiseString(state *s, const char *str);
void match2Lower(state *s, int height);
void swap(char *a, char *b);

bool initialise(state *s, const char *str)
{
  if (s == NULL || str == NULL)
  {
    return false;
  }
  FILE *fp = fopen(str, "r");
  if (fp == NULL)
  {
    return initialiseString(s, str);
  }
  char c;
  int row = 0, col = 0;
  while ((c = fgetc(fp)) != EOF)
  {
    if (c == '\n')
    {
      continue;
    }
    s->board[row][col] = c;
    printf("%c", s->board[row][col]);
    col++;
    if (col == WIDTH)
    {
      col = 0;
      row++;
    }
  }
  s->bottom = row - 1;
  fclose(fp);
  return true;
}

bool initialiseString(state *s, const char *str)
{
  int len = strlen(str);
  if (len % WIDTH != 0)
  {
    return false;
  }
  for (int i = 0; i < len; i++)
  {
    if (!isupper(str[i]) && str[i] != '.')
    {
      return false;
    }
  }
  for (int i = 0; str[i]; i++)
  {
    s->board[i / WIDTH][i % WIDTH] = str[i];
  }
  s->bottom = len / WIDTH - 1;
  return true;
}

bool tostring(const state *s, char *str)
{
  if (s == NULL || str == NULL)
  {
    return false;
  }
  int idx = 0, highest = 0;
  while (highest <= s->bottom)
  {
    if (allDots(s, highest))
    {
      highest++;
    }
    else
    {
      break;
    }
  }
  for (int i = highest; i <= s->bottom; i++)
  {
    for (int j = 0; j < WIDTH; j++)
    {
      str[idx++] = s->board[i][j];
    }
  }
  str[idx] = '\0';
  return true;
}

bool allDots(const state *s, int i)
{
  for (int j = 0; j < WIDTH; j++)
  {
    if (s->board[i][j] != '.')
    {
      return false;
    }
  }
  return true;
}

bool matches(state *s)
{
  if (s == NULL)
  {
    return false;
  }
  int height = s->bottom;
  match2Lower(s, height);
  int removed = 0;
  for (int i = height; i > height - HEIGHT; i--)
  {
    for (int j = 0; j < WIDTH; j++)
    {
      if (islower(s->board[i][j]))
      {
        removed++;
        s->board[i][j] = '.';
      }
    }
  }
  return removed > 0;
}

void match2Lower(state *s, int height)
{
  for (int i = height; i > height - HEIGHT; i--)
  {
    for (int j = 0; j < WIDTH; j++)
    {
      char c1 = tolower(s->board[i][j]);
      if (j < WIDTH - 2)
      {
        char c2 = tolower(s->board[i][j + 1]), c3 = tolower(s->board[i][j + 2]);
        if (c1 == c2 && c1 == c3)
        {
          s->board[i][j] = c1;
          s->board[i][j + 1] = c2;
          s->board[i][j + 2] = c3;
        }
      }

      if (i > height - HEIGHT + 2)
      {
        char c4 = tolower(s->board[i - 1][j]), c5 = tolower(s->board[i - 2][j]);
        if (c1 == c4 && c1 == c5)
        {
          s->board[i][j] = c1;
          s->board[i - 1][j] = c4;
          s->board[i - 2][j] = c5;
        }
      }
    }
  }
}

bool dropblocks(state *s)
{
  if (s == NULL)
  {
    return false;
  }
  int height = s->bottom;

  for (int j = 0; j < WIDTH; j++)
  {
    for (int i = height; i >= 0; i--)
    {
      int idx = i - 1;
      if (s->board[i][j] == '.')
      {
        while (idx >= 0)
        {
          if (s->board[idx][j] != '.')
          {
            swap(&s->board[i][j], &s->board[idx][j]);
            break;
          }
          idx--;
        }
      }
    }
  }
  return true;
}

void swap(char *a, char *b)
{
  char tmp = *a;
  *a = *b;
  *b = tmp;
}

void test(void)
{
  state s;

  assert(initialise(&s, "eleven.txt") == true);
  for (int i = 0; i < 11; i++)
  {
    assert(matches(&s));
    assert(dropblocks(&s));
  }

  assert(initialise(&s, "twelve.txt") == true);
  for (int i = 0; i < 12; i++)
  {
    assert(matches(&s));
    assert(dropblocks(&s));
  }
}