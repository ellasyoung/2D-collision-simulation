import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;

public class Visualizer extends JPanel implements Runnable, MouseListener, MouseMotionListener, KeyListener, FocusListener {
    double startTime, currentTime, lastFrameTime, dt;
    boolean dragging;
    Vector2 screenDimensions = new Vector2();
    JFrame frame;

    Vector2 mousePos = new Vector2();
    Vector2 lastMousePos = new Vector2();
    Vector2 mouseVelocity = new Vector2();

    ArrayList<Circle> circles = new ArrayList<>();
    Circle grabbedCircle = null;
    BVH bvh;

    boolean bvhAccelerated = true;
    boolean simulate = true;

    public static void main(String[] args) {
        new Visualizer();
    }

    public Visualizer() {
        // setup jpanel
        this.setBackground(Color.black);
        this.setPreferredSize(new Dimension(800, 600));

        // setup frame
        frame = new JFrame ("Phys");
        frame.add(this, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(false);
        frame.pack();
        frame.setVisible(true);
        frame.setTitle("Phys");

        // add listeners
        addMouseMotionListener(this);
        addMouseListener(this);
        addKeyListener(this);
        setFocusable(true);

        // init start time
        this.startTime = (System.nanoTime() / 1_000_000_000.0);

        // init scene
        initScene();

        // run
        this.run();
    }

    // Initializes the scene with randomly placed circles.
    public void initScene() {
        this.circles = new ArrayList<>();
        for (int i=0; i<200; i++) {
            double x = ((Math.random()-0.5) * 0.8 + 0.5) * this.getWidth();
            double y = ((Math.random()-0.5) * 0.8 + 0.5) * this.getHeight();
            Circle circle = new Circle(new Vector2(x, y), 10);
            this.circles.add(circle);
        }
    }

    // game loop
    public void run() {
        while (true) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            repaint();
        }
    }

    // Handles the physics simulation for a single contact between two distinct circles
    // You are not responsible for understanding this method.
    // You should not modify this method.
    public void resolveCircleContact(ContactResult cr) {
        if (!simulate) {return;}
        Vector2 relativeVelocity = Vector2.sub(cr.a.velocity, cr.b.velocity);
        double normalVelocity = Vector2.dot(relativeVelocity, cr.contactNormal);

        if (normalVelocity > 0) {
            return;
        }

        double restitution = 0.99f;
        Vector2 impulse = Vector2.mul(cr.contactNormal, restitution * normalVelocity / 2f);
        cr.a.velocity = Vector2.sub(cr.a.velocity, impulse);
        cr.b.velocity = Vector2.add(cr.b.velocity, impulse);

        cr.a.position = Vector2.sub(cr.a.position, Vector2.mul(cr.contactNormal, 0.5f * cr.penetrationDepth));
        cr.b.position = Vector2.add(cr.b.position, Vector2.mul(cr.contactNormal, 0.5f * cr.penetrationDepth));
    }

    // Handles the physics simulation for a single contact between a circle and the boundaries of the level
    // You are not responsible for understanding this method.
    // You should not modify this method.
    public void resolveBoundaryContact(Circle c, Vector2 collisionNormal) {
        if (!simulate) {return;}
        double normalVelocity = Vector2.dot(c.velocity, collisionNormal);
        if (normalVelocity > 0) {
            return;
        }
        double restitution = 0.99f;
        Vector2 impulse = Vector2.mul(collisionNormal, restitution * normalVelocity * 2f);
        c.velocity = Vector2.sub(c.velocity, impulse);

        c.position.x = Math.min(Math.max(c.position.x, c.radius), screenDimensions.x-c.radius);
        c.position.y = Math.min(Math.max(c.position.y, c.radius), screenDimensions.y-c.radius);
    }

