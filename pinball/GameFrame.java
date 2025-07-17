package pinball;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class GameFrame extends JFrame {
	
	public GameFrame() {
	    setTitle("      <--- Pinball Game --->      [press (P) to pause/resume]");
	    setSize(615, 800); // (600, 800)
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    add(new GamePanel());
	    setLocationRelativeTo(null);
	    setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() { // Execute run() on the event dispatching thread. 
            public void run() {
            	new GameFrame();
            }
        });
	}

}

