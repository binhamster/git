#    $Id: Makefile,v 1.7 2016/10/27 00:56:46 phamb Exp $
# CS 352 -- Mini Shell!  
#
#   Binh Pham
#   October 19, 2016
#   Assignment 4
#
CC = gcc
CFLAGS = -g -Wall
FILES = msh.o arg_parse.o builtin.o expansion.o strmode.o

msh: $(FILES)
	$(CC) $(CFLAGS) -o msh $(FILES) 

clean:
	rm -rf msh $(FILES)
