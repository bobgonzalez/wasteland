/**
 * Created by robert on 6/24/17.
 */
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

// TODO: create strength attribule in bullet; have it vary by wepon

public class GamePanel extends JPanel implements Runnable, KeyListener {

    public static int WIDTH = 800;
    public static int HEIGHT = 800;

    private Thread thread;
    private boolean running;
    private BufferedImage image;
    private Graphics2D g;
    private int FPS = 30;
    private double average_FPS;
    public static Player player;
    public static ArrayList<Bullet> bullets;
    public static ArrayList<Enemy> enemies;
    public static ArrayList<NPC> npcs;
    public static ArrayList<PowerUp> powers;
    private long waveStartTimer;
    private long waveStartTimerDiff;
    private int waveNumber;
    private boolean waveStart;
    private int waveDelay = 2000;
    private boolean pause = false;

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
        npcs = new ArrayList<NPC>();
        powers = new ArrayList<PowerUp>();
        waveStartTimer = 0;
        waveStartTimerDiff = 0;
        waveStart = true;
        waveNumber = 0;
        //for(int i = 0; i < 5; i++){
        //    enemies.add(new Enemy(1, 1));
        //}
        while(running){

            if (pause){
                pause();
            }
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

    private void pause(){

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
        if (enemies.size() == 0){
            // kill live NPCS
            for(int i = 0; i < npcs.size(); i++){
                Random r = new Random();
                int i1 = r.nextInt(6 - 1) + 1;
                powers.add(new PowerUp(i1, npcs.get(i).getX(), npcs.get(i).getY()));
                npcs.remove(i);
            }
        }
        //create enemies
        if(waveStart && enemies.size() == 0){
            createNewEnemies();
            createNewNPCS();
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
        // update NPCS
        for(int i = 0; i < npcs.size(); i++){
            npcs.get(i).update();
        }
        // update powers
        for(int i = 0; i < powers.size(); i++){
            powers.get(i).update();
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
                    e.hit(b.getDamage());
                    bullets.remove(i);
                    i--;
                    break;
                }
            }
        }
        // check for bullet NPC collision
        for(int i = 0; i < bullets.size(); i++){
            Bullet b = bullets.get(i);
            double bx = b.getX();
            double by = b.getY();
            double br = b.getR();
            for(int j = 0; j < npcs.size(); j++){
                NPC e = npcs.get(j);
                double ex = e.getX();
                double ey = e.getY();
                double er = e.getR();
                double dx = bx - ex;
                double dy = by - ey;
                double dist = Math.sqrt(dx*dx + dy*dy);

                if(dist < br + er + 1){
                    e.hit(b.getDamage());
                    bullets.remove(i);
                    i--;
                    break;
                }
            }
        }
        // check for Enemy NPC collision
        for(int i = 0; i < npcs.size(); i++){
            NPC b = npcs.get(i);
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
                    b.hit(1);
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
        // clean up dead NPCS
        for(int i = 0; i < npcs.size(); i++){
            if(npcs.get(i).isDead()){
                // - 1player.setKills();
                npcs.remove(i);
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
        // check for player power up collisions
        double bx = player.getX();
        double by = player.getY();
        double br = player.getR();
        for(int i = 0; i < powers.size(); i++){
            PowerUp e = powers.get(i);
            double ex = e.getX();
            double ey = e.getY();
            double er = e.getR();
            double dx = bx - ex;
            double dy = by - ey;
            double dist = Math.sqrt(dx*dx + dy*dy);

            if(dist < br + er + 1){
                if (powers.get(i).getType() == 1) {
                    player.addLife();
                }
                if (powers.get(i).getType() == 2) {
                    player.upSpeed();
                }
                if (powers.get(i).getType() == 3) {
                    player.upFiringSpeed();
                }
                if (powers.get(i).getType() == 4) {
                    player.upFirePower();
                }
                if (powers.get(i).getType() == 5) {
                    player.upLF();
                }
                    powers.remove(i);
                    i--;
                break;
            }
        }
    }

    private void gameRender(){
        BufferedImage img = null;
        //g.setColor(new Color(20, 100, 20));
        //g.fillRect(0, 0, WIDTH, HEIGHT);
        try {
            img = ImageIO.read(new File("/home/robert/Documents/wasteland/src/res/space3.jpg"));
        } catch (IOException e) {System.out.println("IO Error");}
        //g.setColor(Color.BLACK);
        //g.drawString("FPS : " + average_FPS, 10, 10);
        g.drawImage(img, 0, 0, null);
        // draw health
        for(int i = 0; i < player.getLives(); i++){
            BufferedImage img2 = null;
            try {
                img2 = ImageIO.read(new File("/home/robert/Documents/wasteland/src/res/player_up.png"));
            } catch (IOException e) {System.out.println("IO Error");}
            g.drawImage(img2, 40 + (40 * i), 40, null);
            //g.setColor(Color.WHITE);
            //g.fillOval(20 + (20 * i), 20, player.getR()*2, player.getR()*2);
            //g.setStroke(new BasicStroke(3));
            //g.setColor(Color.WHITE.darker());
            //g.drawOval(20 + (20 * i), 20, player.getR()*2, player.getR()*2);
            //g.setStroke(new BasicStroke(1));
        }
        // draw powers
        for(int i = 0; i < powers.size(); i++){
            powers.get(i).draw(g);
        }
        // draw emenies
        for(int i = 0; i < enemies.size(); i++){
            enemies.get(i).draw(g);
        }
        // draw bullets
        for(int i = 0; i < bullets.size(); i++){
            bullets.get(i).draw(g);
        }
        // draw player
        player.draw(g);
        // draw NPCS
        for(int i = 0; i < npcs.size(); i++){
            npcs.get(i).draw(g);
        }
        // draw wave number
        if(waveStartTimer != 0){
            g.setFont(new Font("Century Gothic", Font.BOLD, 24));
            String s = "-    W A V E    " + waveNumber + "    -";
            int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
            int alpha = (int) (255 * Math.sin(3.14 * waveStartTimerDiff / waveDelay));
            if(alpha > 255) alpha = 255;
            g.setColor(new Color(255, 255, 255, alpha));
            g.drawString(s, WIDTH/2 - length/2, HEIGHT/2);
        }
        g.setColor(Color.WHITE);
        String s = "Points : " + player.getKills();
        g.drawString(s, GamePanel.WIDTH-200, 40);
    }

    private void gameDraw(){
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }

    private void createNewNPCS(){
        npcs.clear();

        if(waveNumber % 5 == 1){
            for(int i = 0; i < 1; i++) {
                npcs.add(new NPC(1, 1));
            }
        }
        if(waveNumber % 5 == 2){
            for(int i = 0; i < 2; i++) {
                npcs.add(new NPC(1, 1));
            }
        }
        if(waveNumber % 5 == 3){
            for(int i = 0; i < 4; i++) {
                npcs.add(new NPC(1, 1));
            }
        }
        if(waveNumber % 5 == 4){
            for(int i = 0; i < 8; i++) {
                npcs.add(new NPC(1, 1));
            }
        }
        if(waveNumber % 5 == 0){
            for(int i = 0; i < 16; i++) {
                npcs.add(new NPC(1, 1));
            }
        }
    }

    private void createNewEnemies(){
        enemies.clear();

        if(waveNumber % 5 == 1){
            for(int i = 0; i < 4; i++) {
                enemies.add(new Enemy(1, 1));
            }
        }
        if(waveNumber % 5 == 2){
            for(int i = 0; i < 2; i++) {
                enemies.add(new Enemy(1, 1));
            }
            for(int i = 0; i < 4; i++) {
                enemies.add(new Enemy(1, 2));
            }
        }
        if(waveNumber % 5 == 3){
            for(int i = 0; i < 3; i++) {
                enemies.add(new Enemy(1, 1));
            }
            for(int i = 0; i < 5; i++) {
                enemies.add(new Enemy(1, 2));
            }
            for(int i = 0; i < 4; i++) {
                enemies.add(new Enemy(1, 3));
            }
        }
        if(waveNumber % 5 == 4){
            for(int i = 0; i < 2; i++) {
                enemies.add(new Enemy(1, 1));
            }
            for(int i = 0; i < 2; i++) {
                enemies.add(new Enemy(1, 2));
            }
            for(int i = 0; i < 4; i++) {
                enemies.add(new Enemy(1, 3));
            }
            for(int i = 0; i < 6; i++) {
                enemies.add(new Enemy(1, 4));
            }
        }
        if(waveNumber % 5 == 0){
            for(int i = 0; i < 4; i++) {
                enemies.add(new Enemy(1, 3));
            }
            for(int i = 0; i < 12; i++) {
                enemies.add(new Enemy(1, 4));
            }
        }
        for (int i = 0; i < enemies.size(); i++){
            enemies.get(i).setHealth(enemies.get(i).getHealth()+(waveNumber/5));
            enemies.get(i).setSpeed(enemies.get(i).getSpeed()+(waveNumber/5.0)*.5);
        }
    }

    public void killAll(){
        for(int i = 0; i < enemies.size(); i++){
            enemies.clear();

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
        if(keyCode == KeyEvent.VK_A){
            killAll();
        }
        if(keyCode == KeyEvent.VK_P){
            if(pause){
                pause = false;
            }
            else
                pause = true;
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
