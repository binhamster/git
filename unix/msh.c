/* CS 352 -- Mini Shell!  
*
*   Binh Pham
*   September 28, 2016
*   Assignment 2
*
*/

#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/wait.h>
#include "proto.h"


/* Constants */ 

#define LINELEN 1024

/* Prototypes */

void processline (char *line);

/* Shell main */

int
main (void)
{
  char   buffer [LINELEN];
  int    len;

  while (1) {

    /* prompt and get line */
    fprintf (stderr, "%% ");
    if (fgets (buffer, LINELEN, stdin) != buffer)
      break;

    /* Get rid of \n at end of buffer. */
    len = strlen(buffer);
    if (buffer[len-1] == '\n')
      buffer[len-1] = 0;

    /* Run it ... */
    processline (buffer);

  }

  if (!feof(stdin))
    perror ("read");

  return 0;		/* Also known as exit (0); */
}


void processline (char *line)
{
  pid_t  cpid;
  int    status;
  int    argcp;
  char** argv = arg_parse(line, &argcp);

  /* Check if there was an error in proccessing */
  if (argv == 0)
    return;

  /* Start a new process to do the job. */
  cpid = fork();
  if (cpid < 0) {
    perror ("fork");
    return;
  }

  /* Check for who we are! */
  if (cpid == 0) {
    /* We are the child! */
    execvp(argv[0], argv);
    free(argv);
    perror ("exec");
    exit (127);
  }

  /* Have the parent wait for child to complete */
  if (wait (&status) < 0)
    perror ("wait");
  free(argv);
}








