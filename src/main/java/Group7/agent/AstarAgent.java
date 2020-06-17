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
    private boolean firstTurn = true;
    private int lastMove = -1;

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

        ArrayList<Integer> listOfActions = AsSearch.computePath(map);
        System.out.println("listOfActions = " + listOfActions);

        if(listOfActions.size() ==0){
            System.out.println("no path found");
            return new NoAction();
        }
           int astar_move = listOfActions.get(0);
        System.out.println("astar_move = " + astar_move);

        if(!percepts.wasLastActionExecuted())
        {
            System.out.println("AstarAgent.getAction rejected");

            Angle random_rotation_angle =  Angle.fromRadians(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble());
            System.out.println("rotation_angle = " + random_rotation_angle.getRadians());
           return new Rotate(random_rotation_angle);
        }

            System.out.println("last action accepted");

           double max_dist = percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts);


        IntruderAction out_action = doAction(astar_move,map.getState().getAngle(),percepts);

            if(out_action ==null){
                System.out.println("Error ");
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
                angle = 180;
                break;
            case 2:
                angle = 0;
                break;
            case 3:
                angle = 90;
                break;
            case 4:
                angle = 270;
                break;
        }

        if(current_angle == angle){
            double max_dist = percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue()*getSpeedModifier(percepts);
            if(max_dist>1){
                max_dist=1;
            }
            System.out.println("Move "+max_dist);
            return new Move(new Distance(max_dist));
        }else{
            return rotateTo(angle,percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle());
        }
    }

    private Rotate rotateTo(double a, Angle max_rotation){

        double old = map.getState().getAngle();
        Angle rotation_angle = Angle.fromDegrees(a-old);


        if(rotation_angle.getDegrees() > max_rotation.getDegrees()){
            rotation_angle =max_rotation;
        }else if(rotation_angle.getDegrees() < -max_rotation.getDegrees()){
            rotation_angle = max_rotation;
            rotation_angle = Angle.fromRadians(-rotation_angle.getRadians());
        }
        System.out.println("Rotate to "+a+"; old angle is "+old+"; new angle is "+(old+rotation_angle.getDegrees()));

        return new Rotate(rotation_angle);
    }


}

