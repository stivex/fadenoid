import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/*
 * Classe principal del FADEnoid
 */

public class FADEnoid extends Frame implements ActionListener, iCanvisEspaiDeJoc {
	
	//Atributs de la classe
	private int finestraAmple = 1024;
	private int finestraAlt = 768;
	private int numVidesInicial = 3;
	private int numVides;
	private int numPantalles = 5; //especifica el número de pantalles que tindrà del joc (aquest número ha de ser igual al numero de fitxers TXT)
	private int idPantallaActual = 1; //id de la pantalla actual del joc que l'usuari està jugant en aquests moments (id.TXT matriu)
	private int puntuacioTotal;
	
	private Panel panelVides;
	private Panel panelDisplay;
	private Label lblNivell;
	private Label lblPuntuacio;
	private Panel panelPrincipal;
	private Panel panelPeu;
	
	private EspaiDeJoc espaiJoc;
	private Thread filExec;
	
	private boolean demo = false; //Si està a true, el joc jugarà tot sol
		

	public static void main(String[] args) {
		FADEnoid f;
		f = new FADEnoid();
		f.inicialitzarFinestra(1, 0); //Mètode que inicialitza la finestra pare del programa
	}
	
	public void inicialitzarFinestra(int idPantalla, int puntuacioAcumulada){			

		//Eliminem tot el contingut de controls per tornar a dibuixar-ho tot	
		this.removeAll();
					
		this.numVides = this.numVidesInicial;
		this.puntuacioTotal = puntuacioAcumulada;
				
		//Creem el panel base on dins seu afegirem tots els controls que formaran part del joc
		this.panelPrincipal = new Panel(new FlowLayout(1,1,2));		
		this.add(panelPrincipal, BorderLayout.CENTER);
		
		//Panel d'estil FlowLayout on s'aniran afegint/treient les vides
		this.panelVides = new Panel(new FlowLayout(FlowLayout.LEFT));		
		this.refrescarVides();		
		
		//Creem un panel per col·locar-hi: boto d'inici i puntuació
		this.panelDisplay = new Panel(new FlowLayout(FlowLayout.RIGHT));
		//Hi afegeixo el camp de text on es mostrarà el nivell del joc 
		this.lblNivell = new Label("Nivell: " + idPantalla + "/" + this.numPantalles + " ");
		this.lblNivell.setForeground(Color.WHITE);
		this.lblNivell.setFont(new Font("Courier", Font.BOLD, 20));
		this.panelDisplay.add(lblNivell);
		//Hi afegeix-ho el camp de text on s'anirà mostrant la puntuació
		this.lblPuntuacio = new Label("Puntuació: " + this.puntuacioTotal + "     ");		
		this.lblPuntuacio.setForeground(Color.WHITE);
		this.lblPuntuacio.setFont(new Font("Courier", Font.BOLD, 20)); 
		this.panelDisplay.add(lblPuntuacio);
		//Hi afegeix-ho el botó d'iniciar la partida
		JButton bIniciarPartida = new JButton("Iniciar");
		this.panelDisplay.add(bIniciarPartida);
		
		//Creeo el panel al peu de la finestra que contrindrà tots els altres panels i controls amb informació de la partida
		this.panelPeu = new Panel(new GridLayout(0,2));
		this.panelPeu.setBackground(Color.black);
		//Afegeix-ho el panel de les vides dins del panelPeu
		this.panelPeu.add(this.panelVides);
		//Afegeix-ho el panelDisplay dins del panelPeu
		this.panelPeu.add(this.panelDisplay);
		
						
		//Afegim el panel a la part inferior de la finestra principal que conté els panels: panelVides i panelDisplay
		this.add(panelPeu, BorderLayout.SOUTH);
		
									
		//Títol de la finestra
		this.setTitle("FADEnoid - By Xavier Sarsanedas");
							
		//Assignem a la finestra un objecte EscoltaFinstra
		//Serà l'encarregat de gestionar els events habituals d'una finestra
		this.addWindowListener(new EscoltaFinestra());
		
		//Fa que es redimensioni la finestra segons els elements que conté la finestra
		//this.pack();
		
		//Establim una mida d'entrada de la finestra
		this.setSize(this.finestraAmple, this.finestraAlt);
		
		//No es pot redimensionar
		this.setResizable(false);	
		
		//Afegim l'espai de joc
		espaiJoc = new EspaiDeJoc(this.finestraAmple, this.finestraAlt, this.numVidesInicial, idPantalla, this.demo);		
		this.add(espaiJoc);
		espaiJoc.setFocusable(true);
		espaiJoc.revalidate();
		espaiJoc.repaint();	
		
		//Per escolar el botó
		bIniciarPartida.addActionListener(this);
		
		//Per escoltar la partida actual
		espaiJoc.addCanvisEspaiDeJocListener(this);
				
		//Creem un fil d'execució que permetrà refrescar constantment l'espaiJoc
		//Dins de l'objecte EspaiDeJoc hi ha un mètode run() que s'executarà infinitament		
		filExec = new Thread(espaiJoc);
		filExec.setPriority(Thread.MAX_PRIORITY);
		filExec.start();
		
		this.panelPrincipal.revalidate();
		this.panelPrincipal.repaint();
		
		this.panelPeu.revalidate();
		this.panelPeu.repaint();
		
		//Fem visible la finestra		
		this.setVisible(true);	
		espaiJoc.setFocusable(true);
		
	}
	
