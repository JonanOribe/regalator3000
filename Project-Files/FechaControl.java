package regalator3000;

import regalator3000.DatabaseHandler;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate; //importado para testear
import java.util.ArrayList;

	/*Clase que se encarga de la gestion de los datos de fechas en la base de datos,
	 * usando la instancia de DatabaseHandler que se crea en el prog. principal, 
	 * y despues que se logee con UserControl */
public class FechaControl {
	
	/*Añade una fecha a la base de datos, necesitas que haya un usuario logeado (autenticado en la base de datos, 
	 * vamos que la instancia de DatabaseHandler tenga un numero de usuario != -1 habiendo usado UserControl.authUser)*/
	public static boolean addDate(DatabaseHandler dbConnector, String date){
        try {
        	int id_usuario = dbConnector.getUserID(); //No hay usuario logeado
        	if(id_usuario == -1) return false;
            Statement stmt = dbConnector.getConnection().createStatement();
            String codigoSQL = "INSERT INTO fechas VALUES('"+id_usuario+"','"+date+"');";
            stmt.execute(codigoSQL);
            stmt.close();
            return true;	      
	      }
	      catch(Exception e){
	    	  System.out.println("a" + e.toString());
	    	  return false;
	      }
	}
	
	/*Elimina una fecha de la base de datos, deberia haber un usuario logeado*/
	public static boolean removeDate(DatabaseHandler dbConnector, String date){
		try{
        	int id_usuario = dbConnector.getUserID();
        	if(id_usuario == -1) return false; //No hay usuario logeado
			Statement instruccionSQL = dbConnector.getConnection().createStatement();
	        instruccionSQL.execute("DELETE FROM fechas WHERE id_usuario='"+id_usuario+"' AND fecha='"+date+"';");
	        instruccionSQL.close();
	        return true;
		}
		catch(Exception e){
			System.out.println(e.toString());
			return false;
		}
	}
	
	/*Solo usable una vez autenticado en teoria, devuelve las fechas señaladas del usuario logeado con
  el id existente, puede devolver ArrayList vacia	 */
    public static ArrayList<String> getDates(DatabaseHandler dbConnector) {
        try {
        	int id_usuario = dbConnector.getUserID();
        	if(id_usuario == -1) return null; //No hay usuario logeado
            Statement stmt = dbConnector.getConnection().createStatement();
            String codigoSQL = "SELECT fecha FROM fechas,usuario WHERE usuario.id='"+id_usuario+"' AND usuario.id=fechas.id_usuario;";
            ResultSet rs = stmt.executeQuery(codigoSQL);
            ArrayList<String> stringList = new ArrayList<String>();
            while(rs.next()){
                stringList.add(rs.getString("fecha"));
            }
            stmt.close();
            rs.close();
            return stringList;		      
	      }
	      catch(Exception e){
	    	  System.out.println("a" + e.toString());
	    	  return new ArrayList<String>();
	      }
	}


	public static void main(String[] args) {
		/*DatabaseHandler DbConnector = new DatabaseHandler("regalator","root","PWD DE LA BASE DE DATOS");
		String user="juan",pwd="lol"; //CAMBIA EL USER PARA TESTEAR, UN USER QUE EXISTA
		UserControl.logInUser(DbConnector,user, pwd);
		ArrayList<String> testList = getDates(DbConnector);
		System.out.println("Fechas iniciales del user: ");
		for(int i=0; i < testList.size(); i++) {
			System.out.println("Fecha de Juan " + i + ": " + testList.get(i));
		}
		System.out.println("Añadiendo fecha actual y remirando fechas del user: ");
		System.out.println(addDate(DbConnector,LocalDate.now().toString()));
		testList = null;
		testList = getDates(DbConnector);
		for(int i=0; i < testList.size(); i++) {
			System.out.println("Fecha Juan " + i + ": " + testList.get(i));
		}
		System.out.println("Eliminando fecha actual y remirando fechas del user:  ");
		System.out.println(removeDate(DbConnector,LocalDate.now().toString()));
		testList = null;
		testList = getDates(DbConnector);
		for(int i=0; i < testList.size(); i++) {
			System.out.println("Fecha Juan " + i + ": " + testList.get(i));
		}
		System.out.println("ID del user logeado: " + DbConnector.getUserID());*/
	}

}
