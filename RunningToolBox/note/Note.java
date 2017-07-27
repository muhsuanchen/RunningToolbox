package RunningToolBox.note;

import RunningToolBox.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*; 
import javax.swing.text.html.HTMLEditorKit; 
import javax.swing.text.html.InlineView; 
import javax.swing.text.html.ParagraphView;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.datatransfer.*;

import javax.imageio.ImageIO;
import java.lang.*;
import java.io.*;

public class Note extends JInternalFrame
{
	static int noteCount = 0;
	
	private JScrollPane jsp;
	private JTextPane text;
	//private JPanel textPanel;
	public JButton button;
	private JButton button2;
	BufferedImage bufImg;
	JLabel picLabel;
	
	final JSlider slider = new JSlider(100, 255, 200);
	private JButton settingButton;
	private Box colorBox;
	private Component sliderStruct;
	private Component colorStruct;
	private Component tailStruct;
	private int userColor = 0;
	private int userAlpha = 150;
	private JPanel contentPane;
	public JButton pencilButton = new JButton();
	public DrawFrame drawFrame = new DrawFrame();//面板

	int[][] userBackgroundColor = {{150, 150, 200}, {150, 175, 200}, {150, 200, 200}, {150, 200, 150},
								   {200, 200, 200}, {200, 200, 125}, {200, 150, 150}, {200, 150, 200}, };
	Color userFontColor = Color.black;	
	int colorTotal = 8;	//共四種佈景
	int settingHeight = 59;	//設定欄的總高度
	boolean settingShowFlag = false;
	
	SettingFile settingFile = new SettingFile();
	
	Font defaultFont = new Font("Dialog", Font.BOLD, 14);
	
	static Point mouseDownCompCoords;
	public JButton addButton = new JButton();
	int frameWidth = 250, frameHeight = 250;
	int nowFrameX, nowFrameY;
	
	//private int thick = 5;
	int x = 10;
	AlphaComposite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
	
