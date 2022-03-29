package de.hbib.brakingdistance.handler;

import de.hbib.brakingdistance.api.Car;
import de.hbib.brakingdistance.api.CarType;
import de.hbib.brakingdistance.util.MousePan;
import de.hbib.brakingdistance.util.MouseVecCreator;
import de.hbib.brakingdistance.util.Vec;
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
    private final JPanel canvas, main;

    private final int canvasX = 2300, canvasY = 1200,
                        wallX = 1100, wallY = 650;
    private final double sensitivity = 0.03;

    //pan amount and zooming scale
    private final Vec pan = new Vec();
    private double zoom=1;

    private final List<Car> cars = new ArrayList<>();

    private final MouseStateHandler mouseStateHandler = new MouseStateHandler();
    private final MousePan panState = new MousePan();
    private final MouseVecCreator vecState = new MouseVecCreator();

    // UI elements
    @Getter private final JSlider brakeSlider = new JSlider(JSlider.HORIZONTAL, 1, 5, 1);
    @Getter private final JLabel brakeLabel = new JLabel("Bremsstärke (Darstellung)");
    @Getter private final JLabel chooseCarLabel = new JLabel("Fahrzeugauswahl (Zufall)");
    @Getter private final JLabel generalLabel = new JLabel("Allgemein");
    @Getter private final JLabel speedIntensityLabel = new JLabel("Geschwindigkeitsintensität (Darstellung)");
    @Getter private final JSlider speedIntensitySlider  = new JSlider(JSlider.HORIZONTAL, 1, 20, 10);
    @Getter private final JButton brakeButton = new JButton("Bremsen");
    @Getter private final JButton resetButton = new JButton("Reset");
    @Getter private final JLabel brechtLabel = new JLabel("Hindernis");
    @Getter private final JCheckBox brechtBox = new JCheckBox("Bertolt Brecht", true);

    private Rectangle bertoltBrechtRect;

    public GuiHandler() throws IOException {
        instance = this;

        jFrame = new JFrame("Bremsweg Simulator (Q2-PH-GK-SEMR-2122 - Oualid Hbib)");
        jFrame.setVisible(true);
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        generalLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        brakeLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        chooseCarLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        speedIntensityLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        brechtLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 14));

        // canvas image
        canvasImage = jFrame.createImage(canvasX, canvasY);
        g2 = (Graphics2D)canvasImage.getGraphics();

        BufferedImage bertoltBrechtImage = ImageIO.read(getClass().getClassLoader().getResourceAsStream("bertoltbrecht.png"));
        this.bertoltBrechtRect = new Rectangle(wallX / 2, 50, bertoltBrechtImage.getWidth(), bertoltBrechtImage.getHeight());

        canvas = new JPanel(){
            public void paintComponent(Graphics g){
                // Clear image
                g2.setColor( new Color(0,0,0,255));
                g2.fillRect(0, 0, canvasX, canvasY);

                // Transform
                g2.translate(pan.x, pan.y);
                g2.scale(zoom, zoom);

                // Draw Brecht
                if(brechtBox.isSelected())
                    g2.drawImage(bertoltBrechtImage, bertoltBrechtRect.x, bertoltBrechtRect.y, null);

                // Draw cars
                for (Car car : cars) {
                    car.draw(g2);
                }

                g.drawImage(canvasImage, 0, 0, null);

                // Draw walls
                Graphics2D gg = (Graphics2D)g;
                gg.translate(pan.x, pan.y);
                gg.scale(zoom, zoom);
                gg.setColor(new Color(255, 255, 255));
                gg.drawRect(0, 0, wallX, wallY);
                gg.scale(1/zoom, 1/zoom);
                gg.translate(-pan.x, -pan.y);

                mouseStateHandler.drawStates(g);

                g2.scale(1/zoom, 1/zoom);
                g2.translate(-pan.x, -pan.y);
            }
        };

        main = new JPanel();
        GroupLayout gl = new GroupLayout(main);
        main.setLayout(gl);

        brakeSlider.setMajorTickSpacing(1);
        brakeSlider.setPaintTicks(true);
        brakeSlider.setPaintLabels(true);

        speedIntensitySlider.setMajorTickSpacing(3);
        speedIntensitySlider.setPaintTicks(true);
        speedIntensitySlider.setPaintLabels(true);

        brakeButton.addActionListener(arg0 -> cars.forEach(car -> car.setBrake(true)));

        resetButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent arg0) {
                cars.clear();
                zoom=1;
                centerView();
            }
        });

        // Add cars as checkboxes
        for (CarType value : CarType.values()) {
            carTypeJCheckBoxMap.put(value, new JCheckBox(value.getDisplayName(), true));
        }

        // Layout
        GroupLayout.ParallelGroup parallelGroup = gl.createParallelGroup(GroupLayout.Alignment.CENTER, false)
                .addComponent(generalLabel)
                .addComponent(resetButton)
                .addComponent(brakeLabel)
                .addComponent(brakeSlider)
                .addComponent(brakeButton)
                .addComponent(speedIntensityLabel)
                .addComponent(speedIntensitySlider)
                .addComponent(chooseCarLabel);

        GroupLayout.SequentialGroup sequentialGroup = gl.createSequentialGroup()
                .addComponent(generalLabel)
                .addComponent(resetButton)
                .addComponent(brakeLabel)
                .addComponent(brakeSlider)
                .addComponent(brakeButton)
                .addComponent(speedIntensityLabel)
                .addComponent(speedIntensitySlider)
                .addComponent(chooseCarLabel);

        this.carTypeJCheckBoxMap.forEach((carType, jCheckBox) -> {
            parallelGroup.addComponent(jCheckBox);
            sequentialGroup.addComponent(jCheckBox);
        });

        parallelGroup.addComponent(brechtLabel);
        parallelGroup.addComponent(brechtBox);
        sequentialGroup.addComponent(brechtLabel);
        sequentialGroup.addComponent(brechtBox);

        gl.setHorizontalGroup(
                gl.createSequentialGroup()
                        .addComponent(canvas,
                                0,
                                GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addGroup(parallelGroup)
        );

        gl.setVerticalGroup(
                gl.createParallelGroup()
                        .addComponent(canvas,
                                0,
                                GroupLayout.DEFAULT_SIZE,
                                Short.MAX_VALUE)
                        .addGroup(sequentialGroup)
        );
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);
        gl.setHonorsVisibility(false);

        jFrame.setContentPane(main);
        jFrame.setSize(wallX+240,wallY+70);

        panState.setPanVec(pan);
        mouseStateHandler.setLeftState(vecState);
        mouseStateHandler.setRightState(panState);

        MouseAdapter mouse = new MouseAdapter(){

            public void mouseClicked(MouseEvent e){
                mouseStateHandler.clickAction(e);
            }

            public void mousePressed(MouseEvent e){
                mouseStateHandler.pressAction(e);
            }

            public void mouseDragged(MouseEvent e){
                mouseStateHandler.dragAction(e);
                jFrame.repaint();
            }

            public void mouseReleased(MouseEvent e){
                mouseStateHandler.releaseAction(e);

                if(vecState.hasVec()){
                    Vec vel = vecState.getVec().scale(sensitivity/zoom);
                    Vec pos = vecState.getOrigin().plus(-pan.x, -pan.y).scaleV(1/zoom);
                    cars.add(new Car(getAvailableCarTypes().get(ThreadLocalRandom.current().nextInt(getAvailableCarTypes().size())), pos, vel, 10));
                }

                jFrame.repaint();
            }

            public void mouseMoved(MouseEvent e){
                mouseStateHandler.moveAction(e);
            }

            public void mouseWheelMoved(MouseWheelEvent e){
                double newZoom = zoom*(1-e.getPreciseWheelRotation()/10);
                zoom(newZoom, new Vec(e.getX(),e.getY()));
            }
        };

        canvas.addMouseListener(mouse);
        canvas.addMouseMotionListener(mouse);
        canvas.addMouseWheelListener(mouse);

        while(true){
            try {
                Thread.sleep(10);
                update();
                jFrame.repaint();
            } catch (ArrayIndexOutOfBoundsException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() throws ArrayIndexOutOfBoundsException{
        Car a,b;

        for(int i=0;i<cars.size();i++){
            if(this.checkRectCol(cars.get(i), this.bertoltBrechtRect) && brechtBox.isSelected()) {
                cars.get(i).getVel().scale(-1);
                cars.get(i).update();
                continue;
            }
            for(int j=0;j<cars.size();j++){
                if(i==j)continue;
                a = cars.get(i);
                b = cars.get(j);

                if(checkCollision(a,b)){
                    collide(a,b);
                }
            }

            checkWall(cars.get(i));

            cars.get(i).update();
        }
    }

    public boolean checkCollision(Car a, Car b){
        return a.getPos().minus(b.getPos()).mag()<a.getRadius()+b.getRadius();
    }

    public boolean checkRectCol(Car car, Rectangle rectangle) {
        return rectangle.contains(car.getPos().x, car.getPos().y);
    }

    public void collide(Car a, Car b){
        //get vectors
        Vec ua = a.getVel(), ub = b.getVel();
        Vec U = ub.minus(ua);
        Vec n = b.getPos().minus(a.getPos());
        if(n.mag()==0)return;
        n.scale(1/n.mag());

        //check if collision is proper
        if(U.dot(n)>=0){
            separate(a,b);
        }
    }

    public void separate(Car a, Car b){
        while(checkCollision(a,b)){
            Vec r = b.getPos().minus(a.getPos());
            r.scale(0.01/r.mag());
            a.getPos().subtract(r);
            b.getPos().add(r);
        }
    }

    public void checkWall(Car a){
        if(a.getPos().x<=a.getRadius()){
            a.getVel().x = -a.getVel().x;
            a.getPos().x=a.getRadius();
        }
        if(a.getPos().x>=wallX-a.getRadius()){
            a.getVel().x = -a.getVel().x;
            a.getPos().x=wallX-a.getRadius();
        }
        if(a.getPos().y<=a.getRadius()){
            a.getVel().y = -a.getVel().y;
            a.getPos().y=a.getRadius();
        }
        if(a.getPos().y>=wallY-a.getRadius()){
            a.getVel().y = -a.getVel().y;
            a.getPos().y=wallY-a.getRadius();
        }
    }

    public void zoom(double newZoom, Vec centre){
        centre.subtract(pan);
        pan.subtract(centre.scaleV(newZoom/zoom).minus(centre));
        zoom = newZoom;
        jFrame.repaint();
    }

    private List<CarType> getAvailableCarTypes() {
        return carTypeJCheckBoxMap.entrySet().stream()
                .filter(carTypeJCheckBoxEntry -> carTypeJCheckBoxEntry.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void centerView(){
        pan.x= canvas.getWidth()/2 - wallX * zoom / 2;
        pan.y= canvas.getHeight()/2 - wallY * zoom / 2;
        jFrame.repaint();
    }
}
