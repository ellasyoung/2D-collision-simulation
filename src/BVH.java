import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class BVH implements Iterable<Circle>{
    Box boundingBox;

    BVH child1;
    BVH child2;

    Circle containedCircle;

    public BVH(ArrayList<Circle> circles) {
        this.boundingBox = buildTightBoundingBox(circles);
        if (circles.size() == 1){
            this.containedCircle = circles.get(0);
        }else{
            ArrayList<Circle>[] splitted = split(circles, this.boundingBox);
            this.child1 = new BVH(splitted[0]);
            this.child2 = new BVH(splitted[1]);
        }

    }

    public void draw(Graphics2D g2) {
        this.boundingBox.draw(g2);
        if (this.child1 != null) {
            this.child1.draw(g2);
        }
        if (this.child2 != null) {
            this.child2.draw(g2);
        }
    }

    public static ArrayList<Circle>[] split(ArrayList<Circle> circles, Box boundingBox) {
        ArrayList<Circle> return1 = new ArrayList<>();
        ArrayList<Circle> return2 = new ArrayList<>();
        double seperation=0;


        if (boundingBox.getHeight() > boundingBox.getWidth()){
            seperation=boundingBox.getMidY();
            for(int i=0; i < circles.size(); i++){
                if(circles.get(i).getPosition().y < seperation ){
                    return1.add(circles.get(i));
                }else{
                    return2.add(circles.get(i));
                }
            }
        }else{
            seperation=boundingBox.getMidX();
            for(int i=0; i < circles.size(); i++){
                if(circles.get(i).getPosition().x < seperation ){
                    return1.add(circles.get(i));
                }else{
                    return2.add(circles.get(i));
                }
            }
        }

        ArrayList<Circle>[] toReturn = new ArrayList[2];
        toReturn[0] = return1;
        toReturn[1] = return2;
        return toReturn;
    }

    // returns the smallest possible box which fully encloses every circle in circles
    public static Box buildTightBoundingBox(ArrayList<Circle> circles) {
        Vector2 bottomLeft = new Vector2(Float.POSITIVE_INFINITY);
        Vector2 topRight = new Vector2(Float.NEGATIVE_INFINITY);

        for (Circle c : circles) {
            bottomLeft = Vector2.min(bottomLeft, c.getBoundingBox().bottomLeft);
            topRight = Vector2.max(topRight, c.getBoundingBox().topRight);
        }

        return new Box(bottomLeft, topRight);
    }

    // METHODS BELOW RELATED TO ITERATOR
    @Override
    public Iterator<Circle> iterator() {
        return new BVHIterator(this);
    }

    public class BVHIterator implements Iterator<Circle> {

        ArrayList<Circle> cs = new ArrayList<Circle>();
        Iterator<Circle> it;

        public void findC (BVH bvh, ArrayList<Circle> cs){
            boolean hasCircle = bvh.containedCircle != null;

            if(hasCircle){
                cs.add(bvh.containedCircle);
            }else{
                findC(bvh.child1, cs);
                findC(bvh.child2, cs);
            }
        }

        // todo for students
        public BVHIterator(BVH bvh) {
            findC(bvh, cs);
            it = cs.iterator();
        }

        // todo for students
        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        // todo for students
        @Override
        public Circle next() {
            return it.next();
        }
    }
}