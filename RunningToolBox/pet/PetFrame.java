package RunningToolBox.pet;

import RunningToolBox.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.*;
import static java.awt.GraphicsDevice.WindowTranslucency.*;

public class PetFrame extends JInternalFrame
{

	//主畫面相關
	private JPanel mainPanel;
	
	//隨意行走相關
	public static boolean userSetAutoWalking= false;
	public static boolean isWalking = false;
	private static final int stepDistance = 12;

	//使用者的螢幕的長寬
	private static int WINDOW_HEIGHT;
	private static int WINDOW_WIDTH;
	
	//整體大小(=桌寵大小)
	private static final int TOOL_BOX_HEIGHT= 128;
	private static final int TOOL_BOX_WIDTH= 128;
	
	//桌寵相關 128*128
	private static final int PET_HEIGHT= 128;
	private static final int PET_WIDTH= 128;
	private static final int imageNumber= 15;
	Image petImage[];
	ImageIcon petImageIcon[];
	JLabel petLabel;
	private static final String[] petImageNames = { "image/站立.png", "image/拖移.png", 
				"image/往左走1.png", "image/往左走2.png", "image/往右走1.png", "image/往右走2.png", 
				"image/左側爬牆1.png", "image/左側爬牆2.png", "image/右側爬牆1.png", "image/右側爬牆2.png", 
				"image/往左爬1.png", "image/往左爬2.png", "image/往右爬1.png", "image/往右爬2.png", "image/掉落.png"};
	//桌寵的姿態：0=站立(在地面) , 1=往左走(在地面) , 2=往右走(在地面) , 3=左側爬牆 , 4=右側爬牆 , 5=往左爬(天花板) , 6= 往右爬 (天花板), 7=掉落
	//移動時候的圖片：petImageIcon[getPetPose()*2] (走一步) & petImageIcon[getPetPose()*2+1] (走第二步)
	private int petPose = 0;
	public static boolean isFallDownFlag = false;//是否掉落中(==6/14更新==)
	
	/*座標相關*/
	static Point EDGE;//nowXY最右下的位置
	static Point nowXY;//現在視窗的位置(左上角的點)

	//建構元
	public PetFrame()
	{
		super("Running tool box");
		
		//宣告物件陣列
		petImage = new Image[imageNumber];
		petLabel = new JLabel();
		petImageIcon = new ImageIcon[imageNumber];
		
		//載入桌寵圖片
		loadingImage();
		
		//System.out.println("取得螢幕size");
		//取得螢幕size
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		//System.out.println("取得工作列size");
		//取得工作列size
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle bounds = ge.getMaximumWindowBounds();
		int taskBarSize = bounds.height;
		WINDOW_WIDTH = screenSize.width;
		WINDOW_HEIGHT = screenSize.height;
		//System.out.printf("WINDOW_WIDTH= %d,  WINDOW_HEIGHT= %d,  taskBarSize= %d\n", WINDOW_WIDTH, WINDOW_HEIGHT, taskBarSize);
		nowXY = new Point(WINDOW_WIDTH - TOOL_BOX_WIDTH , taskBarSize - TOOL_BOX_HEIGHT);
		EDGE = new Point(WINDOW_WIDTH - TOOL_BOX_WIDTH , taskBarSize - TOOL_BOX_HEIGHT);
		
		mainPanel = new JPanel( new BorderLayout() );
		mainPanel.setBackground(new Color(0,0,0,0));
		setContentPane(mainPanel);
		mainPanel.add(petLabel, BorderLayout.SOUTH);
		

		/*============設置圖片顯示==============*/
		//System.out.println("載入桌寵圖片");
		setPet(0);//載入桌寵圖片
		
		//System.out.println("設置圖片顯示");
		petLabel.setSize(PET_WIDTH, PET_HEIGHT);// 把Label的大小設置為桌寵的大小
		petLabel.setHorizontalAlignment(JLabel.CENTER);//置中
		/*petLabel.addMouseListener(new PetMouseClickHandler());//增加petLabel的event
		petLabel.addMouseMotionListener(new PetMouseClickHandler());*/
		petLabel.setVisible( true );
		/*============設置frame==============*/
		//拔除邊框
		javax.swing.plaf.InternalFrameUI ifu = getUI();
		((javax.swing.plaf.basic.BasicInternalFrameUI)ifu).setNorthPane(null);

		//設定frame參數
		setBorder(null);
		setBackground(new Color(0,0,0,0));
		setBounds(nowXY.x , nowXY.y , TOOL_BOX_WIDTH , TOOL_BOX_HEIGHT);//右下方
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		setVisible(true);
	}
	
