/**
 * Created by robert on 6/24/17.
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable, KeyListener {

    public static int WIDTH = 600;
    public static int HEIGHT = 600;

    private Thread thread;
    private boolean running;
    private BufferedImage image;
    private Graphics2D g;
    private int FPS = 30;
    private double average_FPS;
    public static Player player;
    public static ArrayList<Bullet> bullets;
    public static ArrayList<Enemy> enemies;
    private long waveStartTimer;
    private long waveStartTimerDiff;
    private int waveNumber;
    private boolean waveStart;
    private int waveDelay = 2000;

    public GamePanel(){
        super();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
    }

    public void addNotify(){
        super.addNotify();
        if(thread==null){
            thread = new Thread(this);
            thread.start();
        }
        addKeyListener(this);
    }

    public void run(){
        running = true;
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        long startTime;
        long URDTimeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        int maxFrameCount = 30;
        long target_time = 1000 / FPS;
        player = new Player();
        bullets = new ArrayList<Bullet>();
        enemies = new ArrayList<Enemy>();
        waveStartTimer = 0;
        waveStartTimerDiff = 0;
        waveStart = true;
        waveNumber = 0;
        //for(int i = 0; i < 5; i++){
        //    enemies.add(new Enemy(1, 1));
        //}
        while(running){
            startTime = System.nanoTime();

            gameUpdate();
            gameRender();
            gameDraw();

            URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = target_time - URDTimeMillis;
            try{
                Thread.sleep(waitTime);
            } catch(Exception e){}
            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if(frameCount==maxFrameCount){
                average_FPS = 1000.0 / ((totalTime / frameCount) / 1000000);
            }
        }
    }

    private void gameUpdate(){
        // wave start
        if(waveStartTimer == 0 && enemies.size() == 0){
            waveNumber++;
            waveStart = false;
            waveStartTimer = System.nanoTime();
        }
        else {
            waveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000;
            if(waveStartTimerDiff > waveDelay){
                waveStart = true;
                waveStartTimer = 0;
                waveStartTimerDiff = 0;
            }
        }
        //create enemies
        if(waveStart && enemies.size() == 0){
            createNewEnemies();
        }
        // update player
        player.update();
        // update bullets
        for(int i = 0; i < bullets.size(); i++){
            boolean remove  = bullets.get(i).update();
            if(remove){
                bullets.remove(i);
                i--;
            }
        }
        // update enemies
        for(int i = 0; i < enemies.size(); i++){
            enemies.get(i).update();
        }
        // check for bullet enemy collision
        for(int i = 0; i < bullets.size(); i++){
            Bullet b = bullets.get(i);
            double bx = b.getX();
            double by = b.getY();
            double br = b.getR();
            for(int j = 0; j < enemies.size(); j++){
                Enemy e = enemies.get(j);
                double ex = e.getX();
                double ey = e.getY();
                double er = e.getR();
                double dx = bx - ex;
                double dy = by - ey;
                double dist = Math.sqrt(dx*dx + dy*dy);

                if(dist < br + er + 1){
                    e.hit();
                    bullets.remove(i);
                    i--;
                    break;
                }
            }
        }
        // clean up dead enemies
        for(int i = 0; i < enemies.size(); i++){
            if(enemies.get(i).isDead()){
                player.setKills(player.getKills()+enemies.get(i).getRank());
                enemies.remove(i);
                i--;
            }
        }
        // check for player enemy collision
        if(!player.isRecovering()){
            double bx = player.getX();
            double by = player.getY();
            double br = player.getR();
            for(int j = 0; j < enemies.size(); j++){
                Enemy e = enemies.get(j);
                double ex = e.getX();
                double ey = e.getY();
                double er = e.getR();
                double dx = bx - ex;
                double dy = by - ey;
                double dist = Math.sqrt(dx*dx + dy*dy);

                if(dist < br + er + 1){
                    player.loseLife();
                    break;
                }
            }
        }
    }

    private void gameRender(){
        g.setColor(new Color(20, 100, 20));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        //g.setColor(Color.BLACK);
        //g.drawString("FPS : " + average_FPS, 10, 10);

        // draw player
        player.draw(g);
        // draw bullets
        for(int i = 0; i < bullets.size(); i++){
            bullets.get(i).draw(g);
        }
        // draw emenies
        for(int i = 0; i < enemies.size(); i++){
            enemies.get(i).draw(g);
        }
        // draw wave number
        if(waveStartTimer != 0){
            g.setFont(new Font("Century Gothic", Font.PLAIN, 18));
            String s = "-    W A V E    " + waveNumber + "    -";
            int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
            int alpha = (int) (255 * Math.sin(3.14 * waveStartTimerDiff / waveDelay));
            if(alpha > 255) alpha = 255;
            g.setColor(new Color(255, 255, 255, alpha));
            g.drawString(s, WIDTH/2 - length/2, HEIGHT/2);
        }
        String s = "Points : " + player.getKills();
        g.drawString(s, GamePanel.WIDTH-100, 20);
        // draw health
        for(int i = 0; i < player.getLives(); i++){
            g.setColor(Color.WHITE);
            g.fillOval(20 + (20 * i), 20, player.getR()*2, player.getR()*2);
            g.setStroke(new BasicStroke(3));
            g.setColor(Color.WHITE.darker());
            g.drawOval(20 + (20 * i), 20, player.getR()*2, player.getR()*2);
            g.setStroke(new BasicStroke(1));
        }
    }

    private void gameDraw(){
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }

    private void createNewEnemies(){
        enemies.clear();
        Enemy e;

        if(waveNumber == 1){
            for(int i = 0; i < 4; i++) {
                enemies.add(new Enemy(1, 1));
            }
        }
        if(waveNumber == 2){
            for(int i = 0; i < 8; i++) {
                enemies.add(new Enemy(1, 1));
            }
            for(int i = 0; i < 4; i++) {
                enemies.add(new Enemy(1, 2));
            }
        }
        if(waveNumber == 3){
            for(int i = 0; i < 10; i++) {
                enemies.add(new Enemy(1, 1));
            }
            for(int i = 0; i < 6; i++) {
                enemies.add(new Enemy(1, 2));
            }
            for(int i = 0; i < 4; i++) {
                enemies.add(new Enemy(1, 3));
            }
        }
        if(waveNumber == 4){
            for(int i = 0; i < 12; i++) {
                enemies.add(new Enemy(1, 1));
            }
            for(int i = 0; i < 8; i++) {
                enemies.add(new Enemy(1, 2));
            }
            for(int i = 0; i < 6; i++) {
                enemies.add(new Enemy(1, 3));
            }
            for(int i = 0; i < 3; i++) {
                enemies.add(new Enemy(1, 4));
            }
        }
    }

    public void keyTyped(KeyEvent key){}
    public void keyPressed(KeyEvent key){
        int keyCode = key.getKeyCode();
        if(keyCode == KeyEvent.VK_LEFT){
            player.setLeft(true);
        }
        if(keyCode == KeyEvent.VK_RIGHT){
            player.setRight(true);
        }
        if(keyCode == KeyEvent.VK_UP){
            player.setUp(true);
        }
        if(keyCode == KeyEvent.VK_DOWN){
            player.setDown(true);
        }
        if(keyCode == KeyEvent.VK_Z){
            player.setFiring(true);
        }
        if(keyCode == KeyEvent.VK_X){
            player.setStrafe(true);
        }
        if(keyCode == KeyEvent.VK_C){
            player.nextMode();
        }
    }
    public void keyReleased(KeyEvent key){
        int keyCode = key.getKeyCode();
        if(keyCode == KeyEvent.VK_LEFT){
            player.setLeft(false);
        }
        if(keyCode == KeyEvent.VK_RIGHT){
            player.setRight(false);
        }
        if(keyCode == KeyEvent.VK_UP){
            player.setUp(false);
        }
        if(keyCode == KeyEvent.VK_DOWN){
            player.setDown(false);
        }
        if(keyCode == KeyEvent.VK_Z){
            player.setFiring(false);
        }
        if(keyCode == KeyEvent.VK_X){
            player.setStrafe(false);
        }

    }

}
