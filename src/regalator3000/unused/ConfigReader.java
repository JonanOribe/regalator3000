package regalator3000;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;

/*Genera y lee un archivo en memoria con las configuraciones personales de los users:
 * DATOS: marcas o categorias de regalos que el user no quiere
 * 
 * ej uso:
 * <user>UserID<user>
 * <evento>EventoID<evento> 
 * <cat>Libreria,Viajes<cat>
 * <marca>Zara,Swarovski<marca>
 * 
 *
 * NOTA: todas las tags existiran aunque esten vacias (sino hay que cambiar codigo) y las lineas ocuparan 22 espacios (+\n) cada una 
 * para tener un formato compatible con el escritor y que no cause problemas.
 * SEGURAMENTE REHACER CON BUFFEREDREADER, CON SCANNER HA QUEDADO UN POCO COMPLICADO, USAR MAS REGEX
 * Buscar duplicados de funciones con ConfigWriter
 * 
 * METODOS IMPORTANTES:
 * 
 * getEventDataForUser(userID) -> Te devuelve una array de objetos EventData con los valores de todos los eventos de un usuario (eventID, marcas i categorias elegidas)
 * 
 */
public class ConfigReader {
	
	Scanner sc;
	String filename;
	
	public ConfigReader(String filename){
		this.filename = filename;
		//System.out.println(loadIniFile(filename));
	}
	
	/*Separar en load para lectura y escritura*/
	public boolean loadIniFile(String filename){
		try{
			this.sc = new Scanner(new FileInputStream(filename));
			return true;
		}
		catch(Exception e){
			System.out.println(e.toString());
			return false;
		}
	}

	//----------------------------------------FUNCIONES DE LECTURA---------------------------------------------------------
	/*Pone el scanner en la linea con la estructura <tagName>value<tagName>, un readLine() mas y estas en las lineas
	 * siguientes(p.ej en las lineas de evento despues de buscar un usuario concreto). P.ej: Usada en getEventDataForUser(userID) para poner el scanner donde toque
	 * y que esa funcion se encargue de leer toda la informacion de los eventos de ese usuario	 */
	public boolean goToLineWithTag(String tagName, String value){
		String result = "";
		try{
			this.sc = new Scanner(new FileInputStream(this.filename));
			this.sc.useDelimiter("<"+tagName+">");
			if(sc.hasNext()){ //first line
				result = sc.next();
			}
			else {
				System.out.println("file is empty?");
				return false; //config file is empty...
			}
			if (result.equals(value)) {
				return true; //Nomes cas primer user
			}
			String tmpLine = sc.nextLine();
			while (!tmpLine.equals(value)){	
				if (!sc.hasNextLine()) {
					return false; //No l'hem trobat en tota la file
				}
				tmpLine = sc.next();	
			}
			return true;
		}
		catch(Exception e){
			System.out.println(e.toString());
			return false;
		}
	}
	
	/*Se queda despues del siguiente tag que buscas(ej: si buscas <evento>...<evento> se quedaria despues
	 *del segundo <evento>, haces un sc.nextLine() y te dara la linea con el siguiente valor
	 *VALOR NO DEL PROPIO TAG SINO DEL SIGUIENTE .. cutre?*/
	public boolean goToNextLineWithTag(String tagName){
		this.sc.useDelimiter("<"+tagName+">");
		if (sc.hasNext()) {
			sc.next();
			sc.nextLine();
			System.out.println("El valor del siguiente Tag a " + tagName + " es: " + sc.nextLine());
			return true;
		}
		return false;
	}
	
	//Mañana, que devuelva todos los valores de una tag de la file;
	public ArrayList<String> getAllTagValues(String tagName){
		return null;
	}
	
