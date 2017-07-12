/**
 * Created by robert on 6/24/17.
 */
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
    private int bulletSize;
    private double bulletSpeed;
    private double damage;

    public int lf;

    private int lives;
    private Color color1;
    private Color color2;

    private boolean firing;
    private long firingTimer;
    private long firingDelay;
    private long fsBonus = 0;
    private double dBonus = 0;
    private int lfBonus = 0;
    private int speedBonus = 0;

    private boolean recovering;
    private long recoveryTimer;

    public Player(){
        x = GamePanel.WIDTH/2;
        y = GamePanel.HEIGHT/2;
        r = 25;
        dx = 0;
        dy = 0;
        speed = 7;
        lives = 3;
        color1 = Color.WHITE;
        color2 = Color.RED;
        firing = false;
        firingTimer = System.nanoTime();
        firingDelay = 200;
        sniper = true;
        mode = 1;
        kills = 0;
        lf = 40;
        recovering = false;
        recoveryTimer = 0;
        bulletSpeed = 20;
        bulletSize = 4;
        damage = 2.5;
    }

    public void loseLife(){
        lives--;
        recovering = true;
        recoveryTimer = System.nanoTime();
        setLfBonus(0);
        setdBonus(0);
        setFsBonus(0);
        setSpeedBonus(0);
    }

    public void nextMode(){
        if(sniper){
            shotgun = true;
            sniper = false;
            mode = 2;
            firingDelay = 400+fsBonus;
            lf = 30+lfBonus;
            speed = 10+speedBonus;
            bulletSpeed = 15;
            bulletSize = 4;
            damage = 1.75+dBonus;
        }
        else if(shotgun){
            shotgun = false;
            aoe = true;
            mode = 3;
            firingDelay = 300+fsBonus;
            lf = 20+lfBonus;
            speed = 13+speedBonus;
            bulletSpeed = 7;
            bulletSize = 4;
            damage = 1.2+dBonus;
        }
        else if(aoe){
            sniper = true;
            aoe = false;
            mode = 1;
            firingDelay = 200+fsBonus;
            lf = 40+lfBonus;
            speed = 7+speedBonus;
            bulletSpeed = 20;
            bulletSize = 4;
            damage = 2.5+dBonus;
        }
    }

    public void setFsBonus(long fsBonus) {
        this.fsBonus = fsBonus;
    }

    public void setSpeedBonus(int speedBonus) {
        this.speedBonus = speedBonus;
    }

    public void setdBonus(double dBonus) {
        this.dBonus = dBonus;
    }

    public void setLfBonus(int lfBonus) {
        this.lfBonus = lfBonus;
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

    public long getFiringDelay() {
        return firingDelay;
    }

    public void setFiringDelay(long firingDelay) {
        this.firingDelay = firingDelay;
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
        if(y > GamePanel.HEIGHT - r) y = GamePanel.HEIGHT - r;

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
                GamePanel.bullets.add(new Bullet(angle, x, y, bulletSpeed, lf, damage, bulletSize));
                firingTimer = System.nanoTime();
            }
        }
        if(mode == 2) {
            if (elapsed > firingDelay) {
                GamePanel.bullets.add(new Bullet(angle, x, y, bulletSpeed, lf, damage, bulletSize));
                GamePanel.bullets.add(new Bullet(angle + 30, x, y, bulletSpeed, lf, damage, bulletSize));
                GamePanel.bullets.add(new Bullet(angle - 30, x, y, bulletSpeed, lf, damage, bulletSize));
                firingTimer = System.nanoTime();
            }
        }
        if(mode == 3) {
            if (elapsed > firingDelay) {
                GamePanel.bullets.add(new Bullet(0, x, y, bulletSpeed, lf, damage, bulletSize));
                GamePanel.bullets.add(new Bullet(90, x, y, bulletSpeed, lf, damage, bulletSize));
                GamePanel.bullets.add(new Bullet(180, x, y, bulletSpeed, lf, damage, bulletSize));
                GamePanel.bullets.add(new Bullet(270, x, y, bulletSpeed, lf, damage, bulletSize));
                GamePanel.bullets.add(new Bullet(30, x, y, bulletSpeed, lf, damage, bulletSize));
                GamePanel.bullets.add(new Bullet(60, x, y, bulletSpeed, lf, damage, bulletSize));
                GamePanel.bullets.add(new Bullet(150, x, y, bulletSpeed, lf, damage, bulletSize));
                GamePanel.bullets.add(new Bullet(210, x, y, bulletSpeed, lf, damage, bulletSize));
                GamePanel.bullets.add(new Bullet(240, x, y, bulletSpeed, lf, damage, bulletSize));
                GamePanel.bullets.add(new Bullet(120, x, y, bulletSpeed, lf, damage, bulletSize));
                GamePanel.bullets.add(new Bullet(330, x, y, bulletSpeed, lf, damage, bulletSize));
                GamePanel.bullets.add(new Bullet(300, x, y, bulletSpeed, lf, damage, bulletSize));
                firingTimer = System.nanoTime();
            }
        }
    }

    public void addLife(){
        this.lives++;
    }

    public void upFiringSpeed(){
        this.fsBonus -= 50;
        this.firingDelay -= this.fsBonus;
    }

    public void upFirePower(){
        this.dBonus += .25;
        this.damage += this.dBonus;
    }

    public void upLF(){
        this.lfBonus += 5;
        this.lf += this.lfBonus;
    }

    public void upSpeed(){
        this.speedBonus += 1;
        this.speed += this.speedBonus;
    }

    public void draw(Graphics2D g){
        //g.setColor(color1);
        BufferedImage img = null;

        if(recovering){
            try {
                if(lastDirection == 270)
                    img = ImageIO.read(new File("/home/robert/Documents/wasteland/src/res//player_up_r.png"));
                else if(lastDirection == 90)
                    img = ImageIO.read(new File("/home/robert/Documents/wasteland/src/res//player_down_r.png"));
                else if(lastDirection == 0)
                    img = ImageIO.read(new File("/home/robert/Documents/wasteland/src/res//player_right_r.png"));
                else if(lastDirection == 180)
                    img = ImageIO.read(new File("/home/robert/Documents/wasteland/src/res//player_left_r.png"));
            } catch (IOException e) {System.out.println("IO Error");}
        }
            //g.setColor(color2);
        //g.fillOval(x-r, y-r, 2*r, 2*r);

        //g.setStroke(new BasicStroke(3));
        //g.setColor(color1.darker());
        //if(recovering) g.setColor(color2.darker());
        //g.drawOval(x-r, y-r, 2*r, 2*r);
        //g.setStroke(new BasicStroke(1));

        else{
            try {
                if (lastDirection == 270)
                    img = ImageIO.read(new File("/home/robert/Documents/wasteland/src/res//player_up.png"));
                else if (lastDirection == 90)
                    img = ImageIO.read(new File("/home/robert/Documents/wasteland/src/res//player_down.png"));
                else if (lastDirection == 0)
                    img = ImageIO.read(new File("/home/robert/Documents/wasteland/src/res//player_right.png"));
                else if (lastDirection == 180)
                    img = ImageIO.read(new File("/home/robert/Documents/wasteland/src/res//player_left.png"));
            } catch (IOException e) {
                System.out.println("IO Error");
            }
        }
        g.drawImage(img, (int)x-r, (int)y-r, null);
    }
}
