package de.hbib.brakingdistance.handler;

import de.hbib.brakingdistance.api.Car;
import de.hbib.brakingdistance.api.CarType;
import de.hbib.brakingdistance.util.MousePan;
import de.hbib.brakingdistance.util.MouseVecCreator;
import de.hbib.brakingdistance.util.Vector;
import lombok.Getter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * @author Oualid Hbib
 */
public class GuiHandler {

    @Getter private static GuiHandler instance;

    private final Map<CarType, JCheckBox> carTypeJCheckBoxMap = new LinkedHashMap<>();

    private final JFrame jFrame;
    private final Image canvasImage;
    private final Graphics2D g2;
    private final JPanel canvas;

    private final int canvasX = 2300, canvasY = 1200,
            wallX = 1100, wallY = 650;
    private final double sensitivity = 0.03;

    //pan amount and zooming scale
    private final Vector pan = new Vector();
    private double zoom = 1;

    private final List<Car> cars = new ArrayList<>();

    private final MouseStateHandler mouseStateHandler = new MouseStateHandler();
    private final MouseVecCreator vecState = new MouseVecCreator();

    // UI elements
    @Getter private final JSlider brakeSlider = new JSlider(JSlider.HORIZONTAL, 1, 5, 1);
    @Getter private final JLabel brakeLabel = new JLabel("Bremsstärke (Darstellung)");
    @Getter private final JLabel chooseCarLabel = new JLabel("Fahrzeugauswahl (Zufall)");
    @Getter private final JLabel generalLabel = new JLabel("Allgemein");
    @Getter private final JLabel speedIntensityLabel = new JLabel("Geschwindigkeitsintensität (Darstellung)");
    @Getter private final JSlider speedIntensitySlider = new JSlider(JSlider.HORIZONTAL, 1, 20, 10);
    @Getter private final JButton brakeButton = new JButton("Bremsen");
    @Getter private final JButton resetButton = new JButton("Reset");
    @Getter private final JLabel brechtLabel = new JLabel("Hindernis");
    @Getter private final JCheckBox brechtBox = new JCheckBox("Bertolt Brecht", true);

    private final Rectangle bertoltBrechtRect;

