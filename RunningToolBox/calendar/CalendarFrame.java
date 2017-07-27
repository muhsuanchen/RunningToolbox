package RunningToolBox.calendar;

import RunningToolBox.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import java.util.Calendar;
import javax.swing.plaf.*;
import javax.swing.table.JTableHeader;
import javax.imageio.ImageIO;
import static java.awt.GraphicsDevice.WindowTranslucency.*;
import java.io.*;

public class CalendarFrame extends JInternalFrame {
	static Point mouseDownCompCoords;
	final JSlider slider = new JSlider(100, 200, 140);
	
	private JPanel contentPane;
	private JPanel buttonPanel;
	
	private JScrollPane scrollPane;
	private JScrollPane noteScrollPane;
	private JTable calendarTable;
	private DefaultTableModel tblModel;
	private Box calendarBox;
	private JTextArea noteJTextArea;
	
	private JButton safeButton;
	private JButton deleteButton;
	private JButton preMonthButton;
	private JButton nextMonthButton;
	private JButton addNoteButton;
	private JButton settingButton;
	private Box colorBox;
	
	private JLabel yearMonthLabel;
	private JLabel noteLabel;
	
	private Component sliderStruct;
	private Component colorStruct;
	private int userColor = 0;
	private int userAlpha = 150;
	
	int[][] userBackgroundColor = {{0, 0, 0}, {50, 0, 0}, {0, 50, 0}, {0, 0, 50}, 
								   {200, 150, 150}, {150, 200, 150}, {150, 150, 200}, {200, 200, 200}};
	int[][] userFontColor = {{255, 255, 255}, {0, 0, 0}};
	int colorTotal = 8;
	
	CalendarFile calendarFile = new CalendarFile();
	SettingFile settingFile = new SettingFile();
	
	Font defaultFont = new Font("Dialog", Font.BOLD, 14);
	Font buttonFont = new Font("Dialog", Font.BOLD, 14);
	
	String[][] mCalendar = null;
	String[] columnTitle = {"SUN" , "MON" , "TUE", "WED", "THU", "FRI", "SAT"};
	//String[] monthArray = {"JAN" , "FEB" , "TUE", "WED", "THU", "FRI", "SAT"};
	int monthLastDay[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	int year, month, week, day, nowYear, nowMonth;
	int showingMonth;
	int nextMonth = 0;
	int line = 0;
	
	int[] choseDay = new int[2];
	int[] today = new int[2];
	
	boolean[][] noteDay = new boolean[6][7];
	boolean noteShowFlag = true;
	boolean settingShowFlag = false;

	int settingHeight = 49;
	int titleSize = 100;
	int frameWidth = 210, frameHeight = 400;
	// Create the frame.
	public CalendarFrame() 
	{
		super("月曆");
		System.setProperty("sun.java2d.noddraw", "true"); // 防止激活输入法時白屏
		UIManager.put("Button.select", new Color(0, 0, 0, 50));
		UIManager.put("TableHeader.font", new Font("Dialog", Font.BOLD, 10));
		//UIManager.put("TableHeader.setHorizontalAlignment", SwingConstants.CENTER);
		putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
		
		//取得使用者自定義設定
		getUserAlpha();
		getUserColor();
		
		contentSet();
		
		changeFontColor();
		changeBackgroundColor();
			
		javax.swing.plaf.InternalFrameUI ifu = getUI();
		((javax.swing.plaf.basic.BasicInternalFrameUI)ifu).setNorthPane(null);
		
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		int settingHeight = (settingShowFlag)? 21 : 0 ;
		
		setSize(frameWidth, frameHeight + settingHeight);
		setBackground(new Color(0, 0, 0, 0));
		//Border lineBorder = new LineBorder (Color.RED, 5, true);
		//setBorder(lineBorder);
		//setShape(new RoundRectangle2D.Double(10, 10, 100, 100, 50, 50));
		
		//add hot key
		KeyStroke plus = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
		InputMap inputMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(plus, "my_action");
		ActionMap actionMap = contentPane.getActionMap();
		actionMap.put("my_action", new AbstractAction() {
					public void actionPerformed(ActionEvent e)
					{
						setVisible(false);
					}
				});
		
		addMouseListener(new  MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				Point nowFrameXY = getLocation();
				settingFile.saveSite("calendar", nowFrameXY.x, nowFrameXY.y);
				
				mouseDownCompCoords = null;
			}
			public void mousePressed(MouseEvent e) {
				mouseDownCompCoords = e.getPoint();
			}
		});
			
