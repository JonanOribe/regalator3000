/*15/04: Ideas -> hacer una label separada con los links, usar JLabel.setCursor para que el cursor cambie de forma al clickarla, meterle
 * mouseListener como lambda function a esa label.
 */
package regalator3000.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


@SuppressWarnings("serial")
public class RegaloPanel extends JPanel{

	public static final String currentDir = System.getProperty("user.dir") + File.separator + "imgs" + File.separator;
	private BufferedImage regaloImg;
	ImgPanel imgPanel;
	JLabel text1,text2,text3,text4;
	String firstURL, secondURL;
	
	public RegaloPanel(String imgSrc,String firstUrl,String secondUrl){
		super();
		try{
			regaloImg = ImageIO.read(new File(currentDir + imgSrc));
		}
		catch(Exception e){
			System.out.println("Error al cargar la imagen. " + e.getMessage());
		}
		firstURL = firstUrl;
		initcomponents();
	}
	
	private class ImgPanel extends JPanel{ //Unnecessary with different planning but eh
		//añadir timers i animaciones en el futuro...
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			g.drawImage(regaloImg, 0, 0, this.getWidth(), this.getHeight(), this);
		}
	}
	
	public void initcomponents(){
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(25,25));
		imgPanel = new ImgPanel();
		//Comprobar que la imagen no sea demasiado grande (tamanyo parent - 100 por lado maximo no?)
		imgPanel.setPreferredSize(new Dimension(regaloImg.getWidth(), regaloImg.getHeight()));
		text1 = new JLabel("Se aproxima una fecha señalada: {descripcion aqui}", SwingConstants.CENTER);
		text2 = new JLabel("Te recomendamos que regales: {regalos aqui}", SwingConstants.LEFT); //Mejorar presentacion cuando rule
		text3 = new JLabel("<html><a href=#>En esta pagina encontraras las mejores ofertas</a></html>", SwingConstants.LEFT); //link vacio porque no usa el link, usa el evento mouseclick
		text4 = new JLabel("<html><a href=#>Y en esta pagina tambien</a></html>", SwingConstants.LEFT); //link vacio porque no usa el link, usa el evento mouseclick
		text3.setCursor(new Cursor(Cursor.HAND_CURSOR));
		text3.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				try {
					Desktop.getDesktop().browse(new URI(firstURL));
				}
				catch(Exception error){
					System.out.println("Problemas con tu navegador: " + error.getMessage());
				}
			}
		});

		JPanel titlePanel = new JPanel();
		titlePanel.add(text1);
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new GridLayout(3,0,15,15));
		textPanel.add(text2);
		textPanel.add(text3);
		textPanel.add(text4);
		mainPanel.add(titlePanel,BorderLayout.NORTH);
		mainPanel.add(textPanel, BorderLayout.SOUTH);
		mainPanel.add(imgPanel, BorderLayout.CENTER);
		add(mainPanel);
	}
	

	
	public static void main(String[] args) {
		JFrame window = new JFrame("");
		RegaloPanel content = new RegaloPanel("img01.jpg","https://www.google.es/#q=Lol+Internet","");
		window.setContentPane(content);
		window.setVisible(true);
		window.pack();
		window.setSize(600,600);
		window.setLocation(200, 100);
	}

}
