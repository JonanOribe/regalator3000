	package regalator3000;
/* A FECHA DE 15/04 FALTA: Añadir mas datos en la BBDD, añadir extra campos para: imagenes que renderear de los productos, mail de los usuarios, paginas web para los productos que el usuario pueda clickar etc
 * Mejorar GUI del aviso del regalo, Añadir cambio de idioma a la GUI en opciones -> más...*/
			
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import regalator3000.db.DatabaseHandler;
import regalator3000.db.EventoControl;
import regalator3000.db.RegalosControl;
import regalator3000.db.UserControl;
import regalator3000.gui.CalendarPanel;
import regalator3000.gui.CreditsPanel;
import regalator3000.gui.DialogGenerator;
import regalator3000.gui.EventsPanel;



/*Clase Principal del programa, llamarla para generar la GUI y comenzar 
 * el proceso de controlar el dia actual y cuando pasa cada dia*/
@SuppressWarnings("serial")
public class MainGUI extends JPanel implements ActionListener{
	
	private JButton Button2,Button3; 
	private JLabel LabelMes,LabelAnyo,LabelDia,LabelDiaNombre,LabelLogged; //Contiene y enseña el dia/mes/año actual (no usar JLabel, currarse algo del palo dibujar un numero bonito o usar mas de una Label con fonts wapas para k kede bonito
	private DatabaseHandler DbConnector = new DatabaseHandler(); //instancia de DatabaseHandler que controlara las conexiones con la BBDD
	private Timer dayTimer;
	//Main constructor(add parameters?)
	public MainGUI(){
            setupMainPanel();
			String[] nowValues = LocalTime.now().toString().split(":"); //Inicializamos el timer hasta medianoche
            int totalMilliSecs = ( ((24 - Integer.parseInt(nowValues[0])) * 60 * 60) - (Integer.parseInt(nowValues[1]) * 60) ) * 1000;
			dayTimer = new Timer(totalMilliSecs,this);
			dayTimer.start();
	}

	
	public void setupMainPanel() { 
		this.setLayout(new BorderLayout(10,10));		
		JPanel southPanel = new JPanel();
		JPanel southPanel2 = new JPanel();
		southPanel2.setLayout(new GridLayout(3,1,5,5)); //Cambiar
		southPanel.setLayout(new BorderLayout(25,25));
		//Button1 = new JButton("Agregar evento");
		Button2 = new JButton("Tus eventos");
		Button3 = new JButton("Login");
        LabelLogged = new JLabel("Usuario no conectado");
        LabelLogged.setHorizontalAlignment(SwingConstants.CENTER);
		//Button1.addActionListener(this);
		Button2.addActionListener(this);
		Button3.addActionListener(this);
		//southPanel.add(Button1);
		JPanel auxPanel = new JPanel();
		auxPanel.setLayout(new BorderLayout(25,25));
		auxPanel.add(new JLabel("                   "), BorderLayout.WEST); //Manera cutre de poner espacios, pero quiero montar todo antes de poner bonita la GUI
		auxPanel.add(Button2,BorderLayout.CENTER);
		auxPanel.add(new JLabel("                   "), BorderLayout.EAST);
		southPanel.add(new JLabel("                                 "), BorderLayout.WEST);
		southPanel.add(Button3,BorderLayout.CENTER);
		southPanel.add(new JLabel("                                 "), BorderLayout.EAST);
		southPanel2.add(auxPanel);
		southPanel2.add(southPanel);
        southPanel2.add(LabelLogged);
        Calendar myCalendar = Calendar.getInstance();
        Date today = new Date(); 
        myCalendar.setTime(today);
		LabelMes = new JLabel(capitalizeFirst(CalendarPanel.monthNames_ESP[myCalendar.get(Calendar.MONTH)]));
		LabelMes.setFont(new Font("TimesRoman",Font.BOLD,28));
                LabelMes.setHorizontalAlignment(SwingConstants.CENTER);
		LabelDia = new JLabel("" + myCalendar.get(Calendar.DAY_OF_MONTH));
                LabelDia.setFont(new Font("TimesRoman", Font.BOLD, 78));
                LabelDia.setForeground(Color.red);
                LabelDia.setHorizontalAlignment(SwingConstants.CENTER);
                LabelDia.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLUE, 4),BorderFactory.createLineBorder(Color.BLACK, 4)));
		LabelAnyo = new JLabel("" + myCalendar.get(Calendar.YEAR));
		LabelAnyo.setFont(new Font("TimesRoman",Font.BOLD,32));
                LabelAnyo.setHorizontalAlignment(SwingConstants.CENTER);
                LabelDiaNombre = new JLabel("(" + capitalizeFirst(CalendarPanel.dayNames_ESP[(myCalendar.get(Calendar.DAY_OF_WEEK)-1)]) + ")"); //Lunes -> 0, etc...
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
		JMenu LoginMenu = new JMenu("Mas");
		JMenuItem LogOnButton = new JMenuItem("Creditos");
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
		if (command == null) { //El timer se ha activado, es medianoche del siguiente dia
			if (UserControl.isUserLogged(DbConnector)){
				RegalosControl.checkForPresents(DbConnector, EventoControl.getEvents(DbConnector)); //Comprueba si toca avisar de algun evento para regalar cuando es medianoche
			}
			dayTimer = null;
			dayTimer = new Timer(24*60*60*1000,this); //Timer puesto para medianoche del siguiente dia, recomenzamos la GUI con los datos de este nuevo dia
			this.removeAll();
			this.setupMainPanel();
			this.revalidate();
			dayTimer.start();
			return;
		}
		if (command.equals("Exit")) {
			System.exit(0);
		}
		else if (command.equals("Login")) {
			String[] userYPwd = DialogGenerator.createUserPwdDialog(new JFrame(""),0); //Abre el dialogo que pide User y pwd y obtiene el resultado
			if (userYPwd[0] == null || userYPwd[0].equals("") || userYPwd[1].equals("")){ return;} //User pressed cancel or introduced a non valid username/pwd (add more checks to validate input in the future)
			boolean loginConseguido = UserControl.logInUser(DbConnector, userYPwd[0], userYPwd[1]);
			if (loginConseguido){
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
				String[] userYPwd = DialogGenerator.createUserPwdDialog(new JFrame(""),1);
				if (userYPwd[0] == null || userYPwd[0].equals("") || userYPwd[1].equals("") || userYPwd[2].equals("")){ return;} 
				Pattern mailTag = Pattern.compile("^[a-zA-Z\\d_]+@[a-zA-Z\\d]+\\.[a-z]{1,3}\\.?[a-z]{2,4}?$",Pattern.CASE_INSENSITIVE); //mi intento de patron regex para pillar mails
				Matcher matcher = mailTag.matcher(userYPwd[2]);
				if (!matcher.matches()){
					LabelLogged.setText("Error en el campo e-mail");
					return;
				}
				int agregado = UserControl.insertUser(DbConnector, userYPwd[0], userYPwd[1], userYPwd[2]);
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
		else if (command.equals("Borrar usuario")){ 
			String[] userYPwd = DialogGenerator.createUserPwdDialog(new JFrame(""),2);
			if (userYPwd[0] == null || userYPwd[0].equals("") || userYPwd[1].equals("") || userYPwd[2].equals("")){ return;} 
			int borrado = UserControl.removeUser(DbConnector, userYPwd[0], userYPwd[1], userYPwd[2]); 
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
		        EventsPanel content = new EventsPanel(DbConnector);
		        JOptionPane.showOptionDialog(new JFrame("test"), content,"Eventos de " + DbConnector.getUserName(), JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.CLOSED_OPTION, null, new Object[]{"Atrás"}, null);
			}
		}
		else if (command.equals("Creditos")){
			JFrame window = new JFrame("Copyright ChichiNabo Productions 2017");
			CreditsPanel panelCreditos = new CreditsPanel();
			window.setContentPane(panelCreditos);
			window.setLocation(600,400); //Hace falta comprobar tamaño ventana usuario etc (otro dia)
			window.setResizable(false);
			window.setVisible(true);
	        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			window.pack();
			window.setSize(400,200);
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
		if (Character.isLowerCase(letters[0])) {letters[0] -= 32;}
		return String.copyValueOf(letters);
	}

	public static void main(String[] args) {
		try {//Pruebas con el Look and feel este a ver que tal va...
        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} 
		catch (Exception e) {
			try{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	
			}
			catch(Exception a){
				System.out.println("Program not supported. " + a.getMessage());
				System.exit(0);
			}
		}
            JFrame window = new JFrame("Regalator 3000");
            MainGUI things = new MainGUI();
	    window.setContentPane(things);
	    JMenuBar topMenu = createMenuBar(things); 
	    window.setJMenuBar(topMenu);
		//window.setResizable(false);
            window.setContentPane(things);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            window.pack();
            window.setSize(420,350);
            window.setLocation(0,screenSize.height-window.getHeight()-80); //hacer un pequeño ini con posicion inicial predeterminada? (en este caso, abajo a la izq)
		//window.setSize(1100,750);
		//window.setLocation(100,0);  //Pillarho per resolucio
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setVisible(true); 
            /*Cambiar botones de los Dialogos a castellano*/
            UIManager.put("OptionPane.cancelButtonTetxt", "Cancelar");
            UIManager.put("OptionPane.yesButtonText", "Aceptar");
            UIManager.put("OptionPane.okButtonText", "Introducir");
			//TESTING, no tengo ganas de reintroducir usuario cada vez
	}

}
