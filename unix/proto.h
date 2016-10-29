/*    $Id: proto.h,v 1.8 2016/10/22 23:19:46 phamb Exp $    */
/* CS 352 -- Mini Shell!  
*
*   Binh Pham
*   October 19, 2016
*   Assignment 4
*
*/
#pragma once
char** arg_parse (char *line, int *argcp);
int check_builtin (char **argv, int argcp);
int expand (char *orig, char *new, int newsize);