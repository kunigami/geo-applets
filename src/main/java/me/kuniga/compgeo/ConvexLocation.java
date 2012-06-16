package me.kuniga.compgeo;

import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
/**
 *
 * @author kunigami
 */
public class ConvexLocation extends AppletEngine implements 
 MouseMotionListener, MouseListener, ActionListener
{    
	private static final long serialVersionUID = 1842862696981961392L;
	
	//Representa o polígono principal
    Polygon P;
    //Polígono representando a área ativa
    Polygon activeArea;
    //Ponto de consulta
    Point query;
    //Representa o centróide do polígono para o caso convexo
    Point centroid;
    //Representa o ponto inicial da busca binária (caso convexo)
    int ini;
    //Representa o ponto final da busca binária (caso convexo)
    int end;
    //Representa o ponto do meio da busca binária (caso convexo)
    int mid;
    //Representa o ponto de interseção entre a reta (centroid,P[0]) e o polígono
    int divisor;
    //Representa o ponto inicial do polígono depois de cortá-lo
    int poly_ini;
    //Representa o ponto final do polígono depois de cortá-lo
    int poly_end;
    
    /*
     * Botões
     */

    //Número de botões
    int nButton;
    //Botão para fechar o polígono
    Button buttonClose;
    //Botão para resetar o applet
    Button buttonReset;
    //Botão para executar o proximo passo no algoritmo
    Button buttonNext;
    //Botão para escolher um novo ponto de consulta
    Button buttonEdit;
        
    //Define o retângulo que representa a borda
    Point lower_bound;
    Point upper_bound;
    //Representa a localização do cursor
    Point pos;
    //Define o ponto de intersecao de (centroid,Pini) com o retangulo
    Point inter1;
    //Define o ponto de intersecao de (centroid,Pend) com o retangulo 
    Point inter2;
    
    /*
     * Flags
     */
    //Decide se o cursor está em uma região ativa
    Boolean active;
    //Decide se um polígono foi feito
    Boolean is_polygon;
    //Decide se o centróide (caso convexo) foi encontrado
    Boolean centroid_found;
    //Decide se o inicio e fim foram encontrados
    Boolean binPoints_found;
    //Decide se o ponto de consulta já foi colocado
    Boolean query_found;
    //Decide se o ponto do meio já foi encontrado
    Boolean midPoint_found;
    //Decide se é para deixar em negrito a aresta Pini,Pend
    Boolean highlight;
    //Decide se o ponto inter1 foi encontrado
    Boolean inter_found;
    //Decide se o algoritmo encontrou o ponto
    Boolean solved;
    //Decide se o ponto esta dentro ou fora
    Boolean inside;
    //Decide se o ponto da divisão inicial foi já encontrado
    Boolean divisor_found;
    
    /* Mensagem a ser impressa na tela */
    String msg;
    //Offset a partir de onde as mensagens começam
    int offset;
    
    /** Controls localization */
    Translator translator;
    
    protected void reset(){
        stage = 0;
        
        /* Seta corretamente as flags */
        active = false;
        is_polygon = false;
        centroid_found = false;
        steped = false;
        binPoints_found = false;
        query_found = false;
        midPoint_found = false;
        highlight = false;
        inter_found = false;
        solved = false;
        divisor_found = false;
        
        /* Desativa os botões */
        buttonClose.setEnabled(false);
        
        /* Inicializa os polígonos */
        P = new Polygon();
        activeArea = new Polygon();
        
        /* desabilita botoes */
        buttonNext.setEnabled(false);
        buttonEdit.setEnabled(false);
    }
    protected void restart(){
        
        /* Seta correstamente as flags */
        centroid_found = false;
        steped = false;
        binPoints_found = false;
        query_found = false;
        midPoint_found = false;
        highlight = false;
        inter_found = false;
        solved = false;
        
        stage = 0;
        buttonNext.setEnabled(false);
        buttonEdit.setEnabled(false);
    }
    public void init() {
    	
    	
    	String country = super.getParameter("country");
    	String language = super.getParameter("language");

    	translator = new Translator(language, country);

        addMouseListener(this);
        addMouseMotionListener(this);

        setLayout(null);
        
        Color bg = new Color(245, 245, 245);
        setBackground (bg);
        int buttonWidth = 110;
        int offX = 10;
        
        nButton = 0;
        
        //Define um ponto inicial qualquer para não jogar exceção
        pos = new Point(0, 0);

        //Define o tamanho da borda
        lower_bound = new Point(10,50);
        upper_bound = new Point(590,385);
        
        //Botao para fechar o polígono
        buttonClose = new Button(translator.localize("close"));
        add(buttonClose);
        buttonClose.setBounds(offX + buttonWidth * nButton, 10, buttonWidth, 30);
        buttonClose.addActionListener(this);
        nButton++;
        
        //Botao para executar o próximo passo do algoritmo
        buttonNext = new Button(translator.localize("next"));
        add(buttonNext);
        buttonNext.setBounds(offX + buttonWidth * nButton, 10, buttonWidth, 30);
        buttonNext.addActionListener(this);       
        nButton++;
        
        //Botao escolher um novo ponto de consulta
        buttonEdit = new Button(translator.localize("edit"));
        add(buttonEdit);
        buttonEdit.setBounds(offX + buttonWidth * nButton, 10, buttonWidth, 30);
        buttonEdit.addActionListener(this);       
        nButton++;
        
        //Botao para resetar o applet
        buttonReset = new Button(translator.localize("reset"));
        add(buttonReset);
        buttonReset.setBounds(offX + buttonWidth * nButton, 10, buttonWidth, 30);
        buttonReset.addActionListener(this);       
        nButton++;
        
        msg = translator.localize("message") + ": ";
        
        reset();
    }

    public int convexLocation(){
     
        int curr_stage = 0;

        if (curr_stage == stage) return curr_stage;
        curr_stage++;
        
        //Encontra o centróide (otimizado)
        if (centroid_found == false){
            //Determina o centroide
            centroid = new Point(0,0);
            for (int i=0; i<P.Size(); i++){
                centroid.x += P.Get(i).x;
                centroid.y += P.Get(i).y;
            }
            centroid.x = (int)Math.round(((double)centroid.x)/P.Size());
            centroid.y = (int)Math.round(((double)centroid.y)/P.Size());
        
            centroid_found = true;
        }
        
        if (curr_stage == stage) return curr_stage;
        curr_stage++;
        
        /*  Determina a reta (centroid, P0) e o outro ponto além de P0
            que esta intercepta
        */
        if (divisor_found == false){
        
            //Primeira fase: Determinar se algum vértice além de P0 é interceptado
            for (int i=1; i<P.Size(); i++){
                if (Essentials.PointCcw(P.Get(i), P.Get(0), centroid) == 0){
                    divisor_found = true;
                    divisor = i;
                    break;
                }
            }
            if (divisor_found == false){
                //Segunda fase: Determinar se alguma aresta é interceptada
                for (int i=0; i<P.Size(); i++){
                    int j = (i+1) % P.Size();
                    if (Essentials.SegmentIntersectionTest(P.Get(0), centroid, P.Get(i), P.Get(j),
                            -1,-1,1,1)){
                        divisor_found = true;
                        Point Pdiv = Essentials.SegmentIntersection(P.Get(0), centroid, P.Get(i), P.Get(j));
                                    
                        /* Devemos processar o ponto "divisor" de modo que este não torne o polígono
                         * não-convexo
                         */ 
                         
                        //vetor diretor perpendicular à reta (P[i], P[i+1])
                        double dirX = P.Get(i).y - P.Get(j).y;
                        double dirY = P.Get(j).x - P.Get(i).x;
                        double norma = Math.sqrt(dirX * dirX + dirY * dirY);
                        double delta = 2; //Quão longe queremos o ponto
                                                
                        dirX /= norma;
                        dirY /= norma;
                                                
                        Point convex1 = new Point((int)(Pdiv.x + dirX * delta), (int)(Pdiv.y + dirY * delta));
                        Point convex2 = new Point((int)(Pdiv.x - dirX * delta), (int)(Pdiv.y - dirY * delta));
                        
                        System.out.println("Orientação: " + P.GetOrientation());
                        System.out.println("Convex1 " + convex1.x + "," + convex1.y + " CCW1: " + Essentials.PointCcw(convex1, P.Get(i), P.Get(j)));
                        System.out.println("Convex2 " + convex2.x + "," + convex2.y + " CCW2: " + Essentials.PointCcw(convex2, P.Get(i), P.Get(j)));
                         
                        if (Essentials.PointCcw(convex1, P.Get(i), P.Get(j)) != P.GetOrientation()){
                            Pdiv = convex1;
                            System.out.println("Escolhi o convex 1");
                        }
                        else {
                            Pdiv = convex2;
                            System.out.println("Escolhi o convex 2");
                        }
                        P.InsertInto(Pdiv, j);
                        divisor = j;
                        break;
                    }
                }
            }
        }         
        
        
        
        if (curr_stage == stage) return curr_stage;
        curr_stage++;
        
        //Determina de qual lado do segmento (P[0],P[divisor]) o ponto de consulta está
        //Direita ou colinear
        if (Essentials.PointCcw(query, centroid, P.Get(0)) >= 0){
            ini = 0;
            end = divisor;
        }
        else {
            ini = divisor;
            end = P.Size();
        }
        poly_ini = ini;
        poly_end = end;

        binPoints_found = true;
        
        if (curr_stage == stage) return curr_stage;
        curr_stage++;

        midPoint_found = false;
        
        while (ini + 1 < end){
            mid = (ini + end)/2;
            midPoint_found = true;

            if (curr_stage == stage) return curr_stage;
            curr_stage++;

            //Esquerda do segmento entre o centroide e o ponto Pmid
            if (Essentials.PointCcw(query, centroid, P.Get(mid)) < 0){
                end = mid;
            }
            else {
                ini = mid;
            }

            midPoint_found = false;
            if (curr_stage == stage) return curr_stage;
            curr_stage++;
        }

        int in_sign = Essentials.PointCcw(centroid, P.Get(ini), P.Get(end % P.Size()));
        int query_sign  = Essentials.PointCcw(query, P.Get(ini), P.Get(end % P.Size()));
        
        if (query_sign == in_sign){
            inside = true;
        }
        else {
            inside = false;
        }
        //Determina se o ponto está dentro ou fora
        //Faz em negrito a aresta Pini,Pend
        highlight = true;
        solved = true;
        buttonNext.setEnabled(false);
        
        return 0;
    }

    //Encontra o ponto de interseção de um segmento pq com o lado rs
    public void InterFrame (Point r, Point s, Point curr, int idx){
        Point inter = Essentials.SegmentIntersection(r, s, centroid, P.Get(idx));
        if (Essentials.PointDist2(centroid, inter) > Essentials.PointDist2(P.Get(idx), inter) &&
            Essentials.PointDist2(centroid, inter) > Essentials.PointDist2(P.Get(idx), centroid)){
            if (Essentials.PointDist2(centroid, inter) < Essentials.PointDist2(centroid, curr)){
                curr.x = inter.x;
                curr.y = inter.y;
            }
        }
    }
    //Determina a área que será pintada tendo os pontos Pini e Pend
    public void FindArea (){
        
        if (binPoints_found == false) return;
        
        inter1 = new Point(100000, 1000000);        
        inter2 = new Point(100000, 1000000);
        
        InterFrame(new Point(lower_bound.x, lower_bound.y), 
                   new Point(upper_bound.x, lower_bound.y), inter1, ini);
        InterFrame(new Point(upper_bound.x, lower_bound.y), 
                   new Point(upper_bound.x, upper_bound.y), inter1, ini);
        InterFrame(new Point(upper_bound.x, upper_bound.y), 
                   new Point(lower_bound.x, upper_bound.y), inter1, ini);
        InterFrame(new Point(lower_bound.x, upper_bound.y), 
                   new Point(lower_bound.x, lower_bound.y), inter1, ini);
        
        InterFrame(new Point(lower_bound.x, lower_bound.y), 
                   new Point(upper_bound.x, lower_bound.y), inter2, end % P.Size());
        InterFrame(new Point(upper_bound.x, lower_bound.y), 
                   new Point(upper_bound.x, upper_bound.y), inter2, end % P.Size());
        InterFrame(new Point(upper_bound.x, upper_bound.y), 
                   new Point(lower_bound.x, upper_bound.y), inter2, end % P.Size());
        InterFrame(new Point(lower_bound.x, upper_bound.y), 
                   new Point(lower_bound.x, lower_bound.y), inter2, end % P.Size());
        
        activeArea = new Polygon();
        activeArea.Insert(inter1);
        activeArea.Insert(inter2);
        
        inter_found = true;
    }
    
    public void paint(Graphics g) {
        
        Graphics2D g2d = (Graphics2D)g;
        
        //Desenha a borda da área de desenho
        g.drawRect(lower_bound.x, lower_bound.y, 
                   upper_bound.x-lower_bound.x, 
                   upper_bound.y-lower_bound.y);

        //Desenha a posição do cursor
        if (active && stage == 0){
            g.drawString("("+(pos.x-lower_bound.x)+","+(upper_bound.y-pos.y)+")",pos.x,pos.y);
        }
        
        if (P.GetType() == PolygonType.CONVEX){
            
            if (steped == true){
                convexLocation();
                FindArea();
                steped = false;
            }
            if (centroid_found == true){
                g.drawOval(centroid.x, centroid.y, 5, 5);
                g.fillOval(centroid.x, centroid.y, 5, 5);
            }            
            if (midPoint_found == true){
                g.setColor(Color.blue);
                g.drawOval(P.Get(mid).x - 5, P.Get(mid).y - 5, 15, 15);
                g.setColor(Color.black);
            }
            
            if (highlight == true){
                g2d.setColor(Color.black);
                g2d.setStroke(bold);
                g2d.drawLine(P.Get(ini).x + 2, P.Get(ini).y + 2, 
                        P.Get(end % P.Size()).x + 2, P.Get(end % P.Size()).y + 2);
                g2d.setStroke(standard);
            }
            
            if (inter_found == true){
                g2d.setColor(Color.black);
                g2d.drawOval(inter1.x -2 , inter1.y - 2, 5, 5);
                g2d.fillOval(inter1.x - 2, inter1.y - 2, 5, 5);
                g2d.drawOval(inter2.x - 2, inter2.y - 2, 5, 5);
                g2d.fillOval(inter2.x - 2, inter2.y - 2, 5, 5);
                
                //Desenha uma linha do centroide até esses pontos
                g2d.setColor(Color.blue);
                g2d.drawLine(inter1.x, inter1.y, centroid.x + 2, centroid.y + 2);
                g2d.drawLine(inter2.x, inter2.y, centroid.x + 2, centroid.y + 2);
                g2d.setColor(Color.black);
            }
        }    
        
        //Desenha os pontos do polígono construído até agora
        for (int i=0; i<P.Size(); i++){
            Point curr = P.Get(i);
            g.drawOval(curr.x, curr.y, 5, 5);
            g.fillOval(curr.x, curr.y, 5, 5);
            //g.drawString("" + i + "",curr.x+5,curr.y+5);
        }
        
        //TODO substituir o segmento pela reta 
        if (divisor_found == true){
            //Desenha uma aresta ligando P[0] e P[divisor]
            g.drawLine(P.Get(0).x + 2, P.Get(0).y + 2, P.Get(divisor).x + 2, P.Get(divisor).y + 2);                    
        }
        
        if (binPoints_found == false) {
            //Desenha as arestas do polígono construído até agora
            for (int i=1; i<P.Size(); i++){
                Point curr = P.Get(i);
                Point prev = P.Get(i-1);
                g.drawLine(curr.x + 2, curr.y + 2, prev.x + 2, prev.y + 2);
            }
            //Desenha a aresta para fechar o polígono
            if (is_polygon == true){
            Point curr = P.Get(P.Size()-1);
            Point prev = P.Get(0);
            g.drawLine(curr.x + 2, curr.y + 2, prev.x + 2, prev.y + 2);
        }

        }
        else {
            //Desenha as arestas do polígono construído até agora
            for (int i=poly_ini; i<poly_end; i++){
                int j = (i+1) % P.Size();
                g2d.drawLine(P.Get(i).x + 2, P.Get(i).y + 2, P.Get(j).x + 2, P.Get(j).y + 2);
            }
            g2d.setStroke(dashed);
            for (int i=poly_end % P.Size(); i != poly_ini; i=(i+1) % P.Size()){
                int j = (i+1) % P.Size();
                g2d.drawLine(P.Get(i).x + 2, P.Get(i).y + 2, P.Get(j).x + 2, P.Get(j).y + 2);
            }
            g2d.setStroke(standard);
        }        
        //Desenha o ponto de consulta
        if (query_found == true){
            g.drawOval(query.x + 2, query.y + 2, 5, 5);
            g.fillOval(query.x + 2, query.y + 2, 5, 5);
        }
        

        offset = 460;
        Point stringOffset = new Point(20, 405);
        if (is_polygon == true){
            //Renderiza diferente dependendo de cada tipo de polígono
            
            if (P.GetType() != PolygonType.CONVEX){
                g.drawString(msg + translator.localize("notConvex"), stringOffset.x, stringOffset.y);
            }
            else if (query_found == false){
                g.drawString(msg + translator.localize("choosePoint"), stringOffset.x, stringOffset.y);
            }
            else if (solved == true){
                if (inside)
                    g.drawString(msg + translator.localize("internalPoint"), stringOffset.x, stringOffset.y);
                else
                    g.drawString(msg + translator.localize("externalPoint"), stringOffset.x, stringOffset.y);
            }
            else {
                g.drawString(msg + translator.localize("executing") + "...", stringOffset.x, stringOffset.y);
            }
        }
        else {
            g.drawString(msg + translator.localize("buildingPolygon"), stringOffset.x, stringOffset.y);
        }
    }
    public void mouseClicked (MouseEvent me){
        if (active && stage == 0){
            Point p = new Point(me.getX(), me.getY());
            if (is_polygon == false){
                P.Insert(p);
                if (P.Size() >= 3) 
                    buttonClose.setEnabled(true);
            }
            else if (query_found == false && P.GetType() == PolygonType.CONVEX) {
                query = p;
                query_found = true;
                if (P.GetType() == PolygonType.CONVEX){
                    buttonNext.setEnabled(true);
                    buttonEdit.setEnabled(true);
                }
            }
            repaint();
        }
    }
    public void mouseEntered (MouseEvent me){
    }
    public void mouseMoved (MouseEvent me){ 
        pos = new Point(me.getX(), me.getY());
        if (pos.x >= lower_bound.x && pos.x <= upper_bound.x &&
            pos.y >= lower_bound.y && pos.y <= upper_bound.y)
            active = true;
        else
            active = false;
        repaint();  
    }
    public void mousePressed (MouseEvent me){
    }
    public void mouseReleased (MouseEvent me){
    } 
    public void mouseExited (MouseEvent me){
    }  
    public void mouseDragged (MouseEvent me){
    }  
    public void actionPerformed(ActionEvent evt){
        
        //Fecha o polígono
        if (evt.getSource() == buttonClose){
            is_polygon = true;
            buttonClose.setEnabled(false);
            //Assumimos que o poligono esta no sentido horario
            P.MakeCW();
            repaint();
        }
        else if (evt.getSource() == buttonNext){
            stage++;
            steped = true;
            repaint();
        }
        else if (evt.getSource() == buttonReset){
            reset();
            repaint();
        }
        else if (evt.getSource() == buttonEdit){
            query_found = false;
            restart();
            repaint();
        }
    }
}