package pinball;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

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
		}
    	
    }
    
    void draw(Graphics2D g2) {
    	
    	// Exit
    	
    	g2.setStroke(new BasicStroke(1));
    	
    	g2.setColor(Color.GREEN);
    	
    	g2.drawOval((int) exitX - 10, (int) exitY - 10, 20, 20);
    	
    	// Entry
    	
    	g2.fillOval((int) (entryX - radius), (int) (entryY - radius), (int) radius * 2, (int) radius * 2);
    	
    	g2.setColor(Color.BLUE);
    	
    	g2.fillOval((int) (entryX - 0.8 * radius), (int) (entryY - 0.8 * radius), (int) (radius * 1.6), (int) (radius * 1.6));
    	
    }
    
}
