import java.awt.event.*;

/*
 * Classe que implementa els mètodes de la interfície WindowListener
 * Gestiona els events habituals d'una finestra
 */

public class EscoltaFinestra implements WindowListener{
		
	public void windowActivated(WindowEvent e){
		//Finstra activada		
	}
	
	public void windowClosed(WindowEvent e){
		//Finestra tancadas		
	}
	
	public void windowClosing(WindowEvent e){
		//Finestra tancant-set		
		System.exit(0); //Això farà que es tanqui
	}
	
	public void windowDeactivated(WindowEvent e){
		//Finestra inactiva		
	}
	
	public void windowDeiconified(WindowEvent e){
		//Finestra restaurada		
	}
	
	public void windowIconified(WindowEvent e){
		//Finestra minimitzada		
	}
	
	public void windowOpened(WindowEvent e){
		//Finestra oberta
	}

}
