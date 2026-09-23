package pinball;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

import java.util.ArrayList;

class GamePanel extends JPanel implements Runnable, KeyListener {
	
	// Game configuration (panel size, nanoseconds per frame, etc.)
	static final int WIDTH = 480;
	static final int HEIGHT = 650;
	private Thread gameThread;
	private boolean running = false;
    private boolean paused = false;
    private boolean gameOver = false;
    private double nsPerFrame = 1_000_000_000.0 / 60.0; // A frame frequency of 60/s yields 16666666.7 nanoseconds per frame. 
    private long lastTime;
    private long now;
    private Font scoreFont = new Font("Dialog", Font.BOLD, 25);
    private int score = 0;
    private int numOfStars = 50;
    private int[] x, y;
    private GradientPaint gpBlue = new GradientPaint(0, 0, Color.BLUE.darker(), 0, HEIGHT - 30, Color.BLACK); // Gradient for the sky. 
    private GradientPaint gpRed = new GradientPaint(0, HEIGHT - 30, Color.BLACK, 0, HEIGHT, Color.RED); // Gradient for the bottom. 
    private BasicStroke basicStroke = new BasicStroke(15);
    
    // Game objects
    
    private Ball ball;
    private double startX = WIDTH / 2;
    private double startY = HEIGHT / 7;
    private double ballRadius = 10;
    
    private ArrayList<Bumper> bumpers = new ArrayList<Bumper>();
    
    private Portal portal;
    
    private Arrow arrowLeft, arrowRight;
    
    private Slingshot slingLeft;
    private double[] slVertex1 = {0.2 * WIDTH - 25, 0.5 * HEIGHT + 10};
    private double[] slVertex2 = {0.2 * WIDTH + 5, 0.55 * HEIGHT + 20};
    private double[] slVertex3 = {0.2 * WIDTH - 5, 0.55 * HEIGHT + 20};
    private double[] slVertex4 = {0.2 * WIDTH + 25, 0.6 * HEIGHT + 30};
    
    private Slingshot slingRight;
    private double[] srVertex1 = {0.8 * WIDTH + 25, 0.5 * HEIGHT - 10};
    private double[] srVertex2 = {0.8 * WIDTH - 5, 0.55 * HEIGHT + 10};
    private double[] srVertex3 = {0.8 * WIDTH + 5, 0.55 * HEIGHT + 10};
    private double[] srVertex4 = {0.8 * WIDTH - 25, 0.6 * HEIGHT + 30};
    
    private Flipper flipperLeft, flipperRight;
    
