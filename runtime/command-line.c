#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "control.h"
#include "command-line.h"

static void errorNoName (char *);
static void errorWrongArg (char *, char *, char *);

typedef enum{
  ARGTYPE_BOOL,
  ARGTYPE_EMPTY, // expects no argument
  ARGTYPE_INT,
  ARGTYPE_STRING,
} ArgType_t;

//////////////////////////////////////////////////////
/*        all functions */


static void Arg_setHeapSize(int heapSize)
{
  Control_heapSize = heapSize;
  return;
}

/* Typically, a commandline argument take the form of:
 *   -name arg        desc

 * for instance:
 *   -o filename      set the output file name

 * the following data structure defines this.
 */
struct Arg_t
{
  char *name;     // argument name
  char *arg;      // argument (for displaying)
  char *desc;     // argument description
  ArgType_t argtype; // what type of argument expects
  void (*action)();  // a call-back
};

/* all available arguments */
static struct Arg_t allArgs[] = {
  {"heapSize", 
   "<n>", 
   "set the Java heap size (in kilobytes)",
   ARGTYPE_INT,
   Arg_setHeapSize},
  {0,
   0,
   0,
   ARGTYPE_EMPTY,
   0}
};


#define LEFT_SIZE 28
#define INDENT_SIZE 3

static void printSpaces (int n)
{
  while (n--)
    printf (" ");
  return;
}

static void Arg_print ()
{
  int left, i;

  for (i = 0; allArgs[i].action; ++i){
    left = INDENT_SIZE + 1+ strlen (allArgs[i].name)
      + 1 + strlen (allArgs[i].arg)+1;
    printSpaces (INDENT_SIZE);
    printf ("-%s", allArgs[i].name);
    printf (" ");
    printf ("%s", allArgs[i].arg);
    if (left>LEFT_SIZE) {
      printf ("\n");
      printSpaces (LEFT_SIZE);
    }
    else printSpaces (LEFT_SIZE-left);
    printf (" %s\n", allArgs[i].desc);
  }
  return;
}

static void errorNoName (char *s)
{
  printf ("unknown switch: %s\n", s);
  Arg_print ();
  exit (0);
}

static void errorNoArg (char *name, 
                        char *arg)
{
  printf ("no argument is given to switch: %s\n"
          "expects arg: %s\n", name, arg);
  Arg_print ();
  exit (0);
}

static void errorWrongArg (char *name,
                           char *arg,
                           char *input)
{
  printf ("invalid arg for switch: %s\n"
          "expects: %s\n"
          "but got: %s\n", name, arg, input);
  Arg_print ();
  exit (0);
}

void CommandLine_doarg (int argc, char **argv)
{
  int index = 0;
  
  // scan all input command-line arguments
  while (index<argc){
    if (strcmp(argv[index++], "@tiger")==0)
      break;
  }
  for (index=0; index<argc; ){
    char *inputName = argv[index++];
    // If a string starts with '@', then
    // treat it as a terminator.
    if ('@' != inputName[0]){
      break;
    }
    
    // this is a potential argument
    int i = 0;
    for (; allArgs[i].action; i++){
      if (strcmp(inputName+1, allArgs[i].name)!=0){
        continue;
      }
      
      switch (allArgs[i].argtype){
      case ARGTYPE_BOOL:{
        int b;
        char *arg;
        
        if (index>=argc)
          errorNoArg (allArgs[i].name,
                      allArgs[i].arg);
        
        arg = argv[index++];
        if (strcmp(arg, "true")==0)
          b = 1;
        else if (strcmp(arg, "false")==0)
          b = 0;
        else errorWrongArg (allArgs[i].name,
                            allArgs[i].arg,
                            arg);
        allArgs[i].action (b);
        break;
      }
      case ARGTYPE_INT:{
        int result;
        char *arg;
        
        if (index>=argc)
          errorNoArg (allArgs[i].name,
                      allArgs[i].arg);
        
        arg = argv[index++];
        result = atoi (arg);
        allArgs[i].action (result);
        break;
      }
      case ARGTYPE_STRING:{
        char *arg;
        
        if (index>=argc)
          errorNoArg (allArgs[i].name,
                      allArgs[i].arg);
        
        arg = argv[index++];         
        allArgs[i].action (arg);
        break;
      }
      case ARGTYPE_EMPTY:{       
        allArgs[i].action ();
        break;
      }         
      default:{
      	printf ("%s\n", "impossible");
        exit(0);
        break;
      }
      }
      break;
    }
    if (!allArgs[i].action)
      errorNoName (inputName);
  }
  return;
}
