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


public class Premi extends Canvas implements Runnable {
	
	//Atributs de la classe
	private EspaiDeJoc espJoc; //EspaiDeJoc per on es mourà el Premi
	private int posicioX, posicioY;
	private int ample, alt;
	private int alturaJoc;
	private int idTipusPremi;
	private Button boto;
	private Image imgPremi;	
	private boolean ocultarPremi;
	private List listListeners; //Llista on es guardaran tots els objectes que escoltaran els events d'aquesta classe
	
	public Premi(EspaiDeJoc espaiJoc, int xx, int yy, int amplada, int altura, int alturaFinestraJoc, int tipusPremi){
		
		this.espJoc = espaiJoc;
		
		this.posicioX = xx;
		this.posicioY = yy;
		this.ample = amplada;
		this.alt = altura;
		
		this.alturaJoc = alturaFinestraJoc;		
		this.idTipusPremi = tipusPremi;
		this.ocultarPremi = false;
		
		boto = new Button();
		boto.setSize(this.ample, this.alt);
		
		//Generem la imatge pel Premi
		BufferedImage premiI;
		ImageFilter premiF;
		ImageProducer premiP;
		try{			
			premiI = ImageIO.read(new File("./img/premi" + this.idTipusPremi + ".gif"));
			premiF = new CropImageFilter(0, 0, this.ample, this.alt);//creem un sector d'imatge
			premiP = new FilteredImageSource(premiI.getSource(), premiF);//indiquem a quina imatge volem aplicar al sector
			this.imgPremi = createImage(premiP);//creem la imatge a partir de premiP	
		}catch(Exception e){
			Eines.MostrarMissatgeError("No es pot carregar la imatge", e.toString());			
		}
		
		this.listListeners = new ArrayList();
				
	}
	
	public void paint(Graphics g){
		if(!this.ocultarPremi){
			g.drawImage(this.imgPremi, this.posicioX, this.posicioY, boto);
		}
	}
	
	public int getPosicioX(){
		return this.posicioX;
	}
	
	public int getPosicioY(){
		return this.posicioY;
	}
	
	public int getAmplada(){
		return this.ample;
	}
	
	public int getAltura(){
		return this.alt;
	}
	
	public int getTipusPremi(){
		return this.idTipusPremi;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(this.getPosicioX(), this.getPosicioY(), this.getAmplada(), this.getAltura());
	}
	
	public synchronized void run(){
		
		//Anirà baixant el premi des del bloc on ha aparegut fins a baix de tot de la finestra
		while(this.getPosicioY() < this.alturaJoc){
			
			if(!this.ocultarPremi){ //Per saber si el Premi ja ha estat lliurat
				if(this.espJoc.getPaleta().getBounds().intersects(this.getBounds())){
					
					//Reproduïm un so, indicant que s'ha recollit el premi
					EfecteDeSo.ReproduirPremi();
					
					//Ocultem el premi que acabem de recollir
					this.ocultarPremi = true;				
					
					//Notifiquem a l'EspaiJoc que s'ha recollit el Premi
					this.notificarPremiRecollit();
					
				}
			}
			
			this.posicioY = this.posicioY + 5;
			
		    try {	    	
		    	Thread.sleep(40);
		    } catch (InterruptedException ie) {
		    	
		    }
			
		}
		
	}
	
	//Aquest mètode serà cridat per tots els objectes que voldran ser notificats quan es produeixi un event d'un objecte Premi
	public synchronized void addCanvisPremiListener(iCanvisPremi listener) {
		this.listListeners.add(listener);
	}
	
	//Aquest mètode servirà perquè un objecte que fins ara volia ser notificat dels events de Premi, a partir d'ara ja no vol ser notificat
	public synchronized void removeCanvisPremiListener(iCanvisPremi listener) {		
		this.listListeners.remove(listener);
	}
	
	//Mètode que notificarà a tots els objectes que volen ser notificats, que la bola s'ha empassat
	private void notificarPremiRecollit() {
		
		List lst;
		EventObject evt = new EventObject(this);
		synchronized(this) {
			lst = (List)((ArrayList)this.listListeners).clone();
		}

		Iterator it = lst.iterator();
		while ( it.hasNext() ){
			((iCanvisPremi)it.next()).premiRecollit(this);
		}
	}

}
