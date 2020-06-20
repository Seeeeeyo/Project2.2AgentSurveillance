 package Group7.agent;

import Group7.Game;
import Group7.agent.Intruder.MindMap;
import Group7.agent.Intruder.AsSearch;
import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.NoAction;
import Interop.Action.Rotate;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Scenario.SlowDownModifiers;
import Interop.Percept.Smell.SmellPercept;
import Interop.Percept.Sound.SoundPercept;
import Interop.Percept.Vision.ObjectPercept;

import java.util.ArrayList;
import java.util.Set;

 public class AstarAgent implements Intruder {

    private MindMap map = new MindMap();

    private double getSpeedModifier(IntruderPercepts guardPercepts)
    {
        SlowDownModifiers slowDownModifiers =  guardPercepts.getScenarioIntruderPercepts().getScenarioPercepts().getSlowDownModifiers();
        if(guardPercepts.getAreaPercepts().isInWindow())
        {
            return slowDownModifiers.getInWindow();
        }
        else if(guardPercepts.getAreaPercepts().isInSentryTower())
        {
            return slowDownModifiers.getInSentryTower();
        }
        else if(guardPercepts.getAreaPercepts().isInDoor())
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

        Direction target_direction = percepts.getTargetDirection();

//        if(map.getDirectionFirstTurn()==target_direction){
//            System.out.println("Move first turn ");
//
//            IntruderAction a = new Move(new Distance(1.0));
//            map.updateState(a);
//            return a;
//        }

        ArrayList<Integer> listOfActions = AsSearch.computePath(map);
        System.out.println("listOfActions = " + listOfActions);

        if(listOfActions == null){
            System.out.println("No path found ");
            System.exit(1);
        }
        if(listOfActions.size() ==0){
            System.out.println("Target Reached");
            System.exit(1);
            return new NoAction();
        }

        int astar_move = listOfActions.get(0);
        System.out.println("astar_move = " + astar_move);

        if(!percepts.wasLastActionExecuted())
        {
            System.out.println("AstarAgent.getAction rejected");
//rotate from a random angle
            Angle random_rotation_angle =  Angle.fromRadians(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble());
           return new Rotate(random_rotation_angle);
        }

            System.out.println("last action accepted");

//make the action based on the a star move type required
        IntruderAction out_action = doAction(astar_move,map.getState().getAngle(),percepts);

            if(out_action ==null){
                System.out.println("Error ");
                //should not happen
                return new NoAction();
            }

            map.updateState(out_action);
            return out_action;
//        }

    }


    private IntruderAction doAction( int nb, double current_angle, IntruderPercepts percepts){
        double angle = 0;
        switch (nb){
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

        if(current_angle == angle){ // if the agent is in the good direction, move
            double max_dist = percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue()*getSpeedModifier(percepts);
            if(max_dist>1){
                max_dist=1;
            }
            return new Move(new Distance(max_dist));
        }else{ //otherwise, rotate to the good direction
            return rotateTo(angle,percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle());
        }
    }

    private Rotate rotateTo(double astar_angle, Angle max_rotation){

        double old_angle = map.getState().getAngle();
        Angle rotation_angle = Angle.fromDegrees(astar_angle-old_angle);

        if(rotation_angle.getDegrees() > max_rotation.getDegrees()){
            rotation_angle =max_rotation;
        }else if(rotation_angle.getDegrees() < -max_rotation.getDegrees()){
            rotation_angle = max_rotation;
            rotation_angle = Angle.fromRadians(-rotation_angle.getRadians());
        }
        System.out.println("Rotate to "+astar_angle+"; old angle is "+old_angle+"; new angle is "+(old_angle+rotation_angle.getDegrees()));

        return new Rotate(rotation_angle);
    }


}

