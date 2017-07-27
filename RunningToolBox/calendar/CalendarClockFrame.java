package RunningToolBox.calendar;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.BorderFactory; 
import javax.imageio.ImageIO;
import java.io.*;
import java.util.Calendar;

public class CalendarClockFrame extends JFrame {	
	private JPanel contentPane;
	private JTextField noteField;
	private JButton clockButton = new JButton();
	private Box clockBox;
	private AddClockActionListener addClockActionListener = new AddClockActionListener();
	private DeleteClockActionListener deleteClockActionListener= new DeleteClockActionListener();
	
	private JComboBox sYearComboBox;
	private JComboBox sMonthComboBox;
	private JComboBox sDayComboBox;
	private JComboBox eYearComboBox;
	private JComboBox eMonthComboBox;
	private JComboBox eDayComboBox;
	
	private Box clockDataBox;
	private JComboBox cDaycomboBox;
	private JComboBox cTimeHcomboBox;
	private JComboBox cTimeMcomboBox;
	private JComboBox cAPMcomboBox;
	private JLabel noteLabel;
	
	private String filePath = "RunningToolBox/calendar/calendarNote/";
	
	String[] clockDayStr = {"事件開始時", "一天前", "兩天前", "三天前", "四天前", "五天前"};
	String[] clockTimeHStr = {"12", "01", "02", "03", "04", "05", 
							  "06", "07", "08", "09", "10", "11"};
	String[] clockTimeMStr = {"00", "05", "10", "15", "20", "25", 
							  "30", "35", "40", "45", "50", "55"};
	String[] clockAPMStr = {"AM", "PM"};
	String[] yearStr = new String[5];
	String[] monthStr = {"一月", "二月", "三月", "四月", "五月", "六月"
						, "七月", "八月", "九月", "十月", "十一月", "十二月"};
	String[] dayStr;
	int sMonthLastDay[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	int eMonthLastDay[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	int year, month, day;
	int startYear, startMonth, startDay;
	int endYear, endMonth, endDay;
	int selectYear, selectMonth, selectDay; 
	
	boolean notifyFlag;
	
	int frameWidth = 410, frameHeight = 212;
	int clockHeight = 80;
	int clockCount = 0, hideClockCount = 0;
	
	public CalendarClockFrame(int year, int month, int day, int SiteX, int SiteY)
	{
		super("Add Note - Running ToolBox");
		
		this.year = startYear = endYear = year;
		this.month = startMonth = endMonth = month;
		this.day = startDay = endDay = day;
		
		//設定閏年
		if( year%400 == 0 || ( !(year%100 == 0) && (year%4 == 0) ) )
			sMonthLastDay[1] = eMonthLastDay[1] = 29;
		else
			sMonthLastDay[1] = eMonthLastDay[1] = 28;
		
		contentSet();
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int frameX = 0;
		if(SiteX <  screenSize.width/2)	//在畫面左側則往右長
			frameX = SiteX + 210 + 30;
		else 							//在畫面右側則往左長
			frameX = SiteX - frameWidth - 30;
		setBounds(frameX, SiteY - frameHeight/2, frameWidth, frameHeight);
		setResizable(false);
		setVisible(true);
	}
	
	private void contentSet()
	{		
		contentPane = new JPanel();
		contentPane.setBorder( new EmptyBorder(10, 10, 10, 10) );
		contentPane.setLayout( new BorderLayout(0, 10) );
		setContentPane(contentPane);
		
		JPanel borderPanel = new JPanel(new BorderLayout());
		borderPanel.setBorder( BorderFactory.createRaisedBevelBorder() );
		borderPanel.setBackground(Color.white);
		
		JPanel northPanel = new JPanel(new BorderLayout(10, 0));
		northPanel.setBorder( new EmptyBorder(10, 10, 10, 10) );
		northPanel.setBackground(Color.white);
		
		//左側文字
		JPanel labelPanel = new JPanel(new GridLayout(3, 1, 0, 5));
		labelPanel.setBackground(Color.white);
		
		JLabel noteLabel = new JLabel("Note :");
		noteLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
		labelPanel.add(noteLabel);
		
		JLabel startLabel = new JLabel("開始日期 :");
		startLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
		labelPanel.add(startLabel);
		
		JLabel endLabel = new JLabel("結束日期 :");
		endLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
		labelPanel.add(endLabel);
		northPanel.add(labelPanel, BorderLayout.WEST);
		
		//右側
		JPanel dataPanel = new JPanel(new GridLayout(3, 1, 0, 5));
		dataPanel.setBackground(Color.white);
		noteField = new JTextField("", 50);
		dataPanel.add(noteField);
		
		//右側選單
		Box startBox = Box.createHorizontalBox();
		//JPanel startPanel = new JPanel(new FlowLayout());
		for(int i = 0 ; i < 5 ; i++)
		{
			yearStr[i] = Integer.toString( year + i );
		}
		sYearComboBox = new JComboBox(yearStr);
		sYearComboBox.setBackground(Color.white);
		startBox.add(sYearComboBox);
		startBox.add(Box.createHorizontalStrut(5));
		
		sMonthComboBox = new JComboBox(monthStr);
		sMonthComboBox.setBackground(Color.white);
		sMonthComboBox.setSelectedItem(monthStr[month-1]);
		startBox.add(sMonthComboBox);
		startBox.add(Box.createHorizontalStrut(5));
		
		dayStr = new String[sMonthLastDay[month-1]];
		for(int i = 0 ; i < sMonthLastDay[month-1] ; i++)
		{
			dayStr[i] = Integer.toString( i + 1 );
		}
		sDayComboBox = new JComboBox(dayStr);
		sDayComboBox.setBackground(Color.white);
		sDayComboBox.setSelectedItem(Integer.toString( day ));
		startBox.add(sDayComboBox);
		startBox.add(Box.createHorizontalGlue());
		
		sYearComboBox.addActionListener(new ComboTimeActionListener('S', 1));
		sMonthComboBox.addActionListener(new ComboTimeActionListener('S', 2));
		sDayComboBox.addActionListener(new ComboTimeActionListener('S', 3));
		dataPanel.add(startBox);
		
		Box endBox = Box.createHorizontalBox();
		//JPanel endPanel = new JPanel(new FlowLayout());
		for(int i = 0 ; i < 5 ; i++)
		{
			yearStr[i] = Integer.toString( year + i );
		}
		eYearComboBox = new JComboBox(yearStr);
		eYearComboBox.setBackground(Color.white);
		endBox.add(eYearComboBox);
		endBox.add(Box.createHorizontalStrut(5));
		
		eMonthComboBox = new JComboBox(monthStr);
		eMonthComboBox.setBackground(Color.white);
		eMonthComboBox.setSelectedItem(monthStr[month-1]);
		endBox.add(eMonthComboBox);
		endBox.add(Box.createHorizontalStrut(5));
		
		dayStr = new String[eMonthLastDay[month-1]];
		for(int i = 0 ; i < eMonthLastDay[month-1] ; i++)
		{
			dayStr[i] = Integer.toString( i + 1 );
		}
		eDayComboBox = new JComboBox(dayStr);
		eDayComboBox.setBackground(Color.white);
		eDayComboBox.setSelectedItem(Integer.toString( day ));
		endBox.add(eDayComboBox);
		endBox.add(Box.createHorizontalGlue());
		
		eYearComboBox.addActionListener(new ComboTimeActionListener('E', 1));
		eMonthComboBox.addActionListener(new ComboTimeActionListener('E', 2));
		eDayComboBox.addActionListener(new ComboTimeActionListener('E', 3));
		dataPanel.add(endBox);
		
		northPanel.add(dataPanel, BorderLayout.CENTER);
		borderPanel.add(northPanel, BorderLayout.CENTER);
		contentPane.add(borderPanel, BorderLayout.NORTH);
		
		//新增鬧鐘
		clockBox = Box.createVerticalBox();
		contentPane.add(clockBox, BorderLayout.CENTER);
		
		//南方按鈕
		Box buttonBox = Box.createHorizontalBox();
		
		try {
			BufferedImage img = ImageIO.read(getClass().getResource("image/addN.png"));
			Image dimg = img.getScaledInstance(20, 17, Image.SCALE_SMOOTH);
			clockButton.setIcon(new ImageIcon(dimg));
		} catch (IOException ex) {
			clockButton.setText("Add");
		}
		clockButton.addActionListener(addClockActionListener);
		buttonBox.add(clockButton);
		buttonBox.add(Box.createHorizontalStrut(5));
		
		JButton displayButton = new JButton("Example");
		displayButton.addActionListener(new DisplayActionListener());
		buttonBox.add(displayButton);
		buttonBox.add(Box.createHorizontalStrut(5));
		buttonBox.add(Box.createHorizontalGlue());
		
		//確定按鈕
		JButton safeButton = new JButton("儲存");
		//safeButton.setFont(buttonFont);
		safeButton.addActionListener(new ButtonActionListener('S'));
		buttonBox.add(safeButton);
		buttonBox.add(Box.createHorizontalStrut(5));
		//取消按鈕
		JButton cancelButton = new JButton("取消");
		//cancelButton.setFont(buttonFont);
		cancelButton.addActionListener(new ButtonActionListener('C'));
		buttonBox.add(cancelButton);
		buttonBox.add(Box.createHorizontalStrut(5));

		contentPane.add(buttonBox, BorderLayout.SOUTH);
	}
	
	public class ComboTimeActionListener implements ActionListener
	{
		private char ch;
		private int Field;
		public ComboTimeActionListener(char ch, int Field)
		{
			this.ch = ch;
			this.Field = Field;
		}
		
		public void actionPerformed(ActionEvent e) 
		{				
			if(ch == 'S'){
				startYear = sYearComboBox.getSelectedIndex() + year;
				startMonth = sMonthComboBox.getSelectedIndex() + 1;
				startDay = sDayComboBox.getSelectedIndex() + 1;
				//System.out.println(" " + startYear + "/" + startMonth + "/" + startDay);
				switch(Field)
				{
				case 1:	//年
					if( startYear%400 == 0 || ( !(startYear%100 == 0) && (startYear%4 == 0) ) )
						sMonthLastDay[1] = 29;
					else
						sMonthLastDay[1] = 28;
					
					//現在是二月
					if(startMonth == 2)
					{
						dayStr = new String[sMonthLastDay[1]];
						for(int i = 0 ; i < sMonthLastDay[1] ; i++)
						{
							dayStr[i] = Integer.toString( i + 1 );
						}
						sDayComboBox.setModel( new DefaultComboBoxModel(dayStr) );
						sDayComboBox.revalidate();
					}
					break;
					
				case 2:	//月
					dayStr = new String[sMonthLastDay[startMonth-1]];
					for(int i = 0 ; i < sMonthLastDay[startMonth-1] ; i++)
					{
						dayStr[i] = Integer.toString( i + 1 );
					}
					sDayComboBox.setModel( new DefaultComboBoxModel(dayStr) );
					sDayComboBox.revalidate();
					break;
				}
				//若設定後月份的最大日期比原先選擇的小，則拉回範圍內
				if(startDay > sMonthLastDay[startMonth-1])
					sDayComboBox.setSelectedIndex(sMonthLastDay[startMonth-1]-1);
				else
					sDayComboBox.setSelectedIndex(startDay-1);
				
				//有開啟提醒，則自動調整時間
				if(notifyFlag)
					refreshClock();
			}
			
			//若結束日期較小，跟開始日期對準
			if(sYearComboBox.getSelectedIndex() > eYearComboBox.getSelectedIndex())
				eYearComboBox.setSelectedIndex(sYearComboBox.getSelectedIndex());
			if(sYearComboBox.getSelectedIndex() == eYearComboBox.getSelectedIndex())
			{
				if(sMonthComboBox.getSelectedIndex() > eMonthComboBox.getSelectedIndex())
					eMonthComboBox.setSelectedIndex(sMonthComboBox.getSelectedIndex());
				if(sMonthComboBox.getSelectedIndex() == eMonthComboBox.getSelectedIndex())
				{
					if(sDayComboBox.getSelectedIndex() > eDayComboBox.getSelectedIndex())
						eDayComboBox.setSelectedIndex(sDayComboBox.getSelectedIndex());
				}
			}
			
			if(ch == 'E')
			{
				endYear = eYearComboBox.getSelectedIndex() + year;
				endMonth = eMonthComboBox.getSelectedIndex() + 1;
				endDay = eDayComboBox.getSelectedIndex() + 1;
				//System.out.println(" " + endYear + "/" + endMonth + "/" + endDay);
				switch(Field)
				{
				case 1:	//年
					if( endYear%400 == 0 || ( !(endYear%100 == 0) && (endYear%4 == 0) ) )
						eMonthLastDay[1] = 29;
					else
						eMonthLastDay[1] = 28;
					
					//現在是二月
					if(endMonth == 2)
					{
						dayStr = new String[eMonthLastDay[1]];
						for(int i = 0 ; i < eMonthLastDay[1] ; i++)
						{
							dayStr[i] = Integer.toString( i + 1 );
						}
						eDayComboBox.setModel( new DefaultComboBoxModel(dayStr) );
						eDayComboBox.revalidate();
					}
					break;
					
				case 2:	//月
					dayStr = new String[eMonthLastDay[endMonth-1]];
					for(int i = 0 ; i < eMonthLastDay[endMonth-1] ; i++)
					{
						dayStr[i] = Integer.toString( i + 1 );
					}
					eDayComboBox.setModel( new DefaultComboBoxModel(dayStr) );
					eDayComboBox.revalidate();
					break;
				}
				
				//若設定後月份的最大日期比原先選擇的小，則拉回範圍內
				if(endDay > eMonthLastDay[endMonth-1])
					eDayComboBox.setSelectedIndex(eMonthLastDay[endMonth-1]-1);
				else
					eDayComboBox.setSelectedIndex(endDay-1);
			}
		}
	}	
	
	public class DisplayActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			try {
				BufferedImage img = ImageIO.read(getClass().getResource("image/TrayLogo.png"));
				Image dimg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
				final TrayIcon trayIcon = new TrayIcon(dimg, "Running ToolBox");
				// get the SystemTray instance
				if (SystemTray.isSupported()) {
					SystemTray tray = SystemTray.getSystemTray();
					trayIcon.setImageAutoSize(true);
					try {
						tray.add(trayIcon);
						trayIcon.displayMessage("" + year + "/" + month + "/" + day,
												"Note: Running Toolbox Display.", TrayIcon.MessageType.INFO);
					} catch (AWTException ae) {
						System.err.println("TrayIcon could not be added.");
					}
				}
			} catch (IOException ex) {
				System.out.println("Can't get cross_left.png");
			}
		}
	}
		
