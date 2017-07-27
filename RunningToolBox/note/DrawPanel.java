package RunningToolBox.note;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.datatransfer.*;

import javax.imageio.ImageIO;
import java.lang.*;
import java.io.*;

public class DrawPanel extends JPanel {
	int x;
	int xImg,yImg;
	int thick = 5;
	int imageHave = 0;
	int pointCount[] = new int[11*10];
	Point points[][] = new Point[11*10][];
	BufferedImage bufImg;
	 
	public DrawPanel()
	{
		
		for(int t=0;t<11*10;t++)
		{
			points[t] = new Point[10000];
			pointCount[t] = 0;
			imageHave = 0;
		}
		
		addMouseMotionListener(new MouseMotionAdapter()	{
					public void mouseDragged(MouseEvent event)
					{
						if(pointCount[thick*11+x/30] < points[0].length && event.getY() > 0)
						{
							points[thick*11+x/30][pointCount[thick*11+x/30]] = event.getPoint();
							pointCount[thick*11+x/30]++;
							repaint();
						}
					}
				});
		addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent e) {
					if(imageHave == 1)
					{
						xImg = e.getX();
						yImg = e.getY();
						repaint();
						imageHave = 2;
						
					}
				}
			});
	}
	
	public void release()
	{
		for(int t = 0 ; t < 11*10 ; t++)
		{
			points[t] = new Point[10000];
			pointCount[t] = 0;
			imageHave = 0;
		}
	}
	
	public void getColorX(int c)
	{
		x = c;
	}
	
	public void getThick(int c)
	{
		thick = c;
	}
	
	public void getImageHave(int c)
	{
		imageHave = c;
	}
	
	public void getBufImg(BufferedImage c)
	{
		bufImg = c;
	}
	
	public void paintComponent (Graphics g)
	{
		super.paintComponent(g);
		this.setBackground(Color.WHITE);
		//System.out.printf("imageHave :%d\n",imageHave);
		if(imageHave == 2)
		{
			int width = bufImg.getWidth();
			int height = bufImg.getHeight();
			int number;
			int temp;
			if( width >= height ){ number = 1; }
			else number = 2;
			
			if( width > 250 || height > 250 )
			{
				if( number == 1 )
				{
					temp = width;
					width = 250;
					height = height*250/temp;
				}
				else if( number == 2 )
				{
					temp = height;
					height = 250;
					width = width*250/temp;
				}
			}
			g.drawImage(bufImg,xImg,yImg, width, height, null);
			
		}
		if(x<=30)
		{
			g.setColor(Color.BLACK);
		}
		else if(x>30&&x<=60)
		{
			g.setColor(Color.DARK_GRAY);
		}
		else if(x>60&&x<=90)
		{
			g.setColor(Color.GRAY);
		}
		else if(x>90&&x<=120)
		{
			g.setColor(Color.LIGHT_GRAY);
		}
		else if(x>120&&x<=150)
		{
			g.setColor(Color.WHITE);
		}
		else if(x>150&&x<=180)
		{
			g.setColor(Color.BLUE);
		}
		else if(x>180&&x<=210)
		{
			g.setColor(Color.CYAN);
		}
		else if(x>210&&x<=240)
		{
			g.setColor(Color.GREEN);
		}
		else if(x>240&&x<=270)
		{
			g.setColor(Color.YELLOW);
		}
		else if(x>270&&x<=300)
		{
			g.setColor(Color.RED);
		}
		else if(x>300&&x<=330)
		{
			g.setColor(Color.MAGENTA);
		}
		for(int t=0;t<11;t++)
		{
			switch(t)
			{
				case 0:g.setColor(Color.BLACK);
						break;
				case 1:g.setColor(Color.DARK_GRAY);
						break;
				case 2:g.setColor(Color.GRAY);
						break;
				case 3:g.setColor(Color.LIGHT_GRAY);
						break;
				case 4:g.setColor(Color.WHITE);
						break;
				case 5:g.setColor(Color.BLUE);
						break;
				case 6:g.setColor(Color.CYAN);
						break;
				case 7:g.setColor(Color.GREEN);
						break;
				case 8:g.setColor(Color.YELLOW);
						break;
				case 9:g.setColor(Color.RED);
						break;
				case 10:g.setColor(Color.MAGENTA);
						break;
			}
			
			for(int a = 0 ; a < 10 ; a++)
				for(int i = 0 ; i < pointCount[t+a*11] ; i++)
					g.fillOval(points[t+a*11][i].x,points[t+a*11][i].y,a,a);
		}
	}
}