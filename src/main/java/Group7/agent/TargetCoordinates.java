package Group7.agent;


import Group7.agent.Intruder.Coordinate;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Point;

import java.util.ArrayList;
/*
Class to compute the target coordinates using 2 target's directions (at 2 different steps of the simulation)
 */

public class TargetCoordinates {

    /*
    Direction of the target at step 1
     */
    private Direction direction1;
    /*
    Direction of the target at step 2
     */
    private Direction direction2;
    /*
    Coordinates of the agent at step 1
     */
    private Coordinate point1;
    /*
    Coordinates of the agent at step 2
     */
    private Coordinate point2;
    /**
     * x and y coordinates of the agent at step 1
     */
    private double xCoord1;
    private double yCoord1;
    /**
     * x and y coordinates of the agent at step 2
     */
    private double xCoord2;
    private double yCoord2;
    /*
    angle of the agent's position - target line at step 1
     */
    private double angle1;
    /*
   angle of the agent's position - target line at step 2
    */
    private double angle2;
    /*
    parameters of the first line's equation (y = a^x + b)
     */
    private double a1;
    private double b1;
    /*
    parameters of the second line's equation (y = a^x + b)
     */
    private double a2;
    private double b2;
    /**
     * Coordinates of the target (computed by this class)
     */
    private double xCoordTarget;
    private double yCoordTarget;




    /**
     * Constructor
     * @param direction1 is the direction of the target at the 1st step
     * @param direction2 is the direction of the target at the 2nd step
     */
    public TargetCoordinates(Direction direction1, Direction direction2, Coordinate point1, Coordinate point2){
        this.direction1 = direction1;
        this.direction2 = direction2;
        this.point1 = point1;
        this.point2 = point2;
    }

    public Coordinate findIntersection(){
        angle1 = direction1.getDegrees(); // set the angle from the direction
        angle2 = direction2.getDegrees(); // set the angle from the direction

        xCoord1 = point1.getX(); // x and y coordinates of agent at step 1
        yCoord1 = point1.getY();

        xCoord2 = point2.getX();// x and y coordinates of agent at step 2
        yCoord2 = point2.getY();

        a1 = Math.tan(angle1);
        a2 = Math.tan(angle2);

        b1 = yCoord1 - a1*xCoord1;
        b2 = yCoord2 - a2*xCoord2;

        double x = -0.5;
        /*
        System.out.println("y1 = " + y1);
        System.out.println("y2 = " + y2);
         */


        double y1 = 2;
        double y2 = 1;

      //  while (y1 != y2){
        while (Math.abs((y1-y2)) > 0.1){
            //System.out.println("diff " + (y1-y2));
            x += 0.1;
            y1 = Math.tan(angle1)*(x-xCoord1) + yCoord1;
            /*
            System.out.println("a1 = " + a1);
            System.out.println("b1 = " + b1);
            System.out.println("x1 = " + x);
            System.out.println("y1 = " + y1);

             */
            y2 = Math.tan(angle2)*(x-xCoord2) + yCoord2;
            /*
            System.out.println("a2 = " + a2);
            System.out.println("b2 = " + b2);
            System.out.println("x = " + x);
            System.out.println("y2 = " + y2);

             */
        }
        //System.out.println("x = " + x);

        xCoordTarget = x;
        yCoordTarget =  Math.tan(angle1)*(x-xCoord1) + yCoord1;

        //System.out.println("y1 = " + y1);
        //System.out.println("y2 = " + y2);

      Coordinate targetCoordinates = new Coordinate(xCoordTarget,yCoordTarget);
        return targetCoordinates;
    }


    // Getters and setters
    public Coordinate getPoint1() {
        return point1;
    }

    public Coordinate getPoint2() {
        return point2;
    }

    public double getA1() {
        return a1;
    }

    public double getA2() {
        return a2;
    }

    public double getxCoordTarget() {
        return xCoordTarget;
    }

    public double getyCoordTarget() {
        return yCoordTarget;
    }

    public Direction getDirection1() {
        return direction1;
    }

    public Direction getDirection2() {
        return direction2;
    }

    public static void main(String[]args){
        Direction dir1 = Direction.fromDegrees(45);
        Direction dir2 = Direction.fromDegrees(40);
        Coordinate coord1 = new Coordinate(0,0);
        Coordinate coord2 = new Coordinate(30,30);

        TargetCoordinates test = new TargetCoordinates(dir1,dir2,coord1,coord2);
        Coordinate target = test.findIntersection();
        System.out.println("xCoord Target = " + target.getX());
        System.out.println("yCoord Target = " + target.getY());
    }
}

