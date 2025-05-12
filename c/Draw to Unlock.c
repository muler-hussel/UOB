#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>

#define MAX_WIDTH 10

typedef struct Table
{
  int width;
  int grid[MAX_WIDTH][MAX_WIDTH];
} Table;

void initialize(Table *t, int width);
void dfs(Table *t, int x, int y, bool **visited, int path[], int step, int len);
void gen(Table *t, int len);

int main()
{
  Table t;
  initialize(&t, 3);
  gen(&t, 9);
  return 0;
}

void initialize(Table *t, int width)
{
  t->width = width;
  for (int i = 0; i < width; i++)
  {
    for (int j = 0; j < width; j++)
    {
      t->grid[i][j] = i * width + j + 1;
    }
  }
}

void dfs(Table *t, int x, int y, bool **visited, int path[], int step, int len)
{
  if (x < 0 || x >= t->width || y < 0 || y >= t->width || visited[x][y])
    return;

  visited[x][y] = true;
  path[step] = t->grid[x][y];

  if (step == len - 1)
  {
    for (int i = 0; i < len; i++)
    {
      printf("%i", path[i]);
    }
    printf("\n");
  }

  dfs(t, x + 1, y, visited, path, step + 1, len);
  dfs(t, x - 1, y, visited, path, step + 1, len);
  dfs(t, x, y + 1, visited, path, step + 1, len);
  dfs(t, x, y - 1, visited, path, step + 1, len);
  visited[x][y] = false;
  return;
}

void gen(Table *t, int len)
{
  bool **visited = malloc(t->width * sizeof(bool *));
  for (int i = 0; i < t->width; i++)
  {
    visited[i] = calloc(t->width, sizeof(bool));
  }

  int *path = malloc(len * sizeof(int));

  for (int i = 0; i < t->width; i++)
  {
    for (int j = 0; j < t->width; j++)
    {
      dfs(t, i, j, visited, path, 0, len);
    }
  }

  for (int i = 0; i < t->width; i++)
  {
    free(visited[i]);
  }
  free(visited);
  free(path);
}