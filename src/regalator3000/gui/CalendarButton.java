package regalator3000.gui;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class CalendarButton extends JButton{
	
	private int eventID;
	
	public CalendarButton(String name, int id){
		super(name);
		this.eventID = id;
	}
	
	public int getEventID(){
		return this.eventID;
	}

}
