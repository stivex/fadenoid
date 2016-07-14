import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;


public class EspaiDeJoc extends Canvas implements Runnable, KeyListener, MouseListener, iCanvisBola, iCanvisBloc, iCanvisPremi {
	
	//Atributs de la classe
	private Image subImatge;
	private Bloc blocs[][]; //Array bidimensional que contrindrà el blocs
	private Paleta pala; //Pala del joc
	private Bola bola; //Bola del joc
	private Thread filBola;
	private Image imgDeFons; //Hi guardarem la imatge de fons del joc		
	private List listListeners; //Llista on es guardaran tots els objectes que escoltaran els events d'aquesta classe EspaiDeJoc, seran els que notificarem quan es produeixi un event de EspaiDeJoc
	private int numVides; //Guardem el nombre de vides de la partida
	private int numBlocs; //Inicialment tindrà el valor del nombre total de blocs a destruïr, al final, quan valgui 0, significarà que s'ha acabat la partida
	private int idTxtPantalla; //Identificador del fitxer txt on hi ha guardada la matriu de blocs
	private List<Bola> listBoles;
	private boolean modeDemo; //per indicat de la partida funcionarà en mode demostració
	
	//Per contabilitzar els premis que es podran adjudicar durant el joc d'aquesta Pantalla	
	private int numTotalPremis;
	private List<Premi> listPremis; //Guardarem en una llista els premis generats durant aquesta pantalla
	
	
	
	public EspaiDeJoc(int ample, int alt, int numeroDeVides, int idMatriuBlocs, boolean demo){		
		
		//Establim la mida del tauler
		this.setSize(ample, alt);
		
		//Establim el nombre de vides inicial
		this.listListeners = new ArrayList();
		this.numVides = numeroDeVides;
		this.idTxtPantalla = idMatriuBlocs;
		
		//Definim la imatge de fons del tauler de joc		
		try {
			BufferedImage fonsI;
			fonsI = ImageIO.read(new File("./img/fons.jpg"));
			this.imgDeFons = fonsI.getScaledInstance(this.getWidth(), this.getHeight(), Image.SCALE_DEFAULT);			
		} catch (IOException e) {
			e.printStackTrace();
		}		
				
		//Creem els blocs
		this.numBlocs = 0;
		this.crearBlocs();
		
		//Creem una matriu de premis tan gran com la suma de tots els premis possibles a donar
		this.numTotalPremis = 10;
		this.listPremis = new ArrayList<Premi>();
						
		//Instanciem una pala de joc, indicant en el contructor la posició inicial
		this.pala = new Paleta(this.getWidth() - (this.getWidth()/2), this.getHeight()-90);
		
		//Crearem una bola del joc
		this.crearBolaDeJoc(false);	
		
		//Instanciem una llista o guardarem totes les boles extra
		this.listBoles = new ArrayList<Bola>(); 
		
		//Els moviments de ratolí que succeeixin a EspaiDeJoc es comunicaran a Pala
		this.addMouseMotionListener(pala);
		this.addKeyListener(pala);
		this.addKeyListener(this);
		this.addMouseListener(this);
		
		this.modeDemo = demo;
		
		//Reproduïm un so, indicant que s'inicia la partida/pantalla
		EfecteDeSo.ReproduirIniciPartida();	
		
		if(this.modeDemo){
			if(this.bola != null){
				this.bola.activarBola();
			}
		}
				
	}
	
	public Paleta getPaleta(){
		return pala;
	}
	
