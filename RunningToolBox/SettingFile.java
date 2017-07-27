package RunningToolBox;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.Calendar;
import java.util.Locale;

public class SettingFile {
	private String mainfilePath = "RunningToolBox";
	
	public int[] loadSetting(String folderName)
	{
		String filename = mainfilePath + "/" + folderName + "/" + folderName + "Setting.txt";
		int[] setting = new int[2];
		try {
			FileReader fr = new FileReader(filename);
			String file = "";
			BufferedReader br = new BufferedReader(fr);
			file = br.readLine();
			
			String[] settingArray = file.split(" ");
			
			setting[0] = Integer.valueOf(settingArray[0]);
			setting[1] = Integer.valueOf(settingArray[1]);
			
			//關檔
			try {
				fr.close();
				fr = null;
			} catch (IOException e) {
				//e.printStackTrace();
			}
		} catch (IOException e) {
			//e.printStackTrace();
			if(folderName.equals("note"))
				setting[0] = 200;
			else
				setting[0] = 150;
			setting[1] = 0;
		}
		
		System.gc();
		return setting;
	}
	
	public void saveSetting(String folderName, int color, int alpha)
	{
		String filename = mainfilePath + "/" + folderName + "/" + folderName + "Setting.txt";
		
		if(alpha < 0)
			alpha = 0;
		if(color < 0)
			color = 0;
		String settingText = "" + alpha + " " + color;
		
		try{
			PrintWriter file = new PrintWriter(filename);//啟用緩衝區寫入
			file.print(settingText);
			file.close();//將緩衝區資料寫到檔案
		} catch (IOException except){
			except.printStackTrace();
		}
		System.gc();
	}
	
	public int[] loadSite(String folderName)
	{
		String filename = mainfilePath + "/" + folderName + "/" + folderName + "frameSite.txt";
		int[] setting = new int[2];
		try {
			FileReader fr = new FileReader(filename);
			String file = "";
			BufferedReader br = new BufferedReader(fr);
			file = br.readLine();
			
			String[] settingArray = file.split(" ");
			
			setting[0] = Integer.valueOf(settingArray[0]);
			setting[1] = Integer.valueOf(settingArray[1]);
			
			//關檔
			try {
				fr.close();
				fr = null;
			} catch (IOException e) {
				//e.printStackTrace();
			}
		} catch (IOException e) {
			//e.printStackTrace();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			setting[0] = screenSize.width - 500;
			setting[1] = 50;
		}
		
		System.gc();
		return setting;
	}
	
	public void saveSite(String folderName, int frameX, int frameY)
	{
		String filename = mainfilePath + "/" + folderName + "/" + folderName + "frameSite.txt";
		
		if(frameX < 0)
			frameX = 0;
		if(frameY < 0)
			frameY = 0;
		String settingText = "" + frameX + " " + frameY;
		
		try{
			PrintWriter file = new PrintWriter(filename);//啟用緩衝區寫入
			file.print(settingText);
			file.close();//將緩衝區資料寫到檔案
		} catch (IOException except){
			except.printStackTrace();
		}
		System.gc();
	}
}