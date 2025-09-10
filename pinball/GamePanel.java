package pinball;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import java.util.ArrayList;

class GamePanel extends JPanel implements Runnable{
	
	// Game configuration
	static final int WIDTH = 480;
	static final int HEIGHT = 650;
	private Thread gameThread;
    private double nsPerFrame = 1_000_000_000.0 / 60.0; // A frame frequency of 60/s yields 16666666.7 nanoseconds per frame. 
    private boolean running = false;
    private long lastTime;
    private long now;
    private boolean paused = false;
    private boolean gameOver = false;
    private Font scoreFont = new Font("Monospaced", Font.BOLD, 25);
    private int score = 0;
    private BasicStroke basicStroke = new BasicStroke(15);
    private Line2D.Double lava = new Line2D.Double(0, HEIGHT - 5, WIDTH, HEIGHT - 5);
    
    // Game objects
    private Ball ball;
    private double startX = WIDTH / 2;
    private double startY = HEIGHT / 7;
    private double ballRadius = 10;
    private ArrayList<Bumper> bumpers = new ArrayList<Bumper>();
    private Portal portal;
    private Arrow arrowLeft, arrowRight;
    private Slingshot slingLeft, slingLeftExtension1, slingLeftExtension2;
    private Slingshot slingRight, slingRightExtension1, slingRightExtension2;
    private Flipper flipperLeft, flipperRight;
    
	GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.BLACK);
        setFocusable(true);
        initObjects();
        initInput();
        gameThread = new Thread(this);
        gameThread.start();
	}
	
	// Preparations
	private void initObjects() {
		
        ball = new Ball(startX, startY, ballRadius);
        
        // Add bumpers to the list. 
        bumpers.add(new Bumper(0.35 * WIDTH, HEIGHT / 4, 15));
        bumpers.add(new Bumper(0.65 * WIDTH, HEIGHT / 5, 20));
        bumpers.add(new Bumper(WIDTH / 2 - 5, HEIGHT / 3 + 5, 25));
        
        portal = new Portal(WIDTH / 2, HEIGHT / 2 + 55, WIDTH / 4, HEIGHT / 8, 20); // Transports the ball to the top. 
        
        // One arrow on each side pointing upwards. 
        arrowLeft = new Arrow(0.2 * WIDTH, HEIGHT / 2, 0.2 * WIDTH + 20, HEIGHT / 2 - 40);
        arrowRight = new Arrow(0.8 * WIDTH, HEIGHT / 2 - 60, 0.8 * WIDTH - 20, HEIGHT / 2 - 90);
        
        // Left slingshot
        slingLeft = new Slingshot(0.2 * WIDTH - 10, 0.65 * HEIGHT - 60, 0.2 * WIDTH + 20, 0.65 * HEIGHT + 20);
        slingLeftExtension1 = new Slingshot(0.2 * WIDTH - 10, 0.65 * HEIGHT - 60, 0.2 * WIDTH - 10, 0.65 * HEIGHT + 20);
        slingLeftExtension2 = new Slingshot(0.2 * WIDTH - 10, 0.65 * HEIGHT + 20, 0.2 * WIDTH + 20, 0.65 * HEIGHT + 20);
        
        // Right slingshot
        slingRight = new Slingshot(0.8 * WIDTH - 20, 0.65 * HEIGHT - 20, 0.8 * WIDTH + 10, 0.65 * HEIGHT - 90);
        slingRightExtension1 = new Slingshot(0.8 * WIDTH - 20, 0.65 * HEIGHT - 20, 0.8 * WIDTH + 10, 0.65 * HEIGHT - 20);
        slingRightExtension2 = new Slingshot(0.8 * WIDTH + 10, 0.65 * HEIGHT - 20, 0.8 * WIDTH + 10, 0.65 * HEIGHT - 90);
        
        // Flippers
        flipperLeft = new Flipper(0.3 * WIDTH, HEIGHT - 100, true);
        flipperRight = new Flipper(0.7 * WIDTH, HEIGHT - 100, false);
        
    }
	
	// Input handling
	private void initInput() {
		
        addKeyListener(new KeyAdapter() {
        	
            @Override
            public void keyPressed(KeyEvent e) {
            	switch (e.getKeyCode()) {
	                case KeyEvent.VK_P:
	                    paused = !paused;
	                    break;
	                case KeyEvent.VK_R: // reset the game
	                    if (gameOver) {
	                    	paused = false;
	                		gameOver = false;
	                	    score = 0;
	                	    ball = new Ball(startX, startY, ballRadius);
	                    }
	                    break;
	                case KeyEvent.VK_LEFT: flipperLeft.press(); break;
	                case KeyEvent.VK_RIGHT: flipperRight.press(); break;
            	}
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) flipperLeft.release();
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) flipperRight.release();
            }
            
        });
        
    }
	
	@Override
	public void run() {
		
		running = true;
		
        lastTime = System.nanoTime();
        
    	// Game Loop
        while (running) {
        	
			now = System.nanoTime();
			
			// Every frame call the update game method and repaint the panel. 
			if (now - lastTime >= nsPerFrame) {
				
				updateGame();
				repaint();
				
				lastTime = now;
				
			}
			
		}
        
	}
	
	private void updateGame() {
		
		// If the game is neither paused nor over, then update it. 
		if (!paused && !gameOver) {
			
			// Move the ball and check collisions with the walls. 
	        ball.update();
	        ball.checkCollisions();
	        
	        // If there is contact with the flippers skip unnecessary collision checks. 
	        if (flipperLeft.checkCollision(ball) || flipperRight.checkCollision(ball)) return;
	        
	        for (Bumper b : bumpers) {
	        	if (b.checkCollision(ball)) score++; // Count collisions with bumpers. 
	        }
	        
	        // Check if the ball touches the arrows. 
	        arrowLeft.checkOverlap(ball);
	        arrowRight.checkOverlap(ball);
	        
	        // Check if the ball entered the portal. 
	        portal.checkOverlap(ball);
	        
	        // Move the flippers. 
	        flipperLeft.update();
	        flipperRight.update();
	        
	        // Check collisions with flippers again to improve collision detection. 
	        if (flipperLeft.checkCollision(ball) || flipperRight.checkCollision(ball)) return;
	        
	        // Left slingshot
	        slingLeft.checkCollision(ball);
	        slingLeftExtension1.checkCollision(ball);
	        slingLeftExtension2.checkCollision(ball);
	        
	        // Right slingshot
	        slingRight.checkCollision(ball);
	        slingRightExtension1.checkCollision(ball);
	        slingRightExtension2.checkCollision(ball);
	        
	        // Check if the ball burns. 
	        if (ball.y + ball.radius > HEIGHT) gameOver = true;
	        
		}
		
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
	      g2.setFont(scoreFont);
	      g2.drawString("Score: " + score, 5, 20);
		  
		  // Draw game objects. 
	      
		  ball.draw(g2);
		  
		  for (Bumper b : bumpers) {
	          b.draw(g2);
	      }
		  
		  arrowLeft.draw(g2);
		  arrowRight.draw(g2);
		  
		  portal.draw(g2);
		  
		  slingLeft.draw(g2);
		  slingLeftExtension1.draw(g2);
		  slingLeftExtension2.draw(g2);
		  
		  slingRight.draw(g2);
		  slingRightExtension1.draw(g2);
		  slingRightExtension2.draw(g2);
		  
	      flipperLeft.draw(g2);
	      flipperRight.draw(g2);
	      
	      // Draw lava at the bottom. 
		  g2.setStroke(basicStroke);
		  g2.setColor(Color.RED);
		  g2.draw(lava);
		  
	      // Overlays
	      
	      if (paused && !gameOver) {
	    	  
	    	  g2.setColor(Color.CYAN);
	    	  g2.setFont(new Font("Monospaced", Font.BOLD, 30));
	    	  
	    	  FontMetrics fm = g2.getFontMetrics();
	    	  
	    	  g2.drawString("Game", (WIDTH - fm.stringWidth("Game")) / 2, HEIGHT / 2 - 5);
	    	  g2.drawString("Paused", (WIDTH - fm.stringWidth("Paused")) / 2, HEIGHT / 2 + fm.getAscent() + 5);
	    	  
	      }
	      
	      if (gameOver) {
	    	  
	    	  g2.setColor(Color.CYAN);
	    	  g2.setFont(new Font("Monospaced", Font.BOLD, 35));
	    	  
	    	  FontMetrics fm = g2.getFontMetrics();
	    	  
	    	  g2.drawString("< GAME OVER >", (WIDTH - fm.stringWidth("< GAME OVER >")) / 2, HEIGHT / 2 - 10);
	    	  g2.drawString("press (R) to restart", (WIDTH - fm.stringWidth("press (R) to restart")) / 2, HEIGHT / 2 + fm.getAscent() + 10);
	    	  
	      }
	      
	}
	
}
