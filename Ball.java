package pinball;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Ball {
	
	double x, y, radius;
    double vx, vy;

    public Ball(double x, double y, double radius) {
    	
        this.x = x;
        this.y = y;
        this.radius = radius;
        
        // random movement
        vx = Math.random() * 6.0 - 3.0;
        vy = Math.random() * 6.0 - 3.0;
        
    }

    public void update() {
    	
    	vy += 0.2; // gravity
    	
    	// Move ball and check for collisions. 
    	
    	x += vx;
        y += vy;
        
        if (x - radius < 0 || x + radius > 600) vx = -vx;
        if (y - radius < 0) vy = -vy;
        
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.fill(new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2));
    }

}

