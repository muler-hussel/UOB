#include <stdio.h>
#include <stdlib.h>

void sTrian(char **canvas, int x, int y, int size);

int main(int argc, char *argv[])
{
  int size = 4;
  if (argc > 1)
  {
    size = 0;
    for (int i = 0; argv[1][i] != EOF; i++)
    {
      size = size * 10 + argv[1][i];
    }
  }

  if (size <= 0 || (size & (size - 1) != 0))
  {
    return 1;
  }

  char **canvas = malloc(size * sizeof(char *));
  for (int i = 0; i < size; i++)
  {
    canvas[i] = malloc(2 * size * sizeof(char));
    for (int j = 0; j < 2 * size - 1; j++)
    {
      canvas[i][j] = ' ';
    }
  }

  sTrian(canvas, 0, size - 1, size);

  for (int i = 0; i < size; i++)
  {
    for (int j = 0; j < 2 * size - 1; j++)
    {
      printf("%c", canvas[i][j]);
    }
    printf("\n");
  }

  for (int i = 0; i < size; i++)
  {
    free(canvas[i]);
  }
  free(canvas);
  return 0;
}

void sTrian(char **canvas, int x, int y, int size)
{
  if (size == 1)
  {
    canvas[x][y] = '#';
    return;
  }

  int newSize = size / 2;
  sTrian(canvas, x, y, newSize);
  sTrian(canvas, x + newSize, y - newSize, newSize);
  sTrian(canvas, x + newSize, y + newSize, newSize);
  return;
}