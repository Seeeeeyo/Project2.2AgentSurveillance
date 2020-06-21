package Group7.agent;

import Group7.Game;
import Interop.Action.*;
import Group7.agent.Intruder.MindMap;
import Interop.Agent.Guard;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Scenario.SlowDownModifiers;
import Interop.Percept.Smell.SmellPercept;
import Interop.Percept.Smell.SmellPerceptType;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;

import java.util.ArrayList;
import java.util.Set;

import static Group7.Game._RANDOM;

/**
 * sequence of update:
 *
 * 1:update current position, first is angle.
 * 2:update map.
 * 3:making decisions.
 *
 *
 * Pay attention that Math.sin using radians, not degree
 */

public class GreedyGuard implements Guard {

    public boolean debug = true;


    //matrix for the map, needed be updated every turn
    public MindMap map = new MindMap();

    private int trackSequence = 0;

    private int trackBuffer = 0;


    public boolean detectIntruderFirst = false;

    public boolean detectIntruderSecond = false;



    @Override
    public GuardAction getAction(GuardPercepts percepts) {
        System.out.println();
        System.out.println("Map_.getAction");

        Txt IO = new Txt();
        Built_In bi = new Built_In();

        Set<ObjectPercept> objectPercepts = percepts.getVision().getObjects().getAll();
        Set<SoundPercept> soundPercepts = percepts.getSounds().getAll();
        Set<SmellPercept> smellPercepts = percepts.getSmells().getAll();
        ArrayList<ObjectPercept> objectPerceptArrayList = new ArrayList<ObjectPercept>(objectPercepts);
        ArrayList<SoundPercept> soundPerceptArrayList = new ArrayList<SoundPercept>(soundPercepts);
        ArrayList<SmellPercept> smellPerceptArrayList = new ArrayList<>(smellPercepts);

        if(!percepts.wasLastActionExecuted())
        {
            System.out.println("Map_.getAction rejected");
           // System.exit(1);
            if(Math.random() < 0.1)
            {
                System.out.println("randomrandomrandom");
                return new DropPheromone(SmellPerceptType.values()[(int) (Math.random() * SmellPerceptType.values().length)]);
            }


            System.out.println("randomrandomrandom");
            Angle moveAngle = Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * _RANDOM.nextDouble());
            return new Rotate(moveAngle);
        }


        SlowDownModifiers slowDownModifiers =  percepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers();

        double modifier = 1;

        if (percepts.getAreaPercepts().isInWindow()){
            modifier = slowDownModifiers.getInWindow();
        }else if (percepts.getAreaPercepts().isInSentryTower()){
            modifier = slowDownModifiers.getInSentryTower();
        }else if (percepts.getAreaPercepts().isInDoor()){
            modifier = slowDownModifiers.getInDoor();
        }

        double epsilon = 0.3;

