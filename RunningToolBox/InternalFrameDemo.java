package RunningToolBox;

import RunningToolBox.calendar.*;
import RunningToolBox.systemData.SDFrame;
import RunningToolBox.UserSettingFrame;
import RunningToolBox.note.*;

import RunningToolBox.pet.PetFrame;
import RunningToolBox.pet.ToolBarFrame;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Locale;
import java.io.*;

public class InternalFrameDemo extends JFrame{  
	
	//保持人物頂置
	private boolean userSetAlwayseTop = false;
	//隨意行走
	private static boolean userSetAutoWalking = false;
	//自動開啟行事曆
	private  boolean userSetAutoOPenCalender = false;
	//自動開啟系統資訊
	private boolean userSetAutoOPenSystemInfo = false;
	
	private JCheckBox autoWalkingButton;
	private JCheckBox aoCalenderButton;
	private JCheckBox aoSystemInfoButton;
	private JCheckBox aoMemoButton;
	private JCheckBox aoImageEditButton;
	private JCheckBox alwayseTopButton;
		
    static JDesktopPane desktop;
	static SettingFile settingFile = new SettingFile();
	
	final Note localNote = new Note();
	Note[] note = new Note[5];
	boolean[] usingNote = new boolean[5];
	boolean showingNote;
	boolean drawFrameExist;
	
	DrawFrame drawFrame = new DrawFrame();
	final CalendarFrame calendar = new CalendarFrame();
	final static SDFrame sdFrame = new SDFrame();
	final static CalendarFile calendarFile = new CalendarFile();
	
	//	Pet相關
	final static PetFrame toolBoxPet= new PetFrame();
	static ToolBarFrame toolBarVertical;//直式 petPose= 3 4的時候用
	static ToolBarFrame toolBarHorizontal;//橫式
	static boolean toolFrameVisibleFlag = false;
	final static int EDGE = 360;//給ToolBarFrame用, 判斷是否需要切換直/橫
	static final Random ranNum = new Random(); //產生亂數種子(桌寵行走用)
	static int toolBarAndPetGarphWidth;//toolBar與Petframe的寬度之差距(橫式用)
	static int toolBarAndPetGarphHeight;//toolBar與Petframe的長度之差距(直式用)
	//點擊event相關
	static Point mouseXY;//鼠標的座標
	static boolean dragFlag = false;
	static int toolBarVisiableFlag = 0;//現在是顯示哪一個toolBar(0= 沒有 1=Horizontal 2=Vertical)
	
	
    public InternalFrameDemo() {
        super("InternalFrameDemo");
		
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		//height of the task bar
		Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
		int taskBarSize = scnMax.bottom;
        setBounds(0, 0, screenSize.width, screenSize.height - taskBarSize);

        //Set up the GUI.
        desktop = new JDesktopPane(); //a specialized layered pane
		desktop.setOpaque(false);
		
		NotifyThreadClass notifyThreadClass = new NotifyThreadClass();
		notifyThreadClass.start();
		
		
		//載入設定
		loadSettingFile();
		
		//套用使用者設定
		setAlwaysOnTop(userSetAlwayseTop);
		toolBoxPet.setUserSetAutoWalking(userSetAutoWalking);
		
		//create initial "window"
		creatWhiteFrame();
		
		createSDFrame();
        createCalendarFrame();
		
		initialNoteFrame();
		
		createPetFrame();
		creatToolBarFrame();
		
        setContentPane(desktop);
        //setJMenuBar(createMenuBar());
		
        //Make dragging a little faster but perhaps uglier.
        desktop.setDragMode(JDesktopPane.LIVE_DRAG_MODE);
    }
	
	protected void creatWhiteFrame()
	{
		final JInternalFrame frame = new JInternalFrame();
        frame.setVisible(true); //necessary as of 1.3		
        desktop.add(frame);
	}
	
    //Create a new Calendar frame.
    protected void createCalendarFrame() 
	{	
		//Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		//Dimension frameSize = calendar.getSize();
		//calendar.setLocation(screenSize.width - frameSize.width - 10, 60);
		
		int[] site = settingFile.loadSite("calendar");
		calendar.setLocation(site[0], site[1]);
		
        calendar.setVisible(userSetAutoOPenCalender);		
        desktop.add(calendar);
    }
	
