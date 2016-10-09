#include "proto.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

/* Define function pointers */
typedef void (*funcptr) (char **, int);

int check_builtin (char **argv, int argc)
{
  funcptr flist[] = {bi_exit, bi_aecho}; /* Function pointer array */
  char *blist[] = {"exit", "aecho"}; /* Built in function string list */

  /* Find correct function */
  for (int i = 0; i < 2; ++i) {
    if(strcmp(argv[0], blist[i]) == 0){
      flist[i](argv, argc);
      return 1;
    }
  }

  return 0;
}

void bi_exit (char **argv, int argc)
{
	if (argc > 1)
    exit(atoi(argv[1])); 
	else
		exit(0);	
}

void bi_aecho (char **argv, int argc)
{
  int i = 1;
  int nflag = 0;
  if (argc == 1){
    dprintf(1, "\n");
    return;
  }

  /* Set -n flag */
  if (strcmp("-n", argv[1]) == 0){
    i++;
    nflag = 1;
  }

  /* Print args */
  while(i != argc){
    dprintf(1, "%s", argv[i]);
    i++;
    if (nflag && (i == argc))
      ;
    else if (i == argc)
      dprintf(1, "\n");
    else
      dprintf(1, " ");
  }
}