	public class AddClockActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			if(clockCount == 0){
				JPanel clockPanel = new JPanel();
				clockPanel.setBorder( new EmptyBorder(5, 10, 5, 10) );
				
				Box clockDataBox = Box.createHorizontalBox();
				
				cDaycomboBox = new JComboBox(clockDayStr);
				cDaycomboBox.addActionListener(new ClockTimeActionListener('D'));
				clockDataBox.add(cDaycomboBox);
				clockDataBox.add(Box.createHorizontalStrut(5));
				
				//預設為 事件開始時，不可選時間
				cTimeHcomboBox = new JComboBox(clockTimeHStr);
				cTimeHcomboBox.setEnabled(false);
				cTimeHcomboBox.addActionListener(new ClockTimeActionListener('T'));
				clockDataBox.add(cTimeHcomboBox);
				clockDataBox.add(Box.createHorizontalStrut(5));
				
				//預設為 事件開始時，不可選時間
				cTimeMcomboBox = new JComboBox(clockTimeMStr);
				cTimeMcomboBox.setEnabled(false);
				cTimeMcomboBox.addActionListener(new ClockTimeActionListener('T'));
				clockDataBox.add(cTimeMcomboBox);
				clockDataBox.add(Box.createHorizontalStrut(5));
				
				cAPMcomboBox = new JComboBox(clockAPMStr);
				cAPMcomboBox.setEnabled(false);
				cAPMcomboBox.addActionListener(new ClockTimeActionListener('A'));
				clockDataBox.add(cAPMcomboBox);
				clockDataBox.add(Box.createHorizontalStrut(5));
				
