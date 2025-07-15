package pinball;

import java.awt.*;
import java.awt.geom.Line2D;

public class Flipper {
	
	double px, py; // pivot
    boolean isLeft; // determines direction of rotation
    boolean pressed = false;
    double angle;
    double angleSpeed = Math.toRadians(5);
    double length1 = 70;
    double length2 = 90;
    float width = 15;

    public Flipper(double px, double py, boolean isLeft) {
        this.px = px;
        this.py = py;
        this.isLeft = isLeft;
    }

    public void press() {
        pressed = true;
    }

    public void release() {
        pressed = false;
    }

    // Adjust pivot/angle according to side. 
    public void update() {
    	
    	if (isLeft) {
    		if (pressed) {
        		angle -= angleSpeed;
        		angle = Math.max(angle, Math.toRadians(0));
        		px += 3;
        		px = Math.min(px, 200);
    		} else {
    			angle += angleSpeed;
    			angle = Math.min(angle, Math.toRadians(45));
    			px -= 3;
    			px = Math.max(px, 150);
    		}
		} else { // right
			if (pressed) {
        		angle += angleSpeed;
        		angle = Math.min(angle, Math.toRadians(180));
        		px -= 3;
    			px = Math.max(px, 400);
    		} else {
    			angle -= angleSpeed;
    			angle = Math.max(angle, Math.toRadians(135));
    			px += 3;
        		px = Math.min(px, 450);
    		}
		}
    	
    }

    public boolean checkCollision(Ball ball) {
    	
    	// Vector from pivot to center of ball. 
    	Vector2D pcVector = new Vector2D(ball.x - px, ball.y - py);
    	
    	// Flipper of unit length. 
    	Vector2D unitFlipper = new Vector2D(Math.cos(angle), Math.sin(angle));
    	
    	// Factor from the projection of the ball center onto the flipper line. 
    	double pf = pcVector.dot(unitFlipper);
        
        // Adjustment of projection factor for cases where projected point does not lie on flipper itself. 
        pf = Math.max(0, Math.min(length1, pf));
        
        // Vector for collision test and reflection. 
        Vector2D referenceVector = pcVector.subtract(unitFlipper.scale(pf));
        
        // Collision with flipper. 
        if (referenceVector.length() < ball.radius) {
        	
        	Vector2D movementVector = new Vector2D(-ball.vx, -ball.vy);
    		movementVector = movementVector.reflect(referenceVector);
    		
    		ball.vx = movementVector.x;
    		ball.vy = movementVector.y;
    		
    		return true;
			
		}
        
        // Collision with flat part. 
        if (isLeft) {
        	if (px - length2 < ball.x && px > ball.x && py < ball.y + ball.radius && py + width > ball.y + ball.radius) {
				ball.vy = -ball.vy;
				return true;
			}
		} else {
			if (px < ball.x && px + length2 > ball.x && py < ball.y + ball.radius && py + width > ball.y + ball.radius) {
				ball.vy = -ball.vy;
				return true;
			}
		}
        
        // There is no collision. 
        return false;
        
    }
    
    public void draw(Graphics2D g2) {
    	
    	g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    	g2.setColor(Color.GRAY);
    	
    	if (isLeft) {
    		g2.draw(new Line2D.Double(px - length2, py, px, py));
		} else {
			g2.draw(new Line2D.Double(px, py, px + length2, py));
		}
    	
        double tx = px + Math.cos(angle) * length1;
        double ty = py + Math.sin(angle) * length1;
        
        g2.draw(new Line2D.Double(px, py, tx, ty));
        
    }

}

