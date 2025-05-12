#include "rot18.h"

char rot13A(char c);
char rot13a(char c);
char rot5(char c);

void rot(char str[]){
    // your code here
    for (int i = 0; i < strlen(str); i++) {
      char c = str[i];
      if (isdigit(c)) {
        str[i] = rot5(c);
      } else if (islower(c)) {
        str[i] = rot13a(c);
      } else if (isupper(c)) {
        str[i] = rot13A(c);
      }
    }
}

// any other functions here
char rot13A(char c) {
  return (c - 'A' + 13) % 26 + 'A';
}

char rot13a(char c) {
  return (c - 'a' + 13) % 26 + 'a';
}

char rot5(char c) {
  return (c - '0' + 5) % 10 + '0';
}

void your_tests(void){
    // your tests here
    char str[100];
    strcpy(str, "");
    rot(str);
    assert(strcmp(str, "") == 0);
    strcpy(str, "6870");
    rot(str);
    assert(strcmp(str, "1325") == 0);
}
