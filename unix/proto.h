/*    $Id: proto.h,v 1.3 2016/10/12 03:55:20 phamb Exp $    */
char** arg_parse (char *line, int *argcp);
int check_builtin (char **argv, int argcp);
void bi_exit (char **argv, int argc);
void bi_aecho (char **argv, int argc);
void bi_cd (char **argv, int argc);
int expand (char *orig, char *new, int newsize);
void bi_envset (char **argv, int argc);
void bi_envunset (char **argv, int argc);
