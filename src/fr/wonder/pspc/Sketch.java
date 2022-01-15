package fr.wonder.pspc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * The main purpose of this class is to expose
 * lots of methods without having to import anything.<br>
 * <br>
 * Most methods should be self-explanatory, explore each
 * section (maths, graphics...) to see what capabilities
 * they expose.
 */
public abstract class Sketch {
	
	public static final Color[] DEFAULT_COLOR_SCHEME = new Color[] {
			colorFromHex("#a6cee3"),colorFromHex("#1f78b4"),colorFromHex("#b2df8a"),colorFromHex("#33a02c"),
			colorFromHex("#fb9a99"),colorFromHex("#e31a1c"),colorFromHex("#fdbf6f"),colorFromHex("#ff7f00"),
			colorFromHex("#cab2d6"),colorFromHex("#6a3d9a"),colorFromHex("#ffff99"),colorFromHex("#b15928")
	};
	
	private static int currentColorInScheme;

	public static final int CLICK_LEFT = 1;
	public static final int CLICK_RIGHT = 3;
	
	public int winWidth, winHeight;
	public int frame;
	public int mouseX, mouseY;
	public boolean mousePressed;
	
	private long frameDelta = 1000/60;
	
	private JFrame jframe;
	private JPanel canvas;
	
	private Graphics2D graphics;
	
	private Color 	fillColor = Color.white,
					strokeColor = Color.black,
					clearColor = Color.black;
	
	private float strokeWidth = 1;
	private Font defaultFont;
	private final Map<Integer, Font> derivedFonts = new HashMap<>();
	
