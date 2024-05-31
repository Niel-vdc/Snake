import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class MyPanel extends JPanel implements KeyListener, MouseListener {
	private static final long serialVersionUID = 3426940946811133635L;

    private final int SCREEN_WIDTH = 1000;
    private final int SCREEN_HEIGHT = 800;

    private final int UNIT_SIZE = 40;

    private int startLength = 5;

    private int addLength = 3;

    private char direction = 'r';

    private int speed = 200;

    private int appleX;
    private int appleY;

    private int score;

    private int highScore;

    private boolean newHighScore = false;

    private String champion = "";

    private File highScoreFile;

    private File championFile;

    private FileWriter fileWriter;

    private Scanner fileReader;

    private LinkedList<int[]> coordinates = new LinkedList<int[]>();

    private int[] head;

    private Random random = new Random();

    private JLabel gameOverLabel;

    private JButton gameOverButton;

    private JLabel scoreLabel;

    private JLabel highScoreLabel;

    private JLabel championLabel;

    private Icon trophy;

    private Timer timer;

    private TimerTask task;


    MyPanel(){
        // Initial coordinates of snake
        int[] newCoordinate = new int[2];
        for (int i = 0; i < startLength; i++){
            newCoordinate[0] = (SCREEN_WIDTH/UNIT_SIZE)/2 * UNIT_SIZE;
            newCoordinate[1] = (SCREEN_HEIGHT/UNIT_SIZE)/2 * UNIT_SIZE;

            coordinates.add(coordinates.size(), newCoordinate);
        }

        newApple();

        scoreLabel = new JLabel("Score: " + String.valueOf(score));
        scoreLabel.setFont(new Font("Raleway", Font.PLAIN, 40));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setVerticalAlignment(JLabel.TOP);

        trophy = new ImageIcon("/Volumes/Macintosh HD 2/Users/nielvandercolff/Documents/Niel Code/Java/Snake/src/trophy.png");

        highScoreLabel = new JLabel(String.valueOf(highScore));
        highScoreLabel.setFont(new Font("Raleway", Font.PLAIN, 40));
        highScoreLabel.setForeground(Color.WHITE);
        highScoreLabel.setIcon(trophy);
        highScoreLabel.setBounds(0, 0, SCREEN_WIDTH - 10, SCREEN_HEIGHT);
        highScoreLabel.setHorizontalAlignment(JLabel.RIGHT);
        highScoreLabel.setVerticalAlignment(JLabel.TOP);

        championLabel = new JLabel();
        championLabel.setFont(new Font("Raleway", Font.PLAIN, 20));
        championLabel.setForeground(Color.WHITE);
        championLabel.setBounds(0, 60, SCREEN_WIDTH - 10, SCREEN_HEIGHT);
        championLabel.setHorizontalAlignment(JLabel.RIGHT);
        championLabel.setVerticalAlignment(JLabel.TOP);


        highScoreFile = new File("/Volumes/Macintosh HD 2/Users/nielvandercolff/Documents/Niel Code/Java/Snake/src/HighScore.txt");
        championFile = new File("/Volumes/Macintosh HD 2/Users/nielvandercolff/Documents/Niel Code/Java/Snake/src/Champion.txt");
        try {
            fileReader = new Scanner(highScoreFile);

            while (fileReader.hasNextLine()) {
                highScore = Integer.valueOf(fileReader.nextLine());
                highScoreLabel.setText(String.valueOf(highScore));
            }

            fileReader.close();

            fileReader = new Scanner(championFile);

            while (fileReader.hasNextLine()) {
                champion = fileReader.nextLine();
                championLabel.setText(champion);
            }
		} catch (IOException e) {
			e.printStackTrace();
		}

        gameOverLabel = new JLabel("Game Over");
        gameOverLabel.setFont(new Font("Raleway", Font.BOLD, 100));
        gameOverLabel.setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        gameOverLabel.setHorizontalAlignment(JLabel.CENTER);
        gameOverLabel.setVisible(false);

        gameOverButton = new JButton("Restart");
        gameOverButton.setFont(new Font("Raleway", Font.PLAIN, 30));
        gameOverButton.setBounds(SCREEN_WIDTH/2 - 100, SCREEN_HEIGHT/2 + 100, 200, 60);
        gameOverButton.setFocusable(false);
        gameOverButton.setOpaque(true);
        gameOverButton.setBorder(null);
        gameOverButton.setBackground(new Color(190, 180, 140));
        gameOverButton.addMouseListener(this);
        gameOverButton.setVisible(false);


        newTimer();

        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.addKeyListener(this);
        this.setFocusable(true);
        this.setLayout(null);

        this.add(scoreLabel);
        this.add(highScoreLabel);
        this.add(championLabel);
        this.add(gameOverLabel);
        this.add(gameOverButton);
        
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        // Draw grid
        g2d.setColor(new Color(30, 175, 80));
        for (int x = 0; x < SCREEN_WIDTH; x += UNIT_SIZE*2){
            for (int y = 0; y < SCREEN_HEIGHT; y += UNIT_SIZE*2){
                g2d.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
            }
        }
        for (int x = UNIT_SIZE; x < SCREEN_WIDTH; x += UNIT_SIZE*2){
            for (int y = UNIT_SIZE; y < SCREEN_HEIGHT; y += UNIT_SIZE*2){
                g2d.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
            }
        }
        g2d.setColor(new Color(50, 195, 100));
        for (int x = UNIT_SIZE; x < SCREEN_WIDTH; x += UNIT_SIZE*2){
            for (int y = 0; y < SCREEN_HEIGHT; y += UNIT_SIZE*2){
                g2d.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
            }
        }
        for (int x = 0; x < SCREEN_WIDTH; x += UNIT_SIZE*2){
            for (int y = UNIT_SIZE; y < SCREEN_HEIGHT; y += UNIT_SIZE*2){
                g2d.fillRect(x, y, UNIT_SIZE, UNIT_SIZE);
            }
        }

        // Draw snake
        g2d.setColor(new Color(30, 80, 200));
        for (int i = 0; i < coordinates.size(); i++){
            int[] block = coordinates.get(i);
            g2d.fillRect(block[0], block[1], UNIT_SIZE, UNIT_SIZE);
        }

        // Draw Apple
        g2d.setColor(new Color(200, 30, 80));
        g2d.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
    }

    public void newTimer() {
        timer = new Timer();
        task = new TimerTask(){
            public void run() {
                head = coordinates.getLast();
                coordinates.removeFirst();
                int[] newCoordinate = new int[2];

                switch (direction) {
                    case 'r':
                        newCoordinate[0] = head[0] + UNIT_SIZE;
                        newCoordinate[1] = head[1];
                        break;
                    case 'l':
                        newCoordinate[0] = head[0] - UNIT_SIZE;
                        newCoordinate[1] = head[1];
                        break;
                    case 'u':
                        newCoordinate[0] = head[0];
                        newCoordinate[1] = head[1] - UNIT_SIZE;
                        break;
                    case 'd':
                        newCoordinate[0] = head[0];
                        newCoordinate[1] = head[1] + UNIT_SIZE;
                        break;
                }

                coordinates.add(coordinates.size(), newCoordinate);
                checkCollision();
                checkApple();
                sidePortals();
                repaint();
            };
        };
        timer.scheduleAtFixedRate(task, 1000, speed);
        
    }
    // Create new apple coordinates
    public void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;

        for (int i = 0; i < coordinates.size()-1; i++) {
            int[] currentBlock = coordinates.get(i);
            if (appleX == currentBlock[0] && appleY == currentBlock[1]){
                newApple();
            }
        }
        
    }

    public void checkApple() {
        head = coordinates.getLast();
        if (head[0] == appleX && head[1] == appleY) {
            score += 2;
            scoreLabel.setText("Score: " + String.valueOf(score));

            if (score > highScore) {
                newHighScore = true;
                highScore = score;
                highScoreLabel.setText(String.valueOf(highScore));

                try {
                    fileWriter = new FileWriter(highScoreFile);
                    fileWriter.write(String.valueOf(highScore));
                    fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }

            newApple();
            repaint();

            for (int i = 0; i < addLength; i++){
                coordinates.addFirst(coordinates.getFirst());
            }
        }
    }

    public void checkCollision() {
        head = coordinates.getLast();
        for (int i = 0; i < coordinates.size()-1; i++) {
            int[] currentBlock = coordinates.get(i);
            if (head[0] == currentBlock[0] && head[1] == currentBlock[1]){
                gameOverLabel.setVisible(true);
                gameOverButton.setVisible(true);
                timer.cancel();
                timer.purge();
                task.cancel();
                
                if (newHighScore) {
                    String newChampion;
                    newChampion = JOptionPane.showInputDialog(null, "Enter a username", "player");

                    if (newChampion != null) {
                        if (newChampion.isBlank()) {
                            JOptionPane.showMessageDialog(null, "Please enter a valid username", "Invalid username", JOptionPane.ERROR_MESSAGE);
                            newChampion = JOptionPane.showInputDialog(null, "Enter a username", "player");
                        }
                        else {
                            champion = newChampion;
                            championLabel.setText(champion);
                        }
                    }
                    else {
                        championLabel.setText(champion);
                    }

                    // Write champion to file
                    try {
                        fileWriter = new FileWriter(championFile);
                        fileWriter.write(champion);
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                
            }
            
        }
    }

    public void sidePortals() {
        head = coordinates.getLast();

        if (head[0] > SCREEN_WIDTH - UNIT_SIZE){
            head[0] = 0;
        }
        if (head[0] < 0){
            head[0] = SCREEN_WIDTH;
        }
        if (head[1] > SCREEN_HEIGHT - UNIT_SIZE){
            head[1] = 0;
        }
        if (head[1] < 0){
            head[1] = SCREEN_HEIGHT;
        }
    }

	@Override
	public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            direction = 'r';
        }
		if (e.getKeyCode() == KeyEvent.VK_LEFT){
            direction = 'l';
        }
        if (e.getKeyCode() == KeyEvent.VK_UP){
            direction = 'u';
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN){
            direction = 'd';
        }
	}

	@Override
	public void mouseClicked(MouseEvent e) {
        
        if (e.getSource() == gameOverButton) {
            direction = 'r';
            
            coordinates.clear();

            int[] newCoordinate = new int[2];
            for (int i = 0; i < startLength; i++){
                newCoordinate[0] = SCREEN_WIDTH/2;
                newCoordinate[1] = SCREEN_WIDTH/2;
    
                coordinates.add(coordinates.size(), newCoordinate);
            }

            newApple();

            gameOverLabel.setVisible(false);
            gameOverButton.setVisible(false);

            score = 0;
            scoreLabel.setText("Score: " + String.valueOf(score));

            repaint();

            newTimer();
            
        }
		
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (e.getSource() == gameOverButton) {
            gameOverButton.setBackground(new Color(160, 150, 110));
        }
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
        
        if (e.getSource() == gameOverButton) {
            gameOverButton.setBackground(new Color(210, 200, 160));
        }
	}

	@Override
	public void mouseEntered(MouseEvent e) {

        if (e.getSource() == gameOverButton) {
            gameOverButton.setBackground(new Color(210, 200, 160));
        }
		
	}

	@Override
	public void mouseExited(MouseEvent e) {

		if (e.getSource() == gameOverButton) {
            gameOverButton.setBackground(new Color(190, 180, 140));
        }
    }
    

    @Override
	public void keyTyped(KeyEvent e) {
		// TODO keyTyped
    }
    
    @Override
	public void keyReleased(KeyEvent e) {
		// TODO keyReleased
		
	}
}
