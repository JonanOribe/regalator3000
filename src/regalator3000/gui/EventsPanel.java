package regalator3000.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import regalator3000.db.DatabaseHandler;
import regalator3000.db.EventoControl;
import regalator3000.db.RegalosControl;
import regalator3000.misc.AuxFunctions;
import regalator3000.misc.EventData;

@SuppressWarnings("serial")
public class EventsPanel extends JPanel implements ActionListener{

	private ArrayList<EventData> userEvents;
	private DatabaseHandler DbConnector;
	private JLabel dateLabel, descLabel;
	private JButton seeButton,modButton,delButton,addButton;
	public int actualMonth;
	public int actualYear;
	private String selectedDate = LocalDate.now().toString(); 
	private EventData actualEvent;
	private static boolean addOrOpen = false;
	
	public EventsPanel(DatabaseHandler DbConnector){
		this.DbConnector = DbConnector;
		actualMonth = AuxFunctions.getFieldFromDate(LocalDate.now().toString(),1)-1;
		actualYear = AuxFunctions.getFieldFromDate(LocalDate.now().toString(),0);
		initcomponents();
	}
	
	/*Posibles cambios, cambiar estructura para usar getMonth/getYear de calendarGUI para dejar puesto el mes que toque o resetear*/
	public void initcomponents(){
		JPanel southPanel = new JPanel();
		userEvents = EventoControl.getEvents(DbConnector);
		CalendarPanel calendarGUI = new CalendarPanel(this, userEvents, actualMonth, actualYear);
		this.setLayout(new BorderLayout(5,5));//this.setLayout(new GridLayout(2,1,5,5));
		southPanel.setLayout(new BorderLayout(5,5));
		JPanel southCenterGrid = new JPanel();
		southCenterGrid.setLayout(new GridLayout(2,0,5,5));
		JPanel southTopGrid = new JPanel();
		southTopGrid.setLayout(new GridLayout(0,3,5,5));
		JPanel southBottomGrid = new JPanel();
		southBottomGrid.setLayout(new GridLayout(0,3,5,5));
		
	
		descLabel = new JLabel(" ", SwingConstants.CENTER);
		dateLabel = new JLabel(" ",SwingConstants.CENTER);
		seeButton = createInvisButton("Ver");
		modButton = createInvisButton("Modificar");
		delButton = createInvisButton("Eliminar");
		JButton allButton = new JButton("Listado eventos");
		JButton goToButton = new JButton("Ir a fecha");
		if (!addOrOpen){
			addButton = new JButton("Agregar evento");
		} else {
			addButton = new JButton("Abrir aviso");
		}
		allButton.addActionListener(this);
		addButton.addActionListener(this);
		goToButton.addActionListener(this);
		southBottomGrid.add(addButton);
		southBottomGrid.add(goToButton); //Dummy spacing labels (wasnt in the mood to create a more complex layout
		southBottomGrid.add(allButton);
		southCenterGrid.add(dateLabel);
		southCenterGrid.add(descLabel);
		southTopGrid.add(seeButton);
		southTopGrid.add(modButton);
		southTopGrid.add(delButton);	
		
		southPanel.add(southTopGrid,BorderLayout.NORTH);
		southPanel.add(southCenterGrid,BorderLayout.CENTER);
		southPanel.add(southBottomGrid,BorderLayout.SOUTH);
		
		this.add(calendarGUI,BorderLayout.CENTER);
		this.add(southPanel,BorderLayout.SOUTH);
				
	}
	
	private JButton createInvisButton(String name){
		JButton button = new JButton(name);
		button.setVisible(false);
		button.addActionListener(this);
		return button;
	}
	
