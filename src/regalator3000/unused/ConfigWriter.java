/* * (NOTA que por motivos de vagancia todas las variables son Strings, incluso aunque la mayoria de cosas que usemos hayan acabado siendo integers
 * fallo mio porque comence a escribir la clase pensando que usariamos nombres de marcas, de usuarios... y luego me ha dado palo 
 * cambiar. En el futuro reescribir para usar Ints pero como solo es un fichero local con pocos usuarios(en teoria) pues mira...
 * NO USAMOS YA, CAMBIAMOS A TODO EN LA BdD*/
package regalator3000;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;

/*Metodos importantes:
 * 
 * 
 * writeNewEvent(userID, oldEventData) : Te escribe el contenido de oldEventData(eventID, lista de marcas y de categorias) como un evento bajo el userID que quieras. 
 * Se convierte en el primer evento bajo el userID(no importa). Devuelve falso si el usuario no existe, cierto si escribe el evento bajo el usuario.
 * 
 * addNewUser(userID) : Te agrega un nuevo usuario al final de la file. Devuelve cierto si lo añade, falso si ya existe el usuario.
 * 
 * editEvent(userID,oldEventData) : Reemplaza los valores del evento con oldEventData.eventID del usuario userID con los nuevos contenidos en oldEventData (vamos se queda usuario y evento
 * igual pero cambia los valores de marca y categoria). Notese que no hay comprobacion en la funcion que el evento sea del usuario por lo que si se necesita hacer la comprobacion
 * manualmente (no se necesita en teoria ya que los eventIDs deberian de ser unicos)).  Devuelve cierto si lo hace y falso si el evento no existe
 * 
 * eliminateEvent(eventID): Se carga el evento con eventID elegido del archivo. Devuelve cierto si lo hace y falso si el evento no existe.
 * 
 * 
 * 
 * EXTRA;
 * goToLineWithTag(tagValue, valor, lineaPrevia) : pone el lector/escritor en la linea con <tag> i valor dentro<tag> deseados (o en la linea previa (teniendo en cuenta que habra un extra \n en la linea anterior asi que cuidado,
 * esto lo tengo en cuenta en las funciones de editar i eliminar eventos)). NO NECESARIO PARA EL FUNCIONAMIENTO NORMAL DE LA APLICACION.
 */
public class ConfigWriter {
	
	private Random randomGen = new Random(); //for testing with random values
	private static int standardLineSize = 30; //Tamany maxim de linea esperat en aquest document, donara errors si es toquen els valors manualment, fer comprobacio en el futur
	//Del pal if(raf.readLine().length() > 30 -> crea un nou arxiu i digues que esta danyat o algo 
	RandomAccessFile raf;
	
	public ConfigWriter(String filename){
		System.out.println("Directorio actual: " + System.getProperty("user.dir")); //Da el directorio donde esta leyendo
		loadIniFile(filename); //canviar per direccionar a un directori o mantenir al local
	}
	
	public boolean loadIniFile(String filename){
		try{
			this.raf = new RandomAccessFile(filename,"rw");
			return true;
		}
		catch(Exception e){
			System.out.println(e.toString());
			return false;
		}
	}
	
	//Escriu un nou event per un user, SEMPRE AFEGEIX DATA NO IMPORTA DIFERENCIA AMB ANTIC SIZE
	public boolean writeNewEvent(String userID, oldEventData eventData) throws IOException{
		long oldPosition = goToLineWithTag("user",userID, false); 
		if (oldPosition == -1) { return false; } //l'usuari no existeix
		ArrayList<String> restOfDocument = getNextLines(false); //guardem la resta del document cap avall...
		raf.seek(oldPosition); //ves a l'antiga posicio abans de la lectura del que l'user té a les lines de sota(els altres events i users)
		writeEventData(eventData,eventData.eventID); //escriu data del nou event
		fillWithOldData(restOfDocument); //Escriu, sota la nova informacio, la resta del document que hi habia a sota
		raf.seek(0); //torna el punter a l'inici del document
		return true;
	}
	
	/*Writes a new user at the end of the config document*/
	public boolean addNewUser(String userID) throws IOException{
		if (goToLineWithTag("user",userID,false) != -1){ return false; } //User already exists!
		String userString = normalizeString("<user>" + userID + "<user>"); //Fem la nova linea del tamany desitjat total
		raf.seek(raf.length()); //ves al final del document
		raf.writeBytes(userString+"\n"); //escriu el nou usuari
		raf.seek(0);
		return true;
	}
	
