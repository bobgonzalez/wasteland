/**
 * Created by robert on 6/24/17.
 */
import java.awt.*;

public class Bullet {
    private double x;
    private double y;
    private int r;

    private double dx;
    private double dy;
    private double rad;
    private double speed;

    public double lifeFrames;

    private Color color1;

    public Bullet(double angle, int x, int y, int sp, int lf){
        this.x = x;
        this.y = y;
        r = 2;
        speed = sp;

        rad = Math.toRadians(angle);
        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;

        color1 = Color.RED;
        lifeFrames = lf;
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
        g.setColor(color1);
        g.fillOval((int) (x-r), (int) (y-r), 2*r, 2*r);
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
}
