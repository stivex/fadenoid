# FADEnoid v1.0

Video game inspired with Arkanoid, developed by Xavier Sarsanedas on june / july, 2014

----------

## TECHNICAL REQUIREMENTS

FADEnoid has been developed in Java, this means that you can run this project in multi-OS:  Windows, GNU/Linux and Mac.

The project is ready for work with Eclipse IDE.

## Start the game

Execute FADEnoid.jar with the command:
```sh
 java -jar FADEnoid.jar
```
Also, you can run the demo with the command:

```sh
 java -jar FADEnoid_demo.jar
```

**IMPORTANT:** don't move or delete files where there are, because the game won't run properly.


## How to play?

- The objective is destroying all blocks of every level using the spacecraft. You can use the mouse for move the spacecraft on the left or on the right.
- Every block, when is destroyed the player earns points.
- Some blocks have a prize when are destroyed, that we can collect with the spacecraft. You can distinguish each prize for color. Every prize has different meaning: 
	- Red: extra life
	- Blue: the spacecraft grows
	- Green: the ball speed increase
	- Orange: extra ball
	
## Create you custom levels

- The game has five levels which you can customize
- Into the "./pantalles" folder there are five files TXT, each one contains a design for one level.
- The number of the TXT file means the level of the game.
- Each file has an a bidimensional array of numbers (20 x 20), where each row of numbers is a row of block in the screen. The  codification blocks is:
	- 0 - Without block
	- 1 - White (1 point)
	- 2 - Pink (2 points)
	- 3 - Yellow (3 points)
	- 4 - Blue (4 points)
	- 5 - Cyan (5 points)
	- 6 - Orange (6 points)
	- 7 - Green (7 points)
	- 8 - Red (8 points)
	- 9 - Gray (10 points) (You need two hits for destroy it)

FADE 2014 (http://www.fadefestival.com)



