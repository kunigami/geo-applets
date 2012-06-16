package me.kuniga.compgeo;


import java.awt.BasicStroke;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Timer;
import java.util.TimerTask;


/**
 *
 * @author kunigami
 */
public class Graham extends AppletEngine implements 
 MouseMotionListener, MouseListener, ActionListener
{

    //Lista de pontos
    Polygon P;
    //Poligono representando o casco convexo
    Polygon CH;
    //Polígono estrelado gerado pelo conjunto de pontos
    Polygon Star;
    
    //Lista de pontos no casco convexo
    int[] inHull;
    //Índice de P tal que P[i0] é o primeiro ponto do casco convexo
    int i0;
    
    //Botao para calcular o casco convexo
    Button buttonCH;
    //Botao para avançar um passo no algoritmo
    Button buttonStep;
    //Botao para reiniciar o applet
    Button buttonReset;
    //Botao para executar o algoritmo automaticamente
    Button buttonAuto;
    //Botao que cessa a execucao automatica
    Button buttonStop;
    
    Point pos;

    //Ponto que está sendo usado como pivot
    Point pivot;
    Boolean pivot_found;
    //Ponto que tentará virar curr como sendo o mais a direita
    Point next;
    Boolean next_found;
    
    //Define o limite da area de desenho
    Point lower_bound, upper_bound;

    //Decide se esta dentro da area de trabalho
    Boolean active;
    //Decide se eh pra desenhar o casco convexo
    Boolean show_hull;
    //Variável usada para só executar o casco quando houver mudanças
    Boolean steped;
    //Decide se é para mostrar o polígono estrelado
    Boolean show_star;
    
    //Define qual estagio da maquina de estado estamos
    int stage;
    
    Timer timer;
    
    //Define um estilo de linha pontilhada

    Stroke width2 = new BasicStroke(2);
         
    
    Dimension offDimension;
    Image offImage;
    Graphics offGraphics;
    
     public void update(Graphics g) {
	Dimension d = getSize();

	// Create the offscreen graphics context
	if ((offGraphics == null)
	 || (d.width != offDimension.width)
	 || (d.height != offDimension.height)) {
	    offDimension = d;
	    offImage = createImage(d.width, d.height);
	    offGraphics = offImage.getGraphics();
	}

	// Erase the previous image
	offGraphics.setColor(getBackground());
	offGraphics.fillRect(0, 0, d.width, d.height);
	offGraphics.setColor(Color.black);

	// Paint the frame into the image
	paint(offGraphics);

	// Paint the image onto the screen
	g.drawImage(offImage, 0, 0, null);
    }

    
    protected void reset(){
        //Inicializa um polígono vazio
        P = new Polygon();
        CH = new Polygon();

        //Inicializa a lista de pontos no casco
        inHull = new int[0];
                
        pos = new Point(100,200);
        lower_bound = new Point(10,50);
        upper_bound = new Point(590,385);
        
        show_hull = false;
        stage = 0;
        active = false;

        //Inicializa todos os pontos
        pivot = new Point();
        next  = new Point();
        pivot_found = false;
        next_found  = false;
        
        buttonCH.setEnabled(false);
        buttonStep.setEnabled(false);  
        buttonAuto.setEnabled(false);   
        buttonStop.setEnabled(false);
                 
        steped = false;
        show_star = false;
    }
    protected void restart(){
        inHull = new int[P.Size()];
        CH = new Polygon();
        show_hull = true;
        show_star = false;
        stage = 0;
        steped = true;
        buttonStep.setEnabled(true);
        buttonAuto.setEnabled(true);
    }
    
    public int GrahamScanIterator(){
            
        int pivot_id, curr_id, next_id;
        int curr_stage = 0;
        pivot_found = false;
        next_found  = false;
        
        if (curr_stage == stage) return curr_stage;
        curr_stage++;
        
        CH = new Polygon();
        Star = P;
               
        pivot = P.GetLeast();
        pivot_id = i0 = P.GetLeastIndex();

        pivot_found = true;
        if (curr_stage == stage) return curr_stage;
        curr_stage++;
        
        Star.Sort(pivot_id);
        
        pivot_found = false;
        show_star = true;
        
       if (curr_stage == stage) return curr_stage;
       curr_stage++;
       
       CH.Insert(Star.Get(0));
       CH.Insert(Star.Get(1));
       
       if (curr_stage == stage) return curr_stage;
       curr_stage++;
       
       for (int i=2; i<Star.Size(); i++){
       
           next = Star.Get(i);
           next_found = true;
           if (curr_stage == stage) return curr_stage;
           curr_stage++;

           while (true){
               Point top = CH.Get(CH.Size()-1);
               Point top1 = CH.Get(CH.Size()-2);
               
               if (Essentials.PointCcw(next, top1, top) <= 0){
                   break;
               }           
               CH.PopBack();
               
               if (curr_stage == stage) return curr_stage;
               curr_stage++;

           }
           CH.Insert(next);

           if (curr_stage == stage) return curr_stage;
           curr_stage++;
        }
        
       CH.Insert(P.Get(i0));
       show_hull   = false;
       next_found = false;
       buttonStep.setEnabled(false);
       buttonAuto.setEnabled(false);
       buttonStop.setEnabled(false);
       buttonReset.setEnabled(true);
       buttonCH.setEnabled(true);
              
        return 0;
    }
    
    /** Initialization method that will be called after the applet is loaded
     *  into the browser.
     */
    public void init() {

    	String country = super.getParameter("country");
    	String language = super.getParameter("language");

    	Translator translator = new Translator(language, country);
  	
        addMouseListener(this);
        addMouseMotionListener(this);

        setLayout(null);
        
        Color bg = new Color(245, 245, 245);
        setBackground (bg);
        int buttonWidth = 110;
        int offX = 10;
        
        //Botao para calcular o casco convexo
        buttonCH = new Button(translator.localize("start"));
        add(buttonCH);
        buttonCH.setBounds(offX, 10, buttonWidth, 30);
        buttonCH.addActionListener(this);
  
        
        //Botao para o proximo passo do casco convexo
        buttonStep = new Button(translator.localize("next"));
        add(buttonStep);
        buttonStep.setBounds(offX + buttonWidth, 10, buttonWidth, 30);
        buttonStep.addActionListener(this);
             

        //Botao para executar o applet automaticamente
        buttonAuto = new Button(translator.localize("auto"));
        add(buttonAuto);
        buttonAuto.setBounds(offX + buttonWidth * 2,10, buttonWidth, 30);
        buttonAuto.addActionListener(this);
        
        
        //Botao que cessa a execução automática do applet
        buttonStop = new Button(translator.localize("stop"));
        add(buttonStop);
        buttonStop.setBounds(offX + buttonWidth * 3,10, buttonWidth, 30);
        buttonStop.addActionListener(this);
        
        //Botao para resetar o applet
        buttonReset = new Button(translator.localize("reset"));
        add(buttonReset);
        buttonReset.setBounds(offX + buttonWidth * 4,10, buttonWidth, 30);
        buttonReset.addActionListener(this);
        
        reset();
    }
   

    // TODO overwrite start(), stop() and destroy() methods
    public void paint(Graphics g) {
        
        Graphics2D g2d = (Graphics2D)g;
        
        g.drawRect(lower_bound.x, lower_bound.y, 
                        upper_bound.x-lower_bound.x, 
                        upper_bound.y-lower_bound.y);

                
        if (steped && show_hull){
            GrahamScanIterator();
            steped = false;
        }
        
        if (show_star){    
            //Desenha os pontos na tela
            for (int i=0; i<Star.Size(); i++){
                Point p_star = Star.Get(i);
                g.drawOval(p_star.x, p_star.y, 5, 5);
                g.fillOval(p_star.x, p_star.y, 5, 5);
                g.drawString("" + i, p_star.x + 10, p_star.y + 10);
                //Desenha arestas entre pontos
                Point next_star = P.Get((i + 1) % Star.Size());
                g2d.setStroke(dashed);
                g2d.drawLine(p_star.x+2, p_star.y+2, next_star.x+2, next_star.y+2);
            }
            
        }
        else {
            //Desenha os pontos na tela
            for (int i=0; i<P.Size(); i++){
                Point p = P.Get(i);
                g.drawOval(p.x, p.y, 5, 5);
                g.fillOval(p.x, p.y, 5, 5);
            }        
        }
        if (active && stage == 0){
               g.drawString("("+(pos.x-lower_bound.x)+","+(upper_bound.y-pos.y)+")",pos.x,pos.y);
        }
        
        //Desenha o que temos de casco convexo até agora
        g2d.setStroke(width2);
        for (int i=1; i<CH.Size(); i++){
            Point p = CH.Get(i);
            Point q = CH.Get(i-1);
            
            g.drawLine(p.x+2, p.y+2, q.x+2, q.y+2);
        }
        g2d.setStroke(standard);
        //Identifica o ponto sendo analisado
        if (next_found == true){
            g.setColor(Color.green);
            g.drawOval(next.x-5, next.y-5, 15, 15);
            
            //Desenha uma linha extra entre o último ponto do Casco e este ponto
            Point last = CH.Get(CH.Size()-1);
            g.drawLine(last.x+2, last.y+2, next.x+2, next.y+2);
        }
        if (pivot_found == true){
            g.setColor(Color.red);
            g.drawOval(pivot.x-5, pivot.y-5, 15, 15);
        }

    }
    /* Metodos que implementam o mouse listener */
    public void mouseClicked (MouseEvent me)
    {
            if (active && stage == 0){
                    Point p = new Point(me.getX(), me.getY());
                    P.Insert(p);
                    repaint();
                    if (P.Size() >= 3) 
                        buttonCH.setEnabled(true);
            }
    }
    /* TESTE: mostra as coordenadas de onde o mouse esta */
    public void mouseEntered (MouseEvent me)  {}

    public void mouseMoved (MouseEvent me)
    {
            pos = new Point(me.getX(), me.getY());
            if (pos.x >= lower_bound.x && pos.x <= upper_bound.x &&
                            pos.y >= lower_bound.y && pos.y <= upper_bound.y)
                    active = true;
            else
                    active = false;

            repaint();    
    }
    private void automaticExecution () {
        int initialDelay = 0;
        int period = 300;   
        
        timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                stage++;
                steped = true;
                repaint();    
                if (show_hull == false) timer.cancel();
            }
        };
        timer.scheduleAtFixedRate(task, initialDelay, period);     
    }
    
    public void mousePressed (MouseEvent me)  {}
    public void mouseReleased (MouseEvent me) {} 
    public void mouseExited (MouseEvent me)   {}  
    public void mouseDragged (MouseEvent me)   {}  

    public void actionPerformed(ActionEvent evt) 
    {
            if (evt.getSource() == buttonCH){
                    restart();
                    repaint();
            }
            else if (evt.getSource() == buttonStep){
                    stage++;
                    steped = true;
                    repaint();
            }
            else if (evt.getSource() == buttonReset){
                    reset();
                    repaint();
            }
            else if (evt.getSource() == buttonAuto){
                    buttonStep.setEnabled(false);
                    buttonStop.setEnabled(true);
                    buttonReset.setEnabled(false);
                    buttonCH.setEnabled(false);
                    automaticExecution();
                    buttonAuto.setEnabled(false);
                            
            }
            else if (evt.getSource() == buttonStop){
                    buttonStep.setEnabled(true);
                    buttonAuto.setEnabled(true);
                    buttonReset.setEnabled(true);
                    buttonCH.setEnabled(true);
                    timer.cancel();
                    buttonStop.setEnabled(false);
            }
    }
}
