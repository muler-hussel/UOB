#include <stdio.h>

#define MAX_SIZE 20

typedef struct Maze
{
  int width;
  int height;
  char cell[MAX_SIZE][MAX_SIZE];
} Maze;

int findEntrance(Maze *maze);
int sloveMaze(Maze *maze, int x, int y);
int startBlock(Maze *maze);
void prinfMaze(Maze *maze);
void secAB(Maze *maze, int bx, int by);

int main(int argc, char *argv[])
{
  if (argc < 2)
  {
    return 1;
  }
  FILE *file = fopen(argv[1], "r");
  if (!file)
  {
    return 1;
  }

  Maze maze;

  if (fscanf(file, "%d,%d\n", &maze.width, &maze.height) != 2 || maze.width > MAX_SIZE || maze.height > MAX_SIZE)
  {
    fclose(file);
    return 1;
  }

  for (int i = 0; i < maze.height; i++)
  {
    for (int j = 0; j < maze.width; j++)
    {
      maze.cell[i][j] = fgetc(file);
      if (maze.cell[i][j] == '\n')
        j--; // Ignore newline characters
    }
  }

  fclose(file);

  // int e = findEntrance(&maze), ex, ey;
  // if (e >= MAX_SIZE)
  // {
  //   ex = e / MAX_SIZE;
  //   ey = 0;
  // }
  // else if (e < MAX_SIZE)
  // {
  //   ex = 0;
  //   ey = e;
  // }
  // else
  // {
  //   return 1;
  // }

  // return sloveMaze(&maze, ex, ey);

  int b = startBlock(&maze);
  secAB(&maze, 0, b);
  prinfMaze(&maze);

  return 0;
}

int findEntrance(Maze *maze)
{
  for (int i = 0; i < maze->width; i++)
  {
    if (maze->cell[0][i] == '.')
      return i;
  }
  for (int i = 1; i < maze->height; i++)
  {
    if (maze->cell[i][0] == '.')
      return i * MAX_SIZE;
  }
  return -1;
}

int sloveMaze(Maze *maze, int x, int y)
{
  if (x == maze->height - 1 || y == maze->width - 1)
  {
    return 0;
  }

  if (x < 0 || x >= maze->height || y < 0 || y >= maze->width || maze->cell[x][y] != '.')
  {
    return 1;
  }

  maze->cell[x][y] = '0';
  sloveMaze(maze, x - 1, y);
  sloveMaze(maze, x + 1, y);
  sloveMaze(maze, x, y - 1);
  sloveMaze(maze, x, y + 1);
  maze->cell[x][y] = '.';
  return 1;
}

int startBlock(Maze *maze)
{
  for (int i = 0; i < maze->width; i++)
  {
    if (maze->cell[0][i] == '#')
      return i;
  }
  return -1;
}

void prinfMaze(Maze *maze)
{
  for (int i = 0; i < maze->height; i++)
  {
    for (int j = 0; j < maze->width; j++)
    {
      if (maze->cell[i][j] == '#')
      {
        printf("B");
      }
      else
      {
        printf("%c", maze->cell[i][j]);
      }
    }
    printf("\n");
  }
}

void secAB(Maze *maze, int bx, int by)
{
  if (bx < 0 || bx >= maze->height || by < 0 || by >= maze->width || maze->cell[bx][by] != '#')
  {
    return;
  }

  maze->cell[bx][by] = 'A';
  secAB(maze, bx + 1, by);
  secAB(maze, bx - 1, by);
  secAB(maze, bx, by + 1);
  secAB(maze, bx, by - 1);
  return;
}