		addMouseMotionListener(new  MouseMotionAdapter() {			
			public void mouseDragged(MouseEvent e) {
				Point currCoords = e.getLocationOnScreen();
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				int frameX = currCoords.x - mouseDownCompCoords.x;
				if(frameX > screenSize.width - frameWidth - 10)
					frameX = screenSize.width - frameWidth - 10;
				else if(frameX < 10)
					frameX = 10;
				
				Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
				int taskBarSize = scnMax.bottom;
				int frameY = currCoords.y - mouseDownCompCoords.y;
				if(frameY > screenSize.height - getHeight() - taskBarSize - 10)
					frameY = screenSize.height - getHeight() - taskBarSize - 10;
				else if(frameY < 10)
					frameY = 10;
				setLocation(frameX, frameY);
				
				Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
				win.repaint();
			}
		});
	}
	
	private void contentSet()
	{
		choseDay[0] = choseDay[1] = -1;
		getdate();
		nowMonth = month;
		nowYear = year;
		
		contentPane = new JPanel();
		contentPane.setBorder( new EmptyBorder(10, 10, 10, 10) );
		contentPane.setLayout( new BorderLayout(0, 5) );
		setContentPane(contentPane);
		
		calendarContentSet();
		otherContentSet();
		settingContentSet();
	}
	
	private void calendarContentSet()
	{
		calendarBox = Box.createVerticalBox();
		calendarBox.setOpaque(false);
		
		JPanel titlePanel = new JPanel(new BorderLayout(0, 0));
		titlePanel.setOpaque(false);
		//切換到上個月份的按鈕
		preMonthButton = new JButton();
		try {
			BufferedImage img = ImageIO.read(getClass().getResource("image/cross_left.png"));
			Image dimg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
			preMonthButton.setIcon(new ImageIcon(dimg));
		} catch (IOException ex) {
			preMonthButton.setText(" < ");
			preMonthButton.setFont(defaultFont);
		}
		preMonthButton.setBorder(BorderFactory.createEmptyBorder());
		preMonthButton.addActionListener(new ButtonActionListener('<'));
		preMonthButton.setOpaque(false);
		preMonthButton.addMouseListener(new ButtonMouseListener());
		preMonthButton.addMouseMotionListener(new ButtonMouseMotionListener());
		preMonthButton.addFocusListener(new comFocusListener());
		titlePanel.add(preMonthButton, BorderLayout.WEST);
		
		//顯示年份月份
		yearMonthLabel = new JLabel();
		yearMonthLabel.setFont(new Font("微軟正黑體", Font.BOLD, 14));
		yearMonthLabel.setHorizontalAlignment(JLabel.CENTER);
		titlePanel.add(yearMonthLabel, BorderLayout.CENTER);
		
		//切換到下個月份的按鈕
		nextMonthButton = new JButton();
		try {
			BufferedImage img = ImageIO.read(getClass().getResource("image/cross_right.png"));
			Image dimg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
			nextMonthButton.setIcon(new ImageIcon(dimg));
		} catch (IOException ex) {
			nextMonthButton.setText(" > ");
			nextMonthButton.setFont(defaultFont);
		}
		nextMonthButton.setBorder(BorderFactory.createEmptyBorder());
		nextMonthButton.addActionListener(new ButtonActionListener('>'));
		nextMonthButton.setOpaque(false);
		nextMonthButton.addMouseListener(new ButtonMouseListener());
		nextMonthButton.addMouseMotionListener(new ButtonMouseMotionListener());
		nextMonthButton.addFocusListener(new comFocusListener());
		titlePanel.add(nextMonthButton, BorderLayout.EAST);
		
		calendarBox.add(titlePanel);
		calendarBox.add(Box.createVerticalStrut(3));
		
		//顯示日曆
		tblModel = new DefaultTableModel(mCalendar,columnTitle);
		calendarTable = new JTable(tblModel){
			public boolean isCellEditable(int row,int column)
			{
				return false;
			}
		};
		calendarTable.addMouseListener(new CalendarMouseListener());
		calendarTable.addMouseMotionListener(new CalendarMouseMotionListener());
		calendarTable.setOpaque(false);
		calendarTable.setShowGrid(false);
		DefaultTableCellRenderer renderer = new CustomTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		calendarTable.setDefaultRenderer(Object.class, renderer);
		calendarTable.setFont(defaultFont);
		calendarTable.setRowHeight(25);
		calendarTable.getTableHeader().setResizingAllowed(false);
		calendarTable.getTableHeader().setReorderingAllowed(false);
		calendarTable.getTableHeader().setBackground(new Color(255, 255, 255, 50));
		for(int i = 0; i < columnTitle.length; i++)
        {
            TableColumn column = calendarTable.getColumnModel().getColumn(i);
            column.setHeaderRenderer( new HeaderRenderer() );
        }
		
		scrollPane = new JScrollPane(calendarTable,
									JScrollPane.VERTICAL_SCROLLBAR_NEVER,
									JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		//scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);	
		calendarBox.add(scrollPane);
		contentPane.add(calendarBox, BorderLayout.NORTH);
	}
	
	private void otherContentSet()
	{
		JPanel notePanel = new JPanel(new BorderLayout(0, 5));
		notePanel.setBorder( new EmptyBorder(5, 0, 0, 0) );
		notePanel.setOpaque(false);
		
		JPanel settingPanel = new JPanel(new BorderLayout());
		settingPanel.setOpaque(false);
		
		//NOTE標籤
		noteLabel = new JLabel("NOTE");
		noteLabel.setFont(defaultFont);
		noteLabel.setHorizontalAlignment(JLabel.CENTER);
		noteLabel.addMouseListener(new MouseAdapter(){
							public void mouseClicked(MouseEvent e) {
								noteShowFlag = !noteShowFlag;
								resizeFrame();
								noteScrollPane.setVisible(noteShowFlag);
								buttonPanel.setVisible(noteShowFlag);
							}
						});
						
		settingPanel.add(noteLabel, BorderLayout.CENTER);
		
		settingButton = new JButton();
		try {
			BufferedImage img = ImageIO.read(getClass().getResource("image/setting.png"));
			Image dimg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
			settingButton.setIcon(new ImageIcon(dimg));
		} catch (IOException ex) {
			settingButton.setText(" Setting ");
			settingButton.setFont(defaultFont);
		}
		settingButton.setBorder(BorderFactory.createEmptyBorder());
		settingButton.addActionListener(new ButtonActionListener('T'));
		settingButton.setOpaque(false);
		settingButton.addMouseListener(new ButtonMouseListener());
		settingButton.addMouseMotionListener(new ButtonMouseMotionListener());
		settingButton.addFocusListener(new comFocusListener());
		settingPanel.add(settingButton, BorderLayout.EAST);
		
		addNoteButton = new JButton();
		try {
			BufferedImage img = ImageIO.read(getClass().getResource("image/addLogo.png"));
			Image dimg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
			addNoteButton.setIcon(new ImageIcon(dimg));
		} catch (IOException ex) {
			addNoteButton.setText(" ADD ");
			addNoteButton.setFont(defaultFont);
		}
		addNoteButton.setBorder(BorderFactory.createEmptyBorder());
		addNoteButton.addActionListener(new ButtonActionListener('A'));
		addNoteButton.setOpaque(false);
		addNoteButton.addMouseListener(new ButtonMouseListener());
		addNoteButton.addMouseMotionListener(new ButtonMouseMotionListener());
		addNoteButton.addFocusListener(new comFocusListener());
		settingPanel.add(addNoteButton, BorderLayout.WEST);

		notePanel.add(settingPanel, BorderLayout.NORTH);
		
		//輸入事件欄位
		noteJTextArea = new JTextArea();
		noteJTextArea.setOpaque(false);
		noteJTextArea.setCaretColor(Color.white);	//輸入槓顏色
		noteJTextArea.setFont(new Font("微軟正黑體", Font.PLAIN, 14));//字體大小			
		noteJTextArea.setText("");
		noteJTextArea.setLineWrap(true);        //自動換行 
        noteJTextArea.setWrapStyleWord(true);	//不斷字
		noteJTextArea.addMouseListener(new TextAreaMouseListener());
		noteJTextArea.addMouseMotionListener(new ButtonMouseMotionListener());
		
		noteJTextArea.setBorder(BorderFactory.createCompoundBorder(noteJTextArea.getBorder(), 
						BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		calendarFile.loadFile(noteJTextArea, year, month, day);
		noteJTextArea.addCaretListener(new TextAreaCaretListener());
		
		//置入滾輪
		noteScrollPane = new JScrollPane(noteJTextArea);
		//noteScrollPane.setOpaque(false);
		noteScrollPane.getViewport().setOpaque(false);
		noteScrollPane.getVerticalScrollBar().addAdjustmentListener(new scrollAdjustmentListener());
		notePanel.add(noteScrollPane, BorderLayout.CENTER);
		contentPane.add(notePanel, BorderLayout.CENTER);
		
		//設定calendar並載入系統時間
		mCalendar = getday(mCalendar, true);
		updateCalendar();
		
		//button.setBorder(BorderFactory.createEmptyBorder());
	}
	
	private void settingContentSet()
	{
		Box setBox = Box.createVerticalBox();
		//存放South按鈕		
		buttonPanel = new JPanel(new BorderLayout(0, 0));
		buttonPanel.setBorder( new EmptyBorder(0, 10, 0, 10) );
		buttonPanel.setOpaque(false);
		//儲存按鈕
		safeButton = new JButton("Save");
		safeButton.addActionListener(new ButtonActionListener('s'));
		safeButton.setBorder(BorderFactory.createEmptyBorder());
		safeButton.setFont(buttonFont);
		safeButton.setOpaque(false);
		safeButton.addMouseListener(new ButtonMouseListener());
		safeButton.addMouseMotionListener(new ButtonMouseMotionListener());
		safeButton.addFocusListener(new comFocusListener());
		buttonPanel.add(safeButton, BorderLayout.WEST);
		
		//刪除按鈕
		deleteButton = new JButton("Clear");
		deleteButton.addActionListener(new ButtonActionListener('c'));
		deleteButton.setBorder(BorderFactory.createEmptyBorder());
		deleteButton.setFont(buttonFont);
		deleteButton.setOpaque(false);
		deleteButton.addMouseListener(new ButtonMouseListener());
		deleteButton.addMouseMotionListener(new ButtonMouseMotionListener());
		deleteButton.addFocusListener(new comFocusListener());
		buttonPanel.add(deleteButton, BorderLayout.EAST);
		
		setBox.add(buttonPanel);
		
		sliderStruct = Box.createVerticalStrut(5);
		sliderStruct.setVisible(settingShowFlag);
		setBox.add(sliderStruct);
		
		slider.setPaintTicks(false);
		slider.setOpaque(false);
		slider.addMouseMotionListener(new ButtonMouseMotionListener());
		slider.addMouseListener(new ButtonMouseListener());
		slider.addFocusListener(new comFocusListener());
		//slider.setBorder(BorderFactory.createTitledBorder("Alpha Value"));
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent cEvt) {
				SetUserAlpha( slider.getValue() );
				changeBackgroundColor();
				CalendarFrame.this.repaint();

				Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
				win.repaint();
			}
		});
		slider.setVisible(settingShowFlag);
		setBox.add(slider);
		
		colorStruct = Box.createVerticalStrut(10);
		colorStruct.setVisible(settingShowFlag);
		setBox.add(colorStruct);
		
		colorBox = Box.createHorizontalBox();
		for(int i = 0 ; i < colorTotal ; i++){
			JButton colorButton = new JButton("       ");
			colorButton.addActionListener(new ColorButtonActionListener(i));
			colorButton.addMouseListener(new ButtonMouseListener());
			colorButton.setBorder(BorderFactory.createEmptyBorder());
			//colorButton.setOpaque(false);
			colorButton.setBackground(new Color(userBackgroundColor[i][0] + 50, userBackgroundColor[i][1] + 50, userBackgroundColor[i][2] + 50));
			colorButton.addFocusListener(new comFocusListener());
			colorBox.add(colorButton);
		}
		colorBox.setVisible(settingShowFlag);
		setBox.add(colorBox);
		
		contentPane.add(setBox, BorderLayout.SOUTH);
	}
	
	private void changeBackgroundColor()
	{
		contentPane.setBackground( new Color(userBackgroundColor[userColor][0], userBackgroundColor[userColor][1], userBackgroundColor[userColor][2], userAlpha) );
		preMonthButton.setBackground(new Color(userBackgroundColor[userColor][0], userBackgroundColor[userColor][1], userBackgroundColor[userColor][2]));
		nextMonthButton.setBackground(new Color(userBackgroundColor[userColor][0], userBackgroundColor[userColor][1], userBackgroundColor[userColor][2]));
		scrollPane.setBackground( new Color(userBackgroundColor[userColor][0] + 50, userBackgroundColor[userColor][1] + 50, userBackgroundColor[userColor][2] + 50, 100) );
		settingButton.setBackground(new Color(userBackgroundColor[userColor][0], userBackgroundColor[userColor][1], userBackgroundColor[userColor][2]));
		addNoteButton.setBackground(new Color(userBackgroundColor[userColor][0], userBackgroundColor[userColor][1], userBackgroundColor[userColor][2]));
		noteScrollPane.setBackground( new Color(userBackgroundColor[userColor][0] + 50, userBackgroundColor[userColor][1] + 50, userBackgroundColor[userColor][2] + 50, 100) );
		safeButton.setBackground(new Color(userBackgroundColor[userColor][0], userBackgroundColor[userColor][1], userBackgroundColor[userColor][2]));
		deleteButton.setBackground(new Color(userBackgroundColor[userColor][0], userBackgroundColor[userColor][1], userBackgroundColor[userColor][2]));
		slider.setBackground(new Color(userBackgroundColor[userColor][0], userBackgroundColor[userColor][1], userBackgroundColor[userColor][2]));
	}
	
	private void changeFontColor()
	{
		if(userColor < 4){
			preMonthButton.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
			yearMonthLabel.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
			nextMonthButton.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
			noteLabel.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
			settingButton.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
			addNoteButton.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
			noteJTextArea.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
			safeButton.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
			deleteButton.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
			slider.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
			calendarTable.getTableHeader().setForeground(new Color(0).white);
		}else{
			preMonthButton.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
			yearMonthLabel.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
			nextMonthButton.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
			noteLabel.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
			settingButton.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
			addNoteButton.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
			noteJTextArea.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
			safeButton.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
			deleteButton.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
			slider.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
			calendarTable.getTableHeader().setForeground(new Color(0).black);
		}
	}
	
	private void SetUserAlpha(int newAlpha)
	{
		userAlpha = newAlpha;
		settingFile.saveSetting("calendar", userColor, userAlpha);
	}
	
	private void SetUserColor(int newColor)
	{
		userColor = newColor;
		settingFile.saveSetting("calendar", userColor, userAlpha);
	}
	
	private void getUserAlpha()
	{
		int[] setting = settingFile.loadSetting("calendar");
		userAlpha = setting[0];
		slider.setValue(userAlpha);
		//System.out.println( "userAlpha " + userAlpha );
	}
	
	private void getUserColor()
	{
		int[] setting = settingFile.loadSetting("calendar");
		userColor = setting[1];
		//System.out.println( "userColor " + userColor );
	}
	
	public String[][] getday(String[][] date, boolean newMonth)
	{
		Calendar calendar = Calendar.getInstance();
		
		if(newMonth)
		{
			getdate();
			
			calendar.set(Calendar.YEAR,year);
			calendar.set(Calendar.MONTH,month-1);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			
			week = calendar.get(Calendar.DAY_OF_WEEK);
			if( year%400 == 0 || ( !(year%100 == 0) && (year%4 == 0) ) )
				monthLastDay[1] = 29;
			else
				monthLastDay[1] = 28;
				
			if( ( (week >= 6) && (monthLastDay[month-1] == 31) ) || 
				( (week == 7) && (monthLastDay[month-1] >= 30) ) )
				line = 6;
			else
				line = 5;
			date = new String[line][7];
		}
		
		int d = 1, preCount = -5, nextNum = 1;
		boolean haveToday = false;
		for(int j = 0 ; j < line ; j++)	//row
		{
			for( int i = 0 ; i < 7 ; i++ )	//column
			{	//設自首列空白
				if( j == 0 && i < week - 1 )
					date[j][i] = "";
				else{
					//設置尾列NEXT MONTH日期
					if(d > monthLastDay[showingMonth-1])
					{
						//確認這天有沒有事件
						if(calendarFile.findFile(year, showingMonth+1, nextNum))
						noteDay[j][i] = true;
						else					
							noteDay[j][i] = false;
						date[j][i] = "" + nextNum++;
					}
					else	//設置當月日期
					{
						if(preCount == -5)	//記錄開頭列最後一個空白的位置
							preCount = i-1;
							
						//記錄今日的位置
						if(newMonth && nowMonth == showingMonth && nowYear == year && d == day)
						{
							today[0] = j;
							today[1] = i;
							haveToday = true;
						}
						
						//確認這天有沒有事件
						if(calendarFile.findFile(year, showingMonth, d))
							noteDay[j][i] = true;
						else					
							noteDay[j][i] = false;
						
						date[j][i] = "" + d++;
					}
				}
			}
		}
		
		int preNum = monthLastDay[showingMonth-2];
		for(int i = preCount ; i >= 0 ; i--)
		{
			//確認這天有沒有事件
			if(calendarFile.findFile(year, showingMonth-1, preNum))
				noteDay[0][i] = true;
			else					
				noteDay[0][i] = false;
			
			date[0][i] = "" + preNum--;
		}
		
		if(newMonth && haveToday == false)
		{
			today[0] = -1;
			today[1] = -1;
		}
		
		return date;
	}
	
	//取得系統時間
	public void getdate()
	{
		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1 + nextMonth;
		day = calendar.get(Calendar.DAY_OF_MONTH);
		
		while( month > 12 || month < 1 )
		{
			if( month > 12 )
			{
				year++;
				month = month - 12;
			}
			else if( month < 1 )
			{
				year--;
				month = 12 + month;
			}
		}
			
		showingMonth = month;
	}
	
	//更新月曆
	public void updateCalendar()
	{
		mCalendar = getday(mCalendar, true);
		
		//更新年份月份
		String strYearMon = new String(year + " 年  " + month + " 月");
		yearMonthLabel.setText(strYearMon);
		
		//更新table
		tblModel.setDataVector(mCalendar, columnTitle);
		calendarTable.setModel(tblModel);
		tblModel.fireTableDataChanged();

		Dimension paneDimension = noteJTextArea.getSize();
		//System.out.println(line);
		scrollPane.setPreferredSize( new Dimension(paneDimension.width, (line + 1)*25 - 3) );
		calendarTable.setSize( paneDimension.width, line*10 );
		
		scrollPane.revalidate();
		scrollPane.repaint();
	}
	
	public class CustomTableCellRenderer extends DefaultTableCellRenderer 
	{
		public Component getTableCellRendererComponent
		   (JTable table, Object value, boolean isSelected,
		   boolean hasFocus, int row, int column) 
		{
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			//設定背景色，選取、未選取、當日
			if(row == choseDay[0] && column == choseDay[1])
				cell.setBackground( new Color(0, 82, 220, 200) );
			else if(row == today[0] && column == today[1]){
				if(userColor < 4)
					cell.setBackground( new Color(0, 33, 165, 150) );
				else
					cell.setBackground( new Color(100, 150, 220, 150) );
			}else
				cell.setBackground(new Color(0, 0, 0, 0));
			
			String valueStr = (String)calendarTable.getValueAt(row, column);
			//設定字體色，有事件、平常日、週末
			//				上個月										下個月
			if(row == 0 && Integer.valueOf(valueStr) > 7 || row == line-1 && Integer.valueOf(valueStr) < 7)
			{
				if(noteDay[row][column] == true)
					cell.setForeground(Color.red.darker());
				else if(column == 0 || column == 6){
					if(userColor < 4)
						cell.setForeground(Color.pink.darker());
					else
						cell.setForeground(new Color(userBackgroundColor[userColor][0] - 100, userBackgroundColor[userColor][1] - 100, userBackgroundColor[userColor][2] - 100));
				}else{
					if(userColor < 4)
						cell.setForeground(Color.lightGray);
					else
						cell.setForeground(Color.darkGray);
				}
			}else{
				if(noteDay[row][column] == true)
					cell.setForeground(Color.red);
				else if(column == 0 || column == 6){
					if(userColor < 4)
						cell.setForeground(Color.pink);
					else
						cell.setForeground(new Color(userBackgroundColor[userColor][0] - 75, userBackgroundColor[userColor][1] - 75, userBackgroundColor[userColor][2] - 75));
				}else{
					if(userColor < 4)
						cell.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
					else
						cell.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
				}
			}
			return cell;
		}
	}
	
	public class HeaderRenderer extends JLabel implements TableCellRenderer
	{
		public Component getTableCellRendererComponent(JTable table,
													   Object value,
													   boolean hasFocus,
													   boolean isSelected,
													   int row,
													   int col)
		{
			setText(value.toString());
			setBorder(BorderFactory.createEmptyBorder());
			setHorizontalAlignment(JLabel.CENTER);
			return this;
		}
	}

	//scroll bar事件
	public class scrollAdjustmentListener implements AdjustmentListener
	{
		public void adjustmentValueChanged(AdjustmentEvent e) {
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
	}
	
	//button事件
	public class ButtonActionListener implements ActionListener
	{
		private char value;
		public ButtonActionListener(char buttonValue)
		{
			value = buttonValue;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			switch(value)
			{
			case '<':
				noteJTextArea.setText("");
				choseDay[0] = choseDay[1] = -1;
				nextMonth--;
				updateCalendar();
				break;
			case '>':
				noteJTextArea.setText("");
				choseDay[0] = choseDay[1] = -1;
				nextMonth++;
				updateCalendar();
				break;
			case 's':
				calendarFile.safeFile(noteJTextArea,year,month,day);
				//System.out.println("s updateCalendar " + year + month + day);
				getday(mCalendar, false);
				calendarTable.repaint();
				break;
			case 'c':
				noteJTextArea.setText("");
				calendarFile.deleteFile(year, month, day);
				getday(mCalendar, false);
				calendarTable.repaint();
				break;
			case 'T':
				settingShowFlag = !settingShowFlag;
				resizeFrame();
				slider.setVisible(settingShowFlag);
				sliderStruct.setVisible(settingShowFlag);
				colorBox.setVisible(settingShowFlag);
				colorStruct.setVisible(settingShowFlag);
				break;
			case 'A':
				Point nowFrameXY = getLocation();
				CalendarClockFrame addFrame = new CalendarClockFrame(year, month, day, nowFrameXY.x, nowFrameXY.y + frameHeight/2);
				addFrame.addWindowListener( new WindowListener() {
							public void windowClosing(WindowEvent e) {}
							public void windowClosed(WindowEvent e) { 
								calendarFile.loadFile(noteJTextArea, year, month, day);
								
								getday(mCalendar, false);
								calendarTable.repaint();
								calendarTable.revalidate();
								
								Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
								win.repaint();
							}
							public void windowOpened(WindowEvent e) {}
							public void windowActivated(WindowEvent e) {}
							public void windowDeactivated(WindowEvent e) {}
							public void windowIconified(WindowEvent e) {}
							public void windowDeiconified(WindowEvent e) {}
						});
				
				break;
			}
			
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
	}
	
	private void resizeFrame()
	{
		int settingH = (settingShowFlag)? settingHeight : 0 ;
		if(noteShowFlag)
			setSize(210, frameHeight + settingH);
		else
			setSize(210, 25*line + titleSize + settingH);
	}
	
	public class ColorButtonActionListener implements ActionListener
	{
		private int colorIndex;
		public ColorButtonActionListener(int colorIndex)
		{
			this.colorIndex = colorIndex;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			SetUserColor(colorIndex);
			changeFontColor();
			changeBackgroundColor();
			
			repaint();

			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
	}
	
	//button滑鼠事件
	public class ButtonMouseListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e) {
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}

		public void mouseEntered(MouseEvent e) {
			setCursor (Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
		
		public void mouseExited(MouseEvent e) {
			setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
		
		public void mouseReleased(MouseEvent e) {
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
		
		public void mousePressed(MouseEvent e) {
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
	}
	
	public class ButtonMouseMotionListener extends MouseMotionAdapter
	{
		public void mouseMoved(MouseEvent e) {
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
		public void mouseDragged(MouseEvent e) {
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
	}
	
	//主權事件
	public class comFocusListener implements FocusListener
	{
		public void focusGained(FocusEvent e) {
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
		public void focusLost(FocusEvent e) {
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
	}
	
	//Calendar滑鼠事件
	public class CalendarMouseListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			int click = e.getClickCount();
			String value;
			
			choseDay[0] = calendarTable.getSelectedRow();
			choseDay[1] = calendarTable.getSelectedColumn();
			value = (String)calendarTable.getValueAt(choseDay[0], choseDay[1]);
			
			day = Integer.valueOf(value);
			
			if(choseDay[0] == 0 && Integer.valueOf(value) > 7)		//上個月
				month = showingMonth-1; 
			else if(choseDay[0] == line-1 && Integer.valueOf(value) < 7)	//下個月
				month = showingMonth+1;
			else
				month = showingMonth;
				
			calendarFile.loadFile(noteJTextArea, year, month, day);
			
			//scrollPane.repaint();
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
		
		public void mousePressed(MouseEvent e) {
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
	}
	
	public class CalendarMouseMotionListener extends MouseMotionAdapter
	{
		public void mouseDragged(MouseEvent e) {
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
	}
	
	public class TextAreaMouseListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e) {
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}

		public void mouseEntered(MouseEvent e) {
			setCursor (Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
		
		public void mouseExited(MouseEvent e) {
			setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
		
		public void mouseReleased(MouseEvent e) {
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
		
		public void mousePressed(MouseEvent e) {
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
	}
	
	public class TextAreaCaretListener implements CaretListener
	{
		public void caretUpdate(CaretEvent e) {
			Window win = SwingUtilities.getWindowAncestor(CalendarFrame.this);
			win.repaint();
		}
	}
}
