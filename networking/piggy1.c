#ifndef unix
#define WIN32
#include <windows.h>
#include <winsock.h>
#else
#define closesocket close
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#endif
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <getopt.h>
#define PROTOPORT 36734 /* default protocol port number */
#define QLEN 6 /* size of request queue */
extern int errno;

void head(char *rraddr, int rrport){
  struct hostent *ptrh; /* pointer to a host table entry */
  struct protoent *ptrp; /* pointer to a protocol table entry */
  struct sockaddr_in sad; /* structure to hold an IP address */
  int sd, sd2; /* socket descriptors */
  int port; /* protocol port number */
  char *host; /* pointer to host name */
  char buf[1000]; /* buffer for data from the server */

#ifdef WIN32
  WSADATA wsaData;
  WSAStartup(0x0101, &wsaData);
#endif
  memset((char *)&sad,0,sizeof(sad)); /* clear sockaddr structure */
  sad.sin_family = AF_INET; /* set family to Internet */
  
  /* Extract protocol port */
  port = rrport;
  sad.sin_port = htons((u_short)port);

  /* Convert host name to equivalent IP address and copy to sad. */
  host = rraddr;
  ptrh = gethostbyname(host);
  if ( ((char *)ptrh) == NULL ) {
   fprintf(stderr,"invalid host: %s\n", host);
   exit(EXIT_FAILURE);
  }
  memcpy(&sad.sin_addr, ptrh->h_addr, ptrh->h_length);

  /* Map TCP transport protocol name to protocol number. */
  if ( ((long int)(ptrp = getprotobyname("tcp"))) == 0) {
    fprintf(stderr, "cannot map \"tcp\" to protocol number");
    exit(EXIT_FAILURE);
  }

  /* Create a socket. */
  sd = socket(PF_INET,SOCK_STREAM,ptrp->p_proto);
  if (sd<0) {
    perror("socket");
    exit(1);
  }

  /*Eliminate "Address already in use"error message.*/
  int flag =1;
  if (setsockopt(sd,SOL_SOCKET,SO_REUSEADDR,&flag,sizeof(int))== -1){ 
    perror("setsockopt");
    exit(1);
  } 

  /* Connect the socket to the specified server. */
  if (connect(sd, (struct sockaddr *)&sad, sizeof(sad)) < 0) {
    fprintf(stderr,"connect failed\n");
    exit(EXIT_FAILURE);
  }

  /* Send messages */
  while(1){
    fgets(buf, 1000, stdin);
    send(sd, buf, strlen(buf),0);
  }
  closesocket(sd);
}

void tail(int llport){
  struct hostent *ptrh; /* pointer to a host table entry */
  struct protoent *ptrp; /* pointer to a protocol table entry */
  struct sockaddr_in sad; /* structure to hold server's address */
  struct sockaddr_in cad; /* structure to hold client's address */
  int sd, sd2; /* socket descriptors */
  int port; /* protocol port number */
  int alen; /* length of address */
  int n; /* number of characters read */
  char buf[1000]; /* buffer for string the server sends */

#ifdef WIN32
  WSADATA wsaData;
  WSAStartup(0x0101, &wsaData);
#endif
  memset((char *)&sad,0,sizeof(sad)); /* clear sockaddr structure */
  sad.sin_family = AF_INET; /* set family to Internet */
  sad.sin_addr.s_addr = INADDR_ANY; /* set the local IP address */

  /* Extract protocol port */
  port = llport;
  sad.sin_port = htons((u_short)port);

  /* Map TCP transport protocol name to protocol number */
  if ( ((long int)(ptrp = getprotobyname("tcp"))) == 0) {
    fprintf(stderr, "cannot map \"tcp\" to protocol number");
    exit(EXIT_FAILURE);
  }
  
  /* Create a socket */
  sd = socket(PF_INET,SOCK_STREAM,ptrp->p_proto);
  if (sd<0) {
    perror("socket");
    exit(1);
  }

  /*Eliminate "Address already in use"error message.*/
  int flag =1;
  if (setsockopt(sd,SOL_SOCKET,SO_REUSEADDR,&flag,sizeof(int))== -1){ 
    perror("setsockopt");
    exit(1);
  } 

  /*Bind a local address to the socket*/
  if (bind(sd,(struct sockaddr *)&sad, sizeof(sad)) <0){ 
    perror("bind");
    exit(1);
  }

  /* Specify size of request queue */
  if (listen(sd, QLEN) < 0){ 
    perror ("listen");
    exit(1);
  } 

  /* Main server loop - accept and handle requests */
  while (1) {
    alen = sizeof(cad);
    if ( (sd2=accept(sd, (struct sockaddr *)&cad, &alen)) < 0) {
      fprintf(stderr, "accept failed\n");
      exit(EXIT_FAILURE);
    }

    n = recv(sd2, buf, sizeof(buf), 0);
    while (n > 0) {
      write(1,buf,n);
      n = recv(sd2, buf, sizeof(buf), 0);
    }
    closesocket(sd);
    closesocket(sd2);
  }
}

