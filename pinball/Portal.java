package pinball;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

class Portal {
	
	private double entryX, entryY, entryRadius; // Location and size of the entry. 
	private double exitX, exitY; // Location of the exit. 
	private Ellipse2D.Double outerPartOfEntry, innerPartOfEntry;
	private Ellipse2D.Double exit;
	private BasicStroke stroke = new BasicStroke(2);
	
    Portal(double entryX, double entryY, double entryRadius, double exitX, double exitY, double exitRadius) {
    	
    	this.entryX = entryX;
        this.entryY = entryY;
        this.entryRadius = entryRadius;
        
        // for rendering
        this.outerPartOfEntry = new Ellipse2D.Double(entryX - entryRadius, entryY - entryRadius, entryRadius * 2, entryRadius * 2);
        this.innerPartOfEntry = new Ellipse2D.Double(entryX - 0.8 * entryRadius, entryY - 0.8 * entryRadius, entryRadius * 1.6, entryRadius * 1.6);
        
        this.exitX = exitX;
        this.exitY = exitY;
        
        // for rendering
        this.exit = new Ellipse2D.Double(exitX - exitRadius, exitY - exitRadius, exitRadius * 2, exitRadius * 2);
        
    }
    
    void checkOverlap(Ball ball) {
    	
    	// Vector from entry center to ball center. 
    	Vector2D centersVector = new Vector2D(ball.x - entryX, ball.y - entryY);
    	
    	// If there is overlapping, then transport the ball to the exit. 
    	if (centersVector.length() < entryRadius) {
    		
    		ball.x = exitX;
    		ball.y = exitY;
    		
    		// Slow down the ball after transportation. 
    		ball.vx *= 0.5;
    		ball.vy *= 0.5;
    		
		}
    	
    }
    
    void draw(Graphics2D g2) {
    	
    	// Exit
    	
    	g2.setStroke(stroke);
    	
    	g2.setColor(Color.GREEN);
    	
    	g2.draw(exit);
    	
    	// Entry
    	
    	g2.fill(outerPartOfEntry);
    	
    	g2.setColor(Color.BLUE);
    	
    	g2.fill(innerPartOfEntry);
    	
    }
    
}
