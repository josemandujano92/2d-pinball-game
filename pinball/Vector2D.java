package pinball;

// Class for 2D vector operations. 

public class Vector2D {
	
	public double x, y;

    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D add(Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }

    public Vector2D subtract(Vector2D v) {
        return new Vector2D(x - v.x, y - v.y);
    }

    public Vector2D scale(double s) {
        return new Vector2D(x * s, y * s);
    }

    public double dot(Vector2D v) {
        return x * v.x + y * v.y;
    }

    public double length() {
        return Math.hypot(x, y);
    }
    
    public Vector2D normalize() {
    	return this.scale(1 / this.length());
    }
    
    public Vector2D proj(Vector2D v) { // Projection of this onto v. 
    	return v.scale(this.dot(v) / v.dot(v));
    }
    
    public Vector2D reflect(Vector2D v) { // Reflection of this over v. 
    	return ((this.proj(v)).scale(2)).subtract(this);
    }
    
}

