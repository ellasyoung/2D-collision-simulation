import java.awt.*;

public class Box {
    public Vector2 bottomLeft;
    public Vector2 topRight;

    public Box(Vector2 bottomLeft, Vector2 topRight) {
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
    }

    public String toString() {
        return "Box(bottomLeft: " + bottomLeft + ", topRight: " + topRight + ")";
    }

    // returns true if this and b are overlapping
    public boolean intersectBox(Box b) {
        return Math.max(this.bottomLeft.x, b.bottomLeft.x) < Math.min(this.topRight.x, b.topRight.x) && Math.min(this.topRight.y, b.topRight.y) > Math.max(this.bottomLeft.y, b.bottomLeft.y);
    }

    // returns true if this is fully inside of b
    public boolean isFullyInside(Box b) {
        return this.bottomLeft.x >= b.bottomLeft.x && this.topRight.x <= b.topRight.x && this.bottomLeft.y >= b.bottomLeft.y && this.topRight.y <= b.topRight.y;
    }

    public void draw(Graphics2D g2) {
        Vector2 offset = Vector2.sub(this.topRight, this.bottomLeft);
        g2.drawRect((int)this.bottomLeft.x, (int)this.bottomLeft.y, (int)offset.x, (int)offset.y);
    }

    public Box getBoundingBox() {
        return this;
    }

    public double getWidth() {
        return this.topRight.x - this.bottomLeft.x;
    }

    public double getHeight() {
        return this.topRight.y - this.bottomLeft.y;
    }

    public double getMidX() {
        return this.bottomLeft.x + this.getWidth() / 2f;
    }

    public double getMidY() {
        return this.bottomLeft.y + this.getHeight() / 2f;
    }

    public double getArea() {
        return this.getWidth() * this.getHeight();
    }

    public Vector2 getPosition() {
        return Vector2.div(Vector2.add(this.bottomLeft, this.topRight), 2);
    }
}