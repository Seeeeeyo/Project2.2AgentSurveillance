package Group7.map.objects;

import Group7.tree.PointContainer;
import Group7.map.area.ModifySpeedEffect;
import Group7.map.area.ModifyViewEffect;
import Group7.map.area.SoundEffect;
import Interop.Percept.Sound.SoundPerceptType;
import Interop.Percept.Vision.ObjectPerceptType;

public class Window extends MapObject {

    public Window(PointContainer.Polygon area,
                  double guardViewModifier, double intruderViewModifier,
                  double soundRadius,
                  double guardSpeedModifier, double intruderSpeedModifier) {
        super(area, ObjectPerceptType.Window);
        this.addEffects(new ModifyViewEffect(this, area, guardViewModifier, intruderViewModifier),
                new SoundEffect(this, area, SoundPerceptType.Noise, soundRadius),
                new ModifySpeedEffect(this, area, guardSpeedModifier, intruderSpeedModifier));
    }

}
