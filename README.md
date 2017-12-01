# Ladder
A basic Android Game written on Java.

In this we are going to handle the touch event in the screen and check if the ball landed on the stairs. Game is also fully dynamic. It is not depend on the size, resolution or ratio of screen. 

In this game we have a ball, by using touch event listener, we jump the ball left or right side. If we touch the left half side of screen then ball jump to left side, or if we touch right half then ball jump to right side. 

All the objects are in this game are PNG images, which are placed and fitted on the canvas using rectangles. We used only four picture with transparent background, which are ballgreen.png, brick.png, left.png and right.png. 

We also included the time limit which is fixed in starting to 20. By jumping to right stairs, we get +1 score and get bonus time added to time limit. Bonus time is depending on your score. If you move to wrong side (not landed on stairs) or time left is equal to zero game will be over. You need to tap 2 times to start new game.  

We are generating max of 1000 stairs (currently, can be increased). So you can get highest score of 999, which is very hard to get. All the stairs are placed randomly by dividing screen in three vertical parts: left, right and middle. So you always get a random pattern on each time. 
<br>
<img height="64" src="https://github.com/preetamdaila/Ladder/blob/master/1.png" />
<br>
<img height="640" src="https://github.com/preetamdaila/Ladder/blob/master/2.png" />
<br>
<img height="640" src="https://github.com/preetamdaila/Ladder/blob/master/3.png" />
