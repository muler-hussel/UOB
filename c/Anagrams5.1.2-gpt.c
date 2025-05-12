#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_WORD_LENGTH 100
#define MAX_WORDS 10000

// Helper struct to store a word and its sorted (normalized) form
typedef struct
{
  char original[MAX_WORD_LENGTH];
  char sorted[MAX_WORD_LENGTH];
} Word;

// Function to sort characters in a string
void sort_string(char *str)
{
  int len = strlen(str);
  for (int i = 0; i < len - 1; i++)
  {
    for (int j = i + 1; j < len; j++)
    {
      if (str[i] > str[j])
      {
        char temp = str[i];
        str[i] = str[j];
        str[j] = temp;
      }
    }
  }
}

// Function to load words from file into the Word array
int load_words(const char *filename, Word words[])
{
  FILE *file = fopen(filename, "r");
  if (!file)
  {
    perror("Unable to open file");
    return -1;
  }

  int count = 0;
  while (fscanf(file, "%s", words[count].original) == 1)
  {
    // Copy original to sorted and sort it
    strcpy(words[count].sorted, words[count].original);
    sort_string(words[count].sorted);
    count++;
    if (count >= MAX_WORDS)
    {
      printf("Reached maximum number of words allowed.\n");
      break;
    }
  }

  fclose(file);
  return count;
}

// Function to find and print anagram groups
void find_anagrams(Word words[], int count)
{
  int *used = calloc(count, sizeof(int)); // Track words already grouped

  for (int i = 0; i < count; i++)
  {
    if (used[i])
      continue; // Skip if already part of a group

    int found_anagram = 0;
    printf("Anagram group: %s", words[i].original);
    used[i] = 1;

    for (int j = i + 1; j < count; j++)
    {
      if (!used[j] && strcmp(words[i].sorted, words[j].sorted) == 0)
      {
        printf(", %s", words[j].original);
        used[j] = 1;
        found_anagram = 1;
      }
    }

    if (found_anagram)
      printf("\n");
  }

  free(used);
}

int main()
{
  Word words[MAX_WORDS];
  int word_count = load_words("dictionary.txt", words);
  if (word_count == -1)
  {
    return 1;
  }

  find_anagrams(words, word_count);

  return 0;
}
