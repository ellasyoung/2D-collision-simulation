import java.awt.*;

public class Circle {
    public static int idCount;

    public Vector2 position;
    public Vector2 velocity;
    public double radius;
    public Color color;
    public final int id;

    public Circle(Vector2 position, double radius) {
        this.position = position;
        this.velocity = new Vector2();
        this.radius = radius;
        this.color = new Color(255,255,255);
        this.id = idCount++;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(this.color);
        Vector2 bottomleft = Vector2.sub(this.position, new Vector2(this.radius));
        g2.fillOval((int)bottomleft.x, (int)bottomleft.y, (int)this.radius*2, (int)this.radius*2);
    }

    public Box getBoundingBox() {
        Vector2 bottomleft = Vector2.sub(this.position, new Vector2(this.radius));
        Vector2 topright = Vector2.add(this.position, new Vector2(this.radius));
        return new Box(bottomleft, topright);
    }

    public Vector2 getPosition() {
        return this.position;
    }

    public double getRadius() {
        return this.radius;
    }

    public ContactResult isContacting(Circle c) {
        Vector2 diff = Vector2.sub(this.position, c.position);
        double separationDist = Vector2.length(diff);
        double minSeparationDist = this.radius + c.radius;

        if (separationDist < minSeparationDist) {
            return new ContactResult(this, c, separationDist - minSeparationDist, Vector2.normalize(diff));
        }
        return null;
    }

    public boolean isInside(Vector2 point) {
        return Vector2.length(Vector2.sub(point, this.position)) < this.radius;
    }

    @Override
    public String toString() {
        return String.format("Circle(id: %s, position: %s, radius: %s)", String.valueOf(id), position.toString(), String.valueOf(radius));
    }
}