	/*S'usa perque totes les linees del document tinguin la mateixa llargada, sino problemes de formateig
	 * i a l'hora de determinar quants bytes ens mengem al eliminar linees*/	
	private String normalizeString(String oldString){
		String normalizedString = fillStringWithBlanks(oldString, standardLineSize - oldString.length());
		return normalizedString;
	}

	
	/*Used to normalize the functions and make them all reach the same length, it just fills
	 * the remaining space up until the maximum defined in standardLineSize with empty space " "*/
	private String fillStringWithBlanks(String oldString, int howMany){
		while(howMany > 0) {
			oldString += " ";
			howMany--;
		}
		return oldString;
	}
	
	/*Escriu la resta del document(tot el que hi ha cap avall de la informacio que vols modificar) que has guardat previament 
	(en format d'un conjunt de linees)*/
	private boolean fillWithOldData(ArrayList<String> oldLines) throws IOException{
		if (oldLines != null) { //No estem al final deldocument..
			String tmpLine;
			try{
				for(int i = 0; i < oldLines.size(); i++)  { //Escriu la resta del document antic
					tmpLine = oldLines.get(i);
					raf.writeBytes(tmpLine +"\n");	
				} 	
			}
			catch(Exception e){
				System.out.println(e.toString());
				return false; //NOMES TORNEM FALSE SI ERROR EN L'ESCRIPTURA
			}
		}
		return true; //Torna true, si escrit sense problemes I si no calia escriure res
	}
		
	//escriu la informacio de l'event
	private void writeEventData(oldEventData eventData, String eventID) throws IOException{
		if(!eventID.equals("")){ //Si modifiquem l'event no canviem el valor de Event ID nomes categ/marca, passem de la primera linea
			raf.writeBytes(normalizeString("<evento>" + eventData.eventID + "<evento>")+"\n"); 
		}
		raf.writeBytes(normalizeString("<cat>" + eventData.listMyArray(eventData.eventoCategorias) + "<cat>")+"\n");
		raf.writeBytes(normalizeString("<marca>" + eventData.listMyArray(eventData.eventoMarcas) + "<marca>")+"\n");
	}
	
	
	/*CONDICIO: ELS EVENTS TENEN UN ID UNIC I L'USUARI ES LADEQUAT...
	 * COMPROBAR AMB ConfigWhatever.getEventDataForUser EN LA LOGICA*/
	public boolean editEvent(String userID, oldEventData newEventData) throws IOException{
		long position = goToLineWithTag("evento",newEventData.eventID,false);
		if (position == -1) {return false;} 									 //No existe el evento
		ArrayList<String> restOfDocument = getNextLines(false);					 //guardem la resta del document cap avall...
		restOfDocument.remove(0); 												//Ens carreguem les linees corresponets a lantic event...(les de <cat>..<cat> i <marca>..<marca>), cal usar 0 els dos cops 
		restOfDocument.remove(0); 												//perque els indexs van cap a leskerra al eliminar un(el que estaba a 1 ara és a 0, 2 -> 1, 3 -> 2, etc i son els dos consecutius del principi
		raf.seek(position);			
		writeEventData(newEventData,"");
		fillWithOldData(restOfDocument);
		raf.seek(0);
		return true;
	}
	
	/*Va a la linea amb tag i valor determinat, ej "user","22" -> va a la linea <user>22<user> si existeix*/
	public long goToLineWithTag(String tagName, String value, boolean startPreviousLine) throws IOException{		
		String tmpLine;
		String[] lineValues;
		raf.seek(0);
		while ((tmpLine = raf.readLine()) != null){
			lineValues = getLineTypeValue(tmpLine);
			if (lineValues[0].equals(tagName) && lineValues[1].equals(value)){
				//System.out.println("Estem a: " + raf.getFilePointer()); //debug
				if (startPreviousLine) { 
					raf.seek(raf.getFilePointer() - tmpLine.length()); 								//Retorna la linea anterior (sense comptar el tamany de \n per aixo........
				}
				return raf.getFilePointer(); //estem a la linea que buscavem retorna la posiicio pel RandomAccessFile
			}
		}
		System.out.println("Tag with value not found");
		return -1; //la linea que buscavem no existeix	
	}
	
