package pinball;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

class Flipper {
	
	private int px, py; // pivot coordinates
    private boolean isLeft; // determines direction of rotation
    private boolean pressed;
    private double angle;
    private double angleSpeed = Math.toRadians(5);
    private int joinRadius = 15; // radius of the join area
    private int length1 = 60;
    private int length2 = 90;
    private float strokeWidth = 15;
    private float strokeWidthHalf = strokeWidth / 2;
    private BasicStroke thick = new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private BasicStroke thin = new BasicStroke(strokeWidth / 5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    
    Flipper(int pivotX, int pivotY, boolean isLeftFlipper) {
    	
        px = pivotX;
        py = pivotY;
        isLeft = isLeftFlipper; // or right flipper
        
        // Initialize angle depending on side. 
        if (isLeft) {
        	angle = Math.toRadians(45);
		} else {
			angle = Math.toRadians(135);
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
    	
    	if (isLeft) {
    		
    		if (pressed) {
    			
    			// If the ball is near the flippers do additional collision checks. 
    			if (ball.y + ball.radius > py - strokeWidthHalf) {
    				
    				for (int i = 1; i <= 5; i++) {
    					
    					angle -= angleSpeed / 5;
    					angle = Math.max(angle, Math.toRadians(0));
    					
    					if (checkCollision1(ball)) return;
    					
    				}
    				
    			} else {
    				
    				angle -= angleSpeed;
    				angle = Math.max(angle, Math.toRadians(0));
    				
    			}
    			
        		px += 3;
        		px = Math.min(px, length2 + 75);
        		
    		} else { // arrow key is not pressed
    			
    			angle += angleSpeed;
    			angle = Math.min(angle, Math.toRadians(45));
    			px -= 3;
    			px = Math.max(px, length2 + 25);
    			
    		}
    		
		} else { // is right flipper
			
			if (pressed) {
				
				// If the ball is near the flippers do additional collision checks. 
				if (ball.y + ball.radius > py - strokeWidthHalf) {
					
					for (int i = 1; i <= 5; i++) {
						
    					angle += angleSpeed / 5;
    					angle = Math.min(angle, Math.toRadians(180));
    					
    					if (checkCollision1(ball)) return;
    					
    				}
					
    			} else {
    				
    				angle += angleSpeed;
    				angle = Math.min(angle, Math.toRadians(180));
    				
    			}
				
        		px -= 3;
    			px = Math.max(px, GamePanel.WIDTH - length2 - 75);
    			
    		} else { // arrow key is not pressed
    			
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
    	
    	// Vector from pivot (center of join area) to center of ball. 
    	Vector2D referenceVector = new Vector2D(ball.x - px, ball.y - py);
    	
    	// If there is contact, then adjust the direction of the ball. 
    	if (referenceVector.length() <= joinRadius + ball.radius) {
    		
    		Vector2D movementVector = new Vector2D(-ball.vx, -ball.vy);
    		movementVector = movementVector.reflect(referenceVector);
    		
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
    	Vector2D referenceVector = new Vector2D(ball.x - px, ball.y - py);
    	
    	// flipper (rotating part)
    	Vector2D flipper = new Vector2D(Math.cos(angle) * length1, Math.sin(angle) * length1);
    	
    	// Factor from the projection of the ball center onto the flipper line. 
    	double pf = referenceVector.dot(flipper) / flipper.dot(flipper);
    	
    	// If projected point does not lie on flipper itself then return "no collision". 
    	if (pf < 0 || 1 < pf) return false;
    	
    	// Vector to determine the distance between ball center and flipper. 
    	referenceVector = referenceVector.subtract(flipper.scale(pf));
    	
    	// collision check
    	if (referenceVector.length() - ball.radius > strokeWidthHalf) return false;
    	
    	// There is a collision. Reflect the ball. 
    	
    	Vector2D movementVector = new Vector2D(-ball.vx, -ball.vy);
		movementVector = movementVector.reflect(referenceVector);
		
		ball.vx = movementVector.x;
		ball.vy = movementVector.y;
		
		// Move the ball away to avoid clipping. 
		movementVector = movementVector.normalize().scale(ball.radius);
		ball.x += movementVector.x;
		ball.y += movementVector.y;
		
		ball.update();
		
		return true;
        
    }
    
    // Check collision with sliding part. 
    private boolean checkCollision2(Ball ball) {
    	
    	// Check if the ball is in the vicinity of the flipper. 
    	if (py - strokeWidthHalf < ball.y + ball.radius && py + strokeWidthHalf > ball.y + ball.radius) {
    		
    		// Check only the correct side. 
    		if (isLeft) {
    			
            	if (px - length2 <= ball.x && px >= ball.x) {
            		
            		// Reflect the ball. 
            		ball.vy = -ball.vy;
    				ball.y -= ball.radius / 2;
    				
    				return true;
    				
    			}
            	
    		} else { // is right
    			
    			if (px <= ball.x && px + length2 >= ball.x) {
    				
    				// Reflect the ball. 
    				ball.vy = -ball.vy;
    				ball.y -= ball.radius / 2;
    				
    				return true;
    				
    			}
    			
    		}
    		
    	}
    	
        // There is no collision. 
        return false;
        
    }
    
    void draw(Graphics2D g2) {
    	
    	// rotating part
    	
    	g2.setColor(Color.GRAY.darker());
    	g2.setStroke(thick);
    	drawRotatingPart(g2);
    	
    	g2.setColor(Color.DARK_GRAY.darker());
        g2.setStroke(thin);
    	drawRotatingPart(g2);
    	
    	// sliding part
    	
    	g2.setColor(Color.GRAY);
    	g2.setStroke(thick);
    	drawSlidingPart(g2);
    	g2.fillOval(px - joinRadius, py - joinRadius, 2 * joinRadius, 2 * joinRadius);
    	
    	g2.setColor(Color.DARK_GRAY);
        g2.setStroke(thin);
        drawSlidingPart(g2);
        g2.fillOval((int) (px - 0.6 * joinRadius), (int) (py - 0.6 * joinRadius), (int) (1.2 * joinRadius), (int) (1.2 * joinRadius));
        
        // The pin of the "hinge". 
        g2.setColor(Color.WHITE);
        g2.fillOval((int) (px - 0.4 * joinRadius), (int) (py - 0.4 * joinRadius), (int) (0.8 * joinRadius), (int) (0.8 * joinRadius));
        
    }
    
    private void drawRotatingPart(Graphics2D g2) {
    	
    	// The tip of the flipper. 
    	double tx = px + Math.cos(angle) * (length1 - strokeWidthHalf);
    	double ty = py + Math.sin(angle) * (length1 - strokeWidthHalf);
    	
    	g2.drawLine(px, py, (int) tx, (int) ty);
    	
    }
    
    private void drawSlidingPart(Graphics2D g2) {
    	
    	// Draw only the correct side. 
    	if (isLeft) {
    		g2.drawLine(px, py, (int) (px - (length2 - strokeWidthHalf)), py);
		} else {
			g2.drawLine(px, py, (int) (px + (length2 - strokeWidthHalf)), py);
		}
    	
    }
    
}
