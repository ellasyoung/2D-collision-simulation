import java.util.ArrayList;
import java.util.HashSet;

public class ContactFinder {

    // Returns a HashSet of ContactResult objects representing all the contacts between circles in the scene.
    // The runtime of this method should be O(n^2) where n is the number of circles.
    public static HashSet<ContactResult> getContactsNaive(ArrayList<Circle> circles) {
        HashSet<ContactResult> toreturn = new HashSet<ContactResult>();
        for(int i=0; i<circles.size(); i++){
            for(int j=0; j< circles.size(); j++){
                Circle a = circles.get(i);
                Circle b = circles.get(j);
                if(a.id == b.id){
                    continue;
                }
                if (a.isContacting(b) != null){
                    toreturn.add(a.isContacting(b));
                }
            }
        }
        return toreturn;
    }

    // Returns a HashSet of ContactResult objects representing all the contacts between circles in the scene.
    // The runtime of this method should be O(n*log(n)) where n is the number of circles.
    public static HashSet<ContactResult> getContactsBVH(ArrayList<Circle> circles, BVH bvh) {
        HashSet<ContactResult> contacts = new HashSet<ContactResult>();

        for(Circle i : circles){
            HashSet<ContactResult> results = (getContactBVH(i,bvh));
            if(results != null){
                contacts.addAll(results);
            }
        }
        return contacts;
    }


    // Takes a single circle c and a BVH bvh.
    // Returns a HashSet of ContactResult objects representing contacts between c
    // and the circles contained in the leaves of the bvh.
    public static HashSet<ContactResult> getContactBVH(Circle c, BVH bvh) {
        HashSet<ContactResult> conts = new HashSet<ContactResult>();
        helpContacts(c, bvh, conts);
        return conts;
    }

    public static void helpContacts (Circle c, BVH bvh, HashSet<ContactResult> conts){
        if(!c.getBoundingBox().intersectBox(bvh.boundingBox)){
            return;
        }
        if (c.getBoundingBox().intersectBox(bvh.boundingBox)){
            boolean hasCircle = bvh.containedCircle != null;

            if(hasCircle && c.id != bvh.containedCircle.id){
                if(c.isContacting(bvh.containedCircle) != null){
                    conts.add(c.isContacting(bvh.containedCircle));
                    return;
                }
            }

            if(!hasCircle && (bvh.child1 != null && bvh.child2 != null )){
                helpContacts(c, bvh.child1, conts);
                helpContacts(c, bvh.child2, conts);
            }
        }

    }

}