	//Create a new creatUserSetting frame.
	protected void creatUserSettingFrame() 
	{		
		boolean[] b = {userSetAlwayseTop, userSetAutoWalking, userSetAutoOPenCalender, userSetAutoOPenSystemInfo };
        System.out.println("boolean array");
		UserSettingFrame usFrame = new UserSettingFrame(b);	
		System.out.println("new UserSettingFrame successed!!");
		
		usFrame.addWindowListener( new WindowListener() {
							public void windowClosing(WindowEvent e) {}
							public void windowClosed(WindowEvent e) { 
								loadSettingFile();
								setAlwaysOnTop(userSetAlwayseTop);
								toolBoxPet.setUserSetAutoWalking(userSetAutoWalking);
							}
							public void windowOpened(WindowEvent e) {}
							public void windowActivated(WindowEvent e) {}
							public void windowDeactivated(WindowEvent e) {}
							public void windowIconified(WindowEvent e) {}
							public void windowDeiconified(WindowEvent e) {}
						});
		usFrame.setVisible(true);
    }
	
	//Create a new System Data frame.
    protected void createSDFrame() 
	{
		refreshThreadClass dataRefreshThread = new refreshThreadClass(0);
		dataRefreshThread.start();
		
		refreshThreadClass cpuRefreshThread = new refreshThreadClass(1);
		
		int[] site = settingFile.loadSite("systemData");
		sdFrame.setLocation(site[0], site[1]);
		
        sdFrame.setVisible(userSetAutoOPenSystemInfo); //necessary as of 1.3
        desktop.add(sdFrame);
    }

	protected void initialNoteFrame() 
	{		
		//先把所有畫面初始化
		for(int i = 0 ; i < 5 ; i++){
			note[i] = new Note();
			desktop.add(note[i]);
		}
		
		int[] site = settingFile.loadSite("note");
		localNote.setLocation(site[0], site[1]);
    }
		
	protected void creatNoteFrame() 
	{		
		note[0] = new Note();	
		Point nowFrameXY = localNote.getLocation();
		note[0].setLocation(nowFrameXY.x, nowFrameXY.y);
		note[0].button.addActionListener(new AddNoteDrawFrameButtonHandler(0));
		note[0].addButton.addActionListener(new AddNoteButtonHandler());
		note[0].pencilButton.addMouseListener(new MouseNoteListenerDemo(0));
		note[0].setVisible(true);
		usingNote[0] = true;
		desktop.add(note[0]);
    }
	
	protected void createDrawFrame()
	{
		drawFrame = new DrawFrame();
		drawFrame.setVisible(false);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		//height of the task bar
		Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
		int taskBarSize = scnMax.bottom;
		Dimension drawSize = drawFrame.getSize();
		
        drawFrame.setLocation(screenSize.width/2 - drawSize.width/2, (screenSize.height - taskBarSize)/2 - drawSize.height/2);
		
		drawFrame.addInternalFrameListener(new InternalFrameAdapter() {
					public void internalFrameClosing(InternalFrameEvent e) {
						drawFrameExist = false;
					}
				});
		drawFrameExist = true;				
        desktop.add(drawFrame);
	}
	
