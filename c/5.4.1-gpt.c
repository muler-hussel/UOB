#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define MAX_WORDS 10000
#define MAX_WORD_LENGTH 100

// Function to insert word into sorted position in array
void insert_sorted(char *words[], int *num_words, char *new_word)
{
  int i = *num_words;
  // Place the new word at the end and shift it to the correct position
  while (i > 0 && strcmp(new_word, words[i - 1]) < 0)
  {
    words[i] = words[i - 1];
    i--;
  }
  words[i] = new_word; // Insert word at correct position
  (*num_words)++;
}

int main(int argc, char *argv[])
{
  if (argc < 2)
  {
    fprintf(stderr, "Usage: %s <filename>\n", argv[0]);
    return 1;
  }

  FILE *file = fopen(argv[1], "r");
  if (!file)
  {
    perror("Error opening file");
    return 1;
  }

  char *words[MAX_WORDS];
  int num_words = 0;
  char buffer[MAX_WORD_LENGTH];

  clock_t start = clock(); // Start timing

  // Read each word from file and insert into sorted array
  while (fscanf(file, "%99s", buffer) == 1)
  {
    char *new_word = malloc(strlen(buffer) + 1);
    if (!new_word)
    {
      fprintf(stderr, "Memory allocation failed\n");
      fclose(file);
      return 1;
    }
    strcpy(new_word, buffer);

    // Insert the word in sorted order
    insert_sorted(words, &num_words, new_word);
  }

  clock_t end = clock(); // End timing

  fclose(file);

  // Print time taken to build the list
  double time_taken = (double)(end - start) / CLOCKS_PER_SEC;
  printf("Time taken to build the list: %f seconds\n", time_taken);

  // Optionally print sorted words
  printf("Sorted words:\n");
  for (int i = 0; i < num_words; i++)
  {
    printf("%s\n", words[i]);
    free(words[i]); // Free each allocated word
  }

  return 0;
}
