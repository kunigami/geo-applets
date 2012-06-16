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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author guilherme kunigami
 */
public class Triangulation extends AppletEngine implements 
 MouseMotionListener, MouseListener, ActionListener {
    
    //Representa o polígono a ser triangulado
    Polygon P;
    //Representa o polígono sem alguns vértices (depois de arrancar as orelhas)
    Polygon Q;
    //Usando dois polígonos podemos simular uma lista de arestas
    ArrayList<Point> Edge_List_U;
    ArrayList<Point> Edge_List_V;
    
    //Representam a orelha
    int ear_curr;
    int ear_next;
    int ear_prev;    
    //Representa o lower endpoint da aresta de interseção
    int edge_endpoint;
    //Representa o ponto de teste
    int inner_point;
    
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
    //Botão para executar o algoritmo automaticamente
    Button buttonAuto;
    //Botão para parar a execução automática
    Button buttonStop;
    
    //Define o retângulo que representa a borda
    Point lower_bound;
    Point upper_bound;
    //Representa a localização do cursor
    Point pos;

     /*
     * Flags
     */
    //Decide se o cursor está dentro do retângulo
    Boolean active;
    //Decide se um polígono foi feito
    Boolean is_polygon;
    //Decide se é um polígono simples
    Boolean is_valid_polygon;
    //Decide se tem um ear_curr selecionado
    Boolean ear_curr_found;
    //Decide se tem um ear_next selecionado
    Boolean ear_next_found;
    //Decide se tem um ear_prev selecionado
    Boolean ear_prev_found;
    //Decide se tem a aresta da orelha
    Boolean ear_edge_found;
    //Decide se a orelha está pra fora
    Boolean outer_ear;
    //Decide se a aresta de interseção ja foi encontrada
    Boolean inner_point_test;
    //Decide se encontramos o ponto interno à orelha
    Boolean inner_point_found;
    //Decide se o algoritmo terminou de executar
    Boolean solved;
            
    /* Mensagem a ser impressa na tela */
    String msg;
    //Offset a partir de onde as mensagens começam
    int offset;
    
    Translator translator;
    
    
    protected void reset(){
        stage = 0;
        steped = false;
        active = false;
        is_polygon = false;
        is_valid_polygon = false;
        ear_curr_found = false;
        ear_next_found = false;
        ear_prev_found = false;
        ear_edge_found = false;
        outer_ear = false;
        inner_point_test = false;
        inner_point_found = false;
        solved = false;
    
        ear_curr = 0;
        ear_prev = 0;
        ear_next = 0;
        edge_endpoint = 0;
        
        /* Desativa os botões */
        buttonClose.setEnabled(false);
        buttonNext.setEnabled(false);
        buttonAuto.setEnabled(false);
        buttonStop.setEnabled(false);
        
        /* Inicializa os polígonos */
        P = new Polygon();
        Q = new Polygon();
        /* Inicializa a lista de arestas */
        Edge_List_U = new ArrayList<Point>();
        Edge_List_V = new ArrayList<Point>();
    }
    
    public int triangulation (){
        int curr_stage = 0;     

        Q = new Polygon(P);
        
        //Esvazia lista de arestas
        Edge_List_U = new ArrayList<Point>();
        Edge_List_V = new ArrayList<Point>();
                               
        if (curr_stage == stage) return stage;
        curr_stage++;
 
        int i = 0;
        while (Q.Size() > 3){
            //Escolhe um ponto qualquer
            ear_curr = i;
            ear_curr_found = true;
            ear_next_found = false;
            ear_prev_found = false;
            ear_edge_found = false;
            outer_ear = false;
            inner_point_test = false;
            inner_point_found = false;

            if (curr_stage == stage) {
                return stage;
            }
            curr_stage++;

            //Pega o proximo ponto e o anterior
            ear_next = (ear_curr + 1) % Q.Size();
            ear_prev = (ear_curr + Q.Size() - 1) % Q.Size();
            ear_next_found = true;
            ear_prev_found = true;

            //Traca uma aresta entre esses pontos
            ear_edge_found = true;

            //Decide se é uma orelha externa
            if (Essentials.PointCcw(Q.Get(ear_curr), Q.Get(ear_prev), Q.Get(ear_next)) == 1) {
                outer_ear = true;
                if (curr_stage == stage) {
                    return stage;
                }
                curr_stage++;
                i = (i+1) % Q.Size();
                continue;
            }
            if (curr_stage == stage) {
                return stage;
            }
            curr_stage++;
            //Decide se algum ponto está 'dentro' da orelha!
            Point a = Q.Get(ear_prev);
            Point b = Q.Get(ear_curr);
            Point c = Q.Get(ear_next);
            for (int j=(ear_next+1)%Q.Size(); j!=ear_prev; j = (j+1)%Q.Size()){
                
                inner_point_test = true;
                inner_point = j;
                if (curr_stage == stage) {
                    return stage;
                }
                curr_stage++;
                
                if ((Essentials.PointCcw(Q.Get(j), a, b) == 1) &&
                (Essentials.PointCcw(Q.Get(j), b, c) == 1) &&
                (Essentials.PointCcw(Q.Get(j), c, a) == 1)) {
                    //Encontrou um ponto dentro
                    inner_point_found = true;
                    inner_point_test = false;
                    
                    if (curr_stage == stage) {
                        return stage;
                    }
                    curr_stage++;
                    break;
                }
            }
            if (inner_point_found == false){
                //Se chegou até aqui é porque é uma orelha válida
                //Remove a orelha

                //Nao precisa andar com o 'i', exceto se este for o ultimo ponto
                if (i == Q.Size() - 1) {
                    i = 0;
                }
                Edge_List_U.add(new Point(Q.Get(ear_prev)));
                Edge_List_V.add(new Point(Q.Get(ear_next)));
                Q.Pop(ear_curr);            
            }
            else {
                i = (i + 1) % Q.Size();
            }
        }
        ear_curr_found = false;
        ear_next_found = false;
        ear_prev_found = false;
        ear_edge_found = false;
        outer_ear = false;
        inner_point_test = false;
        inner_point_found = false;
        solved = true;

        
        buttonClose.setEnabled(false);
        buttonNext.setEnabled(false);
        buttonAuto.setEnabled(false);
        buttonStop.setEnabled(false);
        buttonReset.setEnabled(true);
        return curr_stage;
    }
    @Override
    public void init(){
    	
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

        //Botao para executar o algoritmo automaticamente
        buttonAuto = new Button(translator.localize("auto"));
        add(buttonAuto);
        buttonAuto.setBounds(offX + buttonWidth * nButton, 10, buttonWidth, 30);
        buttonAuto.addActionListener(this);
        nButton++;

        //Botao para parar a execução automática
        buttonStop = new Button(translator.localize("stop"));
        add(buttonStop);
        buttonStop.setBounds(offX + buttonWidth * nButton, 10, buttonWidth, 30);
        buttonStop.addActionListener(this);
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

    
    @Override
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        //Desenha a borda da área de desenho
        g.drawRect(lower_bound.x, lower_bound.y,
                upper_bound.x - lower_bound.x,
                upper_bound.y - lower_bound.y);
        
        //Desenha a posição do cursor
        if (active && stage == 0){
            g.drawString("("+(pos.x-lower_bound.x)+","+(upper_bound.y-pos.y)+")",pos.x,pos.y);
        }
        
        if (is_valid_polygon == true) {
            
            if (steped == true) {
                triangulation();
                steped = false;
            }
            
            //Desenha os vértices da 'orelha'
            if (ear_curr_found == true) {
                g.setColor(Color.red);
                g.drawOval(Q.Get(ear_curr).x - 5, Q.Get(ear_curr).y - 5, 15, 15);
                g.setColor(Color.black);
            }
            if (ear_prev_found == true) {
                g.setColor(Color.blue);
                g.drawOval(Q.Get(ear_next).x - 5, Q.Get(ear_next).y - 5, 15, 15);
                g.drawOval(Q.Get(ear_prev).x - 5, Q.Get(ear_prev).y - 5, 15, 15);
                g.setColor(Color.black);            
            }
            if (ear_edge_found == true) {
                Point curr = Q.Get(ear_next);
                Point prev = Q.Get(ear_prev);
                g2d.setStroke(dashed);
                g2d.drawLine(curr.x + 2, curr.y + 2, prev.x + 2, prev.y + 2);
                g2d.setStroke(standard);
            }
            if (inner_point_test){
                g.setColor(Color.green);
                g.drawOval(Q.Get(inner_point).x - 5, Q.Get(inner_point).y - 5, 15, 15);              
                g.setColor(Color.black); 
            }
            if (inner_point_found){
                g.setColor(Color.red);
                g.drawOval(Q.Get(inner_point).x - 5, Q.Get(inner_point).y - 5, 15, 15);              
                g.setColor(Color.black);             
            }
        }
        
        //Desenha os pontos do polígono construído até agora
        for (int i = 0; i < P.Size(); i++) {
            Point curr = P.Get(i);
            g.drawOval(curr.x, curr.y, 5, 5);
            g.fillOval(curr.x, curr.y, 5, 5);
        }
        
        if (is_valid_polygon == false) {
            //Desenha as arestas do polígono construído até agora
            for (int i = 1; i < P.Size(); i++) {
                Point curr = P.Get(i);
                Point prev = P.Get(i - 1);                
                g.drawLine(curr.x + 2, curr.y + 2, prev.x + 2, prev.y + 2);                
            }
            //Desenha a aresta para fechar o polígono
            if (is_polygon == true) {
                Point curr = P.Get(P.Size() - 1);
                Point prev = P.Get(0);
                g.drawLine(curr.x + 2, curr.y + 2, prev.x + 2, prev.y + 2);
            }
        }
        else {
            //Desenha as arestas do polígono construído até agora
            for (int i = 0; i < P.Size(); i++) {
                Point curr = P.Get(i);
                Point next = P.Get((i + 1) % P.Size());
                if (solved == false)
                    g2d.setStroke(dashed);
                g2d.drawLine(curr.x + 2, curr.y + 2, next.x + 2, next.y + 2);
                g2d.setStroke(standard);
            }        
            for (int i = 0; i < Q.Size(); i++) {
                Point curr = Q.Get(i);
                Point next = Q.Get((i + 1) % Q.Size());                
                g.drawLine(curr.x + 2, curr.y + 2, next.x + 2, next.y + 2);
            }
            //Desenha as arestas removidas
            for (int i=0; i< Edge_List_U.size(); i++){
                Point curr = Edge_List_U.get(i);
                Point next = Edge_List_V.get(i);
                if (solved == false)
                    g2d.setStroke(dashed);
                g2d.drawLine(curr.x + 2, curr.y + 2, next.x + 2, next.y + 2);
                g2d.setStroke(standard);            
            }
        }
        Point stringOffset = new Point(20, 405);
        if (is_polygon == true) {
            if (is_valid_polygon == false) {
                g.drawString(msg + translator.localize("polygonNotSimple"), stringOffset.x, stringOffset.y);
            } else if (ear_edge_found) {
                if (outer_ear){
                    g.drawString(msg + translator.localize("externalEar"), stringOffset.x, stringOffset.y);
                }
                else if (inner_point_test){
                    g.drawString(msg + translator.localize("searchingInternalPoint"), stringOffset.x, stringOffset.y);
                }
                else if (inner_point_found){
                    g.drawString(msg + translator.localize("internalPointFound"), stringOffset.x, stringOffset.y);
                }
                else {
                    g.drawString(msg + translator.localize("searchingEar"), stringOffset.x, stringOffset.y);
                }
            }
            else if (solved){
                g.drawString(msg + translator.localize("finishing"), stringOffset.x, stringOffset.y);
            }
            else {
                //Renderiza diferente dependendo de cada tipo de polígono
                g.drawString(msg + translator.localize("executing"), stringOffset.x, stringOffset.y);
            }
        } else {
            g.drawString(msg + translator.localize("buildingPolygon"), stringOffset.x, stringOffset.y);
        }
    }
    
    public void mouseDragged(MouseEvent me) {}

    public void mouseMoved(MouseEvent me) {
        pos = new Point(me.getX(), me.getY());
        if (pos.x >= lower_bound.x && pos.x <= upper_bound.x &&
                pos.y >= lower_bound.y && pos.y <= upper_bound.y) {
            active = true;
        } else {
            active = false;
        }
        repaint();
    }

    public void mouseClicked(MouseEvent me) {
        if (active && stage == 0) {
            Point p = new Point(me.getX(), me.getY());
            if (is_polygon == false) {
                P.Insert(p);
                if (P.Size() >= 3) {
                    buttonClose.setEnabled(true);
                }
            } 
            repaint();
        }
    }

    public void mousePressed(MouseEvent me) {}

    public void mouseReleased(MouseEvent me) {}

    public void mouseEntered(MouseEvent me) {}

    public void mouseExited(MouseEvent me) {}
    
    private void automaticExecution () {
        int initialDelay = 0;
        int period = 300;   
        
        timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                stage++;
                steped = true;
                if (solved == true) {
                    timer.cancel();
                }
                repaint();    
            }
        };
        timer.scheduleAtFixedRate(task, initialDelay, period);     
    }
    
    public void actionPerformed(ActionEvent evt) {

        //Fecha o polígono
        if (evt.getSource() == buttonClose) {
            is_polygon = true;
            buttonClose.setEnabled(false);

            //O poligono deve ser simples
            if (P.GetType() == PolygonType.CONVEX ||
                    P.GetType() == PolygonType.SIMPLE) {
                is_valid_polygon = true;
                buttonNext.setEnabled(true);
                buttonAuto.setEnabled(true);
                Q = new Polygon(P);
            }
            //Assumimos que o poligono esta no sentido horario
            P.MakeCW();
            repaint();
        } else if (evt.getSource() == buttonNext) {
            stage++;
            steped = true;
            repaint();
        } else if (evt.getSource() == buttonAuto) {
            buttonNext.setEnabled(false);
            buttonStop.setEnabled(true);
            buttonReset.setEnabled(false);            
            automaticExecution();
            buttonAuto.setEnabled(false);
        } else if (evt.getSource() == buttonStop) {
            buttonNext.setEnabled(true);
            buttonAuto.setEnabled(true);
            buttonReset.setEnabled(true);            
            timer.cancel();
            buttonStop.setEnabled(false);
        } else if (evt.getSource() == buttonReset) {
            reset();
            repaint();
        }
    }

}