void middle(int llport, int rrport, char *rraddr){
  struct hostent *ptrh; /* pointer to a host table entry */
  struct protoent *ptrp; /* pointer to a protocol table entry */
  struct sockaddr_in sad; /* structure to hold server's address */
  struct sockaddr_in cad; /* structure to hold client's address */
  int sd, sd2, sd3; /* socket descriptors */
  int l_port, r_port; /* protocol port number */
  int alen; /* length of address */
  int n; /* number of characters read */
  char *host;
  char buf[1000]; /* buffer for string the server sends */
  int flag;

  /* Right Side Connection */
#ifdef WIN32
  WSADATA wsaData;
  WSAStartup(0x0101, &wsaData);
#endif
  memset((char *)&sad,0,sizeof(sad)); /* clear sockaddr structure */
  sad.sin_family = AF_INET; /* set family to Internet */
  
  /* Extract protocol port */
  r_port = rrport;
  sad.sin_port = htons((u_short)r_port);

  /* Convert host name to equivalent IP address and copy to sad. */
  host = rraddr;
  ptrh = gethostbyname(host);
  if ( ((char *)ptrh) == NULL ) {
   fprintf(stderr,"invalid host: %s\n", host);
   exit(EXIT_FAILURE);
  }
  memcpy(&sad.sin_addr, ptrh->h_addr, ptrh->h_length);

  /* Map TCP transport protocol name to protocol number. */
  if ( ((long int)(ptrp = getprotobyname("tcp"))) == 0) {
    fprintf(stderr, "cannot map \"tcp\" to protocol number");
    exit(EXIT_FAILURE);
  }

  /* Create a socket. */
  sd = socket(PF_INET,SOCK_STREAM,ptrp->p_proto);
  if (sd<0) {
    perror("socket");
    exit(1);
  }

  /*Eliminate "Address already in use"error message.*/
  flag =1;
  if (setsockopt(sd,SOL_SOCKET,SO_REUSEADDR,&flag,sizeof(int))== -1){ 
    perror("setsockopt");
    exit(1);
  } 

  /* Connect the socket to the specified server. */
  if (connect(sd, (struct sockaddr *)&sad, sizeof(sad)) < 0) {
    fprintf(stderr,"connect failed\n");
    exit(EXIT_FAILURE);
  }

  /* Left Side Connection */
#ifdef WIN32
  WSADATA wsaData;
  WSAStartup(0x0101, &wsaData);
#endif
  memset((char *)&sad,0,sizeof(sad)); /* clear sockaddr structure */
  sad.sin_family = AF_INET; /* set family to Internet */
  sad.sin_addr.s_addr = INADDR_ANY; /* set the local IP address */

  /* Extract protocol port */
  l_port = llport;
  sad.sin_port = htons((u_short)l_port);

  /* Map TCP transport protocol name to protocol number */
  if ( ((long int)(ptrp = getprotobyname("tcp"))) == 0) {
    fprintf(stderr, "cannot map \"tcp\" to protocol number");
    exit(EXIT_FAILURE);
  }
  
  /* Create a socket */
  sd2 = socket(PF_INET,SOCK_STREAM,ptrp->p_proto);
  if (sd2<0) {
    perror("socket");
    exit(1);
  }

  /*Eliminate "Address already in use"error message.*/
  flag =1;
  if (setsockopt(sd2,SOL_SOCKET,SO_REUSEADDR,&flag,sizeof(int))== -1){ 
    perror("setsockopt");
    exit(1);
  } 

  /*Bind a local address to the socket*/
  if (bind(sd2,(struct sockaddr *)&sad, sizeof(sad)) <0){ 
    perror("bind");
    exit(1);
  }

  /* Specify size of request queue */
  if (listen(sd2, QLEN) < 0){ 
    perror ("listen");
    exit(1);
  } 

  while(1){
    alen = sizeof(cad);
    if ( (sd3=accept(sd2, (struct sockaddr *)&cad, &alen)) < 0) {
      fprintf(stderr, "accept failed\n");
      exit(EXIT_FAILURE);
    }

    while(1){
      n = recv(sd3, buf, sizeof(buf), 0);
      while (n > 0) {
        write(1,buf,n);
        send(sd, buf, strlen(buf), 0);
        memset(buf, 0, 1000);
        n = recv(sd3, buf, sizeof(buf), 0);
      }
      closesocket(sd);
      closesocket(sd2);
      closesocket(sd3);
    }
  }
}

