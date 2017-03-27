package regalator3000.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import regalator3000.aux.EventData;
import regalator3000.db.DatabaseHandler;
import regalator3000.db.EventoControl;

public class DialogGenerator {
		
	/*Clase especial de JButtons que contiene informacion extra para cuando son clickados*/
	@SuppressWarnings("serial")
	private static class NumberedJButton extends JButton{
		private int position; //Una variable para cada boton que determina en que posicion de la array de eventos estan (vamos, que a que evento pertenece el boton)
		private static ArrayList<EventData> eventos = new ArrayList<EventData>(); //static -> una sola variable para todas las instancias de los botones, guarda la lista de eventos del usuario
																				//se usa para que los botones puedan ejecutar sus funciones en los eventos que les corresponden
		
		public NumberedJButton(String name,int i, ArrayList<EventData> inputEventos){
			super(name);
			this.position = i;
			if(inputEventos.size() > 0){
				eventos = inputEventos; //Esto es cutre pero solo asigna el puntero multiples veces, sino comprobar con .containsAll(ArrayList2<>)  
			}
		}
	}
	
	/*Crea un Dialog para que el usuario introduzca User y Password y te los devuelve como una array
	  de String de 2 elementos	 */
	public static String[] createUserPwdDialog(JFrame frame) {
	    String[] logininformation = new String[2];

	    JPanel panel = new JPanel(new BorderLayout(5, 5));

	    JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
	    label.add(new JLabel("Usuario", SwingConstants.RIGHT));
	    label.add(new JLabel("Password", SwingConstants.RIGHT));
	    panel.add(label, BorderLayout.WEST);
	    JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
	    JTextField username = new JTextField();
	    controls.add(username);
	    JPasswordField password = new JPasswordField();
	    controls.add(password);
	    panel.add(controls, BorderLayout.CENTER);

	    JOptionPane.showMessageDialog(frame, panel);
	    
	    try {
	    logininformation[0] = username.getText();
	    logininformation[1] = new String(password.getPassword());
	    }
	    catch(Exception e){
	    }
	    return logininformation;
	}
	
