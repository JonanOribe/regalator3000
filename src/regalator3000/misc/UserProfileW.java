package regalator3000.misc;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/*Class for writing the local users' profile.
 * 3 toggles en teorida: Main window (No molestar, evita que salgan popups hasta que se desactiva (decidir si entonces mira popups (no))
 * los dos otros toggles estaran en el aviso regalo -> No me avises mas esta sesion, no me avises nunca mas
 * Solo no me avises nunca mas iran a la profile.
 * 
 * Format:
 * "Regalator 3000 user profiles" (first line)
 * U:#userNum
 * DND: [#eventid1,#eventid2  --> events that will not be shown ever to the user until he toggles their alarm again
 * (FUTURE) implement more (LANG: , COLOR:  ....) 
 */
public class UserProfileW {
	
	protected static final String FILENAME = "userprofiles.dat";
	public static final int MAXVALUESPERLINE = 10;
	public static final String USERTAG = "U";
	public static final String NOMOLESTARTAG = "DND";
	protected static final String PROFILEHEADER = "Regalator 3000 - user profiles";
	protected static File profile;	//Hay que iniciarla una sola vez con initProfile
	
	/*Funcion que crea la variable de clase File profile, primero mira si el 
	 * header del archivo es el correcto (Regalator 3000...) y si lo es mantiene
	 * el archivo, sino crea uno nuevo. Podriamos añadir encriptacion y paranoias
	 * al archivo en el futuro con un par de funciones encode y decode	 */
	public static void initProfile(){
		try {
			profile = new File(FILENAME);
			if (profile.exists()){ 
				if (!UserProfileR.hasHeader(profile)){ //Gotta delete and recreate the profile
					profile.delete();
					profile = new File(FILENAME);
					createProfile();
				}
			}
			else {
				createProfile();
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static RandomAccessFile openFile(String type){
		try {
			return (new RandomAccessFile(profile,type));
			
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	protected static void closeRAF(RandomAccessFile raf){
		try {
			raf.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		finally {
			try {
				raf.close();
			} catch(Exception e){
				raf = null;
			}
		}
	}
	
	private static void createProfile()
	{
		try {
			RandomAccessFile raf = openFile("rw");
			raf.writeBytes("Regalator 3000 - user profiles");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/* Writes tags to a position along with an empty valuespace f.ex U:  or DND: []
	 * It will append at the end of the file if the position value is too big or -1
	 * else it will append in the desired position
	 * returns the position of the cursor before writing
	 * The boolean isArray checks wether its an empty value or an empty array []
	 * FUTURE change boolean for an int type for other types maybe*/
	private static long addEmptyTag(String tag, boolean isArray, long initialPos){
		try {
			RandomAccessFile raf = openFile("rw");
			if (initialPos < 0 || initialPos > raf.length()){ 
				raf.seek(raf.length());
			}else { 
				raf.seek(initialPos); 
			}
			long pos = raf.getFilePointer()+1;
			if (isArray){
				raf.writeBytes("\n" + tag + ": []");
			}
			else {
				raf.writeBytes("\n" + tag + ": ");
			}
			closeRAF(raf);
			return pos;
		}
		catch(Exception e){
			return -1;
		}
	}

	private static void addValueToTag(long initialPos, String tag, String newValue, boolean arrayTag){
		String[] addedValue = new String[1];
		addedValue[0] = newValue;
		addValuesToTag(initialPos, tag, addedValue, arrayTag);
	}
	
	/*Precondicio -> Estem a la posicio adequada per començar a escriure, a l'inici de la linea amb la tag que busquem
	 * Aquesta funció agreaga valors a tags amb multiples possibilitats (ex DND: [2,3]), el boolean arrayTag
	 * determina si afegira els valors 1,23, etc o [1,2,34] etc.
	   Es multilinea escribint un maxim de MAXVALUESPERLINE elements per linea*/
	private static void addValuesToTag(long initialPos, String tag, String[] newValues, boolean arrayTag){
		String[] initialValues = UserProfileR.getTagValues(tag, initialPos);
		try {
			RandomAccessFile raf = openFile("rw");
			raf.seek(initialPos);
			String[] finalArray = new String[initialValues.length + newValues.length];
			int j = 0; int k = 0;
			for (int i = 0; i < finalArray.length; i++){
				if (j < initialValues.length) {
					finalArray[i] = initialValues[j];
					j++;
				} else {
					finalArray[i] = newValues[k];
					k++;
				}
			}
			//System.out.println(Arrays.toString(finalArray));
			String peekedNextLine;
			boolean lastLine = false;
			raf.readLine(); //Always advance one line (else you'll still be over the original tag line you're overwritting)
			while (!lastLine){
				peekedNextLine = UserProfileR.peekLine(raf);
				//System.out.println("Next line is: " + (peekedNextLine | null) + " , char at 0: " + peekedNextLine.charAt(0));
				if (peekedNextLine == null || !Character.isDigit(peekedNextLine.charAt(0))) { //Reached another tag or endoffile, it doesnt continue with numbers this line
					lastLine = true;
				}	
				else {
					raf.readLine();
				}
			}
			long cursorPos = raf.getFilePointer(); 		
			ArrayList<String> linesUnderCursor = UserProfileR.getLinesFromPosition(cursorPos); //Get the lines under the position we modify
			raf.seek(initialPos);
			raf.writeBytes(tag + ":");
			if (arrayTag){	writeValuesToFile(raf, finalArray, true);} 
			else { writeValuesToFile(raf, finalArray, false); }
			writeLinesUnderCursor(raf.getFilePointer(),linesUnderCursor);
			closeRAF(raf);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unused")
	private static void writeValueToFile(RandomAccessFile raf, String value){
		String[] valueA = new String[1];
		valueA[0] = value;
		writeValuesToFile(raf,valueA,true);
	}
	
	
	private static void removeValueFromTag(long initialPos, String tag, String newValue){
		String[] addedValue = new String[1];
		addedValue[0] = newValue;
		removeValuesFromTag(initialPos, tag, addedValue);
	}
	
	private static void removeAllValuesFromTag(long initialPos, String tag){
		String[] initialValues = UserProfileR.getTagValues(tag, initialPos);
		removeValuesFromTag(initialPos, tag, initialValues);
	}
	
	/*Funcio per eliminar valors d'una tag, mira totes les copies que coincideixen entre
	 * els existents i removableValues i els elimina. SEMPRE escriu en array [1, 4, 5...]
	 * per tant si en el futur afegeixes altres tipus de valors (ex: true/false o iokse) cal
	 * canviar. Es multilinea igual que addValuesToTag	 */
	private static void removeValuesFromTag(long initialPos, String tag, String[] removableValues){
		try {
			RandomAccessFile raf = openFile("rw");
			raf.seek(initialPos);
			String[] initialValues = UserProfileR.getTagValues(tag, initialPos);
			//System.out.println("Starting values " + Arrays.toString(initialValues));
			String elements = "";
			boolean lastLine = false;
			while (!lastLine) {
				elements = raf.readLine();
				if (UserProfileR.valuesEndThisLine(elements)) {
					lastLine = true;
				}
			}
			elements = "";
			boolean elementIncluded;
			boolean atLeastOne = false;
			if (initialValues != null){
				for (int i = 0; i < initialValues.length; i++){
					elementIncluded = false;
					for (int j = 0; j < removableValues.length; j++){
						if (initialValues[i].equals(removableValues[j])){
							elementIncluded = true;
							atLeastOne = true;
						}
					}
					if (!elementIncluded){
						elements += initialValues[i] +",";
					}
				}
			}
			if (!atLeastOne) { return; } //No changes, dont do anything...
			if (elements.length() <= 0){ elements = ",";}
			elements = elements.substring(0, elements.length()-1); 
			long afterLinePos = raf.getFilePointer();
			ArrayList<String> linesUnderCursor = UserProfileR.getLinesFromPosition(afterLinePos); 
			raf.setLength(initialPos);
			addEmptyTag(tag, true,initialPos-1);
			//System.out.println("adding this to an empty value list: " + Arrays.toString(elements.split(",")));
			addValuesToTag(initialPos, tag, elements.split(",") , true);
			writeLinesUnderCursor(raf.length() ,linesUnderCursor);
			closeRAF(raf);
		}
		catch(Exception e){
		}
	}
	
	/*Escriu la resta de linees que calgui sota la posicio inicial y torna la posicio a la que acaba*/
	private static long writeLinesUnderCursor(long startingPos, ArrayList<String> lineArray){
		try {
			RandomAccessFile raf = openFile("rw");
			raf.seek(startingPos);
			if (lineArray.size() > 0){
				raf.writeBytes("\n");
			}
			for (int i = 0; i < lineArray.size(); i++){
				if (i != lineArray.size()-1){
					raf.writeBytes(lineArray.get(i)+"\n");
				} else {
					raf.writeBytes(lineArray.get(i));
				}
			}
			return raf.getFilePointer();
		}
		catch (Exception e){
			e.printStackTrace();
			return -1;
		}
	}
	
	/*Function to write the modified value or values to file with the desired format*/
	private static void writeValuesToFile(RandomAccessFile raf, String[] values, boolean arrayTag){
		try {
			if (arrayTag){ 
				raf.writeBytes(" [");
			}
			else {
				raf.writeBytes(" ");
			}
			for (int i = 0; i < values.length; i++){
				if (i == values.length-1){			//Last value
					if (!arrayTag){
						raf.writeBytes(values[i]);
					} else {
						raf.writeBytes(values[i] + "]");
					}
				} else {
					if ((i+1) % MAXVALUESPERLINE == 0){ //TOo many elements, need to jump to the next line
						raf.writeBytes(values[i] + ",\n");
					} else {
					raf.writeBytes(values[i] + ", ");
					}
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/*FUNCTIONS CREATED TO BE USED DIRECTLY BY REGALATOR CLASSES*/
	
	/*Funcion que anyade un nuevo usuario con userID determinado al final de la profile*/
	public static void addUser(String userID){
		long userPos = addEmptyTag(USERTAG, false,-1);
		addValueToTag(userPos, USERTAG, userID, false);
		addEmptyTag(NOMOLESTARTAG, true,-1);
	}

	/*Funcion que añade valores a la tag determinada de un usuario (puede ser via array o uno a uno, si 
	 * isArrayValue esta activado los guardara de la forma [valor1, valor2,...] sino no pondra []*/
	public static void addToUserTag(String userID, String tag, String[] newValues, boolean isArrayValue){
		long lineAfterUser = UserProfileR.findPropertyPos(USERTAG,userID);
		addValuesToTag(lineAfterUser ,tag, newValues, isArrayValue);
	}
	/*Igual que la anterior pero anyade un solo valor*/
	public static void addToUserTag(String userID, String tag, String newValue, boolean isArrayValue){
		long lineAfterUser = UserProfileR.findPropertyPos(USERTAG,userID);
		addValueToTag(lineAfterUser ,tag, newValue, isArrayValue);
	}
	
	/*Funcion que elimina valores de la tag determinada de un usuario (puede ser via array o uno a uno)
	 * NOTA: ESTA HECHO PARA SIEMPRE PONER [] en este modo, cambiar la funcion original si es necesario*/
	public static void removeFromUserTag(String userID, String tag, String[] newValues){
		long lineAfterUser = UserProfileR.findPropertyPos(USERTAG,userID);
		removeValuesFromTag(lineAfterUser ,tag, newValues);
	}
	/*Igual que la anterior pero elimina un solo valor*/
	public static void removeFromUserTag(String userID, String tag, String newValue){
		long lineAfterUser = UserProfileR.findPropertyPos(USERTAG, userID);
		removeValueFromTag(lineAfterUser ,tag, newValue);
	}
	
	/*Funcion para eliminar todos los valores de una tag de un usuario (p.ej: elimina todos
	 * los valores en la tag DND (no molestar)*/
	public static void removeAllFromUserTag(String userID, String tag){
		long lineAfterUser = UserProfileR.findPropertyPos(USERTAG, userID);
		removeAllValuesFromTag(lineAfterUser ,tag);
	}
	
	public static void main(String[] args) {
	}

}
