package sketches.grapher;

import java.awt.Color;

import fr.wonder.commons.math.ease.Transforms;
import fr.wonder.commons.math.ease.EaseTransforms;
import fr.wonder.commons.math.ease.Transform;
import fr.wonder.pspc.Sketch;

public class Grapher extends Sketch {

	private static final int WINSIZE = 400;
	private static final float PRECISION = .01f;
	
	@Override
	public void setup() {
		setWinSize(WINSIZE, WINSIZE);
		strokeWidth(2);
		stroke(Color.WHITE);
	}

	@Override
	public void draw() {
		clear();
		
		Transform f = EaseTransforms.EASEIN_CIRC
				.andThen(Transforms.clamp(.2f, .8f))
				.andThen(Transforms.mod(.3f));
		
		float y1 = f.apply(0);
		for(float i = PRECISION; i < 1; i += PRECISION) {
			float x1 = i - PRECISION;
			float x2 = i;
			float y2 = f.apply(x2);
			line(x1*WINSIZE, WINSIZE-y1*WINSIZE, x2*WINSIZE, WINSIZE-y2*WINSIZE);
			y1 = y2;
		}
	}
	
	public static void main(String[] args) {
		new Grapher().start();
	}

}
