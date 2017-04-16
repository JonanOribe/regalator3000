package regalator3000.gui;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import regalator3000.db.DatabaseHandler;
import regalator3000.db.EventoControl;
import regalator3000.db.RegalosControl;
import regalator3000.misc.EventData;

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
	
	private static int goingToButton = -1;
	
	/*Crea un Dialog para que el usuario introduzca User y Password y te los devuelve como una array
	  de String de 2 elementos, cogido y modificado de stack overflow	 */
	public static String[] createUserPwdDialog(JFrame frame, int tipo) {
		//tipo = 0; login user; tipo = 1; agregar user; tipo = 2 borrar user;
	    String[] logininformation = new String[3];

	    JPanel panel = new JPanel(new BorderLayout(5, 5));

	    JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
	    JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
	    label.add(new JLabel("Usuario", SwingConstants.RIGHT));
	    JTextField username = new JTextField();
	    controls.add(username);
	    JTextField mail = null;
	    if (tipo != 0) {
	    	label.add(new JLabel("Mail", SwingConstants.RIGHT));
	    	mail = new JTextField();
	    	controls.add(mail);
	    }
	    label.add(new JLabel("Password", SwingConstants.RIGHT));
	    panel.add(label, BorderLayout.WEST);
	    JPasswordField password = new JPasswordField();
	    controls.add(password);
	    panel.add(controls, BorderLayout.CENTER);
	    String message;
	    if(tipo < 2){
	    	message = "Introduce tus datos";
	    }
	    else{
	    	message = "Introduce los datos del usuario a borrar";
	    }
        UIManager.put("OptionPane.noButtonText", "Cancelar");
	    JOptionPane.showConfirmDialog(frame, panel,message,JOptionPane.YES_NO_OPTION);
        UIManager.put("OptionPane.noButtonText", "No");

	    try {
	    logininformation[0] = username.getText();
	    logininformation[1] = new String(password.getPassword());
		    if (tipo != 0){
		    	logininformation[2] = mail.getText();
		    }
	    }
	    catch(Exception e){
	    	System.out.println("Error en la introduccion de datos. " + e.getMessage());
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
			botonesVer[i] = new NumberedJButton("Ir a", i, eventos);
			botonesVer[i].addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                irAEvento(evt);
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
		return goingToButton;
	}
	
	/*Cuando el usuario apreta en un boton de ver, el boton recuerda por su atributo posicion
	 * en que posicion de la array de eventos esta (por tanto, de que evento pedir la informacion).
	 * A partir de aqui, crea proposal_GUI, lee los datos del evento concreto mirando los que tiene 
	 * en su array y usando EventoControl.getEventData para la resta de datos del evento.
	 * Entonces le pasa los datos a proposal_GUI y le dice que no deje modificarlos con las dos
	 * funciones displayEventData(evento) y freezeAllInput().
	 * Una vez tienes proposal_GUI preparada, crea un JOptionPane que incluira a proposal_GUI y un boton que te permite volver atrás	 */
	private static void irAEvento(java.awt.event.ActionEvent evt){
		NumberedJButton src = (NumberedJButton)evt.getSource();
		goingToButton = src.position;
		JDialog mainPanel = (JDialog)src.getTopLevelAncestor();
		mainPanel.dispose();
	}
	
	/*Igual que el boton ver para saber a que evento se refiere, excepto que te abre un dialogo
	 * confirmatorio de si lo quieres borrar y si lo haces llama a las funciones de UsuarioControl
	 * que se encargan de eso	 */
	private static void borrarEvento(java.awt.event.ActionEvent evt){
		NumberedJButton src = (NumberedJButton)evt.getSource();
		int dialogResult = JOptionPane.showConfirmDialog (null, "Estas seguro de que quieres borrar este evento?","ALERTA",JOptionPane.YES_NO_OPTION);
		if (dialogResult == JOptionPane.YES_OPTION){
			JDialog programWindow = (JDialog)src.getTopLevelAncestor();
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
		String userID = NumberedJButton.eventos.get(src.position).userID;
		String eventoID = NumberedJButton.eventos.get(src.position).eventID;
		DatabaseHandler DbConnector = new DatabaseHandler();
		DbConnector.setUserID(Integer.parseInt(userID));
		Proposal_GUI ProposalGUIPanel = new Proposal_GUI(DbConnector);
		EventData eventoElegido = EventoControl.getEventData(DbConnector, Integer.parseInt(eventoID));
		ProposalGUIPanel.displayEventData(eventoElegido);
		JPanel contenidos = (JPanel)ProposalGUIPanel.getContentPane();
        UIManager.put("OptionPane.yesButtonText", "Modificar evento");
        UIManager.put("OptionPane.noButtonText", "Cancelar");
		int dialogResult = JOptionPane.showConfirmDialog (new JFrame("test"), contenidos,"Modificar evento",JOptionPane.YES_NO_OPTION);
		if (dialogResult == JOptionPane.YES_OPTION){
			EventData eventoCambiado =  ProposalGUIPanel.getNewEventData();
			if (ProposalGUIPanel.canCreateEvent(eventoCambiado) || eventoCambiado.fecha.equals(eventoElegido.fecha)){ //Si sobreescribimos los datos de un evento existente que no sea el original...
			eventoCambiado.userID = userID;
			eventoCambiado.eventID = eventoID;
			//eventoCambiado.toConsole();
			EventoControl.modifyEvent(DbConnector, eventoCambiado);
	        UIManager.put("OptionPane.yesButtonText", "Aceptar");
			RegalosControl.checkForPresents(DbConnector, EventoControl.getEvents(DbConnector)); //Comprueba si toca avisar de algun evento por si el que ha agregado toca...
					//Modifca evento con los nuevos datos
			JDialog programWindow = (JDialog)src.getTopLevelAncestor();
			NumberedJButton.eventos = EventoControl.getEvents(DbConnector);
			programWindow.dispose();
			createElegirVerEventoDialog(new JFrame(), NumberedJButton.eventos); 
			}
			else {
		        JOptionPane.showOptionDialog(new JFrame("test"), "    Ya hay otro evento en esa fecha","Error", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.CLOSED_OPTION, null, new Object[]{"Atrás"}, null);
			}
		}
        UIManager.put("OptionPane.yesButtonText", "Sí");
        UIManager.put("OptionPane.noButtonText", "No");
	}
	
	public static String createGoToDialog(){
			JPanel thisPanel = new JPanel();
			JLabel descLabel = new JLabel("Introduce la fecha:");
			JLabel desc2Label = new JLabel("La fecha debe ser del tipo aaaa-mm-dd (ej: 2017-04-13)");
			JFormattedTextField fechaInput = new JFormattedTextField();
	        fechaInput = new javax.swing.JFormattedTextField((new SimpleDateFormat("yyyy-MM-dd")));
	        fechaInput.setText(LocalDate.now().toString());

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(thisPanel);
	        thisPanel.setLayout(layout);
	        
	        layout.setHorizontalGroup(
	                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                .addGroup(layout.createSequentialGroup()
	                    .addContainerGap()
	                    .addComponent(descLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                        .addComponent(fechaInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                        .addComponent(desc2Label, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                    .addContainerGap(15, Short.MAX_VALUE))
	            );
	            layout.setVerticalGroup(
	                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                .addGroup(layout.createSequentialGroup()
	                    .addComponent(desc2Label, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
	                        .addComponent(descLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                        .addComponent(fechaInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                    .addContainerGap(20,30))
	            );

	        UIManager.put("OptionPane.yesButtonText", "Ir a");
	        UIManager.put("OptionPane.noButtonText", "Cancelar");
		    int result = JOptionPane.showConfirmDialog(new JFrame(""), thisPanel,"Ir a fecha concreta",JOptionPane.YES_NO_OPTION);
		    UIManager.put("OptionPane.yesButtonText", "Yes");
		    UIManager.put("OptionPane.noButtonText", "No");
		    String newDate ="";
		    if (result == 0){
			    try {
				    newDate = fechaInput.getText();
				    }
				    catch(Exception e){
				    }
		    }
			return newDate;       
	}
	/*Funcion auxiliar para ver que radioButton es el elegido (NO USADA)*/
	@SuppressWarnings("unused")
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
