#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define MAX_WORD_LENGTH 100

// Node structure for linked list
typedef struct Node
{
  char word[MAX_WORD_LENGTH];
  struct Node *next;
} Node;

// Function to create a new node
Node *create_node(const char *word)
{
  Node *new_node = (Node *)malloc(sizeof(Node));
  if (new_node == NULL)
  {
    perror("Failed to allocate memory");
    exit(1);
  }
  strcpy(new_node->word, word);
  new_node->next = NULL;
  return new_node;
}

// Function to insert a word in sorted order in the linked list
void insert_sorted(Node **head, const char *word)
{
  Node *new_node = create_node(word);
  Node *current = *head;
  Node *previous = NULL;

  // Find the correct position for insertion
  while (current != NULL && strcmp(word, current->word) > 0)
  {
    previous = current;
    current = current->next;
  }

  // Insert at the beginning or in the middle/end
  if (previous == NULL)
  {
    new_node->next = *head;
    *head = new_node;
  }
  else
  {
    previous->next = new_node;
    new_node->next = current;
  }
}

// Function to perform linear search in linked list
int linear_search(Node *head, const char *word)
{
  Node *current = head;
  while (current != NULL)
  {
    if (strcmp(current->word, word) == 0)
    {
      return 1; // Word found
    }
    current = current->next;
  }
  return 0; // Word not found
}

// Function to free the linked list
void free_list(Node *head)
{
  Node *current = head;
  while (current != NULL)
  {
    Node *next_node = current->next;
    free(current);
    current = next_node;
  }
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

  Node *head = NULL;
  char buffer[MAX_WORD_LENGTH];
  int word_count = 0;

  clock_t start = clock(); // Start timing for list building

  // Read words from the file and insert them in sorted order
  while (fscanf(file, "%99s", buffer) == 1)
  {
    insert_sorted(&head, buffer);
    word_count++;
  }

  clock_t end = clock(); // End timing for list building
  double build_time = (double)(end - start) / CLOCKS_PER_SEC;
  printf("Time taken to build the list with %d words: %f seconds\n", word_count, build_time);

  fclose(file);

  // Allow user to search for words and measure search time
  char search_word[MAX_WORD_LENGTH];
  while (1)
  {
    printf("\nEnter a word to search (or type 'exit' to quit): ");
    scanf("%99s", search_word);
    if (strcmp(search_word, "exit") == 0)
    {
      break;
    }

    start = clock(); // Start timing for linear search
    int found = linear_search(head, search_word);
    end = clock(); // End timing for linear search

    double search_time = (double)(end - start) / CLOCKS_PER_SEC;
    if (found)
    {
      printf("The word '%s' is present in the list.\n", search_word);
    }
    else
    {
      printf("The word '%s' is NOT present in the list.\n", search_word);
    }
    printf("Time taken for linear search: %f seconds\n", search_time);
  }

  // Free the linked list
  free_list(head);

  return 0;
}
