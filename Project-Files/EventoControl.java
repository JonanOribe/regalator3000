/*Comentarios: Hay algunas funciones que usan String para unos valores y int para otros que habria que estandarizar, tambien en EventData,
 * Necesita muuucho mas testeo.
 */
package regalator3000;

import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;


 /*Métodos importantes:
 *
 *addEvent(DbHandler, eventoData): Te añade un evento a la base de datos. Los eventIDs son auto_increment por ahora, pero hay que introducirle todo lo demas, te llena la tabla
 *de marcas y categorias con las id_evento id_marca/cat necesarios
 *
 *removeEvent(DbHandler, eventoID: Te elimina el evento con eventoID de la base de datos, en teoria las tablas eventos_marcas etc tienen puesto lo de on update/delete cascade asi que 
 *se deberian eliminar/updatear solas. Es lo que parece que pasaba pero testear más.
 *
 *modifyEvent(DbHandler, eventoData): Te cambia los datos de un evento por los del nuevo que introduces (todos menos usuario_id y eventoID que se deberian mantener), tambien updatea las
 *tablas eventos_marcas/cats con las nuevas preferencias introducidas en el nuevo evento, cargandose las antiguas.
 *
 *getEvents(DbHandler,userID): Te devuelve toda la informacion (en forma de ArrayList de EventData) de los eventos de un usuario, te lo devuelve sin las marcas/categorias elegidas
 *a proposito ya que en teoria no se necesitara en la aplicacion y para hacer currar menos a la base de datos.
 *
 *getEventData(DbHandler,eventID) : Te devuelve toda la informacion de un evento con eventID concreta como un objeto EventData, este contiene toda la informacion del evento.
 *	
 *El uso teorico de las 3 ultimas es que el usuario pide modificar sus eventos, se le enseña las fechas y descripciones de sus eventos y que elija el que quiera cambiar,
 *una vez elegido y con el eventID del evento elegido, se coge la getEventData dek evento, se le pone en pantalla y se le deja cambiar los datos. Se cogen los datos cambiados 
 *y se hace un modifyEvent, poniendo los nuevos datos en el sitio del antiguo evento.
	*/

/*Clase que se encarga de la gestion de los datos de fechas en la base de datos
 *usando la instancia de DatabaseHandler que se creara en el programa principal
 **y despues que un usuario se haya logeado con los metodos que hay en UserControl.*/
public class EventoControl {
	
	private Random randomGen = new Random();
	
	
	/*Funcion auxiliar para obtener la query total, 0 es para categorias, 1 es para marcas, 
	 * devuelve la query para llenar la tabla categorias o marcas con los valores de un evento*/
	private static String compoundSQLText(int type, String id_evento,int[] valores){
		String codigoSQL ="";
		if(type == 0) {
            codigoSQL = "INSERT INTO eventos_marcas (id_evento,id_marca) VALUES";
		}
		else {
            codigoSQL = "INSERT INTO eventos_categorias (id_evento,id_categoria) VALUES";
		}
        for (int i = 0; i < valores.length; i++) {
        	codigoSQL += "('"+id_evento+"','"+valores[i]+"')"; //Combinalos todos en un query del palo VALUES(id_ev,marca1),(id_ev,marca2)...
        	if( i != (valores.length-1)){
        		codigoSQL += ", ";
        	}
        	else {
        		codigoSQL +=";";
        	}
        }
		return codigoSQL;
	}
	/*Añade un evento a la base de datos, necesitas que haya un usuario logeado (autenticado en la base de datos, 
	 * vamos que la instancia de DatabaseHandler tenga un numero de usuario != -1 habiendo usado UserControl.logInUser)
	 * Devuelve el eventID del nuevo evento agregado o -1 si no se ha podido agregar*/
	public static int addEvent(DatabaseHandler DbConnector, EventData evento){
        try {
        	int id_usuario = DbConnector.getUserID(); 
        	if(id_usuario == -1) return -1; //No hay usuario logeado
            Statement stmt = DbConnector.openNewConnection().createStatement(); //Creo que hay que usar un statement por query y no se reciclan pero no estoy seguro
            String codigoSQL = "INSERT INTO eventos VALUES(null,'"+id_usuario+"','"+evento.fecha+"','"+evento.descripcion+"','"+evento.diasAviso+"',null);";
            stmt.execute(codigoSQL); //podemos reciclar stmts despues de usarlos?  pos se ve k si
            codigoSQL = "SELECT max(id) as id FROM eventos"; //para saber la id del evento agregado, problemas si hay mucha actividad en la BdD????
            ResultSet rs = stmt.executeQuery(codigoSQL);
            String nuevoEventoID = "";
            while(rs.next()){
            	nuevoEventoID = rs.getString("id");
            }
            rs.close();
            codigoSQL = compoundSQLText(0,nuevoEventoID,evento.marcas); //0 para marcas
            //System.out.println("enviando a eventos_marcas: " + codigoSQL);
        	stmt.execute(codigoSQL);
            codigoSQL = compoundSQLText(1,nuevoEventoID,evento.categorias);
            stmt.execute(codigoSQL);
            //System.out.println("enviando a eventos_categorias: " + codigoSQL);
        	stmt.close();
            return Integer.parseInt(nuevoEventoID);	      
	      }
	      catch(Exception e){ //separar entre por duplicate key, por...
	    	  System.out.println("a" + e.toString());
	    	  return -1;
	      }
		finally{
			DbConnector.closeConnection();
		}
	}
	
