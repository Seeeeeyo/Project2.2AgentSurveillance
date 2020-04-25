package Group9.agent.Intruder;

import java.util.*;

import Interop.Action.*;
import Interop.Agent.Guard;
import Interop.Geometry.*;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.Scenario.GameMode;
import Interop.Percept.Scenario.ScenarioGuardPercepts;
import Interop.Percept.Scenario.ScenarioPercepts;
import Interop.Percept.Scenario.SlowDownModifiers;
import Interop.Percept.Smell.SmellPercept;
import Interop.Percept.Smell.SmellPercepts;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Sound.SoundPercepts;
import Interop.Percept.Vision.*;
import Interop.Percept.*;


/**
 *
 * @author luotianchen
 * In phase one, the main task for the agent is exploring the map and record the information of the map
 * So, my idea is using grid-based (with scale = 10 )Map in the first phase.
 *
 *
 * The advantages are : 1: Easier for agent to store the information and update.
 * 						2: In our project, there are only few objects, which is not a very complicated world, Grid-based Map is enough to store information.
 * 						3: Easier to implement and maintain
 * 						4: Since all the walls are either vertical or horizontal, matrix is enough to store all the information
 *
 * Disadvantages:       1:Less accuracy
 * 						2:Need more space
 * 						3:Agents are less sensitive to the shape of objects, but in project, all objects are either rectangle or circle. Not like real world.
 *
 * use prime numbers to represents the objects (easier for future calculation) :
 * 0:  unknown place
 * 13: wall
 * 23: teleport
 * 37: window
 * 47: door
 * 59: sentry tower
 * -1: empty space
 * 67: target place.
 * 79: shaded area
 * 83: intruder
 * 97: guard
 * 101: itself
 *
 *
 *
 * The idea for an agent to by pass a wall and explore the whole wall (similar for door and window)
 * Find the nearest point and far point,  calculate the slope of it, since solid objects are consecutively.
 * then, move the agent to the point near the wall within 2.296, and rotate to be the same slope with the object
 * Move until all visual intersection point of wall's distance to agent is less than view range, means it already explore
 * one side of the wall, then rotate 90 degree to find other side.
 *
 * Since it is an exploring agent, try to test the function of exploring the map, so, don't enter any sentry tower and
 * teleport, focusing on exploring the map.
 *
 * So, it will choose to avoid entering them. but not shaded area.
 *
 *
 *for agent: it get the percepts from the game controller
 * then, the agent return an action to the gamecontroller
 * game controller will check the action is allow or not,
 * if is allow, update the world
 * not allow, just keep everything remain.
 * and then give the new percept to agent.
 *
 *
 * moveHistory[][] is an matrix, with 3 colomn,
 * First element: type. 1 is move, 2 is rotate,
 * Second element: value: 2 type, move distance, rotate angle
 * Third element: action type: 1 is normal exploring 2: avoid rotate 3: adjust back rotate 4:appraoch rotate
 * each raw represent an action
 *
 *
 *--------------------------------------
 * currently, simulate a fieldOfView, the agent can return an action successfully.
 * And update the map
 *
 */

public class ExpAgent implements Guard {
    //with range = 6
    final public double viewingWidth = 2.296;

    //with range = 6
    final public double viewingLength = 6;

    public List<TypeOfAction> moveHistory;

    public int mapWidth = 80;

    public int mapLength = 120;

    final public int scale = 1;



    final public double unknownPlace = 0;

   /* final public double wall = 13;

    final public double teleport = 23;

    final public double window = 37;

    final public double door = 47;

    final public double sentryTower = 59;

    final public double emptySpace = -1;*/

    //even not useful for guard agent
    final public double targetPlace = 67;

    final public double shadedArea = 79;

    final public double intruder = 83;

    final public double guard = 97;

    final public double itself = 101;

    final public double boundary = 107;

    //sequence of action executed
    private int actionSequence = 1;

    //current rotate angle compare to the initial state -- degree
    private double rotateAngle = 0;

    //check if there is a wall needed to explore completely
    public boolean wallNeedExplore = false;