	/** Entry point of sketches */
	public void start() {
		System.setProperty("sun.awt.noerasebackground", "true");
		Toolkit.getDefaultToolkit().setDynamicLayout(false);
		
		this.jframe = new JFrame(getClass().getSimpleName());
		jframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jframe.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				int w = jframe.getContentPane().getWidth();
				int h = jframe.getContentPane().getHeight();
				if(w == 0 || h == 0)
					return;
				setWinSize(w, h);
			}
		});
		this.defaultFont = new Font("Courier New", Font.PLAIN, 12);
		derivedFonts.put(defaultFont.getSize(), defaultFont);
		
		this.canvas = new JPanel() {
			private static final long serialVersionUID = -5345971935323546584L;
			@Override
			public void update(Graphics g) {
				if(isShowing()) paint(g);
			}
			@Override
			public void paint(Graphics g) {
				Sketch.this.graphics = (Graphics2D) g;
				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				Sketch.this.frame++;
				try {
					Sketch.this.draw();
				} catch (Throwable t) {
					terminate(t, getClass().getName());
				}
			}
		};
		jframe.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
					terminate(null, null);
				else
					Sketch.this.keyPressed(e.getKeyChar());
			}
			public void keyReleased(KeyEvent e) { Sketch.this.keyReleased(e.getKeyChar()); }
		});
		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				Sketch.this.mousePressed = true;
				Sketch.this.mouseClicked(e.getButton());
			}
			public void mouseReleased(MouseEvent e) {
				Sketch.this.mousePressed = false;
			}
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
		
		jframe.setVisible(true);
		canvas.grabFocus();
		
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
		System.exit(1);
	}
	
	/* ------------------ Overridable -------------------- */

	public abstract void setup();
	public abstract void draw();
	public void keyPressed(char key) {}
	public void keyReleased(char key) {}
	public void mouseClicked(int button) {};
	
	/* ------------------ Setup -------------------- */
	
	public final void setWinSize(int width, int height) {
		if(width <= 0 || height <= 0)
			throw new IllegalArgumentException("Negative size");
		this.winWidth = width;
		this.winHeight = height;
		canvas.setPreferredSize(new Dimension(width, height));
		jframe.pack();
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
		fill(r, g, b, 255);
	}
	
	public final void fill(int r, int g, int b, int a) {
		this.fillColor = new Color(r, g, b, a);
	}
	
	public final void noFill() {
		this.fillColor = null;
	}
	
	public final void stroke(Color color) {
		this.strokeColor = Objects.requireNonNull(color);
	}
	
	public final void stroke(int r, int g, int b) {
		stroke(r, g, b, 255);
	}
	
	public final void stroke(int r, int g, int b, int a) {
		this.strokeColor = new Color(r, g, b, a);
	}
	
	public final void strokeWidth(float width) {
		if(width <= 0)
			throw new IllegalArgumentException("Stroke weight must be >0");
		this.strokeWidth = width;
	}
	
	public final void noStroke() {
		this.strokeWidth = 0;
	}
	
	private final boolean beginFill() {
		if(fillColor != null)
			graphics.setColor(fillColor);
		return fillColor != null;
	}
	
	private final boolean beginStroke() {
		if(strokeWidth > 0) {
			graphics.setColor(strokeColor);
			graphics.setStroke(new BasicStroke(strokeWidth));
		}
		return strokeWidth > 0;
	}
	
	public final void clearColor(Color color) {
		this.clearColor = Objects.requireNonNull(color);
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
		v = v.normalized().times(10);
		final float a = PI/8;
		final float c = cos(a), s = sin(a);
		line(x2, y2, x2+c*v.x-s*v.y, y2+s*v.x+c*v.y);
		line(x2, y2, x2+c*v.x+s*v.y, y2-s*v.x+c*v.y);
	}
	
	public final void cross(float x, float y, float size) {
		line(x-size, y-size, x+size, y+size);
		line(x-size, y+size, x+size, y-size);
	}
	
	public final void textSize(float size) {
		graphics.setFont(derivedFonts.computeIfAbsent((int) size, s -> defaultFont.deriveFont(size)));
	}
	
	public final void text(String text, float x, float y) {
		FontMetrics metrics = graphics.getFontMetrics();
		x -= metrics.stringWidth(text)/2f;
		y += metrics.getHeight()/4f;
		graphics.setColor(Color.WHITE);
		graphics.drawString(text, x, y);
	}
	
	public static Color colorFromHex(String hex) {
		if(hex.startsWith("#"))
			hex = hex.substring(1);
		String r = hex.substring(0, 2);
		String g = hex.substring(2, 4);
		String b = hex.substring(4, 6);
		return new Color(
				Integer.parseInt(r, 16)/255f,
				Integer.parseInt(g, 16)/255f,
				Integer.parseInt(b, 16)/255f);
	}
	
	public static Color colorFromHSB(float hue, float saturation, float brightness) {
		return Color.getHSBColor(hue, saturation, brightness);
	}
	
	public static Color colorInScheme(int i) {
		return DEFAULT_COLOR_SCHEME[i % DEFAULT_COLOR_SCHEME.length];
	}
	
	public static Color nextColorInScheme() {
		return colorInScheme(currentColorInScheme++);
	}
	
	/* ------------------ Math -------------------- */
	
	public static final float PI = (float) Math.PI;
	
	public static int ceil(float f) {
		return (int) Math.ceil(f);
	}
	
	public static int floor(float f) {
		return (int) Math.floor(f);
	}
	
	public static float fract(float f) {
		return f%1;
	}
	
	public static float random() {
		return (float) Math.random();
	}
	
	public static float random(float min, float max) {
		return random()*(max-min)+min;
	}
	
	public static int randint(int min, int max) {
		return (int) (random()*(max-min)+min);
	}
	
	public static int abs(int x) {
		return Math.abs(x);
	}
	
	public static float abs(float x) {
		return Math.abs(x);
	}
	
	public static float sqrt(float f) {
		return (float) Math.sqrt(f);
	}
	
	public static float cos(float f) {
		return (float) Math.cos(f);
	}
	
	public static float sin(float f) {
		return (float) Math.sin(f);
	}
	
	public static float tan(float f) {
		return (float) Math.tan(f);
	}

	public static int max(int... ints) {
		int m = ints[0];
		for(int i : ints)
			m = Math.max(i, m);
		return m;
	}
	
	public static float max(float... floats) {
		float m = floats[0];
		for(float i : floats)
			m = Math.max(i, m);
		return m;
	}

	public static int min(int... ints) {
		int m = ints[0];
		for(int i : ints)
			m = Math.min(i, m);
		return m;
	}
	
	public static float min(float... floats) {
		float m = floats[0];
		for(float i : floats)
			m = Math.min(i, m);
		return m;
	}
	
	public static class Vec2 {
		
		public float x, y;
		
		public Vec2(float x, float y) {
			this.x = x;
			this.y = y;
		}
		
		public Vec2() {
			this(0, 0);
		}
		
		public Vec2 normalized() {
			float l = length();
			return new Vec2(x/l, y/l);
		}
		
		public Vec2 plus(Vec2 v) {
			return new Vec2(x+v.x, y+v.y);
		}
		
		public Vec2 minus(Vec2 v) {
			return new Vec2(x-v.x, y-v.y);
		}
		
		public Vec2 times(float f) {
			return new Vec2(x*f, y*f);
		}
		
		public static float dot(Vec2 v1, Vec2 v2) {
			return v1.x*v2.x+v1.y*v2.y;
		}
		
		public float length() {
			return sqrt(lengthSquared());
		}
		
		public float lengthSquared() {
			return x*x + y*y;
		}
		
		@Override
		public String toString() {
			return String.format("(%.2f, %.2f)", x, y);
		}

		public float distanceTo(Vec2 other) {
			return this.minus(other).length();
		}
		
	}
	
	public int parseInt(String str) {
		return Integer.parseInt(str);
	}
	
	public float parseFloat(String str) {
		return Float.parseFloat(str);
	}
	
	/* ------------------ Debug -------------------- */
	
	public final <T> T LOG(T t) {
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
				Vec2 v = n2.minus(n1).normalized();
				n1 = n1.plus(v.times(edge.first.size));
				n2 = n2.minus(v.times(edge.second.size));
				arrow(n1.x, n1.y, n2.x, n2.y);
			}
		} else {
			for(Pair<Node> edge : g.edges) {
				line(edge.first.x, edge.first.y, edge.first.x, edge.second.y);
			}
		}
	}
	
}