    public GuiHandler() throws IOException {
        instance = this;

        this.jFrame = new JFrame("Bremsweg Simulator (Q2-PH-GK-SEMR-2122 - Oualid Hbib)");
        this.jFrame.setVisible(true);
        this.jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                System.exit(0);
            }
        });

        this.generalLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        this.brakeLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        this.chooseCarLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        this.speedIntensityLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        this.brechtLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 14));

        // canvas image
        this.canvasImage = this.jFrame.createImage(this.canvasX, this.canvasY);
        this.g2 = (Graphics2D) this.canvasImage.getGraphics();

        final BufferedImage bertoltBrechtImage = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("bertoltbrecht.png"));
        this.bertoltBrechtRect = new Rectangle(this.wallX / 2, 50, bertoltBrechtImage.getWidth(), bertoltBrechtImage.getHeight());

        this.canvas = new JPanel() {
            @Override
            public void paintComponent(final Graphics g) {
                // Clear image
                GuiHandler.this.g2.setColor(new Color(0, 0, 0, 255));
                GuiHandler.this.g2.fillRect(0, 0, GuiHandler.this.canvasX, GuiHandler.this.canvasY);

                // Transform
                GuiHandler.this.g2.translate(GuiHandler.this.pan.x, GuiHandler.this.pan.y);
                GuiHandler.this.g2.scale(GuiHandler.this.zoom, GuiHandler.this.zoom);

                // Draw Brecht
                if (GuiHandler.this.brechtBox.isSelected())
                    GuiHandler.this.g2.drawImage(bertoltBrechtImage, GuiHandler.this.bertoltBrechtRect.x, GuiHandler.this.bertoltBrechtRect.y, null);

                // Draw cars
                for (final Car car : GuiHandler.this.cars) {
                    car.draw(GuiHandler.this.g2);
                }

                g.drawImage(GuiHandler.this.canvasImage, 0, 0, null);

                // Draw walls
                final Graphics2D gg = (Graphics2D) g;
                gg.translate(GuiHandler.this.pan.x, GuiHandler.this.pan.y);
                gg.scale(GuiHandler.this.zoom, GuiHandler.this.zoom);
                gg.setColor(new Color(255, 255, 255));
                gg.drawRect(0, 0, GuiHandler.this.wallX, GuiHandler.this.wallY);
                gg.scale(1 / GuiHandler.this.zoom, 1 / GuiHandler.this.zoom);
                gg.translate(-GuiHandler.this.pan.x, -GuiHandler.this.pan.y);

                GuiHandler.this.mouseStateHandler.drawStates(g);

                GuiHandler.this.g2.scale(1 / GuiHandler.this.zoom, 1 / GuiHandler.this.zoom);
                GuiHandler.this.g2.translate(-GuiHandler.this.pan.x, -GuiHandler.this.pan.y);
            }
        };

        JPanel main = new JPanel();
        final GroupLayout gl = new GroupLayout(main);
        main.setLayout(gl);

        this.brakeSlider.setMajorTickSpacing(1);
        this.brakeSlider.setPaintTicks(true);
        this.brakeSlider.setPaintLabels(true);

        this.speedIntensitySlider.setMajorTickSpacing(3);
        this.speedIntensitySlider.setPaintTicks(true);
        this.speedIntensitySlider.setPaintLabels(true);

        this.brakeButton.addActionListener(arg0 -> this.cars.forEach(car -> car.setBrake(true)));

        this.resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                GuiHandler.this.cars.clear();
                GuiHandler.this.zoom = 1;
                GuiHandler.this.centerView();
            }
        });

        // Add cars as checkboxes
        for (final CarType value : CarType.values()) {
            this.carTypeJCheckBoxMap.put(value, new JCheckBox(value.getDisplayName(), true));
        }

        // Layout
        final GroupLayout.ParallelGroup parallelGroup = gl.createParallelGroup(GroupLayout.Alignment.CENTER, false)
                .addComponent(this.generalLabel)
                .addComponent(this.resetButton)
                .addComponent(this.brakeLabel)
                .addComponent(this.brakeSlider)
                .addComponent(this.brakeButton)
                .addComponent(this.speedIntensityLabel)
                .addComponent(this.speedIntensitySlider)
                .addComponent(this.chooseCarLabel);

        final GroupLayout.SequentialGroup sequentialGroup = gl.createSequentialGroup()
                .addComponent(this.generalLabel)
                .addComponent(this.resetButton)
                .addComponent(this.brakeLabel)
                .addComponent(this.brakeSlider)
                .addComponent(this.brakeButton)
                .addComponent(this.speedIntensityLabel)
                .addComponent(this.speedIntensitySlider)
                .addComponent(this.chooseCarLabel);

        this.carTypeJCheckBoxMap.forEach((carType, jCheckBox) -> {
            parallelGroup.addComponent(jCheckBox);
            sequentialGroup.addComponent(jCheckBox);
        });

        parallelGroup.addComponent(this.brechtLabel);
        parallelGroup.addComponent(this.brechtBox);
        sequentialGroup.addComponent(this.brechtLabel);
        sequentialGroup.addComponent(this.brechtBox);

        gl.setHorizontalGroup(
                gl.createSequentialGroup()
                        .addComponent(this.canvas,
                                0,
                                GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addGroup(parallelGroup)
        );

        gl.setVerticalGroup(
                gl.createParallelGroup()
                        .addComponent(this.canvas,
                                0,
                                GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addGroup(sequentialGroup)
        );
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);
        gl.setHonorsVisibility(false);

        this.jFrame.setContentPane(main);
        this.jFrame.setSize(this.wallX + 240, this.wallY + 70);

        MousePan panState = new MousePan();
        panState.setPanVec(this.pan);
        this.mouseStateHandler.setLeftState(this.vecState);
        this.mouseStateHandler.setRightState(panState);

        final MouseAdapter mouse = new MouseAdapter() {

            @Override
            public void mouseClicked(final MouseEvent e) {
                GuiHandler.this.mouseStateHandler.clickAction(e);
            }

            @Override
            public void mousePressed(final MouseEvent e) {
                GuiHandler.this.mouseStateHandler.pressAction(e);
            }

            @Override
            public void mouseDragged(final MouseEvent e) {
                GuiHandler.this.mouseStateHandler.dragAction(e);
                GuiHandler.this.jFrame.repaint();
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                GuiHandler.this.mouseStateHandler.releaseAction(e);

                if (GuiHandler.this.vecState.hasVec()) {
                    final Vector vel = GuiHandler.this.vecState.getVec().scale(GuiHandler.this.sensitivity / GuiHandler.this.zoom);
                    final Vector pos = GuiHandler.this.vecState.getOrigin().plus(-GuiHandler.this.pan.x, -GuiHandler.this.pan.y).scaleV(1 / GuiHandler.this.zoom);
                    GuiHandler.this.cars.add(new Car(GuiHandler.this.getAvailableCarTypes().get(ThreadLocalRandom.current().nextInt(GuiHandler.this.getAvailableCarTypes().size())), pos, vel, 10));
                }

                GuiHandler.this.jFrame.repaint();
            }

            @Override
            public void mouseMoved(final MouseEvent e) {
                GuiHandler.this.mouseStateHandler.moveAction(e);
            }

            @Override
            public void mouseWheelMoved(final MouseWheelEvent e) {
                final double newZoom = GuiHandler.this.zoom * (1 - e.getPreciseWheelRotation() / 10);
                GuiHandler.this.zoom(newZoom, new Vector(e.getX(), e.getY()));
            }
        };

        this.canvas.addMouseListener(mouse);
        this.canvas.addMouseMotionListener(mouse);
        this.canvas.addMouseWheelListener(mouse);

        while (true) {
            try {
                Thread.sleep(10);
                this.update();
                this.jFrame.repaint();
            } catch (final ArrayIndexOutOfBoundsException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() throws ArrayIndexOutOfBoundsException {
        Car a, b;

        for (int i = 0; i < this.cars.size(); i++) {
            if (this.checkRectCol(this.cars.get(i), this.bertoltBrechtRect) && this.brechtBox.isSelected()) {
                this.cars.get(i).getVel().scale(-1);
                this.cars.get(i).update();
                continue;
            }
            for (int j = 0; j < this.cars.size(); j++) {
                if (i == j) continue;
                a = this.cars.get(i);
                b = this.cars.get(j);

                if (this.checkCollision(a, b)) {
                    this.collide(a, b);
                }
            }

            this.checkWall(this.cars.get(i));

            this.cars.get(i).update();
        }
    }

    public boolean checkCollision(final Car a, final Car b) {
        return a.getPos().minus(b.getPos()).mag() < a.getRadius() + b.getRadius();
    }

    public boolean checkRectCol(final Car car, final Rectangle rectangle) {
        return rectangle.contains(car.getPos().x, car.getPos().y);
    }

    public void collide(final Car a, final Car b) {
        //get vectors
        final Vector ua = a.getVel();
        final Vector ub = b.getVel();
        final Vector U = ub.minus(ua);
        final Vector n = b.getPos().minus(a.getPos());
        if (n.mag() == 0) return;
        n.scale(1 / n.mag());

        //check if collision is proper
        if (U.dot(n) >= 0) {
            this.separate(a, b);
        }
    }

    public void separate(final Car a, final Car b) {
        while (this.checkCollision(a, b)) {
            final Vector r = b.getPos().minus(a.getPos());
            r.scale(0.01 / r.mag());
            a.getPos().subtract(r);
            b.getPos().add(r);
        }
    }

    public void checkWall(final Car a) {
        if (a.getPos().x <= a.getRadius()) {
            a.getVel().x = -a.getVel().x;
            a.getPos().x = a.getRadius();
        }
        if (a.getPos().x >= this.wallX - a.getRadius()) {
            a.getVel().x = -a.getVel().x;
            a.getPos().x = this.wallX - a.getRadius();
        }
        if (a.getPos().y <= a.getRadius()) {
            a.getVel().y = -a.getVel().y;
            a.getPos().y = a.getRadius();
        }
        if (a.getPos().y >= this.wallY - a.getRadius()) {
            a.getVel().y = -a.getVel().y;
            a.getPos().y = this.wallY - a.getRadius();
        }
    }

    public void zoom(final double newZoom, final Vector centre) {
        centre.subtract(this.pan);
        this.pan.subtract(centre.scaleV(newZoom / this.zoom).minus(centre));
        this.zoom = newZoom;
        this.jFrame.repaint();
    }

    private List<CarType> getAvailableCarTypes() {
        return this.carTypeJCheckBoxMap.entrySet().stream()
                .filter(carTypeJCheckBoxEntry -> carTypeJCheckBoxEntry.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void centerView() {
        this.pan.x = this.canvas.getWidth() / 2 - this.wallX * this.zoom / 2;
        this.pan.y = this.canvas.getHeight() / 2 - this.wallY * this.zoom / 2;
        this.jFrame.repaint();
    }
}
