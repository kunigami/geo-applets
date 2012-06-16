package me.kuniga.compgeo;

import java.awt.Point;


/**
 *
 * @author kunigami
 */
public class Essentials {
     
    static double eps = 1e-10;
    
    public static int PointCmpXY (Point a, Point b){
        if (a.x < b.x) return -1;
        if (a.x > b.x) return 1;
        if (a.y < b.y) return -1;
        if (a.y > b.y) return 1;
        return 0;
    }
    public static int PointCmpYX (Point a, Point b){
        if (a.y < b.y) return -1;
        if (a.y > b.y) return 1;
        if (a.x < b.x) return -1;
        if (a.x > b.x) return 1;
        return 0;
    }
    public static int DotProductPerp (Point a, Point b){
        return a.x * b.y - a.y * b.x;
    }
    //Área sinalizada do triângulo a>b>c
    public static int Area3 (Point a, Point b, Point c){
        return DotProductPerp(a, b) + DotProductPerp(b, c)+ DotProductPerp(c, a);
    }
    /** Decides in which side of segment p-q point {@code a} is:
     * 
     * <ul>
     * <li>Collinear: 0</li>
     * <li>Left: -1</li>
     * <li>Right: 1</li>
     * </ul>
     * 
     * @param a
     * @param p
     * @param q
     * @return
     */
    public static int PointCcw (Point a, Point p, Point q){
        int area = Area3(p, q, a);
        if (area > 0) return 1;
        if (area < 0) return -1;
        return 0;
    }
    //Decide se o ponto p está sobre o segmento rs
    public static Boolean Between (Point p, Point r, Point s){
        if (PointCcw(p, r, s) == 0 && 
            PointDist2(p,r) <= PointDist2(r,s) &&
            PointDist2(p,s) <= PointDist2(r,s)){
            return true;
        }
        return false;
    }
    
    public static int PointDist2 (Point p, Point q){
        return (p.x - q.x)*(p.x -q.x) + (p.y - q.y)*(p.y - q.y);
    }
    
    /* Valores: 
       -1 - Não faz o teste
        0 - Faz o teste usando igualdade 
        1 - Faz o teste sem usar igualdade
     */
    public static Boolean SegmentIntersectionTest (Point a, Point b, Point c, Point d,
            int min_s, int max_s, int min_t, int max_t){
                
        int det = DotProductPerp(a, d) + DotProductPerp(d, b) + 
            DotProductPerp(b, c) + DotProductPerp(c, a);
        if (det == 0){
            if (Between(a, c, d) || Between(b, c, d) || Between(c, a, b) || Between(d, a, b))
                return true;
            else
                return false;
        }
        double s = (DotProductPerp(a,d) +  DotProductPerp(d,c) + DotProductPerp(c,a)) * 1.0 / det;
        double t = (DotProductPerp(a,b) +  DotProductPerp(b,c) + DotProductPerp(c,a)) * 1.0 / det;
        
        if (!(min_s == -1 || (min_s == 0 && -eps <= s) || (min_s == 1 && eps < s))) 
            return false;
        if (!(max_s == -1 || (max_s == 0 && 1 - eps >= s) || (max_s == 1 && 1 + eps > s))) 
            return false;
        
        if (!(min_t == -1 || (min_t == 0 && -eps <= t) || (min_t == 1 && eps < t))) 
            return false;
        if (!(max_t == -1 || (max_t == 0 && 1 - eps >= t) || (max_t == 1 && 1 + eps > t))) 
            return false;
        return true;
    }
    
    //Decide se os segmentos (s1,s2) e (t1,t2) se interceptam
    public static Boolean SegmentIntersectionTest (Point a, Point b, Point c, Point d){
        
        int det = DotProductPerp(a, d) + DotProductPerp(d, b) + 
            DotProductPerp(b, c) + DotProductPerp(c, a);
        if (det == 0){
            if (Between(a, c, d) || Between(b, c, d) || Between(c, a, b) || Between(d, a, b))
                return true;
            else
                return false;
        }
        int s = (DotProductPerp(a,d) +  DotProductPerp(d,c) + DotProductPerp(c,a));
        int t = (DotProductPerp(a,b) +  DotProductPerp(b,c) + DotProductPerp(c,a));
        if ( 0 <= s && s <= det && 0 <= t && t <= det)
            return true;
        else
            return false;
    }
    //Decide o ponto das retas expandidas dos segmentos (s1,s2) e (t1,t2)
    public static Point SegmentIntersection (Point a, Point b, Point c, Point d){
        //Retas paralelas
        int det = DotProductPerp(a, d) + DotProductPerp(d, b) + 
            DotProductPerp(b, c) + DotProductPerp(c, a);

        double s, t;
        try {
            s = ((double)(DotProductPerp(a,d) +  DotProductPerp(d,c) + DotProductPerp(c,a))) / det;
            t = ((double)(DotProductPerp(a,b) +  DotProductPerp(b,c) + DotProductPerp(c,a))) / det;
            int X = (int)Math.round(a.x + (b.x - a.x) * s);
            int Y = (int)Math.round(a.y + (b.y - a.y) * s);
            return new Point(X,Y);   
        }
        catch (ArithmeticException e){
            System.out.print("Retas Paralelas");
        }
        return null;
    }
    public static void PointSwap(Point p, Point q){
        Point tmp = new Point(p.x, p.y);
        p.x = q.x;
        p.y = q.y;
        q.x = tmp.x;
        q.y = tmp.y;
    }
    
    public static void main (String[ ] args){
        
        //Teste de Swap
        Point a = new Point(0,0);
        Point b = new Point(0,5);
        Point c = new Point(0,6);
        Point d = new Point(0,14);

        if (SegmentIntersectionTest(a,b,c,d) == true){
            System.out.println("Esses segmentos se interceptam");
        }
     
        
        
    }
}

