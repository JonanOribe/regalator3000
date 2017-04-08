package regalator3000;
/*Añadir creditos, Añadir mas datos en la BBDD, añadir extra campos para: imagenes que renderear de los productos, mail de los usuarios, paginas web para los productos que el usuario pueda clickar etc*/
/*Crear calendario grafico para que el usuario pueda clickar en un dia y que tenga colores diferentes dependiendo de si tiene evento ahi o no, con posibilidad de verlos? (FUTURO)*/
			
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;

import regalator3000.aux.EventData;
import regalator3000.db.DatabaseHandler;
import regalator3000.db.EventoControl;
import regalator3000.db.RegalosControl;
import regalator3000.db.UserControl;
import regalator3000.gui.DialogGenerator;
import regalator3000.gui.DialogV2;
import regalator3000.gui.Proposal_GUI;



/*Clase Principal del programa, llamarla para generar la GUI y comenzar 
 * el proceso de controlar el dia actual y cuando pasa cada dia*/
@SuppressWarnings("serial")
public class MainGUI extends JPanel implements ActionListener{
	
	private JButton Button1,Button2,Button3; //usarl array de JButtons?
	private JLabel LabelMes,LabelAnyo,LabelDia,LabelDiaNombre,LabelLogged; //Contiene y enseña el dia/mes/año actual (no usar JLabel, currarse algo del palo dibujar un numero bonito o usar mas de una Label con fonts wapas para k kede bonito
	private DatabaseHandler DbConnector = new DatabaseHandler(); //instancia de DatabaseHandler que controlara las conexiones con la BBDD
	private Timer dayTimer;
	//Main constructor(add parameters?)
	public MainGUI(){
            setupMainPanel();
	}

	
	public void setupMainPanel() { 
		this.setLayout(new BorderLayout(10,10));		
		JPanel southPanel = new JPanel();
		JPanel southPanel2 = new JPanel();
		southPanel2.setLayout(new GridLayout(2,1,5,5));
		southPanel.setLayout(new GridLayout(1,3,5,5));
		Button1 = new JButton("Agregar evento");
		Button2 = new JButton("Tus eventos");
		Button3 = new JButton("Login");
        LabelLogged = new JLabel("Usuario no conectado");
        LabelLogged.setHorizontalAlignment(SwingConstants.CENTER);
		Button1.addActionListener(this);
		Button2.addActionListener(this);
		Button3.addActionListener(this);
		southPanel.add(Button1);
		southPanel.add(Button2);
		southPanel.add(Button3);
		southPanel2.add(southPanel);
        southPanel2.add(LabelLogged);
		String[] todayValues = LocalDate.now().toString().split("-");
		String dayOfWeek = LocalDate.now().getDayOfWeek().toString().toLowerCase();
        dayOfWeek = capitalizeFirst(dayOfWeek);
		DateFormatSymbols dfs = new DateFormatSymbols();
		String[] monthNames = dfs.getMonths();
		LabelMes = new JLabel(monthNames[Integer.parseInt(todayValues[1])]);
		LabelMes.setFont(new Font("TimesRoman",Font.BOLD,24));
                LabelMes.setHorizontalAlignment(SwingConstants.CENTER);
		LabelDia = new JLabel(todayValues[2]);
                LabelDia.setFont(new Font("TimesRoman", Font.BOLD, 78));
                LabelDia.setForeground(Color.red);
                LabelDia.setHorizontalAlignment(SwingConstants.CENTER);
                LabelDia.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE, 4),BorderFactory.createLineBorder(Color.BLACK, 4)));
		LabelAnyo = new JLabel(todayValues[0]);
		LabelAnyo.setFont(new Font("TimesRoman",Font.BOLD,28));
                LabelAnyo.setHorizontalAlignment(SwingConstants.CENTER);
                LabelDiaNombre = new JLabel("(" + dayOfWeek + ")"); //Este calculo esta mal, cambiar
		LabelDiaNombre.setFont(new Font("TimesRoman",Font.PLAIN,24));
                LabelDiaNombre.setHorizontalAlignment(SwingConstants.CENTER);
                JPanel centralPanel = new JPanel();
		GroupLayout layout = new GroupLayout(centralPanel);
                centralPanel.setLayout(layout);
                layout.setAutoCreateGaps(true);
                layout.setAutoCreateContainerGaps(true);
		this.add(centralPanel, BorderLayout.CENTER);
		this.add(southPanel2,BorderLayout.SOUTH);
                layout.setHorizontalGroup(layout.createSequentialGroup()
                        .addComponent(LabelAnyo, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(LabelDia, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(LabelDiaNombre, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(LabelMes, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        );
                layout.setVerticalGroup(layout.createSequentialGroup()
                              .addComponent(LabelAnyo, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                              .addComponent(LabelDia, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                              .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                              .addComponent(LabelDiaNombre, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                              .addComponent(LabelMes, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                );
	}

		
		
		
	private static JMenuBar createMenuBar(ActionListener listener){ //creates the top menu
			
		JMenuBar Menu = new JMenuBar();
				
		//Pestaña opciones 
		JMenu AboutMenu = new JMenu("Opciones"); //Nombre pestaña
		JMenuItem CreditsButton = new JMenuItem("Nuevo usuario"); //Opcion 1, Implementar
		JMenuItem DeleteUserButton = new JMenuItem("Borrar usuario"); //Opcion 2, Implementar
		JMenuItem ExitButton2 = new JMenuItem("Exit"); //Para salir
			
		CreditsButton.addActionListener(listener);     //Pone el listener para que la clase que llama puede implementar interface actionPerformed i saber cuando clicka el user
		ExitButton2.addActionListener(listener);
		DeleteUserButton.addActionListener(listener);
		AboutMenu.add(CreditsButton);
		AboutMenu.addSeparator();
		AboutMenu.add(DeleteUserButton);
		AboutMenu.addSeparator();
		AboutMenu.add(ExitButton2);
		
                //Pestaña "Más"
		JMenu LoginMenu = new JMenu("Más");
		JMenuItem LogOnButton = new JMenuItem("Créditos");
		JMenuItem LogOutButton = new JMenuItem("Logout");

		LogOnButton.addActionListener(listener);			
		LogOutButton.addActionListener(listener);
		LoginMenu.add(LogOnButton);
		LoginMenu.addSeparator();
		LoginMenu.add(LogOutButton);
			
			
		Menu.add(AboutMenu);
		//Menu.add(PrefMenu);
		Menu.add(LoginMenu);
		return Menu;
	}
	
	/* Detecta los botones que se aprieten, tanto menu de arriba como los demás. 
	 * Tambien tiene en cuenta cada vez que el timer se activa(si evt == null pero entra en la funcion);*/
	public void actionPerformed(ActionEvent evt){
		String command = evt.getActionCommand();
		//Object src = evt.getSource();
		if (command == null) { //El timer se ha activado (despues del intervalo)(aun no implementado(cambiar))
			RegalosControl.checkForPresents(DbConnector, EventoControl.getEvents(DbConnector)); //Comprueba si toca avisar de algun evento para regalar cuando es medianoche
			dayTimer = null;
			dayTimer = new Timer(24*60*60*1000,this); //Un poco cutre no? nose...
			dayTimer.start();
		}
		if (command.equals("Exit")) {
			System.exit(0);
		}
		else if (command.equals("Login")) {
			String[] userYPwd = DialogGenerator.createUserPwdDialog(new JFrame(""),0); //Abre el dialogo que pide User y pwd y obtiene el resultado
			if (userYPwd[0] == null || userYPwd[0].equals("") || userYPwd[1].equals("")){ return;} //User pressed cancel or introduced a non valid username/pwd (add more checks to validate input in the future)
			boolean loginConseguido = UserControl.logInUser(DbConnector, userYPwd[0], userYPwd[1]);
			if (loginConseguido){
				if (dayTimer != null){
					dayTimer = null;
				}
				String[] nowValues = LocalTime.now().toString().split(":");
				int totalMilliSecs = ( ((24 - Integer.parseInt(nowValues[0])) * 60 * 60) - (Integer.parseInt(nowValues[1]) * 60) ) * 1000;
				dayTimer = new Timer(totalMilliSecs,this);
				dayTimer.start();
				LabelLogged.setText("Conectado como: " + userYPwd[0]);
				Button3.setText("Logout");
				RegalosControl.checkForPresents(DbConnector, EventoControl.getEvents(DbConnector)); //Comprueba si toca avisar de algun evento para regalar cuando el usuario se loguea
			}
			else {
				LabelLogged.setText("Usuario/Pwd desconocido");
			}	
		}
		else if (command.equals("Nuevo usuario")){
			if (UserControl.isUserLogged(DbConnector) == false){
				String[] userYPwd = DialogGenerator.createUserPwdDialog(new JFrame(""),0);
				if (userYPwd[0] == null || userYPwd[0].equals("") || userYPwd[1].equals("")){ return;} 
				int agregado = UserControl.insertUser(DbConnector, userYPwd[0], userYPwd[1]);
				switch (agregado){
				case -1:
					LabelLogged.setText("Error en la base de datos");
					break;
				case -2:
					LabelLogged.setText("El usuario ya existe!");
					break;
				default:
					LabelLogged.setText("Usuario agregado, bienvenido: " + userYPwd[0]);
					UserControl.logInUser(DbConnector, userYPwd[0], userYPwd[1]);
					Button3.setText("Logout");
					//No comprovamos eventos porque sera nuevo user
				}
			}
			else{
				LabelLogged.setText("No puedes agregar usuario si estas conectado!");
			}
		}
		else if (command.equals("Borrar usuario")){ //01/04/17: Arreglar aqui i en agregar user i en login al apretar cancelar en el dialogo
			String[] userYPwd = DialogGenerator.createUserPwdDialog(new JFrame(""),1);
			if (userYPwd[0] == null || userYPwd[0].equals("") || userYPwd[1].equals("")){ return;} 
			int borrado = UserControl.removeUser(DbConnector, userYPwd[0], userYPwd[1]); 
			if(borrado == 1){
				LabelLogged.setText("Usuario borrado, desconectando...");
				UserControl.logOutUser(DbConnector); //Desconecta a quien esté conectado...
				Button3.setText("Login");
			}
			else {
				LabelLogged.setText("Usuario/Pwd equivocado");
			}
		}
		else if (command.equals("Tus eventos")){
			if(UserControl.isUserLogged(DbConnector)) {
				DialogGenerator.createElegirVerEventoDialog(new JFrame("Eventos"), EventoControl.getEvents(DbConnector));
			}
		}
		else if (command.equals("Agregar evento")) {
			if(UserControl.isUserLogged(DbConnector)) {
				Proposal_GUI ventana = new Proposal_GUI(); //Crea una nueva Proposal_GUI (es una JFrame, con esta instancia veo los cambios que ocurren despues de llamar a la ventanita
				JPanel ProposalGUIPanel = (JPanel)ventana.getContentPane(); //Como Proposal_GUI es una JFrame obtengo lo de dentro asi para ponerlo en el dialogo
	            UIManager.put("OptionPane.yesButtonText", "Agregar evento");
	            UIManager.put("OptionPane.noButtonText", "Cancelar");
				int dialogResult = JOptionPane.showConfirmDialog (new JFrame("test"), ProposalGUIPanel,"Agregar evento",JOptionPane.YES_NO_OPTION); //Lanzo un dialogo con el contenido de Proposal_GUI, cuyos cambios afectaran a la instancia de proposalGUI que tengo
	            UIManager.put("OptionPane.yesButtonText", "Aceptar");
	            UIManager.put("OptionPane.noButtonText", "No");
	            if (dialogResult == JOptionPane.YES_OPTION){ //Si el usuario quiere agregar el evento introducido...
					//HACER COMPROVACIONES AQUI DE QUE TODOS LOS DATOS INTRODUCIDOS SON CORRECTOS Y LEGALES, ya sea en el metodo de PropGUI o algun metodo en EventData que comprueba que todos los datos son legales...
					EventData eventoNuevo = ventana.getNewEventData(); //leo los datos introducidos en la ventana de proposal_gui...
					eventoNuevo.userID = Integer.toString(DbConnector.getUserID()); //Cutre pero necesario
					EventoControl.addEvent(DbConnector, eventoNuevo);
					RegalosControl.checkForPresents(DbConnector, EventoControl.getEvents(DbConnector)); //Comprueba si toca avisar de algun evento por si el que ha agregado toca...
	            }
			}
		}
		else if (command.equals("Créditos")){
			JFrame window = new JFrame("Copyright ChichiNabo Productions 2017");
			DialogV2 panelCreditos = new DialogV2();
			window.setContentPane(panelCreditos);
			window.setSize(500,500);
			window.setLocation(500,500); //Hace falta comprobar tamaño ventana usuario etc (otro dia)
			window.setResizable(false);
			window.setVisible(true);
	        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			window.pack();
		}
		else if (command.equals("Logout")) {
			UserControl.logOutUser(DbConnector);
			LabelLogged.setText("Usuario no conectado");
			Button3.setText("Login");
		}
		//etc para la resta de botones, hacer que llamen a la resta de clases para interacciones
	}
	
	/*Funcion auxiliar para capitalizar la primera letra de una string en lowercase*/
	private static String capitalizeFirst(String word){
		char[] letters = word.toCharArray();
		letters[0] -= 32;
		return String.copyValueOf(letters);
	}

	public static void main(String[] args) {
            JFrame window = new JFrame("Regalator 3000");
            MainGUI things = new MainGUI();
	    window.setContentPane(things);
	    JMenuBar topMenu = createMenuBar(things); 
	    window.setJMenuBar(topMenu);
		//window.setResizable(false);
            window.setContentPane(things);
            window.setSize(100,200);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            window.setLocation(0,screenSize.height-window.getHeight()-150); //hacer un pequeño ini con posicion inicial predeterminada? (en este caso, abajo a la izq)
            window.pack();
		//window.setSize(1100,750);
		//window.setLocation(100,0);  //Pillarho per resolucio
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setVisible(true); 
            /*Cambiar botones de los Dialogos a castellano*/
            UIManager.put("OptionPane.cancelButtonTetxt", "Cancelar");
            UIManager.put("OptionPane.yesButtonText", "Aceptar");
            UIManager.put("OptionPane.okButtonText", "Introducir");
			//TESTING, no tengo ganas de reintroducir usuario cada vez...
	}

}