	/*Crea un Dialog para que el usuario vea los eventos que tiene. Devuelve el numero de evento seleccionado
	 * (el que esta en posicion i de la array de eventdata que se introduce como input.
	 * Te crea botones para ver, modificar y eliminar estos eventos (los jRadioButton no se usan
	 * en realidad pero queda bonito y lo dejo)*/
	public static int createElegirVerEventoDialog(JFrame frame, ArrayList<EventData> eventos){
		if (eventos.size() == 0) { return -1;}
		JPanel panel = new JPanel(new BorderLayout(5,5));
		
		JRadioButton[] botones = new JRadioButton[eventos.size()];
		JPanel listadoEventos = new JPanel(new GridLayout(eventos.size(),1,5,5)); //Pasar a GroupLayout seguramente	
		JPanel listadoBotones = new JPanel(new GridLayout(eventos.size(),3,5,5));
		NumberedJButton[] botonesVer = new NumberedJButton[eventos.size()];
		NumberedJButton[] botonesModificar = new NumberedJButton[eventos.size()];
		NumberedJButton[] botonesBorrar = new NumberedJButton[eventos.size()];
		ButtonGroup eleccion = new ButtonGroup();
		boolean first = true;
		for (int i = 0; i < eventos.size(); i++) {
			if (i == 1) { 
				first = false;
			}
			botones[i] = new JRadioButton(eventos.get(i).fecha +" // " + eventos.get(i).descripcion,first);
			botonesVer[i] = new NumberedJButton("Ver", i, eventos);
			botonesVer[i].addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                verEvento(evt);
	            }
	        });
			botonesModificar[i] = new NumberedJButton("Modificar", i, eventos);
			botonesModificar[i].addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                modificarEvento(evt);
	            }
	        });
			botonesBorrar[i] = new NumberedJButton("Eliminar", i, eventos);
			botonesBorrar[i].addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                borrarEvento(evt);
	            }
	        });
			listadoEventos.add(botones[i]);
			listadoBotones.add(botonesVer[i]);
			listadoBotones.add(botonesModificar[i]);
			listadoBotones.add(botonesBorrar[i]);
			eleccion.add(botones[i]);
			eleccion.add(botonesVer[i]);
			eleccion.add(botonesModificar[i]);
			eleccion.add(botonesBorrar[i]);
		}
		panel.add(listadoEventos, BorderLayout.CENTER);
		panel.add(listadoBotones, BorderLayout.EAST);
		JOptionPane.showOptionDialog(frame, panel,"Eventos actuales", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.CLOSED_OPTION, null, new Object[]{"Atrás"}, null);
		return whatButtonIsPressed(botones); //Retorna cual de los eventos ha elegido (en el array de eventos). NO USADO DESDE EL CAMBIO A USAR LOS 3 BOTONES 
	}
	
	/*Cuando el usuario apreta en un boton de ver, el boton recuerda por su atributo posicion
	 * en que posicion de la array de eventos esta (por tanto, de que evento pedir la informacion).
	 * A partir de aqui, crea proposal_GUI, lee los datos del evento concreto mirando los que tiene 
	 * en su array y usando EventoControl.getEventData para la resta de datos del evento.
	 * Entonces le pasa los datos a proposal_GUI y le dice que no deje modificarlos con las dos
	 * funciones displayEventData(evento) y freezeAllInput().
	 * Una vez tienes proposal_GUI preparada, crea un JOptionPane que incluira a proposal_GUI y un boton que te permite volver atrás	 */
	private static void verEvento(java.awt.event.ActionEvent evt){
		NumberedJButton src = (NumberedJButton)evt.getSource();
		Proposal_GUI ProposalGUIPanel = new Proposal_GUI();
		String userID = NumberedJButton.eventos.get(src.position).userID;
		String eventoID = NumberedJButton.eventos.get(src.position).eventID;
		DatabaseHandler DbConnector = new DatabaseHandler();
		DbConnector.setUserID(Integer.parseInt(userID));
		EventData eventoElegido = EventoControl.getEventData(DbConnector, Integer.parseInt(eventoID));
		ProposalGUIPanel.displayEventData(eventoElegido);
		ProposalGUIPanel.freezeAllInput();
		JPanel contenidos = (JPanel)ProposalGUIPanel.getContentPane();
        JOptionPane.showOptionDialog(new JFrame("test"), contenidos,"Ver detalles evento", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.CLOSED_OPTION, null, new Object[]{"Atrás"}, null);
	}
	
	/*Igual que el boton ver para saber a que evento se refiere, excepto que te abre un dialogo
	 * confirmatorio de si lo quieres borrar y si lo haces llama a las funciones de UsuarioControl
	 * que se encargan de eso	 */
	private static void borrarEvento(java.awt.event.ActionEvent evt){
		NumberedJButton src = (NumberedJButton)evt.getSource();
		JDialog programWindow = (JDialog)src.getTopLevelAncestor();
		int dialogResult = JOptionPane.showConfirmDialog (null, "Estas seguro de que quieres borrar este evento?","ALERTA",JOptionPane.YES_NO_OPTION);
		if (dialogResult == JOptionPane.YES_OPTION){
			DatabaseHandler DbConnector = new DatabaseHandler();
			DbConnector.setUserID(Integer.parseInt(NumberedJButton.eventos.get(src.position).userID)); // Esto es un poco cutre............
			EventoControl.removeEvent(DbConnector, Integer.parseInt(NumberedJButton.eventos.get(src.position).eventID)); //FUTURO: Llamar primero a un dialogo : ESTAS SEGURO? Y si confirma llamar BBDD y hacerlo
			NumberedJButton.eventos = EventoControl.getEvents(DbConnector);
			programWindow.dispose();
			createElegirVerEventoDialog(new JFrame(), NumberedJButton.eventos); 
		}
		return;
	}
	
	/* Como verEvento para abrir la ventana del evento pero ahora usa un JOptionPane con dos botones
	 * (agregar evento y cancelar) que te mira si el usuario apreta agregar evento y si lo hace
	 * mira los nuevos datos contenidos en proposal_GUI y usa EventoControl.modifyEvent para
	 * cambiar los datos del evento en la BBDD	 */
	public static void modificarEvento(java.awt.event.ActionEvent evt){
		NumberedJButton src = (NumberedJButton)evt.getSource();
		Proposal_GUI ProposalGUIPanel = new Proposal_GUI();
		String userID = NumberedJButton.eventos.get(src.position).userID;
		String eventoID = NumberedJButton.eventos.get(src.position).eventID;
		DatabaseHandler DbConnector = new DatabaseHandler();
		DbConnector.setUserID(Integer.parseInt(userID));
		EventData eventoElegido = EventoControl.getEventData(DbConnector, Integer.parseInt(eventoID));
		ProposalGUIPanel.displayEventData(eventoElegido);
		JPanel contenidos = (JPanel)ProposalGUIPanel.getContentPane();
        UIManager.put("OptionPane.yesButtonText", "Modificar evento");
        UIManager.put("OptionPane.noButtonText", "Cancelar");
		int dialogResult = JOptionPane.showConfirmDialog (new JFrame("test"), contenidos,"Modificar evento",JOptionPane.YES_NO_OPTION);
		if (dialogResult == JOptionPane.YES_OPTION){
			EventData eventoCambiado =  ProposalGUIPanel.getNewEventData();
			eventoCambiado.userID = userID;
			eventoCambiado.eventID = eventoID;
			//eventoCambiado.toConsole();
			EventoControl.modifyEvent(DbConnector, eventoCambiado);
					//Modifca evento con los nuevos datos
		}
        UIManager.put("OptionPane.yesButtonText", "Sí");
        UIManager.put("OptionPane.noButtonText", "No");
	}
	/*Funcion auxiliar para ver que boton es el presionado*/
	private static int whatButtonIsPressed(JRadioButton[] botones){
		for (int i = 0; i < botones.length; i++) {
			if (botones[i].isSelected()) {
				return i;
			}
		}
		return -1;
	}
	
	public static void main(String args[]) {
	}
                
}
