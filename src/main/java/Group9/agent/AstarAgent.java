/* package Group9.agent;

import Group9.Game;
import Group9.agent.Intruder.MindMap;
import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Scenario.SlowDownModifiers;

public class AstarAgent implements Intruder {

    private MindMap map = new MindMap();
    private boolean firstTurn = true;

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

        if(!percepts.wasLastActionExecuted())
        {
            if (firstTurn) {
                firstTurn=false;
                System.out.println("firstTurn = " + firstTurn);
            }
            System.out.println("turn = " + turn);
            turn++;
            return new Rotate(Angle.fromRadians(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble()));
        }
        else
        {
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

*/
