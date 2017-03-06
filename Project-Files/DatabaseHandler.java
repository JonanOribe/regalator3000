package regalator3000;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import java.sql.Statement;
import java.util.ArrayList;
public class DatabaseHandler {
	
	/*Hacer constructor para tener instancia permanente de esta clase con
	 *  private variables guardando cosas como la conexion con la base de datos (hasta que acabe el programa, se cambie etc)
	 *  Supongo que la conexion se mantendra hasta que el usuario se haya logueado y entonces se cerrara,
	 *  volviendose a abrir si hay que modificar sus datos en la BdD o buscar regalos etc.
	 */
	
  /*EN CONSTRUCCION, ANTES DE USAR -> MIRATE EL MAIN (cambia el nombre/pwd admin, de la base de datos
  y que las tables de usuario y fechas tengan datas y el nombre que uso en el main,
  A HACER: distinguir entre sqlexceptions si hay error, si no esta conectado a db? lo de que devuelvan null no todas;
  Añadir metodos para fechas*/
	
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
	
	/*Devuelve cierto si el usuario esta en la BdD*/
	public static boolean authUser(Connection dbConn, String name, String pwd){
		ResultSet resultadosConsulta = null;
		try{
			Statement instruccionSQL = dbConn.createStatement();
	        resultadosConsulta = instruccionSQL.executeQuery("SELECT * FROM usuario WHERE nombre='"+name+"' AND password='"+pwd+"';");
		    return resultadosConsulta.first();
		}
		catch(Exception e){
			System.out.println(e.toString());
			return false;
		}
	}
	
	/*INSERTA UN USUARIO EN LA BASE DE DATOS CON nombre y password determinado,
	el id es null ya que será auto_incremented sino habria que buscar max(id)+1*/
	public static boolean insertUser(Connection dbConn, String name, String pwd){
		try{
			Statement instruccionSQL = dbConn.createStatement();
	        instruccionSQL.execute("INSERT INTO usuario VALUES(5,'"+name+"','"+pwd+"');");
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
		
	/*Solo usable una vez autenticado en teoria, devuelve las fechas señaladas del usuario con 
	 * nombre determinado puede devolver null
	 */
    public static ArrayList<String> getDates(Connection dbConn, String nombre) {
        try {
            Statement stmt = dbConn.createStatement();
            String codigoSQL = "SELECT fecha FROM fechas,usuario WHERE usuario.nombre='"+nombre+"' AND usuario.id=fechas.id_usuario;";
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
		Connection dbConn = connectToDb("regalator","root","Rootpwd");
		String user="juan",pwd="lol";
		boolean isUserIn = authUser(dbConn, user, pwd);
		System.out.println(isUserIn);
		boolean addedUser = insertUser(dbConn, "hola","bola");
		System.out.println(addedUser);
		boolean removeUser = removeUser(dbConn,"hola","bola");
		System.out.println(removeUser);
		ArrayList<String> testList = getDates(dbConn,"juan");
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