	/*Elimina un evento de la base de datos, deberia haber un usuario logeado*/
	public static boolean removeEvent(DatabaseHandler DbConnector, int eventID){
		try{
        	int id_usuario = DbConnector.getUserID();
        	if(id_usuario == -1) return false; //No hay usuario logeado
			Statement instruccionSQL = DbConnector.openNewConnection().createStatement();
	        instruccionSQL.execute("DELETE FROM eventos WHERE id_usuario='"+id_usuario+"' AND id='"+eventID+"';"); //en teoria on delete cascade se encarga del resto(comprobar mas casos...)
	        instruccionSQL.close();
	        return true;
		}
		catch(Exception e){
			System.out.println(e.toString());
			return false;
		}
		finally{
			DbConnector.closeConnection();
		}
	}
	
	/*Modifica un evento con los nuevos datos contenidos en eventoNuevo, no modifica userID o eventID */
	public static boolean modifyEvent(DatabaseHandler DbConnector, EventData eventoNuevo) {
    	
		int id_usuario = DbConnector.getUserID(); 
    	if (id_usuario == -1) { return false; } //usuario no logeado
    	try{
    		//Me gustaria borrar y crear un nuevo evento pero eventID es auto_increment por lo que no se puede (a lo mejor cambiar?).
    		//Hay que modificar datos de 3 tablas: tabla eventos, tabla eventos_marcas y eventos_categorias
	    	Statement stmt = DbConnector.openNewConnection().createStatement();
	    	String codigoSQL = "UPDATE eventos SET fecha='"+eventoNuevo.fecha+"' , descripcion='"+eventoNuevo.descripcion+"',"
	    			+ "diasAviso='"+eventoNuevo.diasAviso+"',regaloConcreto='"+eventoNuevo.regaloConcreto+"'  WHERE id='"+eventoNuevo.eventID+"';";
	    	stmt.execute(codigoSQL);
	    	codigoSQL = "DELETE from eventos_marcas where id_evento='"+eventoNuevo.eventID+"';";
	    	stmt.execute(codigoSQL);	
	    	codigoSQL = "DELETE from eventos_categorias where id_evento='"+eventoNuevo.eventID+"';";
	    	stmt.execute(codigoSQL);	
            codigoSQL = compoundSQLText(0,eventoNuevo.eventID,eventoNuevo.marcas);
            stmt.execute(codigoSQL);
            codigoSQL = compoundSQLText(1,eventoNuevo.eventID,eventoNuevo.categorias);	    	
            stmt.execute(codigoSQL);
	        stmt.close(); 
	        return true;
	   	}
    	catch(Exception e){
			System.out.println(e.toString());
			return false;
    	}
		finally{
			DbConnector.closeConnection();
		}
	}
	
