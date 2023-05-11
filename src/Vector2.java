import java.util.Objects;

public class Vector2 {
    public double x;
    public double y;

    // specify x and y
    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // specify x and y as doubles
    public Vector2(float x, float y) {
        this.x = (double) x;
        this.y = (double) y;
    }

    // initialize x and y to same double
    public Vector2(double a) {
        this.x = a;
        this.y = a;
    }

    // initialize zero vector
    public Vector2() {
        this.x = 0;
        this.y = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2 vector2 = (Vector2) o;
        return Double.compare(vector2.x, x) == 0 && Double.compare(vector2.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public String toString() {
        return "vec2(" + x + ", " + y + ")";
    }

    public static Vector2 add(Vector2 a, Vector2 b) {
        return new Vector2(a.x + b.x, a.y + b.y);
    }
    public static Vector2 sub(Vector2 a, Vector2 b) {
        return new Vector2(a.x - b.x, a.y - b.y);
    }
    public static Vector2 mul(Vector2 a, Vector2 b) {
        return new Vector2(a.x * b.x, a.y * b.y);
    }
    public static Vector2 mul(Vector2 a, double b) {
        return new Vector2(a.x * b, a.y * b);
    }
    public static Vector2 div(Vector2 a, Vector2 b) {
        return new Vector2(a.x / b.x, a.y / b.y);
    }
    public static Vector2 div(Vector2 a, double b) {
        return new Vector2(a.x / b, a.y / b);
    }
    public static Vector2 inv(Vector2 a) {
        return new Vector2(1 / a.x, 1 / a.y);
    }
    public static Vector2 min(Vector2 a, Vector2 b) {
        return new Vector2(Math.min(a.x, b.x), Math.min(a.y, b.y));
    }
    public static Vector2 max(Vector2 a, Vector2 b) {
        return new Vector2(Math.max(a.x, b.x), Math.max(a.y, b.y));
    }
    public static double length(Vector2 a) {
        return Math.sqrt(a.x*a.x + a.y*a.y);
    }
    public static Vector2 normalize(Vector2 a) {
        return Vector2.div(a, Vector2.length(a));
    }
    public static double dot(Vector2 a, Vector2 b) {
        return a.x*b.x + a.y*b.y;
    }
}