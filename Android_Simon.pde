/**
 * Android Simon
 * by Salvatore Testa
 * 
 * Repeat the color pattern that is displayed
 *
 * Game state:
 *   0 - intro (opening sequence)
 *   1 - cycle (displaying pattern that needs to be followed)
 *   2 - playing (user is selecting tiles)
 *   3 - over ("Game Over" is displayed)
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
private int state;
// Font for game over
private PFont gameOverFont;
private float gameOverSize;
// Square Font
public static PFont squareFont;
public static float fontSize;

void setup() {
  // Set the screen size
  size(screenWidth, screenHeight); 
  //  size(500/2, 500); 

  // Make the rows and columns split
  row = 4;
  col = 3;

  state = 0;

  // Calculate the width and height
  squareWidth = width/(col*1.0);
  squareHeight = height/(row*1.0);

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

/* Display the squares */
void draw() {
  update();
  background(0); // black
  for (SimonSquare squares:mySquares) {
    squares.draw();
  }

  if (state == 3) {
    fill(100);
    textFont(gameOverFont);
    textAlign(CENTER,CENTER);
    text("Game", width/2, height/2-gameOverSize*.75/2);
    text("Over", width/2, height/2+gameOverSize*.75/2);
  }
}

/* Adjust the state of squares */
float oldTime = millis();
float newTime;
float elapsed;
boolean fingerPressedPrevious = false;
void update() {
  newTime = millis();
  elapsed = (newTime - oldTime)/1000.0;
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
void hold() {
  for (SimonSquare squares:mySquares) {
    if (state == 2) { 
      squares.mousePressed();
    }
  }
}

/* Create a new square in the sequence */
void addToList() {
  // Add a random square
  order.add(mySquares.get(floor(random(mySquares.size()))));
}

// Cycle through the squares to let the player 
// know which squares he/she has to hit
Float nowCycle = 0.0;
float speed = 1;
// Display the squares
void cycle(float elapesd) {
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
    nowCycle = 0.0;
  }
}

void fingerUp() {
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

/* Reset the round */
void newRound() {
  thisRound = new ArrayList<SimonSquare>(order);
  state = 1;
}

/* Light up the tiles */
float nowIntro = -2.0;
float introSpeed = 8.0;
void intro(float elapsed) {
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
    nowIntro = 0.0;
  }
}