	/*Solo usable una vez autenticado, devuelve los datos de TODOS LOS EVENTOS DEL USUARIO logeado con
  el id existente, te devuelve un arraylist de EventData PERO SOLO CON eventoID, fecha y descripcion (aparte del usuarioID)*/
    public static ArrayList<EventData> getEvents(DatabaseHandler DbConnector) {
        try {
        	int id_usuario = DbConnector.getUserID();
        	if(id_usuario == -1) return null; //No hay usuario logeado
            Statement stmt = DbConnector.openNewConnection().createStatement();
            //System.out.println("USER ID FOR SELECTION: " + id_usuario); //
            String codigoSQL = "call getEventos('"+id_usuario+"');";
            ResultSet rs = stmt.executeQuery(codigoSQL);
            ArrayList<EventData> eventos = new ArrayList<EventData>();
            while(rs.next()){
            	eventos.add(new EventData(Integer.toString(id_usuario),rs.getString("id"), rs.getString("fecha"), rs.getString("descripcion"), rs.getInt("diasAviso")));
            }
            //for (int i = 0; i < eventos.size(); i++) {
            	//eventos.get(i).toConsole();
            //}
            return eventos;
        }
	      catch(Exception e){
	    	  System.out.println("a" + e.toString());
	    	  return null;
	      }
		finally{
			DbConnector.closeConnection();
		}
	}
    
    /*Devuelve TODOS los datos de un evento con EventID concreto del usuario logeado*/
    public static EventData getEventData(DatabaseHandler DbConnector, int eventID){
    	try{
	    	int id_usuario = DbConnector.getUserID();
	    	if(id_usuario == -1) return null; //No hay usuario logeado
	        Statement stmt = DbConnector.openNewConnection().createStatement(); //puedes reciclar statements si no los cierras por lo visto
	        String codigoSQL = "SELECT fecha,descripcion,diasAviso,regaloConcreto FROM eventos WHERE eventos.id_usuario='"+id_usuario+"' AND eventos.id='"+eventID+"';";
	        ResultSet rs = stmt.executeQuery(codigoSQL);
	        EventData evento = new EventData(Integer.toString(id_usuario));
	        String eventoID = Integer.toString(eventID);
	        evento.eventID = eventoID;
	        while(rs.next()) { //Should be a single time but need to use next to advance to the first row apparently?
	        	evento.fecha = rs.getString("fecha");
	        	evento.descripcion = rs.getString("descripcion");
	        	evento.diasAviso = rs.getInt("diasAviso");
	        	evento.regaloConcreto = rs.getInt("regaloConcreto"); //pot donar error si null I guess...
	        }
	        codigoSQL = "SELECT DISTINCT id_marca FROM eventos_marcas WHERE eventos_marcas.id_evento='"+eventoID+"';"; 
	        rs = stmt.executeQuery(codigoSQL);
	        ArrayList<String> arrayTemporal = new ArrayList<String>(); //Uso aqui arraylist porque no sabes a priori cuantos numeros retornara por lo que no puedes crear una array de tamanyo estatico
	        while(rs.next()){
	        	arrayTemporal.add(rs.getString("id_marca"));
	        }
	        int[] arrayTempInt = new int[arrayTemporal.size()];
	        for(int i = 0; i < arrayTemporal.size(); i++){
	        	arrayTempInt[i] = Integer.parseInt(arrayTemporal.get(i));
	        }
	        evento.marcas = arrayTempInt;
	        arrayTemporal.clear();
	        codigoSQL = "SELECT DISTINCT id_categoria FROM eventos_categorias WHERE eventos_categorias.id_evento='"+eventoID+"';"; 
	        rs = stmt.executeQuery(codigoSQL);
	        while(rs.next()){
	        	arrayTemporal.add(rs.getString("id_categoria")); //reusamo
	        }
	        arrayTempInt = new int[arrayTemporal.size()];
	        for(int i = 0; i < arrayTemporal.size(); i++){
	        	arrayTempInt[i] = Integer.parseInt(arrayTemporal.get(i));
	        }
	        evento.categorias = arrayTempInt;
	        //evento.toConsole();
	        return evento;
    	}
    	catch(Exception e){
    		System.out.println("a" + e.toString());
	    	  return null;
    	}
		finally{
			DbConnector.closeConnection();
		}
    }
    
