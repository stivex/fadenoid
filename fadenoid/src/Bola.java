import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;


import javax.imageio.ImageIO;


public class Bola extends Canvas implements Runnable, iCanvisBloc {
		
	private int posicioX; //Posició X (horitzontal) de la Bola
	private int posicioY; //Posició Y (vertical) de la Bola
	private EspaiDeJoc espJoc; //EspaiDeJoc per on es mourà la Bola
	private boolean bolaParada; //Ens indica si la bola es troba sobre la Paleta a punt per iniciar joc (true) o bé si la bola ja s'està movent (false)
	private boolean bolaPerduda; //Indica si la bola s'ha empassat i per tant, hem perdut una vida
	private int radi; //Radi de la bola (mida)
	private List listListeners; //Llista on es guardaran tots els objectes que escoltaran els events d'aquesta classe Bola, seran els que notificarem quan es produeixi un event de Bola
	private int velocitat; //El valor que especifica aquesta variable, com més alt sigui, més ràpida anirà la bola
	Color c; //Color de la bola
	private int posicioAntX; //Posicio anterior de la bola (abans posicioX)
	private int posicioAntY; //Posicio anterior de la bola (abans posicioY)
	private int incX;
	private int incY;
	private Button boto;
	private Image imgBola; 
	private int velocitatExtraBola; //Quan es reculli un Premi de velocitat, aquí inclourem la velocitat (rapidesa de desplaçament de la Bola)
	private boolean bolaExtra;
	
	public Bola(EspaiDeJoc espaiJoc, boolean bolaPremi) {
		
		this.bolaExtra = bolaPremi;
		this.velocitatExtraBola = 1;
		this.bolaPerduda = false;		
		this.radi = 7;
		this.posicioX = espaiJoc.getPaleta().getX();
		this.posicioY = espaiJoc.getPaleta().getY() - espaiJoc.getPaleta().getAltura(); //yy + 10;
		this.posicioAntX = 0;
		this.posicioAntY = 0;		
		this.velocitat=1;
		this.incX = this.velocitat;
		this.incY = this.velocitat;				
		c = new Color((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256)); //ens crea un color aleatori per la bola
		this.espJoc = espaiJoc;		
		this.bolaParada = true; //Indiquem que la bola està aturada a sobre a la paleta, a punt per començar
		this.listListeners = new ArrayList();
		
		boto = new Button();
		boto.setSize((this.getRadi()*2), (this.getRadi()*2));
		
		//Generem la imatge de la Bola de joc
		BufferedImage bolaI;
		ImageFilter bolaF;
		ImageProducer bolaP;
		try{	
			if(this.bolaExtra){
				bolaI = ImageIO.read(new File("./img/bola_extra.gif"));	
			}else{
				bolaI = ImageIO.read(new File("./img/bola.gif"));
			}							
			bolaF = new CropImageFilter(0, 0, (this.radi * 2), (this.radi * 2));//creem un sector d'imatge
			bolaP = new FilteredImageSource(bolaI.getSource(), bolaF);//indiquem a quina imatge volem aplicar el sector
			imgBola = createImage(bolaP);//creem la imatge a partir de palaP	
		}catch(Exception e){
			Eines.MostrarMissatgeError("No es pot carregar la imatge", e.toString());			
		}
	}	
	
	public Rectangle getBounds() {
		return new Rectangle(this.getPosicioX(), this.getPosicioY(), (this.radi*2), (this.radi*2));
	}
	