	/*Et diu quina tag te la linea y el valor de la tag, ej: <evento>15<evento> retorna ["evento","15"]*/
	public String[] getLineTypeValue(String line) throws IOException{
		try {
			String[] regexLine = line.split("<|>"); //amb aixo dona empty,user,value,user
			//String[] test = tmpLine.split("[<|>]\\w*[<|>]"); //amb aixo dona empty,value
			//String[] test = tmpLine.split("<\\w*>|</\\w*>"); //amb aixo dona empty,value
			String[] finalString = new String[2];
			finalString[0] = regexLine[1]; //tagValue
			finalString[1] = regexLine[2]; //valor de la tag
			return finalString;
		}
		catch(Exception e){
			System.out.println("Error reading this line");
			return null;
		}
	}
	
	/*Eliminates an event, events should all have unique IDs, it resizes the document
	 * to match for the loss of the 3 lines that conform an event	 */
	public boolean eliminateEvent(String eventID) throws IOException{
		long oldPosition = goToLineWithTag("evento",eventID, true); 
		if (oldPosition == -1) { return false; } //no existeix l'event
		ArrayList<String> restOfDocument = getNextLines(false); //guardem la resta del document cap avall...
		long oldSize = raf.length();
		raf.seek(oldPosition-1); 									//ves a l'antiga posicio(cal restarli el -1 pel \n que ens mengem amb patates sino
		for(int i = 0; i < 3; i++) {
			System.out.println("ens carreguem: " + restOfDocument.get(0));
			restOfDocument.remove(0);
		}
		fillWithOldData(restOfDocument); //Escribim tota l'informacio de sota sobre la posicio de l'antic event
		raf.setLength(oldSize - 3*(standardLineSize+1)); //+1 pels \n que tenen les linees
		raf.seek(0);
		return true;
	}
	
	/*GUARDA EL QUE QUEDA DE FILE CAP AVALL EN UNA ARRAYLIST, USADA PEL WRITER PER APPENDEJAR EL NOU EVENTO A 
	 * UN USER Y DESPRES ESCRIURE LA RESTA DEL DOCUMENT PER SOTA AL DOC I ALTRES SIMILARS */
	private ArrayList<String> getNextLines(boolean trimmed) throws IOException{
		ArrayList<String> nextLines = new ArrayList<String>();
		String nextLine;
		try {
			while((nextLine = raf.readLine()) != null){
				if (trimmed) { nextLine = nextLine.trim(); }
				nextLines.add(nextLine);
			}
		}
		catch (Exception e){
			System.out.println("Error or EOF, " + e.toString());
		}
		return nextLines;
	}
	
	/*Mainly used to test this class' methods*/
	public oldEventData generateRandomEvent(String userID, String eventID){
		String[] cats = new String[this.randomGen.nextInt(5)+1]; //1-6
		String[] marcas = new String[this.randomGen.nextInt(5)+1]; //1-6
		oldEventData evento;
		int i;
		for (i = 0; i < cats.length; i++){
			cats[i] = Integer.toString(randomGen.nextInt(100));
		}
		for (i = 0; i < marcas.length; i++){
			marcas[i] = Integer.toString(randomGen.nextInt(100));
		}
		evento = new oldEventData(userID, eventID, cats, marcas);
		return evento;
	}
	
	public static void main(String[] args) throws IOException{
		String filename = "config.ini";
		ConfigWriter configTest = new ConfigWriter(filename);
		configTest.addNewUser("1"); //Prova addicio d'users
		configTest.addNewUser("2");
		configTest.addNewUser("45");
		configTest.addNewUser("132");
		configTest.addNewUser("12");
		//configTest.addNewUser(Integer.toString(configTest.randomGen.nextInt(100)));
		//configTest.goToLineWithTag("evento", "4",true);
		ConfigReader reader = new ConfigReader(filename);
		String newEventID = Integer.toString(configTest.randomGen.nextInt(100));
		oldEventData evento = configTest.generateRandomEvent("1",newEventID);  
		System.out.println("Generating new event for user 45 with eventID: " + newEventID);
		configTest.writeNewEvent("1", evento); //generamos un nuevo evento prueba
		System.out.println("Checking generated event data...");
		reader.getEventDataForUser("1"); //leemos sus datos
		System.out.println("Modifying event for user 1...");
		evento = configTest.generateRandomEvent("1",newEventID);  //editamos el evento prueba
		configTest.editEvent(newEventID, evento);
		System.out.println("New event data...");
		reader.getEventDataForUser("1"); //leemos sus datos otra vez
		//configTest.eliminateEvent(newEventID); //Eliminamos el evento prueba
		//reader.getEventDataForUser("45"); //leemos sus datos otra vez

		// TODO Auto-generated method stub

	}

}