    //check if there is a door needed to explore completely
    public boolean doorNeedExplore = false;

    //check if there is a window needed to explore completely
    public boolean windowNeedExplore = false;

    //check if there is a shadedArea needed to explore completely
    public boolean shadeAreaNeedExplore = false;

    //check if there is a sentryTower needed to explore completely
    public boolean sentryTowerNeedExplore = false;

    //check if there is a teleport needed to explore completely
    public boolean teleportNeedExplore = false;

    private MindMap map;

    //whether the agent faces a boundary
    public boolean touchedBoundary = false;

    public static void main(String[] args) {
        Point p1 = new Point(1, 5-1.4);
        Point p7 = new Point(0, 5-1.4);
        Point p8 = new Point(-1, 5-1.4);

        Point p2 = new Point(2, 5);
        Point p3 = new Point(1, 3);
        Point p4 = new Point(0.3, 2);
//		Point p5 = new Point(0.2, 3);
        Point p6 = new Point(0.25, 4);
        Set<ObjectPercept> objectPercepts = new HashSet<ObjectPercept>();
        Set<SoundPercept> sp = new HashSet<SoundPercept>();
        Set<SmellPercept> smp = new HashSet<SmellPercept>();
        SmellPercept smellPercept = new SmellPercept(null,null);

        ObjectPercept ob1 = new ObjectPercept(ObjectPerceptType.Wall, p1);
        ObjectPercept ob7 = new ObjectPercept(ObjectPerceptType.Wall, p7);
        ObjectPercept ob8 = new ObjectPercept(ObjectPerceptType.Wall, p8);
        ObjectPercept ob2 = new ObjectPercept(ObjectPerceptType.Intruder, p2);
        ObjectPercept ob3 = new ObjectPercept(ObjectPerceptType.Teleport, p3);
        ObjectPercept ob4 = new ObjectPercept(ObjectPerceptType.Door, p4);
//		ObjectPercept ob5 = new ObjectPercept(ObjectPerceptType.EmptySpace, p5);
        ObjectPercept ob6 = new ObjectPercept(ObjectPerceptType.SentryTower, p6);

        objectPercepts.add(ob1);
        objectPercepts.add(ob2);
//		objectPercepts.add(ob3);
        objectPercepts.add(ob4);
//		objectPercepts.add(ob5);
        objectPercepts.add(ob6);
        objectPercepts.add(ob7);
        objectPercepts.add(ob8);

        GameMode gameMode = GameMode.CaptureAllIntruders;
        Distance captureDistance = new Distance(0.5);
        Angle maxRotationAnlge = Angle.fromDegrees(45);
        SlowDownModifiers slowDownModifiers = new SlowDownModifiers(1,1,1);
        Distance radiusPheromone = new Distance(3);
        int pheromoneCooldown = 0;


        Distance range = new Distance(6);
        Angle viewAnlge = Angle.fromDegrees(45);

        FieldOfView fieldOfView = new FieldOfView(range,viewAnlge);
        ObjectPercepts ob = new ObjectPercepts(objectPercepts);

        VisionPrecepts visionPrecepts = new VisionPrecepts(fieldOfView,ob);
        SoundPercepts soundPercepts = new SoundPercepts(sp);
        AreaPercepts areaPercepts = new AreaPercepts(false,false,false,false);

        SmellPercepts smellPercepts = new SmellPercepts(smp);
        ScenarioPercepts scenarioPercepts = new ScenarioPercepts(gameMode,captureDistance,maxRotationAnlge,slowDownModifiers,radiusPheromone,pheromoneCooldown);
        ScenarioGuardPercepts scenarioGuardPercepts = new ScenarioGuardPercepts(scenarioPercepts,new Distance(1.4));
        GuardPercepts guardPercepts = new GuardPercepts(visionPrecepts,soundPercepts,smellPercepts,areaPercepts,scenarioGuardPercepts,true);

        ExpAgent exp = new ExpAgent();

        System.out.println("The agent will return :  "+exp.getAction(guardPercepts).getClass());


        if (!exp.map.isGridMapEmpty()) {
            exp.map.printGridMap();
        }


    }

