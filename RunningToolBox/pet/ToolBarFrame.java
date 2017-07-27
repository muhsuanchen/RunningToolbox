package RunningToolBox.pet;
import RunningToolBox.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.*;
/*
工具列，用來呼叫其他工具。
根據桌寵的位置會有相對應的位置
有直(70*360)、橫(360*70)兩種
*/
public class ToolBarFrame extends JInternalFrame
{
	
	private JPanel toolPanel;
	private static final int iconNumber=6;//icon圖片數量
	Image iconImage[];
	private static final String[] iconImageNames = { "image/日曆icon.png", "image/系統資訊icon.png", "image/便條紙icon.png", "image/圖片編輯icon.png", "image/系統設定icon.png", "image/關閉程式icon.png"};
	//工具列在螢幕的座標
	private int ToolBarPositionX;
	private int ToolBarPositionY;
	
	
	private JLabel toolIconLabel[];//(==☆新增==)
	
	//工具列大小
	private int TOOL_BAR_HEIGHT;
	private int TOOL_BAR_WIDTH;
	private boolean isHorizontal;
	
	public ToolBarFrame(int x, int y, boolean isH)
	{
		/*============初始化變數==============*/
		toolPanel = new JPanel();
		
		toolIconLabel = new JLabel[iconNumber];//(==☆新增==)
		iconImage = new Image[iconNumber];
		isHorizontal = isH;
		if(isHorizontal)//橫式的
		{
			TOOL_BAR_HEIGHT = 70;
			TOOL_BAR_WIDTH = 360;
		}
		else
		{
			TOOL_BAR_HEIGHT = 360;
			TOOL_BAR_WIDTH = 70;
		}
		//紀錄初始的座標
		ToolBarPositionX = x;
		ToolBarPositionY = y;
		
		/*============設定工具列==============*/
		//System.out.println("設定工具列");
		toolPanel.setSize( TOOL_BAR_WIDTH ,TOOL_BAR_HEIGHT);
		toolPanel.setOpaque(false);
		toolPanel.setBorder(null);
		toolPanel.setLayout(new FlowLayout( FlowLayout.CENTER ));
		//System.out.println("IconImage");
		loadingImage();
		setIconImage();
		setContentPane(toolPanel);
		/*============設置frame==============*/
		//拔除邊框
		javax.swing.plaf.InternalFrameUI ifu = getUI();
		((javax.swing.plaf.basic.BasicInternalFrameUI)ifu).setNorthPane(null);

		//設定frame參數
		setBorder(null);
		
		setBackground(new Color(0,0,0,0));
		setBounds(x , y , TOOL_BAR_WIDTH  , TOOL_BAR_HEIGHT);//右下方
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		setVisible(false);
	}
	
	//給 event使用(==☆新增==)
	public JLabel getLabelName(int i)
	{
		if(i>=0 && i<iconNumber)
			return toolIconLabel[i];
		else
			return null;
	}
	
	public int geticonNumber()
	{
		return iconNumber;
	}
	
	public int getToolBarWidth()
	{
		return TOOL_BAR_WIDTH;
	}
	
	public int getToolBarHeight()
	{
		return TOOL_BAR_HEIGHT;
	}
	
	public void setLocationXY(int nowX, int nowY)
	{
		ToolBarPositionX = nowX;
		ToolBarPositionY = nowY;
		setLocation( ToolBarPositionX , ToolBarPositionY );
		repaint();
	}

	//工具列顯示(==☆更新==)
	protected void setIconImage()
	{
		int i=0, j=10;
		if (isHorizontal)
		{
			for( i=0, j=10; i<iconNumber; i++,j+=60 )
			{
				toolIconLabel[i] = new JLabel();
				toolIconLabel[i].setIcon(new ImageIcon(iconImage[i]));
				toolIconLabel[i].setBounds(j, 0, 50,  50);
				toolPanel.add( toolIconLabel[i] );
			}
		}
		else
		{
			for( i=0, j=10; i<iconNumber; i++,j+=60 )
			{
				toolIconLabel[i] = new JLabel();
				toolIconLabel[i].setIcon(new ImageIcon(iconImage[i]));
				toolIconLabel[i].setBounds(0, j, 50,  50);
				toolPanel.add( toolIconLabel[i] );
			}
		}
	}
	
	private void loadingImage()
	{
		int i;
		try{
			for( i=0; i<iconNumber ;i++)
			{
				iconImage[i] = ImageIO.read(getClass().getResource( iconImageNames[i] ));
			}
		} catch (IOException ex) {
			//System.out.println("!!!!!");
		}
	}
	
	
}