	/*Comenzara en la linea que introduzcas con el userID adecuado y leera todos sus eventos, te los devolvera como una array de ese tipo de informacion
	 * Si no encuentra el userID devuelve lista vacia. Cuando acaba devuelve el scanner al principio del archivo y sin delimitadores	 */
	public ArrayList<oldEventData> getEventDataForUser(String userID){
		String[] usedTags = {"evento","cat","marca"}; //tags que se usan para guardar la informacion, en el orden que se usan
		ArrayList<oldEventData> totalEventosUsuario = new ArrayList<oldEventData>();
		oldEventData eventoActual;
		String siguienteUserOEvento;
		int i = 0;	
		String tmpLine = "";
		
		if (!this.goToLineWithTag("user",userID)) { //Va a a la primera linea del usuario buscado (<user>userID<user>)
			System.out.println("couldn't find the userID"); //debug
			return new ArrayList<oldEventData>(); //No such user, get out
		}
		//Hem trobat l'user...

		sc.nextLine(); //pasamos a la siguiente linea que sera la de <evento>eventID<evento>

		String testForEmptyUser = getTagData(usedTags[i]).toString(); 
		if(testForEmptyUser.startsWith("[<user>")) { //Es un user vacio, la siguiente linea es user
			System.out.println("Empty user..."); 
			return new ArrayList<oldEventData>(); 
		}
		else {   //Para decir que hemos leido la siguiente linea y lo que habia (la linea que dice si es otro <user> o un <evento> de este user)
			siguienteUserOEvento = testForEmptyUser; 
		}

		boolean nextEvent = true; //El siguiente ciclo es otro evento o un nuevo usuario?
		while(nextEvent){
			eventoActual = new oldEventData(userID);
			
			if(!siguienteUserOEvento.equals("")) {						 //En el caso que despues de un ciclo tengamos otro evento y no un nuevo usuario, tendremos aqui guardado
				eventoActual.eventID = siguienteUserOEvento;			//su eventID, lo pondremos en el campo que toca y moveremos el indice del bucle para que siga en el siguiente campo (cat)
				siguienteUserOEvento = "";
				sc.nextLine();
				i = 1;
			}
			
			while(i < 3) { //itera los tres tipos
				switch(i) {
					case 0: //evento
						eventoActual.eventID = getTagData(usedTags[i]).toString();
						break;
					case 1: //categorias
						eventoActual.eventoCategorias = getTagData(usedTags[i]);
						break;
					case 2: //marcas
						eventoActual.eventoMarcas = getTagData(usedTags[i]);
						break;
				}
				tmpLine = sc.nextLine();
				i++;
			}
			eventoActual.toConsole(); //debug
			if (!sc.hasNextLine()){ //Final del archivo, guarda el evento y sal
					totalEventosUsuario.add(eventoActual);	
					System.out.println("EOF");
					break;
			}
			totalEventosUsuario.add(eventoActual);
			//System.out.println("adding event to total, reminder next line is: " + tmpLine);
			sc.useDelimiter("<evento>");
			siguienteUserOEvento = sc.next();
			i = 0;
			//Una vez hemos comprobado el batch, vemos si la siguiente linea (ya esta en ella es un campo <user>(salimos) o un campo <evento>(otro evento del mismo usuario, sigue el ciclo)
			if (siguienteUserOEvento.startsWith("<user>")) { //nuevo usuario, salimos
				nextEvent = false;
			}
		}
		//System.out.println("reached here");
		return totalEventosUsuario;
	}
	
	/*A partir de una tag(ej: <user>) te coge todo lo que hay hasta su cierre (otro <user> ), es decir te devuelve
	 * toda la informacion pertiente al tag. No avanza otra linea hay que hacerlo una vez has obtenido los datos*/
	public ArrayList<String> getTagData(String tag){
		String result ="";
		ArrayList<String> finalArray = new ArrayList<String>();
		try{
			this.sc.useDelimiter("<" + tag + ">");
			result = this.sc.next();
			finalArray.add(result);
			//System.out.println("DATA RETURNED: "+ result); //debug
			return finalArray;
		}
		catch(Exception e){
			System.out.println(e.toString());
			return finalArray;
		}
		
	}
		

		
	/*Probar dema a fer scanner amb <tag>...</tag> a veure que tal*/
	
	/*LUEGO: Hacer una que liste todos los eventos de todos los usuarios?*/
	public static void main(String[] args) {
		/*LOS EVENTOS DE PRUEBA DEL MAIN DE ConfigWriter.java YA INCLUYEN
		 LLAMADAS A ESTA CLASE. ESTA CLASE NO CREA UN ARCHIVO NUEVO MIENTRAS
		 QUE CONFIGWRITER SI.
		ConfigReader configTest = new ConfigReader("config.ini");
		 
		ArrayList<oldEventData> eventosUser = configTest.getEventDataForUser("1");
		eventosUser = configTest.getEventDataForUser("2");
		eventosUser = configTest.getEventDataForUser("45");
		eventosUser = configTest.getEventDataForUser("12");
		eventosUser = configTest.getEventDataForUser("32");

		/*configTest.sc.reset();
		configTest.goToLineWithTag("evento", "4");
		configTest.sc.nextLine();
		System.out.println(configTest.sc.nextLine());*/
		//configTest.goToNextLineWithTag("user");*/
	}

}
