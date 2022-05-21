package sketches.verletsim;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import fr.wonder.pspc.Sketch;

public class Verlet extends Sketch {

	private final int STEPS = 8;
	
	private final List<VerletSphere> spheres = new ArrayList<>();
	private final float dt = .03f / STEPS;
	
	public static void main(String[] args) {
		new Verlet().start();
	}

	@Override
	public void setup() {
		setWinSize(700, 700);
		clearColor(Color.white);
		noStroke();
		
		for(int i = 0; i < 40; i++) {
			spheres.add(new VerletSphere(randPos(), random(.01f, .1f), colorInScheme(i)));
		}
	}
	
	private Vec2 randPos() {
		return Vec2.unitary(random()*2*PI).times(random()*.8f);
	}
	
	private Vec2 acceleration(float t) {
		return new Vec2(0, -1);
	}

	@Override
	public void draw() {
		clear();
		fill(Color.black);
		circle(winWidth/2, winHeight/2, winWidth/2);
		
		for(int i = 0; i < STEPS; i++)
			step();

		for(VerletSphere s : spheres) {
			fill(s.color);
			circle( mix(s.pos.x, -1, 1, 0, winWidth),
					mix(s.pos.y, -1, 1, winHeight, 0),
					mix(s.radius, 0, 1, 0, winWidth/2f));
		}
	}
	
	private void step() {
		Vec2 acceleration = acceleration(frame/framerate);
		
		for(int i = 0; i < spheres.size(); i++) {
			var s = spheres.get(i);
			Vec2 p = s.pos.times(2f).minus(s.prevPos).plus(acceleration.times(dt*dt));
			s.prevPos = s.pos;
			
			// push spheres
			for(int j = 0; j < spheres.size(); j++) {
				if(i == j)
					continue;
				var s2 = spheres.get(j);
				Vec2 v = p.minus(s2.pos);
				float d = s.radius + s2.radius;
				if(v.length() < d) {
					p = p.plus(v.times( .5f*dt));
					s2.pos = s2.pos.plus(v.times(-.5f*dt));
				}
			}
			
			// clamp in bounds
			float l = s.pos.length();
			if(l + s.radius > 1)
				p = p.times(1f/(l+s.radius));
			
			s.pos = p;
		}
	}
	
	static class VerletSphere {
		
		Vec2 pos;
		Vec2 prevPos;
		float radius;
		Color color;
		
		VerletSphere(Vec2 pos, float radius, Color color) {
			this.pos = pos;
			this.prevPos = pos;
			this.radius = radius;
			this.color = color;
		}
		
	}
	
}
