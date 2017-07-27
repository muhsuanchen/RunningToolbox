package RunningToolBox;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.BorderFactory; 
import java.io.*;
import java.text.*;

public class UserSettingFrame extends JFrame {
	private static boolean userSetAlwayseTop= false;
	private static boolean userSetAutoWalking= true;
	private static boolean userSetAutoOPenCalender = false;
	private static boolean userSetAutoOPenSystemInfo= false;

	private static final int settungNumber=6;
	
	private JCheckBox autoWalkingButton;
	private JCheckBox aoCalenderButton;
	private JCheckBox aoSystemInfoButton;
	private JCheckBox aoMemoButton;
	private JCheckBox aoImageEditButton;
	private JCheckBox alwayseTopButton;
	private JPanel contentPanel;
	private JPanel checkBoxPanel;
	private JPanel buttonPanel;
	private JButton saveButton = new JButton("儲存");
	private JButton cancelButton = new JButton("取消");
	
	static String[] labelText = {"系統置頂", "桌寵隨意行走", "自動開啟行事曆", "自動開啟系統資訊"};
	
	static final int frameWidth = 200, frameHeight = 200;

	public UserSettingFrame(boolean [] settingArray)
	{
		super("Setting - Running ToolBox");
		setSetting(settingArray);
		setPanel();
		//System.out.println("setPanel succeed");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		//取得螢幕size
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();
		//取得工作列size
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle bounds = ge.getMaximumWindowBounds();
		int taskBarSize = bounds.height;
		int winWidth = screenSize.width;
		//讓視窗在螢幕的正中間出現
		int x = (winWidth- frameWidth)/2;
		int y = (taskBarSize- frameHeight)/2;
		setBounds( x , y , frameWidth, frameHeight);
		setResizable(false);//不可變動視窗大小
		setVisible(true);
	}
	
	public void setSetting(boolean [] settingArray)
	{
		System.out.println("loading array");
		userSetAlwayseTop = settingArray[0];
		userSetAutoWalking = settingArray[1];
		userSetAutoOPenCalender = settingArray[2];
		userSetAutoOPenSystemInfo = settingArray[3];
		
		//System.out.println("load array SUCCEED!");
	}
	
	private void setPanel()
	{
		contentPanel = new JPanel();
		contentPanel.setBorder( new EmptyBorder(10, 10, 10, 10) );
		contentPanel.setLayout( new BorderLayout() );
		setContentPane(contentPanel);
		
		//設置JCheckBox
		checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout((new GridLayout(4, 1)));
		checkBoxPanel.setBorder(null);
		
		alwayseTopButton = new JCheckBox(labelText[0]);
		alwayseTopButton.setSelected(userSetAlwayseTop);
		alwayseTopButton.addItemListener(new checkBoxItemListener());
		
		autoWalkingButton = new JCheckBox(labelText[1]);
		autoWalkingButton.setSelected(userSetAutoWalking);
		autoWalkingButton.addItemListener(new checkBoxItemListener());
		
		aoCalenderButton = new JCheckBox(labelText[2]);
		aoCalenderButton.setSelected(userSetAutoOPenCalender);
		aoCalenderButton.addItemListener(new checkBoxItemListener());
		
		aoSystemInfoButton = new JCheckBox(labelText[3]);
		aoSystemInfoButton.setSelected(userSetAutoOPenSystemInfo);
		aoSystemInfoButton.addItemListener(new checkBoxItemListener());
		
		checkBoxPanel.add(alwayseTopButton);
		checkBoxPanel.add(autoWalkingButton);
		checkBoxPanel.add(aoCalenderButton);
		checkBoxPanel.add(aoSystemInfoButton);
		
		contentPanel.add(checkBoxPanel, BorderLayout.NORTH);
		
		
		//設置button
		buttonPanel = new JPanel();
		buttonPanel.setLayout( new BorderLayout() );
		buttonPanel.setBorder(null);
		saveButton.addActionListener(new ButtonActionListener());
		saveButton.addMouseListener(new ButtonMouseListener());
		cancelButton.addActionListener(new ButtonActionListener());
		cancelButton.addMouseListener(new ButtonMouseListener());
		buttonPanel.add(saveButton, BorderLayout.WEST);
		buttonPanel.add(cancelButton, BorderLayout.EAST);

		contentPanel.add(buttonPanel, BorderLayout.SOUTH);
	}
	public class checkBoxItemListener implements ItemListener
	{
		public void itemStateChanged (ItemEvent e) {
			JCheckBox tmpBox= (JCheckBox)e.getSource();

			if (tmpBox == alwayseTopButton)
			{
				userSetAlwayseTop = !userSetAlwayseTop;
			}
			else if (tmpBox == autoWalkingButton)
			{
				//System.out.println("autoWalkingButton");
				userSetAutoWalking = !userSetAutoWalking;
			}
			else if (tmpBox == aoCalenderButton)
			{
				userSetAutoOPenCalender = !userSetAutoOPenCalender;
			}
			else if (tmpBox == aoSystemInfoButton)
			{
				userSetAutoOPenSystemInfo = !userSetAutoOPenSystemInfo;
			}
		}
	}
	
	public class ButtonActionListener implements ActionListener
	{
	
		public void actionPerformed(ActionEvent event) 
		{
			if(event.getSource() == saveButton)
			{
				System.out.println("click save");
				saveFile();
				JOptionPane.showMessageDialog( UserSettingFrame.this, "自動開啟畫面的設定要重開本系統才會生效!" );
			}
			dispose();
		}
	}
	
	public class ButtonMouseListener extends MouseAdapter
	{
		public void mouseEntered(MouseEvent e) {
			setCursor (Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
		
		public void mouseExited(MouseEvent e) {
			setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	public void saveFile()
	{
		String text = userSetAlwayseTop + " " + userSetAutoWalking + " " + userSetAutoOPenCalender + " " + userSetAutoOPenSystemInfo;

		try{
			PrintWriter file = new PrintWriter("RunningToolBox/userSetting.txt");
			file.print(text);
			file.close();
		} catch (IOException except){
			except.printStackTrace();
		}

		System.gc();

	}
}