	public void actionPerformed(ActionEvent evt){
		String command = evt.getActionCommand();
	    Object src = evt.getSource();
	    CalendarButton eventButton = new CalendarButton("",-1);
	    addOrOpen = false;
		if (command != null){
		    if (command.equals("Abrir aviso")) {
		    	RegalosControl.abreEventoConcreto(DbConnector, actualEvent);
		    }
		    else if (command.equals("Listado eventos")){
				int goTo = DialogGenerator.createElegirVerEventoDialog(new JFrame("Eventos"), EventoControl.getEvents(DbConnector));
				//Nota se podria llamar a getEvents y comprobar si == eventoActual y entonces reiniciar pero que prefieres, llamada a DDBB obligada o restart GUI obligado...?
				if (goTo > -1){
					actualMonth = AuxFunctions.getFieldFromDate(userEvents.get(goTo).fecha,1)-1;
					actualYear = AuxFunctions.getFieldFromDate(userEvents.get(goTo).fecha,0);
					restartWindow();
					actualEvent = userEvents.get(goTo);
					setButtonsInvis(false);
					descLabel.setText("Descripcion: " + actualEvent.descripcion);
					selectedDate = actualEvent.fecha;
					dateLabel.setText("Fecha: " + selectedDate);
				}
				else {
				restartWindow();
				}
			}
			else if (command.equals("Agregar evento")){
				Proposal_GUI ventana = new Proposal_GUI(DbConnector); //Crea una nueva Proposal_GUI (es una JFrame, con esta instancia veo los cambios que ocurren despues de llamar a la ventanita
				ventana.goToDate(selectedDate);
				JPanel ProposalGUIPanel = (JPanel)ventana.getContentPane(); //Como Proposal_GUI es una JFrame obtengo lo de dentro asi para ponerlo en el dialogo
	            UIManager.put("OptionPane.yesButtonText", "Agregar evento");
	            UIManager.put("OptionPane.noButtonText", "Cancelar");
				int dialogResult = JOptionPane.showConfirmDialog (new JFrame("test"), ProposalGUIPanel,"Agregar evento",JOptionPane.YES_NO_OPTION); //Lanzo un dialogo con el contenido de Proposal_GUI, cuyos cambios afectaran a la instancia de proposalGUI que tengo
	            UIManager.put("OptionPane.yesButtonText", "Aceptar");
	            UIManager.put("OptionPane.noButtonText", "No");
	            if (dialogResult == JOptionPane.YES_OPTION){ //Si el usuario quiere agregar el evento introducido...
					//HACER COMPROVACIONES AQUI DE QUE TODOS LOS DATOS INTRODUCIDOS SON CORRECTOS Y LEGALES, ya sea en el metodo de PropGUI o algun metodo en EventData que comprueba que todos los datos son legales...
	            	EventData eventoNuevo = ventana.getNewEventData(); //leo los datos introducidos en la ventana de proposal_gui...
					if (ventana.canCreateEvent(eventoNuevo)){
						eventoNuevo.userID = Integer.toString(DbConnector.getUserID()); //Cutre pero necesario
						EventoControl.addEvent(DbConnector, eventoNuevo);
						RegalosControl.checkForPresents(DbConnector, EventoControl.getEvents(DbConnector)); //Comprueba si toca avisar de algun evento por si el que ha agregado toca...
						restartWindow();
					}
					else {
				        JOptionPane.showOptionDialog(new JFrame("test"), "    Ya hay un evento en esa fecha","Error", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.CLOSED_OPTION, null, new Object[]{"Atras"}, null);
					}
	            }
			}
			else if (command.equals("Ver")){
				Proposal_GUI ProposalGUIPanel = new Proposal_GUI(DbConnector);
				ProposalGUIPanel.displayEventData(actualEvent);
				ProposalGUIPanel.freezeAllInput();
				JPanel contenidos = (JPanel)ProposalGUIPanel.getContentPane();
		        JOptionPane.showOptionDialog(new JFrame("test"), contenidos,"Ver detalles evento", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.CLOSED_OPTION, null, new Object[]{"Atras"}, null);	
			}
			else if (command.equals("Modificar")){
				Proposal_GUI ProposalGUIPanel = new Proposal_GUI(DbConnector);
				ProposalGUIPanel.goToDate(selectedDate);
				ProposalGUIPanel.displayEventData(actualEvent);
				JPanel contenidos = (JPanel)ProposalGUIPanel.getContentPane();
		        UIManager.put("OptionPane.yesButtonText", "Modificar evento");
		        UIManager.put("OptionPane.noButtonText", "Cancelar");
				int dialogResult = JOptionPane.showConfirmDialog (new JFrame("test"), contenidos,"Modificar evento",JOptionPane.YES_NO_OPTION);
				if (dialogResult == JOptionPane.YES_OPTION){
					EventData eventoCambiado =  ProposalGUIPanel.getNewEventData();
					if (ProposalGUIPanel.canCreateEvent(eventoCambiado) || eventoCambiado.fecha.equals(actualEvent.fecha)){ //Si sobreescribimos los datos de un evento existente que no sea el original...
						eventoCambiado.eventID = actualEvent.eventID;
						eventoCambiado.userID = actualEvent.userID;
				        UIManager.put("OptionPane.yesButtonText", "Aceptar");
						EventoControl.modifyEvent(DbConnector, eventoCambiado);
						RegalosControl.checkForPresents(DbConnector, EventoControl.getEvents(DbConnector)); //Comprueba si toca avisar de algun evento por si el que ha agregado toca...
						actualMonth = AuxFunctions.getFieldFromDate(actualEvent.fecha,1)-1; //Nos quedamos en la fecha que estabamos no vamos a la del nuevo evento
						actualYear = AuxFunctions.getFieldFromDate(actualEvent.fecha,0); 
						actualEvent = eventoCambiado;
						restartWindow();
					}
					else {
				        JOptionPane.showOptionDialog(new JFrame("test"), "    Ya hay otro evento en esa fecha","Error", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.CLOSED_OPTION, null, new Object[]{"Atrás"}, null);
					}
				}
		        UIManager.put("OptionPane.yesButtonText", "Sí");
		        UIManager.put("OptionPane.noButtonText", "No");
			}
			else if (command.equals("Eliminar")){
				int dialogResult = JOptionPane.showConfirmDialog (null, "Estas seguro de que quieres borrar este evento?","ALERTA",JOptionPane.YES_NO_OPTION);
				if (dialogResult == JOptionPane.YES_OPTION){
					EventoControl.removeEvent(DbConnector, Integer.parseInt(actualEvent.eventID)); 
					actualEvent = null;
					restartWindow();
				}
			}
			else if (command.equals("Ir a fecha")){ //go to a specific date, check for the separator after the year then... etc (gotta make the GUI ugh
				String newDate = DialogGenerator.createGoToDialog();
	            if (!newDate.equals("")){
	            	actualMonth = (AuxFunctions.getFieldFromDate(newDate, 1)-1);
	            	actualYear = AuxFunctions.getFieldFromDate(newDate, 0);
	            	actualEvent = null;
	            	restartWindow();
	            }
	        }
			else if (src.getClass().equals(eventButton.getClass())){ //El user ha clickado en un dia con evento...
				eventButton = (CalendarButton)src;
				int selectedEventID = eventButton.getEventID();
				actualEvent = EventoControl.getEventData(DbConnector,selectedEventID);
				addOrOpen = true;
				restartWindow();
				setButtonsInvis(false);
				descLabel.setText("Descripcion: " + actualEvent.descripcion);
				selectedDate = actualEvent.fecha;
				dateLabel.setText("Fecha: " + selectedDate);
			}
			else { //Tiene que ser la ultima frase esta...
				JButton botonPresionado = (JButton)src;
				selectedDate = AuxFunctions.formatDateFromValues(actualYear,(actualMonth+1), Integer.parseInt(botonPresionado.getText()),"-");
				dateLabel.setText("Fecha: " + selectedDate);
				actualEvent = null;
				setButtonsInvis(true);
				restartWindow();
			}
			//System.out.println(buttonsrc.getEventID());
		}
	}
	
	/*Function to restart the GUI f.ex when events get changed*/
	private void restartWindow() { 
		this.removeAll();
		this.initcomponents();
		this.revalidate();
	}
	
	private void setButtonsInvis(boolean setEm){
		if (setEm){
			seeButton.setVisible(false);
			modButton.setVisible(false);
			delButton.setVisible(false);
			descLabel.setText(" ");
		}
		else {
			seeButton.setVisible(true);
			modButton.setVisible(true);
			delButton.setVisible(true);
		}
	}

	public static void main(String[] args){
		DatabaseHandler DbConnector = new DatabaseHandler(); //En teoria estara creado fuera de tests...
		DbConnector.setUserID(1);
        JFrame window = new JFrame("events test");
        EventsPanel content = new EventsPanel(DbConnector);
        window.setContentPane(content);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setPreferredSize(new Dimension(screenSize.width/3,screenSize.height/3)); //!improve this for weird resolutions, or just other types of resolutions
        window.setLocation(200, 200);
        window.setVisible(true);
        window.pack();
        window.setSize(520,420);

	}
}
