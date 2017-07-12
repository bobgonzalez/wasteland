/**
 * Created by robert on 6/24/17.
 */
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Bullet {
    private double x;
    private double y;
    private int r;

    private double dx;
    private double dy;
    private double rad;
    private double speed;

    private double damage;
    public double lifeFrames;

    private Color color1;

    public Bullet(double angle, int x, int y, double sp, int lf, double d, int ra){
        this.x = x;
        this.y = y;
        r = 13;
        speed = sp;

        rad = Math.toRadians(angle);
        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;

        color1 = Color.RED;
        lifeFrames = lf;
        damage = d;
    }

    public boolean update(){
        x += dx;
        y += dy;
        lifeFrames--;
        if (x < -r || x > GamePanel.WIDTH + r || y < -r || y > GamePanel.HEIGHT + r )
            return true;
        if(lifeFrames <= 0){
            return true;
        }
        return false;
    }

    public void draw(Graphics2D g){
        //g.setColor(color1);
        //g.fillOval((int) (x-r), (int) (y-r), 2*r, 2*r);
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("/home/robert/Documents/wasteland/src/res/b2.png"));
        } catch (IOException e) {System.out.println("IO Error");}
        g.drawImage(img, (int)x-r, (int)y-r, null);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getR() {
        return r;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}
