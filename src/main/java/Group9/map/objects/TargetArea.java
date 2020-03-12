package Group9.map.objects;

import Group9.map.objects.MapObject;
import Group9.tree.PointContainer;
import Interop.Percept.Vision.ObjectPerceptType;

public class TargetArea extends MapObject {

    public TargetArea(PointContainer.Quadrilateral area) {
        super(area, ObjectPerceptType.TargetArea);
    }

}