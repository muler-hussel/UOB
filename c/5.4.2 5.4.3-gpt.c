#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define MAX_WORDS 10000
#define MAX_WORD_LENGTH 100

// Function to insert a word in a sorted position in an array
void insert_sorted(char *words[], int *num_words, char *new_word)
{
  int i = *num_words;
  while (i > 0 && strcmp(new_word, words[i - 1]) < 0)
  {
    words[i] = words[i - 1];
    i--;
  }
  words[i] = new_word;
  (*num_words)++;
}

// Linear search to check if a word exists in the array
int linear_search(char *words[], int num_words, const char *word)
{
  for (int i = 0; i < num_words; i++)
  {
    if (strcmp(words[i], word) == 0)
    {
      return 1; // Word found
    }
  }
  return 0; // Word not found
}

// Binary search to check if a word exists in the array
int binary_search(char *words[], int num_words, const char *word)
{
  int left = 0, right = num_words - 1;
  while (left <= right)
  {
    int mid = left + (right - left) / 2;
    int cmp = strcmp(words[mid], word);
    if (cmp == 0)
    {
      return 1; // Word found
    }
    if (cmp < 0)
    {
      left = mid + 1;
    }
    else
    {
      right = mid - 1;
    }
  }
  return 0; // Word not found
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

  // Read each word from the file and insert it into the sorted array
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
    insert_sorted(words, &num_words, new_word);
  }

  fclose(file);

  clock_t end = clock(); // End timing
  double time_taken = (double)(end - start) / CLOCKS_PER_SEC;
  printf("Time taken to build the list: %f seconds\n", time_taken);

  // Allow the user to search for words using Linear or Binary search
  char search_word[MAX_WORD_LENGTH];
  while (1)
  {
    printf("\nEnter a word to search (or type 'exit' to quit): ");
    scanf("%99s", search_word);
    if (strcmp(search_word, "exit") == 0)
    {
      break;
    }

    // Search using Linear Search
    if (linear_search(words, num_words, search_word))
    {
      printf("Linear Search: The word '%s' is present in the list.\n", search_word);
    }
    else
    {
      printf("Linear Search: The word '%s' is NOT present in the list.\n", search_word);
    }

    // Search using Binary Search
    if (binary_search(words, num_words, search_word))
    {
      printf("Binary Search: The word '%s' is present in the list.\n", search_word);
    }
    else
    {
      printf("Binary Search: The word '%s' is NOT present in the list.\n", search_word);
    }
  }

  // Free allocated memory for each word
  for (int i = 0; i < num_words; i++)
  {
    free(words[i]);
  }

  return 0;
}
