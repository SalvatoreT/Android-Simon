package changethispackage.beforesubmitting.tothemarket.android_simon;

import processing.core.*; 

import android.view.MotionEvent; 
import android.view.KeyEvent; 
import android.graphics.Bitmap; 
import java.io.*; 
import java.util.*; 

public class Android_Simon extends PApplet {

/**
 * Android Simon
 * by Salvatore Testa
 * 
 * Repeat the color pattern that is displayed
 *
 */

// Number of squares
int numSquares;
// Square widths
private float squareWidth;
// Square heights
private float squareHeight;
// Shapes per row and per column
int row, col;
// Squares in the game
ArrayList<SimonSquare> mySquares;
// Squares in order
ArrayList<SimonSquare> order;
// Temporary holder for this round
ArrayList<SimonSquare> thisRound;
// Game State
// 0 - intro, 1 - cycle, 2 - playing, 3 - over
private int state;
// Font for game over
private PFont gameOverFont;
private float gameOverSize;
// Square Font
public static PFont squareFont;
public static float fontSize;

public void setup() {
  // Set the screen size
  
  //  size(500/2, 500); 

  // Make the rows and columns split
  row = 4;
  col = 3;

  state = 0;

  // Calculate the width and height
  squareWidth = width/(col*1.0f);
  squareHeight = height/(row*1.0f);

  // Make the container for the squares
  mySquares = new ArrayList<SimonSquare>();
  // Set the order
  order = new ArrayList<SimonSquare>();
  thisRound = new ArrayList<SimonSquare>();

  // Game over font and size
  gameOverSize = min(width/3, height/2);
  gameOverFont = createFont("Serif", gameOverSize);

  // Square font and size
  fontSize = min(squareWidth, squareHeight);
  hint(ENABLE_NATIVE_FONTS);
  squareFont = createFont("Serif", fontSize);



  float colorNumber;
  // Cycle through the i rows
  for (int i = 0; i < col; i++) {
    // Cycle through the j columns
    for (int j = 0; j < row; j++) {
      // Set the color
      colorNumber = (i*col+j)*30;
      // Draw the rectangle
      mySquares.add(new SimonSquare(i*squareWidth, j*squareHeight, squareWidth, squareHeight, colorNumber));
    }
  }
}

public void draw() {
  update();
  background(0); // black
  for (SimonSquare squares:mySquares) {
    squares.draw();
  }

//  fill(100);
//  textFont(gameOverFont);
//  text(state, 5, height/2);

  if (state == 3) {
    fill(100);
    textFont(gameOverFont);
    textAlign(CENTER,CENTER);
    text("Game", width/2, height/2-gameOverSize*.75f/2);
    text("Over", width/2, height/2+gameOverSize*.75f/2);
  }
}



float oldTime = millis();
float newTime;
float elapsed;
boolean fingerPressedPrevious = false;
public void update() {
  newTime = millis();
  elapsed = (newTime - oldTime)/1000.0f;
  intro(elapsed);
  cycle(elapsed);
  if (mousePressed && state == 2) {
    hold();
  }else if(!mousePressed && fingerPressedPrevious){
    fingerUp();
  }
  for (SimonSquare squares:mySquares) {
    squares.update(elapsed);
  }
  if (thisRound.size() == 0 && state == 2) {
    addToList();
    newRound();
  }
  oldTime = newTime;
  fingerPressedPrevious = mousePressed;
}


// If the button is held down
public void hold() {
  for (SimonSquare squares:mySquares) {
    if (state == 2) { 
      squares.mousePressed();
    }
  }
}

public void addToList() {
  // Add a random square
  order.add(mySquares.get(floor(random(mySquares.size()))));
}

// Cycle through the squares to let the player 
// know which squares he/she has to hit
Float nowCycle = 0.0f;
float speed = 1;
// Display the squares
public void cycle(float elapesd) {
  nowCycle += elapsed*speed;
  if (nowCycle >= 0 && state == 1) {
    // Make sure it's within the bounds
    if (nowCycle > order.size()) {
      state = 2;
      return;
    }
    // Light up the square
    int boxNum = floor((nowCycle) % order.size());
    order.get(boxNum).light(boxNum+1);
  }
  else {
    nowCycle = 0.0f;
  }
}

public void fingerUp() {
  if (state == 2 && thisRound.size() > 0) {
    if (!thisRound.remove(0).check(mouseX, mouseY)) {
      state = 3;
      order.clear();
      addToList();
    }
  }
  else if (state == 3) {
    state = 0;
  }
}

public void newRound() {
  thisRound = new ArrayList<SimonSquare>(order);
  state = 1;
}

float nowIntro = -2.0f;
float introSpeed = 8.0f;
public void intro(float elapsed) {
  nowIntro += elapsed*introSpeed;
  if (state == 0) {
    // Make sure it's within the bounds
    if (nowIntro > mySquares.size()) {
      // begin playing after the intro
      newRound();
      return;
    }
    else {
      // Light up the square
      int boxNum = floor((nowIntro) % mySquares.size());
      mySquares.get(boxNum).light();
    }
  }
  else {
    nowIntro = 0.0f;
  }
}
//
//public boolean surfaceTouchEvent(MotionEvent event) {
//  // your code here
//
//  // if you want the variables for motionX/motionY, mouseX/mouseY etc.
//  // to work properly, you'll need to call super.surfaceTouchEvent().
//  return super.surfaceTouchEvent(event);
//}
//
//public boolean surfaceKeyUp(int code, KeyEvent event) {
//  mouseReleased();
//  return super.surfaceKeyDown(code, event);
//}


// Interactive Simon Square
class SimonSquare{

