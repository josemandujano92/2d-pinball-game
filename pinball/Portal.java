package pinball;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

class Portal {
	
	private int entryX, entryY, entryRadius; // Location and size of the entry. 
	private int exitX, exitY, exitRadius; // Location and size of the exit. 
	private boolean active;
	private BasicStroke thin = new BasicStroke(2);
	private BasicStroke thick = new BasicStroke(8);
	
    Portal(int entryX, int entryY, int entryRadius, int exitX, int exitY, int exitRadius) {
    	
    	this.entryX = entryX;
        this.entryY = entryY;
        this.entryRadius = entryRadius;
        
        this.exitX = exitX;
        this.exitY = exitY;
        this.exitRadius = exitRadius;
        
    }
    
    void checkOverlap(Ball ball) {
    	
    	// Vector from entry center to ball center. 
    	Vector2D centersVector = new Vector2D(ball.x - entryX, ball.y - entryY);
    	
    	// If there is overlapping, then transport the ball to the exit. 
    	if (centersVector.length() < entryRadius) {
    		
    		active = true;
    		
    		ball.x = exitX;
    		ball.y = exitY;
    		
    		// Slow down the ball after transportation. 
    		ball.vx *= 0.5;
    		ball.vy *= 0.5;
    		
		} else {
			
			active = false;
			
		}
    	
    }
    
    void draw(Graphics2D g2) {
    	
    	// Exit
    	
    	g2.setColor(Color.GREEN);
    	
    	g2.setStroke(thin);
    	
    	g2.drawOval(exitX - exitRadius, exitY - exitRadius, 2 * exitRadius, 2 * exitRadius);
    	
    	// Entry
    	
    	g2.setStroke(thick);
    	
    	g2.drawOval(entryX - entryRadius, entryY - entryRadius, 2 * entryRadius, 2 * entryRadius);
    	
    	if (!active) g2.setColor(Color.BLUE);
    	
    	g2.fillOval(entryX - entryRadius, entryY - entryRadius, 2 * entryRadius, 2 * entryRadius);
    	
    }
    
}
