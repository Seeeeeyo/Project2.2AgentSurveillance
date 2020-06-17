package Group7.map.objects;

import Group7.tree.PointContainer;
import Group7.map.area.ModifyViewEffect;
import Interop.Percept.Vision.ObjectPerceptType;

public class ShadedArea extends MapObject {
    public ShadedArea(PointContainer area, double guardModifier, double intruderModifier) {
        super(area, ObjectPerceptType.ShadedArea);
        this.addEffects(new ModifyViewEffect(this, area, guardModifier, intruderModifier));
    }
}