  private float x,y,squareWidth,squareHeight;
  private float colorNumber;
  private float mySaturation = 100;
  private float myBrightness = 100;
  private float myAlpha = 100;
  
  private String displayNumber = null;
  private boolean light = false;
  private float fontSize;
  PFont font;
  
  // @param x   x-coord of the top left of the square
  // @param y   y-coord of the top left of the sauare
  // @squareWidth    the width of the square
  // @squareHight    the height of the square
  // @colorNumber    the hue of the square on a 0-100 scale
  public SimonSquare(float x, float y, float squareWidth, 
      float squareHeight, float colorNumber){
    this.x = x;
    this.y = y;
    this.squareWidth = squareWidth;
    this.squareHeight = squareHeight;
    this.colorNumber = colorNumber;
    
    fontSize = min(squareWidth,squareHeight);
    font = Android_Simon.squareFont;
    fontSize = Android_Simon.fontSize;
  }
  
  public void draw(){
    // Make the color mode Hue/Saturation/Brightness
    colorMode(HSB, 100);
    // Fill the rectangle
    fill(colorNumber%100,mySaturation,myBrightness,myAlpha);
    // Draw the rectangle
    rect(x,y,squareWidth,squareHeight);
    // Draw the number if its there
    if(displayNumber != null){
      fill((50+colorNumber)%100,100,100);
      textAlign(CENTER,CENTER);
      textFont(font);
      text(displayNumber,x+squareWidth/2,y+squareHeight/2);
      displayNumber = null;
    }
  }
  
  public void update(float elapsed){
    if(light){
      myAlpha = 100;
    }else{
      myAlpha = 25;
    }
    light = false;
  }
  
  public void mousePressed(){
    if(check(mouseX,mouseY)){
      light = true;
    }
  }
  
  public void light(){
    light = true;
  }
  
  public void light(Integer num){
    displayNumber = num.toString();
    light = true;
  }
  
  public boolean check(float checkX, float checkY){
    if(checkX > x && checkX < x+squareWidth && checkY > y && checkY < y + squareHeight){
      return true;
    }else{
      return false;
    }
  }
  
}

  public int sketchWidth() { return screenWidth; }
  public int sketchHeight() { return screenHeight; }
}
