/*02/06 -> Fade in y fade out de imagenes implementado, parece que funciona bien pero habria que probar con imagenes pequeñas si el resultado es el esperado o poner todas las imagenes que usemos
 * de un intervalo de tamaño (o de uno fijo) concreto.
 * 
 *  - * Cambios para proposal_GUI -> eliminar objetos de las combobox al seleccionarlos, que regalo concreto tenga en cuenta la eleccion ya hecha para la lista (demasiadas llamadas a la BBDD? pensar) 
 * 
 * Falta: 
 * cambiar la GUI para que quede más ordenada y bonita usando probablemente un groupLayout, mejorarla con mejores fuentes, otros botones nose?
 * En la base de datos asignar a cada regalo la imagen/URLS que tocan... (maomeno)
 * Algo que me dejo seguro
 */
package regalator3000.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import regalator3000.db.DatabaseHandler;
import regalator3000.db.EventoControl;
import regalator3000.misc.EventData;


@SuppressWarnings("serial")
public class RegaloPanel extends JPanel implements ActionListener{
	
	private static final int maxWidth = 800; //Maximo tamaño de la imagen
	private static final int maxHeight = 600;
	private static final int timerTick = 40; //Time in milliseconds the animation will step once
	private static final float alphaDelta = 0.07f; //How much the transparency will change per tick
	private static final int mainImageStandInTick = 30; //Ticks the main image will stay before changing for the secondary one, for each cicle ( number / timerTick to know the number in millis)
	private static final String regalatorPage = "http://www.regalator3000maxchupiway.com"; //poner aqui la pagina del regalator cuando este
	private static final String regaloNoEncontradoSrc = "regalo_NE.jpg";  //Ruta a la imagen de regalo no encontrado
	private static final BasicStroke strokeType = new BasicStroke(5.0f);
	public static final String currentDir = System.getProperty("user.dir") + File.separator + "imgs" + File.separator;
	
	private BufferedImage imgFadeOut, imgFadeIn;
	ImgPanel imgPanel;
	JLabel text1,text2,text3,text4;
	String firstURL, secondURL, imagenRegalo; 
	EventData eventoRelacionado;  //Para saber que evento relacionar con el regalo, por si hay que borrarlo o hacer otras cosas
	private Timer alphaTimer;
	private float currentAlpha = 1f; //transparencia inicial (totalmente opaco)
	private int standInTicks = mainImageStandInTick;
	private boolean mainImage = true;
	
	public RegaloPanel(EventData evento, int dias, String[] regaloData){
		super();
		eventoRelacionado = evento;
		try{
			if (regaloData[1].equals("")) {
				regaloData[1] = regaloNoEncontradoSrc; //IMAGEN DE REGALO NO ENCONTRADO...
			}
			imagenRegalo = "regalo.jpg";
			Image imgLoader = ImageIO.read(new File(currentDir + regaloData[1])); //O potser carregar les dues veure quina es mes gran i a partir daqui canviar tamanys...
			//System.out.println("Actual size: " + imgLoader.getWidth(this) + " , " + imgLoader.getHeight(this));
			int finalWidth = (imgLoader.getWidth(this) >= (maxWidth-20)) ? (maxWidth-20) : imgLoader.getWidth(this); //Comprobar tambe que no sigui massa petita?
			int finalHeight = (imgLoader.getHeight(this) >= (maxHeight-185)) ? (maxHeight-185) : imgLoader.getHeight(this);
			//System.out.println("Used size: " + finalWidth + " , " + finalHeight);
			imgFadeOut = getImgOfSize(imgLoader, finalWidth, finalHeight); //aixo o forçar totes un tamany pero cutre...
			imgLoader = ImageIO.read(new File(currentDir + imagenRegalo));
			finalWidth = (imgLoader.getWidth(this) >= (maxWidth-20)) ? (maxWidth-20) : imgLoader.getWidth(this); //Comprobar tambe que no sigui massa petita?
			finalHeight = (imgLoader.getHeight(this) >= (maxHeight-185)) ? (maxHeight-185) : imgLoader.getHeight(this);
			imgFadeIn = getImgOfSize(imgLoader, finalWidth, finalHeight);
		}
		catch(Exception e){
			System.out.println("Error al cargar la imagen. " + e.getMessage());
		}
		firstURL = regaloData[2];
		secondURL = regaloData[3]; //O pasar esto a initComponents i ponerlo como parametro local ya veremos si hay que cambiarlo por eventos o no
		initcomponents(dias, regaloData[0]);
		alphaTimer = new Timer(timerTick,this);
		alphaTimer.start();
	}
	
