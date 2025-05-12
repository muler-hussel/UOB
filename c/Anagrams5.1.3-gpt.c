#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_WORD_LENGTH 50
#define MAX_WORDS 10000

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

// Function to check if two strings are anagrams
int are_anagrams(char *str1, char *str2)
{
  char sorted1[MAX_WORD_LENGTH];
  char sorted2[MAX_WORD_LENGTH];
  strcpy(sorted1, str1);
  strcpy(sorted2, str2);
  sort_string(sorted1);
  sort_string(sorted2);
  return strcmp(sorted1, sorted2) == 0;
}

// Function to load words from a file into an array
int load_words(const char *filename, char words[][MAX_WORD_LENGTH])
{
  FILE *file = fopen(filename, "r");
  if (!file)
  {
    perror("Unable to open file");
    return -1;
  }

  int count = 0;
  while (fscanf(file, "%s", words[count]) == 1)
  {
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

// Function to find pairs of words that make an anagram of the target
void find_anagram_pairs(char words[][MAX_WORD_LENGTH], int word_count, char *target)
{
  char target_sorted[MAX_WORD_LENGTH];
  strcpy(target_sorted, target);
  sort_string(target_sorted);

  for (int i = 0; i < word_count; i++)
  {
    for (int j = i + 1; j < word_count; j++)
    {
      // Concatenate the words and check if they are anagrams of the target
      char combined[MAX_WORD_LENGTH * 2];
      snprintf(combined, sizeof(combined), "%s%s", words[i], words[j]);
      if (are_anagrams(combined, target_sorted))
      {
        printf("%s + %s -> %s\n", words[i], words[j], target);
      }
    }
  }
}

int main()
{
  char words[MAX_WORDS][MAX_WORD_LENGTH];
  char target[MAX_WORD_LENGTH];

  printf("Enter the target word: ");
  scanf("%s", target);

  int word_count = load_words("dictionary.txt", words);
  if (word_count == -1)
  {
    return 1;
  }

  find_anagram_pairs(words, word_count, target);

  return 0;
}
