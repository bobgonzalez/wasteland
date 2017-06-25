/**
 * Created by robert on 6/24/17.
 */
import java.awt.*;

public class Player {
    private int x;
    private int y;
    private int r;

    private int dx;
    private int dy;
    private int speed;

    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;

    private double lastDirection;
    private boolean strafe;
    private boolean sniper;
    private boolean shotgun;
    private boolean aoe;
    private int mode;
    private int kills;

    public int lf;

    private int lives;
    private Color color1;
    private Color color2;

    private boolean firing;
    private long firingTimer;
    private long firingDelay;

    private boolean recovering;
    private long recoveryTimer;

    public Player(){
        x = GamePanel.WIDTH/2;
        y = GamePanel.HEIGHT/2;
        r = 5;
        dx = 0;
        dy = 0;
        speed = 8;
        lives = 3;
        color1 = Color.WHITE;
        color2 = Color.RED;
        firing = false;
        firingTimer = System.nanoTime();
        firingDelay = 200;
        sniper = true;
        mode = 1;
        kills = 0;
        lf = 25;
        recovering = false;
        recoveryTimer = 0;
    }

    public void loseLife(){
        lives--;
        recovering = true;
        recoveryTimer = System.nanoTime();
    }

    public void nextMode(){
        if(sniper){
            shotgun = true;
            sniper = false;
            mode = 2;
            firingDelay = 300;
            lf = 15;
            speed = 5;

        }
        else if(shotgun){
            shotgun = false;
            aoe = true;
            mode = 3;
            firingDelay = 600;
            lf = 10;
            speed = 3;
        }
        else if(aoe){
            sniper = true;
            aoe = false;
            mode = 1;
            firingDelay = 100;
            lf = 25;
            speed = 8;
        }
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setFiring(boolean firing) {
        this.firing = firing;
    }

    public void setStrafe(boolean strafe) {
        this.strafe = strafe;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getR() {
        return r;
    }

    public int getLives() {
        return lives;
    }

    public int getKills() {
        return kills;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isRecovering() {
        return recovering;
    }

    public void update(){
        if(left){
            dx = -speed;
            if(!strafe) lastDirection = 180;
        }
        if(right){
            dx = speed;
            if(!strafe) lastDirection = 0;

        }
        if(up){
            dy = -speed;
            if(!strafe) lastDirection = 270;

        }
        if(down){
            dy = speed;
            if(!strafe) lastDirection = 90;

        }

        x+=dx;
        y+=dy;

        if (x < r) x = r;
        if (y < r) y = r;
        if(x > GamePanel.WIDTH - r) x = GamePanel.WIDTH - r;
        if(x > GamePanel.HEIGHT - r) x = GamePanel.HEIGHT - r;

        dx = 0;
        dy = 0;
        if(sniper) mode = 1;
        if(shotgun) mode = 2;
        if(aoe) mode = 3;
        if(firing){
            long elapsed = (System.nanoTime() - firingTimer) / 1000000;
            if(left && !strafe){
                fire(180, elapsed);
            }
            else if(right && !strafe){
                fire(0, elapsed);
            }
            else if(up && !strafe){
                fire(270, elapsed);
            }
            else if(down && !strafe){
                fire(90, elapsed);
            }
            else{
                fire(lastDirection, elapsed);
            }

        }
        long elapsed = (System.nanoTime() - recoveryTimer) / 1000000;
        if(elapsed > 2000){
            recovering = false;
            recoveryTimer = 0;
        }
    }

    private void fire(double angle, long elapsed){
        if(mode == 1) {
            if (elapsed > firingDelay) {
                GamePanel.bullets.add(new Bullet(angle, x, y, 10, lf));
                firingTimer = System.nanoTime();
            }
        }
        if(mode == 2) {
            if (elapsed > firingDelay) {
                GamePanel.bullets.add(new Bullet(angle, x, y, 10, lf));
                GamePanel.bullets.add(new Bullet(angle + 30, x, y, 10, lf));
                GamePanel.bullets.add(new Bullet(angle - 30, x, y, 10, lf));
                firingTimer = System.nanoTime();
            }
        }
        if(mode == 3) {
            if (elapsed > firingDelay) {
                GamePanel.bullets.add(new Bullet(0, x, y, 5, lf));
                GamePanel.bullets.add(new Bullet(90, x, y, 5, lf));
                GamePanel.bullets.add(new Bullet(180, x, y, 5, lf));
                GamePanel.bullets.add(new Bullet(270, x, y, 5, lf));
                GamePanel.bullets.add(new Bullet(30, x, y, 5, lf));
                GamePanel.bullets.add(new Bullet(60, x, y, 5, lf));
                GamePanel.bullets.add(new Bullet(150, x, y, 5, lf));
                GamePanel.bullets.add(new Bullet(210, x, y, 5, lf));
                GamePanel.bullets.add(new Bullet(240, x, y, 5, lf));
                GamePanel.bullets.add(new Bullet(120, x, y, 5, lf));
                GamePanel.bullets.add(new Bullet(330, x, y, 5, lf));
                GamePanel.bullets.add(new Bullet(300, x, y, 5, lf));
                firingTimer = System.nanoTime();
            }
        }
    }

    public void draw(Graphics2D g){
        g.setColor(color1);
        if(recovering) g.setColor(color2);
        g.fillOval(x-r, y-r, 2*r, 2*r);

        g.setStroke(new BasicStroke(3));
        g.setColor(color1.darker());
        if(recovering) g.setColor(color2.darker());
        g.drawOval(x-r, y-r, 2*r, 2*r);
        g.setStroke(new BasicStroke(1));
    }
}
