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
import Interop.Percept.Percepts;
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

//    storing the visual perception of the agent
        private MindMap map = new MindMap();

        @Override
        public GuardAction getAction(GuardPercepts percepts) {
            System.out.println();
            System.out.println("Explorator.getAction");

            map.updateGridMap(percepts);

//            finds the closest unvisited point to go visit it afterwards, returns the path to go to it
            ArrayList<Integer> listOfActions = AsSearch.find_closest_unvisited(map);
//        System.out.println("listOfActions = " + listOfActions);

            if(listOfActions == null){
//                the agent has explored the entire map, clear it so that it does it again
                map.clearEmpty();
//            System.exit(1);
                return new NoAction();
            }
            else if(listOfActions.size() ==0){
//                should not happen since the agent must see unvisited point before going on its location
                System.out.println("Target Reached");
//            System.exit(1);
                return new NoAction();
            }else {

                int next_move = listOfActions.get(0);
//        System.out.println("move = " + move);

                if (!percepts.wasLastActionExecuted()) {
                    System.out.println("AstarAgent.getAction rejected");
//                  rotate from a random angle
                    Angle random_rotation_angle = Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble());
                    return new Rotate(random_rotation_angle);
                }

//            System.out.println("last action accepted");

//              choose an action to apply based on the a star move type required
                GuardAction out_action = (GuardAction)AstarAgent.doAction(AstarAgent.astarMove2Angle(next_move), map, percepts);

//            finds the closest unvisited point to go visit it afterwards, returns the path to go to it
                map.updateState(out_action);
                return out_action;
            }
        }

    }

