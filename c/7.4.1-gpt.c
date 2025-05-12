#include <stdio.h>
#include <stdlib.h>

// Function to check if a cell (x, y) is part of the Sierpinski Carpet
char sierpinski_carpet(int x, int y)
{
  while (x > 0 || y > 0)
  {
    if (x % 3 == 1 && y % 3 == 1)
    {
      return ' '; // Middle square, should be empty
    }
    x /= 3;
    y /= 3;
  }
  return '#'; // Part of the fractal
}

// Function to generate and print the Sierpinski Carpet
void generate_carpet(int size)
{
  for (int i = 0; i < size; i++)
  {
    for (int j = 0; j < size; j++)
    {
      putchar(sierpinski_carpet(i, j));
    }
    putchar('\n');
  }
}

int main(int argc, char *argv[])
{
  int size = 27; // Default size, should be a power of 3 for the best visual result
  if (argc > 1)
  {
    size = atoi(argv[1]);
  }

  if (size % 3 != 0 || size <= 0)
  {
    fprintf(stderr, "Size should be a positive power of 3.\n");
    return 1;
  }

  generate_carpet(size);
  return 0;
}
