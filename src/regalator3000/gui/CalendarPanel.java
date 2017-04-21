package regalator3000.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import regalator3000.misc.AuxFunctions;
import regalator3000.misc.EventData;

@SuppressWarnings("serial")
public class CalendarPanel extends JPanel{   
	//Constantes con los nombres de dias/meses etc.
	public static final String[] dayNames_ESP = {"Domingo","Lunes","Martes","Miércoles","Jueves","Viernes","Sábado"};
	public static final String[] dayNames_short_ESP = {"Lun.","Mar.","Mié.","Jue.","Vie.","Sáb.","Dom."};
	public static final String[] monthNames_ESP = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
    public static final String[] dayNames_EN = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"}; 
    public static final String[] dayNames_short_EN = {"mon","tu","wed","th","fr","sat","sun"}; 
    public static final String[] monthNames_EN = {"January", "February","March","April","May","June","July","August","September","October","November","December"};
    private static Font daysFont = new Font("Helvetica", Font.BOLD,14);
    private ActionListener parentPanel;
    public static Calendar myCalendar; 
    private ArrayList<EventData> userEvents;
   
    /*Constructor, it starts the program with a calendar set to the actual day,
     * Parent és usat per enviarli engancharli els actionlisteners dapretar un boto amb events*/
    public CalendarPanel(ActionListener parent, ArrayList<EventData> eventos, int startingMonth, int startingYear){ 
        myCalendar = Calendar.getInstance();
        Date today = new Date(); //La inicialitza a ara
        myCalendar.setTime(today);
        if (startingMonth >= 0 && startingMonth <= 11) {
        	myCalendar.set(Calendar.MONTH, startingMonth);
        }
        if (startingYear >= 1900 && startingMonth <= 9999) { //Might come to bite me in the ass in 10.000 AC...
            myCalendar.set(Calendar.YEAR, startingYear);
        }
        this.userEvents = eventos;
        this.parentPanel = parent;
        setupMonth();
    }
   
    /*It sets up the current month data as set by the actual calendar instance in the object which can be changed by some methods,
     * it then calls the drawing method     */
    public void setupMonth(){ //date = "yyyy-mm-dd , format standard de LocalDate.now();
        try{            
            myCalendar.set(Calendar.DAY_OF_MONTH, 2); //sets it up as the first day of the month, funciona pero mirar pq 2 i no 1...?
            int panelsToDrawToTheLeft;
            int dayNumber = myCalendar.get(Calendar.DAY_OF_WEEK); //1 -> sab; 2 -> dom; 3 -> lunes etc
            switch (dayNumber) {
            case 1: case 2:
                panelsToDrawToTheLeft = dayNumber + 4;
                break;
            default:
                panelsToDrawToTheLeft = dayNumber - 3;
            }
        int currentMonth = myCalendar.get(Calendar.MONTH);
        int currentYear = myCalendar.get(Calendar.YEAR);
        int lastMonth;
        //Amb calendar mesos de 0(gener) -> 11 (desembre)
        if (currentMonth == 0){
            lastMonth = 11;
        }
        else{
            lastMonth = (currentMonth - 1);
        }

        int lastMonthDays = AuxFunctions.getMonthLengthDays(lastMonth+1, currentYear); //Defined here for clarity, could put it inside the function call
        int daysThisMonth = AuxFunctions.getMonthLengthDays((currentMonth+1), currentYear);
        initComponents(panelsToDrawToTheLeft,lastMonthDays,daysThisMonth, currentMonth,currentYear); //Pq calendar da 0 para enero y nuestra funcion usa 1 para enero 
        }catch(Exception e){
        	System.out.println("Problema al generar el calendario: " + e.toString());
        }
    }

    
    /*Draws all the Calendar panel components for a given month and year*/
	public void initComponents(int drawnPanels, int daysLastMonth, int daysThisMonth, int month, int year){
        this.setLayout(new BorderLayout(5,5));
        JPanel calendar = new JPanel();
        JPanel northernPanel = new JPanel();
        
        Action increaseMonth = new AbstractAction(">") {
           public void actionPerformed(ActionEvent evt) { 
              changeDate(0,1);
           }
        };
        Action increaseYear = new AbstractAction(">") {
               public void actionPerformed(ActionEvent evt) { 
                   changeDate(1,1);
               }
            };
        Action decreaseMonth = new AbstractAction("<") {
            public void actionPerformed(ActionEvent evt) { 
                changeDate(0,0);
                   }
                };
        Action decreaseYear = new AbstractAction("<") {
            public void actionPerformed(ActionEvent evt) { 
                changeDate(1,0);
            }
        };
     
        northernPanel.setLayout(new GridLayout(1,2,5,5));
        JPanel northWestPanel = new JPanel();
        JPanel northEastPanel = new JPanel();
        northWestPanel.setLayout(new BorderLayout(5,5));
        northEastPanel.setLayout(new BorderLayout(5,5));
        northernPanel.add(northWestPanel);
        northernPanel.add(northEastPanel);
        northWestPanel.add(new JButton(decreaseMonth),BorderLayout.WEST);
        northWestPanel.add(new JLabel(monthNames_ESP[month],SwingConstants.CENTER),BorderLayout.CENTER);
        northWestPanel.add(new JButton(increaseMonth),BorderLayout.EAST);
        northEastPanel.add(new JButton(decreaseYear),BorderLayout.WEST);
        northEastPanel.add(new JLabel(Integer.toString(year),SwingConstants.CENTER),BorderLayout.CENTER);
        northEastPanel.add(new JButton(increaseYear),BorderLayout.EAST);
        
        JPanel firstRow = new JPanel();
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(5,5)); 
        firstRow.setLayout(new GridLayout(1,7));
        calendar.setLayout(new GridLayout(0,7,4,4)); //Mágia negra con GridLayouts(te lo llena forzando a llenar 7 columnas (1 por dia semana))
       
