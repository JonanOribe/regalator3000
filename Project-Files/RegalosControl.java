package regalator3000;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

/*Clase con metodos estaticos para gestionar criterios de obtencion de IDs de los regalos basados en los criterios del usuario, de aleatoriedad y de marcas/categorias elegidas
 * 
 * METODOS IMPORTANTES:
 * 
* getRegalosElegidos(DbHandler, EventData) te devuelve una ArrayList<String> con todos los IDs de los regalos elegidos con los criterios del usuario de marcas/categorias
* (Notese que hay dentro una funcion que se llama getCriteriaSQLCode(EventData,boolean,boolean) donde se le puede especificar si se quiere que los
* criterios(de marca i categoria) sean de inclusion o de exclusion, false = ese criterio sera de inclusion (usara IN(criterios en la BBDD) i true de exclusion 
* (NOT IN(criterios) en la llamada a la BBDD). En principio los he dejado false o sea inclusivos. Si hay mas dudas descomentar la linea bajo la llamada a la funcion para ver en la
* consola el texto enviado.
* 
* eligeRegaloAleatorio(ArrayList<String>) Te devuelve una string con el eventoID elegido aleatoriamente de la lista de eventos;
* 
 */
public class RegalosControl {
	
	/*Devuelve un array de Strings con los IDs de los eventos elegidos con los criterios del usuario. Devuelve null si no hay regalos
	 * que cumplan los criterios. ESTO ES IMPORTANTE YA QUE HABRA QUE COMPROBAR CUANDO EL USUARIO ELIGE CRITERIOS QUE HAYA ALGUN REGALO
	 * QUE LOS CUMPLE, SI NO NO TIENE SENTIDO EL PROGRAMA (HACERLE VOLVER A ELEGIR? INVENTARSE UNO? LLENAR TOOOODAS LAS CATEGORIAS PARA
	 * QUE NO PASE?) LA COMPROBACION TENDRA QUE HACERSE EN LA GUI CUANDO EL USUARIO INTRODUCE LOS DATOS*/
	public static ArrayList<String> getRegalosElegidos(DatabaseHandler DbConnector, EventData evento){
		int id_usuario = DbConnector.getUserID();
		ArrayList<String> eventosElegidos = new ArrayList<String>(); //Al final usare ArrayList para tener lista de tamaño dinamico al no poder saber cantidad elementos
		if(id_usuario == -1) { return new ArrayList<String>(); } //Not a valid user logged in, return empty array
		if(evento.regaloConcreto != 0) {  //Devolver el regalo concreto elegido si el usuario ha elegido esa opcion
			eventosElegidos.add(Integer.toString(evento.regaloConcreto)); 
			return eventosElegidos;
		}
		String codigoSQL = getCriteriaSQLCode(evento,false,false);
		//System.out.println("Codigo enviado a la BBDD: " + codigoSQL);
		try{
			Statement stmt = DbConnector.openNewConnection().createStatement(); 
			ResultSet rs = stmt.executeQuery(codigoSQL);
			while(rs.next()){
				eventosElegidos.add(rs.getString("id"));
			}
			rs.close();
			if (eventosElegidos.size() > 0) {
				return eventosElegidos;
			}
			return null; //Lista vacia devuelve null
		}
		catch(Exception e){
			System.out.println("Excepcion al elegir regalo " + e.toString());
			return new ArrayList<String>();
		}
		finally{
			DbConnector.closeConnection();
		}
	}
	
	/*Devuelve una id aleatoria de la lista de regalosID*/
	public static String eligeRegaloAleatorio(ArrayList<String> regalos){
		Random randomGen = new Random();
		if (regalos.size() > 0){
			int seleccion = randomGen.nextInt(regalos.size());
			return regalos.get(seleccion);
		}
		return "Ninguno"; //Lista vacia
	}
	
	/*Funcion para retornar el codigo SQL combinado con la seleccion de marcas/categorias incluidas/excluidas
	 * Los booleanos te dejan hacer inclusion IN(numeros) o exclusion NOT IN(numeros) porque en realidad no se
	 * si queriamos hacer que eligieran marcas/cats que quieren o que no quieren */
	private static String getCriteriaSQLCode(EventData evento,boolean exclusionMarcas, boolean exclusionCategorias){
		String finalString = "SELECT id FROM regalos";
		int i = 0;
		if(evento.marcas.length > 0 || evento.categorias.length > 0) {
			finalString+=" WHERE";
		}
		if(evento.marcas.length > 0){ //Si el evento tiene criterios de marca...
			if (exclusionMarcas){
				finalString += " id_marca NOT IN(";
			}
			else{
				finalString += " id_marca IN(";
			}
			for(i = 0; i < evento.marcas.length; i++){
				if ( i != evento.marcas.length-1) {
					finalString += (evento.marcas[i]+",");
				}
				else{
					finalString += evento.marcas[i] + ")";
				}
			}
			if (evento.categorias.length > 0) {
				finalString += " AND";
			}
		}
		if(evento.categorias.length > 0){ //Si el evento tiene criterios de marca...
			if(exclusionCategorias){
				finalString += " id_categoria NOT IN(";
			}
			else{
				finalString += " id_categoria IN(";
			}
			for(i = 0; i < evento.categorias.length; i++){
				if ( i != evento.categorias.length-1) {
					finalString += (evento.categorias[i]+",");
				}
				else{
					finalString += evento.categorias[i] + ")";
				}
			}
		}		
		finalString += ";";
		return finalString;
	}
	public static void main(String[] args) {
		
		/*//prueba funcionamiento con un evento aleatorio (borrar metodo de testeo en eventocontrol cuando acabemos
		EventoControl tester = new EventoControl();
		DatabaseHandler DbConnector = new DatabaseHandler();
		EventData eventoRandom = tester.generateRandomEvent("1");
		String user="Juan",pwd="A2445D"; //CAMBIA EL USER PARA TESTEAR, UN USER QUE EXISTA
		UserControl.logInUser(DbConnector,user, pwd);
		System.out.println("DETALLES EVENTO ELEGIDO: ");
		eventoRandom.toConsole();
		ArrayList<String> regaloIDs = getRegalosElegidos(DbConnector, eventoRandom);
		System.out.println("Ids regalo elegidos: ");
		if (regaloIDs == null) {System.out.println("No hay ningun regalo para esos criterios."); return;}
		for(int i = 0; i < regaloIDs.size(); i++){
			System.out.println("ID: " + regaloIDs.get(i));
		}
		System.out.println("El regalo elegido aleatoriamente es: " + eligeRegaloAleatorio(regaloIDs));*/
	}

}
