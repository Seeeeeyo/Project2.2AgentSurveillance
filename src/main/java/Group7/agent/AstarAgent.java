 package Group7.agent;

import Group7.Game;
import Group7.agent.Intruder.MindMap;
import Group7.agent.Intruder.AsSearch;
import Interop.Action.*;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Percept.GuardPercepts;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Percepts;
import Interop.Percept.Scenario.SlowDownModifiers;
import Interop.Percept.Smell.SmellPercept;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Vision.ObjectPercept;

import java.util.ArrayList;
import java.util.Set;

 public class AstarAgent implements Intruder {

    private MindMap map = new MindMap();

    public static double getSpeedModifier(Percepts percepts)
    {
        SlowDownModifiers slowDownModifiers;
        if(percepts instanceof IntruderPercepts) {
            slowDownModifiers = ((IntruderPercepts) percepts).getScenarioIntruderPercepts().getScenarioPercepts().getSlowDownModifiers();
        }else{
            slowDownModifiers = ((GuardPercepts) percepts).getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers();
        }
        if(percepts.getAreaPercepts().isInWindow())
        {
            return slowDownModifiers.getInWindow();
        }
        else if(percepts.getAreaPercepts().isInSentryTower())
        {
            return slowDownModifiers.getInSentryTower();
        }
        else if(percepts.getAreaPercepts().isInDoor())
        {
            return slowDownModifiers.getInDoor();
        }

        return 1;
    }

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {
        System.out.println();
        System.out.println("AstarAgent.getAction");

        map.updateGridMap(percepts);
        map.computeTargetPoint(percepts.getTargetDirection());

//            finds the closest unvisited point to go visit it afterwards, returns the path to go to it
        ArrayList<Integer> listOfActions = AsSearch.computePath(map);
//        System.out.println("listOfActions = " + listOfActions);

        if(listOfActions == null){
//            the target is unreachable, the agent might be stuck in a room
            System.out.println("No path found ");
//            System.exit(1);
            return new NoAction();
        }
        else if(listOfActions.size() ==0){
            System.out.println("Target Reached");
//            System.exit(1);
            return new NoAction();
        }else {

            int next_move = listOfActions.get(0);

//        System.out.println("next_move = " + next_move);

            if (!percepts.wasLastActionExecuted()) {
                System.out.println("AstarAgent.getAction rejected");
//              rotate from a random angle
                Angle random_rotation_angle = Angle.fromRadians(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble());
                return new Rotate(random_rotation_angle);
            }

//            System.out.println("last action accepted");

//          chose an action to apply based on the a star move type required
            IntruderAction out_action = (IntruderAction)doAction(astarMove2Angle(next_move), map, percepts);

            //            finds the closest unvisited point to go visit it afterwards, returns the path to go to it
            map.updateState(out_action);
            return out_action;
        }
    }

    public static double astarMove2Angle(int move_nb){
        double angle = 0;
        //           gets the agent corresponding to the chosen move 1-4
        switch (move_nb){
            case 1:
//                go up in the grid
                angle = 180;
                break;
            case 2:
//                go down in the grid
                angle = 0;
                break;
            case 3:
//                go left in the grid
                angle = 90;
                break;
            case 4:
//                go right in the grid
                angle = 270;
                break;
        }
        return angle;
    }

    public static Action doAction(double goal_angle, MindMap map, Percepts percepts){
        double max_dist;
        Angle max_angle;
        if(percepts instanceof IntruderPercepts) {
        max_dist = ((IntruderPercepts) percepts).getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue()*getSpeedModifier(percepts);
        max_angle = ((IntruderPercepts) percepts).getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();
        }else{
            max_dist = ((GuardPercepts) percepts).getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue()*getSpeedModifier(percepts);
            max_angle = ((GuardPercepts) percepts).getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle();
        }
            double current_angle = map.getState().getAngle();

        if(current_angle == goal_angle){ // if the agent is in the good direction, move
            if(max_dist>1){ //ensure that the agent moves only from one case in the matrix
                max_dist=1;
            }
            return new Move(new Distance(max_dist));
        }else{ //otherwise, rotate to the good direction
            return rotateTo(goal_angle,max_angle, map);
        }
    }

    public static Rotate rotateTo(double goal_angle, Angle max_rotation, MindMap map){

        double old_angle = map.getState().getAngle();
        double rotation = goal_angle-old_angle;
        double rotation2 = goal_angle-360-old_angle;// anti-angle of the rotation angle

        Angle rotation_angle;
        //            checks which angle is the smallest and assign it to the rotation angle
        if(Math.abs(rotation)>=Math.abs(rotation2)){
            rotation_angle = Angle.fromDegrees(rotation2);
        }else{
            rotation_angle = Angle.fromDegrees(rotation);
        }

        //            checks if the angle is in the controller-acceptable range
        if(rotation_angle.getDegrees() > max_rotation.getDegrees()){
            rotation_angle =max_rotation;
        }else if(rotation_angle.getDegrees() < -max_rotation.getDegrees()){
            rotation_angle = max_rotation;
            rotation_angle = Angle.fromRadians(-rotation_angle.getRadians());
        }
//        System.out.println("Rotate to "+astar_angle+"; old angle is "+old_angle+"; new angle is "+(old_angle+rotation_angle.getDegrees()));

        return new Rotate(rotation_angle);
    }


}

