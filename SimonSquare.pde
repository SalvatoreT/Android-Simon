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
  
  void draw(){
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
  
  void update(float elapsed){
    if(light){
      myAlpha = 100;
    }else{
      myAlpha = 25;
    }
    light = false;
  }
  
  void mousePressed(){
    if(check(mouseX,mouseY)){
      light = true;
    }
  }
  
  
  /* Box is brighter */
  void light(){
    light = true;
  }
  
  void light(Integer num){
    displayNumber = num.toString();
    light();
  }
  
  /* Is user's finger inside the box? */
  boolean check(float checkX, float checkY){
    if(checkX > x && checkX < x+squareWidth && checkY > y && checkY < y + squareHeight){
      return true;
    }else{
      return false;
    }
  }
  
}
