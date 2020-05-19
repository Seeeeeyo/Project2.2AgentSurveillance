package Group9.agent;

import Group9.Game;
import Group9.map.objects.TargetArea;
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
import Interop.Utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class Rule_Based_Guard implements Guard {

    public boolean debug = true;//turn on if need to see debug message

    public double a = 1;

    Built_In ov = new Built_In();

    private boolean foundIntruder = false;

    private boolean foundTargetArea = false;

    private boolean analyzeTarget = false;

    private boolean analyzeSentry = false;

    public ArrayList<TypeOfAction> moveHistory = new ArrayList<TypeOfAction>();

    public double[] selfLocation = new double[2];

    public int mapWidth = 80;

    public int mapLength = 120;

    protected double[][] stateSituation = new double[mapLength][mapWidth];

    final public int scale = 1;

    // agent's initial coordinate
    public int initialY = (mapWidth / 2) * scale;

    public int initialX = (mapLength / 2) * scale;

    final public double itself = 101;

    final public double unknownPlace = 0;

    final public double Exp = 1;

    final public double teleport = 23;

    final public double window = 37;

    final public double door = 47;

    final public double sentryTower = 59;

    final public double emptySpace = 1;

    //even not useful for guard agent
    final public double targetPlace = 67;

    final public double shadedArea = 79;

    final public double intruder = 83;

    final public double guard = 1;

    private int avoidSequence = -1;

    private int patrolSequence = -1;

    private boolean avoidFlag = false;

    public boolean drop1 = false;

    public int dropCoolDown = 0;

    public boolean adjustDirection = false;

    public double sumOfRotation = 0;

    public double lastMoveDistance = 0;

    @Override
    public GuardAction getAction(GuardPercepts percepts) {



        Set<ObjectPercept> objectPercepts = percepts.getVision().getObjects().getAll();
        Set<SoundPercept> soundPercepts = percepts.getSounds().getAll();
        Set<SmellPercept> smellPercepts = percepts.getSmells().getAll();
        //change the set to list, imo list is more convenient
        ArrayList<ObjectPercept> objectPerceptArrayList = new ArrayList<ObjectPercept>(objectPercepts);
        ArrayList<SoundPercept> soundPerceptArrayList = new ArrayList<SoundPercept>(soundPercepts);
        ArrayList<SmellPercept> smellPerceptArrayList = new ArrayList<>(smellPercepts);
        encapAction e = new encapAction();
        //updateXY();
        //updateGridMap(objectPerceptArrayList);
        //printMoveHistory();
        //printMemoryMap();

        SlowDownModifiers slowDownModifiers =  percepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers();
        double modifier = 1;
        if (percepts.getAreaPercepts().isInWindow()){
            modifier = slowDownModifiers.getInWindow();
        }else if (percepts.getAreaPercepts().isInSentryTower()){
            modifier = slowDownModifiers.getInSentryTower();
        }else if (percepts.getAreaPercepts().isInDoor()){
            modifier = slowDownModifiers.getInDoor();
        }


        //-----------if find target area-------------

        if (ov.hasObjectInView(objectPerceptArrayList, ObjectPerceptType.TargetArea)){

            if (!foundTargetArea) setFoundTargetArea(true);

            if(debug) System.out.println("----------FindFindTarget--------");

        }

        if (foundTargetArea && !adjustDirection){
            Point[] nearAndFar = ov.findNearAndFarPoint(objectPerceptArrayList);
            double slope = ov.findSlope(nearAndFar[0],nearAndFar[1]);
            System.out.println("slope is: "+ slope);
            double degree = Math.toDegrees(Math.atan(slope));
            if (slope<0){
                degree = -degree;
            }
                adjustDirection = true;

            if (Utils.isRealNumber(degree)) {
                System.out.println("Adjusted-----"+degree+" degree");
                return new Rotate(Angle.fromDegrees(degree));
            }
        }

        if (adjustDirection){
            if (patrolSequence == e.targetPatrol.size()-1){
               setPatrolSequence(-1);

            }

            if (patrolSequence<e.targetPatrol.size()-1){
                System.out.println("----------Begin execute patrol----------");
                addPatrolSequence(1);
                return e.targetPatrol.get(patrolSequence);
            }



        }

        //--------------- patrol------------------






        //----------sentry tower avoid if smell pheromones----------
        if (ov.hasDetectSmell(smellPerceptArrayList,SmellPerceptType.Pheromone1) && !percepts.getAreaPercepts().isInSentryTower()) {
            //System.out.println("Smell the pheromone");
            if (ov.hasObjectInView(objectPerceptArrayList, ObjectPerceptType.SentryTower)) {
                double sentryD = ov.getObjectDirection(objectPerceptArrayList, ObjectPerceptType.SentryTower).getDegrees();
                if (sentryD < 40 || sentryD > 320) {
                    System.out.println("-------Need to avoid------");
                    setAvoidFlag();
                }
            }
        }
        if (avoidFlag){

            if (avoidSequence == e.avoid.size()-1){
                System.out.println("-----stop aviod----");
                setAvoidFlag(); // close avoid
                setAvoidSequence(0);

            }

            if (avoidSequence<e.avoid.size()-1){
                System.out.println("----------Begin execute avoid----------");
                addAvoidSequence();
                return e.avoid.get(avoidSequence);
            }

        }

        //-------when smell pheromone1 , avoid the sentry tower------

        //If already inside the sentry tower, then keep patrolling inside
        if (percepts.getAreaPercepts().isInSentryTower()){
            if (drop1 && dropCoolDown == percepts.getScenarioGuardPercepts().getScenarioPercepts().getPheromoneCooldown()){
                //System.out.println("-----Drop again-----");
                dropCoolDown = 0;
                return new DropPheromone(SmellPerceptType.Pheromone1);
            }


            if (!drop1){
                drop1 = true; // if haven't drop, then drop
                //System.out.println("-----Drop-----");
                return new DropPheromone(SmellPerceptType.Pheromone1);
            }else {
                dropCoolDown++;
                return new Rotate(Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians())); // if already drop, patrol inside the sentry
            }
        }





       // If detect yell, then focus its direction
        if (ov.hasDetectYell(soundPerceptArrayList, SoundPerceptType.Yell)){

            Direction yellDirection = ov.getYellDirection(soundPerceptArrayList);

            if(debug)System.out.println("Degree is: "+yellDirection.getDegrees());

            if(debug)System.out.println("-----Detect yell------");

            return new NoAction();

        }


        //IF guard found intruder in the field of view, then Yell.
        //If this is the first time it found intruder.
        if (!isFoundIntruder() && ov.hasObjectInView(objectPerceptArrayList, ObjectPerceptType.Intruder)){
            setFoundIntruder(true);

            if(debug)System.out.println("----------FindFindIntruder--------");
            return new Yell();

        }

        if ( ov.hasObjectInView(objectPerceptArrayList, ObjectPerceptType.Intruder)){

            if (debug) System.out.println("----- Begin to approach-----");
            Direction intruderDirection = ov.getObjectDirection(objectPerceptArrayList,ObjectPerceptType.Intruder);
            if (debug)System.out.println("Current degree is ------------  "+intruderDirection.getDegrees());

            //Distance to the intruder:
            Distance toIntruder = ov.distanceToObject(objectPerceptArrayList,ObjectPerceptType.Intruder);

            double safeScale = 1.5;
            if (toIntruder.getValue()<safeScale*percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue()){
                if (intruderDirection.getDegrees() > 300 ) {

                    if (debug) System.out.println("The angle is : "+ intruderDirection.getDegrees());
                    if (debug) System.out.println("----- rotate negative to approach-----"+Angle.fromDegrees(intruderDirection.getDegrees()-360).getDegrees());
                    return new Rotate(Angle.fromDegrees(intruderDirection.getDegrees()-360));
                }else{
                    if (debug) System.out.println("----- rotate positive to approach-----"+intruderDirection.getDegrees());
                    return new Rotate(intruderDirection);
                }

            }else {

                if (intruderDirection.getDegrees() < 2 || intruderDirection.getDegrees() > 358) {
                    if (debug) System.out.println("----- Move to approach-----");
                    return new Move(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * modifier));

                } else {

                    //calculate the anti-clock degree
                    if (intruderDirection.getDegrees() > 300) {

                        if (debug) System.out.println("----- rotate negative to approach-----"+Angle.fromDegrees(intruderDirection.getDegrees() - 360).getDegrees());
                        return new Rotate(Angle.fromDegrees(intruderDirection.getDegrees() - 360));
                    } else {
                        if (debug) System.out.println("----- rotate positive to approach-----"+intruderDirection.getDegrees());
                        return new Rotate(intruderDirection);
                    }

                }
            }


        }

        if (debug) System.out.println("------No catch action happen-------");


        //if inside sentry tower, then rotate to look around
        if (percepts.getAreaPercepts().isInSentryTower()){
            a++;
           // System.out.println(a);
            moveHistory.add(new TypeOfAction(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees(),2));
            return new Rotate(Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians()));
        }



        if(!percepts.wasLastActionExecuted())
        {
            moveHistory.add(new TypeOfAction(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees(),2));
            return new Rotate(Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians()*(Math.random()*2-0.3)));
        }
        else
        {
            moveHistory.add(new TypeOfAction(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue(),1));
            return new Move(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * modifier));
        }
    }



    public void printMoveHistory(){
        for (int i = 0;i<moveHistory.size();i++){
            System.out.println(moveHistory.get(i).getType());
        }

    }

    /**
     * find the location of the agent.
     *
     * @param state
     * @return the x-y coordinate of the agent
     */
    public int[] findSelfLocation(double[][] state) {
        int[] location = new int[2];

        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                if (state[i][j] == itself) {
                    location[0] = i;
                    location[1] = j;
                }
            }
        }

        return location;

    }

    public Angle changeFromClockAngleToAntiClock(Angle angle) {

        return Angle.fromDegrees(-(360 - angle.getDegrees()));


    }

    public void printMemoryMap(){
        for (int i = 0;i<stateSituation.length;i++){
            for(int j = 0;j<stateSituation[0].length;j++){

                System.out.print(stateSituation[i][j]+" ");


            }
            System.out.println();
        }


    }



    //------------------------getter setter------------------------

    public boolean isFoundIntruder() {
        return foundIntruder;
    }

    public void setFoundIntruder(boolean foundIntruder) {
        this.foundIntruder = foundIntruder;
    }

    public void setFoundTargetArea(boolean foundTargetArea){
        this.foundTargetArea = foundTargetArea;
    }

    public boolean isFoundTargetArea() {
        return foundTargetArea;
    }

    public void addAvoidSequence(){
        avoidSequence++;
    }

    public void setAvoidSequence(int val){
        avoidSequence = val;
    }

    public int getAvoidSequence(){
        return  avoidSequence;
    }

    public void setAvoidFlag(){
        avoidFlag  = !avoidFlag;
    }

    public boolean getAvoidFlag(){
        return avoidFlag;
    }

    public void addPatrolSequence(int val){
        patrolSequence = patrolSequence + val;
    }

    public int getPatrolSequence(){
        return patrolSequence;
    }

    public void setPatrolSequence(int val){
        patrolSequence = val;
    }
}
