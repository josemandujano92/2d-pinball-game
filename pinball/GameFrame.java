package pinball;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

class GameFrame extends JFrame {
	
	private GameFrame() {
	    setTitle("<--- Pinball Game --->   [press (P) to pause/resume]");
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    add(new GamePanel());
	    pack();
	    setLocationRelativeTo(null); // The window is placed in the center of the screen. 
	    setVisible(true);
	}
	
	public static void main(String[] args) {
		
		//System.out.println(Thread.currentThread());
		
		SwingUtilities.invokeLater(new Runnable() { // Execute run() on the event dispatching thread. 
            public void run() {
    			//System.out.println(Thread.currentThread());
            	new GameFrame();
            }
        });
		
	}
	
}
