#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
// The Gimple Garbage Collector.

int global_remain=0;
int account=0;
void* prev;
void Tiger_gc ();
//===============================================================//
// The Java Heap data structure.

/*   
      ----------------------------------------------------
      |                        |                         |
      ----------------------------------------------------
      ^\                      /^
      | \<~~~~~~ size ~~~~~~>/ |
    from                       to
*/
struct JavaHeap
{
  int size;         // in bytes, note that this if for semi-heap size
  char *from;       // the "from" space pointer
  char *fromFree;   // the next "free" space in the from space
  char *to;         // the "to" space pointer
  char *toStart;    // "start" address in the "to" space
  char *toNext;     // the next "free" space pointer in the to space
};

// The Java heap, which is initialized by the following
// "heap_init" function.
struct JavaHeap heap;

// Lab 4, exercise 10:
// Given the heap size (in bytes), allocate a Java heap
// in the C heap, initialize the relevant fields.
void Tiger_heap_init (int heapSize)
{
  // You should write 7 statement here:
  // #1: allocate a chunk of memory of size "heapSize" using "malloc"

  char *Java_Heap = (char *)malloc(heapSize);

  // #2: initialize the "size" field, note that "size" field
  // is for semi-heap, but "heapSize" is for the whole heap.

  heap.size = heapSize/2;

  // #3: initialize the "from" field (with what value?)

  heap.from = Java_Heap;

  // #4: initialize the "fromFree" field (with what value?)

  heap.fromFree = Java_Heap;

  // #5: initialize the "to" field (with what value?)

  heap.to = (char *)(heap.from+heap.size);

  // #6: initizlize the "toStart" field with NULL;

  heap.toStart = heap.to;

  // #7: initialize the "toNext" field with NULL;

  heap.toNext = heap.to;

  return;
}

// The "prev" pointer, pointing to the top frame on the GC stack. 

// (see part A of Lab 4)
//===============================================================//
// Object Model And allocation


// Lab 4: exercise 11:
// "new" a new object, do necessary initializations, and
// return the pointer (reference).
/*    ----------------
      | vptr      ---|----> (points to the virtual method table)
      |--------------|
      | isObjOrArray | (0: for normal objects)
      |--------------|
      | length       | (this field should be empty for normal objects)
      |--------------|
      | forwarding   | 
      |--------------|\
p---->| v_0          | \      
      |--------------|  s
      | ...          |  i
      |--------------|  z
      | v_{size-1}   | /e
      ----------------/
*/
// Try to allocate an object in the "from" space of the Java
// heap. Read Tiger book chapter 13.3 for details on the
// allocation.
// There are two cases to consider:
//   1. If the "from" space has enough space to hold this object, then
//      allocation succeeds, return the apropriate address (look at
//      the above figure, be careful);
//   2. if there is no enough space left in the "from" space, then
//      you should call the function "Tiger_gc()" to collect garbages.
//      and after the collection, there are still two sub-cases:
//        a: if there is enough space, you can do allocations just as case 1; 
//        b: if there is still no enough space, you can just issue
//           an error message ("OutOfMemory") and exit.
//           (However, a production compiler will try to expand
//           the Java heap.)
void *Tiger_new (void *vtable, int size)
{
  int * vptr = 0;
  int remain = heap.size-(heap.fromFree-heap.from);
  if(remain>=(size+16))
  {
  	 vptr = (int *)heap.fromFree;
  	 heap.fromFree += size+16;
  	 memset(vptr,0,size+16);
  	 vptr[0] = (int)vtable;
  	 vptr[1] = 0;
  	 vptr[2] = size;
  }
  else
  {
  	 global_remain = remain;
	 Tiger_gc();
	 remain=heap.size-(heap.fromFree-heap.from);
	 if(remain>=(size+16))
	 {
		vptr = (int *)heap.fromFree;
  	 	heap.fromFree += size+16;
  	 	memset(vptr,0,size+16);
  	 	vptr[0] = (int)vtable;
  	 	vptr[1] = 0;
  	 	vptr[2] = size;
	 }
	 else 
	 {
	 	printf("OutOfMemory\n");
	 	exit(0);
	 }
  }
  return vptr;
}

// "new" an array of size "length", do necessary
// initializations. And each array comes with an
// extra "header" storing the array length and other information.
/*    ----------------
      | vptr         | (this field should be empty for an array)
      |--------------|
      | isObjOrArray | (1: for array)
      |--------------|
      | length       |
      |--------------|
      | forwarding   |  (forwarding pointer should point to the "To" heap)
      |--------------|\
p---->| e_0          | \      
      |--------------|  s
      | ...          |  i
      |--------------|  z
      | e_{length-1} | /e
      ----------------/
*/
// Try to allocate an array object in the "from" space of the Java
// heap. Read Tiger book chapter 13.3 for details on the
// allocation.
// There are two cases to consider:
//   1. If the "from" space has enough space to hold this array object, then
//      allocation succeeds, return the apropriate address (look at
//      the above figure, be careful);
//   2. if there is no enough space left in the "from" space, then
//      you should call the function "Tiger_gc()" to collect garbages.
//      and after the collection, there are still two sub-cases:
//        a: if there is enough space, you can do allocations just as case 1; 
//        b: if there is still no enough space, you can just issue
//           an error message ("OutOfMemory") and exit.
//           (However, a production compiler will try to expand
//           the Java heap.)
int *Tiger_new_array (int length)
{
  int remain = heap.size-(heap.fromFree-heap.from);
  int* vptr = 0;
  int size = length*4+16;
  if(remain >= size)
  {
      vptr = (int *)heap.fromFree;
      memset(vptr,0,size);
      vptr[1] = 1;
      vptr[2] = length;
 	  heap.fromFree += size;
  }
  else
  {
  	  global_remain = remain;
  	  Tiger_gc();
  	  remain = heap.size-(heap.fromFree-heap.from);
  	  if(remain>=size)
  	  {
  	  	vptr = (int *)heap.fromFree;
	      memset(vptr,0,size);
	      vptr[1] = 1;
	      vptr[2] = length;
	 	  heap.fromFree += size;
  	  }
  	  else
  	  {
  	  	  printf("OutOfMemory\n");
	 	  exit(0);
  	  }
  }
  return vptr;
}

