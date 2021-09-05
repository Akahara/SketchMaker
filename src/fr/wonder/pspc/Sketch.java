package fr.wonder.pspc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

public abstract class Sketch {
	
	public int winWidth, winHeight;
	public int frame;
	public int mouseX, mouseY;
	
	private long frameDelta = 1000/60;
	
	private JFrame jframe;
	private JPanel canvas;
	
	private Graphics graphics;
	
	private Color 	fillColor = Color.white,
					strokeColor = Color.black,
					clearColor = Color.black;
	
	/** Entry point of sketches */
	public void start() {
		System.setProperty("sun.awt.noerasebackground", "true");
		Toolkit.getDefaultToolkit().setDynamicLayout(false);
		
		this.jframe = new JFrame(getClass().getSimpleName());
		jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jframe.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				setWinSize(jframe.getContentPane().getWidth(), jframe.getContentPane().getHeight());
			}
		});
		this.canvas = new JPanel() {
			private static final long serialVersionUID = -5345971935323546584L;
			@Override
			public void update(Graphics g) {
				if(isShowing()) paint(g);
			}
			@Override
			public void paint(Graphics g) {
				Sketch.this.graphics = g;
				Sketch.this.frame++;
				try {
					Sketch.this.draw();
				} catch (Throwable t) {
					terminate(t, getClass().getName());
				}
			}
		};
		canvas.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
					terminate(null, null);
				else
					Sketch.this.keyPressed(e.getKeyChar());
			}
			public void keyReleased(KeyEvent e) { Sketch.this.keyReleased(e.getKeyChar()); }
		});
		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) { Sketch.this.mouseClicked(e.getButton()); }
		});
		canvas.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
			public void mouseDragged(MouseEvent e) { mouseMoved(e); }
		});
		setWinSize(100, 100);
		jframe.add(canvas);
		
		try {
			this.setup();
		} catch (Throwable t) {
			terminate(t, Sketch.class.getName());
			return;
		}
		
		jframe.pack();
		jframe.setVisible(true);
		
		long lastFPSMillis = System.currentTimeMillis();
		int frameCounter = 0;
		while(jframe.isShowing()) {
			long nextMillis = System.currentTimeMillis() + frameDelta;
			canvas.repaint();
			frameCounter++;
			long millis = System.currentTimeMillis();
			long sleepTime = nextMillis - millis;
			if(sleepTime > 0)
				try { Thread.sleep(sleepTime); } catch (InterruptedException x) { }
			if(millis - lastFPSMillis > 1000) {
				long fpsDelta = millis-lastFPSMillis;
				jframe.setTitle(getClass().getSimpleName() + " " + frameCounter*1000/fpsDelta + " fps");
				lastFPSMillis = millis;
				frameCounter = 0;
			}
		}
	}
	
	private final void terminate(Throwable t, String className) {
		if(t != null) {
			StackTraceElement[] trace = t.getStackTrace();
			int i = 0;
			for( ; i < trace.length && !trace[i].getClassName().equals(className); i++);
			t.setStackTrace(Arrays.copyOfRange(trace, 0, i));
			t.printStackTrace();
		}
		jframe.dispatchEvent(new WindowEvent(jframe, WindowEvent.WINDOW_CLOSING));
	}
	
	/* ------------------ Overridable -------------------- */

	public abstract void setup();
	public abstract void draw();
	public void keyPressed(char key) {}
	public void keyReleased(char key) {}
	public void mouseClicked(int button) {};
	
	/* ------------------ Setup -------------------- */
	
	public final void setWinSize(int width, int height) {
		this.winWidth = width;
		this.winHeight = height;
		this.canvas.setPreferredSize(new Dimension(width, height));
		this.jframe.pack();
	}
	
	public final void setResizable(boolean resizable) {
		jframe.setResizable(resizable);
	}
	
	public final void setFramerate(float fps) {
		if(fps <= 0)
			this.frameDelta = Long.MAX_VALUE;
		else
			this.frameDelta = (long) (1000/fps);
	}
	
	/** @deprecated causes the canvas to flicker */
	@Deprecated
	public final void setFramerateUnlimited() {
		this.frameDelta = 0;
	}
	
	/* ------------------ Render -------------------- */
	
	public final void fill(Color color) {
		this.fillColor = color;
	}
	
	public final void fill(int r, int g, int b) {
		this.fill(r, g, b, 255);
	}
	
	public final void fill(int r, int g, int b, int a) {
		this.fillColor = new Color(r, g, b, a);
	}
	
	public final void noFill() {
		this.fillColor = null;
	}
	
	public final void stroke(Color color) {
		this.strokeColor = color;
	}
	
	public final void stroke(int r, int g, int b) {
		this.stroke(r, g, b, 255);
	}
	
	public final void stroke(int r, int g, int b, int a) {
		this.strokeColor = new Color(r, g, b, a);
	}
	
	public final void noStroke() {
		this.strokeColor = null;
	}
	
	private final boolean beginFill() {
		if(this.fillColor != null)
			this.graphics.setColor(fillColor);
		return this.fillColor != null;
	}
	
	private final boolean beginStroke() {
		if(this.strokeColor != null)
			this.graphics.setColor(strokeColor);
		return this.strokeColor != null;
	}
	
	public final void clearColor(Color color) {
		if(color == null)
			throw new NullPointerException("The clear color cannot be null");
		this.clearColor = color;
	}
	
	public final void clear() {
		graphics.setColor(clearColor);
		graphics.fillRect(0, 0, winWidth, winHeight);
	}
	
	public final void rect(float x, float y, float width, float height) {
		if(beginFill())
			graphics.fillRect((int) x, (int) y, (int) width, (int) height);
		if(beginStroke())
			graphics.drawRect((int) x, (int) y, (int) width, (int) height);
	}
	
	public final void ellipse(float x, float y, float width, float height) {
		if(beginFill())
			graphics.fillOval((int) (x-width/2), (int) (y-height/2), (int) width, (int) height);
		if(beginStroke())
			graphics.drawOval((int) (x-width/2), (int) (y-height/2), (int) width, (int) height);
	}
	
	public final void circle(float x, float y, float radius) {
		if(beginFill())
			graphics.fillOval((int) (x-radius), (int) (y-radius), (int) (radius*2), (int) (radius*2));
		if(beginStroke())
			graphics.drawOval((int) (x-radius), (int) (y-radius), (int) (radius*2), (int) (radius*2));
	}

	public final void line(float x1, float y1, float x2, float y2) {
		if(beginStroke())
			graphics.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
	}
	
	public final void arrow(float x1, float y1, float x2, float y2) {
		line(x1, y1, x2, y2);
		Vec2 v = new Vec2(x1-x2, y1-y2);
		v = v.normalized().multiply(10);
		final float a = PI/8;
		final float c = cos(a), s = sin(a);
		line(x2, y2, x2+c*v.x-s*v.y, y2+s*v.x+c*v.y);
		line(x2, y2, x2+c*v.x+s*v.y, y2-s*v.x+c*v.y);
	}
	
	/* ------------------ Math -------------------- */
	
	public final float PI = (float) Math.PI;
	
	public final int ceil(float f) {
		return (int) Math.ceil(f);
	}
	
	public final int floor(float f) {
		return (int) Math.floor(f);
	}
	
	public final float random() {
		return (float) Math.random();
	}
	
	public final int random(int min, int max) {
		return (int) (random()*(max-min)+min);
	}
	
	public final static float sqrt(float f) {
		return (float) Math.sqrt(f);
	}
	
	public final float cos(float f) {
		return (float) Math.cos(f);
	}
	
	public final float sin(float f) {
		return (float) Math.sin(f);
	}
	
	public final float tan(float f) {
		return (float) Math.tan(f);
	}
	
	public static class Vec2 {
		
		public float x, y;
		
		public Vec2(float x, float y) {
			this.x = x;
			this.y = y;
		}
		
		public Vec2 normalized() {
			float l = length();
			return new Vec2(x/l, y/l);
		}
		
		public Vec2 add(Vec2 v) {
			return new Vec2(x+v.x, y+v.y);
		}
		
		public Vec2 substract(Vec2 v) {
			return new Vec2(x-v.x, y-v.y);
		}
		
		public Vec2 multiply(float f) {
			return new Vec2(x*f, y*f);
		}
		
		public float length() {
			return sqrt(x*x + y*y);
		}
		
	}
	
	/* ------------------ Debug -------------------- */
	
	public final <T> T LOG(T t) {
		if(t == null) {
			System.out.println(t);
			return t;
		}
//		if(t.getClass().isArray()) {
//			System.out.println(Arrays.deepToString((Object[]) t));
//			return t;
//		}
		System.out.println(t);
		return t;
	}
	
	public final void print(Object... objs) {
		for(Object o : objs) {
			System.out.print(o);
			System.out.print(" ");
		}
		System.out.println();
	}
	
	/* ----------------- Graphs ------------------ */
	
	public static class Tuple<T, K> {
		
		public T first;
		public K second;
		
		public Tuple(T first, K second) {
			this.first = first;
			this.second = second;
		}
		
		@Override
		public boolean equals(Object other) {
			return other instanceof Tuple &&
					Objects.equals(first, ((Tuple<?, ?>) other).first) &&
					Objects.equals(second, ((Tuple<?, ?>) other).second);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(first, second);
		}
		
	}
	
	public static class Pair<T> extends Tuple<T, T> {
		
		public Pair(T first, T second) {
			super(first, second);
		}
		
	}
	
	public static class Graph implements Iterable<Node> {
		
		private final List<Node> nodes = new ArrayList<>();
		private final Set<Pair<Node>> edges = new HashSet<>();
		
		public final boolean directionalGraph;
		
		public Graph(boolean directionalGraph) {
			this.directionalGraph = directionalGraph;
		}
		
		public void addNode(Node... n) {
			for(Node node : n)
				if(!nodes.contains(node))
					nodes.add(node);
		}
		
		public void removeNode(Node... n) {
			for(Node node : n)
				nodes.remove(node);
		}

		public void addEdge(Node n1, Node n2) {
			if(n1 == n2)
				throw new IllegalArgumentException("The two nodes are the same");
			if(n1.id > n2.id && !directionalGraph) {
				Node t = n1;
				n1 = n2;
				n2 = t;
			}
			edges.add(new Pair<>(n1, n2));
		}
		
		public void removeEdge(Node n1, Node n2) {
			if(n1.id > n2.id && !directionalGraph) {
				Node t = n1;
				n1 = n2;
				n2 = t;
			}
			edges.remove(new Pair<>(n1, n2));
		}
		
		public void addBidirectionalEdge(Node n1, Node n2) {
			addEdge(n1, n2);
			addEdge(n2, n1);
		}
		
		public void removeBidirectionalEdge(Node n1, Node n2) {
			removeEdge(n1, n2);
			removeEdge(n2, n1);
		}
		
		public List<Node> getConnectedNodes(Node n) {
			List<Node> connected = new ArrayList<>();
			for(Pair<Node> e : edges) {
				if(e.first == n)
					connected.add(e.second);
				else if(!directionalGraph && e.second == n)
					connected.add(e.first);
			}
			return connected;
		}
		
		public int size() {
			return nodes.size();
		}
		
		public Node getNode(int index) {
			return nodes.get(index);
		}
		
		public List<Node> getNodes() {
			return nodes;
		}
		
		public Set<Pair<Node>> getEdges() {
			return edges;
		}
		
		@Override
		public Iterator<Node> iterator() {
			return nodes.iterator();
		}

		public int edgeCount() {
			return edges.size();
		}
		
	}
	
	public static class Node {
		
		private static final float DEFAULT_SIZE = 10f;
		private static int nextId;
		
		public final int id;
		public float x, y, size;
		public Color color;
		public String text;
		
		public Node(float x, float y, float size, String text) {
			this.id = nextId++;
			this.x = x;
			this.y = y;
			this.size = size;
			this.text = text;
		}

		public Node(float x, float y, float size) {
			this(x, y, size, null);
		}
		
		public Node(float x, float y, String text) {
			this(x, y, DEFAULT_SIZE, text);
		}
		
		public Node(float x, float y) {
			this(x, y, DEFAULT_SIZE, null);
		}
		
	}
	
	public final void drawGraph(Graph g, boolean drawEdges) {
		noStroke();
		for(int i = 0; i < g.size(); i++) {
			Node n = g.getNode(i);
			if(n.color == null)
				n.color = Color.WHITE;
			fill(n.color);
			circle(n.x, n.y, n.size);
			// TODO draw text
		}
		if(!drawEdges)
			return;
		stroke(Color.white);
		if(g.directionalGraph) {
			for(Pair<Node> edge : g.edges) {
				Vec2 n1 = new Vec2(edge.first.x, edge.first.y);
				Vec2 n2 = new Vec2(edge.second.x, edge.second.y);
				Vec2 v = n2.substract(n1).normalized();
				n1 = n1.add(v.multiply(edge.first.size));
				n2 = n2.substract(v.multiply(edge.second.size));
				arrow(n1.x, n1.y, n2.x, n2.y);
			}
		} else {
			for(Pair<Node> edge : g.edges) {
				line(edge.first.x, edge.first.y, edge.first.x, edge.second.y);
			}
		}
	}
	
}
