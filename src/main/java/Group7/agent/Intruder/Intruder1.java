package Group7.agent.Intruder;

import Interop.Action.IntruderAction;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Percept.IntruderPercepts;

public class Intruder1 implements Intruder {
    Cell position = new Cell();
    public Intruder1()
    {

    }

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {
        //building the "mind-map" of the building
        Direction direction = percepts.getTargetDirection();
        Angle alpha = percepts.getVision().getFieldOfView().getViewAngle();
        Distance range = percepts.getVision().getFieldOfView().getRange();
        return null;
    }
}
