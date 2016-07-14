import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class EfecteDeSo {

	/*
	INICI_JOC("./inici.wav"),
	TOC_PALA("./toc_pala.wav"),
	TOC_BLOC("./toc_bloc.wav"),
	BOLA_PERDUDA("./bola_perduda.wav");
	PREMI("./premi.wav");
	GAME_OVER("./game_over.wav");
	*/

   public static void ReproduirIniciPartida() {      
	   Reproduir("./sons/inici.wav");
   }
   
   public static void ReproduirTocPala() {      
	   Reproduir("./sons/toc_pala.wav");
   }
   
   public static void ReproduirTocBloc() {      
	   Reproduir("./sons/toc_bloc.wav");
   }
   
   public static void ReproduirBolaPerduda() {      
	   Reproduir("./sons/bola_perduda.wav");
   }
   
   public static void ReproduirPremi() {      
	   Reproduir("./sons/premi.wav");
   }
   
   public static void ReproduirGameOver() {      
	   Reproduir("./sons/game_over.wav");
   }
	
	private static void Reproduir(String urlFitxerAudio){
		   try {	            
	            Clip sonido = AudioSystem.getClip();	             
	            sonido.open(AudioSystem.getAudioInputStream(new File(urlFitxerAudio)));	  
	            sonido.start();
	        } catch (Exception e) {
	            System.out.println("No s'ha pogut reprodu�r el so: " + e);
	        }	
	}
   
}
