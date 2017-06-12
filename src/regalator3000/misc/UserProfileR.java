package regalator3000.misc;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

/*Clase encargada de las funciones de lectura para la profile del regalator*/
public class UserProfileR extends UserProfileW{
		
	/*Funcion que determina si la primera linea de la profile es correcta*/
	public static boolean hasHeader(File file){
		try {
			RandomAccessFile raf = UserProfileW.openFile("r");
			String line = raf.readLine();
			if (line != null && line.equals(PROFILEHEADER)) {
				return true;
			}
			return false;
		}
		catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/*Devuelve la posicion en la que esta una tag con un valor determinado.
	 * Usado sobretodo para buscar la posicion de una tag de User y a partir
	 * de ahi cambiar sus propiedades (findPropertyPos("U" (o USERTAG), "1")) busca al U: 1 en la profile
	 * Si no la encuentra devuelve -1*/
	public static long findPropertyPos(String property1, String property2){
		try {
			RandomAccessFile raf = UserProfileW.openFile("r");
			String line;
			String[] splittedLine;
			while ((line = raf.readLine()) != null){
				splittedLine = line.split(":");
				if (splittedLine[0].equals(property1)){
					if (filterLine(splittedLine[1]).equalsIgnoreCase(property2)){
						return raf.getFilePointer();
					}
				}
			}
			return -1; //Property not found
		}
		catch (Exception e){
			return -1;
		}
	}
	
	/*Devuelve si existe o el usuario con ese ID en el archivo*/
	public static boolean userExistsInFile(String userID){
		return (findPropertyPos(USERTAG, userID) != -1);
	}
	
	/*Usado para obtener el valor de la siguiente linea sin avanzar el lector*/
	public static String peekLine(RandomAccessFile raf){
		try {
			long initialPos = raf.getFilePointer();
			String nextLine = raf.readLine();
			raf.seek(initialPos);
			return nextLine;
		}
		catch(Exception e){
			return null;
		}
	}
	
	/*No usado en principio, pero determina el siguiente caracter del archivo
	 * sin mover el lector	 */
	public static char peekNext(RandomAccessFile raf){
		try{
			long initialPos = raf.getFilePointer();
			int nextChar = raf.read();
			raf.seek(initialPos);
			return (char)nextChar;
		}
		catch(Exception e){
			return '\0';
		}
	}
	
	/*Lee todas las lineas a partir de la posicion elegida y las devuelve como una
	 * array de lineas	 */
	public static ArrayList<String> getLinesFromPosition(long position){
		try {
			RandomAccessFile raf = UserProfileW.openFile("r");
			raf.seek(position);
			ArrayList<String> lines = new ArrayList<String>();
			String line;
			while ((line = raf.readLine()) != null){
				if (!(line.equals("\n") || line.equals(""))) {
					lines.add(line);
				}
			}
			closeRAF(raf);
			return lines;
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/*removes blank spaces, [ and ] from a string, intended for getting the values after f.ex: [1,2,3,4] -> 1,2,3,4*/
	private static String filterLine(String line){
		char[] lineChars = line.toCharArray();
		line = "";
		for (int i = 0; i < lineChars.length; i++){
			if (lineChars[i] != ' ' && lineChars[i] != '[' && lineChars[i] != ']'){
				line += lineChars[i];
			}
		}
		return line;
	}
	
	/*Checks if the line ends with ] or not (if it does you dont need to go to the next line
	 * to fetch more values)	 */
	public static boolean valuesEndThisLine(String line){
		line = line.trim();
		try {
			if (line.charAt(line.length()-1) != ']'){
				return false;
			}
			else {
				return true;
			}
		} catch(Exception e){
			return true; //hmmm (recheck in future)
		}
	}
	
	/*Counts the number of elements in the tag value.
	 *ideally done after filtering the line although it will work anyways */
	public static int valueAmount(String filteredLine){
		char[] lineChars = filteredLine.toCharArray();
		int amount = 0;
		boolean isDigit = false;
		for (int i = 0; i < lineChars.length; i++){
			if (i == (lineChars.length-1)) { //digit at the end of the array
				if (isDigit || (!isDigit && Character.isDigit(lineChars[i]))){
					amount++; //nothing else since its the arrays' end
				}
			}
			else if (Character.isDigit(lineChars[i]) && !isDigit){
				isDigit = true;
			}
			else if (isDigit && !Character.isDigit(lineChars[i])){
				isDigit = false;
				amount++;
			}
		}
		return amount;
	}
	
	/*Gets the tag value immediately under the cursor, accepted value types: 1  or  [1,2,3, (continued next line)  or  [1,2,3]*/
	public static String[] getTagValues(String tag, long position){
		try {
			RandomAccessFile raf = UserProfileW.openFile("r");
			raf.seek(position);
			String filteredValues = "";
			String actualLine = raf.readLine();
			String[] splittedLine = actualLine.split(":"); //Ideally ["tag"," values"], f.ex ["DND"," [1,2,3,4]"]
			if (splittedLine[0].equals(tag)){ //Not ignoring case here, write this in doc?
				actualLine = splittedLine[1]; //First time do this... next iterations itll get the full line...
				do {
					filteredValues += filterLine(actualLine);    
					if (valuesEndThisLine(actualLine)){	break; }
				} while((actualLine = raf.readLine()) != null);
			}
			char[] charray = filteredValues.toCharArray();
			splittedLine = new String[valueAmount(filteredValues)];
			int j = 0;
			boolean isDigit = false;
			String newDigit = "";
			for (int i = 0; i < charray.length; i++){
				//System.out.println("i: " + i + ", char: " + charray[i] + ", written: " + newDigit + " , is it?: " + isDigit);
				if (i == (charray.length-1)) {
					if (isDigit){
						newDigit += charray[i];
						splittedLine[j] = newDigit;
					}
					else if (!isDigit && Character.isDigit(charray[i])){
						splittedLine[j] = Character.toString(charray[i]);
					}
					//Nothing else since its the array's end...
				}
				else if (Character.isDigit(charray[i]) && !isDigit){
					isDigit = true;
					newDigit += charray[i];
				}
				else if ((isDigit && !Character.isDigit(charray[i]))){
					splittedLine[j] = newDigit;
					newDigit = "";
					isDigit = false;
					j++;
				}
				else { //if (Character.isDigit(charray[i]) && isDigit){
					newDigit += charray[i];
				}
			}
			closeRAF(raf);
			return splittedLine;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/*Checks if the user has a tag with a chosen value*/
	public static boolean valueExistsForUser(String userID, String tag, String value){
			String[] userValues = getTagValuesForUser(userID, tag);
			for (int i = 0; i < userValues.length; i++){
				if (userValues[i].equalsIgnoreCase(value)){
					return true;
				}
			}
			return false;
	}
	
	/*returns the values from a tag from a user*/
	public static String[] getTagValuesForUser(String userID, String tag){
		long lineAfterUser = findPropertyPos("U",userID);
		return getTagValues(tag,lineAfterUser);
	}

	
	public static void main(String[] args) {
	}

}
