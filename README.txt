#################
# FADEnoid v1.0 #
#################
Joc inspirat en l'Arkanoid, desenvolupat per Xavier Sarsanedas Trias (intersarsi@gmail.com) durant el juny/juliol del 2014.

0-REQUISITS T�CNICS
El FADEnoid ha estat desenvolupat en Java, pel que caldr� que tinguis instal�lat el Java al teu ordinador.
Visita la web: http://www.java.com per descarregar-te'l i instal�lar-te'l.
El Java �s multi-plataforma, aix� es pot executar en sistemes Windows, GNU/Linux i Mac.

1-INICIAR EL JOC
Simplement executa el fitxer FADEnoid.jar
IMPORTANT: no desplacis o eliminis les carpetes img, pantalles i sons, ja que no funcionar� correctament el joc.

2-FUNCIONAMENT DEL JOC
L'objectiu �s destru�r tots els blocs de cada nivell mitjan�ant la paleta que el jugador ha de despla�ar d'esquerra a dreta de la pantalla mitjan�ant el ratol�.
Cada bloc, quan es destrueix s'incrementa la puntuaci� del jugador.
En alguns blocs, quan es destrueixen, apareixen premis extra que es poden recollir amb la paleta i que es distingeixen pel seu color:
-Vermell: vida extra
-Blau: s'allarga la paleta de joc.
-Verd: s'incrementa la velocitat de la vola.
-Taronja: bola extra.

3-PERSONALITZAR LES PANTALLES
El joc disposa de 5 pantalles/nivells que es poden personalitzar.

A la carpeta "./pantalles" hi ha 5 fitxers TXT, cada un cont� el disseny d'una de les pantalles. 
El nom de fitxer significa el n�mero de nivell del joc.

Els fitxers estan formats per una matriu de n�meros de 20x20, cada fila representa una fila de blocs. 
La codificaci� �s la seg�ent:

0-Sense bloc
1-Bloc blanc 	(1 punt )
2-Bloc rosa  	(2 punts)
3-Bloc groc  	(3 punts)
4-Bloc blau  	(4 punts)
5-Bloc cyan  	(5 punts)
6-Bloc taronja 	(6 punts)
7-Bloc verd 	(7 punts)
8-Bloc vermell 	(8 punts)
9-Bloc gris (10 punts) (cal dos tocs per destruir-lo)

FADE 2014