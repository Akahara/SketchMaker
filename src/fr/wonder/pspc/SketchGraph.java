package fr.wonder.pspc;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static fr.wonder.pspc.Sketch.*;

public class SketchGraph {

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
}
