package Group7.map.objects;

import Group7.tree.PointContainer;
import Group7.map.ViewRange;
import Group7.map.area.ModifySpeedEffect;
import Group7.map.area.ModifyViewRangeEffect;
import Interop.Percept.Vision.ObjectPerceptType;

public class SentryTower extends MapObject {

    public SentryTower(PointContainer area, double sentrySlowdownModifier, ViewRange viewRange) {
        super(area, ObjectPerceptType.SentryTower);
        this.addEffects(
                new ModifySpeedEffect(this, area, sentrySlowdownModifier,sentrySlowdownModifier),
                new ModifyViewRangeEffect(this, area, viewRange)
        );
    }

}
