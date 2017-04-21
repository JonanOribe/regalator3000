/*15/04: Ideas -> hacer una label separada con los links, usar JLabel.setCursor para que el cursor cambie de forma al clickarla, meterle
 * mouseListener como lambda function a esa label.
 * 20/04 -> Poner iconos tb, i 3 links en total (el segundo opcional, el tercero siempre a una pagina nuestra)
 * Cambios para proposal_GUI -> eliminar objetos de las combobox al seleccionarlos, que regalo concreto tenga en cuenta la eleccion ya hecha para la lista (demasiadas llamadas a la BBDD? pensar)
 * +
 */
package regalator3000.gui;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import regalator3000.misc.EventData;


@SuppressWarnings("serial")
public class RegaloPanel extends JPanel{
	
	private static final int maxWidth = 800;
	private static final int maxHeight = 600;

	public static final String currentDir = System.getProperty("user.dir") + File.separator + "imgs" + File.separator;
	private BufferedImage regaloImg; //regaloImg2;NOTE: change them to only have a couple of them in memory, even if you're showing more
	private Image imgLoader;
	ImgPanel imgPanel;
	JLabel text1,text2,text3,text4;
	String firstURL, secondURL;
	EventData eventoRelacionado;  //Para saber que evento relacionar con el regalo, por si hay que borrarlo o hacer otras cosas
	
