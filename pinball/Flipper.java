package pinball;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

class Flipper {
	
	private double px, py; // pivot coordinates
    private boolean isLeft; // determines direction of rotation
    private boolean pressed;
    private double angleSpeed = Math.toRadians(5);
    private double angle;
    private double length1 = 60;
    private double length2 = 90;
    private double joinRadius = 17; // radius of the join area
    private float width = 15;
    private BasicStroke strokeCircle = new BasicStroke(width / 2);
    private BasicStroke strokeFlatParts = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    
    Flipper(double px, double py, boolean isLeft) {
    	
        this.px = px;
        this.py = py;
        this.isLeft = isLeft; // or right flipper
        
        // Initialize angle depending on side. 
        if (isLeft) {
        	this.angle = Math.toRadians(45);
		} else {
			this.angle = Math.toRadians(135);
		}
        
    }
    
    // Methods for key inputs. 
    
    void press() {
    	pressed = true;
    }
    
    void release() {
        pressed = false;
    }
    
    // Adjust pivot/angle according to side and key inputs. 
    void update(Ball ball) {
    	
    	// If the ball is near the flippers do an additional collision check. 
    	if (ball.y - ball.radius > 0.75 * GamePanel.HEIGHT) {
    		
    		// Move the ball slightly. 
    		ball.x += 0.5 * ball.vx;
    		ball.y += 0.5 * ball.vy;
    		
    		if (checkCollision(ball)) return;
    		
    		// If there is no collision, then return the ball to it's previous location. 
    		ball.x -= 0.5 * ball.vx;
    		ball.y -= 0.5 * ball.vy;
    		
    	}
    	
    	if (isLeft) {
    		
    		if (pressed) {
        		angle -= angleSpeed;
        		angle = Math.max(angle, Math.toRadians(0));
        		px += 3;
        		px = Math.min(px, length2 + 75);
    		} else {
    			angle += angleSpeed;
    			angle = Math.min(angle, Math.toRadians(45));
    			px -= 3;
    			px = Math.max(px, length2 + 25);
    		}
    		
		} else { // is right
			
			if (pressed) {
        		angle += angleSpeed;
        		angle = Math.min(angle, Math.toRadians(180));
        		px -= 3;
    			px = Math.max(px, GamePanel.WIDTH - length2 - 75);
    		} else {
    			angle -= angleSpeed;
    			angle = Math.max(angle, Math.toRadians(135));
    			px += 3;
        		px = Math.min(px, GamePanel.WIDTH - length2 - 25);
    		}
			
		}
    	
    }
    
    boolean checkCollision(Ball ball) {
    	
    	// Check collisions with join circle and rotating/sliding parts. 
    	if (checkCollisionJoin(ball) | checkCollision1(ball) | checkCollision2(ball)) return true;
    	
    	// There is no collision. 
    	return false;
    	
    }
    
    // Check contact with join area. 
    private boolean checkCollisionJoin(Ball ball) {
    	
    	// Vector from pivot to center of ball. 
    	Vector2D pcVector = new Vector2D(ball.x - px, ball.y - py);
    	
    	// If there is contact, then adjust the direction of the ball. 
    	if (pcVector.length() <= joinRadius + ball.radius) {
    		
    		Vector2D movementVector = new Vector2D(-ball.vx, -ball.vy);
    		movementVector = movementVector.reflect(pcVector);
    		
    		ball.vx = movementVector.x;
    		ball.vy = movementVector.y;
    		
    		// Move the ball away from the join to avoid clipping. 
    		movementVector = movementVector.normalize().scale(ball.radius / 2);
    		ball.x += movementVector.x;
    		ball.y += movementVector.y;
    		
    		return true;
			
		}
    	
    	// There is no collision. 
    	return false;
    	
    }
    
    // Check collision with rotating part. 
    private boolean checkCollision1(Ball ball) {
    	
    	// Vector from pivot to center of ball. 
    	Vector2D pcVector = new Vector2D(ball.x - px, ball.y - py);
    	
    	// Flipper of unit length. 
    	Vector2D unitFlipper = new Vector2D(Math.cos(angle), Math.sin(angle));
    	
    	// Factor from the projection of the ball center onto the flipper line. 
    	double pf = pcVector.dot(unitFlipper);
        
        // Adjustment of projection factor for cases where projected point does not lie on flipper itself. 
        pf = Math.max(0, Math.min(length1, pf));
        
        // Vector for collision test (and reflection). 
        Vector2D referenceVector = pcVector.subtract(unitFlipper.scale(pf));
        
        // If there is a collision, then move the ball accordingly. 
        if (referenceVector.length() <= ball.radius) {
        	
        	// Ensure that the reference vector is pointing upwards for a correct reflection of the ball (especially near the tip of the flipper). 
        	if (referenceVector.y < 0) referenceVector = referenceVector.scale(-1);
        	
        	Vector2D movementVector = new Vector2D(-ball.vx, -ball.vy);
    		movementVector = movementVector.reflect(referenceVector);
    		
    		ball.vx = movementVector.x;
    		ball.vy = movementVector.y;
    		
    		// Move the ball away to avoid clipping. 
    		if (pressed) {
    			movementVector = movementVector.normalize().scale(ball.radius);
    			ball.x += movementVector.x;
    			ball.y += movementVector.y;
    		}
    		
    		ball.update();
    		
    		return true;
			
		}
        
        // There is no collision. 
        return false;
        
    }
    
    // Check collision with sliding part. 
    private boolean checkCollision2(Ball ball) {
    	
    	// Check if the ball is in the vicinity of the flipper. 
    	if (py - width / 2 < ball.y + ball.radius && py + width / 2 > ball.y + ball.radius) {
    		
    		// Check only the correct side. 
    		if (isLeft) {
    			
            	if (px - length2 < ball.x + ball.radius && px > ball.x - ball.radius) {
            		
    				ball.vy = -ball.vy;
    				
    				// Move the ball upwards to avoid clipping. 
    				ball.y -= ball.radius / 2;
    				
    				return true;
    				
    			}
            	
    		} else {
    			
    			if (px < ball.x + ball.radius && px + length2 > ball.x - ball.radius) {
    				
    				ball.vy = -ball.vy;
    				
    				// Move the ball upwards to avoid clipping. 
    				ball.y -= ball.radius / 2;
    				
    				return true;
    				
    			}
    			
    		}
    		
    	}
    	
        // There is no collision. 
        return false;
        
    }
    
    void draw(Graphics2D g2) {
    	
    	g2.setColor(Color.GRAY);
    	g2.setStroke(strokeFlatParts);
    	
    	// Draw only the correct side. 
    	if (isLeft) {
    		g2.draw(new Line2D.Double(px - length2, py, px, py));
		} else {
			g2.draw(new Line2D.Double(px, py, px + length2, py));
		}
    	
    	// The tip of the flipper. 
        double tx = px + Math.cos(angle) * length1;
        double ty = py + Math.sin(angle) * length1;
        
        g2.draw(new Line2D.Double(px, py, tx, ty));
        
    	// The join area. 
        g2.setColor(Color.WHITE);
        g2.setStroke(strokeCircle);
    	g2.draw(new Ellipse2D.Double(px - joinRadius, py - joinRadius, joinRadius * 2, joinRadius * 2));
        
    }
    
}