        if (Math.random()<epsilon){
            return new Move(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue()*modifier));
        }

        if (!detectIntruderFirst&&!detectIntruderSecond){
            if (bi.hasObjectInView(objectPerceptArrayList, ObjectPerceptType.Intruder)){
                Point point = bi.getIntruder(objectPerceptArrayList);


                IO.writePoint1(IO.point1,point);


                if (debug) System.out.println("---------------------------First found Intruder, stay to find--------------------------------");
                detectIntruderFirst = true;


                return new Yell();
            }
        }

        if (detectIntruderFirst&&!detectIntruderSecond){

            if (bi.hasObjectInView(objectPerceptArrayList, ObjectPerceptType.Intruder)){
                if (debug) System.out.println("---------------------------Second found Intruder, stay to find--------------------------------");
                detectIntruderSecond = true;

                Point point1 = IO.readPoint(IO.point1);
                Point point2 = bi.getIntruder(objectPerceptArrayList);

                if (possibleCapturePoints(predict(point1,point2,10)).size()!=0){
                    Point possibleCapturePoint = possibleCapturePoints(predict(point1,point2,10)).get(0);
                    System.out.println("The capture point is: --------------------");
                    printPointXY(possibleCapturePoint);
                    IO.writePoint1(IO.targetPoint,possibleCapturePoint);
                    IO.writeTime(IO.times,turnsRequired(possibleCapturePoint));

                    encapAction e = new encapAction(possibleCapturePoint);
                    if (debug) System.out.println(e.capture.get(trackSequence).getClass());


                    return e.capture.get(0);
                }

            }else {
                if (debug) System.out.println("fail to track, add buffer");
                detectIntruderSecond = false;
                addTrackBuffer(1);
            }

        }

        if (trackBuffer == 1){
            setTrackBuffer(0);
            detectIntruderFirst = false;
        }

        int times = IO.readTime(IO.times);

        if (detectIntruderFirst&&detectIntruderSecond&& trackSequence<times-2 && times!=0){

            if (debug) System.out.println("---------------------------Start Tracking--------------------------------");

            addTrackSequence(1);

            Point x = IO.readPoint(IO.targetPoint);

            encapAction e = new encapAction(x);

            if (debug) System.out.println("Track sequence is: "+trackSequence);

            if (debug) System.out.println(e.capture.get(trackSequence).getClass());

            return e.capture.get(trackSequence);

        }

        if (detectIntruderFirst&&detectIntruderSecond&&trackSequence==times-1){

            detectIntruderSecond=false;
            detectIntruderFirst=false;
            setTrackSequence(0);
        }



        Angle moveAngle = Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble());




//        Capturer cp = new Capturer();
//        Txt txt = new Txt();
        map.updateGridMap(percepts);
//        System.out.println();
//        System.out.println("after update: ");
//        MindMap.printMatrix(map.getMapData(),map.getTargetPos(),map.getState().getPos());

//        int times = txt.readTime(txt.times);
//
//        if (turns == 500){
//
//           // printTrajectory();
//            //printMemoryMap();
//            turns = 0;
//        }
//
//        if (rotateSequence == txt.readTime(txt.times)-1){
//            rotateFlag = false;
//            rotateSequence = -1;
//        }
//
//        if (rotateFlag && rotateSequence < txt.readTime(txt.times)-1){
//            rotateSequence = rotateSequence + 1;
//            int goalDirection = txt.readDirection(txt.goalDirection);
//            int direction = txt.readDirection(txt.direction);
//
//            encapAction e = new encapAction(direction,goalDirection);
//
//            if (times!=0){
//                return e.rotate.get(rotateSequence);
//            }else {
//                rotateFlag = false;
//                rotateSequence = -1;
//            }
//        }
//
//
//        if (!rotateFlag){
//            if (objectPerceptArrayList.size() == 0){
//                System.out.println("No object in view");
//            }else {
//                if (cp.needRotate(objectPerceptArrayList)){
//                    rotateSequence = rotateSequence + 1;
//
//                    rotateFlag = true; //need to rotate, then using evaluation function to evaluate 8 states:

                    int range = 10;

                   int goalSector = evaluationChoice(range); // greedy evaluation function
        System.out.println("goalSector = " + goalSector);
