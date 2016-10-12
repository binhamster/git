/*    $Id: expansion.c,v 1.1 2016/10/12 03:55:20 phamb Exp $    */
#include "proto.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

int expand (char *orig, char *new, int newsize) 
{
	char name[newsize];
	int ni = 0; 	// name index
	int oi = 0; 	// orig index
	while ((orig[oi] != 0) && (oi != newsize)) {
		if ((orig[oi] == '$') && (orig[oi + 1] == '{')) {
			oi = oi + 2;
			while (orig[oi] != '}'){
				name[ni] = orig[oi];
				oi++;
				ni++;

			}
			oi++;
		} else {
			oi++
		}
	}
	name[ni] = 0;





	return 0;
}