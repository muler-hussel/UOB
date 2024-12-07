All words containing the letter Q, capitalized:
grep 'Q' /usr/share/dict/words

All words starting with the letter R, in either upper or lower case:
grep -i '^r' /usr/share/dict/words

All words ending in j:
grep 'j$' /usr/share/dict/words

The number of words containing the letter Q, ignoring case:
grep -i 'q' /usr/share/dict/words | wc -l

The first five words containing the letter sequence 'cl':
grep 'cl' /usr/share/dict/words | head -n 5

All words containing the sequence "kp", but not "ckp":
grep 'kp' /usr/share/dict/words | grep -v 'ckp'

The last 15 words of exactly two letters:
grep '^..$' /usr/share/dict/words | tail -n 15

All three-letter words with no vowels (aeiou):
grep '^[^AEIOUaeiou][^AEIOUaeiou][^AEIOUaeiou]$' /usr/share/dict/words

All words of exactly 7 letters, where the third one is an e and the word ends with "-ded":
grep '^..e.ded$' /usr/share/dict/words

Find all words that start with a P (whether capitalized or not), and contain at least four instances of the letter a:
grep -i '^p[^a]*a[^a]*a[^a]*a[^a]*a$' /usr/share/dict/words

Find all dictionary words ending in 'ay' and replace 'day' with 'week':
grep 'ay$' /usr/share/dict/words | sed 's/day/week/'

In the same selection as above, replace all words that begin with 's' with the word 'sway'.
grep 'ay$' /usr/share/dict/words | sed 's/^s.*/sway/'

Duplicate the match after a space for lines containing 'day' (e.g., 'saturday' becomes 'saturday day'):
grep 'ay$' /usr/share/dict/words | sed 's/day/& day'

Turn lines ending in 'day' into "Xday or Xweek", where X is the part of the word before 'day':
grep 'day$' /usr/share/dict/words | sed 's/\(.*\)day/\1day or \1week/'

Flip words ending in 'way' or 'day', so 'someday' becomes 'day (some)' and 'speedway' becomes 'way (speed)':
grep -E'(way|day)$' /usr/share/dict/words | sed 's/\(.*\)\(way|day\)/\2 (\1)/'

Difference between s/a/e/ and s/a/e/g
echo "banana" | sed 's/a/e/'
benana
echo "banana" | sed 's/a/e/g'
benene

grep -o counts each occurrence of pattern, even if they appear multiple times on the same line.
grep -c counts lines containing at least one match, regardless of how many times pattern appears in that line.

encyclo(p|ae)dia
colou?r

^[a-zA-Z]{2}\d\s?\d[a-zA-Z]{2}$

^(([A-Z]{1,2}[0-9][A-Z0-9]?|ASCN|STHL|TDCU|BBND|[BFS]IQQ|PCRN|TKCA) ?[0-9][A-Z]{2}|BFPO ?[0-9]{1,4}|(KY[0-9]|MSR|VG|AI)[ -]? [0-9]{4}|[A-Z]{2} ?[0-9]{2}|GE ?CX|GIR ?0A{2}|SAN ?TA1)$
one or two uppercase letters + one digit + optionally matches one uppercase letter or one digit
OR
specific postcodes for British Overseas Territories,  [BFS]IQQ Matches BIQQ, FIQQ, SIQQ
+
optional space + one digit + two uppercase letter
OR
BFPO + optional space + 1 to 4 digits
OR
KY + 1 digit OR MSR|VG|AI
+ 
Optionally allows a space or a dash + 4 digits OR 2 uppercase letters + optional space + 2 digits + GE ?CX|GIR ?0A{2}|SAN ?TA1