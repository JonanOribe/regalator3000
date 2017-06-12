//21/04: Hacer una opcion: tienes eventos que tal, verlos? si/no para cada uno y ademas uno de no volver a avisar hoy (con posible cambio en GUI en mas o algo)
package regalator3000.db;


import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import regalator3000.gui.RegaloPanel;
import regalator3000.misc.AuxFunctions;
import regalator3000.misc.EventData;
import regalator3000.misc.UserProfileR;
import regalator3000.misc.UserProfileW;

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
	
	public static ArrayList<String> idEvtDNDSesion = new ArrayList<String>();
	
	/*Devuelve un array de Strings con los IDs de los eventos elegidos con los criterios del usuario. Devuelve null si no hay regalos
	 * que cumplan los criterios. ESTO ES IMPORTANTE YA QUE HABRA QUE COMPROBAR CUANDO EL USUARIO ELIGE CRITERIOS QUE HAYA ALGUN REGALO
	 * QUE LOS CUMPLE, SI NO NO TIENE SENTIDO EL PROGRAMA (HACERLE VOLVER A ELEGIR? INVENTARSE UNO? LLENAR TOOOODAS LAS CATEGORIAS PARA
	 * QUE NO PASE?) LA COMPROBACION TENDRA QUE HACERSE EN LA GUI CUANDO EL USUARIO INTRODUCE LOS DATOS*/
	public static ArrayList<String> getRegalosElegidos(DatabaseHandler DbConnector, EventData evento){
		int id_usuario = DbConnector.getUserID();
		ArrayList<String> eventosElegidos = new ArrayList<String>(); //Al final usare ArrayList para tener lista de tamaño dinamico al no poder saber cantidad elementos
		if(id_usuario == -1) { return new ArrayList<String>(); } //Not a valid user logged in, return empty array
		if(evento.regaloConcreto != 0) {  //Devolver el regalo concreto elegido si el usuario ha elegido esa opcion
			String[] nombresRegalos = GUIDataRetriever.getAllElements(DbConnector, "nombre", "regalos",false);
			eventosElegidos.add(nombresRegalos[evento.regaloConcreto-1]);  //-1 porque la array de seleccion va de 1-maxLength-1 i aqui de 0-maxLenght...
			return eventosElegidos;
		}
		String codigoSQL = getCriteriaSQLCode(evento,false,false);
		//System.out.println("Codigo enviado a la BBDD: " + codigoSQL);
		try{
			Statement stmt = DbConnector.openNewConnection().createStatement(); 
			ResultSet rs = stmt.executeQuery(codigoSQL);
			while(rs.next()){
				eventosElegidos.add(rs.getString("nombre"));
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
	
	/*Devuelve una id aleatoria de la lista de regalosID,
	 * NOTA: Para no liar mucho el codigo hara otra llamada a la BBDD y obtendra las imagenes y URL del regalo...
	 * Precondicion: No hay mas de un regalo con el mismo nombre, tendria que ser el caso...*/
	public static String[] eligeRegaloAleatorio(DatabaseHandler DbConnector, ArrayList<String> regalos){
		Random randomGen = new Random();
		try{
		if (regalos != null){
			int seleccion = randomGen.nextInt(regalos.size());
			String[] valoresFinales = new String[4];
			valoresFinales[0] = regalos.get(seleccion);
			//Encuentra el nombre del regalo elegido
			String codigoSQL = "SELECT imagen,url1,url2 FROM regalos WHERE nombre ='"+valoresFinales[0]+"';"; //Hacer una funcion en el futuro que haga esto
			Statement stmt = DbConnector.openNewConnection().createStatement(); 
			ResultSet rs = stmt.executeQuery(codigoSQL);
			while(rs.next()){
				valoresFinales[1] = rs.getString("imagen");
				if (valoresFinales[0] == null) {
					valoresFinales[1] = ""; 
				}
				valoresFinales[2] = rs.getString("url1");
				valoresFinales[3] = rs.getString("url2");
				if (valoresFinales[3] == null) {
					valoresFinales[3] = "";
				}
			}
			rs.close();
			return valoresFinales;
		}
		String valoresFinales[] = {"Ninguno","","",""};
		return valoresFinales;
		}
		catch(Exception e){
			System.out.println("Error accediendo a la imagen y url del regalo. " + e.getMessage());
			String valoresFinales[] = {"Ninguno","","",""};
			return valoresFinales;
		}
		finally{
			DbConnector.closeConnection();
		}
	}
	
	/*Mira la lista de eventos del usuario por si hay alguno del que toque avisar hoy, mejorar graficos, opciones, etc.
	DECISION: HACER QUE DE ENTRADA ENTRA QUE EVENTOS NO SE LLAMAN PORQUE TIENEN TOGGLE (Se guarda eso en main_GUI, si creo)
	implementar funcion dentro y otro parametro entrada.
	Aparte comprobacion inicial si esta el toggle de dnd (eso en main_GUI creo)*/
	public static void checkForPresents(DatabaseHandler DbConnector, ArrayList<EventData> eventos){
		//Should show a JDialogMessage warning you for every present in your date interval
		EventData evento;
		long tiempoEnHorasHoy = tiempoEnHoras(LocalDate.now().toString());
		long diasAntesEnHoras = 0;
		long diferencia = 0;
		long[] tiempoHorasEventos = ordenaPorFecha(eventos);
		for (int i = 0; i < eventos.size(); i++){
		
			evento = eventos.get(i);
			String[] eventosDNDSiempre = UserProfileR.getTagValuesForUser(evento.userID, UserProfileW.NOMOLESTARTAG);
			boolean eventoIsIn = false;
			for (int j = 0; j < eventosDNDSiempre.length; j++) {
				if (eventosDNDSiempre[j].equals(evento.eventID)){ eventoIsIn = true; break; }
			}
			if (!checkEventoDND(evento.eventID) && !eventoIsIn){
				try {
					diasAntesEnHoras = evento.diasAviso * 24;
					diferencia = tiempoHorasEventos[i] - tiempoEnHorasHoy;
					//System.out.println("Horas hoy: " + tiempoEnHorasHoy + " , horas dia regalo: " + tiempoEnHorasEvento + " , dias en horas: " + diasAntesEnHoras + " , diferencia " + diferencia);
					if (diferencia < 0) {
						//el dia del evento esta en el pasado ya, borrar o avisar??? Pensar en ello...
						continue;
					}
					if (diferencia <= diasAntesEnHoras){
						evento = EventoControl.getEventData(DbConnector, Integer.parseInt(evento.eventID));
						String[] regalo = RegalosControl.eligeRegaloAleatorio(DbConnector, RegalosControl.getRegalosElegidos(DbConnector, evento));
						String diff = Long.toString(diferencia/24);
						//System.out.println("diff: " + diff + " , " + Arrays.toString(regalo));
						RegaloPanel content = new RegaloPanel(evento, Integer.parseInt(diff) , regalo);
				        JOptionPane.showOptionDialog(new JFrame("test"), content,"Se aproxima una fecha importante!", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.CLOSED_OPTION, null, new Object[]{"De acuerdo"}, null);				}
				}
				catch(Exception e){
					System.out.println("Datos de evento mal formateados " + e.toString());
				}
			}
		}
	}
	
	public static void abreEventoConcreto(DatabaseHandler DbConnector, EventData evento){ //Seguramente puedes mergear esta y un cacho de la de arriba
		try {
			long diasAntesEnHoras = evento.diasAviso * 24;
			long eventoFechaEnHoras = tiempoEnHoras(evento.fecha);
			long diferencia = eventoFechaEnHoras - tiempoEnHoras(LocalDate.now().toString());
			if (diferencia < 0) {
				return;
			}
			if (diferencia <= diasAntesEnHoras){
				evento = EventoControl.getEventData(DbConnector, Integer.parseInt(evento.eventID));
				boolean DNDSesion = checkEventoDND(evento.eventID);
				boolean DNDProfile = UserProfileR.valueExistsForUser(evento.userID, UserProfileW.NOMOLESTARTAG, evento.eventID);
				String[] regalo = RegalosControl.eligeRegaloAleatorio(DbConnector, RegalosControl.getRegalosElegidos(DbConnector, evento));
				String diff = Long.toString(diferencia/24);
				int DND = 0;
				if (DNDSesion) DND += 1;
				if (DNDProfile) DND += 2;
				RegaloPanel content = new RegaloPanel(evento, Integer.parseInt(diff) , regalo, DND);
		        JOptionPane.showOptionDialog(new JFrame("test"), content,"Se aproxima una fecha importante!", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.CLOSED_OPTION, null, new Object[]{"De acuerdo"}, null);				}
		}
		catch(Exception e){
			System.out.println("Datos de evento mal formateados " + e.toString());
		}
	}
	
	private static long tiempoEnHoras(String fecha){
		String[] valores = fecha.split("-"); 
		int MonthLengthDays = AuxFunctions.getMonthLengthDays(Integer.parseInt(valores[1]),Integer.parseInt(valores[0]));
		long tiempoEnHorasEvento = (Long.parseLong(valores[0]) * 365 * 24) + (Long.parseLong(valores[1]) * MonthLengthDays * 24) + (Long.parseLong(valores[2]) * 24); //anyos + meses + dias en segundos (puede estar mal, sobretodo meses, buscar una funcion mas completa o hacerla
		return tiempoEnHorasEvento;
	}

	
	
	/*Funcion que retorna una array con el tiempo total de cada evento en horas, ordenada de menor a mayor. A la vez tambien
	 * ordena la lista de eventos del usuario para que sigan el mismo orden que el de las fechas para que le aparezcan
	 * al usuario en ese orden	 */
	private static long[] ordenaPorFecha(ArrayList<EventData> eventos){
		long[] horas = new long[eventos.size()];
		String[] valores = new String[3];
		String valoresData;
		EventData eventoTmp,eventoTmp2;
		long tmp;
		for(int i = 0; i < eventos.size(); i++){ //Creamos una array de longs con el tiempo total en horas de cada fecha de evento, (cuanto mas tarde mas grande sera) se distribuyen en igual orden que en la lista de eventos
			valoresData = eventos.get(i).fecha;
			valores = valoresData.split("-"); 
			int MonthLengthDays = AuxFunctions.getMonthLengthDays(Integer.parseInt(valores[1]),Integer.parseInt(valores[0]));
			horas[i] = (Long.parseLong(valores[0]) * 365 * 24) + (Long.parseLong(valores[1]) * MonthLengthDays * 24) + (Long.parseLong(valores[2]) * 24); 
		}
		
		for (int i = 0; i < eventos.size()-1; i++){ //Ahora ordenamos los eventos dependiendo de su tiempo total (menor mas a la izquierda) y ordenamos asi tanto la array de numeros como la arraylist de eventos por su fecha
			if (horas[i] > horas[i+1]){
				tmp = horas[i];
				horas[i] = horas[i+1];
				horas[i+1] = tmp;
				eventoTmp = eventos.get(i);  //Puede que haya manera sin usar doble variable temporal
				eventoTmp2 = eventos.get(i+1);
				eventos.remove(i);
				eventos.add(i, eventoTmp2);
				eventos.remove(i+1);
				eventos.add(i+1, eventoTmp);
				i = -1;
			}
		}
		/*for(int i = 0; i < eventos.size(); i++){
			System.out.println(horas[i]);
			System.out.println(eventos.get(i).userID);
		}*/
		
		return horas;
	}

	public static void addEventoDND(String eventID){
		if (!idEvtDNDSesion.contains(eventID)){
			idEvtDNDSesion.add(eventID);
		}
	}

	public static void removeEventoDND(String eventID){
		if (idEvtDNDSesion.contains(eventID)){
			idEvtDNDSesion.remove(eventID);
		}
	}
	
	public static void removeAllEventosDND(){
		idEvtDNDSesion.clear();
	}
	
	public static boolean checkEventoDND(String eventID){
		return idEvtDNDSesion.contains(eventID);
	}
	
	/*Funcion para retornar el codigo SQL combinado con la seleccion de marcas/categorias incluidas/excluidas
	 * Los booleanos te dejan hacer inclusion IN(numeros) o exclusion NOT IN(numeros) porque en realidad no se
	 * si queriamos hacer que eligieran marcas/cats que quieren o que no quieren */
	private static String getCriteriaSQLCode(EventData evento,boolean exclusionMarcas, boolean exclusionCategorias){
		String finalString = "SELECT nombre FROM regalos";
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
		ArrayList<EventData> eventos = new ArrayList<EventData>();
		EventData eventoRandom = tester.generateRandomEvent("1");
		eventoRandom.fecha = "2017-09-25";
		eventos.add(eventoRandom);
		eventoRandom = tester.generateRandomEvent("2");
		eventoRandom.fecha = "2017-06-26";
		eventos.add(eventoRandom);
		eventoRandom = tester.generateRandomEvent("3");
		eventoRandom.fecha = "2017-02-25";
		eventos.add(eventoRandom);
		eventoRandom = tester.generateRandomEvent("4");
		eventoRandom.fecha = "2017-01-27";
		eventos.add(eventoRandom);
		checkForPresents(DbConnector,eventos);
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
