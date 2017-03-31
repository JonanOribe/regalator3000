package regalator3000.db;


import java.sql.ResultSet;
import java.sql.Statement;

/*Clase con metodos estaticos (no hay que crear instancia de clase) para gestionar los usuarios que hay en la base de datos +
 * Work in progress los del fichero en memoria con preferencias.
 * Como referencia en el programa principal deberia instanciarse la clase DatabaseHandler que guardara
 * el id del usuario cuando este se logee con logInUser. Tambien metodos para desloguearlo y
 * para cambiar los datos de usuarios en la Base de Datos. */
public class UserControl {

	/*Inserta un usuario en la base de datos con nombre y password determinado,
	Devuelve su id por si quieres logearlo o ver si se ha insertado en la BBDD1
	DEVUELVE -1 ERROR, DEVUELVE -2 SI USUARIO EXISTE YA CON ESE NOMBRE*/
	public static int insertUser(DatabaseHandler DbConnector, String name, String pwd){
		int newUserID = -1;
		try{
			Statement instruccionSQL = DbConnector.openNewConnection().createStatement();
	        ResultSet rs = instruccionSQL.executeQuery("SELECT addUser('"+name+"','"+pwd+"') as newUserID;");
	        while(rs.next()) {
	        	newUserID = rs.getInt("newUserID");
	        }
	        instruccionSQL.close();
	        
	        return newUserID;
		}
		catch(Exception e){
			System.out.println("Error al agregar el usuario " + e.toString());
			return newUserID;
		}
		finally{
			DbConnector.closeConnection();
		}
	}
	
	/*Logea a un usuario, poniendo en la instancia de la clase DBHandler el valor de su id.
	 * Añadido booleano para saber si se ha podido logear o no, habra que añadir algo para
	 * diferenciar excepciones de mal user/pwd
	 * USA UNA FUNCION QUE SE DEBE CREAR PREVIAMENTE EN LA BASE DE DATOS CON EL SCRIPT QUE HAY EN GITHUB*/
	public static boolean logInUser(DatabaseHandler DbConnector, String name, String pwd){
		//ResultSet resultadosConsulta = null;
		try{
			Statement instruccionSQL = DbConnector.openNewConnection().createStatement();
	        ResultSet resultadosConsulta = instruccionSQL.executeQuery("SELECT getuserID('"+name+"','"+pwd+"') as id;");
	        int resultado = -1;
	        resultadosConsulta.next();
	        resultado = resultadosConsulta.getInt("id");
	        DbConnector.setUserID(resultado);
	        if(resultado == -1) { //La funcion devolvera un valor siempre, aunque sea -1
	        	return false;
	        }
	        return true;
	    }
		catch(Exception e){
			System.out.println("Error al hacer el login del usuario " + e.toString());
			return false;
		}
		finally{
			DbConnector.closeConnection();
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
	public static int removeUser(DatabaseHandler DbConnector, String name, String pwd){
		try{
			Statement instruccionSQL = DbConnector.openNewConnection().createStatement();
	        ResultSet rs = instruccionSQL.executeQuery("select removeUser('"+name+"','"+pwd+"') as borrado;");
	        rs.next();
	        int resultado = rs.getInt("borrado");
	        instruccionSQL.close();
	        return resultado;
		}
		catch(Exception e){
			System.out.println("Error al borrar el usuario " + e.toString());
			return -2;
		}
		finally{
			DbConnector.closeConnection();
		}
	}
	
	public static void main(String[] args) {
		/*DatabaseHandler DbConnector = new DatabaseHandler();
		String user="juan",pwd="a2445d"; //CAMBIA EL USER PARA TESTEAR, UN USER QUE EXISTA. NO ES CASE SENSITIVE(CAMBIAR? COMO?)
		logInUser(DbConnector,user, pwd);
		System.out.println(DbConnector.getUserID());
		System.out.println(insertUser(DbConnector,"lol","test"));
		System.out.println(removeUser(DbConnector,"lol","test"));*/

	}

}