	private void refrescarVides(){
		
		//Eliminem tot el contingut del panel per tornar-lo a carregar de nou
		this.panelVides.removeAll();
		
		//Obtenim la imatge
		ImageIcon img = new ImageIcon("./img/paleta_mini.gif");
		
		//Afegim tantes imatges com vides hi ha a l'iniciar una partida
		for(int i=0; i<this.numVides; i++){					
			JLabel lblImg = new JLabel(img);
			this.panelVides.add(lblImg);
		}
		
		this.panelVides.revalidate();
		this.panelVides.repaint();
		
	}

	//Es desencadenarà quan l'EspaiDeJoc ens notifiqui que s'ha perdut una vida
	public void vidaPerduda() {
		//Decrementem en una vida
		this.numVides--;
		this.refrescarVides();
	}
	
	//Es desencadenarpa quan l'EspaiDeJoc ens comuniqui que s'ha recollit un nou premi i aquest consisteix en afegir una vida extra a la partida
	public void vidaExtra() {
		this.numVides++;	
		this.refrescarVides();
	}
	
	//Es desencadenarà quan l'EspaiDeJoc ens comuniqui que el jugador ha perdut totes les vides
	public void jocPerdut() {
		//Reproduïm un so, indicant que el jugador ha perdut
		EfecteDeSo.ReproduirGameOver();
		JOptionPane.showMessageDialog(this, "Has perdut...\n - Nivell:  " + this.idPantallaActual + "\n - Punts: " + this.puntuacioTotal, "Fi de la partida", JOptionPane.WARNING_MESSAGE);
		this.idPantallaActual=1;
		this.inicialitzarFinestra(this.idPantallaActual, 0);
	}
	
	//Es desencadena quan l'EspaiDeJoc ens comunica que el jugador ha destruït nous blocs i per tant s'ha d'incrementar la puntuació
	public void novaPuntuacio(int punts){		
		this.puntuacioTotal = this.puntuacioTotal + punts;
		this.lblPuntuacio.setText("Puntuació: " + this.puntuacioTotal);		
	}
	
	//Es desencadena quan l'EspaiDeJoc ens comunica que s'ha destruït tots els blocs que es podien trencar, per tant, cal seguir amb la següent pantalla o bé final de joc
	public void finalPantalla(){
		if (this.idPantallaActual < this.numPantalles){
			this.idPantallaActual++;			
			this.inicialitzarFinestra(this.idPantallaActual, this.puntuacioTotal);
		}else{
			//Comuniquem al judador que ha aconseguit passar totes les pantalles del joc
			JOptionPane.showMessageDialog(this, "Has superat tots els nivells de FADEnoid!", "Enhorabona!", JOptionPane.WARNING_MESSAGE);			
			//Tornem a començar a la primera pantalla
			this.idPantallaActual = 1;
			this.inicialitzarFinestra(this.idPantallaActual, 0);
		}
	}

	//Quan es clica el botó Inici
	public void actionPerformed(ActionEvent e) {
		//S'ha clicat un botó
		if(e.getActionCommand().equals("Iniciar")){			
			this.idPantallaActual=1;
			this.inicialitzarFinestra(this.idPantallaActual, 0);
		}
	}

}
