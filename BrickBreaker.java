import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BrickBreaker extends JPanel implements KeyListener, ActionListener {
    private int ballX = 250, ballY = 300, ballDX = 2, ballDY = -3, ballSize = 20;
    private int paddleX = 200, paddleWidth = 100, paddleHeight = 10;
    private int score = 0;
    private int totalBricks = 18; // Total number of bricks
    private boolean gameOver = false, gameWon = false;
    private Timer timer;
    private int timeLeft = 60; // Timer countdown (1 min)
    
    private boolean[][] bricks;
    
    public BrickBreaker() {
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        bricks = new boolean[3][6]; // 3 rows, 6 columns of bricks
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 6; j++) {
                bricks[i][j] = true; // All bricks initially present
            }
        }

        timer = new Timer(1000, e -> { // Timer for countdown
            if (timeLeft > 0) timeLeft--;
            else gameOver = true;
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Paddle
        g.setColor(Color.WHITE);
        g.fillRect(paddleX, getHeight() - 50, paddleWidth, paddleHeight);

        // Ball
        g.setColor(Color.RED);
        g.fillOval(ballX, ballY, ballSize, ballSize);

        // Bricks
        int brickWidth = getWidth() / 6;
        int brickHeight = 30;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 6; j++) {
                if (bricks[i][j]) {
                    g.setColor(new Color((i + 1) * 80, (j + 1) * 40, 200));
                    g.fillRect(j * brickWidth, i * brickHeight, brickWidth - 5, brickHeight - 5);
                }
            }
        }

        // Score & Timer Display
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Time: " + timeLeft + "s", 20, 20);
        g.drawString("Bricks Left: " + totalBricks, 20, 40);

        // Game Over/Win Message
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER!", getWidth() / 2 - 100, getHeight() / 2);
        } else if (gameWon) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("YOU WIN!", getWidth() / 2 - 80, getHeight() / 2);
        }
    }

    public void moveBall() {
        if (gameOver || gameWon) return;

        ballX += ballDX;
        ballY += ballDY;

        // Ball hits walls
        if (ballX <= 0 || ballX + ballSize >= getWidth()) ballDX = -ballDX;
        if (ballY <= 0) ballDY = -ballDY;

        // Ball hits paddle
        if (ballY + ballSize >= getHeight() - 50 && ballX + ballSize >= paddleX && ballX <= paddleX + paddleWidth) {
            ballDY = -ballDY;
        }

        // Ball hits bricks
        int brickWidth = getWidth() / 6;
        int brickHeight = 30;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 6; j++) {
                if (bricks[i][j]) {
                    int brickX = j * brickWidth;
                    int brickY = i * brickHeight;
                    if (ballX + ballSize >= brickX && ballX <= brickX + brickWidth &&
                        ballY + ballSize >= brickY && ballY <= brickY + brickHeight) {
                        bricks[i][j] = false; // Break brick
                        ballDY = -ballDY;
                        totalBricks--;
                        if (totalBricks == 0) gameWon = true; // Win condition
                    }
                }
            }
        }

        // Ball falls below screen
        if (ballY > getHeight()) gameOver = true;

        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        moveBall();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT && paddleX > 0) {
            paddleX -= 30;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && paddleX + paddleWidth < getWidth()) {
            paddleX += 30;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Brick Breaker");
        BrickBreaker game = new BrickBreaker();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.add(game);
        frame.setVisible(true);

        Timer gameLoop = new Timer(10, game); // Moves the ball every 10ms
        gameLoop.start();
    }
}
