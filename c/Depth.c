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