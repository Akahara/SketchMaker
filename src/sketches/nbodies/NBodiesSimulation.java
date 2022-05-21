package sketches.nbodies;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import fr.wonder.pspc.Sketch;

public class NBodiesSimulation extends Sketch {

	public static void main(String[] args) {
		System.out.println("click to add a body");
		new NBodiesSimulation().start();
	}
	
	static final float G = 6.674f;
	static final int FRAMERATE = 60;         // ok, I have no idea of how the delta or the 
	static final float DELTA = 1f/FRAMERATE; // framerate are supposed to impact the simulation
	
	class Body {
		
		Vec2 pos;
		Vec2 movement;
		float mass;
		float radius;
		Color c = nextColorInScheme();
		
		Body(float x, float y, float dx, float dy, float mass) {
			this.pos = new Vec2(x, y);
			this.movement = new Vec2(dx, dy);
			this.mass = mass;
			this.radius = min(mass, 50);
		}
		
	}
	
	List<Body> bodies = new ArrayList<>();
	
	@Override
	public void setup() {
		setWinSize(700, 700);
		setFramerate(FRAMERATE);
		clearColor(new Color(0, 0, 0, .05f));
		stroke(Color.RED);
		
//		for(int i = 0; i < 10; i++) {
//			bodies.add(new Body(
//					new Vec2(random(0, winWidth), random(0, winHeight)),
//					new Vec2(random(-2, 2), random(-2, 2)),
//					random(5, 10)));
//		}
//		bodies.add(new Body(new Vec2(winWidth/2, winHeight/2), new Vec2(), 1000));
		bodies.add(new Body(winWidth/2, winHeight*3/4,  1, 0, 10));
		bodies.add(new Body(winWidth/2, winHeight*1/4, -1, 0, 10));
	}

	@Override
	public void draw() {
		for(int s = 0; s < FRAMERATE; s++) {
			Vec2[] accelerations = new Vec2[bodies.size()];
			
			for(int i = 0; i < bodies.size(); i++) {
				accelerations[i] = new Vec2();
				Body b1 = bodies.get(i);
				for(int j = 0; j < bodies.size(); j++) {
					if(j == i)
						continue;
					Body b2 = bodies.get(j);
					Vec2 d = b2.pos.minus(b1.pos);
					float minDist = b1.radius+b2.radius;
					float r2 = max(d.lengthSquared(), minDist*minDist);
					accelerations[i] = accelerations[i].plus(d.normalized().times(10*G*b2.mass/r2));
				}
			}
			
			for(int i = 0; i < bodies.size(); i++) {
				Body b = bodies.get(i);
				b.movement = b.movement.plus(accelerations[i].times(DELTA));
				b.pos = b.pos.plus(b.movement.times(DELTA));
			}
		}
		
		clear();
		for(Body b : bodies) {
			circle(b.pos.x, b.pos.y, b.radius);
			arrow(b.pos.x, b.pos.y, b.pos.x+b.movement.x, b.pos.y+b.movement.y);
		}
	}

	@Override
	public void mousePressed(int button) {
		if(button == CLICK_LEFT)
			bodies.add(new Body(mouseX, mouseY, 0, 0, 10));
	}
	
}
