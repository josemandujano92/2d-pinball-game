package pinball;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

class Bumper {
	
	private double x, y, radius; // Location and size of the bumper. 
	private Ellipse2D.Double outerPart, innerPart;
	private boolean active;
	
    Bumper(double x, double y, double radius) {
    	
        this.x = x;
        this.y = y;
        this.radius = radius;
        
        // Parts of the bumper (for rendering). 
        this.outerPart = new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2);
        this.innerPart = new Ellipse2D.Double(x - radius / 2, y - radius / 2, radius, radius);
        
    }
    
    boolean checkCollision(Ball ball) {
    	
    	// Vector from bumper center to ball center. 
    	Vector2D centersVector = new Vector2D(ball.x - x, ball.y - y);
    	
    	// If there is a collision, then reflect the ball. 
    	if (centersVector.length() < ball.radius + radius) {
    		
    		active = true;
    		
    		Vector2D movementVector = new Vector2D(-ball.vx, -ball.vy);
    		movementVector = movementVector.reflect(centersVector);
    		
    		// speed up
    		ball.vx = 1.2 * movementVector.x;
    		ball.vy = 1.2 * movementVector.y;
    		
    		ball.update();
    		
		} else {
			
			// The bumper is not active. 
			active = false;
			
		}
    	
    	// Return information for the game panel. 
    	return active;
    	
    }
    
    void draw(Graphics2D g2) {
    	
    	g2.setColor(Color.LIGHT_GRAY);
    	
        g2.fill(outerPart);
    	
        // Let the center of the bumper "blink" on contact. 
        if (active) {
    		g2.setColor(Color.GRAY);
		} else {
			g2.setColor(Color.DARK_GRAY);
		}
    	
        g2.fill(innerPart);
        
    }
    
}