	private BufferedImage getImgOfSize(Image originalImg, int finalWidth, int finalHeight){
		try {
			BufferedImage finalImg = new BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_ARGB); //Creamos una nueva bufferedimage con el tamaño correcto
			Graphics2D imgGraphics = finalImg.createGraphics(); //Creamos una instancia de sus graficos para dibujarla
			imgGraphics.drawImage(originalImg, 0, 0,finalWidth,finalHeight, null); //La dibujamos sin offset y con el grafico de la imagen cargada
			imgGraphics.dispose(); //Nos cargamos la instancia del motor grafico
			return finalImg;
		}
		catch(Exception e){
			System.out.println("Problems converting the image to another size! " + e.getMessage());
			return null;
		}
	}
	
	/*setComposite technique derived from stackoverflow:
	 * https://stackoverflow.com/questions/20346661/java-fade-in-and-out-of-images
	 */
	private class ImgPanel extends JPanel{

		public void paintComponent(Graphics g){
			Graphics2D g2d = (Graphics2D) g;
			super.paintComponent(g2d);
			int desiredWidth = (imgFadeOut.getWidth() >= imgFadeIn.getWidth()) ? imgFadeOut.getWidth() : imgFadeIn.getWidth();     //Haz la imagen del tamaño d la imagen mas grande
			int desiredHeight = (imgFadeOut.getHeight() >= imgFadeIn.getHeight()) ? imgFadeOut.getHeight() : imgFadeIn.getHeight();
			int positionX = (desiredWidth >= this.getWidth()) ? 0 : (this.getWidth() - desiredWidth)/2; //Centra la imagen si es mas pequeña que el JPanel
			int positionY = (desiredHeight >= this.getHeight()) ? 0 : (this.getHeight() - desiredHeight)/2;
 
            
            g2d.setComposite(AlphaComposite.SrcOver.derive(currentAlpha));
            g2d.drawImage(imgFadeOut, positionX, positionY, desiredWidth, desiredHeight, this);
            
            g2d.setComposite(AlphaComposite.SrcOver.derive(1f-currentAlpha));
            g2d.drawImage(imgFadeIn, positionX, positionY, desiredWidth, desiredHeight, this);

            g2d.setComposite(AlphaComposite.SrcOver.derive(1f));
            g2d.setStroke(strokeType);
            g2d.drawRect(positionX, positionY+2, desiredWidth, this.getHeight()-5);
		}
	}
	
	public void actionPerformed(ActionEvent evt){
		String command = evt.getActionCommand();
		if (command == null){
			String[] things = this.toString().split(",");
			if (things[4].equals("invalid")){ alphaTimer.stop();} //Cutre cutre, pero al usar nested frames i tal el timer seguia en scope cuando cerrabas el dialogo, esto lo para
			//Me preocupa que el dialogo en si se mantenga tambien en scope pero la comprobacion es larga...
			if (standInTicks <= 0) {
				currentAlpha -= alphaDelta;
			}
			else {
				standInTicks--;
			}
			repaint();
			if (currentAlpha <= 0f){ 
				mainImage = !mainImage;
				if (mainImage) standInTicks = mainImageStandInTick;
				currentAlpha = 1f;
				BufferedImage tmp = imgFadeIn;
				imgFadeIn = imgFadeOut;
				imgFadeOut = tmp;
			}
		}
		else if (command.equals("Borra el evento")) {
			int seguro = JOptionPane.showConfirmDialog(new JFrame(""), "Estas seguro de querer borrar el evento " + eventoRelacionado.descripcion + "?", "Borrar el evento", JOptionPane.YES_NO_OPTION);
			if (seguro == 0) { //Opciones van de 0->... por tanto 0 es la primera opcion que es si a borrar7
				DatabaseHandler DbConnector = new DatabaseHandler();
				DbConnector.setUserID(Integer.parseInt(eventoRelacionado.userID));
				EventoControl.removeEvent(DbConnector, Integer.parseInt(eventoRelacionado.eventID)); 
				JDialog topFrame = (JDialog) this.getTopLevelAncestor(); //NOTA: Esto petara si estas testeando solo, es porque el testeo se hace en una JFrame y main_GUI llama a esto como un JDialog...
				alphaTimer.stop();
				topFrame.dispose();
			}
		}
		else if (command.equals("Visita nuestra página para mas!")){
			goToUrl(regalatorPage);
		}
	}
	
	public void initcomponents(int dias, String regalo){
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(25,25));
		imgPanel = new ImgPanel();
		imgPanel.setPreferredSize(new Dimension(imgFadeOut.getWidth(), imgFadeOut.getHeight()));
		 //Randomizar, añadir mas frases...
		if (dias <= 0){
			text1 = new JLabel("Hoy es el dia de tu evento: " + eventoRelacionado.descripcion, SwingConstants.CENTER);
		} else if (dias == 1){
			text1 = new JLabel("Queda " + dias + " dia para tu evento: " , SwingConstants.CENTER);
		} else {
			text1 = new JLabel("Quedan " + dias + " dias para tu evento: ", SwingConstants.CENTER);
		}
		JLabel eventoTexto = new JLabel(eventoRelacionado.descripcion, SwingConstants.CENTER);
		text1.setForeground(Color.BLACK);
		text1.setFont(new Font("Century Schoolbook L",Font.BOLD,22));
		eventoTexto.setForeground(Color.BLACK);
		eventoTexto.setFont(new Font("Century Schoolbook L",Font.BOLD,22));
		if (regalo.equals("Ninguno")){
			text2 = new JLabel("No hemos podido encontrar un regalo adecuado!", SwingConstants.LEFT);
			text3 = new JLabel("");
			text4 = new JLabel("");
		} else {
			text2 = new JLabel("Te recomendamos que regales: " + regalo, SwingConstants.LEFT); //Mejorar presentacion cuando rule
			text3 = new JLabel("<html><a href=#>En esta pagina encontraras las mejores ofertas</a></html>", SwingConstants.LEFT); //link vacio porque no usa el link, usa el evento mouseclick
			text4 = new JLabel("<html><a href=#>Y en esta pagina tambien</a></html>", SwingConstants.LEFT); //link vacio porque no usa el link, usa el evento mouseclick
			text3.setCursor(new Cursor(Cursor.HAND_CURSOR));
			text3.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					goToUrl(firstURL);
				}
			});
			text4.setCursor(new Cursor(Cursor.HAND_CURSOR));
			text4.addMouseListener(new MouseAdapter(){
				public void mouseClicked(MouseEvent e){
					goToUrl(secondURL);
				}
			});
		}
		
		text2.setFont(new Font("Century Schoolbook L",Font.ITALIC|Font.BOLD,21));
		text2.setForeground(Color.RED);
		
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new GridLayout(2,0,10,10));
		titlePanel.add(text1);
		titlePanel.add(eventoTexto);
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(3,0,15,15));
		textPanel.add(text2);
		textPanel.add(text3);
		textPanel.add(text4);
		JPanel textAndIconsPanel = new JPanel();
		JPanel iconsPanel = new JPanel();
		textAndIconsPanel.setLayout(new GridLayout(1,3,20,20));
		iconsPanel.setLayout(new BorderLayout(10,10));
		iconsPanel.add(new JLabel(" "),BorderLayout.NORTH);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout(10,10));
		JButton borraButton = new JButton("Borra el evento");
		borraButton.addActionListener(this);
		buttonPanel.add(borraButton ,BorderLayout.CENTER); //Falta implementar la accion de borrar el evento (facil, esta el metodo hecho)
		buttonPanel.add(new JLabel("                               "), BorderLayout.EAST); //Manera cutre de poner la posicion relativa
		buttonPanel.add(new JLabel("                               "), BorderLayout.WEST); //Mejor recrear y usar GroupLayout en el futuro...
		buttonPanel.add(new JLabel("   "), BorderLayout.NORTH);
		iconsPanel.add(buttonPanel, BorderLayout.CENTER);
		JButton visitButton = new JButton("Visita nuestra página para mas!");
		visitButton.addActionListener(this);
		iconsPanel.add(visitButton,BorderLayout.SOUTH);
		textAndIconsPanel.add(textPanel);
		textAndIconsPanel.add(iconsPanel);
		mainPanel.add(titlePanel,BorderLayout.NORTH);
		mainPanel.add(textAndIconsPanel, BorderLayout.SOUTH);
		mainPanel.add(imgPanel, BorderLayout.CENTER);
		add(mainPanel);
	}
	
	private static void goToUrl(String newUrl)
	{
		try {
			if (newUrl.equals("")){ throw new Exception("pagina no encontrada");}
			Desktop.getDesktop().browse(new URI(newUrl));
		}
		catch(Exception error){
			System.out.println("Problemas al cargar la pagina: " + error.getMessage());
		}
	}

	
	public static void main(String[] args) {
		JFrame window = new JFrame("");
		EventData evento = new EventData("1");
		evento.eventID = "5";
		evento.descripcion = "BODA JUANAAAA";
		String[] regalo = {"Ninguno", "brazalete.jpg","https://www.google.es/#q=Lol+Internet",""};
		RegaloPanel content = new RegaloPanel(evento, 3, regalo);
		//BODA JUANaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa 45 char (max)
		window.setContentPane(content);
		window.setVisible(true);
		window.pack();
		int standardSizeX = 900; //Pasar a constante en el futuro
		int standardSizeY = 670;
		window.setSize(standardSizeX,standardSizeY);
		window.setLocation(200, 000);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
