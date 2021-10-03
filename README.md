# Sketch Maker

This project is heavily inspired by Processing (https://processing.org) Processing is a marvelous tool I used to use but the lack of some features (auto-completion and error diagnotics mainly) made me think it would be way easier to just use a full fledged IDE.

This is why this project exists, it is simply a minimal version of the most usefull features of processing embeded into a single class (Sketch) that can be extended to access every convenience method without importing anything else.

## Running & compiling

This project is not meant to be ran by itself, but rather integrated in your projects.
Almost any version of java should work, I tested it with java 8 and 14.

## How to use

```java
class SomeSketch {
	
	// Main function, should always look like this
	public static void main(String[] args) {
		new SomeSketch().start();
	}
	
	// The setup function is called once, at startup
	@Override
	public void setup() {
		setWinSize(800, 800);
		setFramerate(60);        // optional, defaults to 60
		clearColor(Color.black); // optional, defaults to black
	}
	
	// The draw function is called once per frame
	@Override
	public void draw() {
		clear();
		// ... render stuff
	}
	
}
```

Some more functions can be overriden, they will trigger during runtime. The overrideable methods are supposed to be the only non-final methods of the Sketch class, so IDEs should show you the complete list when auto-completing for overrides.
```java
public void keyPressed(char key) {}
public void keyReleased(char key) {}
public void mouseClicked(int button) {}
```

To draw, multiple methods are available. First set the stroke and/or fill colors then call one of these:
```java
void rect(float x, float y, float width, float height);
void ellipse(float x, float y, float width, float height);
void circle(float x, float y, float radius);
void line(float x1, float y1, float x2, float y2);
void arrow(float x1, float y1, float x2, float y2);
void cross(float x, float y, float size);

void textSize(float size);
void text(String text, float x, float y);

/** Returns one of the default scheme color (SCHEME[i%SCHEME.length]) */
Color colorInScheme(int i);
```

A lot of math functions can be used without importation, they mostly use `float` and `int` instead of `double` and `long`, a Vec2 (vector xy) class is available and graphs can be created using the `Graph` inner class.

When searching for a specific functionality do not esitate to inspect the code or modify the `Sketch` class.

## Example

You can read the Bezier sketch for a good example, when run it will look something like this:
![could not load image](https://github.com/Akahara/SketchMaker/blob/master/screens/bezier.jpeg?raw=true)


### About me:
You can learn more about me at [acalais.fr](acalais.fr)
