README.txt for GomokuPlayer

CS455 Lab #:2
Name: Patrick Matts, Levi Sinclair, Austen Herrick
Date: 2/15/17

This program was developed primarily on a Windows 7 machine and tested on Windows 10 machines

Steps to run:
1: first compile the main program file by entering command: javac GomokuPlayer.java(in a terminal or command prompt)
2: Now run the Racket server by opening GomokuServer.rkt and hitting the run button
3: If you want the GomokuPlayer to be first simply enter: java GomokuClient
4: Then enter the same command if you want the program to play against itself or run another player
5: If you want GomokuPlayer to be p2 then just run GomokuClient second
6: program will send a message telling the user if it won, lost, or ended with a draw

Things we tried:
simpleHeuristic
This was our first heuristic and was used primarily to test the minimax function and originally it only
looked for open 3s and 4s

boardHeuristic
first real heuristic we tried which was later replaced with lineHeuristic this tallied points for the board
using a series of arrays

lineHeuristic
heuristic we ended up using, this heuristic counts unblocked lines and assigns exponentially higher values based
on how many pieces of one color they contain.

moveLSort
this sorted moves on the basis of heuristic value of the point on the board itself. It was unable to make
the program faster so eventually it was scrapped

moveHSort
current move sorting method we use, only speeds up program when applied to first iteration of minimax

DeepHashCode
this standard package function was used to try and utilize the hashmap, ended up not working

hashMap encoding
Made a transpositional table using a hashmap to store and retrieve previously calculated values

