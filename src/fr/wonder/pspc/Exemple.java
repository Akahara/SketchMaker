package fr.wonder.pspc;

public class Exemple extends Sketch {

	int frame;
	
	@Override
	public void setup() {
		setWinSize(200, 400);
	}

	@Override
	public void draw() {
		clear();
		frame++;
		rect(frame % winWidth, frame / winWidth * 10, 10, 10);
	}
	
	public static void main(String[] args) {
		new Exemple().start();
	}

}
