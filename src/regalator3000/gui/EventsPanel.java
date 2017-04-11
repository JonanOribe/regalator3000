/* FECHA 11/04: FUTURO Agregar boton de agregar al lado del listado, que te abra proposal_GUI como harias desde agregar eventos a pelo UNA VEZ CAMBIADA PROPOSAL GUI. 
 * AGREGAR A LOS TRES BOTONES DE LISTADO UNO (IR) QUE TE LLEVA A LA FECHA? (Penyazo de hacer, hay que añadir referencias a dialogGenerator...*/
package regalator3000.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
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
import regalator3000.misc.EventData;

@SuppressWarnings("serial")
public class EventsPanel extends JPanel implements ActionListener{

	private ArrayList<EventData> userEvents;
	private DatabaseHandler DbConnector;
	private JLabel dateLabel, descLabel;
	private JButton seeButton,modButton,delButton;
	private int actualMonth;
	private int actualYear;
	private EventData actualEvent;

	
	public EventsPanel(DatabaseHandler DbConnector){
		this.DbConnector = DbConnector;
		actualMonth = getFieldFromDate(LocalDate.now().toString(),1)-1;
		actualYear = getFieldFromDate(LocalDate.now().toString(),0);
		initcomponents();
	}
	
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
		allButton.addActionListener(this);
		southBottomGrid.add(new JLabel(" ")); //Dummy spacing labels (wasnt in the mood to create a more complex layout
		southBottomGrid.add(allButton);
		southBottomGrid.add(new JLabel(" "));
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
	
	
	//Falta darle el userID al construir el panel...
	public void actionPerformed(ActionEvent evt){
		String command = evt.getActionCommand();
	    Object src = evt.getSource();
	    CalendarButton eventButton = new CalendarButton("",-1);
		if (command != null){
			if (command.equals("Listado eventos")){
				DialogGenerator.createElegirVerEventoDialog(new JFrame("Eventos"), EventoControl.getEvents(DbConnector));
				//Nota se podria llamar a getEvents y comprobar si == eventoActual y entonces reiniciar pero que prefieres, llamada a DDBB obligada o restart GUI obligado...?
				restartWindow();
			}
			else if (command.equals("Ver")){
				Proposal_GUI ProposalGUIPanel = new Proposal_GUI();
				ProposalGUIPanel.displayEventData(actualEvent);
				ProposalGUIPanel.freezeAllInput();
				JPanel contenidos = (JPanel)ProposalGUIPanel.getContentPane();
		        JOptionPane.showOptionDialog(new JFrame("test"), contenidos,"Ver detalles evento", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.CLOSED_OPTION, null, new Object[]{"Atrás"}, null);	
			}
			else if (command.equals("Modificar")){
				Proposal_GUI ProposalGUIPanel = new Proposal_GUI();
				ProposalGUIPanel.displayEventData(actualEvent);
				JPanel contenidos = (JPanel)ProposalGUIPanel.getContentPane();
		        UIManager.put("OptionPane.yesButtonText", "Modificar evento");
		        UIManager.put("OptionPane.noButtonText", "Cancelar");
				int dialogResult = JOptionPane.showConfirmDialog (new JFrame("test"), contenidos,"Modificar evento",JOptionPane.YES_NO_OPTION);
				if (dialogResult == JOptionPane.YES_OPTION){
					EventData eventoCambiado =  ProposalGUIPanel.getNewEventData();
					eventoCambiado.eventID = actualEvent.eventID;
					eventoCambiado.userID = actualEvent.userID;
					EventoControl.modifyEvent(DbConnector, eventoCambiado);
					actualMonth = getFieldFromDate(actualEvent.fecha,1)-1; //Nos quedamos en la fecha que estabamos no vamos a la del nuevo evento
					actualYear = getFieldFromDate(actualEvent.fecha,0); 
					actualEvent = eventoCambiado;
					restartWindow();
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
			else if (src.getClass().equals(eventButton.getClass())){ //El user ha clickado en un dia con evento...
				eventButton = (CalendarButton)src;
				int selectedEventID = eventButton.getEventID();
				actualEvent = EventoControl.getEventData(DbConnector,selectedEventID);
				setButtonsInvis(false);
				descLabel.setText("Descripcion: " + actualEvent.descripcion);
				dateLabel.setText("Fecha: " + actualEvent.fecha);
			}
			else { //Tiene que ser la ultima frase esta...
				actualEvent = null;
				setButtonsInvis(true);
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
			dateLabel.setText(" ");
		}
		else {
			seeButton.setVisible(true);
			modButton.setVisible(true);
			delButton.setVisible(true);
		}
	}
	/*0 -> year; 1 -> month; 2 -> day*/
	private int getFieldFromDate(String dateText,int timeField){
		if (timeField >=0 && timeField <= 2){
			char splitter = dateText.charAt(4); //El char despres de 2017X04...
			String[] valoresData =  dateText.split(Character.toString(splitter));
			return Integer.parseInt(valoresData[timeField]);
		}
		return -1;
	}
	public static void main(String[] args){
		/*DatabaseHandler DbConnector = new DatabaseHandler(); //En teoria estara creado fuera de tests...
		DbConnector.setUserID(1);
        JFrame window = new JFrame("events test");
        EventsPanel content = new EventsPanel(DbConnector);
        window.setContentPane(content);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setPreferredSize(new Dimension(screenSize.width/3,screenSize.height/3)); //!improve this for weird resolutions, or just other types of resolutions
        window.setLocation(200, 200);
        window.setVisible(true);
        window.pack();
        window.setSize(500,400);*/

	}
}
