/*    $Id: global.h,v 1.2 2016/10/25 00:10:18 phamb Exp $    */
/* CS 352 -- Mini Shell!  
*
*   Binh Pham
*   October 19, 2016
*   Assignment 4
*
*/
#pragma once

#ifndef MAIN

#define EXTERN extern
#define INIT(x)

#else

#define EXTERN
#define INIT(x) = x 

#endif

EXTERN int paraArgc INIT(0);

EXTERN char **paraArgv INIT(NULL);

EXTERN int shiftn INIT(0);

EXTERN int exitGlobal INIT(0);