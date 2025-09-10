package pinball;

import java.awt.*;
import java.awt.geom.Line2D;

class Slingshot {
	
	private double spX, spY; // start point coordinates
	private double epX, epY; // end point coordinates
	private boolean active;
	private Vector2D seVector;
	
	Slingshot(double spX, double spY, double epX, double epY) {
		
		this.spX = spX;
        this.spY = spY;
        this.epX = epX;
        this.epY = epY;
        
        // Vector from start point to end point. 
    	this.seVector = new Vector2D(epX - spX, epY - spY);
        
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
		
    	g2.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    	
    	if (active) { // Let the slingshot glow. 
    		g2.setColor(Color.YELLOW);
		} else {
			g2.setColor(Color.ORANGE);
		}
        
        g2.draw(new Line2D.Double(spX, spY, epX, epY));
        
    }
	
}
