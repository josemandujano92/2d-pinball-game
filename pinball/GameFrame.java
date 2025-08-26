package pinball;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

class GameFrame extends JFrame {
	
	private GameFrame() {
	    setTitle("<--- Pinball Game --->   [press (P) to pause/resume]");
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    add(new GamePanel());
	    pack();
	    setLocationRelativeTo(null);
	    setVisible(true);
	}
	
	public static void main(String[] args) {
		
		//System.out.println(Thread.currentThread());
		
		// Execute run() on the event dispatching thread. 
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
    			//System.out.println(Thread.currentThread());
            	new GameFrame();
            }
        });
		
	}
	
}
