package pinball;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

class Slingshot {
	
	private int numOfVertices;
	private ArrayList<SlingshotSegment> segments = new ArrayList<SlingshotSegment>();
	private boolean active;
	private int[] x, y;
	private BasicStroke thin = new BasicStroke(7, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private BasicStroke thick = new BasicStroke(11, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	Slingshot(double[]... vertices) {
		
		numOfVertices = vertices.length;
		
		x = new int[numOfVertices];
    	y = new int[numOfVertices];
		
		for (int i = 0; i < numOfVertices - 1; i++) {
			
			// Add segments to the slingshot. 
	    	segments.add(new SlingshotSegment(vertices[i], vertices[i + 1]));
	    	
	    	// for rendering
	    	x[i] = (int) (segments.get(i)).spX;
	    	y[i] = (int) (segments.get(i)).spY;
	    	
	    }
		
		x[numOfVertices - 1] = (int) (segments.getLast()).epX;
    	y[numOfVertices - 1] = (int) (segments.getLast()).epY;
    	
	}
	
	boolean checkCollision(Ball ball) {
		
		// Check collisions with slingshot segments. 
		for (SlingshotSegment s : segments) {
			if (s.checkCollision(ball)) return true;
		}
		
		// There is no collision. 
		return false;
		
	}
	
	private class SlingshotSegment {
		
		private double spX, spY; // start point coordinates
		private double epX, epY; // end point coordinates
		private Vector2D seVector;
		
		private SlingshotSegment(double[] sp, double[] ep) {
			
			spX = sp[0];
	        spY = sp[1];
	        
	        epX = ep[0];
	        epY = ep[1];
	        
	        // Vector from start point to end point. 
	    	seVector = new Vector2D(epX - spX, epY - spY);
			
		}
		
		private boolean checkCollision(Ball ball) {
			
	    	// Vector from start point to ball center. 
	    	Vector2D slingVector = new Vector2D(ball.x - spX, ball.y - spY);
	    	
	    	// Factor from the projection of the ball center onto the slingshot line. 
	    	double pf = slingVector.dot(seVector) / seVector.dot(seVector);
	        
	        // Adjustment of projection factor for cases where projected point does not lie on slingshot itself. 
	        pf = Math.max(0, Math.min(1, pf));
	        
	        // Vector for collision test and slinging. 
	        slingVector = slingVector.subtract(seVector.scale(pf));
	        
	        // If there is a collision, then sling the ball. 
	        if (slingVector.length() <= ball.radius) {
	        	
	        	active = true;
	        	
	        	// Adjust sling vector with ball velocity. 
	        	slingVector = slingVector.normalize().scale(Math.hypot(ball.vx, ball.vy));
	        	
	        	ball.vx = 1.3 * slingVector.x;
	        	ball.vy = 1.3 * slingVector.y;
	        	
	        	ball.update();
	        	
			} else {
				
				active = false;
				
			}
	        
	        // Return information about activity. 
	    	return active;
			
		}
		
	}
	
	void draw(Graphics2D g2) {
		
		g2.setColor(Color.ORANGE);
		
		g2.setStroke(thin);
        g2.drawPolygon(x, y, numOfVertices);
		
		g2.setColor(Color.YELLOW);
		
    	g2.fillPolygon(x, y, numOfVertices);
		
		if (active) { // let the slingshot glow
    		g2.setStroke(thick);
    		g2.drawPolygon(x, y, numOfVertices);
		}
		
    }
	
}
