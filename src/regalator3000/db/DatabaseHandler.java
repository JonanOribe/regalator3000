package regalator3000.db;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLTimeoutException;

/*Clase que guarda la conexion con la base de datos para que las demas clases puedan acceder a ella,
 * Hay que instanciarla y guarda en actuaUserID el id en la base de datos del usuario logeado para
 * que las demas clases puedan cambiar sus datos. 
 * 
 * Para logear/deslogear a un usuario usar los metodos de UserControl, no 
 * hacerlo a lo bruto con setUserID...
 */
public class DatabaseHandler {
	
	/*Static names that the default db constructor scripts set up, change if you tool with them*/
	private static String standardDbName = "regalator";
	private static String noPrivilegesUser = "usuarioMedio";
	private static String userPassword = "sinprivilegios1";
	
	private Connection actualConnection;
	private int actualUserID = -1;

	/*Constructor vacio, a lo mejor cambiar a metodos estaticos cuando tengas la logica del programa principal*/
	public DatabaseHandler(){ 
		try{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch(Exception e){
			System.out.println("El driver para conectarse con la BBDD no existe,\n debes incluir las dependencias (mysql-connector-java) en tu classpath " + e.toString());
			System.out.println("La pagina de descarga es: https://dev.mysql.com/downloads/connector/j/");
			System.out.println("Busca mysql-connector-java-*(usado version 5.1.41), descargalo, descomprime y linkea el .jar que hay dentro con el mismo nombre");
			System.out.println("Si hay más errores con la base de datos comprueba que has usado los tres scripts para generarla, poner datos y poner el usuario");
		}
	}

	//Metodo por si has de crear una conexion con parametros diferentes
	public Connection openNewConnection(String nombreBdD, String superUser, String pwd){
		this.actualConnection = connectToDb(nombreBdD, superUser, pwd);
		return this.actualConnection;
	}
	/*Funcion estandard para obtener la conexion con el usuario/pwd construidos previamente con el 
	acceso y funciones hechas a su medida para el programa*/
	public Connection openNewConnection(){
		this.actualConnection = connectToDb(standardDbName,noPrivilegesUser,userPassword);
		return this.actualConnection;
	}
	
	
	public int getUserID(){
		return actualUserID;
	}
	
	public void setUserID(int newID){
		this.actualUserID = newID;
	}
	
	public void closeConnection(){
		if(this.actualConnection == null) {System.out.println("Attempting to close unexistant connection"); return;} // no tendria que pasar
		try{
			this.actualConnection.close();
		}
		catch (Exception e) {
			System.out.println("Unable to close connection " + e.toString());
		}
	}

	/*Crea una conexion a una base de datos, PUEDE DEVOLVER NULL*/
	private static Connection connectToDb(String dbName,String user,String pwd) {
		Connection unaConexion = null;
		try{
			/*Auto reconnect=true, solo se cerrara llamando al metodo close() de la connection, usamos ssl para mas seguridad(a lo mejor
			para usuarios remotos no va bien comprobar; Tambien esta la opcion verifyServerCertificate=true/false sobretodo para modo local*/
			unaConexion  = DriverManager.getConnection("jdbc:mysql://localhost/" + dbName+"?autoReconnect=true&useSSL=true",user, pwd);
		}
		catch (Exception e){
			System.out.println("Unable to stablish a connection " + e.toString());
		}
		return unaConexion;
	}
	

	public static void main(String[] args) throws SQLTimeoutException{ 

	}

}
