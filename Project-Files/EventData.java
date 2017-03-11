public class EventData {
		public String userID;
		public String eventID;
		public String fecha;
		public String descripcion;
			//Mapeamos cada evento a su correspondiente marca/categoria no deseada;
		public int[] marcas;
		public int[] categorias;
		public int diasAviso;
		public int regaloConcreto;
		
		//Para hacer un evento con todos los datos vacios...
		public EventData(String userID){
			this.userID = userID;
		}
			
		//Para hacer un evento con los datos fecha i descripcion usado para saber cual quieres modificar, con el eventID añadido para poder buscarlo facil si se necesita
		public EventData(String userID, String eventID, String fecha, String descripcion){
			this.userID = userID; 
			this.eventID = eventID;
			this.fecha = fecha;
			this.descripcion = descripcion;
		}
		
		public EventData(String userID, String eventID, String fecha, String descripcion, int[] categorias, int[] marcas, int diasAviso, int regaloConcreto ){
			this.userID = userID;
			this.eventID = eventID;
			this.marcas = marcas;
			this.categorias = categorias;
			this.fecha = fecha;
			this.descripcion = descripcion;
			this.diasAviso = diasAviso;
			this.regaloConcreto = regaloConcreto;
		}
			
			/*Metodo para visualizar el contenido de un EventData en consola, usado para debug mostly*/
		public void toConsole(){ 
			System.out.println("----------------Evento--------------------");
			System.out.println("ID EVENTO: " + eventID + " , ID USUARIO: " + userID + ", fecha: " + fecha);
			System.out.println("Descripcion: " + descripcion + ", dias de aviso: " + diasAviso + ", regalo concreto? " + regaloConcreto);
			System.out.println("CATEGORIAS ELEGIDAS: " + Arrays.toString(categorias) +"; MARCAS ELEGIDAS: " + Arrays.toString(marcas) );
			System.out.println("----------------------------------------------");

		}
			
		//Usado en el metodo toConsole para ver los elementos de las arrays seguidos
		/*public String listMyArray(ArrayList<String> array){
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
		}*/
}