void main(int argc, char *argv[]) {
  int opt = 0;
  int nl_flag = 0, nr_flag = 0, ra_flag = 0, rp_flag = 0, lp_flag = 0;
  char *rraddr;
  int rrport;
  int llport;

  struct option long_options[] = {
    {"noleft",  no_argument,        0,  'a'},
    {"noright", no_argument,        0,  'b'},
    {"rraddr",  required_argument,  0,  'c'},
    {"rrport",  required_argument,  0,  'd'},
    {"llport",  required_argument,  0,  'e'},
    {0, 0, 0, 0}
  };

  int long_index = 0;
  while ((opt = getopt_long_only(argc, argv,"abc:d:e:", long_options, &long_index )) != -1) {
    switch (opt) {
      case 'a': nl_flag = 1; 
      break;
      case 'b': nr_flag = 1; 
      break;
      case 'c': ra_flag = 1; 
      rraddr = optarg;
      break;
      case 'd': rp_flag = 1;
      rrport = atoi(optarg);
      break;
      case 'e': lp_flag = 1; 
      llport = atoi(optarg);
      break;
      case '?':
      fprintf(stderr, "%s: option `-%c' is invalid: ignored\n", argv[0], optopt);
      break;
    }
  }
  
  // Check for -noleft and -noright
  if (nl_flag == 1 && nr_flag == 1){
    fprintf(stderr, "Piggy needs at least one side \n");
    exit(0);
  } 
  if (ra_flag == 0 && nr_flag == 0){
    fprintf(stderr, "Please enter a Right Remote Address \n");
    exit(0);
  }

  // If the ports were not given, then set to default
  if (lp_flag == 0){
    llport = PROTOPORT;
  }
  if (rp_flag == 0){
    rrport = PROTOPORT;
  }

  // Check that the port is between 0 and 65535
  if (rrport < 0 || rrport > 65535){
    fprintf(stderr, "Right Remote Port needs to be between 1 and 65535.\n");
    exit(0);
  }
  if (llport < 0 || llport > 65535){
    fprintf(stderr, "Left Local Port needs to be between 1 and 65535.\n");
    exit(0);
  }

  printf("Left Port: %d | Right Port: %d\n", llport, rrport);

  if(nl_flag == 1){
    head(rraddr, rrport);
  } else if(nr_flag == 1){
    tail(llport);
  } else if(nr_flag == 0 && nl_flag == 0){
    middle(llport, rrport, rraddr);
  } 
}

