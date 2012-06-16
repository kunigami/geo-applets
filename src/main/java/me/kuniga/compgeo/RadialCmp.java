package me.kuniga.compgeo;

import java.awt.Point;
import java.util.Comparator;
/**
 *
 * @author kunigami
 */
public class RadialCmp implements Comparator<Point> {
    
    Point pivot;
    
    public RadialCmp() {
    }
    public RadialCmp(Point p){
        pivot = p;
    }
    public int compare(Point p, Point q) {
        if (Essentials.PointCmpXY(pivot, p) == 0)        
            return -1;
        if (Essentials.PointCmpXY(pivot, q) == 0) 
            return 1;
        int resp = Essentials.PointCcw(p, pivot, q);
        if (resp != 0) return -resp;
        return Essentials.PointDist2(pivot,p) - Essentials.PointDist2(pivot, q);
    }
}
