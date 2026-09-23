package pinball;

import java.awt.Color;
import java.awt.Graphics2D;

class Bumper {
	
	private int x, y, radius; // Location and size of the bumper. 
	private boolean active;
	
    Bumper(int x, int y, int radius) {
    	
        this.x = x;
        this.y = y;
        this.radius = radius;
        
    }
    
    boolean checkCollision(Ball ball) {
    	
    	// Vector from bumper center to ball center. 
    	Vector2D centersVector = new Vector2D(ball.x - x, ball.y - y);
    	
    	// If there is a collision reflect the ball. 
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
    	
        // outer part
        g2.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
    	
        // Let the center of the bumper "blink" on contact. 
        if (active) {
        	g2.setColor(Color.WHITE);
		} else {
			g2.setColor(Color.DARK_GRAY);
		}
        
        // inner part
        g2.fillOval(x - radius / 2, y - radius / 2, radius, radius);
    	
    }
    
}
