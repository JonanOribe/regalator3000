package regalator3000;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class GUIDataRetriever {
	
	/*Puede devolver null, combinaciones aceptadas -> nombre + marcas/regalos; tipo + categorias
	 * Basicamente te hace un SELECT columna FROM tabla; y te guarda todos los elementos que te
	 * da eso en una arry de Strings*/
	public static String[] getAllElements(DatabaseHandler DbConnector, String columna, String tabla, boolean ninguno){
		 try {
			 if (columna.equals("nombre")) {	
				 if (!(tabla.equals("regalos") || tabla.equals("marcas"))) {  return null; 	 } //combinacion no permitida
			 }
			 else if (columna.equals("tipo")) {
				 if (!tabla.equals("categorias")) {  return null; } //combinacion no permitida
			 }
			 else {
				 System.out.println("Unknown column type");
				 return null; //columna desconocida
			 }
	         Statement stmt = DbConnector.openNewConnection().createStatement(); 
	         String codigoSQL = "SELECT "+columna+" FROM "+tabla+";";
	         ResultSet rs = stmt.executeQuery(codigoSQL); //podemos reciclar stmts despues de usarlos?  pos se ve k si
	         ArrayList<String> allValues = new ArrayList<String>();
	         if(ninguno){
	        	 allValues.add("Ninguna"); //Para añadir este valor a la lista
	         }
	         while(rs.next()){
	        	 allValues.add(rs.getString(columna));
	         }
	         String[] valuesString = new String[allValues.size()]; //Pasamos la arrayList a String[] porque es lo que la GUI usaba
	         for (int i = 0; i < allValues.size();i++){
	        	 valuesString[i] = allValues.get(i);
	         }
	         rs.close();
	         stmt.close();
	         return valuesString;      
		     }
		     catch(Exception e){ //separar entre por duplicate key, por...
		    	 System.out.println("a" + e.toString());
		    	 return null;
		     }
			 finally{
				 DbConnector.closeConnection();
			 }
	}

	public static void main(String[] args) {
		/*DatabaseHandler DbConnector = new DatabaseHandler();
		String[] test = getAllElements(DbConnector, "nombre", "marcas");
		if (test != null) {
			System.out.println(Arrays.toString(test));
		}*/
	}

}