	//Create a new PetFrame frame.
    protected void createPetFrame()
	{
		toolBoxPetWalkingThreadClass toolBoxPetWalkingThread = new toolBoxPetWalkingThreadClass();
		toolBoxPetWalkingThread.start();
		toolBoxPet.addMouseListener(new PetMouseClickHandler());
		toolBoxPet.addMouseMotionListener(new PetMouseClickHandler());
        toolBoxPet.setVisible(true);		
        desktop.add(toolBoxPet);
        try {
            toolBoxPet.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }
	
	//Create a new ToolBarFrame frame.
	protected void creatToolBarFrame()
	{
		Point p = toolBoxPet.getPetFramePosition();
		toolBarHorizontal = new ToolBarFrame( p.x-116, p.y-70, true );
		
		toolBarVertical = new ToolBarFrame( p.x-70, p.y-116, false );
		
        toolBarVertical.setVisible(false); //necessary as of 1.3	
		toolBarHorizontal.setVisible(false);
		
		
		desktop.add(toolBarHorizontal);
        desktop.add(toolBarVertical);
		//增加事件
		int num = toolBarHorizontal.geticonNumber(), i;
		for(i=0; i<num ; i++)
		{
			toolBarHorizontal.getLabelName(i).addMouseListener(new toolBarMouseClickHandler());
			toolBarVertical.getLabelName(i).addMouseListener(new toolBarMouseClickHandler());
		}

		toolBarAndPetGarphWidth = (toolBarHorizontal.getToolBarWidth() - toolBoxPet.getPetFrameWidth())/2;//橫式用
		toolBarAndPetGarphHeight = (toolBarVertical.getToolBarHeight() - toolBoxPet.getPetFrameHeight())/2;
	}
	
    //Quit the application.
    protected void quit() {
        System.exit(0);
    }

    private static void createAndShowGUI() {
        //Create and set up the window.
        InternalFrameDemo frame = new InternalFrameDemo();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setUndecorated(true);
		frame.setBackground(new Color(0, 0, 0, 0));
        //Display the window.
        frame.setVisible(true);
		
		frame.desktop.repaint();
    }

    public static void main(String[] args) {
		System.setProperty("sun.java2d.noddraw", "true"); // 防止激活输入法時白屏
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
				
	static class refreshThreadClass extends Thread {
		private final int threadNumber;

		public refreshThreadClass(int threadNumber) {
			super();
			this.threadNumber = threadNumber;
		}

		public void run() {
			while(true) {
				if(threadNumber == 0)
				{
					sdFrame.refreshData();
					desktop.repaint();
					try {
							Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}else if(threadNumber == 1){
					sdFrame.refreshCPU();
					desktop.repaint();
					try {
							Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
		
	}
	
	static class NotifyThreadClass extends Thread {
		public void run() {
			while(true) {
				String notifyStr = calendarFile.loadNotify();
				//System.out.println(notifyStr);
				
				if(notifyStr != null)	//有提醒
				{
					String[] notifyaArray = notifyStr.split(" ");
					String notifyText = null;
					String notifyTitle = notifyaArray[3];
					
					if(notifyaArray.length < 5)	//沒有Note內容
						notifyText = "" + " Empty note.";
					else{
						notifyText = "" + notifyaArray[4];
						for(int i = 5 ; i < notifyaArray.length ; i++)
						{
							notifyText += " " + notifyaArray[i];
						}
					}
					
					//取得當前時間
					Calendar calendar = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.US);
					String nowTimeStr = sdf.format(calendar.getTime());
					//System.out.println(nowTimeStr);
					//若時間與目前時間相同，則設定系統提醒
					if(nowTimeStr.equals(notifyaArray[0] + " " + notifyaArray[1] + " " + notifyaArray[2])){
						try {
							BufferedImage img = ImageIO.read(getClass().getResource("calendar/image/TrayLogo.png"));
							Image dimg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
							final TrayIcon trayIcon = new TrayIcon(dimg, "Running ToolBox");
							// get the SystemTray instance
							if (SystemTray.isSupported()) {
								SystemTray tray = SystemTray.getSystemTray();
								trayIcon.setImageAutoSize(true);
								try {
									tray.add(trayIcon);
									trayIcon.displayMessage(notifyTitle, notifyText, TrayIcon.MessageType.INFO);
								} catch (AWTException ae) {
									System.err.println("TrayIcon could not be added.");
								} 
							}
						} catch (IOException ex) {
							//System.out.println("Can't get TrayLogo.png");
						}
					}
				}	//end if
				
				try {
						Thread.sleep(30000);	//每半分鐘更新
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public class DeleteNoteButtonHandler implements ActionListener
	{
		private final int index;
		public DeleteNoteButtonHandler(int index)
		{
			this.index = index;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			note[index].setVisible(false);
			usingNote[index] = false;
			desktop.remove(note[index]);
		}
	}
	
	public class AddNoteButtonHandler implements ActionListener
	{
		private int unVisibleNote()
		{
			for(int i = 0 ; i < 5 ; i++)
			{
				if(note[i].isVisible() == false)
					return i;
			}
			return -1;
		}
		
		public void actionPerformed(ActionEvent e)
		{
			int index = unVisibleNote();
			//顯示的還不到五個
			if(index != -1){
				note[index] = new Note();	
				Point nowFrameXY = localNote.getLocation();
				note[index].setLocation(nowFrameXY.x, nowFrameXY.y);
				note[index].button.addActionListener(new AddNoteDrawFrameButtonHandler(index));
				note[index].addButton.addActionListener(new AddNoteButtonHandler());
				note[index].pencilButton.addMouseListener(new MouseNoteListenerDemo(index));
				note[index].setVisible(true);
				usingNote[index] = true;
				desktop.add(note[index]);
			}
		}
	}
	
	public class MouseNoteListenerDemo extends MouseAdapter
	{
		private final int index;
		public MouseNoteListenerDemo(int index)
		{
			this.index = index;
		}
		
		int count = 0;
		public void mouseClicked(MouseEvent e) {
			count = e.getClickCount();            
			if(count == 2)
			{ 
				note[index].drawFrame.setVisible(true);
				desktop.add(note[index].drawFrame);
				JFrame jf = new JFrame();
				jf.setSize(300, 200);
				//jf.setTitle("貼心小提示!!");
				//jf.setVisible(true);
				JOptionPane.showMessageDialog(jf, "請點選畫面決定要放置圖片的位置");				
				note[index].drawFrame.panel.getImageHave(1);
			
			}				
		}		
	}
		
	public class AddNoteDrawFrameButtonHandler implements ActionListener
	{
		private final int index;
		public AddNoteDrawFrameButtonHandler(int index)
		{
			this.index = index;
		}
		public void actionPerformed(ActionEvent e)
		{
			if(e.getActionCommand().equals("Draw"))
			{
				//note[index].drawFrame.release();
				note[index].drawFrame.setVisible(!note[index].drawFrame.isVisible());
				
				if(note[index].drawFrame.isVisible())
				{
					desktop.remove(note[index].drawFrame);
					
					Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
					int taskBarSize = scnMax.bottom;
					Dimension noteSize = note[index].getSize();
					Dimension drawSize = note[index].drawFrame.getSize();
					Point noteSite = note[index].getLocation();
					int frameX = 0;
					if(noteSite.x < screenSize.width/2)	//在畫面左側則往右長
						frameX = noteSite.x + noteSize.width + 30;
					else 							//在畫面右側則往左長
						frameX = noteSite.x - drawSize.width - 30;
					
					int frameY = 0;
					if(noteSite.y < (screenSize.height - taskBarSize)/2)
						frameY = noteSite.y;
					else
						frameY = noteSite.y + noteSize.height - drawSize.height;
						
					System.out.println(" " + frameX + " " + frameY);
					note[index].drawFrame.setLocation(frameX, frameY);
			
					desktop.add(note[index].drawFrame);
				}
			}
		}
	}
	
	//ToolBar click event
	public class toolBarMouseClickHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent event)
		{
			//System.out.println("Tool bar click!!");
			ToolBarFrame tmp = null;
			if(toolBarVisiableFlag == 1)
			{
				//System.out.println("是toolBarHorizontal");
				tmp = toolBarHorizontal;
			}else //(toolBarVisiableFlag==2)
			{
				//System.out.println("是toolBarVertical");
				tmp = toolBarVertical;
			}
			
			if(event.getSource() == tmp.getLabelName(0))
			{	//月曆
				//System.out.println("calendar setVisible!");
				calendar.setVisible(!calendar.isVisible());
				try {
					calendar.setSelected(true);
				} catch (java.beans.PropertyVetoException e) {}
		
			}else if(event.getSource() == tmp.getLabelName(1))
			{	//系統資訊
				sdFrame.setVisible(!sdFrame.isVisible());
				try {
					sdFrame.setSelected(true);
				} catch (java.beans.PropertyVetoException e) {}
			}else if(event.getSource() == tmp.getLabelName(2))
			{	//便條紙
				showingNote = !showingNote;
				if(showingNote){
					//System.out.println("showing Note");
					int count = 0;
					for(int i = 0 ; i < 5 ; i++)
					{
						if(usingNote[i] == true){
							note[i].setVisible(showingNote);
							count++;
							try {
								sdFrame.setSelected(true);
							} catch (java.beans.PropertyVetoException e) {}
						}
					}
					if(count == 0)
						creatNoteFrame();
						
				}else{
					//System.out.println("hiding Note");
					for(int i = 0 ; i < 5 ; i++)
					{
						if(usingNote[i] == true)
							note[i].setVisible(showingNote);
					}
				}
			}else if(event.getSource() == tmp.getLabelName(3))
			{	//繪圖版
				if(drawFrameExist)
					drawFrame.setVisible(!drawFrame.isVisible());
				else
					createDrawFrame();
			}else if(event.getSource() == tmp.getLabelName(4))
			{	//系統設定
				creatUserSettingFrame();
			}else if(event.getSource() == tmp.getLabelName(5))
			{
				//System.out.println("Bye bye!!");
				quit();
			}
		}
	}
	
	//桌寵的click&dragging事件
	public class PetMouseClickHandler extends MouseAdapter
	{
		//單純點擊才會呼叫
		public void mouseClicked(MouseEvent event)
		{
			System.out.println("\nmouseClicked!!");
			if(toolBoxPet.isFallDownFlag == false)
				toolFrameVisibleFlag = !toolFrameVisibleFlag;
			
			if(toolFrameVisibleFlag)
			{
				//System.out.println("toolBar顯示! 停止移動!");
				toolBoxPet.isWalking= false;
				//System.out.println("tool is visible!!");
				int currtnePose = toolBoxPet.getPetPose();//取得目前姿態
				Point p = toolBoxPet.getPetFramePosition();
				int toolPositionX = 0;
				int toolPositionY = 0;
				//邊界
				int boundry =0 ;
				//修正桌寵在地面上時點擊維持走路姿態(==6/14更新==)
				
				if(currtnePose<3)
				{
					toolBoxPet.setPetPose(0);
					toolBoxPet.stand();
				}
				
				if(currtnePose==3 || currtnePose==4)
				{
					//System.out.println("toolBar是直的");
					toolBarVisiableFlag = 2;
					toolBarVertical.setVisible(toolFrameVisibleFlag);
					toolBarHorizontal.setVisible(!toolFrameVisibleFlag);
					//設定y座標的邊界
					boundry = toolBoxPet.getEdge().y + toolBoxPet.getPetFrameHeight() -toolBarVertical.getToolBarHeight();
				}
				else
				{
					//System.out.println("toolBar是橫的");
					toolBarVisiableFlag = 1;
					toolBarHorizontal.setVisible(toolFrameVisibleFlag);
					toolBarVertical.setVisible(!toolFrameVisibleFlag);
					//設定x座標的邊界
					boundry = toolBoxPet.getEdge().x + toolBoxPet.getPetFrameWidth() -toolBarHorizontal.getToolBarWidth();
				}
				switch(currtnePose)
				{
					case 0:case 1:case 2: //桌寵在工具列(地面)上
					{
						//System.out.println("case 012");
						toolPositionX = p.x-toolBarAndPetGarphWidth;
						toolPositionY = p.y- toolBarHorizontal.getToolBarHeight();
						//邊界處理
						if(toolPositionX <0 )
							toolPositionX= 0;
						else if(toolPositionX> boundry)
							toolPositionX = boundry;
						if(toolPositionY< 0)//拖移發生後
							toolPositionY = toolBoxPet.getPetFrameHeight();
						toolBarHorizontal.setLocationXY( toolPositionX, toolPositionY );
						break;
					}
					case 3://左側爬牆
					{
						toolPositionX = p.x+ toolBarVertical.getToolBarWidth();
						toolPositionY = p.y- toolBarAndPetGarphHeight;
						//邊界處理
						if(toolPositionY <0 )
							toolPositionY= 0;
						else if(toolPositionY> boundry)
							toolPositionY = boundry;
							
						//System.out.println("case 3");
						toolBarVertical.setLocationXY( toolPositionX, toolPositionY );
						break;
					}
					case 4://右側爬牆
					{
						toolPositionX = p.x- toolBarVertical.getToolBarWidth();
						toolPositionY = p.y- toolBarAndPetGarphHeight;
						//邊界處理
						if(toolPositionY <0 )
							toolPositionY= 0;
						else if(toolPositionY> boundry)
							toolPositionY = boundry;
							
						//System.out.println("case 4");
						toolBarVertical.setLocationXY( toolPositionX, toolPositionY );
						break;
					}
					case 5:case 6://在天花板
					{
						toolPositionX = p.x- toolBarAndPetGarphWidth;
						toolPositionY = p.y+ toolBarHorizontal.getToolBarHeight();
						//邊界處理
						if(toolPositionX <0 )
							toolPositionX= 0;
						else if(toolPositionX> boundry)
							toolPositionX = boundry;
							
						//System.out.println("case 5");
						toolBarHorizontal.setLocationXY( toolPositionX , toolPositionY );
						break;
					}
					
				}
				desktop.repaint();
			}
			else
			{
				//System.out.println("tool is UNvisible!!");
				toolBarVisiableFlag =0;
				toolBarHorizontal.setVisible(false);
				toolBarVertical.setVisible(false);
				if(toolBoxPet.userSetAutoWalking)
				{
					//System.out.println("恢復移動1");
					toolBoxPet.isWalking = true;
				}
			}
		}

		public void mousePressed(MouseEvent event) {
			mouseXY = event.getPoint();
			if(toolBoxPet.isFallDownFlag == true)
			{
				toolBoxPet.isFallDownFlag = false;
			}
		}
		
		public void mouseReleased(MouseEvent event)
		{
			//System.out.println("mouseReleased!!");
			mouseXY = null;
			Point ReleasedXY = event.getPoint();;
			//toolBoxPet.setPet(toolBoxPet.getPetPose()*2);
			if(dragFlag == true || toolBoxPet.isFallDownFlag == true)
			{	
				Point position = toolBoxPet.getPetFramePosition();
				Point edge = toolBoxPet.getEdge();

				if(position.y == 0)//在天花板
				{
					//System.out.println("黏在天花板");
					toolBoxPet.setPetPose(5);
				}
				else if( position.x == 0)//在左牆
				{
					//System.out.println("黏在左牆");
					toolBoxPet.setPetPose(3);
				}
				else if( position.x == edge.x )//在右牆
				{
					//System.out.println("黏在右牆");
					toolBoxPet.setPetPose(4);
				}
				else if(position.y < toolBoxPet.getEdge().y )//只有不靠在邊緣上才會掉落 && position.x >0 && position.x < edge.x
				{
					toolBoxPet.isFallDownFlag = true;
					toolBoxPet.setPetPose(7);//(==掉落修改==)
				}	
				dragFlag = false;
				//toolBoxPet.setPetPose(0);
				toolBoxPet.stand();
				desktop.repaint();
				toolBoxPet.isWalking = toolBoxPet.userSetAutoWalking;
				//System.out.println("恢復移動2");
			}
		}
		
		//在螢幕中央範圍會掉下來
		public void mouseDragged(MouseEvent event)
		{
			dragFlag = true;//開始拖移
			toolFrameVisibleFlag = false;//收起工具列
			toolBarHorizontal.setVisible(false);
			toolBarVertical.setVisible(false);
			toolBoxPet.isWalking = false;//停止移動
			//System.out.println("Dragging pet!");
			toolBoxPet.setPet(1);
			toolBoxPet.setPetPose(0);
			desktop.repaint();
			
			Point draggedPoint = event.getLocationOnScreen();
			toolBoxPet.movePet(draggedPoint.x - mouseXY.x , draggedPoint.y - mouseXY.y);
		}
	}

	static class toolBoxPetWalkingThreadClass extends Thread {
		static int num = 0; //一次走5~ 15步 
		static int clockwiseFlag = ranNum.nextInt(2); //順時針走/逆時針走 (0= 順  1= 逆) 
		
		public void run()
		{
			while(true){
				if(userSetAutoWalking == true && toolFrameVisibleFlag == false)
				{
					//System.out.println("toolBoxPetWalkingThreadClass run()!!");
					if(dragFlag == false && toolBoxPet.isWalking == true && toolBoxPet.isFallDownFlag ==false ){//(==6/14更新==)
						if(num == 0)
						{
							//System.out.println("停下");
							toolBoxPet.stand();//停下
							desktop.repaint();
							try{
								Thread.sleep(1500);
							}catch (InterruptedException e){
								e.printStackTrace();
							}
							clockwiseFlag = ranNum.nextInt(2); //重設方向
							num = 5 + ranNum.nextInt(7);//重新設置步數
							//System.out.println("重新設置方向完成, " + clockwiseFlag + "(0=左/ 1=右)");
							//System.out.println("重新設置步數完成, " + num + "步");
						}else{
							if(toolBoxPet.getPetPose() == 0 && toolBoxPet.isWalking == true)
							{
								//System.out.printfln("開始行走！");
								toolBoxPet.setPetPose(clockwiseFlag + 1);
							}
							while(toolBoxPet.isWalking == true && num != 0 && dragFlag == false)
							{
								//System.out.println("走走走");
								//如果在停止狀態，之前可能是拖移或掉落，強制離開
								if(toolBoxPet.getPetPose() == 0 || toolBoxPet.getPetPose() == 7 )//(==掉落修改==)
									break;
									
								if(clockwiseFlag == 0)
								{
									toolBoxPet.walkingClockwise(true);//左走-左腳跨一步
								}else{
									toolBoxPet.walkingAanticlockwise(true);//右走-左腳跨一步
								}
								desktop.repaint();
								
								try{
									Thread.sleep(500);
								}catch (InterruptedException e){
									e.printStackTrace();
								}
								
								if(toolBoxPet.getPetPose() == 0|| toolBoxPet.getPetPose() == 7)//(==掉落修改==)
									break;
									
								if(clockwiseFlag == 0)
								{
									toolBoxPet.walkingClockwise(false);//右腳跨一步
								}else{
									toolBoxPet.walkingAanticlockwise(false);//右腳跨一步
								}
								desktop.repaint();
								
								try{
									Thread.sleep(500);
								}catch (InterruptedException e){
									e.printStackTrace();
								}
								//System.out.println("num:" + num);
								num--;
							}	//end walk while
							
							num = 0;
						}
					}
					else if(toolBoxPet.isFallDownFlag==true)
					{
						toolBoxPet.fallDown();
						desktop.repaint();
						try{
							Thread.sleep(250);//(==掉落修改==)
						}catch (InterruptedException e){
							e.printStackTrace();
						}
					}
					else{
						num = 0;
						try{
							Thread.sleep(500);
						}catch (InterruptedException e){
							e.printStackTrace();
						}
					}
					
				}
				else//處理userSetAutoWalking=false 時不能掉落的問題
				{
					while(toolBoxPet.isFallDownFlag == true)
					{
						toolBoxPet.fallDown();
						desktop.repaint();
						try{
							Thread.sleep(250);//(==掉落修改==)
						}catch (InterruptedException e){
							e.printStackTrace();
						}
					}
				}//end if-else
			}//end while(true)
		}//end run
	}

	public void loadSettingFile()
	{
		try {
			FileReader fr = new FileReader("RunningToolBox/userSetting.txt");
			String file = "";
			BufferedReader br = new BufferedReader(fr);
			file = br.readLine();
			
			String[] settingArray = file.split(" ");
			
			userSetAlwayseTop  = Boolean.valueOf(settingArray[0]);
			userSetAutoWalking = Boolean.valueOf(settingArray[1]);
			userSetAutoOPenCalender = Boolean.valueOf(settingArray[2]);
			userSetAutoOPenSystemInfo = Boolean.valueOf(settingArray[3]);

			//關檔
			try {
				fr.close();
				fr = null;
			} catch (IOException e) {
				
			}
		} catch (IOException e) {
			userSetAlwayseTop  = false;
			userSetAutoWalking = false;
			userSetAutoOPenCalender = false;
			userSetAutoOPenSystemInfo = false;
		}
		
		System.gc();
	}

}