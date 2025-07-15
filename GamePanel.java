package pinball;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import java.util.List;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable{
	
	private Thread gameThread;
    private double nsPerFrame = 1_000_000_000.0 / 60.0; // A frame frequency of 60/s yields 16666666.7 nanoseconds per frame. 
    private long lastTime;
    private long now;
	
    private boolean running = false;
    private boolean paused = false;
    private boolean gameOver = false;
    private int score = 0;
    
    private Ball ball;
    private double startX = 300, startY = 100; // Starting position of the ball. 
    private List<Bumper> bumpers = new ArrayList<>();
    private Slingshot slingLeft, slingRight;
    private Flipper flipperLeft, flipperRight;

	public GamePanel() {
		setBackground(Color.BLACK);
        setFocusable(true);
        initObjects();
        initInput();
        startGame();
	}
	
	private void initObjects() {
        ball = new Ball(startX, startY, 10);
        bumpers.add(new Bumper(200, 200, 15));
        bumpers.add(new Bumper(400, 160, 20));
        bumpers.add(new Bumper(280, 280, 25));
        slingLeft = new Slingshot(25, 400, 60, 480);
        slingRight = new Slingshot(540, 430, 575, 350);
        flipperLeft = new Flipper(200, 650, true);
        flipperRight = new Flipper(400, 650, false);
    }
	
	// Input Handling 
	private void initInput() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
            	switch (e.getKeyCode()) {
	                case KeyEvent.VK_P:
	                    paused = !paused;
	                    break;
	                case KeyEvent.VK_R:
	                    if (gameOver) resetGame();
	                    break;
	                case KeyEvent.VK_LEFT:
	                    flipperLeft.press();
	                    break;
	                case KeyEvent.VK_RIGHT:
	                    flipperRight.press();
	                    break;
            	}
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT)  flipperLeft.release();
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) flipperRight.release();
            }
        });
    }
	
	// Start game thread. 
	private void startGame() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

	// Game Loop: Every frame update object positions, handle collisions and repaint panel. 
	@Override
	public void run() {
		
        lastTime = System.nanoTime();
        
        while (running) {
			now = System.nanoTime();
			if (now - lastTime >= nsPerFrame) {
				updateGame();
				repaint();
				lastTime = now;
			}
		}
        
	}
	
	private void updateGame() {
		
		// Skip updates if paused or game over. 
	    if (paused || gameOver) return;
		
	    // Move ball and check for collisions. 
	    
        ball.update();
        
        for (Bumper b : bumpers) {
        	if (b.checkCollision(ball)) score += 1; // Count collisions with bumpers. 
        }
        
        slingLeft.checkCollision(ball);
        slingRight.checkCollision(ball);
        
        if (!flipperLeft.checkCollision(ball)) {
        	flipperLeft.update();
		}
        
        if (!flipperRight.checkCollision(ball)) {
        	flipperRight.update();
		}
        
        // Check if ball burns. 
        if (ball.y + ball.radius > 800) gameOver = true;
        
    }
	
	private void resetGame() {
		paused = false;
		gameOver = false;
	    score = 0;
	    ball = new Ball(startX, startY, ball.radius);
	}
	
	// Rendering
	@Override
	protected void paintComponent(Graphics g) {
		  
		  super.paintComponent(g);
		  Graphics2D g2 = (Graphics2D) g;
		  
		  // Antialiasing for smoother lines. 
		  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		  
		  // Draw the score. 
	      g2.setColor(Color.WHITE);
	      g2.setFont(new Font("Monospaced", Font.BOLD, 25));
	      g2.drawString("Score: " + score, 5, 20);
		  
		  // Draw game objects. 
	      
		  ball.draw(g2);
		  
		  for (Bumper b : bumpers) {
	          b.draw(g2);
	      }
		  
		  slingLeft.draw(g2);
		  slingRight.draw(g2);
		  
	      flipperLeft.draw(g2);
	      flipperRight.draw(g2);
	      
	      // Draw lava at the bottom. 
		  g2.setStroke(new BasicStroke(10));
		  g2.setColor(Color.RED);
		  g2.draw(new Line2D.Double(0, 760, 600, 760));

	      // Overlays 
	      
	      if (paused && !gameOver) {
	    	  
	    	  g2.setColor(Color.CYAN);
	    	  
	    	  FontMetrics fm = g2.getFontMetrics();
	    	  
	    	  g2.drawString("Game", (getWidth() - fm.stringWidth("Game"))/2, getHeight()/2 - 5);
	    	  g2.drawString("Paused", (getWidth() - fm.stringWidth("Paused"))/2, getHeight()/2 + fm.getAscent() + 5);
	    	  
	      }

	      if (gameOver) {
	    	  
	    	  g2.setColor(Color.CYAN);
	    	  
	    	  FontMetrics fm = g2.getFontMetrics();
	    	  
	    	  g2.drawString("< GAME OVER >", (getWidth() - fm.stringWidth("< GAME OVER >"))/2, getHeight()/2 - 10);
	    	  g2.drawString("press (R) to restart", (getWidth() - fm.stringWidth("press (R) to restart"))/2, getHeight()/2 + fm.getAscent() + 10);
	    	  
	      }

	}

}

