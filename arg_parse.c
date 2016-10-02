#include "proto.h"
#include <stdlib.h>
#include <string.h>
#include <stdio.h>

static int count_args (char *line){
  int count = 0; 
  int li = 0; 

  while (line[li] != 0) {
    while (line[li] == ' ')
      li++;

    if ((line[li] != 0))
      count++;

    while ( (line[li] != 0) && (line[li] != ' ') ){
      // see quotes, so now start skipping until another quote
      if (line[li] == '"'){
        li++; // move to next char after first quote
        while ( (line[li] != '"') && (line[li] != 0) ) 
          li++;

        // if another quote is not seen until EOL then uneven number of quotes
        if (line[li] == 0)
          fprintf(stderr, "Uneven number of quotes\n");
        li++; // move to next char after last quote
      } else{
        li++;
      }
    }
  }
  
  //printf("Final argument count is: %d\n", count);

  return count;
}

// void remove_quotes(char *line, int src, int dst){
//   while (line[src] != 0){
//     if (line[dst] == '"'){
//       dst++;
//     } else{
//       line[src] = line[dst];
//       src++;
//       dst++;
//     }
//   }
//   line[dst] = 0;
// }

char** arg_parse (char *line, int *argcp){
  *argcp = count_args(line); // number of arguments
  int li = 0; // line index
  int ai = 0; // args index
  
  char** args = (char **)malloc(sizeof(char *) * (*argcp + 1));
  int src = 0;
  while (line[li] != 0) {
    // Skip spaces
    while (line[li] == ' '){
      li++;
      src++;
    }

    // Set pointer of args
    if (line[li] != 0){
      args[ai] = &line[li];
      ai++;
    }

    // Walk argument
    while ( (line[li] != 0) && (line[li] != ' ') ){
      // see quotes, so now start skipping until another quote
      if (line[li] == '"'){

        while (line[li] != 0){
          if (line[src] == '"'){
            src++;
          }
          //printf("src: %d, li: %d\n", src, li);
          line[li] = line[src];
          li++;
          src++;

        }
        //line[li] = 0;



        // li++; // move to next char after first quote

        // while ( (line[li] != '"') && (line[li] != 0) ){
        //   li++;
        // }
        // // if another quote is not seen until EOL then uneven number of quotes
        if (line[li] == 0)
          fprintf(stderr, "Uneven number of quotes\n");
        // li++; // move to next char after last quote
        
      } else{
        li++;
        src++;
      }
    }

    // zero out after argument
    line[li] = 0;
    li++;
    src++;

    if (line[li] == 0)
      args[ai] = NULL;
    //printf("src: %d, dst: %d\n", src, dst);
    //remove_quotes(line, src, dst);
  }

  // remove quotes
  // int src = 0, dst = 0;
  // while (line[src] != 0){
  //   if (line[dst] == '"'){
  //     dst++;
  //   } else{
  //     line[src] = line[dst];
  //     src++;
  //     dst++;
  //   }
  // }
  //line[dst] = 0;
  // remove_quotes(line, src, dst);
  //printf("%s\n", args[0]);
  //printf("%s\n", args[1]);

  return args; 
}