        JLabel dayN;
        for (int i = 0; i < dayNames_short_ESP.length; i++){ //Pone los nombres de los dias (lun mar,etc) en la primera fila
            dayN = new JLabel(dayNames_short_ESP[i], SwingConstants.CENTER);
            firstRow.add(dayN);
        }  
        
        int startingDay = daysLastMonth - drawnPanels + 1;
        if (startingDay > daysLastMonth) { //Hay que llenar toda la priemra semana con dias del anterior mes para llegar a 42 dias total entonces
        	startingDay = daysLastMonth - 6;
        }
        
        int dayCounter = 0;
        int normalizedMonthBottom = month, normalizedMonthUpper = month + 2;  //datos iniciales del mes pasado y el siguiente
        int normalizedYearUpper = year,normalizedYearBottom  = year;
        //Months de 0-11; => 11 + 2 = 13 = diciembre para el mes siguiente...
        if (normalizedMonthBottom == 0) { normalizedMonthBottom = 12; normalizedYearBottom--; }// Por si el mes actual es enero, hay que mirar el diciembre del año pasado como mes pasado
        if (normalizedMonthUpper >= 13) { normalizedMonthUpper = 1; normalizedYearUpper++; } //Por si el mes actual es diciembre, hay que mirar el enero del año siguiente como mes siguiente
        //System.out.println("Previo: " + normalizedMonthBottom + "  , " + normalizedYearBottom);
        //System.out.println("Siguiente: " + normalizedMonthUpper + "  , " + normalizedYearUpper);
        //System.out.println("Starting day: " + startingDay);
        dayCounter += fillMonthWithEvents(calendar,normalizedYearBottom, normalizedMonthBottom,  startingDay, daysLastMonth, true); //Llena los dias del mes anterior y les pone eventos
        dayCounter += fillMonthWithEvents(calendar, year, month + 1,  1, daysThisMonth, false); //Llena los dias del mes actual y les pone eventos
        fillMonthWithEvents(calendar, normalizedYearUpper, normalizedMonthUpper,  1, 42-dayCounter, true);//Llena los dias del mes siguiente y les pone eventos

