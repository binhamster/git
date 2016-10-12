#    $Id: Makefile,v 1.2 2016/10/12 03:55:20 phamb Exp $
CC = gcc
CFLAGS = -g -Wall

msh: msh.o arg_parse.o builtin.o expansion.o
	gcc -g -o msh msh.o arg_parse.o builtin.o expansion.o

clean:
	rm -rf msh msh.o arg_parse.o builtin.o expansion.o

# dependency list
msh.o arg_parse.o builtin.o: proto.h

Macros: CC, CFLAGS