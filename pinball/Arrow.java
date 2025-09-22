package pinball;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

class Arrow {
	
	private double spX, spY; // start point coordinates
	private double epX, epY; // end point coordinates
	private Vector2D arrow;
	private BasicStroke stroke = new BasicStroke(5, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
	
	Arrow(double spX, double spY, double epX, double epY) {
		
		this.spX = spX;
        this.spY = spY;
        this.epX = epX;
        this.epY = epY;
        
        // Vector from start point to end point. 
    	this.arrow = new Vector2D(epX - spX, epY - spY);
        
	}
	
	void checkOverlap(Ball ball) {
		
    	// Vector from start point to ball center. 
    	Vector2D sbVector = new Vector2D(ball.x - spX, ball.y - spY);
    	
    	// Factor from the projection of the ball center onto the arrow line. 
    	double pf = sbVector.dot(arrow) / arrow.dot(arrow);
    	
    	// Adjustment of projection factor for cases where projected point does not lie on arrow itself. 
        pf = Math.max(0, Math.min(1, pf));
        
        // Vector for overlapping test. 
        Vector2D referenceVector = sbVector.subtract(arrow.scale(pf));
        
        // If there is overlapping, then redirect the ball. 
        if (referenceVector.length() < ball.radius) {
        	ball.vx = 0.5 * arrow.x;
        	ball.vy = 0.5 * arrow.y;
        }
		
	}
	
	void draw(Graphics2D g2) {
		
		g2.setStroke(stroke);
		g2.setColor(Color.MAGENTA);
		
		// Arrow body
		g2.draw(new Line2D.Double(spX, spY, epX, epY));
		
		// Compute additional points. 
		Vector2D headSide = (new Vector2D(arrow.y, -arrow.x)).normalize().scale(5);
		Vector2D headFront = (new Vector2D(epX, epY)).add(arrow.normalize().scale(5));
		
		// Arrow head
		g2.draw(new Line2D.Double(epX + headSide.x, epY + headSide.y, headFront.x, headFront.y));
		g2.draw(new Line2D.Double(headFront.x, headFront.y, epX - headSide.x, epY - headSide.y));
		
	}
	
}
