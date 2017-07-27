package RunningToolBox.note;

import java.awt.*;
import javax.swing.*;
import java.awt.Graphics;

class ColorBlock extends JPanel
	{
		int xImg,yImg;
		public int x;

		public int giveColor()
		{
			return x;
		}
		
		public void paintComponent (Graphics g)
		{
			super.paintComponent(g);
			this.setBackground(Color.WHITE);
			g.setColor(Color.BLACK);
			g.fillRect(0,0,30,31);
			g.setColor(Color.DARK_GRAY);
			g.fillRect(30,0,30,31);
			g.setColor(Color.GRAY);
			g.fillRect(60,0,30,31);
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(90,0,30,31);
			g.setColor(Color.WHITE);
			g.fillRect(120,0,30,31);
			g.setColor(Color.BLUE);
			g.fillRect(150,0,30,31);
			g.setColor(Color.CYAN);
			g.fillRect(180,0,30,31);
			g.setColor(Color.GREEN);
			g.fillRect(210,0,30,31);
			g.setColor(Color.YELLOW);
			g.fillRect(240,0,30,31);
			g.setColor(Color.RED);
			g.fillRect(270,0,30,31);
			g.setColor(Color.MAGENTA);
			g.fillRect(300,0,30,31);
		}
	
	}