	//(==6/15更新==)
	public void setUserSetAutoWalking(boolean autoWalk)
	{
		userSetAutoWalking = autoWalk;
		isWalking = userSetAutoWalking;
	}
	
	//設定桌寵顯示
	public void setPet(int pet)
	{
		petLabel.setIcon( petImageIcon[pet] );	// 背景圖片顯示在Label中
	}
	
	public void setwindowPosituonX(int newX)
	{
		nowXY.x = newX;
	}
	
	public void setwindowPosituonY(int newY)
	{
		nowXY.y = newY;
	}
	
	public void setPetPose(int flag)
	{
		petPose = flag;
	}
	
	public void movePet(int x, int y)
	{
		//邊界處理
		if(x< 0)
			x= 0;
		if(x> getEdge().x)
			x= getEdge().x;
		if(y< 0)
			y= 0;
		if(y> getEdge().y)
			y= getEdge().y;
		setwindowPosituonX(x);
		setwindowPosituonY(y);
		setLocation(nowXY.x , nowXY.y);
	}
	
	
	public int getPetPose()
	{
		return petPose;
	}
	
	public int getPetFrameHeight()
	{
		return TOOL_BOX_HEIGHT;
	}
	
	public int getPetFrameWidth()
	{
		return TOOL_BOX_WIDTH;
	}
	
	public Point getPetFramePosition()
	{
		return nowXY;
	}
	
	public Point getEdge()
	{
		return EDGE;
	}
	
	private void loadingImage()
	{
		int i;
		try{
			//System.out.println("設定petImage");
			for( i=0; i<imageNumber ;i++)
			{
				petImage[i] = ImageIO.read(getClass().getResource( petImageNames[i] ));
				petImageIcon[i] = new ImageIcon(petImage[i]);
			}
			//System.out.println("設定petImage完畢");
		} catch (IOException ex) {
			//System.out.println("!!!!!");
		}
	}

	/*==========隨意行走=======*/
	/*順時針：(平面出發)往左>往上>往右>往下*/
	public void walkingClockwise(boolean flag)
	{
		int currentPose = getPetPose();
		int newPositionX = nowXY.x;
		int newPositionY = nowXY.y;
		if(flag)//左腳
		{
			setPet(currentPose*2);
		}
		else//右腳
		{
			setPet(currentPose*2+1);
		}
		
		/*決定移動方向
		可能情況：　1=往左走(在地面) , 3=左側爬牆 , 4=右側爬牆 , 6= 往右爬 (天花板)(==☆新增==)*/
		switch(currentPose)
		{
			case 1:case 2://往左走(在地面)
				setPetPose(1);//處理卡原地或倒退嚕的問題
				newPositionX= nowXY.x -stepDistance;
				if(newPositionX <= 0)//邊界處理
				{
					//System.out.println("邊界");
					newPositionX = 0;
					setPetPose(3);
				}
				break;
			case 3://左側爬牆
				newPositionY = nowXY.y -stepDistance;
				if(newPositionY <= 0)//邊界處理
				{
					//System.out.println("邊界");
					newPositionY = 0;
					setPetPose(6);
				}
				break;
			case 4://右側爬牆
				newPositionY = nowXY.y +stepDistance;
				if(newPositionY >= EDGE.y)//邊界處理
				{
					//System.out.println("邊界");
					newPositionY = EDGE.y;
					setPetPose(1);
				}
				break;
			case 5:case 6://往右爬 (天花板)
				setPetPose(6);//處理卡原地或倒退嚕的問題
				newPositionX= nowXY.x +stepDistance;
				if(newPositionX >= EDGE.x)//邊界處理
				{
					//System.out.println("邊界");
					newPositionX = EDGE.x;
					setPetPose(4);
				}
				
				break;	
			default:
				//System.out.println("走走走" + currentPose);
			break;	
		}
		//currentPose= getPetPose();
		
		//座標更新
		setwindowPosituonX(newPositionX);
		setwindowPosituonY( newPositionY );
		setLocation(nowXY.x , nowXY.y);
		repaint();
	}
	
