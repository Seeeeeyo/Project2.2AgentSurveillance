package Group7.map.area;

import Group7.agent.container.AgentContainer;

public class NoModify extends EffectArea<Double> {

    public NoModify() {
        super(null, null);
    }

    @Override
    public Double get(AgentContainer<?> agentContainer) {
        return 1D;
    }

}
