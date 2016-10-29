/*    $Id: builtin.c,v 1.14 2016/10/28 20:45:29 phamb Exp $    */
/* CS 352 -- Mini Shell!  
*
*   Binh Pham
*   October 19, 2016
*   Assignment 4
*
*/
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <grp.h>
#include <pwd.h>
#include <time.h>
#include "global.h"

/* Define function pointers */
typedef void (*funcptr) (char **, int);


/* Protoypes */
int check_builtin (char **argv, int argc);
void bi_exit (char **argv, int argc);
void bi_aecho (char **argv, int argc);
void bi_cd (char **argv, int argc);
void bi_envset (char **argv, int argc);
void bi_envunset (char **argv, int argc);
void bi_shift(char **argv, int argc);
void bi_unshift(char **argv, int argc);
void bi_sstat(char **argv, int argc);
void strmode(mode_t mode, char *p);

int check_builtin (char **argv, int argc)
{
  funcptr flist[] = {bi_exit, bi_aecho, bi_cd, bi_envset, bi_envunset, bi_shift, bi_unshift, bi_sstat}; /* Function pointer array */
  char *blist[] = {"exit", "aecho", "cd", "envset", "envunset", "shift", "unshift", "sstat"}; /* Built in function string list */

  /* Find correct function */
  for (int i = 0; i < 8; ++i) {
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
  char *home;
  /* Too many arguments */
  if (argc > 2) {
    dprintf(2, "usage: cd [dir]\n");
    exitGlobal = 1;
  /* cd to home directory */
  } else if (argc == 1) {
    if ((home = getenv("HOME")) != NULL)
      chdir(home);
    else { 
      dprintf(2, "Enviroment variable HOME not set\n");
      exitGlobal = 1;
    }
  } else if (argc == 2) {
    if (chdir(argv[1]) == -1) {
      dprintf(2, "cd: %s: No such file or directory\n", argv[1]);
      exitGlobal = 1;
    }
  }
}

void bi_envset (char **argv, int argc)
{
  if ((argc >= 4) || (argc <= 2)) { 
    dprintf(2, "usage: envset name value\n");
    exitGlobal = 1;
  } else {
    int ret = setenv(argv[1], argv[2], 1);
    if (ret == -1)
      perror("setenv");
  }
}

void bi_envunset (char **argv, int argc) 
{
  if ((argc >= 3) || (argc <= 1)) {
    dprintf(2, "usage: envunset name\n");
    exitGlobal = 1;
  } else {
    int ret = unsetenv(argv[1]);
    if (ret == -1)
      perror("unsetenv");
  }
}

void bi_shift(char **argv, int argc) 
{
  if (argc > 2) {
    dprintf(2, "usage: shift [n]\n");
    exitGlobal = 1;
  } else if (argc == 2) { 
    if (atoi(argv[1]) >= (paraArgc - 1 - shiftn)) {
      dprintf(2, "Cannot shift with n = %d\n", atoi(argv[1]));
      exitGlobal = 1;
    } else
      shiftn = atoi(argv[1]);
  } else
    shiftn++;
}

void bi_unshift(char **argv, int argc) 
{
  if (argc > 2) {
    dprintf(2, "usage: unshift [n]\n");
    exitGlobal = 1;
  } else if (argc == 2) {
    if (atoi(argv[1]) > shiftn) {
      dprintf(2, "Cannot unshift with n = %d\n", atoi(argv[1]));
    } else
      shiftn = shiftn - atoi(argv[1]);
  } else
    shiftn = 0;
}

void bi_sstat(char **argv, int argc) 
{
  struct stat sb;
  struct passwd *pwd;
  struct group *grp;
  char buf[100];

  for (int i = 1; i < argc; i++){
    if (stat(argv[i], &sb) == - 1) {
      perror("stat");
      exit(127);
    }

    dprintf(1, "%s ", argv[i]);

    if ((pwd = getpwuid(sb.st_uid)) == 0)
      dprintf(1, "%d ", sb.st_uid);
    else 
      dprintf(1, "%s ", pwd->pw_name);

    if ((grp = getgrgid(sb.st_gid)) == 0)
      dprintf(1, "%d ", sb.st_gid);
    else
      dprintf(1, "%s ", grp->gr_name);

    strmode(sb.st_mode, buf);
    dprintf(1, "%s", buf);

    dprintf(1, "%ld ",sb.st_nlink);
    dprintf(1, "%ld ",sb.st_size);

    dprintf(1, "%s", asctime(localtime(&sb.st_mtime)));

  }
}