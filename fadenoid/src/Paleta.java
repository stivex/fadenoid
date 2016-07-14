import java.awt.Button;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.File;

import javax.imageio.ImageIO;

/*
 * Classe que representa la pala del joc que es desplaça i llisca a esquerra/dreta
 */

class Paleta extends Canvas implements MouseMotionListener, KeyListener, iCanvisPortSerie {
	
	//Atributs de la classe	
	private int posicioX;
	private int posicioY;
	private int ampladaPala;
	private int alturaPala;
	private Image imgPaleta;
	private Button boto;
	private int ampladaEspaiJoc;
	private boolean paletaExtraLlarga;
	
	//Constructor (Params: posició inicial X i Y)
	public Paleta(int posicioIncialX, int posicioInicialY){
		
		this.posicioX = posicioIncialX;
		this.posicioY = posicioInicialY;
		
		this.ampladaEspaiJoc = posicioIncialX * 2;
		
		this.paletaExtraLlarga = false;
		this.ampladaPala = 70;		
		this.alturaPala = 12;
		
		this.setPaletaExtra(false);		
		
		//Creem un fil d'execusió per poder rebre informació del port sèrie	
		/*
		PortSerie serialPort = new PortSerie();
		serialPort.addCanvisPortSerieListener(this);
		Thread filExec = new Thread(serialPort);
		filExec.start();
		*/
					
	}
	
	//Retorna la posició X de la paleta
	public int getX() {
		return this.posicioX;
	}
	
	//Retorna la posició Y de la paleta
	public int getY() {
		return this.posicioY;
	}
	
	//Retorna la posicio del centre de la Paleta
	public int getCentrePaleta() {
		return (this.ampladaPala/2);
	}
	
	//Retorna l'amplada de la paleta
	public int getAmplada(){
		return this.ampladaPala;
	}
	
	//Retorna l'altura de la paleta
	public int getAltura(){
		return this.alturaPala;
	}
	
	public void setPaletaExtra(boolean extra){
	
		String urlImatgePaleta = "";
		
		if (extra){
			this.ampladaPala = 140;
			urlImatgePaleta = "./img/paleta_petita_llarga.gif";
		}else{
			this.ampladaPala = 70;
			urlImatgePaleta = "./img/paleta_petita.gif";
		}
		
		this.boto = new Button();
		this.boto.setSize(this.getAmplada(), this.getAltura());				
		
		//Generem la imatge de la pala de joc
		BufferedImage palaI;
		ImageFilter palaF;
		ImageProducer palaP;
		try{			
			palaI = ImageIO.read(new File(urlImatgePaleta));						
			palaF = new CropImageFilter(0, 0, this.ampladaPala, 20);//creem un sector d'imatge
			palaP = new FilteredImageSource(palaI.getSource(), palaF);//indiquem a quina imatge volem aplicar el sector
			imgPaleta = createImage(palaP);//creem la imatge a partir de palaP	
		}catch(Exception e){
			Eines.MostrarMissatgeError("No es pot carregar la imatge", e.toString());			
		}			
				
	}
	
	//Retorna un requadre de l'espai que ocupa la Paleta
	public Rectangle getBounds() {
		return new Rectangle(this.getX(), this.getY(), this.getAmplada(), this.getAltura());
	}
	
	public void paint(Graphics g) {		
		g.drawImage(imgPaleta, this.getX(), this.getY(), boto);
	}
	
	public void MourePaleta(int posX, int posY){
		
		//Calculem la meitat de l'amplada de la Paleta
		int midaMeitatPaleta = this.getAmplada()/2; //this.imgPaleta.getWidth(this.boto)/2;
				
		//Si la posició rebuda és negativa, el valor mínim serà 0
		if((posX-midaMeitatPaleta) < 0){
			posX = midaMeitatPaleta;
		}
		
		//Si la posició rebuda és superior a la màxima amplada de la finestra, el valor màxim passarà a ser l'amplada de la finestra		
		if(posX > this.ampladaEspaiJoc){			
			posX = this.ampladaEspaiJoc - midaMeitatPaleta;
		}		
		
		this.posicioX = posX - midaMeitatPaleta;		
				
	}
	
	//Mètodes que cal definir perquè la classe implementi la interfície MouseMotionListener
	public void mouseDragged(MouseEvent e){}
	
	public void mouseMoved(MouseEvent e){		
		this.MourePaleta(e.getX() - (this.getAmplada()/2), e.getY());
	}
	
	//Es desencadenarà quan es rebi un valor nou des del port sèrie (quan es mou la plataforma)
	public void nouValorRebut(int valor) {		
		if (valor == 0){
			this.MourePaleta( (this.ampladaEspaiJoc/2) - (this.getAmplada()/2), this.getY());
		}else{
			this.MourePaleta( (valor*5) - (this.getAmplada()/2), this.getY());
		}			
	}
	
	//Mètodes que cal definir perquè la classe implementi la interfície KeyListener
	public void keyPressed(KeyEvent arg0) {
		int desplacament = 20;
		if (arg0.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT){			
			this.MourePaleta(this.getX() - desplacament + (this.getAmplada()/2), this.getY());			
		}else if (arg0.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT){
			this.MourePaleta(this.getX() + desplacament + (this.getAmplada()/2), this.getY());
		}
	}

	public void keyReleased(KeyEvent e) {}

	public void keyTyped(KeyEvent e) {}
	
}