    //constructor
    public ExpAgent() {

        moveHistory = new ArrayList();

        moveHistory.add(new TypeOfAction(1,1,1));

        map = new MindMap();
    }



    @java.lang.Override
    public GuardAction getAction(GuardPercepts percepts) {
        //First, acquire all the information from this turn.

        //move distance
        Distance maxMoveDistance = new Distance(1.4);


        // Area information
        boolean isInDoor = percepts.getAreaPercepts().isInDoor();
        boolean isInSentryTower = percepts.getAreaPercepts().isInSentryTower();
        boolean isInWindow = percepts.getAreaPercepts().isInWindow();
        boolean isJustTeleported = percepts.getAreaPercepts().isJustTeleported();

        //get all the Objects with types and localization.
        Set<ObjectPercept> objectPercepts = percepts.getVision().getObjects().getAll();

        //change the set to list, imo list is more convenient
        List<ObjectPercept> ls = new ArrayList<ObjectPercept>(objectPercepts);


        //get view range
        Distance viewRange = percepts.getVision().getFieldOfView().getRange();

        //get view angle
        Angle viewAngle = percepts.getVision().getFieldOfView().getViewAngle();


        //get all smell with types
        percepts.getSmells().getAll();

        //get all sound with types
        percepts.getSounds().getAll();

        //the max distance that the agent can move every turn.
        percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard();

        //Get data from the scenrio
        Distance captureDistance = percepts.getScenarioGuardPercepts().getScenarioPercepts().getCaptureDistance();
        GameMode gameMode = percepts.getScenarioGuardPercepts().getScenarioPercepts().getGameMode();
        Angle maxRotationAngle = percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle();
        double inDoorSlowDownModifier = percepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers().getInDoor();
        double inWindowSlowDownModifier = percepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers().getInWindow();
        double inSenreyTowerSlowDownModifier = percepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers().getInSentryTower();
        Distance radiusPheromone = percepts.getScenarioGuardPercepts().getScenarioPercepts().getRadiusPheromone();
        int pheromoneCoolDown = percepts.getScenarioGuardPercepts().getScenarioPercepts().getPheromoneCooldown();

        Iterator<ObjectPercept> iterator = objectPercepts.iterator();


        //----------------------------if front is already explored-------------------------------------

        if (alreadyExplored()){

            return new Rotate(Angle.fromDegrees(45));

        }


        //----------------------------update the map based on what agent perceived now--------------------------------------

//		updateXY();
        map.updateGridMap(percepts);

        //------------------------------------------------------------------

        //if nothing is in the front, just move in, keep exploring
        if (ls.size() == 0){
            return new Move(maxMoveDistance);
        }



//		if(isSafe) return new Move(maxMoveDistance);

        //rotate back to continue

        //if last action is rotate to avoid, then, this action is move to avoid,
        if (justAvoidLastAction()&&!hasObstacleOnWay(objectPercepts)){

            moveHistory.add(new TypeOfAction(1,maxMoveDistance.getValue(),2));
            return new Move(maxMoveDistance);
        }
        //if last action is move to avoid, this action is rotate back.
        else if (needRotateBack()){
            moveHistory.add(new TypeOfAction(2,-moveHistory.get(moveHistory.size()-2).getVal(),3));
            return new Rotate(Angle.fromDegrees(-moveHistory.get(moveHistory.size()-2).getVal()));
        }







        //----------------------------decide need to avoid enter somewhere or not--------------------------------------

        //if there is teleport, sentryTower and this is the first avoid action, then rotate to avoid.
        if (hasObstacleOnWay(objectPercepts) && isFirstAvoidAction()){

            moveHistory.add(new TypeOfAction(2,maxRotationAngle.getDegrees(),2));
            return new Rotate(maxRotationAngle);

        }

        //if it is not possible, then what it is in front of the agent, if it is a wall, then explore the wall completely
        // in order to explore the wall, first get close to the wall at distance within viewing width. so, the first thing
        //to do is calculate the distance

        LinkedList<Distance> distanceWallList = new LinkedList<Distance>();




        //initial point, if there is no wall point, then these two point will be useless but no extra bad influence
        Point shortestPoint = findFirstWallPoint(objectPercepts);
        Point farawayPoint = findFirstWallPoint(objectPercepts);

        //find the far and short point.
        for (int i = 0;i<ls.size();i++){
            if (ls.get(i).getType().equals(ObjectPerceptType.Wall)){

                Distance distance = ls.get(i).getPoint().getDistanceFromOrigin();

                if (distance.getValue()<shortestPoint.getDistanceFromOrigin().getValue()){
                    shortestPoint = ls.get(i).getPoint();
                }else if (distance.getValue()>farawayPoint.getDistanceFromOrigin().getValue()){
                    farawayPoint = ls.get(i).getPoint();
                }

                distanceWallList.add(distance);
            }
        }

        //find slope
        double slope = findSlope(shortestPoint,farawayPoint);

        //TODO: calculate the difference angle between agent direction and slope.
        double degree = Math.toDegrees(Math.atan(slope));



        //get the distance between agent and middle point of wall
        Distance targetDistance = findMiddlePoint(objectPercepts).getDistanceFromOrigin();

        //if the distance between agent and point is greater than 2.4 and the wall need to be explored, then move towards
        //TODO: wallNeedExplore need be defined before

        if (!(degree >= 89.99)) {
            if (hasMiddelWallPoint(objectPercepts) && targetDistance.getValue()
                    >= viewingWidth + maxMoveDistance.getValue() && wallNeedExplore && moveIsSafe(objectPercepts)) {

                //normal explore move
                moveHistory.add(new TypeOfAction(1,maxMoveDistance.getValue(),1));
                return new Move(maxMoveDistance);
            }
            //if the wall has no point in the middle, then rotate to approach.
            else if (!hasMiddelWallPoint(objectPercepts) && wallNeedExplore && shortestPoint.getClockDirection().getDegrees() > 270) {

                //approach rotate
                moveHistory.add(new TypeOfAction(2,changeFromClockAngleToAntiClock(shortestPoint.getClockDirection()).getDegrees(),4));
                return new Rotate(changeFromClockAngleToAntiClock(shortestPoint.getClockDirection()));
            }
        }


//-----------------------------------------otherwise, rotate--------------------------------------

        return new Rotate(Angle.fromDegrees(rotateAngle(degree)));


    }


