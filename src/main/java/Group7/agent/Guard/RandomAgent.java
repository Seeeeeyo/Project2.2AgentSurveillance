package Group7.agent.Guard;

import Group7.Game;
import Interop.Action.*;
import Interop.Agent.Guard;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Scenario.SlowDownModifiers;


public class RandomAgent implements Guard {

    public RandomAgent() {}

    @Override
    public GuardAction getAction(GuardPercepts percepts) {

        SlowDownModifiers slowDownModifiers =  percepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers();
        double modifier = 1;
        if (percepts.getAreaPercepts().isInWindow()){
            modifier = slowDownModifiers.getInWindow();
        }else if (percepts.getAreaPercepts().isInSentryTower()){
            modifier = slowDownModifiers.getInSentryTower();
        }else if (percepts.getAreaPercepts().isInDoor()){
            modifier = slowDownModifiers.getInDoor();
        }

        if(!percepts.wasLastActionExecuted())
        {
            return new Rotate(Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble()));
        }
        else
        {
            return new Move(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * modifier));
        }
    }

}