    // Handles a single tick/frame of the physics simulator.
    // If bvhAccelerated is set, circle/circle contacts will be found using getContactsBVH(), otherwise getContactsNaive().
    // Each circle/circle contact will then be resolved/simulated.
    // Each circle will then be contact checked against the 4 walls and all such contacts will be resolved/simulated.
    // We then step the simulation forward using numerical integration.
    // You are not responsible for understanding this method.
    // You should not modify this method.
    public void simulate() {
        HashSet<ContactResult> contacts = this.bvhAccelerated ? ContactFinder.getContactsBVH(circles, this.bvh) : ContactFinder.getContactsNaive(circles);
        if (contacts == null) {
            return;
        }

        // Resolve contacts with other circles
        for (ContactResult cr: contacts) {
            cr.a.color = new Color(255,0,127);
            cr.b.color = new Color(225,0,127);
            resolveCircleContact(cr);
        }

        // resolve contacts with walls;
        for (Circle c1 : circles) {
            if (c1.position.x - c1.radius < 0) {
                resolveBoundaryContact(c1, new Vector2(1, 0));
            }
            if (c1.position.x + c1.radius > screenDimensions.x) {
                resolveBoundaryContact(c1, new Vector2(-1, 0));
            }
            if (c1.position.y - c1.radius < 0) {
                resolveBoundaryContact(c1, new Vector2(0, 1));
            }
            if (c1.position.y + c1.radius > screenDimensions.y) {
                resolveBoundaryContact(c1, new Vector2(0, -1));
            }
        }

        // time integration
        for (Circle c : circles) {
            c.velocity = Vector2.mul(c.velocity, 0.99999f);
            c.position = Vector2.add(c.position, Vector2.mul(c.velocity, this.dt));
        }
    }

    @Override
    // Called every frame to update and render the scene.
    // You should not modify this method.
    public void paintComponent(Graphics g) {
        // clear frame
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        g2.setStroke(new BasicStroke(1));

        // handle resizing
        this.screenDimensions.x = this.getWidth();
        this.screenDimensions.y = this.getHeight();

        // update times
        this.lastFrameTime = currentTime;
        this.currentTime = (System.nanoTime() / 1_000_000_000.0) - this.startTime;
        this.dt = currentTime - lastFrameTime;

        // rebuild BVH every frame
        if (this.bvhAccelerated) {
            bvh = new BVH(circles);
//            System.out.println(Utils.area(bvh));
        }

        // draw circles
        for (Circle c : circles) {
            c.draw(g2);
            c.color = new Color(255,255,255);
        }

        // draw bvh
        if (bvh != null) {
            g2.setColor(new Color(191, 64, 191, 200));
            bvh.draw(g2);
        }

        // physics tick
        simulate();

        // exponential moving average for smooth mouse velocity
        double alpha = 1f - Math.exp(-this.dt / 0.1);
        Vector2 diff = Vector2.sub(mousePos, lastMousePos);
        this.mouseVelocity.x = alpha*diff.x + (1f-alpha)*this.mouseVelocity.x;
        this.mouseVelocity.y = alpha*diff.y + (1f-alpha)*this.mouseVelocity.y;

        // handle dragging/throwing circles
        if (grabbedCircle != null) {
            grabbedCircle.position = mousePos;
            grabbedCircle.velocity = Vector2.mul(this.mouseVelocity, 1f/this.dt);

            if (!dragging) {
                grabbedCircle = null;
            }
        }

        this.lastMousePos = mousePos;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.mousePos = new Vector2(e.getX(), e.getY());
        this.dragging = true;
        if (grabbedCircle == null) {
            for (Circle c : circles) {
                if (c.isInside(this.mousePos)) {
                    grabbedCircle = c;
                    break;
                }
            }
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        this.dragging = false;
    }
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {
        this.mousePos = new Vector2(e.getX(), e.getY());
        this.dragging = true;
        if (grabbedCircle == null) {
            for (Circle c : circles) {
                if (c.isInside(this.mousePos)) {
                    grabbedCircle = c;
                    break;
                }
            }
        }
    }
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {
        this.mousePos = new Vector2(e.getX(), e.getY());
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Regenerating scene");
        if (e.getKeyChar() == 'r') {
            initScene();
        }
    }
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void focusGained(FocusEvent e) {
        grabbedCircle = null;
    }
    @Override
    public void focusLost(FocusEvent e) {
        grabbedCircle = null;
    }
}
