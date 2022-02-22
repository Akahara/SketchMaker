package sketches.ferns;

import fr.wonder.pspc.Sketch;
import static fr.wonder.pspc.SketchMath.*;

import java.awt.Color;

/**
 * See https://en.wikipedia.org/wiki/Barnsley_fern
 */
public class BarnsleyFern extends Sketch {

	public static void main(String[] args) {
		new BarnsleyFern().start();
	}

	private static final Matrix[] fmatrices = {
		new Matrix(    0,     0,     0, .16f),
		new Matrix( .85f,  .04f, -.04f, .85f),
		new Matrix( .20f, -.26f,  .23f, .22f),
		new Matrix(-.15f,  .28f,  .26f, .24f), };
	private static final Vec2[] fvectors = {
		new Vec2(0,    0),
		new Vec2(0, 1.6f),
		new Vec2(0, 1.6f),
		new Vec2(0, .44f), };
	
	@Override
	public void setup() {
		setWinSize(800, 800);
		noStroke();
		setFramerate(30);
	}
	
	@Override
	public void firstDraw() {
		clear();
		clearColor(alpha(Color.black, .1f));
		fill(alpha(Color.green, .4f));
	}
	
	private void dot(float x, float y, int degree) {
		// values taken from the wikipedia page
		x = mix(x, -2.182f, 2.6558f, 0, winWidth);
		y = mix(y, 0, 9.9983f, winHeight, 0);
		// add margins
		x = mix(x, 0, winWidth,  .1f*winWidth,  .9f*winWidth );
		y = mix(y, winHeight, 0, .9f*winHeight, .1f*winHeight);
		
//		fill(colorInScheme(degree));
//		fill(alpha(Color.green, .9f/(1+degree)));
		circle(x, y, .99f);
	}

	@Override
	public void draw() {
		clear();
		fmatrices[1].set(0, 0, .83f+.02f*cos(frame*.1f));
		Vec2 p = new Vec2();
		int pointDegree = 0;
		for(int i = 0; i < 50000; i++) {
			dot(p.x, p.y, pointDegree);
			float r = random();
			int t;
			if     (r < .01f) { t = 0; pointDegree = 0; }
			else if(r < .86f) { t = 1; }
			else if(r < .93f) { t = 2; pointDegree++; }
			else              { t = 3; pointDegree++; }
			p = fmatrices[t].multiply(p).plus(fvectors[t]);
		}
	}
	
}