	public void paint(Graphics g){
		//Aquest mètode es crida cada cop que cal pintar la pantalla
		//Ja sigui per primer cop, o bé perquè s'ha sol·licitat pintar-la de nou
		
				
		Image imgBuffer = null;
		Graphics imgTemp;
		
		//Creo una imatge temporal
		if (imgBuffer == null) {
			//La inicialitzo, amb les mides del EspaiDeJoc
			imgBuffer = createImage(this.getWidth(), this.getHeight());
		}
		imgTemp = imgBuffer.getGraphics();
		
		//A gPanell (lloc de dibuix temporal), hi dibuixarem tots els sub elements a pintar dins
		Graphics gPanell = imgBuffer.getGraphics();		
		gPanell.clearRect(0, 0, this.getWidth(), this.getHeight());
		
		//Definim la imatge de fons del tauler de joc
		gPanell.drawImage(this.imgDeFons, 0, 0, this);		
		
		//Dibuixem a gPanell els blocs				
		for(int i=0; i < blocs.length; i++){
			for(int j=0; j<blocs[i].length; j++){
				blocs[i][j].paint(gPanell);
			}
		}
		
		//Dibuixem la bola del joc
		if(this.bola != null){
			this.bola.paint(gPanell);
		}
		
		//Dibuixem la resta de boles extra
		for (Iterator<Bola> it = this.listBoles.iterator(); it.hasNext(); ) {
            it.next().paint(gPanell);	            
		}
		
		//Dibuixem els premis
		for (Iterator<Premi> it = this.listPremis.iterator(); it.hasNext(); ) {
	            it.next().paint(gPanell);	            
        }
		
		//Dibuixem a gPandell la pala del joc
		this.pala.paint(gPanell);
		
		//Pinto tot de cop, per evitar l'efecte parpalleig (doubleBuffering)
		g.drawImage(imgBuffer, 0, 0, this);
		
	}
	
    public void update (Graphics g){
    	//sobrescrit el mètode update per tal que no borri, només pinti
        paint(g);
    }

	//Aquest mètode s'executa en un fil d'execusió per separat
	//És l'ecarregat de refrescar constantment l'EspaiDeJoc
	//Es desencadena des del programa principal
	public void run(){
		for(;;){
			this.repaint(); //Fa que es cridi el mètode Update()
			
			try{
				Thread.sleep(10);
			}
			catch (InterruptedException e) {
				// l'sleep del thread ha estat interromput per un altre thread			
			}
		}
	}
	
