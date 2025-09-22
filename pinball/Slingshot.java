package pinball;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

class Slingshot {
	
	private double spX, spY; // start point coordinates
	private Vector2D seVector;
	private Line2D.Double slingshotLine;
	private boolean active;
	private BasicStroke lineWidth = new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	
	Slingshot(double spX, double spY, double epX, double epY) {
		
		this.spX = spX;
        this.spY = spY;
        
        // Vector from start point to end point. 
    	this.seVector = new Vector2D(epX - spX, epY - spY);
    	
    	// Slingshot line (for rendering)
    	this.slingshotLine = new Line2D.Double(spX, spY, epX, epY);
        
	}
	
	void checkCollision(Ball ball) {
		
    	// Vector from start point to ball center. 
    	Vector2D sbVector = new Vector2D(ball.x - spX, ball.y - spY);
    	
    	// Factor from the projection of the ball center onto the slingshot line. 
    	double pf = sbVector.dot(seVector) / seVector.dot(seVector);
        
        // Adjustment of projection factor for cases where projected point does not lie on slingshot itself. 
        pf = Math.max(0, Math.min(1, pf));
        
        // Vector for overlapping test and slinging. 
        Vector2D slingVector = sbVector.subtract(seVector.scale(pf));
        
        // If there is overlapping, then sling the ball. 
        if (slingVector.length() <= ball.radius) {
        	
        	active = true;
        	
        	// The sling vector should only determine the direction of the ball. 
        	slingVector = slingVector.normalize();
        	
        	// ball velocity
        	double ballVel = Math.hypot(ball.vx, ball.vy);
        	
        	ball.vx = 1.2 * ballVel * slingVector.x;
        	ball.vy = 1.2 * ballVel * slingVector.y;
        	
        	ball.update();
        	
		} else {
			
			// The slingshot is not active. 
			active = false;
			
		}
		
	}
	
	void draw(Graphics2D g2) {
		
    	g2.setStroke(lineWidth);
    	
    	if (active) { // Let the slingshot glow. 
    		g2.setColor(Color.YELLOW);
		} else {
			g2.setColor(Color.ORANGE);
		}
        
        g2.draw(slingshotLine);
        
    }
	
}
