import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;


public class Bloc {
	
	//Atributs de la classe
	private int posicioX, posicioY;
	private int ample, alt;
	private boolean blocDestruit;
	private Color colorBloc;
	private int punts;
	private int numCopsPerDestruir; //Nombre de cops que caldrà fer per destruir un bloc
	private List listListeners; //Llista on es guardaran tots els objectes que escoltaran els events d'aquesta classe Bloc, seran els que notificarem quan es produeixi un event de Bloc
	
	public Bloc(int xx, int yy, int amplada, int altura, int tipusBloc){		
		
		this.posicioX = xx;
		this.posicioY = yy;
		this.ample = amplada;
		this.alt = altura;		
		this.punts = 0;
		
		if (tipusBloc == 0){
			//No volem que sigui un bloc visible
			this.blocDestruit = true;
			this.colorBloc = null;			
		}else{
			//Serà un bloc visible i depenen del id serà d'un color o altre
			this.blocDestruit = false;
			
			switch (tipusBloc){
				case 1:
					this.colorBloc = Color.white;
					this.punts = 1;
					this.numCopsPerDestruir = 1;
					break;
				case 2:
					this.colorBloc = new Color(255, 0, 255);
					this.punts = 2;
					this.numCopsPerDestruir = 1;
					break;
				case 3:
					this.colorBloc = Color.yellow;
					this.punts = 3;
					this.numCopsPerDestruir = 1;
					break;
				case 4:
					this.colorBloc = Color.blue;
					this.punts = 4;
					this.numCopsPerDestruir = 1;
					break;
				case 5:
					this.colorBloc = Color.cyan;
					this.punts = 5;
					this.numCopsPerDestruir = 1;
					break;
				case 6:
					this.colorBloc = Color.orange;
					this.punts = 6;
					this.numCopsPerDestruir = 1;
					break;
				case 7:
					this.colorBloc = Color.green;
					this.punts = 7;
					this.numCopsPerDestruir = 1;
					break;
				case 8:
					this.colorBloc = Color.red;
					this.punts = 8;
					this.numCopsPerDestruir = 1;
					break;
				case 9:
					this.colorBloc = Color.gray;
					this.punts = 10;
					this.numCopsPerDestruir = 2;
					break;
				default:
					this.blocDestruit = true;
					this.colorBloc = null;
					this.punts = 0;
					this.numCopsPerDestruir = 0;
					break;
			}
		}
		
		this.listListeners = new ArrayList();
	}
	
	public void paint(Graphics g){
		//Si el bloc no s'ha trencat, el mostrarem
		if (!this.blocTrencat()){
			g.setColor(this.colorBloc);
			g.fillRect(this.posicioX, this.posicioY, ample, alt);
		}
	}
	
	public int getPosicioBlocX(){
		return this.posicioX;
	}
	
	public int getPosicioBlocY(){
		return this.posicioY;
	}
	
	public int getAmplada(){
		return this.ample;
	}
	
	public int getAltura(){
		return this.alt;
	}
	
	public int getPunts(){
		return this.punts;
	}
	
	//Functió per saber si el bloc es troba trencat
	public boolean blocTrencat(){
		return this.blocDestruit;
	}
	
	//Quan es crida aquest mètode, directament destruïm el bloc (no serà visible)
	public void trencarBloc(){
		this.blocDestruit = true;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(this.getPosicioBlocX(), this.getPosicioBlocY(), this.getAmplada(), this.getAltura());
	}
	
	//Quan es crida aquest mètode, segons les coordenades de la bola trencarem (o no) el bloc
	public void trencarBloc(Bola b){
				
		if (!this.blocDestruit){
			//Si el bloc no es troba destruït de moment, mirem si la bola es troba en les seves coodenades
			if(b.getBounds().intersects(this.getBounds())){
				
				iCanvisBloc.BlocTocat toc = null;
				
				if (b.getPosicioAntX() <= this.getPosicioBlocX() ){ // && this.getPosicioBlocX() < b.getPosicioX()){
					//System.out.println("Tocat per la dreta del bloc");
					toc = iCanvisBloc.BlocTocat.DRETA; 
				}else if (b.getPosicioAntX() >= (this.getPosicioBlocX()+this.getAmplada()) ){ // && (this.getPosicioBlocX()+getAmplada()) > b.getPosicioX()){
					//System.out.println("Tocat per l'esquerra del bloc");
					toc = iCanvisBloc.BlocTocat.ESQUERRA;
				}else if (b.getPosicioAntY() >= (this.getPosicioBlocY()+this.getAltura()) ){ // && (this.getPosicioBlocY()+this.getAltura()) > b.getPosicioY()){
					//System.out.println("Tocat per sota del bloc");
					toc = iCanvisBloc.BlocTocat.BAIX;
				}else if (b.getPosicioAntY() <= this.getPosicioBlocY() ){ // && this.getPosicioBlocY() < b.getPosicioY()){
					//System.out.println("Tocat per sobre del bloc");
					toc = iCanvisBloc.BlocTocat.DALT;
				}else{
					//System.out.println("NO SE SAP");
				}
								
				this.numCopsPerDestruir--; //treiem un cop, quan sigui 0, llavors el destruirem de veritat i puntuarem
				
				if(this.numCopsPerDestruir == 0){						
					
					if(this.numCopsPerDestruir != -1){
						//El bloc s'ha de destruïr
						this.blocDestruit=true;						
					}									
					
				}
				
				//Reproduïm un so
				EfecteDeSo.ReproduirTocBloc();
				
				//Comuniquem a la bola que el bloc existeix i per tant ha de rebotar					
				this.notificarBlocDestruit(toc);				
				
			}
			
		}		
		
	}
	
	//Aquest mètode serà cridat per tots els objectes que voldran ser notificats quan es produeixi un event d'un objecte Bloc
	public synchronized void addCanvisBlocListener(iCanvisBloc listener) {
		this.listListeners.add(listener);
	}
	
	//Aquest mètode servirà perquè un objecte que fins ara volia ser notificat dels events de Bloc, a partir d'ara ja no vol ser notificat
	public synchronized void removeCanvisBlocListener(iCanvisBloc listener) {		
		this.listListeners.remove(listener);
	}
	
	//Mètode que notificarà a tots els objectes que volen ser notificats, que la bola s'ha empassat
	private void notificarBlocDestruit(iCanvisBloc.BlocTocat t) {
		
		List lst;
		EventObject evt = new EventObject(this);
		synchronized(this) {
			lst = (List)((ArrayList)this.listListeners).clone();
		}

		Iterator it = lst.iterator();
		while ( it.hasNext() ){
			((iCanvisBloc)it.next()).blocDestruit(this, t, this.blocDestruit);
		}
	}
	
}
