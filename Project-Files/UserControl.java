package regalator3000;

import regalator3000.DatabaseHandler;
import java.sql.ResultSet;
import java.sql.Statement;

/*Clase con metodos estaticos (no hay que crear instancia de clase) para gestionar los usuarios que hay en la base de datos +
 * Work in progress los del fichero en memoria con preferencias.
 * Como referencia en el programa principal deberia instanciarse la clase DatabaseHandler que guardara
 * el id del usuario cuando este se logee con logInUser. Tambien metodos para desloguearlo y
 * para cambiar los datos de usuarios en la Base de Datos. */
public class UserControl {

	/*Inserta un usuario en la base de datos con nombre y password determinado,
	el id es null ya que será auto_incremented sino habria que buscar max(id)+1*/
	public static boolean insertUser(DatabaseHandler dbConnector, String name, String pwd){
		try{
			Statement instruccionSQL = dbConnector.getConnection().createStatement();
	        instruccionSQL.execute("INSERT INTO usuario VALUES(null,'"+name+"','"+pwd+"');");
	        instruccionSQL.close();
	        return true;
		}
		catch(Exception e){
			System.out.println(e.toString());
			return false;
		}
	}
	
	/*Logea a un usuario, poniendo en la instancia de la clase DBHandler el valor de su id.*/
	public static void logInUser(DatabaseHandler DbConnector, String name, String pwd){
		//ResultSet resultadosConsulta = null;
		try{
			Statement instruccionSQL = DbConnector.getConnection().createStatement();
	        ResultSet resultadosConsulta = instruccionSQL.executeQuery("SELECT id FROM usuario WHERE nombre='"+name+"' AND password='"+pwd+"';");
	        int resultado = -1;
	        if(resultadosConsulta.next()){
	        	resultado = resultadosConsulta.getInt("id");
	        	DbConnector.setUserID(resultado);
	        }
	    }
		catch(Exception e){
			System.out.println(e.toString());
		}
	}
	
	/*Deslogea a un usuario*/
	public static void logOutUser(DatabaseHandler DbConnector){
		DbConnector.setUserID(-1);
	}
	
	/*Comprueba si hay algun user logeado*/
	public static boolean isUserLogged(DatabaseHandler DbConnector){
		if (DbConnector.getUserID() != -1){
			return true;
		}
		return false;
	}
	
	/*Elimina un usuario SOLO si se conocen su nombre i password,
	 * LA BASE DE DATOS NO PUEDE ESTAR EN SAFE MODE!(sino añadir LIMIT 1 creo?	 */
	public static boolean removeUser(DatabaseHandler dbConnector, String name, String pwd){
		try{
			Statement instruccionSQL = dbConnector.getConnection().createStatement();
	        instruccionSQL.execute("DELETE FROM usuario WHERE nombre='"+name+"' AND password='"+pwd+"';");
	        instruccionSQL.close();
	        return true;
		}
		catch(Exception e){
			System.out.println(e.toString());
			return false;
		}
	}
	
	public static void main(String[] args) {
		/*DatabaseHandler DbConnector = new DatabaseHandler("regalator","root","PASSWORD DE LA BASE DE DATOS");
		String user="juan",pwd="lol"; //CAMBIA EL USER PARA TESTEAR, UN USER QUE EXISTA
		logInUser(DbConnector,user, pwd);
		System.out.println(DbConnector.getUserID());*/

	}

}
