package sketches.ferns;

import java.awt.Color;
import java.util.Arrays;

import fr.wonder.pspc.Sketch;

public class FractalFerns extends Sketch {

	public static void main(String[] args) {
		System.out.println("Press space to regenerate the tree");
		new FractalFerns().start();
	}
	
	private static final float[] viewport = { -30, 30,  -10, 50};
	private static final float WIND_FORCE = 0; //.01f;
	private Tree[] trees = {
		new Tree(0, 0, 7, new float[] {
				.95f, PI/4.1f,
				.95f, -PI/3.9f,
		}),
	};
	
	int firstFrame = 0;
	
	@Override
	public void setup() {
		setWinSize(800, 800);
		stroke(Color.white);
		strokeWidth(2);
	}
	
	@Override
	public void draw() {
		clear();
		float t = (frame-firstFrame)/framerate*3;
		for(Tree tree : trees) {
			tree.draw(t);
			tree.wind = cos(t/2f)*WIND_FORCE;
		}
	}
	
	private void regenTree() {
		firstFrame = frame;
		float[] branches = new float[randint(2, 4)*2];
		for(int i = 0; i < branches.length; i+=2) {
			branches[ i ] = round(random(.5f, 1f), 3);
			branches[i+1] = round(random(-PI/3f, PI/3f), 3);
		}
		trees = new Tree[] {
				new Tree(0, 0, randint(5, 10), branches),
		};
		System.out.println(Arrays.toString(trees));
	}
	
	@Override
	public void keyPressed(char key) {
		if(key == ' ')
			regenTree();
	}
	
	private class Tree {
		
		private final int maxDegree;
		private final float[] sbranches;
		private final float rootX, rootY, rootTheta = PI/2;
		
		public float wind;
		
		public Tree(float rootX, float rootY, int maxDegree, float[] sbranches) {
			this.rootX = rootX;
			this.rootY = rootY;
			this.maxDegree = maxDegree;
			this.sbranches = sbranches;
		}
		
		public void draw(float lengthFraction) {
			branch(rootX, rootY, rootTheta, lengthFraction, 0);
		}
		
		private void branch(
				float x, float y, float theta,
				float lengthFraction, int degree) {
			
			if(lengthFraction <= 0)
				return;
			
//			float weight = 1f/(1+degree);
			float weight = 1-(float)degree/(2+maxDegree);
			float lineLength = 10f*weight;
			drawBranch(x, y, theta, lineLength*min(1, lengthFraction), weight);
			
			if(degree > maxDegree)
				return;
			
			for(int i = 0; i < sbranches.length; i += 2) {
				float a = sbranches[ i ];
				float t = sbranches[i+1];
				float lenf = lengthFraction - a;
				branch(
						x+cos(theta)*lineLength*a,
						y+sin(theta)*lineLength*a,
						theta+t + wind,
						lenf,
						degree+1);
			}
		}
		
		private void drawBranch(float x, float y, float theta, float length, float weight) {
			float x1 = x;
			float y1 = y;
			float x2 = x+cos(theta)*length;
			float y2 = y+sin(theta)*length;
			x1 = mix(x1, viewport[0], viewport[1], .1f*winWidth, .9f*winWidth);
			x2 = mix(x2, viewport[0], viewport[1], .1f*winWidth, .9f*winWidth);
			y1 = mix(y1, viewport[2], viewport[3], .9f*winHeight, .1f*winHeight);
			y2 = mix(y2, viewport[2], viewport[3], .9f*winHeight, .1f*winHeight);
			strokeWidth(3*weight);
			line(x1, y1, x2, y2);
		}
		
		@Override
		public String toString() {
			return "TREE(d=" + maxDegree + " branches=" + Arrays.toString(sbranches) + ")";
		}
	}
	
}
