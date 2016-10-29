/*    $Id: expansion.c,v 1.32 2016/10/28 20:53:09 phamb Exp $    */
/* CS 352 -- Mini Shell!  
*
*   Binh Pham
*   October 19, 2016
*   Assignment 4
*
*/
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <ctype.h>
#include "global.h"
#include <sys/types.h>
#include <dirent.h>

/* Prototypes */
int expand (char *orig, char *new, int newsize);
void apendStr(char *line, char *str, int *i, int maxsize);
int getn(char *orig, int *oi);
void wildCard(char *new, int *ni, int newsize, int mode, char *str);
int getStr(char *orig, int *oi, char *str);
int endStrExist(char *hay, char* needle);

int expand (char *orig, char *new, int newsize) 
{
	char *value;
	char *name;
	char pid[10];	
	char buf[100];
	int ni = 0;		/* new index */
	int oi = 0; 	/* orig index */
	int n;
	int e;
	char str[100];
	while ((orig[oi] != 0) && (ni < newsize)) {
		if ((orig[oi] == '$') && (orig[oi + 1] == '{')) {
			
			oi = oi + 2; 
			name = &orig[oi]; /* Set starting pointer for name */

			/* Move to ending brace */
			while ((orig[oi] != '}') && (orig[oi] != 0))
				oi++;

			/* If there is no ending brace */
			if (orig[oi] == 0) {
				fprintf(stderr, "msh: no matching }\n");
				return 0;
			}

			orig[oi] = 0;
			oi++;

			/*  enviroment variable value */
			if ((value = getenv(name)) != NULL)
				apendStr(new, value, &ni, newsize);

		} else if ((orig[oi] == '$') && (orig[oi + 1] == '$')) {
			/* Get pid as string and apend to new */
			oi = oi + 2; 
			snprintf(pid, 10, "%d", getpid());
			apendStr(new, pid, &ni, newsize);

		} else if ((orig[oi] == '$') && (orig[oi + 1] == '0')){

			oi = oi + 2;
			if (paraArgc > 1)
				apendStr(new, paraArgv[1], &ni, newsize);
			else
				apendStr(new, paraArgv[0], &ni, newsize);

		} else if ((orig[oi] == '$') && (n = getn(orig, &oi))){
			if (n <= paraArgc - 2 - shiftn)
				apendStr(new, paraArgv[n + 1 + shiftn], &ni, newsize);

		} else if ((orig[oi] == '$') && (orig[oi + 1] == '#')) {

			oi = oi + 2;
			if (paraArgc > 1) 
				snprintf(buf, 10, "%d", paraArgc - 1 - shiftn);
			else	
				snprintf(buf, 10, "%d", 1);	
			apendStr(new, buf, &ni, newsize);

		} else if ((orig[oi] == '$') && (orig[oi + 1] == '?')) {

			oi = oi + 2;
			snprintf(buf, 10, "%d", exitGlobal);
			apendStr(new, buf, &ni, newsize);

		} else if ((orig[oi-1] == ' ') && (orig[oi] == '*') && 
			       ((orig[oi+1] == ' ') || (orig[oi+1] == '"') || (orig[oi+1] == 0))) {
			
			oi++;
			wildCard(new, &ni, newsize, 0, "");

		} else if ((orig[oi-1] == ' ') && (orig[oi] == '*') && (e = getStr(orig, &oi, str))){

			if (e == 2) {
				fprintf(stderr, "bad context character: /\n");
			 	return 0;
			} else
				wildCard(new, &ni, newsize, 1, str);

		} else if ((orig[oi] == '\\') && (orig[oi+1] == '*')) {
			
			oi = oi + 2;
			apendStr(new, "*", &ni, newsize);
		
		} else {
			/* Copy over unchanged characters */ 
			new[ni] = orig[oi];
			ni++;
			oi++;
		}
	}

	if (ni >= newsize) {
		fprintf(stderr, "expansion overflow error: writing pass buffer limit\n");
		return 0;
	}
	new[ni] = 0;

	return 1;
}

void apendStr(char *line, char *str, int *i, int maxsize)
{
	int len = strlen(str);
	for (int n = 0; n < len; n++) {
		/* Make sure it doesn't overflow */
		if (*i < maxsize){
			line[*i] = str[n];
			(*i)++;
		} 
	}
}

int getn(char *orig, int *oi) 
{
	char buf[10];
	int i = 0; 
	if (isdigit(orig[*oi + 1]))
		(*oi)++;
	while (isdigit(orig[*oi])) {
		buf[i] = orig[*oi];
		i++;
		(*oi)++;
	}
	buf[i] = 0;

	return (int) strtol(buf, (char **)NULL, 10);
}

void wildCard(char *new, int *ni, int newsize, int mode, char *str)
{
	char buf[100];
	DIR *dir;
	struct dirent *ent;
	if ((dir = opendir(".")) != NULL) {
		if ((ent = readdir(dir)) != NULL) {
			if (ent->d_name[0] != '.') {
				snprintf(buf, 100, "%s", ent->d_name);
				if (mode == 0)
					apendStr(new, buf, ni, newsize);
				else if (mode && endStrExist(buf, str))
					apendStr(new, buf, ni, newsize);
			}
		}
		while ((ent = readdir(dir)) != NULL) {
			if (ent->d_name[1] != '.') {
				snprintf(buf, 100, " %s", ent->d_name);
				if (mode == 0)
					apendStr(new, buf, ni, newsize);
				else if (mode && endStrExist(buf, str))
					apendStr(new, buf, ni, newsize);
			}
		}
		closedir(dir);
	} else 
		fprintf(stderr, "directory couldn't be open\n");
}

int endStrExist(char *hay, char* needle)
{
	int hlen = strlen(hay) - 1;
	int nlen = strlen(needle) - 1;

	for (int i = nlen; i >= 0; i--) {
		if (needle[i] != hay[hlen])
			return 0;
		hlen--;
	}

	return 1;
}

int getStr(char *orig, int *oi, char *str)
{

	int c = *oi + 1;
	int i = 0;
	while ((orig[c] != 0) && (orig[c] != ' ') && (orig[c] != '"')) {  
		str[i] = orig[c];
		i++; c++;
	}
	str[i] = 0;

	if (strstr(str, "/") != NULL)
		return 2;

	DIR *dir;
	struct dirent *ent;
	if ((dir = opendir(".")) != NULL) {
		while ((ent = readdir(dir)) != NULL) {
			if (endStrExist(ent->d_name, str)) {
				*oi = c;
				return 1;
			}
		}
		closedir(dir);
	}

	return 0;
}