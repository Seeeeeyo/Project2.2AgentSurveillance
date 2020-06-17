package Group7.map.area;

import Group7.agent.container.AgentContainer;
import Group7.map.objects.MapObject;
import Group7.tree.PointContainer;
import Group7.map.ViewRange;

public class ModifyViewRangeEffect extends EffectArea<ViewRange> {

    private final ViewRange viewRange;

    public ModifyViewRangeEffect(MapObject parent, PointContainer pointContainer, ViewRange viewRange) {
        super(parent, pointContainer);
        this.viewRange = viewRange;
    }

    @Override
    public ViewRange get(AgentContainer<?> agentContainer) {
        return this.viewRange;
    }
}