	public synchronized  void run(){		
			
		this.incX = this.velocitat;
		this.incY = this.velocitat;
		
		while(!this.bolaPerduda){
			
			if (this.bolaParada){
				//Quan la bola es troba sobre la paleta i no està en moviment la bola --> la bola seguirà la paleta
				this.posicioX = this.espJoc.getPaleta().getX() + this.espJoc.getPaleta().getCentrePaleta() - (this.radi/2);
			}else{
			
				//Calculem la nova posició X de la bola 
				if(this.posicioX+this.radi >= espJoc.getWidth()){
					this.incX = this.incX * (-1);
					this.posicioX = espJoc.getWidth()-radi;					
				}
				else if(this.posicioX <= 0){
					this.incX = this.incX * (-1);					
				}
				
				
				//Calculem la nova posició Y de la bola
				if(this.posicioY+this.radi >= espJoc.getPaleta().getY()){																									
					
					//Mirem si la Bola i la Paleta es toquen
					if(this.espJoc.getPaleta().getBounds().intersects(this.getBounds())){
											
						int posicioBola = this.posicioX + this.radi;
						int posicioPala = espJoc.getPaleta().getX(); //Ens dona la posició de la Paleta (respecte el punt de la meitat de la Paleta)
						int fragmentPaleta = espJoc.getPaleta().getAmplada()/7;
						int meitatFragmentPaleta = fragmentPaleta/2;												
						
						if (posicioBola < posicioPala){
							this.incX = this.incX * (-1); 
						}else{
							//this.incX = this.incX * (-1);
						}
											
						//Com que ha tocat la paleta, farem que reboti la Bola cap amunt
						this.incY = this.incY * (-1);
						
						//Reproduïm un so
						EfecteDeSo.ReproduirTocPala();
												
					}
					else{ 
						//Si no toquem la bola amb la pala, voldrà dir que s'ha empassat i per tant hem perdut una vida
						this.bolaPerduda = true;
						if(!this.bolaExtra){
							this.notificarBolaEmpassada(); //Desencadenem l'event que indicarà als objectes que volen ser notificats que la bola s'ha empassat
						}
					}					
				}else if(this.posicioY <= 0){
					this.incY = this.incY * (-1);					
				}
				
				
				//Establim el valor de la nova posició X de la bola
				this.posicioAntX = this.posicioX;
				this.posicioX = this.posicioX + (incX * this.velocitatExtraBola); //incX;
				//Establim el valor de la nova posició Y de la bola
				this.posicioAntY = this.posicioY; 
				this.posicioY = this.posicioY + (incY * this.velocitatExtraBola); //incY;
				
				//Aquí comunicarem a EspaiDeJoc que la bola ha canviat de posició, per tal EspaiDeJoc ho comuniqui a cada Bloc
				this.notificarBolaCanviPosicio();
				
			}


			try{
				//Fem que s'adormi el fil d'execusió un petit instant de temps, perquè sinó el fil d'execució consumiria tot el temps la CPU
				Thread.sleep((int)(Math.random() * 7)); //7 //this.velocitatExtraBola
			}
			catch (InterruptedException e) {
				// l'sleep del thread ha estat interromput per un altre thread			
			}
			
		}				
	}
		
	public void paint(Graphics g){
		if (!this.bolaPerduda){ 
			g.drawImage(imgBola, this.posicioX, this.posicioY, boto);
		}
	}
	public synchronized int getPosicioX(){
		return this.posicioX;
	}
	
	public int getPosicioY(){
		return this.posicioY;
	}
	
	public int getPosicioAntX(){
		return this.posicioAntX;
	}
	
	public int getPosicioAntY(){
		return this.posicioAntY;
	}
	
	public int getRadi(){
		return radi;
	}
	
	public void activarBola(){
		this.bolaParada = false;
	}
	
	public void pararBola(){
		this.bolaParada = true;
	}
	
	public synchronized void velocitatBolaNormal(){
		this.velocitatExtraBola=1;
	}

	public synchronized void velocitatBolaRapida(){
		this.velocitatExtraBola=4;
	}

	//Aquest mètode serà cridat per tots els objectes que voldran ser notificats quan es produeixi un event d'un objecte Bola
	public synchronized void addCanvisBolaListener(iCanvisBola listener) {
		this.listListeners.add(listener);
	}
	
	//Aquest mètode servirà perquè un objecte que fins ara volia ser notificat dels events de Bola, a partir d'ara ja no vol ser notificat
	public synchronized void removeCanvisBolaListener(iCanvisBola listener) {		
		this.listListeners.remove(listener);
	}
	
	//Mètode que notificarà a tots els objectes que volen ser notificats, que la bola s'ha empassat
	private void notificarBolaEmpassada() {		
		
		List lst;
		EventObject evt = new EventObject(this);
		synchronized(this) {
			lst = (List)((ArrayList)this.listListeners).clone();
		}

		Iterator it = lst.iterator();
		while ( it.hasNext() ){
			((iCanvisBola)it.next()).bolaPerduda(evt);
		}
	}
	
	//Mètode que notificarà a tots els objectes que volen ser notificats, que la bola ha canviat de posició
	private void notificarBolaCanviPosicio() {
		
		List lst;
		EventObject evt = new EventObject(this);
		synchronized(this) {
			lst = (List)((ArrayList)this.listListeners).clone();
		}

		Iterator it = lst.iterator();
		while ( it.hasNext() ){
			((iCanvisBola)it.next()).bolaCanviPosicio(this);
		}
	}
	
	//Aquest mètode es desencadena quan un bloc ha detectat que la bola l'ha tocat, per tant, caldrà canviar la direcció de la bola
	public void blocDestruit(Bloc b, BlocTocat t, boolean destruit) {
		
		if(t == BlocTocat.ESQUERRA ){
			this.incX = this.incX * (-1);
		}else if (t == BlocTocat.DRETA){
			this.incX = this.incX * (-1);
		}else if (t == BlocTocat.DALT){
			this.incY = this.incY * (-1);
		}else if (t == BlocTocat.BAIX){
			this.incY = this.incY * (-1);
		}
		
	}
	
}
