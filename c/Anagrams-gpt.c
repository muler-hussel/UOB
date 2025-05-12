#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_WORD_LENGTH 100
#define DICTIONARY_FILE "dictionary.txt"

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
  // If lengths differ, they can't be anagrams
  if (strlen(str1) != strlen(str2))
  {
    return 0;
  }

  // Sort both strings
  char sorted_str1[MAX_WORD_LENGTH], sorted_str2[MAX_WORD_LENGTH];
  strcpy(sorted_str1, str1);
  strcpy(sorted_str2, str2);

  sort_string(sorted_str1);
  sort_string(sorted_str2);

  // Compare sorted versions of both strings
  return strcmp(sorted_str1, sorted_str2) == 0;
}

int main()
{
  char input[MAX_WORD_LENGTH];
  printf("Enter an anagram: ");
  scanf("%s", input);

  // Open the dictionary file
  FILE *file = fopen(DICTIONARY_FILE, "r");
  if (!file)
  {
    perror("Unable to open dictionary file");
    return 1;
  }

  printf("Possible words:\n");

  // Buffer to store each word from the dictionary
  char word[MAX_WORD_LENGTH];
  int found = 0;

  // Loop through each word in the file
  while (fscanf(file, "%s", word) == 1)
  {
    // Check if the word is an anagram of the input
    if (are_anagrams(input, word))
    {
      printf("%s\n", word);
      found = 1;
    }
  }

  if (!found)
  {
    printf("No matching anagrams found.\n");
  }

  // Close the file
  fclose(file);
  return 0;
}