				String timeStr = "" + startYear + "/ " + startMonth + "/ " + startDay + " - 12:00AM";
				noteLabel = new JLabel(timeStr);
				clockDataBox.add(noteLabel);
				clockDataBox.add(Box.createHorizontalGlue());
				
				clockPanel.add(clockDataBox, BorderLayout.CENTER);
				clockBox.add(clockPanel);
				clockBox.setBorder( BorderFactory.createTitledBorder("Notify") );
				
				selectYear = startYear;
				selectMonth = startMonth;
				selectDay = startDay;
				
				notifyFlag = true;
				clockCount++;
				setSize(frameWidth, frameHeight + clockHeight);
				
				try {
					BufferedImage img = ImageIO.read(getClass().getResource("image/deleteN.png"));
					Image dimg = img.getScaledInstance(17, 17, Image.SCALE_SMOOTH);
					clockButton.setIcon(new ImageIcon(dimg));
				} catch (IOException ex) {
					clockButton.setText("Delete");
				}
				clockButton.removeActionListener(addClockActionListener);
				clockButton.addActionListener(deleteClockActionListener);
			}
		}
	}
	
	public class DeleteClockActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			if(clockCount != 0)
			{
				clockBox.remove(0);		//刪除按鈕
				clockBox.setBorder( null );

				notifyFlag = false;
				clockCount--;
				setSize(frameWidth, frameHeight);
				
				try {
					BufferedImage img = ImageIO.read(getClass().getResource("image/addN.png"));
					Image dimg = img.getScaledInstance(20, 17, Image.SCALE_SMOOTH);
					clockButton.setIcon(new ImageIcon(dimg));
				} catch (IOException ex) {
					clockButton.setText("Add");
				}
				clockButton.removeActionListener(deleteClockActionListener);
				clockButton.addActionListener(addClockActionListener);
			}
		}
	}
	
	public class ClockTimeActionListener implements ActionListener
	{
		private char ch;
		public ClockTimeActionListener(char ch)
		{
			this.ch = ch;
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			//設定日期，調整時間是否顯示
			if(ch == 'D')
			{
				if(cDaycomboBox.getSelectedIndex() == 0)	//如果選擇 事件開始前
				{	//回到預設選項，並且不可開啟下拉選單
					cTimeHcomboBox.setSelectedIndex(0);
					cTimeHcomboBox.setEnabled(false);
					cTimeMcomboBox.setSelectedIndex(0);
					cTimeMcomboBox.setEnabled(false);
					cAPMcomboBox.setSelectedIndex(0);
					cAPMcomboBox.setEnabled(false);
				}else{
					cTimeHcomboBox.setEnabled(true);
					cTimeMcomboBox.setEnabled(true);
					cAPMcomboBox.setEnabled(true);
				}
			}
			
			refreshClock();
		}
	}
	
	public void refreshClock()
	{
		String timeStr = "";
		if(cDaycomboBox.getSelectedIndex() == 0)	//日期為開始時間
		{
			selectYear = startYear;
			selectMonth = startMonth;
			selectDay = startDay;
		}else{
			int comboDay = cDaycomboBox.getSelectedIndex();
			selectYear = startYear;
			selectMonth = startMonth;
			selectDay = startDay;
			for(int count = 0; count < comboDay ; count++)
			{
				if(selectDay > 1)
				{
					selectDay--;
				}else if(selectMonth > 1)
				{
					selectDay = sMonthLastDay[selectMonth - 2];
					selectMonth--;
				}else{
					selectMonth = 12;
					selectDay = sMonthLastDay[selectMonth - 1];
					selectYear--;
				}
			}
			//System.out.println("DAY_OF_YEAR " + selectDay);
		}
		
		timeStr += selectYear + "/ " + selectMonth + "/ " + selectDay + " - ";
		timeStr = timeStr + clockTimeHStr[cTimeHcomboBox.getSelectedIndex()]
					+ ":" + clockTimeMStr[cTimeMcomboBox.getSelectedIndex()];
		timeStr = timeStr + clockAPMStr[cAPMcomboBox.getSelectedIndex()];
		noteLabel.setText(timeStr);
	}
	
	public class ButtonActionListener implements ActionListener
	{
		private char value;
		public ButtonActionListener(char buttonValue)
		{
			value = buttonValue;
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			if(value == 'S')
				saveFile();
			dispose();
		}
	}
	
	public void saveFile()
	{
		String note = noteField.getText();
		
		//把note加到 開始時間 與 結束時間 檔案中
		if(!note.equals(""))
		{
			String titletext = "" + startYear + "/" + startMonth + "/" + startDay + 
							" - " + endYear + "/" + endMonth + "/" + endDay + "\n";
			String text = "\n-------------------------\n" + titletext + note;
			//System.out.println(text);

			String sfilename = "" + startYear + "_" + startMonth + "_" + startDay;
			String efilename = "" + endYear + "_" + endMonth + "_" + endDay;

			try{
				BufferedWriter file = new BufferedWriter( new FileWriter(filePath + sfilename + ".txt", true));//啟用緩衝區寫入
				file.write(text);
				file.flush();//將緩衝區資料寫到檔案
			} catch (IOException except){
				except.printStackTrace();
			}
			//不同檔案才進行寫入
			if(!efilename.equals(sfilename))
			{
				try{
					BufferedWriter file = new BufferedWriter( new FileWriter(filePath + efilename + ".txt", true));//啟用緩衝區寫入
					file.write(text);
					file.flush();//將緩衝區資料寫到檔案
				} catch (IOException except){
					except.printStackTrace();
				}
			}
		}
		//把提醒加到檔案中
		if(notifyFlag){
			String notifyStr = "" + selectYear;
						//若月份或天數僅一個數字，自動補0
			notifyStr += ((selectMonth/10 == 0)? "/0" : "/") + selectMonth;
			notifyStr += ((selectDay/10 == 0)? "/0" : "/") + selectDay + " ";
			notifyStr += clockTimeHStr[cTimeHcomboBox.getSelectedIndex()] + ":" + 
						 clockTimeMStr[cTimeMcomboBox.getSelectedIndex()] + " " + 
						 clockAPMStr[cAPMcomboBox.getSelectedIndex()] + " " + 
						 startYear + "/" + startMonth + "/" + startDay + " " + note + "\n";
			try{
				BufferedWriter file = new BufferedWriter( new FileWriter(filePath + "notification.txt", true));//啟用緩衝區寫入
				file.write(notifyStr);
				file.flush();//將緩衝區資料寫到檔案
			} catch (IOException except){
				except.printStackTrace();
			}
		}
		
		System.gc();
		
		/*
		RandomAccessFile file = new RandomAccessFile(new File(filePath + filename + ".txt"), "rw");
		file.seek(0); // to the beginning
		file.write(text.getBytes());
		file.close();*/
	}
}