	public RegaloPanel(EventData evento, int dias, String[] regaloData){
		super();
		eventoRelacionado = evento;
		try{
			if (regaloData[1].equals("")) {
				regaloData[1] = "img01.jpg"; //PONER AQUI IMAGEN DE NINGUN REGALO VALIDO EN EL FUTURO!
			}
			imgLoader = ImageIO.read(new File(currentDir + regaloData[1])); //O potser carregar les dues veure quina es mes gran i a partir daqui canviar tamanys...
			//System.out.println("Actual size: " + imgLoader.getWidth(this) + " , " + imgLoader.getHeight(this));
			int finalWidth = (imgLoader.getWidth(this) >= (maxWidth-20)) ? (maxWidth-20) : imgLoader.getWidth(this); //Comprobar tambe que no sigui massa petita?
			int finalHeight = (imgLoader.getHeight(this) >= (maxHeight-185)) ? (maxHeight-185) : imgLoader.getHeight(this);
			//System.out.println("Used size: " + finalWidth + " , " + finalHeight);
			regaloImg = getImgOfSize(imgLoader, finalWidth, finalHeight); //aixo o forçar totes un tamany pero cutre...
		}
		catch(Exception e){
			System.out.println("Error al cargar la imagen. " + e.getMessage());
		}
		firstURL = regaloData[2];
		secondURL = regaloData[3]; //O pasar esto a initComponents i ponerlo como parametro local ya veremos si hay que cambiarlo por eventos o no
		initcomponents(dias, regaloData[0]);
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
	
	private class ImgPanel extends JPanel{ //Unnecessary with different planning but eh
		//18/04 -> añadir timers i animaciones en el futuro...
		//Usa opacity per fade ins & outs (Alphacomposite + a timer)
		//El tamany es seteja al crear la imatge i prou

		public void paintComponent(Graphics g){
			Graphics2D g2d = (Graphics2D) g;
			super.paintComponent(g2d);
			int desiredWidth = (regaloImg.getWidth() >= this.getWidth()) ? this.getWidth() : regaloImg.getWidth();     //Haz la imagen del tamaño del JPanel si es mas grande o del tamaño de la imagen real si es mas pequeña
			int desiredHeight = (regaloImg.getHeight() >= this.getHeight()) ? this.getHeight() : regaloImg.getHeight();
			int positionX = (regaloImg.getWidth() >= this.getWidth()) ? 0 : (this.getWidth() - regaloImg.getWidth())/2; //Centra la imagen si es mas pequeña que el JPanel
			int positionY = (regaloImg.getHeight() >= this.getHeight()) ? 0 : (this.getHeight() - regaloImg.getHeight())/2;
			g2d.drawImage(regaloImg, positionX, positionY, desiredWidth,desiredHeight, this);
			g2d.setStroke(new BasicStroke(10));
			g2d.drawRect(positionX, positionY, desiredWidth, desiredHeight);
		}
	}
	
	public void initcomponents(int dias, String regalo){
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(25,25));
		imgPanel = new ImgPanel();
		imgPanel.setPreferredSize(new Dimension(regaloImg.getWidth(), regaloImg.getHeight()));
		 //Randomizar, añadir mas frases...
		if (dias <= 0){
			text1 = new JLabel("Hoy es el dia! " + eventoRelacionado.descripcion, SwingConstants.CENTER);
		} else if (dias == 1){
			text1 = new JLabel("Queda " + dias + " dia para tu evento: " + eventoRelacionado.descripcion, SwingConstants.CENTER);
		} else {
			text1 = new JLabel("Quedan " + dias + " dias para tu evento: " + eventoRelacionado.descripcion, SwingConstants.CENTER);
		}
		text1.setForeground(Color.BLACK);
		text1.setFont(new Font("Century Schoolbook L",Font.BOLD,22));
		if (regalo.equals("Ninguno")){
			text2 = new JLabel("No hemos podido encontrar un regalo adecuado!", SwingConstants.LEFT);
		} else {
			text2 = new JLabel("Te recomendamos que regales: " + regalo, SwingConstants.LEFT); //Mejorar presentacion cuando rule
		}
		text2.setFont(new Font("Century Schoolbook L",Font.ITALIC|Font.BOLD,21));
		text2.setForeground(Color.RED);
		//Hacer lo mismo con las URLS, si es "" poner texto de no encontrado
		text3 = new JLabel("<html><a href=#>En esta pagina encontraras las mejores ofertas</a></html>", SwingConstants.LEFT); //link vacio porque no usa el link, usa el evento mouseclick
		text4 = new JLabel("<html><a href=#>Y en esta pagina tambien</a></html>", SwingConstants.LEFT); //link vacio porque no usa el link, usa el evento mouseclick
		text3.setCursor(new Cursor(Cursor.HAND_CURSOR));
		text3.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				goToUrl(firstURL);
			}
		});

		JPanel titlePanel = new JPanel();
		titlePanel.add(text1);
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(3,0,15,15));
		textPanel.add(text2);
		textPanel.add(text3);
		textPanel.add(text4);
		JPanel textAndIconsPanel = new JPanel();
		JPanel iconsPanel = new JPanel();
		textAndIconsPanel.setLayout(new GridLayout(1,3,10,10));
		iconsPanel.setLayout(new BorderLayout(10,10));
		iconsPanel.add(new JLabel(" "),BorderLayout.NORTH);
		iconsPanel.add(new JButton("Borra el evento"),BorderLayout.CENTER); //Toca hacer una action
		iconsPanel.add(new JButton("Visita nuestra página para mas!"),BorderLayout.SOUTH);
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
			Desktop.getDesktop().browse(new URI(newUrl));
		}
		catch(Exception error){
			System.out.println("Problemas con tu navegador: " + error.getMessage());
		}
	}

	
	public static void main(String[] args) {
		JFrame window = new JFrame("");
		EventData evento = new EventData("1");
		evento.descripcion = "BODA JUANAAAA";
		String[] regalo = {"Ninguno", "img01.jpg","https://www.google.es/#q=Lol+Internet",""};
		RegaloPanel content = new RegaloPanel(evento, 3, regalo);
		//BODA JUANaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa 45 char (max)
		window.setContentPane(content);
		window.setVisible(true);
		window.pack();
		int standardSizeX = 900; //Pasar a constante en el futuro
		int standardSizeY = 670;
		window.setSize(standardSizeX,standardSizeY);
		window.setLocation(200, 000);
	}

}
