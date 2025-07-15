package pinball;

import java.awt.*;
import java.awt.geom.Line2D;

public class Slingshot {
	
	double spx, spy; // start point
	double epx, epy; // end point
	boolean active = false;
	
	public Slingshot(double spx, double spy, double epx, double epy) {
		this.spx = spx;
        this.spy = spy;
        this.epx = epx;
        this.epy = epy;
	}
	
	public void checkCollision(Ball ball) {
		
		// Vector from start to end point of slingshot. 
    	Vector2D seVector = new Vector2D(epx - spx, epy - spy);
    	
    	// Vector from start point to ball center. 
    	Vector2D sbVector = new Vector2D(ball.x - spx, ball.y - spy);
    	
    	// Factor from the projection of the ball center onto the slingshot line. 
    	double pf = sbVector.dot(seVector) / seVector.dot(seVector);
        
        // Adjustment of projection factor for cases where projected point does not lie on slingshot itself. 
        pf = Math.max(0, Math.min(1, pf));
        
        // Vector for overlapping test and slinging. 
        Vector2D slingVector = sbVector.subtract(seVector.scale(pf));
        
        // If there is overlapping, then sling the ball. 
        if (slingVector.length() < ball.radius) {
        	
        	active = true;
        	
        	// ball velocity 
        	double ballVel = Math.hypot(ball.vx, ball.vy);
        	
        	ball.vx = 0.2 * ballVel * slingVector.x;
        	ball.vy = 0.2 * ballVel * slingVector.y;
        	
		} else {
			
			active = false;
			
		}
		
	}
	
	public void draw(Graphics2D g2) {
		
    	g2.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    	
    	if (active) {
    		g2.setColor(Color.YELLOW);
		} else {
			g2.setColor(Color.ORANGE);
		}
        
        g2.draw(new Line2D.Double(spx, spy, epx, epy));
        
    }

}