//===============================================================//
// The Gimple Garbage Collector

// Lab 4, exercise 12:
// A copying collector based-on Cheney's algorithm.
// Copy the object from "from" to "to".   
void* Tiger_forward(void* p)
{ 
    int *obj = (int *)p;
    char *ptr = (char *)p;
    int size = 0;
    //ensure the pointer p is in the "From" heap.
    if(ptr!=0 && ptr<(heap.from+heap.size) && ptr>=heap.from)  
    {   //ensure the forwarding pointer if not in the "To" heap
    	if((char*)obj[3]>=heap.toStart&&(char*)obj[3]<heap.toNext)
		{
			return (void*)obj[3];  
		}
	    if(obj[1] == 0)
	    	size = obj[2]+16;
	    else
	    	size = obj[2]*4+16;
	    obj[3] = (int)heap.toNext;
	    memset(heap.toNext,0,size); 
	    memcpy(heap.toNext,p,size);
	    heap.toNext += size;
	    return (void *)obj[3];
    }
    else
    	return p;
}
void Tiger_gc ()
{
	account++;//轮数
	clock_t  start,end;
	start = clock();
	//----------------------------------------------
	//GC frame
	//---prev
	//---gc_argument_map
	//---first_argument_address
	//---int locals_count
	//---(Dec)local_referenced_variable--1
	//---(Dec)local_referenced_variable--2
	//---.....
	//---(Dec)local_referenced_variable--local_count
	//---End
	//----------------------------------------------
	//Move the useful Object to the heap "To" 
	void *current_frame = prev;
  int i;
  //printf("%d\n",(int)current_frame);
  while(current_frame != 0)
	{
      char *str = *(char **)(current_frame+4);
      //printf("%s",str);
 	    int *argu = *(int **)(current_frame+8);
 	    for(i=0;*(char *)(str+i)!='\0';i++)
 	    {
 	    	if(str[i]=='1')
 	    	{
 	    		argu[i] = (int)Tiger_forward((int*)argu[i]);  //update the reference variable's address
 	    	}
 	    }
    	//solve the local reference varibale on the GC_frame
    	int local_num = *(int *)(current_frame+12);
    	if(local_num>0)
    	{
    		int *a = &current_frame[4];
	    	for(i=0;i<local_num;i++)
  			{
  				a[i] = Tiger_forward((int *)a[i]);
  			}
		  }
		  current_frame = *(int **)current_frame;  //set the current_frame to be the previous function frame			
  }
	//----------------------------------------------
	//Object Structure
	    /*    ----------------
	      | vptr         | (this field should be empty for an array)
	      |--------------|
	      | isObjOrArray | (0:ordinary variable ; 1:Int_Array)
	      |--------------|
	      | lengt or size|
	      |--------------|
	      | forwarding   | 
	      |--------------|\
	p---->| e_0          | \      
	      |--------------|  s
	      | ...          |  i
	      |--------------|  z
	      | e_{length-1} | /e
	      ----------------/
	*/
	//----------------------------------------------
    //scanning the "To" heap, move something useful to the "To" heap ,
    //because those thing will be refered by the real Object which already in the "To" heap
	//use the BFS algorithm 
    char *scan = heap.toStart;
    int *scan_int = (int *)scan;
    char *obj_vptr;
    int num = 0,size = 0;
    while(scan < heap.toNext)
    {
    	int * vptr_ = *(int**)(scan_int);
    	obj_vptr = (char *)(vptr_[0]);
	    if((int)scan_int[1]==0)
	    {
			for(num=0;obj_vptr[num]!='\0';num++)
		    {
		    	if(obj_vptr[num]=='1')
		    	{
		    		scan_int[4+num] = (int)Tiger_forward((int *)scan_int[4+num]);
		    	}
		    }
		    size = scan[2]+16;
		    scan += size;
	    }
	    else
	    {
	    	size = scan[2];
			size *= 4;
			size += 16;
			scan += size;
	    }        
    }

	//ok! right now! Garbage collection is finished!!!!!
	char *temp = heap.from;
	heap.from = heap.toStart;
    heap.toStart = temp;
    heap.fromFree = heap.toNext;
    heap.toNext = heap.toStart;
    heap.to = heap.toStart;
    int free_size = heap.size-global_remain-(heap.fromFree-heap.from);
    end = clock();
    long tim=(long)(end-start);
    printf("%d round of GC: %ldms, collected %d bytes\n",account,tim,free_size);
}

