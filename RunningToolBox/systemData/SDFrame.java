package RunningToolBox.systemData;

import RunningToolBox.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.EmptyBorder;
import java.text.DecimalFormat;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;

public class SDFrame extends JInternalFrame 
{	
	static Point mouseDownCompCoords;
	final JSlider slider = new JSlider(100, 200, 140);
	
	static private JPanel contentPane;
	private JPanel userNamePanel;
		
	private JLabel userNameLabel;
	private JLabel runtimeMemoryLabel;
	private JLabel runtimeMemoryLDataLabel;
	private JLabel physicalMemoryLabel;
	private JLabel physicalMemoryDataLabel;
	private JLabel cpuUsageLabel;
	private JLabel cpuUsageDataLabel;
	
	private LinesComponent rmLineComp = new LinesComponent();
	private LinesComponent pmLineComp = new LinesComponent();
	private LinesComponent cpuLineComp = new LinesComponent();
	
	private JButton settingButton;
	private Box colorBox;
	private Component sliderStruct;
	private Component colorStruct;
	private Component tailStruct;
	private int userColor = 0;
	private int userAlpha = 150;
	
	int[][] userBackgroundColor = {{0, 0, 0}, {50, 0, 0}, {0, 50, 0}, {0, 0, 50}, 
								   {200, 150, 150}, {150, 200, 150}, {150, 150, 200}, {200, 200, 200}};
	int[][] userFontColor = {{255, 255, 255}, {0, 0, 0}};
	int colorTotal = 8;
	int settingHeight = 59;
	
	boolean settingShowFlag = false;
	
	SettingFile settingFile = new SettingFile();
	
	Font titleFont = new Font("Dialog", Font.BOLD, 14);
	Font dataFont = new Font("Dialog", Font.BOLD, 11);
	Color dataColor = Color.pink;
	Color labelColor = Color.white;
	Color lineColor = new Color(228, 154, 0);
	Color baseLineColor = new Color(255, 255, 255, 25);
	int frameWidth = 210, frameHeight = 110;
	
