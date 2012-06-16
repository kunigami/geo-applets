package me.kuniga.compgeo;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
/**
 *
 * @author kunigami
 */

public class Polygon {
    
    ArrayList<Point> P;
    PolygonType type;
    
    /***************************************************************************
     * 
     *  Contrutores
     * 
     **************************************************************************/
    public Polygon() {
        P = new ArrayList<Point>();
        type = PolygonType.UNDEFINED;
    }
    public Polygon(ArrayList<Point> _P) {
        P = new ArrayList<Point>(_P);
    }
    public Polygon(Polygon _P){
        P = new ArrayList<Point>();
        for (int i=0; i<_P.Size(); i++){
            this.Insert(_P.Get(i));
        }        
    }
    
    public void Insert(Point p){
        type = PolygonType.UNDEFINED;
        P.add(p);
    }
    //Insere um ponto 'p' na posicao 'pos'
    public void InsertInto(Point p, int pos){
        type = PolygonType.UNDEFINED;
        P.add(pos, p);
    } 
    public Point Get(int i){
       return P.get(i);
    }
    //Retorna o tipo deste polígono
    public PolygonType GetType(){
        if (type == PolygonType.UNDEFINED)
            type = Type();
        return type;
    }
    
    public int Size(){
        return P.size();
    }
    public int GetLeastIndex (){
        Point resp = new Point();
        int resp_index = -1;
        for (int i=0; i<P.size(); i++){
            Point p = P.get(i);
            if (resp_index == -1 || Essentials.PointCmpXY(resp,p) > 0){
                resp = p;
                resp_index = i;
            }
        }
        return resp_index;
    }
    public Point GetLeast (){
        return P.get(GetLeastIndex());
    }
    //Remove o último elemento do polígono
    public void PopBack (){
        P.remove(P.size()-1);
    }
    public void Pop(int i){
        P.remove(i);
    }
    public void Sort(int pivot_id){
        Comparator radialCmp = new RadialCmp(P.get(pivot_id));
        Collections.sort(P, radialCmp);
    }
    //Retorna o tipo de polígono
    public PolygonType Type(){
        
        if (P.size() < 3) 
            return PolygonType.UNDEFINED;
        
        //Decide se é convexo
        Boolean is_convex;
        is_convex = true;
        int sign = Essentials.PointCcw(P.get(2), P.get(0), P.get(1));
        for (int i=0; i<P.size(); i++){
            int r = Essentials.PointCcw(P.get((i+2)%P.size()), 
                    P.get(i), P.get((i+1)%P.size()));
            if (r != sign && r != 0){
                is_convex = false;
                break;
            }
        }
        if (is_convex) 
            return PolygonType.CONVEX;
        //Decide se é simples
        for (int i=0; i<P.size(); i++){
        
            Point a = P.get(i);
            Point b = P.get((i+1) % P.size());
            
            for (int j=0; j<P.size(); j++){
                if (j != i && j != (i+1)%P.size() && (j+1)%P.size() != i){
                    Point c = P.get(j);
                    Point d = P.get((j+1) % P.size());
                    if (Essentials.SegmentIntersectionTest(a,b,c,d) == true){
                        System.out.println("" + i + " " + j);
                        return PolygonType.NONE;
                    }
                }
            }
        }
        return PolygonType.SIMPLE;
    }
    //Retorna 2 vezes a área do polígono
    public int SignedArea(){
        int area = 0;
        for (int i=0; i<P.size(); i++){
            area += Essentials.DotProductPerp(P.get(i), P.get((i+1)%P.size()));
        }
        return area;
    }
    //Decide a orientação do polígono
    //1 Horário
    //-1 anti-Horário
    public int GetOrientation(){
        int sgn_area = SignedArea();
        if (sgn_area < 0) return -1;
        if (sgn_area > 0) return 1;
        return 0;
    }
    public void MakeCW (){
        if (GetOrientation() == 1) return;
        Reverse();
    }
    public void MakeCCW(){
        if (GetOrientation() == -1) return;
        Reverse();
    }
    public void Reverse(){
        for (int i=0; i<P.size()/2; i++)
            Essentials.PointSwap(P.get(i), P.get(P.size()-1-i));
    }
    
    public static void main (String[] args){
        
        Polygon P = new Polygon();
        //Anti-horario
        P.Insert(new Point(0,0));
        P.Insert(new Point(10,0));
        P.Insert(new Point(0,10));
        
        System.out.println(P.GetOrientation() + "");
        
        P.Reverse();
        
        System.out.println(P.GetOrientation() + "");
    }
};