	//Mètodes que cal definir perquè la classe implementi la interfície KeyListener
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE){
			//En prèmer l'espai del teclat, farem que la bola comencia circular
			if(this.bola != null){
				this.bola.activarBola();
			}
		}
	}

	public void keyReleased(KeyEvent e) {}

	public void keyTyped(KeyEvent e) {}
	
	//Mètodes que cal definir perquè la classe implementi la interfície MouseListener
	public void mouseClicked(MouseEvent arg0) {
		if (arg0.getButton() == 1){
			//En prèmer el botó esquerre del ratolí, farem que la bola comencia circular
			if(this.bola != null){
				this.bola.activarBola();
			}
		}
	}

	public void mouseEntered(MouseEvent arg0) {}

	public void mouseExited(MouseEvent arg0)  {}

	public void mousePressed(MouseEvent arg0) {}

	public void mouseReleased(MouseEvent arg0){}
	
	private void crearBlocs() {

		int posicioX = 0;
		int posicioY = 0;
		int midaAlt = 30;
		int midaAmple = 51;
		this.blocs = new Bloc[20][20];
		
		//Obtenim el fitxer txt on hi ha la configuració de la disposició/disseny de blocs (línia a línia)
		FileReader fr = null;
		try {
			//Obro el fitxer on hi la la matriu configurada
			fr = new FileReader("./pantalles/" + this.idTxtPantalla + ".txt");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		BufferedReader bf = new BufferedReader(fr);
		String linia;
		try {
			//Llegeixo línia a línia el fitxer txt
			int i = 0;
			while ((linia = bf.readLine()) != null) {
				//Llegeixo caràcter a caràcter la informació d'una línia
				for (int j = 0; j < linia.length(); j++){
					this.blocs[i][j] = new Bloc(posicioX ,posicioY ,midaAmple ,midaAlt, Integer.parseInt(Character.toString(linia.charAt(j))));
					this.blocs[i][j].addCanvisBlocListener(this);	
					posicioX = posicioX + midaAmple;					
				}						
				posicioX = 0;
				posicioY = posicioY + midaAlt;
				i++;
			}
			
			//Fem recompte dels blocs que s'ha de destruïr			
			for (i=0; i < this.blocs.length; i++){
				for (int j=0; j < this.blocs[i].length; j++){
					if (!this.blocs[i][j].blocTrencat()){
						this.numBlocs++;
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	private void crearBolaDeJoc(boolean bolaDePremi){
		
		Bola b = null;
		if(!bolaDePremi){
			b = this.bola;
		}		
		
		//Si la bola de joc no està creada, la crearem
		if(b == null){			
			b = new Bola(this, bolaDePremi);
			filBola = new Thread(b); //creem un nou procés
			b.addCanvisBolaListener(this); //Perquè poguem escoltar quan s'ha empassat/perdut la bola
			
			//Fem que la bola escolti cada un dels blocs, així si un bloc es trenca la bola ho sabrà interpretar que ha col·losionat i ha de fer un canvi de direcció
			for(int i=0; i < this.blocs.length; i++){
				for(int j=0; j < this.blocs[i].length; j++){
					this.blocs[i][j].addCanvisBlocListener(b);
				}
			}
			
			//Engeguem el fil d'execusió independent per a cada bola
			filBola.start();
		}
		
		//Si és una Bola de premi (extra) l'afegim a una llista per tal de poder-la repintar quan sigui necessari
		//En cas contrari, haurem creat la Bola principal del joc
		if(bolaDePremi){
			this.listBoles.add(b);
			b.activarBola();
		}else{
			this.bola = b;
		}
		
	}
	
	//Mètode que es desencadenarà quan rebem una notificació que la bola actual de joc s'ha empassat/perdut (event)
	public void bolaPerduda(EventObject e) {
		
		//Reproduïm un so 
		EfecteDeSo.ReproduirBolaPerduda();
		
		//Destruïm la bola actual
		this.bola = null;
		//Descontem una vida
		this.numVides = this.numVides -1; 
		
		if(this.numVides == 0){
			//Tornem a començar el joc
			this.notificarPartidaPerduda();
		}else{
			//Preparem una nova bola
			this.notificarVidaPerduda();
			this.crearBolaDeJoc(false);
		}
	}
	
	public void bolaCanviPosicio(Bola b){			
		//Comuniquem als blocs quina és la posició actual de la bola, i ells decidiran si s'han de destruïr
		for(int i=0; i < blocs.length; i++){
			for(int j=0; j<blocs[i].length; j++){				
				blocs[i][j].trencarBloc(b);
			}
		}
		
		if(this.modeDemo){
			this.pala.MourePaleta(b.getPosicioX(), b.getPosicioY());
		}
	}

	public void blocDestruit(Bloc b, BlocTocat t, boolean destruit) {
		
		this.notificarPuntuacio(b.getPunts());
		
		//Hi ha blocs que per destruir-los cal tocar-los més d'un cop, per tant, no cada cop que es toqui un bloc s'ha de comptabilitzar com a destruït
		if(destruit){
			this.numBlocs--;
			
			//Mirem si hem assignat tots els premis
			if(this.listPremis.size() < this.numTotalPremis ){
				
				//Un cop destruït el bloc, mirem si generem un premi (ho decidim aleatoriament)				
				if(this.numBlocs > 0){
												
					Random genVal = new Random();
					int val = genVal.nextInt(this.numBlocs);
					
					if(val < (this.numBlocs / this.numTotalPremis)){
						
						//Decideixo aleatoriament quin tipus de premi crearem (hi ha 4 tipus de premis)
						int tipusPremi = genVal.nextInt(4)+1;
						
						//Generem el premi						
						Premi p = new Premi(this, b.getPosicioBlocX()+ (b.getAmplada()/2) - 15 , b.getPosicioBlocY(), 30, 15, this.getSize().height, tipusPremi);
						this.listPremis.add(p);
						p.addCanvisPremiListener(this);					
						Thread filPremi = new Thread(p);
						filPremi.start();
					}
				
				}
			
			}
			
		}		
		
		//Si ja no queden més blocs a aquesta pantalla, notificarem que s'ha acabat aquesta pantalla de joc
		if (this.numBlocs == 0){
			this.notificarFinalPantalla();
		}		
		
	}
	
	//Quan es reculli un premi es desencadenarà aquest mètode
	public void premiRecollit(Premi p) {
		if (p.getTipusPremi() == 1){ //Si el premi és una nova vida extra
			this.numVides = this.numVides + 1;
			this.notificarVidaExtra();
		}else if (p.getTipusPremi() == 2){ //Incrementem la velocitat habitual de la bola
			this.bola.velocitatBolaRapida();
		}else if (p.getTipusPremi() == 3){ //Si el premi és allargar la paleta			
			this.pala.setPaletaExtra(true);
		}else if (p.getTipusPremi() == 4){ //Afegim una bola extra que jugui
			this.crearBolaDeJoc(true);			
		}
	}

	//Aquest mètode serà cridat per tots els objectes que voldran ser notificats quan es produeixi un event d'un objecte EspaiDeJoc
	public synchronized void addCanvisEspaiDeJocListener(iCanvisEspaiDeJoc listener) {
		this.listListeners.add(listener);
	}
	
	//Aquest mètode servirà perquè un objecte que fins ara volia ser notificat dels events de EspaiDeJoc, a partir d'ara ja no vol ser notificat
	public synchronized void removeCanvisEspaiDeJocListener(iCanvisEspaiDeJoc listener) {		
		this.listListeners.remove(listener);
	}
	
	//Mètode que notificarà a tots els objectes que volen ser notificats, que s'ha perdut la bola i que cal decrementar en una vida
	private void notificarVidaPerduda(){
		
		//Treiem tots els premis que se li havien concedit al jugador
		if(this.pala != null){
			this.pala.setPaletaExtra(false);
		}
		if(this.bola != null){
			this.bola.velocitatBolaNormal();
		}
		
		//Notifiquem
		List lst;
		EventObject evt = new EventObject(this);
		synchronized(this) {
			lst = (List)((ArrayList)this.listListeners).clone();
		}

		Iterator it = lst.iterator();
		while ( it.hasNext() ){
			((iCanvisEspaiDeJoc)it.next()).vidaPerduda();
		}
		
	}
	
	//Mètode que notificarà a tots els objectes que volen ser notificats, que es dona una nova vide de regal extra
	private void notificarVidaExtra(){
		
		List lst;
		EventObject evt = new EventObject(this);
		synchronized(this) {
			lst = (List)((ArrayList)this.listListeners).clone();
		}

		Iterator it = lst.iterator();
		while ( it.hasNext() ){
			((iCanvisEspaiDeJoc)it.next()).vidaExtra();
		}
		
	}
	
	//Mètode que notificarà a tots els objectes que volen ser notificats, que l'EspaiDeJoc s'ha podroduït un canvi a comunicar
	private void notificarPartidaPerduda() {
		
		List lst;
		EventObject evt = new EventObject(this);
		synchronized(this) {
			lst = (List)((ArrayList)this.listListeners).clone();
		}

		Iterator it = lst.iterator();
		while ( it.hasNext() ){
			((iCanvisEspaiDeJoc)it.next()).jocPerdut();
		}
		
	}
	
	//Mètode que notificarà a tots els objectes que volen ser notificats, que l'EspaiDeJoc se li ha comunicat que s'ha trencat blocs i per tant, cal comunicar la nova puntuació
	private void notificarPuntuacio(int punts) {
		
		List lst;
		EventObject evt = new EventObject(this);
		synchronized(this) {
			lst = (List)((ArrayList)this.listListeners).clone();
		}

		Iterator it = lst.iterator();
		while ( it.hasNext() ){
			((iCanvisEspaiDeJoc)it.next()).novaPuntuacio(punts);
		}
		
	}
	
	//Mètode que notificarà a tots els objectes que volen ser notificats, que s'ha completat amb èxit la pantalla actual del joc
	private void notificarFinalPantalla() {
		
		//Fem que la bola que fins ara teniem en joc deixi de notificar-nos
		this.bola.removeCanvisBolaListener(this);
				
		List lst;
		EventObject evt = new EventObject(this);
		synchronized(this) {
			lst = (List)((ArrayList)this.listListeners).clone();
		}

		Iterator it = lst.iterator();
		while ( it.hasNext() ){
			((iCanvisEspaiDeJoc)it.next()).finalPantalla();
		}
		
	}
	
}
