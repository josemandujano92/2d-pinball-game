package pinball;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

import java.util.ArrayList;

class GamePanel extends JPanel implements Runnable{
	
	// Game configuration
	private final int WIDTH = 550;
	private final int HEIGHT = 750;
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
    private Line2D.Double lava = new Line2D.Double(0, 745, WIDTH, 745);
    
    // Game objects
    private Ball ball;
    private double startX = WIDTH / 2;
    private double startY = HEIGHT / 6;
    private double ballRadius = 10;
    private ArrayList<Bumper> bumpers = new ArrayList<Bumper>();
    private Slingshot slingLeft, slingRight;
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
        bumpers.add(new Bumper(270, 280, 25));
        bumpers.add(new Bumper(180, 190, 15));
        bumpers.add(new Bumper(365, 135, 20));
        slingLeft = new Slingshot(70, 500, 100, 580);
        slingRight = new Slingshot(450, 510, 480, 440);
        flipperLeft = new Flipper(210, 650, true);
        flipperRight = new Flipper(340, 650, false);
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
		
		// If the game is neither paused nor over, then move the ball and check collisions. 
		if (!paused && !gameOver) {
			
	        ball.update();
	        
	        // Check collisions with walls. 
	        ball.checkCollisions();
	        
	        for (Bumper b : bumpers) {
	        	if (b.checkCollision(ball)) score++; // Count collisions with bumpers. 
	        }
	        
	        slingLeft.checkCollision(ball);
	        slingRight.checkCollision(ball);
	        
	        // If there is contact with the flippers skip their updates (to avoid clipping) and continue the game. 
	        
	        if (flipperLeft.checkCollision(ball) || flipperRight.checkCollision(ball)) return;
	        
	        flipperLeft.update();
	        flipperRight.update();
	        
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
		  
		  slingLeft.draw(g2);
		  slingRight.draw(g2);
		  
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
