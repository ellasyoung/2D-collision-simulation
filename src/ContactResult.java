import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ContactResult {
    double penetrationDepth;
    Vector2 contactNormal;
    Circle a;
    Circle b;

    public ContactResult() {
        this.penetrationDepth = 0f;
        this.contactNormal = new Vector2();
        this.a = null;
        this.b = null;
    }

    public ContactResult(Circle a, Circle b, double penetrationDepth, Vector2 contactNormal) {
        this.penetrationDepth = penetrationDepth;
        this.contactNormal = contactNormal;
        this.a = a;
        this.b = b;
    }

    public ContactResult(Circle a, Circle b) {
        this.penetrationDepth = 0.0;
        this.contactNormal = null;
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString(){
        Circle first = a.id < b.id ? a : b;
        Circle second = a.id >= b.id ? a : b;
        return "Contact between circles: " + first + " and " + second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactResult that = (ContactResult) o;
        return this.hashCode() == that.hashCode();
    }

    @Override
    public int hashCode() {
        Circle first = a.id < b.id ? a : b;
        Circle second = a.id >= b.id ? a : b;
        return Objects.hash(first, second);
    }
}
