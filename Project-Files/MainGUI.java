package regalator3000;

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
import javax.swing.UIManager;

/*Clase Principal del programa, llamarla para generar la GUI y comenzar 
 * el proceso de controlar el dia actual y cuando pasa cada dia*/
public class MainGUI extends JPanel implements ActionListener{
	
	private JButton Button1,Button2,Button3; //usarl array de JButtons?
	private JLabel LabelMes,LabelAnyo,LabelDia,LabelDiaNombre,LabelLogged; //Contiene y enseña el dia/mes/año actual (no usar JLabel, currarse algo del palo dibujar un numero bonito o usar mas de una Label con fonts wapas para k kede bonito
	private DatabaseHandler DbConnector = new DatabaseHandler(); //instancia de DatabaseHandler que controlara las conexiones con la BBDD
	
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
		DateFormatSymbols dfs = new DateFormatSymbols();
		String[] monthNames = dfs.getMonths();
                String[] dayNames = dfs.getWeekdays();
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
                LabelDiaNombre = new JLabel("(" + dayNames[Integer.parseInt(todayValues[2])%9] + ")"); //Este calculo esta mal, cambiar
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
			return; //Por ahora sal ya que dara error de isnull sino, cambiar al poner timer
		}
		if (command.equals("Exit")) {
			System.exit(0);
		}
		else if (command.equals("Login")) {
			String[] userYPwd = DialogGenerator.createUserPwdDialog(new JFrame()); //Abre el dialogo que pide User y pwd y obtiene el resultado
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
	            }
			}
		}
		else if (command.equals("Logout")) {
			UserControl.logOutUser(DbConnector);
			LabelLogged.setText("Usuario no conectado");
			Button3.setText("Login");
		}
		//etc para la resta de botones, hacer que llamen a la resta de clases para interacciones
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
            window.setLocation(0,screenSize.height-window.getHeight()); //hacer un pequeño ini con posicion inicial predeterminada? (en este caso, abajo a la izq)
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
            things.LabelLogged.setText("Conectado como: Felipe");
			things.DbConnector.setUserID(2);
			things.Button3.setText("Logout");
	}

}