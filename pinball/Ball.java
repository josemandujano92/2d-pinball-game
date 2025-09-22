package pinball;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

class Ball {
	
	double x, y, radius;
    double vx, vy;
    
    Ball(double startX, double startY, double ballRadius) {
    	
        // Ball center and size. 
    	x = startX;
        y = startY;
        radius = ballRadius;
        
        // Random movement when spawning. 
        vx = Math.random() * 6.0 - 3.0;
        vy = Math.random() * 6.0 - 3.0;
        
    }
    
    void update() {
    	
    	// Gravity
    	vy += 0.2;
    	
    	// Limit the velocity to keep the game playable. 
    	if (Math.abs(vx) > 9) vx = Math.signum(vx) * 9;
    	if (Math.abs(vy) > 9) vy = Math.signum(vy) * 9;
    	
    	// Move the ball. 
    	x += vx;
        y += vy;
        
    }
    
    // Check collisions with walls. 
    void checkCollisions() {
    	
    	if (x - radius < 0 || x + radius > GamePanel.WIDTH) {
    		vx = -vx;
    		update();
    	}
    	
    	if (y - radius < 0) vy = -vy;
    	
    }
    
    void draw(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.fill(new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2));
    }
    
}
