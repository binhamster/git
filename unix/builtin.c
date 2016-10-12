/*    $Id: builtin.c,v 1.3 2016/10/12 03:55:20 phamb Exp $    */
#include "proto.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>

/* Define function pointers */
typedef void (*funcptr) (char **, int);

int check_builtin (char **argv, int argc)
{
  funcptr flist[] = {bi_exit, bi_aecho, bi_cd, bi_envset, bi_envunset}; /* Function pointer array */
  char *blist[] = {"exit", "aecho", "cd", "envset", "envunset"}; /* Built in function string list */

  /* Find correct function */
  for (int i = 0; i < 5; ++i) {
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

void bi_cd (char **argv, int argc)
{
  if (argc > 2)
    dprintf(1, "usage: cd [dir]");
  else if (argc == 2) {
    if (chdir(argv[1]))
      dprintf(1, "cd: %s: No such file or directory\n", argv[1]);
  }
}

void bi_envset (char **argv, int argc)
{
  if ((argc >= 4) || (argc <= 2)) 
    dprintf(1, "usage: envset name value");
  else {
    int ret = setenv(argv[1], argv[2], 1);
    if (ret == -1)
      perror("setenv");
  }
}

void bi_envunset (char **argv, int argc) 
{
  if ((argc >= 3) || (argc <= 1))
    dprintf(1, "usage: envunset name");
  else {
    int ret = unsetenv(argv[1]);
    if (ret == -1)
      perror("unsetenv");
  }
}

