package regalator3000;

import java.util.ArrayList;

/*No usado ya, guarda los datos de un evento de un usuario*/
public class oldEventData {
	public String userID;
	public String eventID;
	public ArrayList<String> eventoMarcas;
	public ArrayList<String> eventoCategorias;
	
	//Para hacer un evento con todos los datos vacios...
	public oldEventData(String userID){
		this.userID = userID;
	}
		
	public oldEventData(String userID, String eventID, String[] categorias, String[] marcas){
		this.userID = userID;
		this.eventID = eventID;
		eventoCategorias = new ArrayList<String>();
		eventoMarcas = new ArrayList<String>();
		int i;
		for (i = 0; i < categorias.length; i++){
			eventoCategorias.add(categorias[i]);
		}
		for (i = 0; i < marcas.length; i++){
			eventoMarcas.add(marcas[i]);
		}
	}
		
		/*Metodo para visualizar el contenido de un EventData en consola, usado para debug mostly*/
	public void toConsole(){ 
		System.out.println("ID USUARIO: " + this.userID + ", ID EVENTO: " + this.eventID +".");
		System.out.println("CATEGORIAS ELEGIDAS: " + listMyArray(eventoCategorias) +"; MARCAS ELEGIDAS: " + listMyArray(eventoMarcas) );
		}
		
	//Usado en el metodo toConsole para ver los elementos de las arrays seguidos
	public String listMyArray(ArrayList<String> array){
		String totalList = "";
		if (array == null) return "";
		for(int i = 0; i < array.size(); i++){
			if (i < array.size() - 1) {
				totalList += (array.get(i) + ",");
			}
			else{
				totalList += array.get(i);
			}
		}
		return totalList;
	}
}
