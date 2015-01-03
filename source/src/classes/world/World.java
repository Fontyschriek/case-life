package classes.world;

import classes.Exceptions.LocationAlreadyOccupiedException;

import classes.enumerations.Digestion;
import classes.enumerations.LocationType;
import classes.interfaces.ISimulate;
import classes.life.Animal;
import classes.life.Genetics;
import classes.life.Life;
import classes.life.Plant;
import javafx.scene.canvas.*;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class World implements Serializable, ISimulate {

    private ArrayList<Life> life;

    private Node[][] nodes;

    private int width;
    private int height;

    private World(Node[][] nodes) {
        life = new ArrayList();

        this.nodes = nodes;

        width = nodes.length;
        height = nodes[0].length;

        for (int x = 0; x < nodes.length; x++) {
            for (int y =0; y < nodes[x].length; y++) {
                Node node = nodes[x][y];

                node.addAdjacentNode(getNode(x - 1, y + 1));
                node.addAdjacentNode(getNode(x, y + 1));
                node.addAdjacentNode(getNode(x + 1, y + 1));

                node.addAdjacentNode(getNode(x - 1,y ));
                node.addAdjacentNode(getNode(x + 1, y));

                node.addAdjacentNode(getNode(x - 1,y - 1));
                node.addAdjacentNode(getNode(x,y - 1));
                node.addAdjacentNode(getNode(x + 1,y - 1));
            }
        }

    }

    public static World instantiateWorldFromImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Node[][] nodes = new Node[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                java.awt.Color color = new java.awt.Color(image.getRGB(x, y));
                String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());

                LocationType type;
                if (hex.equals("#000000")) {
                    type = LocationType.Water;
                } else if (hex.equals("#ed1c1c")) {
                    type = LocationType.Obstacle;
                } else {
                    type = LocationType.Land;
                }

                nodes[x][y] = new Node(x, y, type);
            }
        }

        World world = new World(nodes);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                java.awt.Color color = new java.awt.Color(image.getRGB(x, y));
                String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());

                Life life = null;
                if (hex.equals("#00ff00")) {
                    life = new Plant(world, 100);
                } else if (hex.equals("#ff6a00")) {
                    life = new Animal(world, new Genetics(Digestion.Carnivore, 4, 50, 100, 100));
                } else if (hex.equals("#0026ff")) {
                    life = new Animal(world, new Genetics(Digestion.Omnivore, 4, 50, 100, 100));
                } else if (hex.equals("#ff00ff")) {
                  life = new Animal(world, new Genetics(Digestion.Herbivore, 4, 50, 100, 100));
                }

                if (life != null) {
                    try {
                        world.addLife(life, nodes[x][y]);
                    }
                    catch (LocationAlreadyOccupiedException exception) {

                    }
                }
            }
        }

        return world;
    }

    public Node getNode(int x, int y) {
        x = x % width;
        y = y % height;

        while (x < 0) {
            x += width;
        }

        while (y < 0) {
            y += height;
        }

        return nodes[x][y];
    }

    public Node[][] getNodes() {
        return nodes;
    }

    public ArrayList<Life> getLife() {
        return life;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Node getNodeForLife(Life life) {
        for (int x = 0; x < nodes.length; x++) {
            for (int y = 0; y < nodes[x].length; y++) {
                Node node = nodes[x][y];
                Life holder = node.getHolder();
                if (holder != null && holder.equals(life)) {
                    return node;
                }
            }
        }

        return null;
    }

    public void addLife(Life life, Node node) throws LocationAlreadyOccupiedException {
        this.life.add(life);
        node.setHolder(life);
    }

    public void simulate() {
        for (Life life : this.life) {
            life.simulate();
        }
    }

    public double getDiagonalDistance(Node origin, Node target) {
        int width = Math.abs(origin.getX() - target.getX()) ;
        int height = Math.abs(origin.getY() - target.getY());

        return Math.max(width, height);
    }

    public void removeLife(Life life) {
        Node node = getNodeForLife(life);
        node.unsetHolder();
        this.life.remove(life);
    }

    public void draw(GraphicsContext context) {

//        try {
//            InputStream str = new FileInputStream("resources/maps/Darwin map(empty).png");
//            Image img = new Image(str);
//            context.drawImage(img, 0, 0, 1000, 1000);
//        } catch(Exception ex) {
//
//        }
//            Image img = new Image("resources/maps/small.png");



        for (int x = 0; x < nodes.length; x++) {
            for (int y = 0; y < nodes[x].length; y++) {
                Node node = nodes[x][y];

                if (node.getLocationType().equals(LocationType.Land)) {
                    context.setFill(javafx.scene.paint.Color.WHITE);
                } else if (node.getLocationType().equals(LocationType.Obstacle)) {
                    context.setFill(javafx.scene.paint.Color.LIGHTGRAY);
                } else {
                    context.setFill(javafx.scene.paint.Color.LIGHTBLUE);
                }

                context.fillRect(node.getX(), node.getY(), 1, 1);
            }
        }
    }

}
