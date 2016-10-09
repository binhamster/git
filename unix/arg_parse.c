#include "proto.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

static int count_args (char *line)
{
  int count = 0; 
  int li = 0; 

  while (line[li] != 0) {
    /* Skip spaces */
    while (line[li] == ' ')
      li++;
    /* Set pointers */
    if (line[li] != 0)
      count++;

    while ((line[li] != 0) && (line[li] != ' ')) {
      /* Start proccessing quotes */
      if (line[li] == '"') {

        li++; /* Skip first quote */
        while ((line[li] != '"') && (line[li] != 0)) 
          li++;

        if (line[li] == 0)
          return -1; /* Odd number of quotes */
        li++; /* Skip last quote */

      } else {
        li++;
      }
    }
  }

  return count;
}

char** arg_parse (char *line, int *argcp)
{
  int li = 0; /* Line index */
  int ai = 0; /* Args index */
  int dst = 0;
  int src = 0;
  *argcp = count_args(line);

  /* Check if there is an unmatched quote */
  if (*argcp == -1) {
    fprintf(stderr, "Unmatched quote\n");
    return 0;
  }

  /* No arguments */
  if (*argcp == 0)
    return 0;

  /* Malloc for space */
  char** args = (char **)malloc(sizeof(char *) * (*argcp + 1));
  if (args == NULL) {
    perror("malloc");
    return 0;
  }

  while (line[li] != 0) {
    /* Skip spaces */
    while (line[li] == ' ')
      li++;

    /* Set pointers and first index */
    if (line[li] != 0) {
      dst = li;
      src = li;
      args[ai] = &line[li];
      ai++;
    }

    /* Walk argument */
    while ((line[li] != 0) && (line[li] != ' ')) {
      /* Start proccessing quotes */
      if (line[li] == '"'){
        
        li++; /* Skip first quote */
        while ((line[li] != '"') && (line[li] != 0))
          li++;
        li++; /* Skip last quote */

      } else {
        li++;
      }

    }

    /* Zero out after argument */
    if (line[li] != 0) {
      line[li] = 0;
      li++;
    }

    /* Remove Quotes */
    while(line[src] != 0) {
      if (line[src] != '"') {
        line[dst] = line[src];
        dst++;
      }
      src++;
    }
    line[dst] = 0;

    if (line[li] == 0)
      args[ai] = NULL;
  }

  /* Check if the command was a built in */
  if (check_builtin(args, *argcp))
    return 0;

  return args; 
}