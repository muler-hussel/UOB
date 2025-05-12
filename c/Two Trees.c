#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <time.h>

#define STRSIZE 5000

struct node {
    char c;
    struct node *left;
    struct node *right;
};
typedef struct node Node;

Node *MakeNode(char c);
void InsertRandom(Node *t, Node *n);
char *PrintTree(Node *t);
int MaxDepth(Node *t);
int AreIdentical(Node *t1, Node *t2);

int main(void)
{
    char c;
    Node *tree1 = MakeNode('A');
    Node *tree2 = MakeNode('A');
    Node *n;

    srand(time(NULL));
    // Generate the first tree
    for(c = 'B'; c < 'G'; c++) {
        n = MakeNode(c);
        InsertRandom(tree1, n);
    }
    // Generate the second tree
    for(c = 'B'; c < 'G'; c++) {
        n = MakeNode(c);
        InsertRandom(tree2, n);
    }

    // Print the trees
    printf("Tree 1: %s\n", PrintTree(tree1));
    printf("Tree 2: %s\n", PrintTree(tree2));

    // Check if the trees are identical
    if (AreIdentical(tree1, tree2)) {
        printf("The two trees are identical.\n");
    } else {
        printf("The two trees are not identical.\n");
    }

    return 0;
}

Node *MakeNode(char c)
{
    Node *n = (Node *)calloc(1, sizeof(Node));
    assert(n != NULL);
    n->c = c;
    return n;
}

void InsertRandom(Node *t, Node *n)
{
    if((rand() % 2) == 0) { /* Left */
        if(t->left == NULL) {
            t->left = n;
        }
        else {
            InsertRandom(t->left, n);
        }
    }
    else { /* Right */
        if(t->right == NULL) {
            t->right = n;
        }
        else {
            InsertRandom(t->right, n);
        }
    }
}

char *PrintTree(Node *t)
{
    char *str;

    assert((str = calloc(STRSIZE, sizeof(char))) != NULL);
    if(t == NULL) {
        strcpy(str, "*");
        return str;
    }
    sprintf(str, "%c (%s) (%s)", t->c, PrintTree(t->left), PrintTree(t->right));
    return str;
}

int MaxDepth(Node *t)
{
    if(t == NULL) {
        return 0; // An empty tree has a depth of 0.
    }

    int leftDepth = MaxDepth(t->left);
    int rightDepth = MaxDepth(t->right);

    // Return the greater of the two depths, plus one for the current node.
    return (leftDepth > rightDepth ? leftDepth : rightDepth) + 1;
}

int AreIdentical(Node *t1, Node *t2)
{
    // Base case: both trees are empty
    if (t1 == NULL && t2 == NULL) {
        return 1;
    }

    // If one of them is empty, or if the values don't match, the trees are not identical
    if (t1 == NULL || t2 == NULL || t1->c != t2->c) {
        return 0;
    }

    // Recursively check the left and right subtrees
    return AreIdentical(t1->left, t2->left) && AreIdentical(t1->right, t2->right);
}
