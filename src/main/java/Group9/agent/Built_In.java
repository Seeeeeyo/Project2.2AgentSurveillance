package Group9.agent;
import Group9.Game;
import Interop.Action.*;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.GuardPercepts;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Scenario.SlowDownModifiers;
import Interop.Percept.Smell.SmellPercept;
import Interop.Percept.Smell.SmellPerceptType;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Sound.SoundPerceptType;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;

import java.util.ArrayList;
import java.util.Set;


public class Built_In {


    public boolean hasObjectInView(ArrayList<ObjectPercept> ls, ObjectPerceptType objectPerceptType){
        boolean val = false;

        for (int i = 0;i<ls.size();i++){

            if (ls.get(i).getType().equals(objectPerceptType)){
                val = true;
            }

        }

        return val;

    }


    public void printFieldOfView(ArrayList<ObjectPercept> ls){
        for (int i = 0;i<ls.size();i++){

            System.out.println(ls.get(i).getType());
            break;
        }

    }

    public boolean hasDetectYell(ArrayList<SoundPercept> soundPerceptArrayList, SoundPerceptType soundPerceptType){
        boolean val = false;
        for (int i = 0;i<soundPerceptArrayList.size();i++){

            if (soundPerceptArrayList.get(i).getType().equals(soundPerceptType)){
                val = true;
            }

        }

        return val;

    }

    public Direction getYellDirection(ArrayList<SoundPercept> soundPerceptArrayList){
        //first find Yell
        boolean hasFind = false;

        int indexOfYell = -1;

        for (int i = 0;i<soundPerceptArrayList.size();i++){

            if (!hasFind && soundPerceptArrayList.get(i).getType().equals(SoundPerceptType.Yell)){
                indexOfYell = i;
                hasFind = true;
            }

        }

        return soundPerceptArrayList.get(indexOfYell).getDirection();
    }

    /**
     * get direction of a point with specific type
     * Useful for intruder, teleport, sentry tower. Not good for wall
     * @param objectPerceptArrayList the list of objects in field of view
     * @param objectPerceptType object type
     * @return clock direction of the object.
     */
    public Direction getObjectDirection(ArrayList<ObjectPercept> objectPerceptArrayList,ObjectPerceptType objectPerceptType){
        //first find Yell
        boolean hasFind = false;

        int index = -1;

        for (int i = 0;i<objectPerceptArrayList.size();i++){

            if (!hasFind && objectPerceptArrayList.get(i).getType().equals(objectPerceptType)){
                index = i;
                hasFind = true;
            }

        }

        return objectPerceptArrayList.get(index).getPoint().getClockDirection();
    }

    /**
     * check whether specific type is in field of view.
     * @param objectPerceptArrayList
     * @param objectPerceptType
     * @return if there exist input object type in field of view.
     */
    public boolean hasDetectObject(ArrayList<ObjectPercept> objectPerceptArrayList, ObjectPerceptType objectPerceptType){
        boolean val = false;
        for (int i = 0;i<objectPerceptArrayList.size();i++){

            if (objectPerceptArrayList.get(i).getType().equals(objectPerceptType)){
                val = true;
            }

        }

        return val;

    }

    public boolean hasDetectSmell(ArrayList<SmellPercept> objectPerceptArrayList, SmellPerceptType smellPerceptType){
        boolean val = false;
        for (int i = 0;i<objectPerceptArrayList.size();i++){

            if (objectPerceptArrayList.get(i).getType().equals(smellPerceptType)){
                val = true;
            }

        }

        return val;

    }

    public Distance distanceToObject(ArrayList<ObjectPercept> objectPerceptArrayList, ObjectPerceptType objectPerceptType){

        Distance val = new Distance(0);

        boolean hasFind = false;

        int index = -1;

        for (int i = 0;i<objectPerceptArrayList.size();i++){

            if (!hasFind && objectPerceptArrayList.get(i).getType().equals(objectPerceptType)){
                index = i;
                hasFind = true;
            }

        }

        return objectPerceptArrayList.get(index).getPoint().getDistanceFromOrigin();

    }

    public ArrayList<Point> getObjectList(ArrayList<ObjectPercept> objectPerceptArrayList, ObjectPerceptType objectPerceptType){
        ArrayList<Point> returnList = new ArrayList<>();

        for (int i = 0;i<objectPerceptArrayList.size();i++){
            if (objectPerceptArrayList.get(i).getType().equals(objectPerceptType)){
                returnList.add(objectPerceptArrayList.get(i).getPoint());
            }
        }

        return returnList;
    }

    public Point[] findNearAndFarPoint(ArrayList<ObjectPercept> objectPerceptArrayList) {

        ArrayList<Point> targetPoint = getObjectList(objectPerceptArrayList,ObjectPerceptType.TargetArea);

        Point nearPoint = targetPoint.get(0);
        Point farPoint = targetPoint.get(0);
        Distance curr = new Distance(0);

        for (int i = 1;i<targetPoint.size();i++){
            if (targetPoint.get(i).getDistanceFromOrigin().getValue()>farPoint.getDistanceFromOrigin().getValue()){
                farPoint = targetPoint.get(i);
            }
        }

        for (int i = 1;i<targetPoint.size();i++){
            if (targetPoint.get(i).getDistanceFromOrigin().getValue()<nearPoint.getDistanceFromOrigin().getValue()){
                nearPoint = targetPoint.get(i);
            }
        }

        Point[] val = new Point[2];
        val[0] = nearPoint;
        val[1] = farPoint;

        return val;
    }

    public double findSlope(Point shortP, Point farP) {
        double slope = 0;


        double xF = farP.getX();
        double yF = farP.getY();

        double xS = shortP.getX();
        double yS = shortP.getY();

        slope = (yF - yS) / (xF - xS);

        return slope;
    }


}

class TypeOfAction {

    private double val;
    private int actionType;//rotate, move
    private int type;


    public TypeOfAction( double val, int type){
        this.type = type;

        this.val = val;

    }


    public int getType(){
        return type;
    }

    public void setType(int val){
        type = val;
    }

    public double getVal(){
        return val;
    }

    public int getActionType(){
        return actionType;
    }

}

class encapAction {

    public ArrayList<GuardAction> avoid;
    public ArrayList<GuardAction> targetPatrol;

    public boolean debug = false;

    public encapAction(){
        initialAvoid();
        initialPatrol(18);


    }

    public void initialAvoid(){
        avoid = new ArrayList<>();

        avoid.add(new Rotate(Angle.fromDegrees(45)));
        avoid.add(new Rotate(Angle.fromDegrees(45)));
        avoid.add(new Move(new Distance(1.4)));
        avoid.add(new Move(new Distance(1.4)));
        avoid.add(new Move(new Distance(1.4)));
        avoid.add(new Move(new Distance(1.4)));
    }

    public void initialPatrol(double length){
        targetPatrol = new ArrayList<>();
        int step = (int) (length/1.4)+1;
        if (debug) System.out.println(step);
        for (int i = 0;i<step;i++){
            targetPatrol.add(new Move(new Distance(1.4)));
        }

        for (int i = 0;i<2;i++){
            targetPatrol.add(new Rotate(Angle.fromDegrees(-45)));
        }

        targetPatrol.add(new DropPheromone(SmellPerceptType.Pheromone2));
    }

    public static void main(String[] args) {


        encapAction e = new encapAction();

        for (int i = 0;i<e.targetPatrol.size();i++){
            System.out.println(e.targetPatrol.get(i).getClass());
        }
    }



}