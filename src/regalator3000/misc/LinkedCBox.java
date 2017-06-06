package regalator3000.misc;

import java.util.ArrayList;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/*Clase con metodos para crear una serie de comboboxes k todas comparten una array que se ira modificando, y con funciones para
 * controlar que se comporten asi.
 * por ahora cuando se elige un elemento  * esa combobox puede elegir ahora solo entre ese elemento y el elemento vacio (Default: Ninguna (porque es categoria))
 * mientras que las demas comboboxes tendran todos los elementos menos los elegidos en otras boxes. Al volver a elegir el elemento vacio se volvera
 * a poner el antiguo elemento elegido en la lista en segunda posicion (primera posicion sera siempre elemento vacio)
 * 
 *  
 *  
 *  
 *  NOTA: El codigo esta un poco liado por eso dejare los textos de debug i tal, luego lo mejoro cuando tenga tiempo por ahora chuta
 *  TANTO ESTE CODIGO COMO EL DE Proposal_GUI Habria que revisarse para ver si se puede hacer con menos variables y funciones 
 *  NOTA 2: En un futuro modificar para que la combobox con algo elegido no tenga solo el elemento y ninguna sino los elementos de
 *  la lista actual junto con el suyo (creo que no seria demasiado dificil*/
@SuppressWarnings("serial")
public class LinkedCBox<T> extends JComboBox<T>{

	/**
	 * 
	 */
	
	private static final String EMPTY_ITEM_STRING = "Ninguna";

	private static ArrayList<String> mainArrayList = new ArrayList<String>(); //Created here too as an extra security layer against someone not using initArray;
	private static String[] mainArray; //Podriamos hacerla variable local
	private static String[] initialPositionArray; //Keeps the original position of the elements so as to maintain the relationship with their DB indexes
	private String[] selectedItemArray = {EMPTY_ITEM_STRING,""};	
	private int oldIndex; //Keeps the old selected item index
	private String oldItem;
	boolean shownElement; //flag for when an item is chosen (that it is not "none" (ninguna)), it flags so we dont update its element list with the other boxes list
	
	/*Default constructor, creates a LinkedCBox as a default combobox with the defined item model*/
	public LinkedCBox(DefaultComboBoxModel<T> dcbm){
		super(dcbm);
	}
	
	/*This function initializes the arrays of the class with the data from the database, presumably*/
	public static void initArray(String[] initialArray){
			mainArrayList = new ArrayList<String>();
			for (int i = 0; i < initialArray.length; i++){
				if (initialArray[i] != null){
					mainArrayList.add(initialArray[i]);
				}
			mainArray = initialArray;
			initialPositionArray = initialArray;
		}
	}
	
	/*Elimina de la arraylist con los elementos el elemento que se corresponde al indice de la array original
	 * (Usado desde el programa principal en conjuncion con fillBox... i updateMainArray()	 */
	public static void removeFromModel(int elementPos){
		String element = initialPositionArray[elementPos];
		mainArrayList.remove(element); 
	}
	
	public static void updateMainArray(){
		mainArray = AuxFunctions.arrayListToArray(mainArrayList);
	}
	
	/*Function to create LinkedCBoxes with their starting attributes*/
	public static LinkedCBox<String> createStrCBox (){
		if (mainArray != null){
		    DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<>(mainArray); 
		    LinkedCBox<String> newCBox = new LinkedCBox<String>(dcbm); 
		    newCBox.oldIndex = 0;
		    newCBox.shownElement = false;
		    return newCBox;
		}
		return null;
	}
	
	/*Method called from the LinkedCBoxes when their actionListener fires (so, a new element has been chosen
	 * if we are in an empty element (index 0) and we choose a non empty element itll change to that element and update
	 * the element list in the other boxes as to not have the chosen element.
	 * if we are in a chosen element we can only then choose the empty element (or choose again the chosen element
	 * and nothing will happen) and then the chosen element will be put back in the list for all boxes and the
	 * current box will also get that list again
	 * 
	 * This method should be defined in the class creating the LinkedCBox as to set the boxes array correctly
	 */
	public static void choseItemInBoxes(LinkedCBox<String>[] boxes, LinkedCBox<String> chosenBox){
		int index = chosenBox.getSelectedIndex(); //new Selected index
		String item = (String)chosenBox.getSelectedItem();
		if (chosenBox.oldIndex == 0){ //Coming from a 0 index...
			if (index > 0){ //We choose a non 0 index...
				chosenBox.shownElement = true;
				chosenBox.selectedItemArray[1] = item; //Set the new element array with (Ninguna, chosenElement) only
				chosenBox.oldIndex = index; //Store the old index of the element in the main array
				chosenBox.oldItem = item;
				mainArrayList.remove(item); //Remove the chosenElement from the main array then update the other CBoxes with that array
				updateMainArray();
				updateModel(boxes);
			    DefaultComboBoxModel<String> dcbm = new DefaultComboBoxModel<>(chosenBox.selectedItemArray);
				chosenBox.setModel(dcbm);	//Set this box with his new element array with (ninguna, chosenElement)
				chosenBox.setSelectedIndex(1);
			}
		}
		else {  //We come from a non 0 index...
			if (index == 0){ //If we choose the 0 index now, need to add the element in the selecteditemarray to the main array then update all boxes with that array
				chosenBox.shownElement = false;
				item = chosenBox.selectedItemArray[1];
				chosenBox.selectedItemArray[1] = "";
				mainArrayList.add(1,item); //Sin respetar el orden, solo poniendo como segundo elemento siempre
				chosenBox.oldIndex = 0;
				chosenBox.oldItem = "Ninguna";
				updateMainArray();
				updateModel(boxes);
			}
		}
	}
	
	public void fillCBoxFromMain(int elementPos){
		String element = initialPositionArray[elementPos];
		this.shownElement = true;
		this.selectedItemArray[1] = element; //Set the new element array with (Ninguna, chosenElement) only
		this.oldIndex = mainArrayList.indexOf(element);
		this.oldItem = element;
	}
	
	/*Usar esto en vez de getSelectedIndex para ver que elemento ha elegido el usuario en vez
	 * de getSelectedIndex, esto es debido a que se pierde el orden al ir modificando
	 * las listas	 */
	public int getChosenIndex(){
		for (int i = 0; i < initialPositionArray.length; i++){
			if (initialPositionArray[i].equals(this.oldItem)){
				return i;
			}
		}
		return 0; //return -1 since Ninguna is also checked?
	}
	
	public String[] getSelectedItemArray(){
		return this.selectedItemArray;
	}
	/*Method to update the itemmodels for all boxes except those with a chosen element*/
	public static void updateModel(LinkedCBox<String>[] boxes){
	    DefaultComboBoxModel<String> dcbm;
		for (int i = 0; i < boxes.length; i++){
			if (!(boxes[i].shownElement)){
				dcbm = new DefaultComboBoxModel<>(mainArray); //Nota: recreamos el modelo para cada box porque sino aparece comportamiento inesperado (aprietas la 8 
				boxes[i].setEnabled(false);					  //Y llama a la 2 primero y luego a la 8, cosas asi, leer docu de defaultcomboboxmodel y jcombobox pa saber pq
				boxes[i].setModel(dcbm);
				boxes[i].setEnabled(true);
			}
		}
	}

}