	// LabelFrame constructor adds JLabels to JFrame
	public SDFrame()
	{
		super( "Testing JLabel" );
		UIManager.put("Button.select", new Color(0, 0, 0, 50));
		putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
		
		//取得使用者自定義設定
		getUserAlpha();
		getUserColor();
		
		sDataContentSet();
		
		changeFontColor();
		changeBackgroundColor();
		
		//去除Title bar
		javax.swing.plaf.InternalFrameUI ifu = getUI();
		((javax.swing.plaf.basic.BasicInternalFrameUI)ifu).setNorthPane(null);
		
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(frameWidth, frameHeight);
		setBackground(new Color(0, 0, 0, 0));
		//setBorder(null);
		
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
		
		/*
		addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent ke) {  // handler
						if(ke.getKeyCode() == ke.VK_ESCAPE) {
							setVisible(false);
						}
					} 
				});
		setFocusable(true);*/
		
		addMouseListener(new  MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				Point nowFrameXY = getLocation();
				settingFile.saveSite("systemData", nowFrameXY.x, nowFrameXY.y);
				
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
				
				Window win = SwingUtilities.getWindowAncestor(SDFrame.this);
				win.repaint();
			}
		});
	}
	
	private void sDataContentSet()
	{
		contentPane = new JPanel();
		contentPane.setBorder( new EmptyBorder(0, 0, 0, 0) );
		contentPane.setLayout( new BorderLayout(0, 3) );
		setContentPane(contentPane);
		
		//user name
		userNamePanel = new JPanel( new BorderLayout() );
		userNamePanel.setBorder( new EmptyBorder(5, 5, 5, 5) );
		
		userNameLabel = new JLabel();
		userNameLabel.setFont(titleFont);
		userNameLabel.setForeground(Color.white);
		userNameLabel.setHorizontalAlignment(JLabel.CENTER);
		userNamePanel.add( userNameLabel , BorderLayout.CENTER );
		
		settingButton = new JButton();
		try {
			BufferedImage img = ImageIO.read(getClass().getResource("setting.png"));
			Image dimg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
			settingButton.setIcon(new ImageIcon(dimg));
		} catch (IOException ex) {
			settingButton.setText(" Setting ");
			settingButton.setFont(titleFont);
		}
		settingButton.setBorder(BorderFactory.createEmptyBorder());
		settingButton.addActionListener(new SettingActionListener());
		settingButton.setOpaque(false);
		settingButton.addMouseListener(new ButtonMouseListener());
		settingButton.addMouseMotionListener(new ButtonMouseMotionListener());
		settingButton.addFocusListener(new comFocusListener());
		userNamePanel.add(settingButton, BorderLayout.EAST);
		
		JLabel whiteLabel = new JLabel("       ");
		//whiteLabel.setOpaque(false);
		userNamePanel.add(whiteLabel, BorderLayout.WEST);
		
		contentPane.add( userNamePanel , BorderLayout.NORTH );
		
		//data panel
		JPanel syatemDataPanel = new JPanel( new GridLayout(3, 1, 0, 0) );
		syatemDataPanel.setBorder( new EmptyBorder(0, 5, 0, 5) );
		syatemDataPanel.setOpaque(false);
		
		//JVM內存
		Box rmVBox = Box.createVerticalBox();
		Box rmHBox = Box.createHorizontalBox();
		
		runtimeMemoryLabel = new JLabel("JVM Usage");
		runtimeMemoryLabel.setFont(dataFont);
		runtimeMemoryLabel.setHorizontalAlignment(JLabel.CENTER);
		rmHBox.add( runtimeMemoryLabel );	
		
		rmHBox.add( Box.createHorizontalGlue() );	
		
		runtimeMemoryLDataLabel = new JLabel();
		runtimeMemoryLDataLabel.setFont(dataFont);
		runtimeMemoryLDataLabel.setHorizontalAlignment(JLabel.LEFT);
		rmHBox.add( runtimeMemoryLDataLabel );

		rmVBox.add( rmHBox );
		rmVBox.add( rmLineComp );
		syatemDataPanel.add( rmVBox );
		
		//物理內存
		Box pmVBox = Box.createVerticalBox();
		Box pmHBox = Box.createHorizontalBox();
		
		physicalMemoryLabel = new JLabel("RAM Usage");
		physicalMemoryLabel.setFont(dataFont);
		physicalMemoryLabel.setHorizontalAlignment(JLabel.CENTER);
		pmHBox.add( physicalMemoryLabel );				
		
		pmHBox.add( Box.createHorizontalGlue() );	
		
		physicalMemoryDataLabel = new JLabel();
		physicalMemoryDataLabel.setFont(dataFont);
		physicalMemoryDataLabel.setHorizontalAlignment(JLabel.LEFT);
		pmHBox.add( physicalMemoryDataLabel );
		pmVBox.add( pmHBox );
		pmVBox.add( pmLineComp );
		syatemDataPanel.add( pmVBox );
		
		//CPU使用量
		Box cpuVBox = Box.createVerticalBox();
		Box cpuHBox = Box.createHorizontalBox();
		
		cpuUsageLabel = new JLabel("CPU Usage");
		cpuUsageLabel.setFont(dataFont);
		cpuUsageLabel.setHorizontalAlignment(JLabel.CENTER);
		cpuHBox.add( cpuUsageLabel );	
		
		cpuHBox.add( Box.createHorizontalGlue() );
		
		cpuUsageDataLabel = new JLabel();
		cpuUsageDataLabel.setFont(dataFont);
		cpuUsageDataLabel.setHorizontalAlignment(JLabel.CENTER);
		cpuHBox.add( cpuUsageDataLabel );
		
		cpuVBox.add( cpuHBox );
		cpuVBox.add( cpuLineComp );
		syatemDataPanel.add( cpuVBox );
		
		contentPane.add( syatemDataPanel , BorderLayout.CENTER );
		
		getData();
		//cpuUsageDataLabel.setToolTipText( "This is label3" );
		//cpuUsageDataLabel.setForeground(new Color((float)0.3, (float)0.2, (float)0.2));
		
		settingContentSet();
	}
	
	private void settingContentSet()
	{
		JPanel settingPanel = new JPanel(new BorderLayout(0, 0));
		settingPanel.setBorder( new EmptyBorder(0, 10, 0, 10) );
		settingPanel.setOpaque(false);
		
		Box setBox = Box.createVerticalBox();
		
		sliderStruct = Box.createVerticalStrut(5);
		sliderStruct.setVisible(settingShowFlag);
		setBox.add(sliderStruct);
		
		//comp = comp.derive((float)0.8);
		//slider.setMajorTickSpacing(20);
		//slider.setMinorTickSpacing(5);
		//slider.setPaintLabels(true);
		slider.setPaintTicks(false);
		slider.setOpaque(false);
		slider.addMouseMotionListener(new ButtonMouseMotionListener());
		slider.addMouseListener(new ButtonMouseListener());
		slider.addFocusListener(new comFocusListener());
		//slider.setBorder(BorderFactory.createTitledBorder("Alpha Value"));
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent cEvt) {
				SetUserAlpha( slider.getValue() );
				//comp = comp.derive(alpha);
				changeBackgroundColor();
				SDFrame.this.repaint();

				Window win = SwingUtilities.getWindowAncestor(SDFrame.this);
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
			colorBox.add(colorButton);
		}
		colorBox.setVisible(settingShowFlag);
		setBox.add(colorBox);
		
		tailStruct = Box.createVerticalStrut(10);
		tailStruct.setVisible(settingShowFlag);
		setBox.add(tailStruct);
		
		settingPanel.add(setBox, BorderLayout.CENTER);
		contentPane.add(settingPanel, BorderLayout.SOUTH);
	}
	
	private void changeBackgroundColor()
	{
		
		contentPane.setBackground(new Color(userBackgroundColor[userColor][0], userBackgroundColor[userColor][1], userBackgroundColor[userColor][2], userAlpha));
		if(userColor < 4)
			userNamePanel.setBackground(new Color(userBackgroundColor[userColor][0], userBackgroundColor[userColor][1], userBackgroundColor[userColor][2], 75));
		else
			userNamePanel.setBackground(new Color(userBackgroundColor[userColor][0], userBackgroundColor[userColor][1], userBackgroundColor[userColor][2], 125));
		settingButton.setBackground(new Color(userBackgroundColor[userColor][0], userBackgroundColor[userColor][1], userBackgroundColor[userColor][2]));
	}
	
	private void changeFontColor()
	{
		if(userColor < 4){
			runtimeMemoryLabel.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
			runtimeMemoryLDataLabel.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
			physicalMemoryLabel.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
			physicalMemoryDataLabel.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
			cpuUsageLabel.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
			cpuUsageDataLabel.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
			settingButton.setForeground(new Color(userFontColor[0][0], userFontColor[0][1], userFontColor[0][2]));
			lineColor = new Color(228, 154, 0);
			baseLineColor = new Color(255, 255, 255, 25);
		}else{
			runtimeMemoryLabel.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
			runtimeMemoryLDataLabel.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
			physicalMemoryLabel.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
			physicalMemoryDataLabel.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
			cpuUsageLabel.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
			cpuUsageDataLabel.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
			settingButton.setForeground(new Color(userFontColor[1][0], userFontColor[1][1], userFontColor[1][2]));
			lineColor = new Color(userBackgroundColor[userColor][0] - 75, userBackgroundColor[userColor][1] - 75, userBackgroundColor[userColor][2] - 75);
			baseLineColor = new Color(255, 255, 255, 75);
		}
		
		refreshLabel();
		refreshCPULabel();
	}
	
	private void SetUserAlpha(int newAlpha)
	{
		userAlpha = newAlpha;
		settingFile.saveSetting("systemData", userColor, userAlpha);
	}
	
	private void SetUserColor(int newColor)
	{
		userColor = newColor;
		settingFile.saveSetting("systemData", userColor, userAlpha);
	}
	
	private void getUserAlpha()
	{
		int[] setting = settingFile.loadSetting("systemData");
		userAlpha = setting[0];
		slider.setValue(userAlpha);
		//System.out.println( "userAlpha " + userAlpha );
	}
	
	private void getUserColor()
	{
		int[] setting = settingFile.loadSetting("systemData");
		userColor = setting[1];
		//System.out.println( "userColor " + userColor );
	}
	
	public void refreshCPU()
	{
		refreshCPULabel();
	}
	
	public void refreshData()
	{
		refreshLabel();
	}
	
	private void resizeFrame()
	{
		int settingH = (settingShowFlag)? settingHeight : 0 ;
		setSize(210, frameHeight + settingH);
	}
	
	public void getData()
	{
		IMonitorService service = new IMonitorService(); 
		MonitorInfoBean monitorInfo = service.getMonitorInfoBean(); 
		
		userNameLabel.setText( "" + service.getUserNameForWindows() );
		
		float raNum = (float)monitorInfo.getTotalMemory() / (float)monitorInfo.getMaxMemory();
		raNum *= 100;
		runtimeMemoryLDataLabel.setText( "" + (int)raNum + "%" );
		rmLineComp.clearLines();
		rmLineComp.addLine(0, 0, 200, 0, baseLineColor);
		rmLineComp.addLine(0, 0, (int)raNum*2, 0, lineColor);
		
		float pmNum = (float)monitorInfo.getUsedPhysicalMemory() / (float)monitorInfo.getTotalPhysicalMemorySize();
		pmNum *= 100;
		physicalMemoryDataLabel.setText(	"" + (int)pmNum + "%" );
		pmLineComp.clearLines();
		pmLineComp.addLine(0, 0, 200, 0, baseLineColor);
		pmLineComp.addLine(0, 0, (int)pmNum*2, 0, lineColor);
		
		cpuUsageDataLabel.setText(	"" + monitorInfo.getCpuRatio() + "%" );
		cpuLineComp.clearLines();
		cpuLineComp.addLine(0, 0, 200, 0, baseLineColor);
		cpuLineComp.addLine(0, 0, (int)monitorInfo.getCpuRatio()*2, 0, lineColor);
	}
	
	public void refreshLabel()
	{
		IMonitorService service = new IMonitorService(); 
		MonitorInfoBean monitorInfo = service.getMonitorInfoBean(); 

		float raNum = (float)monitorInfo.getTotalMemory() / (float)monitorInfo.getMaxMemory();
		raNum *= 100;
		runtimeMemoryLDataLabel.setText( "" + (int)raNum + "%" );
		rmLineComp.clearLines();
		rmLineComp.addLine(0, 0, 200, 0, baseLineColor);
		rmLineComp.addLine(0, 0, (int)raNum*2, 0, lineColor);
		
		float pmNum = (float)monitorInfo.getUsedPhysicalMemory() / (float)monitorInfo.getTotalPhysicalMemorySize();
		pmNum *= 100;
		physicalMemoryDataLabel.setText( "" + (int)pmNum + "%" );
		pmLineComp.clearLines();
		pmLineComp.addLine(0, 0, 200, 0, baseLineColor);
		pmLineComp.addLine(0, 0, (int)pmNum*2, 0, lineColor);
		
		repaint();
	}
	
	public void refreshCPULabel()
	{
		IMonitorService service = new IMonitorService(); 
		MonitorInfoBean monitorInfo = service.getMonitorInfoBean(); 
		
		cpuUsageDataLabel.setText(	"" + (int)monitorInfo.getCpuRatio() + "%" );
		cpuLineComp.clearLines();
		cpuLineComp.addLine(0, 0, 200, 0, baseLineColor);
		cpuLineComp.addLine(0, 0, (int)monitorInfo.getCpuRatio()*2, 0, lineColor);
		
		repaint();
	}	

	public class SettingActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			settingShowFlag = !settingShowFlag;
			resizeFrame();
			slider.setVisible(settingShowFlag);
			sliderStruct.setVisible(settingShowFlag);
			colorBox.setVisible(settingShowFlag);
			colorStruct.setVisible(settingShowFlag);
			tailStruct.setVisible(settingShowFlag);
			
			Window win = SwingUtilities.getWindowAncestor(SDFrame.this);
			win.repaint();
		}
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

			Window win = SwingUtilities.getWindowAncestor(SDFrame.this);
			win.repaint();
		}
	}

		//button滑鼠事件
	public class ButtonMouseListener extends MouseAdapter
	{
		public void mouseEntered(MouseEvent e) {
			setCursor (Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			Window win = SwingUtilities.getWindowAncestor(SDFrame.this);
			win.repaint();
		}
		
		public void mouseExited(MouseEvent e) {
			setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			Window win = SwingUtilities.getWindowAncestor(SDFrame.this);
			win.repaint();
		}
	}
	
	public class ButtonMouseMotionListener extends MouseMotionAdapter
	{
		public void mouseMoved(MouseEvent e) {
			Window win = SwingUtilities.getWindowAncestor(SDFrame.this);
			win.repaint();
		}
		public void mouseDragged(MouseEvent e) {
			Window win = SwingUtilities.getWindowAncestor(SDFrame.this);
			win.repaint();
		}
	}
	
		//主權事件
	public class comFocusListener implements FocusListener
	{
		public void focusGained(FocusEvent e) {
			Window win = SwingUtilities.getWindowAncestor(SDFrame.this);
			win.repaint();
		}
		public void focusLost(FocusEvent e) {
			Window win = SwingUtilities.getWindowAncestor(SDFrame.this);
			win.repaint();
		}
	}
}
