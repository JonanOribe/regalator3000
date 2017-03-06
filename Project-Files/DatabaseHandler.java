package regalator3000;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
public class DatabaseHandler {
	
	/*Hacer constructor para tener instancia permanente de esta clase con
	 *  private variables guardando cosas como la conexion con la base de datos (hasta que acabe el programa, se cambie etc)
	 *  Supongo que la conexion se mantendra hasta que el usuario se haya logueado y entonces se cerrara,
	 *  volviendose a abrir si hay que modificar sus datos en la BdD o buscar regalos etc.
	 Probablemente Guarda el ID_User del usuario cuando se logea y usalo en metodos internos hasta que se desloguea
	 */
	
	
	/*Crea una conexion a una base de datos, PUEDE DEVOLVER NULL*/
	private static Connection connectToDb(String dbName,String user,String pwd) {
		Connection unaConexion = null;
		try{
			/*Auto reconnect=true, solo se cerrara llamando al metodo close() de la connection, usamos ssl para mas seguridad(a lo mejor
			para usuarios remotos no va bien comprobar; Tambien esta la opcion verifyServerCertificate=true/false sobretodo para modo local*/
			unaConexion  = DriverManager.getConnection("jdbc:mysql://localhost/" + dbName+"?autoReconnect=true&useSSL=true",user, pwd);
		}
		catch (Exception e){
			System.out.println(e.toString());
		}
		return unaConexion;
	}
	
	/*Devuelve la id del usuario si el usuario esta en la BdD*/
	public static int authUser(Connection dbConn, String name, String pwd){
		//ResultSet resultadosConsulta = null;
		try{
			Statement instruccionSQL = dbConn.createStatement();
	        ResultSet resultadosConsulta = instruccionSQL.executeQuery("SELECT id FROM usuario WHERE nombre='"+name+"' AND password='"+pwd+"';");
	        int resultado = -1;
	        if(resultadosConsulta.next()){
	        	resultado = resultadosConsulta.getInt("id");
	        }
		    return resultado;
		}
		catch(Exception e){
			System.out.println(e.toString());
			return -1;
		}
	}
	
	/*INSERTA UN USUARIO EN LA BASE DE DATOS CON nombre y password determinado,
	el id es null ya que será auto_incremented sino habria que buscar max(id)+1*/
	public static boolean insertUser(Connection dbConn, String name, String pwd){
		try{
			Statement instruccionSQL = dbConn.createStatement();
	        instruccionSQL.execute("INSERT INTO usuario VALUES(null,'"+name+"','"+pwd+"');");
	        instruccionSQL.close();
	        return true;
		}
		catch(Exception e){
			System.out.println(e.toString());
			return false;
		}
	}
	
	/*Elimina un usuario SOLO si se conocen su nombre i password,
	 * LA BASE DE DATOS NO PUEDE ESTAR EN SAFE MODE!
	 */
	public static boolean removeUser(Connection dbConn, String name, String pwd){
		try{
			Statement instruccionSQL = dbConn.createStatement();
	        instruccionSQL.execute("DELETE FROM usuario WHERE nombre='"+name+"' AND password='"+pwd+"';");
	        instruccionSQL.close();
	        return true;
		}
		catch(Exception e){
			System.out.println(e.toString());
			return false;
		}
	}
	
	
	/*Añade una data a la base de datos, en el presupuesto que el usuario este autenticado*/
	public static boolean addDate(Connection dbConn, int id_usuario, String date){
        try {
            Statement stmt = dbConn.createStatement();
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
	
	public static boolean removeDate(Connection dbConn, int id_usuario, String date){
		try{
			Statement instruccionSQL = dbConn.createStatement();
	        instruccionSQL.execute("DELETE FROM fechas WHERE id_usuario='"+id_usuario+"' AND fecha='"+date+"';");
	        instruccionSQL.close();
	        return true;
		}
		catch(Exception e){
			System.out.println(e.toString());
			return false;
		}
	}
	
	/*Solo usable una vez autenticado en teoria, devuelve las fechas señaladas del usuario con 
	 * nombre determinado puede devolver null
	 */
    public static ArrayList<String> getDates(Connection dbConn, int userID) {
        try {
            Statement stmt = dbConn.createStatement();
            String codigoSQL = "SELECT fecha FROM fechas,usuario WHERE usuario.id='"+userID+"' AND usuario.id=fechas.id_usuario;";
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
	    	  return null;
	      }
	}
	

	public static void main(String[] args) { 
		Connection dbConn = connectToDb("regalator","root","PASSWORD DE LA BASE DE DATOS");
		String user="juan",pwd="lol"; //CAMBIA EL USER PARA TESTEAR, UN USER QUE EXISTA
		int userID = authUser(dbConn, user, pwd);
		System.out.println("ID USUARIO ENCONTRADA: " + userID);
		boolean addedUser = insertUser(dbConn, "hola","bola");
		System.out.println("USER AÑADIDO: " + addedUser);
		boolean removeUser = removeUser(dbConn,"hola","bola");
		System.out.println("USER ELIMINADO: " + removeUser);
		ArrayList<String> testList = getDates(dbConn,userID);
		System.out.println("Fechas iniciales del user: ");
		for(int i=0; i < testList.size(); i++) {
			System.out.println("Fecha de Juan " + i + ": " + testList.get(i));
		}
		System.out.println("Añadiendo fecha actual y remirando fechas del user: ");
		System.out.println(addDate(dbConn,userID,LocalDate.now().toString()));
		testList = null;
		testList = getDates(dbConn,userID);
		for(int i=0; i < testList.size(); i++) {
			System.out.println("Fecha Juan " + i + ": " + testList.get(i));
		}
		System.out.println("Eliminando fecha actual y remirando fechas del user:  ");
		System.out.println(removeDate(dbConn,userID,LocalDate.now().toString()));
		testList = null;
		testList = getDates(dbConn,userID);
		for(int i=0; i < testList.size(); i++) {
			System.out.println("Fecha Juan " + i + ": " + testList.get(i));
		}
		try{
		dbConn.close();
		}
		catch(Exception e){
		}
	}

}
