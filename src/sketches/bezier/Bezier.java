package sketches.bezier;

import java.awt.Color;

import fr.wonder.pspc.Sketch;

public class Bezier extends Sketch {

	final float step = 1/120f;
	
	// runtime
	Vec2[] points;
	boolean displayLines = true;
	
	public static void main(String[] args) {
		new Bezier().start();
	}
	
	@Override
	public void setup() {
		setWinSize(800, 500);
		
		genPoints(50);
	}
	
	@Override
	public void keyPressed(char key) {
		print(key);
		if(key == ' ')
			genPoints(points.length);
		if(key == 'h')
			displayLines = !displayLines;
		if('1' <= key && key <= '9')
			genPoints(key - '0');
	}
	
	@Override
	public void mouseClicked(int button) {
		print(button);
	}
	
	void genPoints(int count) {
		points = new Vec2[count];
		for(int i = 0; i < count; i++) {
			points[i] = new Vec2(random()*winWidth, random()*winHeight);
		}
	}
	
	@Override
	public void draw() {
		clear();
		
		float delta = fract(frame*step);
		
		strokeWidth(1f);
		curve(points, delta, displayLines);
		
		strokeWidth(2f);
		stroke(Color.WHITE);
		Vec2 prev = curve(points, 0, false);
		for(float i = step; i < delta; i += step) {
			Vec2 next = curve(points, i, false);
			line(prev.x, prev.y, next.x, next.y);
			prev = next;
		}
		
		noStroke();
		fill(Color.WHITE);
		for(Vec2 v : points) {
			circle(v.x, v.y, 5f);
		}
	}
	
	Vec2 curve(Vec2[] positions, float delta, boolean draw) {
		if(draw) {
			stroke(colorInScheme(positions.length));
			fill(colorInScheme(positions.length));
		}
		Vec2[] subPositions = new Vec2[positions.length-1];
		for(int i = 0; i < positions.length-1; i++) {
			Vec2 u = positions[i];
			Vec2 v = positions[i+1];
			Vec2 w = v.minus(u).times(delta).plus(u);
			subPositions[i] = w;
			if(draw) {
				line(u.x, u.y, v.x, v.y);
				circle(w.x, w.y, 3f);
			}
		}
		if(positions.length == 1)
			return positions[0];
		return curve(subPositions, delta, draw);
	}
	
}