import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

public class PublicTesterUtils {

    // checks to see if the leaf boundingBoxes of your BVH are at least a certain size
    public static boolean leavesMinimumSize(BVH bvh, double size) {
        if (bvh.containedCircle != null) {
            return bvh.boundingBox.getWidth()/2 >= size && bvh.boundingBox.getHeight()/2 >= size;
        }
        if (bvh.child1 != null && !leavesMinimumSize(bvh.child1, size)) {
            return false;
        }
        if (bvh.child2 != null && !leavesMinimumSize(bvh.child2, size)) {
            return false;
        }
        return true;
    }

    // returns true if all Circles in circles are contained in the given bvh
    public static boolean containsAllCircles(ArrayList<Circle> circles, BVH bvh) {
        for (Circle c: circles) {
            if (!containsCircle(c, bvh)) {
                return false;
            }
        }
        return true;
    }

    public static boolean containsCircle(Circle c, BVH bvh) {
        if (bvh.containedCircle != null && bvh.containedCircle.equals(c)) {
            return true;
        }
        boolean contains = false;
        if (bvh.child1 != null) {
            if (containsCircle(c, bvh.child1)) {
                contains = true;
            }
        }
        if (bvh.child2 != null) {
            if (containsCircle(c, bvh.child2)) {
                contains = true;
            }
        }
        return contains;
    }

    public static ArrayList<Circle> randomInit(int seed, int count, double width, double height) {
        ArrayList<Circle> circles = new ArrayList<>();
        Random generator = new Random(seed);
        for (int i=0; i<count; i++) {
            double x = generator.nextDouble() * width;
            double y = generator.nextDouble() * height;
            Circle circle = new Circle(new Vector2(x, y), 10);
            circles.add(circle);
        }
        return circles;
    }

    // This checks two things
    // 1. That ONLY the leaves of your BVH have containedCircles
    // 2. That ALL the leaves of your BVH have containedCircles
    // Read carefully, these are not the same thing
    // Returns true if both checks pass, false otherwise
    static boolean areCirclesInLeaves(BVH bvh) {
        boolean hasCircle = bvh.containedCircle != null;

        // if leaf and hasCircle, that's good. If not a leaf and hasCircle, that's bad
        if (bvh.child1 == null && bvh.child2 == null) {
            return hasCircle;
        } else {
            if (hasCircle == true) {
                return false;
            }
        }

        if (bvh.child1 != null && !areCirclesInLeaves(bvh.child1)) {
            return false;
        }

        if (bvh.child2 != null && !areCirclesInLeaves(bvh.child2)) {
            return false;
        }

        return true;
    }

    // returns the total area of all boundingBoxes in this bvh
    static double area(BVH bvh) {
        double area = bvh.boundingBox.getArea();
        if (bvh.child1 != null) {
            area += area(bvh.child1);
        }
        if (bvh.child2 != null) {
            area += area(bvh.child2);
        }
        return area;
    }

    // returns the number of circles contained in this bvh
    static int weight(BVH bvh) {
        if (bvh.containedCircle != null) {
            return 1;
        }
        int sum = 0;
        if (bvh.child1 != null) {
            sum += weight(bvh.child1);
        }
        if (bvh.child2 != null) {
            sum += weight(bvh.child2);
        }
        return sum;
    }

    // returns true if all nodes are nested fully inside their parent
    static boolean childrenInsideParents(BVH bvh) {
        if (bvh.child1 == null && bvh.child2 == null) {
            return true;
        }
        if (bvh.child1 != null && !bvh.child1.boundingBox.isFullyInside(bvh.boundingBox)){
            return false;
        }
        if (bvh.child2 != null && !bvh.child2.boundingBox.isFullyInside(bvh.boundingBox)) {
            return false;
        }
        return childrenInsideParents(bvh.child1) && childrenInsideParents(bvh.child2);
    }

    public static ArrayList<String> serializeContactResultList(HashSet<ContactResult> contacts){
        ArrayList<String> contactsString = new ArrayList<>();
        for (ContactResult contact : contacts) {
            contactsString.add(contact.toString());
        }
        Collections.sort(contactsString);
        return contactsString;
    }
}