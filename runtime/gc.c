#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// "new" a new object, do necessary initializations, and
// return the pointer (reference).
/*    -----------------------------------------
      | vptr | v0 | v1 | ...      | v_{size-1}|                           
      -----------------------------------------
      ^      \                                /
      |       \<------------- size --------->/
      |
      p (returned address)
*/
void *Tiger_new (void *vtable, int size)
{
  // You should write 4 statements for this function.
  // #1: "malloc" a chunk of memory (be careful of the size) :
  
  // #2: clear this chunk of memory (zero off it):
  
  // #3: set up the "vptr" pointer to the value of "vtable":
  
  // #4: return the pointer 
  
}

// "new" an array of size "length", do necessary
// initializations. And each array comes with an
// extra "header" storing the array length.
// This function should return the starting address
// of the array elements, but not the starting address of
// the array chunk. 
/*    ---------------------------------------------
      | length | e0 | e1 | ...      | e_{length-1}|                           
      ---------------------------------------------
               ^
               |
               p (returned address)
*/
void *Tiger_new_array (int length)
{
  // You can use the C "malloc" facilities, as above.
  // Your code here:
  
}