    //check if last action is rotate to avoid entering some actions.
    public boolean justAvoidLastAction(){
        int size = moveHistory.size();

        if (moveHistory.get(size-1).getActionType() == 2 &&moveHistory.get(size-1).getType() == 2){
            return true;
        }else{
            return false;
        }

    }



    //check if need rotate back
    public boolean needRotateBack(){
        int size = moveHistory.size();

        if (moveHistory.get(size-1).getActionType() == 1 && moveHistory.get(size-1).getType() == 2
                &&moveHistory.get(size -2).getActionType() == 2 && moveHistory.get(size-2).getType() == 2){
            return true;
        }else {
            return false;
        }

    }



    public boolean isFirstAvoidAction(){
        int size = moveHistory.size();

        if (moveHistory.get(size-1).getType() == 1){
            return true;
        }else {
            return  false;
        }

    }

    //if in the way of moving there is no solid object.
    public boolean moveIsSafe(Set<ObjectPercept> objectPercepts){
        boolean safe = true;
        List<ObjectPercept> ls = new ArrayList<ObjectPercept>(objectPercepts);

        for (int i = 0;i<ls.size();i++){
            if (ls.get(i).getType().isSolid()){
                if (ls.get(i).getPoint().getX()<0.5 && ls.get(i).getPoint().getX()>-0.5 && ls.get(i).getPoint().getY() < 1.4){
                    safe = false;
                }
            }
        }


        return safe;
    }


