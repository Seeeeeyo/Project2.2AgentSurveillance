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

import java.util.ArrayList;
import java.util.List;

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

        map.updateGridMap(percepts);
        map.computeTargetPoint(percepts.getTargetDirection());

        List<int[]> listPositions =  AsSearch.computePath(map);
        //ArrayList<Integer> listMoves = AsSearch.getListOfActionsDirections(listPositions);
        ArrayList array = AsSearch.getListDirections();
        List<int[]> listConsecutivesActions = AsSearch.getNumberConsecutiveMoves(array);


        int counter = 0;

     /*   if (!firstTurn){
            // lastMove = (int) listMoves.get(counter-1);

        }*/

        int move = listConsecutivesActions.get(0)[0];
        int numberConsecutiveMove = listConsecutivesActions.get(0)[1];

        if(!percepts.wasLastActionExecuted())
        {

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
                return new Rotate(angle);
            }
            else if ((lastMove == 1 && move == 2) || (lastMove == 3 && move == 4) || (lastMove == 2 && move == 1) || (lastMove == 4 && move == 3)){
                Angle angle = Angle.fromDegrees(180);
                return new Rotate(angle);
            }
            else if ((lastMove == 1 && move == 4) || (lastMove == 3 && move == 1) || (lastMove == 2 && move == 3) || (lastMove == 4 && move == 2)) {
                Angle angle = Angle.fromDegrees(270);
                return new Rotate(angle);
            }
            // otherwise just rotate randomly
            else {
                return new Rotate(Angle.fromRadians(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble()));
            }
        }


        else
        {
            if ((move == 1)){
               if (map.getState().getAngle().getDegrees() == 0) {
                    return new Move(new Distance(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts)));
                }
                /*
                else if(map.getState().getAngle().getDegrees() == 90){
                    Angle angle = Angle.fromDegrees(270);
                    return new Rotate(angle);
                }*/
                Angle angle = Angle.fromDegrees(-map.getState().getAngle().getDegrees());
                if(angle.getDegrees() > percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()){
                    angle = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();
                    return new Rotate(angle);
                }
            }

            if ((move == 2)){
                if (map.getState().getAngle().getDegrees() == 180) {
                    return new Move(new Distance(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts)));
                }

                Angle angle = Angle.fromDegrees(180-map.getState().getAngle().getDegrees());
                if(angle.getDegrees() > percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()){
                    angle = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();
                    return new Rotate(angle);
                }
            }

            if ((move == 3)){
                if (map.getState().getAngle().getDegrees() == 90) {
                    return new Move(new Distance(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts)));
                }

                Angle angle = Angle.fromDegrees(90-map.getState().getAngle().getDegrees());
                if(angle.getDegrees() > percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()){
                    angle = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();
                    return new Rotate(angle);
                }
            }

            if ((move == 4)){
                if (map.getState().getAngle().getDegrees() == 270) {
                    return new Move(new Distance(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts)));
                }

                Angle angle = Angle.fromDegrees(270-map.getState().getAngle().getDegrees());
                if(angle.getDegrees() > percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()){
                    angle = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();
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

