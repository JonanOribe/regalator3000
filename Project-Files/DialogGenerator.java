package regalator3000;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class DialogGenerator {
	
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

	    JOptionPane.showMessageDialog(frame, panel, "Login", JOptionPane.QUESTION_MESSAGE);
	    
	    try {
	    logininformation[0] = username.getText();
	    logininformation[1] = new String(password.getPassword());
	    }
	    catch(Exception e){
	    }
	    return logininformation;
	}
	
	public static int createElegirEventoDialog(JFrame frame, ArrayList<EventData> eventos){
		JPanel panel = new JPanel(new BorderLayout(5,5));
		
		JRadioButton[] botones = new JRadioButton[eventos.size()];
		JPanel listadoEventos = new JPanel(new GridLayout(eventos.size(),1,5,5));	
		ButtonGroup eleccion = new ButtonGroup();
		boolean first = true;
		for (int i = 0; i < eventos.size(); i++) {
			if (i == 1) { 
				first = false;
			}
			botones[i] = new JRadioButton(eventos.get(i).fecha +" // " + eventos.get(i).descripcion,first);
			listadoEventos.add(botones[i]);
			eleccion.add(botones[i]);
		}
		panel.add(listadoEventos, BorderLayout.CENTER);
		
		JOptionPane.showMessageDialog(frame, panel, "Elige evento", JOptionPane.QUESTION_MESSAGE);
		return whatButtonIsPressed(botones); //Retorna cual de los eventos ha elegido (en el array de eventos)
	}
	
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