//                    txt.writeDirection(txt.direction,direction);//write down the direction need to rotate.
//                    txt.writeGoalDirection(txt.goalDirection,goalDirection);
//
//                    times = goalDirection - direction;
//
//                    if (times<-4){
//                        times = -(times+8);
//                    }else if (times>4){
//                        times = 8 - times;
//                    }
//
//                    txt.writeTime(txt.times,times);
//
//                    encapAction e = new encapAction(direction,goalDirection);
//
//                    if (times!=0){
//                        return e.rotate.get(rotateSequence);
//                    }else {
//                        rotateFlag = false;
//                        rotateSequence = -1;
//                        return new Rotate(Angle.fromDegrees(45));
//                    }
//
//                }
//            }
//        }else if (!percepts.wasLastActionExecuted()){
//            return new Rotate(Angle.fromDegrees(45));
//        }
//
//        else {
//            return new Move(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * modifier));
//        }

        GuardAction out;
        if(goalSector == getCurrentSector()){ // if the agent is in the good direction, move
            double max_dist = percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue()*modifier;
            if(max_dist>1){
                max_dist=1;
            }
            out =  new Move(new Distance(max_dist));
        }else{ //otherwise, rotate to the good direction
            out = rotateTo(getSectorAngle(goalSector),percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle());
        }
        map.updateState(out);
        return out;
    }

    private Rotate rotateTo(double angle, Angle max_rotation){

        double old_angle = map.getState().getAngle();
        Angle rotation_angle = Angle.fromDegrees(angle-old_angle);

        if(rotation_angle.getDegrees() > max_rotation.getDegrees()){
            rotation_angle =max_rotation;
        }else if(rotation_angle.getDegrees() < -max_rotation.getDegrees()){
            rotation_angle = max_rotation;
            rotation_angle = Angle.fromRadians(-rotation_angle.getRadians());
        }
        System.out.println("Rotate to "+angle+"; old angle is "+old_angle+"; new angle is "+(old_angle+rotation_angle.getDegrees()));

        return new Rotate(rotation_angle);
    }

    public int evaluationChoice(int range){

        double angleTo = 45;
        double angleFrom =0;
        int best_sector = 0;
        double best_sector_fitness = -Double.MAX_VALUE;

        for (int i = 0; i <8 ; i++) {

            ArrayList<Integer> sectorInfo = map.getSectorInfo(angleFrom,angleTo,range);
           double fitness_value = evaluationSector(sectorInfo);
            if(fitness_value>best_sector_fitness){
                best_sector = i;
                best_sector_fitness = fitness_value;
            }
            angleFrom+=45;
            angleTo+=45;
        }

        return best_sector;
    }


    //get evaluation reward for different kind of type.
    public double evaluationSector(ArrayList<Integer> info){

        double eva =0;

      for(Integer val : info){
                if (val == map.Wall){
                    eva -= 3;
                }else if (val == map.Shaded) {
                    eva -= 1;
                }else if (val == map.Sentry) {
                    eva += 0;
                }else if (val == map.Teleport) {
                    eva += 0;
                }else if (val == map.Window) {
                    eva += 1;
                }else if (val == map.Door) {
                    eva += 1;
                }else if (val == map.Empty) {
                    eva += 0;
                }else if (val == map.Unvisited) {
                    eva += 30;
                }

        }
        return eva;
    }


    public int getCurrentSector(){
       return getSector(map.getState().getAngle());
    }

    public int getSector(double angle){
            return (int) angle/45;
    }


    public double getSectorAngle(double sector) {
       return 45*sector+45/2;
    }

    public void printCurrentPosition(){
        System.out.println("------------------Currently the position of guard agent is---------------------");
        System.out.println("In row: "+ map.getState().getX());
        System.out.println("In column: "+map.getState().getY());
    }

    public void printCurrentAngle(){

        System.out.println("Current Angle is: "+map.getState().getAngle());
    }

    public boolean needRotate(ArrayList<ObjectPercept> objectPerceptArrayList){


        boolean flag = false;
        boolean val = false;


        for (int i = 0;i<objectPerceptArrayList.size();i++){

            double x = objectPerceptArrayList.get(i).getPoint().getX();
            double y = objectPerceptArrayList.get(i).getPoint().getY();

            //if the point is solid, then, input = 1;
            if (!flag&&objectPerceptArrayList.get(i).getType().isSolid() && Math.abs(x)<0.8){
                val = true;
                flag = true;
            }

        }

        return val;

    }





    public  ArrayList<Point> predict(Point a, Point b,int numP){
        ArrayList<Point> val = new ArrayList<>();

        Built_In bi = new Built_In();
        double slope = bi.findSlope(a,b);

        double ax = a.getX();
        double ay = a.getY();

        double bx = b.getX();
        double by = b.getY();

        double xD = bx-ax;
        double yD = by-ay;

        double currentX = bx;
        double currentY = by;

        for (int i = 0;i<numP;i++){

            val.add(new Point(currentX+(i+1)*xD,currentY+(i+1)*yD));
            //currentX = currentX+xD;
            //currentY = currentY+xD;
        }

        return val;
    }

    public  Point findCapturePoint(ArrayList<Point> pp){
        double[] distanceArray = new double[pp.size()];

        for (int i = 0;i<distanceArray.length;i++){
            distanceArray[i] = pp.get(i).getDistanceFromOrigin().getValue();
        }

        int index = 0;

        for (int i = 0;i<distanceArray.length;i++){

            if (distanceArray[i]<distanceArray[index]){
                index = i;
            }

        }

        return pp.get(index);


    }

    public  void printPointXY(Point x){
        System.out.println("X is: "+ x.getX());
        System.out.println("Y is: "+ x.getY());
        System.out.println("------------------------------");


    }


    public  void printArraylist(ArrayList<Point> pp){
        for (int i = 0;i<pp.size();i++){

            System.out.println("X id: "+pp.get(i).getX());
            System.out.println("Y is: "+pp.get(i).getY());
            System.out.println("----------------------------");

        }
    }


    public int turnsRequired(Point x){

        double degree = x.getClockDirection().getDegrees();

        if (degree>200) degree = degree - 360;

        double absDegree = Math.abs(degree);

        int rotateTimes = (int)Math.ceil((absDegree)/(45.0));

        // if (debug) System.out.println("Angle is:  ----"+absDegree);

        // if(debug) System.out.println("rotate time is: "+rotateTimes);

        int moveTimes = (int)Math.ceil((x.getDistanceFromOrigin().getValue())/(1.40));

        //if (debug) System.out.println("Distance is:  ----"+x.getDistanceFromOrigin().getValue());

        //if(debug) System.out.println("move time is: "+moveTimes);

        return rotateTimes+moveTimes;
    }

    public  ArrayList<Point> possibleCapturePoints(ArrayList<Point> points){
        int[] turnsRequire = new int[points.size()];

        for (int i = 0;i<turnsRequire.length;i++){

            turnsRequire[i] = turnsRequired(points.get(i));

        }


        ArrayList<Point> val = new ArrayList<>();

        for (int i = 0;i<turnsRequire.length;i++){

            if (turnsRequire[i]<=(i+1)){
                val.add(points.get(i));
            }

        }


        return val;


    }

    public void setTrackSequence(int val){
        trackSequence = val;
    }

    public void addTrackSequence(int val){
        trackSequence = trackSequence + val;
    }

    public int getTrackSequence(){
        return  trackSequence;
    }

    public void setTrackBuffer(int val){
        trackBuffer = val;
    }

    public void addTrackBuffer(int val)
    {
        trackBuffer = trackBuffer + val;

    }

    public int getTrackBuffer(){
        return trackBuffer;
    }

    public void printActionList(ArrayList<GuardAction> list){
        for (int i = 0;i< list.size();i++){
            System.out.println("The action is: "+list.get(i).getClass());
        }

    }

    public static void main(String[] args) {
        Point p1 = new Point(0,-1.4);
        Point p2 = new Point( 0,1.3);



    }

//    /**
//     * debug method to see the action
//     */
//    public void printAction(){
//
//        System.out.println("------------------The action you are checking is:---------------------");
//        switch (type){
//            case 1: System.out.println("Action is Move, with value:  "+val);
//            break;
//
//            case 2: System.out.println("Action is Rotate, with value:  "+val);
//            break;
//
//            case 3: System.out.println("Action is NoAction, with value: "+val);
//            break;
//
//            case 4: System.out.println("Action is Yell, with value: "+val);
//            break;
//
//            case 5: System.out.println("Action is Drop_Pheromone, with Pheromone Type:  "+val);
//            break;
//        }
//
//
//    }


}


