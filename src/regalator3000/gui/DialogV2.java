/*Fer panell amb un calendari dins(1), engancharli eventos dels usuaris als dies que toquin(2)*/
/*Fer aqui tambe el panell dels credits, a vere que posem... FET*/
package regalator3000.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class DialogV2 extends JPanel implements ActionListener{
	
	private static int estado; //En que estado de la "broma" estamos
	private JButton primerClick,secondClick,thirdClick;
	private JPanel southPanel;
	private JFrame broma2Frame;
	private Timer broma2;
	private static Random randomGen = new Random();
	
	/*Constructor*/
	public DialogV2(){
		super();
		estado = 0;
		broma2 = new Timer(100,this);
		initcomponents();
	}
	
	/*Reinitializes the Main panel's components with the different buttons for the "joke" each time*/
	public void initcomponents(){
		this.setLayout(new BorderLayout(5,5));
		JPanel centerPanel = new JPanel();
		southPanel = new JPanel();
		southPanel.setLayout(new GridLayout(3,1,5,5));
		centerPanel.add(new JLabel("Made by stupid, with stupid, for stupid"));
		if (estado == 0){
			primerClick = new JButton("De acuerdo!");
			primerClick.addActionListener(this);
			southPanel.add(primerClick);
		}
		else { southPanel.add(new JLabel(""));}
		if (estado == 1) {
			secondClick = new JButton("Dejadme salir cabrones!");
			secondClick.addActionListener(this);
			southPanel.add(secondClick);
		}
		if (estado == 2){
			JLabel DummyLabel1 = new JLabel(" ");
			thirdClick = new JButton("Ahora de verdad, salimos");
			thirdClick.addActionListener(this);
			southPanel.add(DummyLabel1);
			southPanel.add(thirdClick);
		}
		if (estado == 3){
			JLabel DummyLabel1 = new JLabel(" ");
			thirdClick = new JButton("Seguro?");
			thirdClick.addActionListener(this);
			southPanel.add(DummyLabel1);
			southPanel.add(thirdClick);
		}
		if (estado >= 4){
			JLabel DummyLabel1 = new JLabel(" ");
			thirdClick = new JButton("Vaaaale");
			thirdClick.addActionListener(this);
			southPanel.add(DummyLabel1);
			southPanel.add(thirdClick);
		}
			this.add(centerPanel, BorderLayout.CENTER);
			this.add(southPanel,BorderLayout.SOUTH);
	}
	
	/*Crea un panell senzill per fer la broma*/
	public void simplePanel(){
		JButton clickMe = new JButton("De acuerdo!");
		clickMe.addActionListener(this);
		this.add(clickMe);
	}
	
	/*Clicks a los botones y timer*/
	public void actionPerformed(ActionEvent evt){
		if (estado >= 5){ //Se ha cerrado todo, cierra ventanas abiertas si las hay y cancela el timer
			broma2Frame.dispose();
			broma2.stop();
			estado = 0;
		}
		String command = evt.getActionCommand();
		if (command == null){ //timer
			if (estado >= 2){ //Got this way of checking mouse pos from stack overflow
				PointerInfo posicionRaton = MouseInfo.getPointerInfo();
				Point pos = posicionRaton.getLocation();
				int x = (int) pos.getX();
				int y = (int) pos.getY();
				//System.out.println(x + " , " + y);
				int luck = randomGen.nextInt(4); //Maybe make a circular motion next time? this is crazier and more spastic
				int xChange = randomGen.nextInt(60)+50;
			    int yChange = randomGen.nextInt(60)+50;
				switch(luck){
				case 0:
					broma2Frame.setLocation(x+xChange, y+yChange);
					break;
				case 1:
					broma2Frame.setLocation(x-xChange, y+yChange);
					break;
				case 2:
					broma2Frame.setLocation(x+xChange, y-yChange);
					break;
				default:
					broma2Frame.setLocation(x-xChange, y-yChange);
				}
			};
		}
		else{
	    	if (command.equals("De acuerdo!") && estado == 0){
	    		estado=1;
	    		generateDummyFrame(); 	//the dummy frame can generate others
	    		this.removeAll(); 		//Reinitialize the main panel's components
	    		this.initcomponents();
	    		this.revalidate();
	    	}
	    	else if (command.equals("Dejadme salir cabrones!")){
	    		estado=2;
	    		broma2Frame = new JFrame("No puedes salir...");
	    		JPanel content = new JPanel();
	    		JLabel text = new JLabel("No me dejes!");
	    		content.add(text);
	    		broma2Frame.setContentPane(content);
				PointerInfo posicionRaton = MouseInfo.getPointerInfo();
				Point pos = posicionRaton.getLocation();
				int x = (int) pos.getX();
				int y = (int) pos.getY();
				broma2Frame.setLocation(x, y+50);
				broma2Frame.setVisible(true);
				broma2Frame.setAlwaysOnTop(true);
				broma2Frame.pack();
	    		broma2.start();
	    		this.removeAll();
	    		this.initcomponents();
	    		this.revalidate();
	    	}
	    	else if(command.equals("Ahora de verdad, salimos")){
	    		estado = 3;
	    		this.removeAll();
	    		this.initcomponents();
	    		this.revalidate();
	    	}
	    	else if (command.equals("Seguro?")){
	    		estado = 4;
	    		this.removeAll();
	    		this.initcomponents();
	    		this.revalidate();
	    	}
	    	else if (command.equals("Vaaaale")){
	    		estado = 5;
	    		JFrame myWindow = (JFrame)getTopLevelAncestor();
	    		myWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    		myWindow.dispatchEvent((new WindowEvent(myWindow, WindowEvent.WINDOW_CLOSING)));
	    	}
		}
	}
    
    public void generateDummyFrame(){
		JFrame newWindow = new JFrame(". . .");
		newWindow.setLocation(randomGen.nextInt(400), randomGen.nextInt(400));
		JPanel broma = new JPanel();
		JButton throw2 = new JButton("De acuerdo!");
		throw2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               if (estado != 0 && estado < 5){ 
               generateDummyFrame();
               }
               newWindow.dispose();
               }
		});
		broma.add(throw2);
		newWindow.setContentPane(broma);
		newWindow.setVisible(true);
		newWindow.pack();
    }
    	
	public static void main(String[] args){
		JFrame window = new JFrame("testing");
		DialogV2 panelCreditos = new DialogV2();
		window.setContentPane(panelCreditos);
		window.setSize(500,500);
		window.setResizable(false);
		window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
	}

	/*Fer tambe calendari grafic aki*/
}
