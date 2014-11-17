#Design doc

nPuzzle for Android. 

##Overview
![App overview](https://raw.githubusercontent.com/Shiqan/Naive-App-Studio/master/doc/n-Puzzle%20Android.png)

___ 
##Activities

![Main Activity](https://raw.githubusercontent.com/Shiqan/Naive-App-Studio/master/doc/Capture.PNG)
* 3 buttons to start the loadimg activity (setOnClickListener).
* 1 button to start the highscore activity (setOnClickListener).
* Options menu to open this github repository.

![Loadimg Acitivity](https://raw.githubusercontent.com/Shiqan/Naive-App-Studio/master/doc/Capture1.PNG)
* Preview of the image, which starts with puzzle_0.jpg and is slideable to puzzle_1.jpg and puzzle_2.jpg (HorizontalScrollView)
* Button to start camera intent to take a picture which can be used for the puzzle (Intent(MediaStore.ACTION_IMAGE_PICTURE)).
* Button to start gallery intent to select a picture from the phone which can be used for the puzzle (Intent(Action.ACTION_PICK)).
* Start game will launch the game with the selected picture.

![Game Activity](https://raw.githubusercontent.com/Shiqan/Naive-App-Studio/master/doc/Capture2.PNG)
* First three seconds the solution will be shown (correct order) after that the tiles will be randomly assigned to the gridview.
* Gridview for the tiles.
* Number of moves is shown.
* Options menu to preview the solution, restart the game or new puzzle (back to main screen).

![Complete Game](https://raw.githubusercontent.com/Shiqan/Naive-App-Studio/master/doc/Capture4.PNG)
* When game is successfully completed a toast is presented to enter your name, after that, the user is sent to the highscore activity (Toast, EditText).

![Highscore Activity](https://raw.githubusercontent.com/Shiqan/Naive-App-Studio/master/doc/Capture3.PNG)
* Highscores for each of the three modes are shown (SharedPreferences).
* Options menu to share highscores or reset the highscores.

___ 
##Classes

###SplitImage
In this class the chosen image will be converted to a Bitmap and splitted into n pieces (dependent on the difficulty).

___ 
##SharedPreferences
The following data will be stored in the SharedPreferences:

  * Game State
  * # of current moves
  * Highscores for each of the difficulties
