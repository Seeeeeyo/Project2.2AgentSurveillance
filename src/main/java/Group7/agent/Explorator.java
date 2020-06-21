package Group7.agent;

import Group7.Game;
import Group7.agent.Intruder.AsSearch;
import Group7.agent.Intruder.MindMap;
import Interop.Action.*;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Percept.GuardPercepts;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Scenario.SlowDownModifiers;

import java.util.ArrayList;

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

public class Explorator implements Guard {

        private MindMap map = new MindMap();

        private double getSpeedModifier(GuardPercepts guardPercepts)
        {
            SlowDownModifiers slowDownModifiers =  guardPercepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers();
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
        public GuardAction getAction(GuardPercepts percepts) {
            System.out.println();
            System.out.println("Explorator.getAction");

            map.updateGridMap(percepts);


            ArrayList<Integer> listOfActions = AsSearch.find_closest_unvisited(map);
        System.out.println("listOfActions = " + listOfActions);

            if(listOfActions == null){
                map.clearEmpty();
//            System.exit(1);
                return new NoAction();
            }
            else if(listOfActions.size() ==0){
                System.out.println("Target Reached");
//            System.exit(1);
                return new NoAction();
            }else {

                int astar_move = listOfActions.get(0);
//        System.out.println("astar_move = " + astar_move);

                if (!percepts.wasLastActionExecuted()) {
                    System.out.println("AstarAgent.getAction rejected");
//rotate from a random angle
                    Angle random_rotation_angle = Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble());
                    return new Rotate(random_rotation_angle);
                }

//            System.out.println("last action accepted");

//make the action based on the a star move type required
                GuardAction out_action = doAction(astar_move, map.getState().getAngle(), percepts);

                if (out_action == null) {
                    System.out.println("Error ");
                    //should not happen
                    return new NoAction();
                }

                map.updateState(out_action);
                return out_action;
            }
        }


        private GuardAction doAction( int nb, double current_angle, GuardPercepts percepts){
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
                double max_dist = percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue()*getSpeedModifier(percepts);
                if(max_dist>1){
                    max_dist=1;
                }
                return new Move(new Distance(max_dist));
            }else{ //otherwise, rotate to the good direction
                return rotateTo(angle,percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle());
            }
        }

        private Rotate rotateTo(double astar_angle, Angle max_rotation){

            double old_angle = map.getState().getAngle();
            double rotation = astar_angle-old_angle;
            double rotation2 = astar_angle-360-old_angle;

            Angle rotation_angle;
            if(Math.abs(rotation)>=Math.abs(rotation2)){
                rotation_angle = Angle.fromDegrees(rotation2);
            }else{
                rotation_angle = Angle.fromDegrees(rotation);
            }

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