    public boolean hasObstacleOnWay(Set<ObjectPercept> objectPercepts) {
        boolean has = false;
        List<ObjectPercept> ls = new ArrayList<ObjectPercept>(objectPercepts);

        for (int i = 0;i<ls.size();i++){
            if (ls.get(i).getType().equals(ObjectPerceptType.SentryTower) || ls.get(i).getType().equals(ObjectPerceptType.Teleport)){
                if (ls.get(i).getPoint().getX()<0.5 && ls.get(i).getPoint().getX()>-0.5 && ls.get(i).getPoint().getY() < 1.4){
                    has = true;
                }
            }
        }

        return has;
    }


    public Angle changeFromClockAngleToAntiClock(Angle angle){

        return Angle.fromDegrees(-(360-angle.getDegrees()));


    }


    public double rotateAngle(double degree){
        double val = 0;

        //since max rotate angle is 45
        if (degree>=0 && degree<=45){
            val = 45;
        }else if (degree >=45 && degree<=90){
            val =  90 - degree;
        }else if (degree >= -90 && degree <= -45){
            val = -90 - degree;
        }else if (degree >= -45 && degree <= 0){
            val = -45;
        }

        return val;
    }

    public Point findFirstWallPoint(Set<ObjectPercept> objectPercepts){
        List<ObjectPercept> ls = new ArrayList<ObjectPercept>(objectPercepts);
        Point point;
        for (int i = 0;i<ls.size();i++){
            if (ls.get(i).getType().equals(ObjectPerceptType.Wall)){
                point = ls.get(i).getPoint();
                return point;
            }
        }

        return new Point(0,0);
    }

    public boolean hasMiddelWallPoint(Set<ObjectPercept> objectPercepts){
        boolean val = false;

        List<ObjectPercept> ls = new ArrayList<ObjectPercept>(objectPercepts);

        for (int i = 0;i<ls.size();i++){
            if (ls.get(i).getType().equals(ObjectPerceptType.Wall)){

                if (ls.get(i).getPoint().getClockDirection().getDegrees()==0){
                    return true;
                }

            }
        }

        return val;
    }


    public Point findMiddlePoint(Set<ObjectPercept> objectPercepts){
        Point point = new Point(0,0);
        boolean find = false;
        List<ObjectPercept> ls = new ArrayList<ObjectPercept>(objectPercepts);

        for (int i = 0;i<ls.size();i++){
            if (ls.get(i).getType().equals(ObjectPerceptType.Wall)){

                if (ls.get(i).getPoint().getClockDirection().getDegrees()>=-5 && ls.get(i).getPoint().getClockDirection().getDegrees()<=5){
                    point = ls.get(i).getPoint();
                }

            }
        }


        //if there is no object in the field of view, return the agent self location

        return point;
    }


    public double findSlope(Point shortP, Point farP){
        double slope = 0;


        double xF = farP.getX();
        double yF = farP.getY();

        double xS = shortP.getX();
        double yS = shortP.getY();

        slope = (yF-yS)/(xF-xS);

        return slope;
    }

    //TODO
    public boolean decideIfTheWallIsBoundry(){
        boolean val = false;

        return val;
    }



    public boolean alreadyExplored(){
        boolean val = false;

        int  x =  map.state.getX();

        int  y =  map.state.getY();

        double sum1 = 0;


        double sumOfRotation = 0;

        for (int i = 0;i<moveHistory.size();i++){

            //if the action is rotation
            if (moveHistory.get(i).getActionType() == 2){
                sumOfRotation = sumOfRotation + moveHistory.get(i).getVal();
            }
        }

        for (int i = (int)x-5;i<x;i++){
            for (int j = (int)y-2;j<y+2;j++){

               // sum1 = stateSituation[i][j] + sum1;

            }
        }

        if (sum1 >100){
            val = true;
        }


        return val;

    }




    //------- getter and setter and debug functions-------------------

    public int getSequence() {
        return actionSequence;
    }

    public void addSequence() {
        actionSequence = actionSequence + 1;
    }

    public void setSequence(int value) {
        actionSequence = value;
    }

    public void setRotateAngle(double angle) {
        rotateAngle = angle;
    }

    public double getRotateAngle() {
        return rotateAngle;
    }


}




class Test{






}
