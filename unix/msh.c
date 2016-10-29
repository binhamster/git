/*    $Id: msh.c,v 1.13 2016/10/27 00:58:08 phamb Exp $    */
/* CS 352 -- Mini Shell!  
*
*   Binh Pham
*   October 19, 2016
*   Assignment 4
*
*/
#define MAIN
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/wait.h>
#include "proto.h"
#include <sys/stat.h>
#include <fcntl.h>
#include "global.h"


/* Constants */ 

#define LINELEN 1024

/* Prototypes */

void processline (char *line);

/* Shell main */

int
main (int argc, char** argv)
{
  char   buffer [LINELEN];
  int    len;
  FILE   *fp;
  int    fd; 
  
  paraArgc = argc;
  paraArgv = argv;

  if (argc > 1) {
    if ((fd = open(argv[1], O_RDONLY)) == -1) {
      perror("open");
      exit(127);
    } 
    if ((fp = fdopen(open(argv[1], O_RDONLY), "r")) == NULL)
      perror("fdopen");
  } else
    fp = stdin;


  while (1) {

    /* prompt and get line */
    if (argc == 1)
      fprintf (stderr, "%% ");

    if (fgets(buffer, LINELEN, fp) != buffer)
      break;

    /* Get rid of \n at end of buffer. */
    len = strlen(buffer);
    if (buffer[len-1] == '\n')
      buffer[len-1] = 0;

    /* Run it ... */
    processline (buffer);

  }

  if (!feof(fp))
    perror ("read");

  return 0;		/* Also known as exit (0); */
}


void processline (char *line)
{
  pid_t  cpid;
  int    status;
  int    argcp;
  char** argv; 
  char   new[LINELEN];
  int    ret;

  /* Check if there was an error in expand */
  if ((ret = expand(line, new, LINELEN)) == 0) 
    return;

  /* Check if there was an error in proccessing */
  if ((argv = arg_parse(new, &argcp)) == 0)
    return;

  /* Check if the command was a built in */
  if (check_builtin(argv, argcp))
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
    perror ("exec");
    fclose(stdin);
    exit (127);
  }

  /* Have the parent wait for child to complete */
  if (wait (&status) < 0)
    perror ("wait");
  free(argv);

  if(WIFEXITED(status))
    exitGlobal = WEXITSTATUS(status);
}