    // The panel constructor is called by the run method of the frame. 
	GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.BLACK);
        setFocusable(true);
        gameThread = new Thread(this);
        stars();
        initObjects();
        addKeyListener(this);
	}
	
	// Preparations
	
	private void stars() {
		
		x = new int[numOfStars];
		y = new int[numOfStars];
		
		// Create stars on random locations. 
		for (int i = 0; i < numOfStars; i++) {
			x[i] = (int) (Math.random() * WIDTH);
			y[i] = (int) (Math.random() * HEIGHT);
	    }
		
	}
	
	private void initObjects() {
		
        ball = new Ball(startX, startY, ballRadius);
        
        // Add bumpers to the list. 
        bumpers.add(new Bumper((int) (0.35 * WIDTH), HEIGHT / 4, 15));
        bumpers.add(new Bumper((int) (0.65 * WIDTH), HEIGHT / 5, 20));
        bumpers.add(new Bumper(WIDTH / 2 - 5, HEIGHT / 3 + 5, 25));
        
        // Portal that transports the ball to the top. 
        portal = new Portal(WIDTH / 2, HEIGHT / 2 + 55, (int) (2 * ballRadius), WIDTH / 4, HEIGHT / 8, (int) ballRadius);
        
        // One arrow on each side pointing upwards. 
        arrowLeft = new Arrow(120, 290, 145, 255);
        arrowRight = new Arrow(WIDTH - 120, 250, WIDTH - 145, 215);
        
        // The slingshot on the left. 
        slingLeft = new Slingshot(slVertex1, slVertex2, slVertex3, slVertex4);
        
        // The slingshot on the right. 
        slingRight = new Slingshot(srVertex1, srVertex2, srVertex3, srVertex4);
        
        flipperLeft = new Flipper(145, HEIGHT - 100, true);
        flipperRight = new Flipper(WIDTH - 145, HEIGHT - 100, false);
        
    }
	
	// Input handling
	
	@Override
    public void keyPressed(KeyEvent e) {
    	
    	switch (e.getKeyCode()) {
            case KeyEvent.VK_P:
            	if (running) paused = !paused;
                break;
            case KeyEvent.VK_LEFT: flipperLeft.press(); break;
            case KeyEvent.VK_RIGHT: flipperRight.press(); break;
    	}
    	
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
    	
    	switch (e.getKeyCode()) {
	    	case KeyEvent.VK_S: // The title overlay appears until the thread is started. 
				if (!running) gameThread.start();
				break;
	    	case KeyEvent.VK_R: // reset the game
                if (gameOver) {
                	paused = false;
            		gameOver = false;
            	    score = 0;
            	    ball = new Ball(startX, startY, ballRadius);
                }
                break;
	    	case KeyEvent.VK_LEFT: flipperLeft.release(); break;
	    	case KeyEvent.VK_RIGHT: flipperRight.release(); break;
    	}
        
    }
    
    @Override
    public void keyTyped(KeyEvent e) { }
	
    // Execute this run method on the game thread! 
	@Override
	public void run() {
		
		//System.out.println(Thread.currentThread());
		
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
	        
	        if (ball.y - ball.radius > 0.75 * HEIGHT) {
	        	
	        	// If there is contact with the flippers skip unnecessary collision checks. 
		        if (flipperLeft.checkCollision(ball) || flipperRight.checkCollision(ball)) return;
		        
		        // Move the flippers. 
		        flipperLeft.update(ball);
		        flipperRight.update(ball);
		        
		        // Check collisions with the flippers again to improve collision detection. 
		        if (flipperLeft.checkCollision(ball) || flipperRight.checkCollision(ball)) return;
		        
		        // Check if the ball burns. 
		        if (ball.y + ball.radius > HEIGHT) gameOver = true;
	        	
	        } else {
	        	
	        	// Move the flippers. 
		        flipperLeft.update(ball);
		        flipperRight.update(ball);
	        	
	        	if (ball.x < 0.5 * WIDTH) {
	        		
	        		// Check collisions with the left slingshot. 
	        		if (slingLeft.checkCollision(ball)) return;
	        		
	        		// Check if the ball touches the left arrow. 
	    	        arrowLeft.checkOverlap(ball);
	        		
	        	} else {
	        		
	        		// Check collisions with the right slingshot. 
	        		if (slingRight.checkCollision(ball)) return;
	    	        
	    	        // Check if the ball touches the right arrow. 
	    	        arrowRight.checkOverlap(ball);
	        		
	        	}
	        	
	        	// Count collisions with bumpers. 
	        	for (Bumper b : bumpers) {
		        	if (b.checkCollision(ball)) score++;
		        }
	        	
	        	// Check if the ball entered the portal. 
		        portal.checkOverlap(ball);
	        	
	        }
	        
		}
		
    }
	
	// Rendering
	@Override
	protected void paintComponent(Graphics g) {
		  
		  super.paintComponent(g);
		  Graphics2D g2 = (Graphics2D) g;
		  
		  // sky with stars in the background
		  
		  g2.setPaint(gpBlue);
		  g2.fillRect(0, 0, WIDTH, HEIGHT - 30);
		  
		  g2.setColor(Color.WHITE);
		  for (int i = 0; i < numOfStars; i++) {
			  g2.fillOval(x[i], y[i], 3, 3);
		  }
		  
		  // glow of lava planet
		  g2.setPaint(gpRed);
		  g2.fillRect(0, HEIGHT - 30, WIDTH, 30);
		  
		  // Antialiasing for smoother lines. 
		  g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		  
		  // Draw the score. 
	      g2.setColor(Color.WHITE);
	      g2.setFont(scoreFont);
	      g2.drawString("Score: " + score, 5, 25);
		  
		  // Draw the game objects. 
	      
		  ball.draw(g2);
		  
		  for (Bumper b : bumpers) {
	          b.draw(g2);
	      }
		  
		  arrowLeft.draw(g2);
		  arrowRight.draw(g2);
		  
		  portal.draw(g2);
		  
		  slingLeft.draw(g2);
		  slingRight.draw(g2);
		  
	      flipperLeft.draw(g2);
	      flipperRight.draw(g2);
	      
		  // Draw lava at the bottom. 
		  g2.setStroke(basicStroke);
		  g2.setColor(Color.RED);
		  g2.drawLine(0, HEIGHT - 5, WIDTH, HEIGHT - 5);
		  
	      // Overlays
		  
		  g2.setColor(Color.CYAN);
		  
		  if (!running) drawTitle(g2);
	      
	      if (paused && !gameOver) drawPaused(g2);
	      
	      if (gameOver) drawGameOver(g2);
	      
	}
	
	// Method to draw the title overlay with instructions. 
    private void drawTitle(Graphics2D g2) {
        
        g2.setFont(new Font("Dialog", Font.BOLD, 45));
        
        String title = "Pinball Game";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        
        g2.drawString(title, (WIDTH - titleWidth) / 2, HEIGHT / 4 + 40);
        
        g2.setFont(new Font("Dialog", Font.BOLD, 25));
        
        String prompt = "press (S) to start";
        int promptWidth = g2.getFontMetrics().stringWidth(prompt);
        
        g2.drawString(prompt, (WIDTH - promptWidth) / 2, HEIGHT / 2 + 20);
        
        g2.setFont(new Font("Dialog", Font.BOLD, 20));
        FontMetrics fm = g2.getFontMetrics();
        
        String instructionL1 = "use the left/right arrow key";
        String instructionL2 = "to move the left/right flipper";
        
        g2.drawString(instructionL1, (WIDTH - fm.stringWidth(instructionL1)) / 2, (int) (0.75 * HEIGHT) - 5);
        g2.drawString(instructionL2, (WIDTH - fm.stringWidth(instructionL2)) / 2, (int) (0.75 * HEIGHT) + fm.getAscent() + 5);
        
    }
    
    // Method for the pauses. 
    private void drawPaused(Graphics2D g2) {
    	
    	g2.setFont(new Font("Dialog", Font.BOLD, 30));
  	  	
  	  	String pausedStr = "Game Paused";
  	  	int pausedStrWidth = g2.getFontMetrics().stringWidth(pausedStr);
  	  	
  	  	g2.drawString(pausedStr, (WIDTH - pausedStrWidth) / 2, HEIGHT / 2);
    	
    }
    
    // Method for the game over state. 
    private void drawGameOver(Graphics2D g2) {
    	
    	g2.setFont(new Font("Dialog", Font.BOLD, 35));
  	  	
  	  	String gameOverStr = "Game Over";
  	  	int gameOverStrWidth = g2.getFontMetrics().stringWidth(gameOverStr);
  	  	
  	  	g2.drawString(gameOverStr, (WIDTH - gameOverStrWidth) / 2, HEIGHT / 2 - 20);
  	  	
  	  	g2.setFont(new Font("Dialog", Font.BOLD, 25));
        
        String prompt = "press (R) to restart";
        int promptWidth = g2.getFontMetrics().stringWidth(prompt);
  	  	
  	  	g2.drawString(prompt, (WIDTH - promptWidth) / 2, HEIGHT / 2 + 20);
    	
    }
    
}
