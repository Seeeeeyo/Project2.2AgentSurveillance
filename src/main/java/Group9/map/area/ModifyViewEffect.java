package Group9.map.area;

import Group9.agent.AgentContainer;
import Group9.tree.PointContainer;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;

public class ModifyViewEffect extends EffectArea {

    private double guardModifier;
    private double intruderModifier;

    public ModifyViewEffect(PointContainer pointContainer, double guardModifier, double intruderModifier) {
        super(pointContainer);
        this.guardModifier = guardModifier;
        this.intruderModifier = intruderModifier;
    }

    @Override
    public double get(AgentContainer<?> agentContainer) {

        if(agentContainer.getAgent() instanceof Guard)
        {
            return this.guardModifier;
        }
        else if(agentContainer.getAgent() instanceof Intruder)
        {
            return this.intruderModifier;
        }

        throw new IllegalStateException();

    }
}
