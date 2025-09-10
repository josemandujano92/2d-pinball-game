package pinball;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

class Flipper {
	
	private double px, py; // pivot coordinates
    private boolean isLeft; // determines direction of rotation
    private boolean pressed;
    private double angle;
    private double angleSpeed = Math.toRadians(5);
    private double length1 = 60;
    private double length2 = 90;
    private float width = 15;
    private double joinRadius = 17; // radius for the join area
    
    Flipper(double px, double py, boolean isLeft) {
        this.px = px;
        this.py = py;
        this.isLeft = isLeft;
    }
    
    // Methods for key inputs. 
    
    void press() {
    	pressed = true;
    }
    
    void release() {
        pressed = false;
    }
    
    // Adjust pivot/angle according to side and key inputs. 
    void update() {
    	
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
    	
    	// Check collision with rotating part and sliding part. 
    	if (checkCollision1(ball) || checkCollision2(ball)) return true;
    	
    	// There is no collision. 
    	return false;
    	
    }
    
    // Check collision with rotating part and join. 
    private boolean checkCollision1(Ball ball) {
    	
    	// Vector from pivot to center of ball. 
    	Vector2D pcVector = new Vector2D(ball.x - px, ball.y - py);
    	
    	// Check collision at the join (and reflect the ball). 
    	if (pcVector.length() <= joinRadius + ball.radius) {
    		
    		Vector2D movementVector = new Vector2D(-ball.vx, -ball.vy);
    		movementVector = movementVector.reflect(pcVector);
    		
    		ball.vx = movementVector.x;
    		ball.vy = movementVector.y;
    		
    		ball.update();
    		
    		return true;
			
		}
    	
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
        	
        	Vector2D movementVector = new Vector2D(-ball.vx, -ball.vy);
    		movementVector = movementVector.reflect(referenceVector);
    		
    		ball.vx = movementVector.x;
    		ball.vy = movementVector.y;
    		
    		ball.update();
    		
    		return true;
			
		}
        
        // There is no collision. 
        return false;
        
    }
    
    // Check collision with sliding part. 
    private boolean checkCollision2(Ball ball) {
    	
    	// Check if the ball is in the vicinity of the flipper. 
    	if (py < ball.y + ball.radius && py + width > ball.y + ball.radius) {
    		
    		// Check only the correct side. 
    		if (isLeft) {
            	if (px - length2 < ball.x + ball.radius && px > ball.x - ball.radius) {
    				ball.vy = -ball.vy;
    				ball.update();
    				return true;
    			}
    		} else {
    			if (px < ball.x + ball.radius && px + length2 > ball.x - ball.radius) {
    				ball.vy = -ball.vy;
    				ball.update();
    				return true;
    			}
    		}
    		
    	}
    	
        // There is no collision. 
        return false;
        
    }
    
    void draw(Graphics2D g2) {
    	
    	g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    	
    	g2.setColor(Color.GRAY);
    	
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
        g2.setStroke(new BasicStroke(5));
    	g2.draw(new Ellipse2D.Double(px - joinRadius, py - joinRadius, joinRadius * 2, joinRadius * 2));
        
    }
    
}
