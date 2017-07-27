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

public class DrawFrame extends JInternalFrame 
{
	public DrawPanel panel = new DrawPanel();
	ColorBlock colorPanel = new ColorBlock();
	JSlider Thickness = new JSlider(SwingConstants.HORIZONTAL,0,9,5);
	private int thick = 5;
	JPanel upButton = new JPanel();
	public JButton item1 = new JButton("Load");
	public JButton item2 = new JButton("Save");
	BufferedImage bufImg;
	/*class ButtonHandler 
	{
		public void actionPerformed(ActionEvent e)
			{
			}
	}*/
	public void release()
	{
		panel.release();
	}
	
	public DrawFrame()
	{
		super("DrawFrame",true,true);
		panel.release();
		setLayout(null);
		Thickness.setMinorTickSpacing(1);
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
		  });
		upButton.setLayout(null);
		upButton.add( item1 );
		upButton.add( item2 );
		upButton.add( Thickness );
		colorPanel.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				//System.out.printf("aaaaaaa");
				if(e.getY()<31)
				panel.getColorX(e.getX());
			}
		});
		item1.addActionListener(new AddLoadButtonHandler());
		item2.addActionListener(new AddSaveButtonHandler());
		item1.setContentAreaFilled(false);
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
		item2.setBounds(100,0,100,45);
		add(upButton);
		add(panel);
		add(colorPanel);
		upButton.setBounds(0,0,345,45);
		colorPanel.setBounds(0,45,345,31);
		panel.setBounds(0,76,345,450);
		pack();
		setSize(345, 400); //視窗大小
		panel.release();
		//show();
		//item1.addActionListener(new ButtonHandler());
		//item2.addActionListener(new ButtonHandler());
	}
	
	public class AddLoadButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
				JFileChooser chooser = new JFileChooser();
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
					JOptionPane.showMessageDialog(jf,"請點選畫面決定要放置圖片的位置");
				}
		}
	}
	public class AddSaveButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
				BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_RGB);
				panel.paint(image.createGraphics());
				System.out.printf("W: %d H: %d\n",panel.getWidth(),panel.getHeight());
				try {
					new File("RunningToolbox_Image").mkdir();
					File file = null;
					int count = -1;
					do{
						count++;
						String nameStr = "test";
						if(count / 100 == 0)
							nameStr += "0";
						if(count / 10 == 0)
							nameStr += "0";
						nameStr += count + ".jpg";
			
						file = new File("RunningToolbox_Image", nameStr);
					}while(file.exists() && !file.isDirectory());	//一直找，直到檔案不存在
					
					ImageIO.write(image, "jpg", file);
				} catch(Exception e2) {
					//e.printStackTrace();
				}
		}
	}
	}