package pinball;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Bumper {
	
	double x, y, radius;

    public Bumper(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public boolean checkCollision(Ball ball) {
    	
    	// Vector from bumper center to ball center. 
    	Vector2D centersVector = new Vector2D(ball.x - x, ball.y - y);
    	
    	// If there is a collision, then reflect the ball. 
    	if (centersVector.length() < ball.radius + radius) {
    		
    		Vector2D movementVector = new Vector2D(-ball.vx, -ball.vy);
    		movementVector = movementVector.reflect(centersVector);
    		
    		// speed up 
    		ball.vx = 1.1 * movementVector.x;
    		ball.vy = 1.1 * movementVector.y;
    		
			return true;
			
		} else {
			
			return false;
			
		}
    	
    }
    
    public void draw(Graphics2D g2) {
        g2.setColor(Color.LIGHT_GRAY);
        g2.fill(new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2));
        g2.setColor(Color.DARK_GRAY);
        g2.fill(new Ellipse2D.Double(x - radius / 2, y - radius / 2, radius, radius));
    }

}