        centerPanel.add(firstRow, BorderLayout.NORTH);
        centerPanel.add(calendar, BorderLayout.CENTER);
        this.add(northernPanel, BorderLayout.NORTH);
        this.add(centerPanel, BorderLayout.CENTER);
    }

    /*It creates all the buttons of the calendar panel for a given month/piece of month (it creates some greyed out buttons for next and past
     * month, it also sets up all the buttons with an event associated to them if they are in a drawn date     */
    private int fillMonthWithEvents(JPanel targetPanel, int year, int monthNumber, int startingMonthDay, int daysThisMonth,boolean greyDays){
        int[][] diasMes = getEventDataMonth(year, monthNumber);
        /*for (int i = 0; i < diasMes.length; i++){
        	System.out.println(Arrays.toString(diasMes[i]));
        }*/
        int fechaCounter = 0;
        JButton days;
        int daysCounter = 0; //Cuantos dias has puesto
        for (int i = startingMonthDay; i <= daysThisMonth; i++){ //Añade los dias del mes actual en color normal
        	//System.out.println("StartDay: " + startingMonthDay + " , " + daysThisMonth);
        	if (diasMes.length > 0 && fechaCounter < diasMes.length){
        		//System.out.println("Try to add day: " + diasMes[fechaCounter][1] + " , actualPos: " + i);
        			if (diasMes[fechaCounter][1] == i) { //Evento ese dia
        				days = createCalendarButton(this.userEvents.get(diasMes[fechaCounter][0]), i, monthNumber, greyDays);
        				fechaCounter++;
        			}
        			else if (diasMes[fechaCounter][1] < i){ //Para meses en los que comenzamos en fechas avanzadas (ej escribimos solo dias 24->30 porque solo cogemos ese cacho del mes, al estar la array de fechas ordenada buscamos la primera que coincide
        				fechaCounter++;
        				i-=1;
        				continue;
        			}
        			else { days = createCalendarButton(null, i, monthNumber, greyDays);  }
        	}
        	else { days = createCalendarButton(null, i, monthNumber, greyDays); }
        	days.setFont(daysFont);
        	days.addActionListener(parentPanel);
            targetPanel.add(days);
            daysCounter++;
        }
        return daysCounter;
    }
    
    /*Creates a normal calendar button or a button linked to an event with some of its data*/
    private JButton createCalendarButton(EventData evento, int dia, int month, boolean mesDiferente){
    	if (evento == null) { //Normal button, not linked to an event, maybe make it non clickable?
        	JButton button = new JButton(Integer.toString(dia));
        	if(mesDiferente){		
        		button.setForeground(Color.GRAY);
        		button.setBackground(Color.LIGHT_GRAY);
        	}
        	else {    	
        		button.setBackground(Color.GRAY);
        	}
        	return button;
    	}
    	else { //Button linked to an event
        	CalendarButton button = new CalendarButton(Integer.toString(dia), Integer.parseInt(evento.eventID));
	    	button.setToolTipText("<html><p>" +"Dia " + dia + " de " + monthNames_ESP[month-1] + ": "  + evento.descripcion + ".</p><p>" + "Avisará " + evento.diasAviso + " dias antes</p></html>");
	    	if (mesDiferente){
	        	button.setBackground(Color.LIGHT_GRAY);
	    		button.setForeground(Color.RED);
	    	}
	    	else {
		    	button.setBackground(Color.RED);
	    	}
	    	return button;
    	}
    }
    
    /*Returns an array containing, for all the events that happen in the selected month, its position in the
     * eventos array coupled with the corresponding day that the event happens that month, ordered by day 
     * f.ex: event array = event1 (23/04), event2 (15/04),event3(08/04) it'll return -> [ [2,8],[1,15],[0,23] ]  */
    private int[][] getEventDataMonth(int year, int month){
    	if (this.userEvents != null){ //Cambiar a  que si la arraylist esta vacia tp haga curro (10/04)
	    	int[][] fechas = new int[this.userEvents.size()][2];
    		int counter = 0;
	    	for (EventData evento : this.userEvents) {
	    		char splitter = evento.fecha.charAt(4); //El char despres de 2017X04...
	    		String[] valoresData =  evento.fecha.split(Character.toString(splitter));
	    		if(Integer.parseInt(valoresData[0]) == year){
		    		if (Integer.parseInt(valoresData[1]) == month) {
		    			fechas[counter][0] = counter;
		    			fechas[counter][1] = Integer.parseInt(valoresData[2]);
		    		}
	    		}
    			counter++;
	    	}
	    	return cleanMonthDaysArray(fechas);
    	}
    	else {
    		return null;
    	}
    }
    
    /*It works but maybe there's a way to reduce iteration number, shouldnt be a big concern though
     * First it removes all zeros in the created array of eventposition + dates and it sets the array
     * with just that data, then it orders the data by event date within the month in ascending order*/
    private int[][] cleanMonthDaysArray(int[][] arrayWithZeros){
    	int itemCounter = 0;
    	for (int i = 0; i < arrayWithZeros.length; i++){
    		if (arrayWithZeros[i][1] != 0) { 
    		      itemCounter++;
    		}
    	}
    	int[][] newArray = new int[itemCounter][2];  //Crea la nueva array sin zeros y la llena con los datos correctos
    	itemCounter = 0;
    	for (int i = 0; i < arrayWithZeros.length; i++){
    		if (arrayWithZeros[i][1] != 0) { 
	    		newArray[itemCounter][0] = arrayWithZeros[i][0];
	    		newArray[itemCounter][1] = arrayWithZeros[i][1];
	    		itemCounter++;
    		}
    	}
    	int tmp,tmp2;
    	for (int i = 0; i < newArray.length-1; i++){ //Ordena la nova array de menor a menor depenent del dia de l'event
    		if(newArray[i][1] > newArray[i+1][1]){
    			tmp = newArray[i][1];
    			tmp2 = newArray[i][0];
    			newArray[i][1] = newArray[i+1][1];
    			newArray[i][0] = newArray[i+1][0];
    			newArray[i+1][1] = tmp;
    			newArray[i+1][0] = tmp2;
    			i = -1;
    		}
    	} 
    	return newArray;
    }
    
    /*It changes the actual date in the calendar instance and calls the Calendar panel generating method
     * so that the Calendar GUI gets redrawn with the month/date changed accordingly*/
    public void changeDate(int monthOrYear, int incOrDec){ //Potser ferla mes general, en comptes de IncOrDec posar aqui el diferencial en mesos dies i enganxarho, pero weno k tp es massa important
        if(monthOrYear == 0) { //modify months;
            if(incOrDec == 0) { //decrease
                myCalendar.add(Calendar.MONTH, -1);
                if (parentPanel.getClass().equals(EventsPanel.class)){ //MANERA CUTRE DE FERHO, caldria remontar eventsPanel perque hi ha moltes maneres de fer el mateix que es podrien unificar y falta control de mesos/anys al tocar botons mes/any... (que saconsegueix amb aixo...)
              	   EventsPanel parent = (EventsPanel)parentPanel;
              	   parent.actualMonth--;
              	   if (parent.actualMonth <= 0){
              		 parent.actualMonth = 11;
              		 parent.actualYear--;
              	   }
                 }
            }
            else { //increase
                myCalendar.add(Calendar.MONTH, 1);
                if (parentPanel.getClass().equals(EventsPanel.class)){
              	   EventsPanel parent = (EventsPanel)parentPanel;
              	   parent.actualMonth++;
              	   if (parent.actualMonth > 11){
              		 parent.actualMonth = 0;
              		 parent.actualYear++;
              	   }
                 }
            }
        }
        else { //modify Years;
            if (incOrDec == 0) { //decrease
                myCalendar.add(Calendar.YEAR, -1); 
               if (parentPanel.getClass().equals(EventsPanel.class)){
            	   EventsPanel parent = (EventsPanel)parentPanel;
            	   parent.actualYear -=1;
               }
            }
            else { //increase
                myCalendar.add(Calendar.YEAR, 1);
                if (parentPanel.getClass().equals(EventsPanel.class)){
             	   EventsPanel parent = (EventsPanel)parentPanel;
             	   parent.actualYear +=1;
                }
            }
        }       
        redraw();
    }
    
    /*Returns the actual month, used in proposal_GUI, la verdad es que deberia haber implementado y usado estos metodos mas...*/
    public int getMonth(){
    	return myCalendar.get(Calendar.MONTH);
    }
    
    public void setMonth(int newValue){
    	myCalendar.set(Calendar.MONTH, newValue);
    }
    
    public int getYear(){
    	return myCalendar.get(Calendar.YEAR);
    }
    
    public void setYear(int newValue){
    	myCalendar.set(Calendar.YEAR, newValue);
    }
    
    public boolean isDayAnEvent(String date){
    	int year = AuxFunctions.getFieldFromDate(date,0);
    	int month = AuxFunctions.getFieldFromDate(date,1);
    	int day = AuxFunctions.getFieldFromDate(date,2);
    	String proposedDate = AuxFunctions.formatDateFromValues(year, month, day, "-");
    	for (int i = 0; i < userEvents.size(); i++){
    		if (proposedDate.equals(userEvents.get(i).fecha)){
    			return true;
    		}
    	}
    	return false;
    }
    
    public void redraw(){
    	this.removeAll();
    	setupMonth();
    	this.revalidate();
    }

    public static void main(String[] args) {
		/*DatabaseHandler DbConnector = new DatabaseHandler(); //En teoria estara creado fuera de tests...
		DbConnector.setUserID(1);
        ArrayList<EventData> eventosTest = EventoControl.getEvents(DbConnector);
        JFrame window = new JFrame("calendar test");
        CalendarPanel content = new CalendarPanel(null,eventosTest,-1,2017);
        window.setContentPane(content);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setPreferredSize(new Dimension(screenSize.width/3,screenSize.height/3)); //!improve this for weird resolutions, or just other types of resolutions
        window.setLocation(200, 200);
        window.setVisible(true);
        window.pack();*/



    }

}