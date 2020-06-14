 package Group9.agent;

import Group9.Game;
import Group9.agent.Intruder.AgentState;
import Group9.agent.Intruder.AsSearch;
import Group9.agent.Intruder.MindMap;
import Interop.Action.IntruderAction;
import Interop.Action.Move;
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
import java.util.List;
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
private int turn =0;



    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {

        Set<ObjectPercept> objectPercepts = percepts.getVision().getObjects().getAll();
        Set<SoundPercept> soundPercepts = percepts.getSounds().getAll();
        Set<SmellPercept> smellPercepts = percepts.getSmells().getAll();
        //change the set to list, imo list is more convenient
        ArrayList<ObjectPercept> objectPerceptArrayList = new ArrayList<ObjectPercept>(objectPercepts);
        ArrayList<SoundPercept> soundPerceptArrayList = new ArrayList<SoundPercept>(soundPercepts);
        ArrayList<SmellPercept> smellPerceptArrayList = new ArrayList<>(smellPercepts);
        encapAction e = new encapAction();

        map.updateGridMap(percepts);
        map.computeTargetPoint(percepts.getTargetDirection());

        List<int[]> listPositions =  AsSearch.computePath(map);
        //ArrayList<Integer> listMoves = AsSearch.getListOfActionsDirections(listPositions);
        //ArrayList array = AsSearch.getListDirections();
        ArrayList<Integer> directions = AsSearch.getListOfActionsDirections(listPositions);
        List<int[]> listConsecutivesActions = AsSearch.getNumberConsecutiveMoves(directions);

        int counter = 0;
        System.out.println("directions.get(0) = " + directions.get(0));
        int move = listConsecutivesActions.get(0)[0];
        System.out.println("move = " + move);
        int numberConsecutiveMove = listConsecutivesActions.get(0)[1];
        System.out.println("numberConsecutiveMove = " + numberConsecutiveMove);

        if(!percepts.wasLastActionExecuted())
        {
            System.out.println("not accepted or 1st turn");

            // changed the first turn boolean
            if (firstTurn) {
                firstTurn=false;
                System.out.println("firstTurn = " + firstTurn);
            }
            System.out.println("turn = " + turn);
            turn++;

            // get the angle to rotate
            if((lastMove == 1 && move == 3) || (lastMove == 3 && move == 2) || (lastMove == 2 && move == 4) || (lastMove == 4 && move == 1)){
                Angle angle = Angle.fromDegrees(90);
                System.out.println("Case 1");
                return new Rotate(angle);
            }
            else if ((lastMove == 1 && move == 2) || (lastMove == 3 && move == 4) || (lastMove == 2 && move == 1) || (lastMove == 4 && move == 3)){
                Angle angle = Angle.fromDegrees(180);
                System.out.println("Case 2");
                return new Rotate(angle);
            }
            else if ((lastMove == 1 && move == 4) || (lastMove == 3 && move == 1) || (lastMove == 2 && move == 3) || (lastMove == 4 && move == 2)) {
                Angle angle = Angle.fromDegrees(270);
                System.out.println("Case 3");
                return new Rotate(angle);
            }
            // otherwise just rotate randomly
            else {
                System.out.println("Case 4");
                return new Rotate(Angle.fromRadians(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble()));
            }
        }


        else
        {
            System.out.println("last action accepted");
            if ((move == 1)){
               if (map.getState().getAngle().getDegrees() == 0) {
                   System.out.println("Case 5");
                    return new Move(new Distance(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts)));
                }
                /*
                else if(map.getState().getAngle().getDegrees() == 90){
                    Angle angle = Angle.fromDegrees(270);
                    return new Rotate(angle);
                }*/
                Angle angle = Angle.fromDegrees(-map.getState().getAngle().getDegrees());
                if(angle.getDegrees() > percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()){
                    System.out.println("Case 6");
                    angle = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();
                    return new Rotate(angle);
                }
            }

            if ((move == 2)){
                if (map.getState().getAngle().getDegrees() == 180) {
                    System.out.println("Case 7");
                    return new Move(new Distance(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts)));
                }

                Angle angle = Angle.fromDegrees(180-map.getState().getAngle().getDegrees());
                if(angle.getDegrees() > percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()){
                    System.out.println("Case 8");
                    angle = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();
                    return new Rotate(angle);
                }
            }

            if ((move == 3)){
                if (map.getState().getAngle().getDegrees() == 90) {
                    System.out.println("Case 9");
                    return new Move(new Distance(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts)));
                }

                Angle angle = Angle.fromDegrees(90-map.getState().getAngle().getDegrees());
                if(angle.getDegrees() > percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()){
                    System.out.println("Case 10");
                    angle = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();
                    return new Rotate(angle);
                }
            }

            if ((move == 4)){
                System.out.println("map.getState().getAngle().getDegrees() = " + map.getState().getAngle().getDegrees());
                if ( map.getState().getAngle().getDegrees() <= 360) { // TO CHANGE !!! should be 270
                    System.out.println("Case 11");
                    return new Move(new Distance(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts)));
                }

                Angle angle = Angle.fromDegrees(270-map.getState().getAngle().getDegrees());
                if(angle.getDegrees() > percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()){
                    System.out.println("Case 12");
                    angle = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();
                    System.out.println("angle.getDegrees() = " + angle.getDegrees());
                    return new Rotate(angle);
                }
            }



            if (firstTurn) {
                System.out.println("firstTurn = " + firstTurn);
                firstTurn=false;
            }
            System.out.println("turn = " + turn);
            turn++;
            map.updateGridMap(percepts);
            map.computeTargetPoint(percepts.getTargetDirection());

            Move ac =  new Move(new Distance(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts)));
            map.updateState(ac);
            return ac;
        }

    }

}

