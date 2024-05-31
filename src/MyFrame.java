import javax.swing.JFrame;

public class MyFrame extends JFrame{
	private static final long serialVersionUID = -2122161377842820073L;
    
    MyFrame() { 
        MyPanel myPanel = new MyPanel();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.add(myPanel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