	/*Te crea un evento con id 1-2, fecha de hoy, nombre evento test, datos aleatorios en sus marcas/cats elegidas/diasAviso*/
	public EventData generateRandomEvent(String userID){
		String eventID = Integer.toString(this.randomGen.nextInt(2)+1); //esto no importa excepto en el caso de modifyEvent y entonces cambialo manualmente
		int oldValue = randomGen.nextInt(7)+1;
		int oldValue2 = randomGen.nextInt(6)+1;
		if(oldValue == oldValue2) { oldValue2++; }  //Para que no te de dos valores iguales i te diga lo de duplicate primary key
		int[] cats = {oldValue,oldValue2};
		oldValue = randomGen.nextInt(7)+1; 
		oldValue2 = randomGen.nextInt(6)+1;
		if(oldValue == oldValue2) { oldValue2++; }
		int[] marcas = {oldValue,oldValue2};  
		EventData evento;
		evento = new EventData(userID, eventID,LocalDate.now().toString(),"evento test", cats, marcas,randomGen.nextInt(7)+1,0);
		return evento;
	}


	public static void main(String[] args) {
		/*DatabaseHandler DbConnector = new DatabaseHandler();
		EventoControl removeSoon = new EventoControl();
		String user="Juan",pwd="A2445D"; //CAMBIA EL USER PARA TESTEAR, UN USER QUE EXISTA
		if(UserControl.logInUser(DbConnector,user, pwd)){
			System.out.println("Usuario logeado, bienvenido " + user);
			//Test adicion:
			EventData randomEvent = removeSoon.generateRandomEvent(Integer.toString(DbConnector.getUserID()));
			System.out.println("Añadiendo evento aleatorio,datos: ");
			randomEvent.toConsole();
			int newEventID = addEvent(DbConnector,randomEvent);
			System.out.println("Comprobando los datos escritos en la base de datos... ");
			EventData test = getEventData(DbConnector,newEventID);
			test.toConsole();
			ArrayList<EventData> testList = getEvents(DbConnector); //recordatorio que getEvents no te enseña eventos_marcas o eventos_categorias (diseñado asi)
			System.out.println("EVENTOS TOTALES DEL USUARIO SIN MARCAS/CATS: " + user);
			for (int i = 0; i < testList.size(); i++) { 
				testList.get(i).toConsole();
				System.out.println("");
			}
			//Test modificacion
			EventData oldEvent = getEventData(DbConnector,2);
			randomEvent = removeSoon.generateRandomEvent("1");
			randomEvent.eventID = "2";
			System.out.println("Antiguos datos del evento 2: ");
			oldEvent.toConsole();
			System.out.println("Modificando evento 2 del usuario con nuevos datos y comprobando nuevos valores: ");
			modifyEvent(DbConnector,randomEvent);
			randomEvent = null;
			randomEvent = getEventData(DbConnector,2);
			randomEvent.toConsole();
			testList = null;
			testList = getEvents(DbConnector); //recordatorio que getEvents no te enseña eventos_marcas o eventos_categorias (diseñado asi)
			System.out.println("EVENTOS TOTALES DEL USUARIO SIN MARCAS/CATS: " + user);
			for (int i = 0; i < testList.size(); i++) {
				testList.get(i).toConsole();
				System.out.println("");
			}
			//Test eliminacion
			System.out.println("Eliminando evento creado y remirando eventos(modificados) del user:  ");
			System.out.println(removeEvent(DbConnector,newEventID));
			testList = null;
			testList = getEvents(DbConnector); //recordatorio que getEvents no te enseña eventos_marcas o eventos_categorias (diseñado asi)
			System.out.println("EVENTOS TOTALES DEL USUARIO SIN MARCAS/CATS: " + user);
			for (int i = 0; i < testList.size(); i++) {
				testList.get(i).toConsole();
				System.out.println("");
			}
			System.out.println("ID del user logeado: " + DbConnector.getUserID());
		}
		else {
			System.out.println("Usuario o password no son correctos");
		}*/
	}
}
