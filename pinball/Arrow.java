package pinball;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

class Arrow {
	
	private int spX, spY; // start point coordinates
	private int epX, epY; // end point coordinates
	private Vector2D arrow;
	private Vector2D headFront, headSide;
	private boolean active;
	private BasicStroke stroke = new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	Arrow(int spX, int spY, int epX, int epY) {
		
		this.spX = spX;
        this.spY = spY;
        this.epX = epX;
        this.epY = epY;
        
        // Vector from start point to end point. 
        this.arrow = new Vector2D(epX - spX, epY - spY);
        
        // Compute additional points for rendering. 
        headFront = (new Vector2D(epX, epY)).add(arrow.normalize().scale(6));
        headSide = (new Vector2D(arrow.y, -arrow.x)).normalize().scale(6);
     	
	}
	
	void checkOverlap(Ball ball) {
		
    	// Vector from start point to ball center. 
    	Vector2D referenceVector = new Vector2D(ball.x - spX, ball.y - spY);
    	
    	// Factor from the projection of the ball center onto the arrow line. 
    	double pf = referenceVector.dot(arrow) / arrow.dot(arrow);
    	
    	// Adjustment of projection factor for cases where projected point does not lie on arrow itself. 
        pf = Math.max(0, Math.min(1, pf));
        
        // Vector for overlapping test. 
        referenceVector = referenceVector.subtract(arrow.scale(pf));
        
        // If there is overlapping, then redirect the ball. 
        if (referenceVector.length() < ball.radius) {
        	
        	active = true;
        	
        	ball.vx += 0.5 * arrow.x;
        	ball.vy += 0.5 * arrow.y;
        	
        } else {
        	
        	active = false;
        	
        }
		
	}
	
	void draw(Graphics2D g2) {
		
		g2.setStroke(stroke);
		
		if (active) {
			g2.setColor(Color.PINK);
		} else {
			g2.setColor(Color.MAGENTA);
		}
		
    	// arrow head
		g2.drawLine((int) headFront.x, (int) headFront.y, (int) (epX + headSide.x), (int) (epY + headSide.y));
		g2.drawLine((int) headFront.x, (int) headFront.y, (int) (epX - headSide.x), (int) (epY - headSide.y));
		
    	// arrow tail
    	g2.drawLine(spX, spY, epX, epY);
		
	}
	
}