	public Note()
	{
		super("NOTE");
		System.setProperty("sun.java2d.noddraw", "true"); // 防止激活输入法時白屏
		putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
		setLayout(null);
		
		//取得使用者自定義設定
		getUserAlpha();
		getUserColor();
		
		//放frame外觀設定
		contentSet();
		//放佈景設置的按鈕
		settingContentSet();
		
		//設定背景色跟字體色
		changeFontColor();
		changeBackgroundColor();
			
		javax.swing.plaf.InternalFrameUI ifu = getUI();
		((javax.swing.plaf.basic.BasicInternalFrameUI)ifu).setNorthPane(null);
		//setBorder(null);
		
		setBackground(new Color(0,0,0,0));
		setSize(frameWidth, frameHeight);
				
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
				settingFile.saveSite("note", nowFrameXY.x, nowFrameXY.y);
				
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
				nowFrameX = frameX;
				nowFrameY = frameY;
				
				Window win = SwingUtilities.getWindowAncestor(Note.this);
				win.repaint();
			}
		});
		
		noteCount++;
	}

	private void contentSet()
	{	
		contentPane = new JPanel(new BorderLayout());
		contentPane.setBorder( new EmptyBorder(10, 10, 10, 10) );
		contentPane.setLayout( new BorderLayout(0, 5) );
		setContentPane(contentPane);
		
		Box titleBox = Box.createHorizontalBox();
		
		try {
			BufferedImage img = ImageIO.read(getClass().getResource("addLogo.png"));
			Image dimg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
			addButton.setIcon(new ImageIcon(dimg));
		} catch (IOException ex) {
			addButton.setText(" ADD ");
			addButton.setFont(defaultFont);
		}
		addButton.addMouseMotionListener(new ButtonMouseMotionListener());
		addButton.addMouseListener(new ButtonMouseListener());
		addButton.addFocusListener(new comFocusListener());
		addButton.setForeground(Color.white);
		addButton.setContentAreaFilled(false);//button 透明
		addButton.setBorder(BorderFactory.createEmptyBorder());
		addButton.addActionListener(new ButtonHandler());
		titleBox.add(addButton);
		
		titleBox.add(Box.createHorizontalGlue());
		
		button = new JButton("Draw");
		button.addMouseMotionListener(new ButtonMouseMotionListener());
		button.addMouseListener(new ButtonMouseListener());
		button.addFocusListener(new comFocusListener());
		button.setContentAreaFilled(false);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setForeground(Color.white);
		button.addActionListener(new ButtonHandler());
		titleBox.add(button);
		
		titleBox.add(Box.createHorizontalStrut(30));
		
		button2 = new JButton("Image");
		button2.addMouseMotionListener(new ButtonMouseMotionListener());
		button2.addMouseListener(new ButtonMouseListener());
		button2.addFocusListener(new comFocusListener());
		button2.setContentAreaFilled(false);
		button2.setBorder(BorderFactory.createEmptyBorder());
		button2.setForeground(Color.white);
		button2.addActionListener(new ButtonHandler());
		titleBox.add(button2);
		
		titleBox.add(Box.createHorizontalGlue());
		
		settingButton = new JButton();
		try {
			BufferedImage img = ImageIO.read(getClass().getResource("setting.png"));
			Image dimg = img.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
			settingButton.setIcon(new ImageIcon(dimg));
		} catch (IOException ex) {
			settingButton.setText(" Setting ");
			settingButton.setFont(defaultFont);
		}
		settingButton.setBorder(BorderFactory.createEmptyBorder());
		settingButton.addActionListener(new SettingActionListener());
		settingButton.setOpaque(false);
		settingButton.addMouseListener(new ButtonMouseListener());
		settingButton.addMouseMotionListener(new ButtonMouseMotionListener());
		settingButton.addFocusListener(new comFocusListener());
		titleBox.add(settingButton);
		contentPane.add(titleBox, BorderLayout.NORTH);
		
		//文字輸入介面
		text = new JTextPane();
		text.setEditorKit(new HTMLEditorKit(){ 
           public ViewFactory getViewFactory(){ 
 
               return new HTMLFactory(){ 
                   public View create(Element e){ 
                      View v = super.create(e); 
                      if(v instanceof InlineView){ 
                          return new InlineView(e){ 
                              public int getBreakWeight(int axis, float pos, float len) { 
                                  return GoodBreakWeight; 
                              } 
                              public View breakView(int axis, int p0, float pos, float len) { 
                                  if(axis == View.X_AXIS) { 
                                      checkPainter(); 
                                      int p1 = getGlyphPainter().getBoundedPosition(this, p0, pos, len); 
                                      if(p0 == getStartOffset() && p1 == getEndOffset()) { 
                                          return this; 
                                      } 
                                      return createFragment(p0, p1); 
                                  } 
                                  return this; 
                                } 
                            }; 
                      } 
                      else if (v instanceof ParagraphView) { 
                          return new ParagraphView(e) { 
                              protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) { 
                                  if (r == null) { 
                                        r = new SizeRequirements(); 
                                  } 
                                  float pref = layoutPool.getPreferredSpan(axis); 
                                  float min = layoutPool.getMinimumSpan(axis); 
                                  // Don't include insets, Box.getXXXSpan will include them. 
                                    r.minimum = (int)min; 
                                    r.preferred = Math.max(r.minimum, (int) pref); 
                                    r.maximum = Integer.MAX_VALUE; 
                                    r.alignment = 0.5f; 
                                  return r; 
                                } 
 
                            }; 
                        } 
                      return v; 
                    } 
                }; 
            } 
			}); 
		text.setContentType("text/html"); 
		text.setOpaque(false);
		text.setText("Input text...");
		text.setFont(new Font("微軟正黑體", Font.BOLD, 15));
		text.addCaretListener(new TextAreaCaretListener());
		
		jsp = new JScrollPane(text);
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.getViewport().setOpaque(false);
		jsp.getVerticalScrollBar().addAdjustmentListener(new scrollAdjustmentListener());
		/*
		textPanel = new JPanel();
		textPanel.setBounds(0,40,300,210);
		textPanel.setLayout(new BorderLayout());
		textPanel.add(jsp);
		text.setBackground(Color.gray);
		textPanel.setPreferredSize(new Dimension(300, 200));*/
		contentPane.add(jsp, BorderLayout.CENTER);		
		
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
		
		slider.setPaintTicks(false);
		slider.setOpaque(false);
		slider.addMouseMotionListener(new ButtonMouseMotionListener());
		slider.addMouseListener(new SliderMouseListener());
		slider.addFocusListener(new comFocusListener());
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent cEvt) {
				SetUserAlpha( slider.getValue() );
				changeBackgroundColor();
				Note.this.repaint();

				Window win = SwingUtilities.getWindowAncestor(Note.this);
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
			JButton colorButton = new JButton("        ");
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
	
	//改背景色
	private void changeBackgroundColor()
	{
		contentPane.setBackground(new Color(userBackgroundColor[userColor][0], userBackgroundColor[userColor][1], userBackgroundColor[userColor][2], userAlpha));
		//panelTitle.setBackground(new Color(userBackgroundColor[userColor][0], userBackgroundColor[userColor][1], userBackgroundColor[userColor][2], userAlpha));
		jsp.setBackground( new Color(userBackgroundColor[userColor][0] + 50, userBackgroundColor[userColor][1] + 50, userBackgroundColor[userColor][2] + 50, 100) );
		//textPanel.setBackground(new Color(userBackgroundColor[userColor][0], userBackgroundColor[userColor][1], userBackgroundColor[userColor][2], userAlpha));
		settingButton.setBackground(new Color(userBackgroundColor[userColor][0], userBackgroundColor[userColor][1], userBackgroundColor[userColor][2], userAlpha));
	}
	
	//改字體顏色
	private void changeFontColor(){
		settingButton.setForeground(userFontColor);
		addButton.setForeground(userFontColor);
		button.setForeground(userFontColor);
		button2.setForeground(userFontColor);
	}
	
	private void SetUserAlpha(int newAlpha)
	{
		userAlpha = newAlpha;
	}
	
	private void SaveUserAlpha()
	{
		settingFile.saveSetting("note", userColor, userAlpha);
	}
	
	private void SetUserColor(int newColor)
	{
		userColor = newColor;
		settingFile.saveSetting("note", userColor, userAlpha);
	}

	private void getUserAlpha()
	{
		int[] setting = settingFile.loadSetting("note");
		userAlpha = setting[0];
		slider.setValue(userAlpha);
	}

	private void getUserColor()
	{
		int[] setting = settingFile.loadSetting("note");
		userColor = setting[1];
	}
	
	private void resizeFrame()
	{
		int settingH = (settingShowFlag)? settingHeight : 0 ;
		setSize(frameWidth, frameHeight + settingH);
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
			
			Window win = SwingUtilities.getWindowAncestor(Note.this);
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
			changeBackgroundColor();
			
			repaint();

			Window win = SwingUtilities.getWindowAncestor(Note.this);
			win.repaint();
		}
	}

	public class SliderMouseListener extends MouseAdapter
	{
		public void mouseReleased(MouseEvent e) {
			SaveUserAlpha();
			
			Window win = SwingUtilities.getWindowAncestor(Note.this);
			win.repaint();
		}
		
		public void mouseEntered(MouseEvent e) {
			setCursor (Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			Window win = SwingUtilities.getWindowAncestor(Note.this);
			win.repaint();
		}
		
		public void mouseExited(MouseEvent e) {
			setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			Window win = SwingUtilities.getWindowAncestor(Note.this);
			win.repaint();
		}
	}
	
	public class ButtonMouseListener extends MouseAdapter
	{
		public void mouseEntered(MouseEvent e) {
			setCursor (Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			Window win = SwingUtilities.getWindowAncestor(Note.this);
			win.repaint();
		}
		
		public void mouseExited(MouseEvent e) {
			setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			Window win = SwingUtilities.getWindowAncestor(Note.this);
			win.repaint();
		}
	}

	public class ButtonMouseMotionListener extends MouseMotionAdapter
	{
		public void mouseMoved(MouseEvent e) {
			Window win = SwingUtilities.getWindowAncestor(Note.this);
			win.repaint();
		}
		public void mouseDragged(MouseEvent e) {
			Window win = SwingUtilities.getWindowAncestor(Note.this);
			win.repaint();
		}
	}

	//主權事件
	public class comFocusListener implements FocusListener
	{
		public void focusGained(FocusEvent e) {
			Window win = SwingUtilities.getWindowAncestor(Note.this);
			win.repaint();
		}
		public void focusLost(FocusEvent e) {
			Window win = SwingUtilities.getWindowAncestor(Note.this);
			win.repaint();
		}
	}
	
	public class TextAreaCaretListener implements CaretListener
	{
		public void caretUpdate(CaretEvent e) {
			Window win = SwingUtilities.getWindowAncestor(Note.this);
			win.repaint();
		}
	}
	
	//scroll bar事件
	public class scrollAdjustmentListener implements AdjustmentListener
	{
		public void adjustmentValueChanged(AdjustmentEvent e) {
			Window win = SwingUtilities.getWindowAncestor(Note.this);
			win.repaint();
		}
	}
	
	class ButtonHandler implements ActionListener
	{
		//ColorBlock colorPanel = new ColorBlock();
		//JPanel upButton = new JPanel();
		//JButton itemEnd = new JButton("x");
		//JButton item1 = new JButton("Load");
		//JButton item2 = new JButton("Save");
		//JSlider Thickness = new JSlider(SwingConstants.HORIZONTAL,0,9,5);
		//JFrame demo = new JFrame("test");//開啟視窗
		boolean demoEnds = true;
		
		public void ButtonHandler()
		{
			
		}
		
		public void actionPerformed(ActionEvent e)
		{
			
			if(e.getActionCommand().equals("Draw"))
			{        
				//Timer timer = new Timer(0, null); 
				//panel.release();
				//demoEnds = true;
				//demo.setLayout(null);
				/*Thickness.setMinorTickSpacing(1);
				Thickness.setMajorTickSpacing(1);
				Thickness.setPaintTicks(false);
				Thickness.setPaintLabels(false);
				Thickness.addChangeListener(new javax.swing.event.ChangeListener() {
					public void stateChanged(javax.swing.event.ChangeEvent e) {
					  JSlider slider = (JSlider) e.getSource();
					  thick = slider.getValue();
					  panel.getThick (thick);
					  //System.out.printf("stateChanged %d\n",slider.getValue());
					}
				  });*/
				 
				
				/*upButton.setLayout(null);
				upButton.add( item1 );
				upButton.add( item2 );
				upButton.add( Thickness );*/
				
				/*colorPanel.addMouseListener(new MouseAdapter(){
							public void mousePressed(MouseEvent e) {
								//System.out.printf("aaaaaaa");
								if(e.getY()<31)
								panel.getColorX(e.getX());
							}
						});*/
				
				/*item1.setContentAreaFilled(false);
				item1.setBorderPainted(false);
				item1.setForeground(Color.gray);
				item2.setContentAreaFilled(false);
				item2.setBorderPainted(false);
				item2.setForeground(Color.gray);
				Thickness.setOpaque(false);

				panel.setLayout(null);
				Thickness.setBounds(200,0,120,45);
				//itemEnd.setBounds(0,0,50,45);
				item1.setBounds(0,0,100,45);
				item2.setBounds(100,0,100,45);*/

				//demo.add(upButton);
				//demo.add(panel);
				//demo.add(colorPanel);

				/*upButton.setBounds(0,0,345,45);
				colorPanel.setBounds(0,45,345,31);
				panel.setBounds(0,76,345,450);
				demo.pack();
				demo.setSize(345, 400); //視窗大小
				panel.release();
				demo.show();
				item1.addActionListener(new ButtonHandler());
				item2.addActionListener(new ButtonHandler());*/
			}
			else if(e.getActionCommand().equals("Image"))
			{
				JFileChooser chooser = new JFileChooser(); //創建文件選擇器
				chooser.setDialogTitle("Choose a picture");//設置標題
				chooser.setMultiSelectionEnabled(true);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("請選擇圖片檔","jpg","jpeg","png");//文件名稱過濾器
				chooser.setFileFilter(filter);//將文件名稱過濾器加入文件選擇器
				chooser.showOpenDialog(null); //打開選擇文件對話框,參數可設null或目前打開對話框的元件
				File file = chooser.getSelectedFile(); //file為所選擇的圖片檔
				
				if(file != null)
				{
					try {
						bufImg = ImageIO.read(file);  
						drawFrame.panel.getBufImg(bufImg);
					} catch (Exception e2) {
					//e2.printStackTrace();
					}
					
					ImageIcon pic = new ImageIcon(bufImg);
					picLabel = new JLabel();
					picLabel.setIcon(pic);
					
					ImageIcon iconButton = new ImageIcon( getClass().getResource("pencil.jpg") );
					//pencilButton = new JButton();
					pencilButton.setBorder(BorderFactory.createEmptyBorder());
					pencilButton.setPreferredSize(new Dimension(35,35));
					
					pencilButton.setContentAreaFilled(false);//設為透明
					pencilButton.setIcon(iconButton);
					//pencilButton.addMouseListener(new MouseListenerDemo());
					
					text.insertComponent(pencilButton);
					text.insertComponent(picLabel);
				}
				
			}else if(e.getActionCommand().equals("Load"))
			{
				/*JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose a picture");
				chooser.setMultiSelectionEnabled(true);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("請選擇圖片檔","jpg","jpeg","png");
				chooser.setFileFilter(filter);
				chooser.showOpenDialog(null);
				File file = chooser.getSelectedFile();
				
				if(file != null)
				{
					try {
					bufImg = ImageIO.read(file); 
					panel.getBufImg(bufImg);
					panel.getImageHave(1);
					} catch (Exception e2) {
						//e2.printStackTrace();
					}
					JFrame jf = new JFrame();
					jf.setSize(300, 200);
					//jf.setTitle("貼心小提示!!");
					//jf.setVisible(true);
					JOptionPane.showMessageDialog(jf,"請點選畫面決定要放置圖片的位置");*/
				}
				
			else if(e.getActionCommand().equals("Save"))
			{	
				
				/*BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
				panel.paint(image.createGraphics());
				System.out.printf("W: %d H: %d\n",panel.getWidth(),panel.getHeight());
				try {
					new File("testImg").mkdir();
					//File file=new File("testImg",(++num)+".png");
					File file=new File("testImg","test000.jpg");
					ImageIO.write(image, "jpg", file);
				} catch(Exception e2) {
					//e.printStackTrace();
				}*/
			}
		}
	}
		
}
