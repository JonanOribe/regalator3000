/*Fer panell amb un calendari dins(1), engancharli eventos dels usuaris als dies que toquin(2)*/
/*Fer aqui tambe el panell dels credits, a vere que posem... FET*/
package regalator3000.gui;

import java.awt.BorderLayout;
import java.awt.Font;
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
public class CreditsPanel extends JPanel implements ActionListener{
	
	private static int estado; //En que estado de la "broma" estamos
	private JFrame broma2Frame;
	private Timer broma2;
	private static Random randomGen = new Random();
	private JFrame dummyFrame1,dummyFrame2;
	
	/*Constructor*/
	public CreditsPanel(){
		super();
		estado = 0;
		broma2 = new Timer(100,this);
		initcomponents();
	}
	
	/*Reinitializes the Main panel's components with the different buttons for the "joke" each time*/
	public void initcomponents(){
		this.setLayout(new BorderLayout(5,5));
		JPanel centerPanel = new JPanel();
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridLayout(3,1,5,5));
		JLabel creditsText1 = new JLabel("Hecho con mucho carinyo y respeto");
		JLabel creditsText2 = new JLabel("en 2017");
		creditsText1.setFont(new Font("Calibri",Font.BOLD,18));
		creditsText2.setFont(new Font("Calibri",Font.BOLD,18));
		centerPanel.add(creditsText1);
		centerPanel.add(creditsText2);
		if (estado == 0){
			JButton primerClick = new JButton("De acuerdo!");
			primerClick.addActionListener(this);
			southPanel.add(primerClick);
		}
		else { southPanel.add(new JLabel(""));}
		if (estado == 1) {
			JButton secondClick = new JButton("Dejadme salir aaah!");
			secondClick.addActionListener(this);
			southPanel.add(secondClick);
		}
		if (estado == 2){
			JLabel DummyLabel1 = new JLabel(" ");
			JButton thirdClick = new JButton("Ahora de verdad, salimos");
			thirdClick.addActionListener(this);
			southPanel.add(DummyLabel1);
			southPanel.add(thirdClick);
		}
		if (estado == 3){
			JLabel DummyLabel1 = new JLabel(" ");
			JButton thirdClick = new JButton("Seguro que quieres salir?");
			thirdClick.addActionListener(this);
			southPanel.add(DummyLabel1);
			southPanel.add(thirdClick);
		}
		if (estado >= 4){
			JButton thirdClick = new JButton("Vaaaale");
			thirdClick.addActionListener(this);
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
			dummyFrame1.dispose();
			dummyFrame2.dispose();
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
	    		this.getTopLevelAncestor().setLocation(randomGen.nextInt(600)+150, randomGen.nextInt(450));
	    		generateDummyFrame(0); 	//the dummy frame can generate others
	    		this.removeAll(); 		//Reinitialize the main panel's components
	    		this.initcomponents();
	    		this.revalidate();
	    	}
	    	else if (command.equals("Dejadme salir aaah!")){
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
	    		this.getTopLevelAncestor().setLocation(randomGen.nextInt(400)+200, randomGen.nextInt(450));
	    	}
	    	else if (command.equals("Seguro que quieres salir?")){
	    		estado = 4;
	    		this.removeAll();
	    		this.initcomponents();
	    		this.revalidate();
	    	}
	    	else if (command.equals("Vaaaale")){
	    		estado = 5;
	    		JFrame myWindow = (JFrame)getTopLevelAncestor();
	    		dummyFrame1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    		dummyFrame1.dispatchEvent((new WindowEvent(myWindow, WindowEvent.WINDOW_CLOSING)));
	    		dummyFrame2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    		dummyFrame2.dispatchEvent((new WindowEvent(myWindow, WindowEvent.WINDOW_CLOSING)));
	    		myWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    		myWindow.dispatchEvent((new WindowEvent(myWindow, WindowEvent.WINDOW_CLOSING)));
	    	}
		}
	}
    
    public void generateDummyFrame(int window){
    	if (window == 0 || window == 1){
    	dummyFrame1 = new JFrame("Aqui estoy!");
    	dummyFrame1.setLocation(randomGen.nextInt(800), randomGen.nextInt(400));
		dummyFrame1.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		JPanel broma1 = new JPanel();
		JButton throw1 = new JButton("De acuerdo!");
		throw1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { 
               dummyFrame1.dispose();
               generateDummyFrame(1);
               }
		});
		broma1.add(throw1);
		dummyFrame1.setContentPane(broma1);
		dummyFrame1.setVisible(true);
		dummyFrame1.pack();
		dummyFrame1.setSize(300,80);
    	}
    	if (window == 0 || window == 2){
    	dummyFrame2 = new JFrame("Estoy aqui!");
    	dummyFrame2.setLocation(randomGen.nextInt(800), randomGen.nextInt(400));
		dummyFrame2.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		JPanel broma2 = new JPanel();
		JButton throw2 = new JButton("De acuerdo!");

		throw2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { 
               dummyFrame2.dispose();
               generateDummyFrame(2);
               }
		});
		broma2.add(throw2);
		dummyFrame2.setContentPane(broma2);
		dummyFrame2.setVisible(true);
		dummyFrame2.pack();
		dummyFrame2.setSize(300,80);
    	}
    }
    	
	public static void main(String[] args){
		JFrame window = new JFrame("testing");
		CreditsPanel panelCreditos = new CreditsPanel();
		window.setContentPane(panelCreditos);
		window.setResizable(false);
		window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setSize(400,200);
	}

	/*Fer tambe calendari grafic aki*/
}