	/*逆時針：(平面出發)往右>往上>往左>往下(==☆新增==)*/
	public void walkingAanticlockwise(boolean flag)
	{
		int currentPose= getPetPose();
		int newPositionX = nowXY.x;
		int newPositionY = nowXY.y;
		if(flag)//左腳
		{
			setPet(currentPose*2);
		}
		else//右腳
		{
			setPet(currentPose*2+1);
		}
		
		//可能情況： 2=往右走(在地面) , 3=左側爬牆 , 4=右側爬牆 , 5=往左爬(天花板)
		switch(currentPose)
		{
			case 1:case 2://往右走(在地面)
				setPetPose(2);//處理卡原地或倒退嚕的問題
				newPositionX = nowXY.x +stepDistance;
				if(newPositionX >= EDGE.x)//邊界處理
				{
					//System.out.println("邊界");
					newPositionX = EDGE.x;
					setPetPose(4);
				}
			break;
			
			case 3://左側爬牆(下
				 newPositionY= nowXY.y +stepDistance;
				 if(newPositionY >= EDGE.y)//邊界處理
				{
					//System.out.println("邊界");
					newPositionY = EDGE.y;
					setPetPose(2);
				}
			break;
			
			case 4://右側爬牆(上
				newPositionY= nowXY.y -stepDistance;
				if(newPositionY <= 0)//邊界處理
				{
					//System.out.println("邊界");
					newPositionY = 0;
					setPetPose(5);
				}
			break;
			
			case 5:case 6://往左爬(天花板)
				setPetPose(5);//處理卡原地或倒退嚕的問題
				newPositionX = nowXY.x -stepDistance;
				if(newPositionX <= 0)//邊界處理
				{
					//System.out.println("邊界");
					newPositionX = 0;
					setPetPose(3);
				}
			break;
			default:
				//System.out.println("走走走" + currentPose);
			break;	
		}
		//currentPose= getPetPose();//重新取得
		
		//座標更新
		setwindowPosituonX( newPositionX );
		setwindowPosituonY( newPositionY );
		setLocation(nowXY.x , nowXY.y);
		repaint();
	}
	
	//(==6/15更新==)
	public void stand()
	{
		int flag = getPetPose();
		if(flag >= 3 && flag <= 6)
			setPet(flag*2 + 1);//持續攀爬狀態
		else if(flag==7)//持續掉落狀態(==掉落修改==)
			setPet(14);
		else
			setPet(0);//回復站姿
		repaint();
	}	
	//(==6/15更新==)
	public void fallDown()
	{
		int newPositionX = nowXY.x;
		int newPositionY = nowXY.y;
		
		setPet(14);//掉落姿態
		
		newPositionY += stepDistance*4;//(==掉落修改==)
		
		if(newPositionY>= EDGE.y)//(==掉落修改==)
		{
			newPositionY = EDGE.y;
			setPetPose(0);
			isFallDownFlag=false;
			stand();
		}
		setwindowPosituonX( newPositionX );
		setwindowPosituonY( newPositionY );
		setLocation(nowXY.x , nowXY.y);
		
		repaint();
		
		
	}
	
	
}