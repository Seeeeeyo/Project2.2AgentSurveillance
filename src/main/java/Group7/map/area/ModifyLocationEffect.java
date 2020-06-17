package Group7.map.area;

import Group7.agent.container.AgentContainer;
import Group7.map.objects.MapObject;
import Group7.math.Vector2;
import Group7.tree.PointContainer;

public abstract class ModifyLocationEffect extends EffectArea<Vector2> {

    public ModifyLocationEffect(MapObject parent, PointContainer pointContainer) {
        super(parent, pointContainer);
    }

    @Override
    public abstract Vector2 get(AgentContainer<?> agentContainer);

}
