package pinball;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

class Portal {
	
	private double entryX, entryY; // entry coordinates
	private double exitX, exitY; // exit coordinates
	private double radius;
	
    Portal(double entryX, double entryY, double exitX, double exitY, double radius) {
    	this.entryX = entryX;
        this.entryY = entryY;
        this.exitX = exitX;
        this.exitY = exitY;
        this.radius = radius;
    }
    
    void checkOverlap(Ball ball) {
    	
    	// Vector from entry center to ball center. 
    	Vector2D centersVector = new Vector2D(ball.x - entryX, ball.y - entryY);
    	
    	// If there is overlapping, then transport the ball to the exit center. 
    	if (centersVector.length() < radius) {
    		
    		ball.x = exitX;
    		ball.y = exitY;
    		
    		// Slow ball down after transportation. 
    		ball.vx *= 0.5;
    		ball.vy *= 0.5;
    		
		}
    	
    }
    
    void draw(Graphics2D g2) {
    	
    	// Exit
    	
    	g2.setStroke(new BasicStroke(2));
    	
    	g2.setColor(Color.GREEN);
    	
    	g2.draw(new Ellipse2D.Double(exitX - 10, exitY - 10, 20, 20));
    	
    	// Entry
    	
    	g2.fill(new Ellipse2D.Double(entryX - radius, entryY - radius, radius * 2, radius * 2));
    	
    	g2.setColor(Color.BLUE);
    	
    	g2.fill(new Ellipse2D.Double(entryX - 0.8 * radius, entryY - 0.8 * radius, radius * 1.6, radius * 1.6));
    	
    }
    
}
