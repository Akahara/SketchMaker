package fr.wonder.pspc;

import fr.wonder.pspc.Sketch.Vec2;
import static fr.wonder.pspc.Sketch.*;

import java.util.Objects;

public class SketchMath {

	public static class Matrix {
		
		public final int width, height;
		private final float[][] components;
		
		public Matrix(int width, int height) {
			this(new float[height][width]);
		}
		
		public Matrix(float[][] components) {
			this.width = components[0].length;
			this.height = components.length;
			this.components = components;
		}
		
		public Matrix(float a11, float a12, float a21, float a22) {
			this(new float[][] {
				{ a11, a12 },
				{ a21, a22 }});
		}
		
		public static Matrix rotation2(float angle) {
			float c = cos(angle);
			float s = sin(angle);
			return new Matrix(c, -s, s, c);
		}
		
		public static Matrix identity2() {
			return new Matrix(0, 1, 1, 0);
		}
		
		@Override
		public String toString() {
			return "[" + width + "x" + height + "]";
		}
		
		public float at(int row, int col) {
			Objects.checkIndex(row, width);
			Objects.checkIndex(col, height);
			return components[row][col];
		}
		
		public Matrix set(int row, int col, float x) {
			Objects.checkIndex(row, width);
			Objects.checkIndex(col, height);
			this.components[row][col] = x;
			return this;
		}
		
		public Vec2 multiply(Vec2 v) {
			if(width != 2 || height != 2)
				throw new IllegalStateException("Cannot multiply a non 2x2 matrix with a vector");
			return new Vec2(
					at(0, 0)*v.x+at(0, 1)*v.y,
					at(1, 0)*v.x+at(1, 1)*v.y);
		}
		
		public Matrix multiply(Matrix m) {
			if(width != m.height)
				throw new IllegalArgumentException("Cannot multiply matrices with sizes " + this + " " + m);
			float[][] newComp = new float[height][m.width];
			for(int i = 0; i < height; i++) {
				for(int j = 0; j < m.width; j++) {
					for(int k = 0; k < width; k++)
						newComp[i][j] += at(i, k)*m.at(k, j);
				}
			}
			return new Matrix(newComp);
		}
		
		public Matrix add(Matrix m) {
			if(width != m.width || height != m.height)
				throw new IllegalArgumentException("Cannot multiply matrices with sizes " + this + " " + m);
			float[][] newComp = new float[height][m.width];
			for(int i = 0; i < height; i++) {
				for(int j = 0; j < m.width; j++) {
					newComp[i][j] = at(i, j) + m.at(i, j);
				}
			}
			return new Matrix(newComp);
		}
		
	}
}
