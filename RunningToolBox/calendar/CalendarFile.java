package RunningToolBox.calendar;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.Calendar;
import java.util.Locale;

public class CalendarFile {
	private String filePath = "RunningToolBox/calendar/calendarNote/";
	
	public void safeFile(JTextArea textArea, int year, int month, int day)
	{
		if( month > 12 )
		{
			year++;
			month = month - 12;
		}
		else if( month < 1 )
		{
			year--;
			month = 12 + month;
		}
		
		String text = textArea.getText();
		if(!text.equals(""))
		{
			try{
				String filename = "" + year + "_" + month + "_" + day;
				FileWriter fw = new FileWriter(filePath + filename + ".txt");
				fw.write(text);
				BufferedWriter bfw = new BufferedWriter(fw);//啟用緩衝區寫入
				
				bfw.flush();//將緩衝區資料寫到檔案
				fw.close();//關閉檔案
				fw = null;
			} catch (IOException except){
				except.printStackTrace();
			}
			System.gc();
		}else{
			deleteFile(year,month,day);
		}
	}
	
	public void deleteFile(int year,int month,int day)
	{
		if( month > 12 )
		{
			year++;
			month = month - 12;
		}
		else if( month < 1 )
		{
			year--;
			month = 12 + month;
		}
		
		String text,filename = "" + year + "_" + month + "_" + day;
		File file = new File(filePath + filename + ".txt");
		if(file != null)
		{
			System.gc();
			file.delete();
		}
	}
	
	public void loadFile(JTextArea textArea, int year, int month, int day)
	{
		if( month > 12 )
		{
			year++;
			month = month - 12;
		}
		else if( month < 1 )
		{
			year--;
			month = 12 + month;
		}
		
		String text,filename = "" + year + "_" + month + "_" + day;
		try {
			FileReader fr = new FileReader(filePath + filename + ".txt");
			String file = "";
			BufferedReader br = new BufferedReader(fr);
			StringBuffer sb = new StringBuffer();
			try {
				if((file = br.readLine()) != null)
					sb.append(file);
				while ((file = br.readLine()) != null)
				{
					sb.append("\n");
					sb.append(file);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			file = "" + sb;
			textArea.setText(file);
			try {
				fr.close();
				fr = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.gc();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			textArea.setText("");
			//e.printStackTrace();
		}
	}
	
	public boolean findFile(int year,int month,int day)
	{
		if( month > 12 )
		{
			year++;
			month = month - 12;
		}
		else if( month < 1 )
		{
			year--;
			month = 12 + month;
		}
		
		String text,filename = "" + year + "_" + month + "_" + day;
		//System.out.println("findFile" + filename);
		try {
			FileReader fr = new FileReader(filePath + filename + ".txt");
		} catch (FileNotFoundException e) {
			return false;
		}
		return true;
	}
	
	//回傳時間最近的提醒
	public String loadNotify()
	{
		String earlyStr = null;
		String earlyText = null;
		
		try {
			FileReader fr = new FileReader(filePath + "notification.txt");
			String file = "";
			BufferedReader br = new BufferedReader(fr);
			
			//篩選最早的日期
			try {
				while ((file = br.readLine()) != null)
				{
					//System.out.println(file);	//一次一組事件
					String[] notifyaArray = file.split(" ");
					String[] dateArray = notifyaArray[0].split("/");	//取得 年/月/日
					String newDate = new String(notifyaArray[0] + " " + notifyaArray[1] + " " + notifyaArray[2]);
					
					Calendar calendar = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.US);
					String nowTimeStr = sdf.format(calendar.getTime());
					int nResault = compareTime(nowTimeStr, newDate);
					//事件不是在現在之前
					if(nResault != 2)
					{
						if(earlyStr != null)
						{
							int cResault = compareTime(earlyStr, newDate);
							switch(cResault)
							{
								case 0:	//一樣時間，用換行接在一起顯示
									if(notifyaArray.length < 5)	//沒有Note內容
										earlyText += "\n" + notifyaArray[3] + " Empty note.";
									else{
										earlyText += "\n" + notifyaArray[3] + " " + notifyaArray[4];
										for(int i = 5 ; i < notifyaArray.length ; i++)
										{
											earlyText += " " + notifyaArray[i];
										}
									}
									break;
									
								case 1:	//原先較大，不做事情
									break;
									
								case 2:	//新的較大，改新時間
									earlyStr = new String(newDate);
									if(notifyaArray.length < 5)	//沒有Note內容
										earlyText = "" + notifyaArray[3] + " Empty note.";
									else{
										earlyText = "" + notifyaArray[3] + " " + notifyaArray[4];
										for(int i = 5 ; i < notifyaArray.length ; i++)
										{
											earlyText += " " + notifyaArray[i];
										}
									}
									break;
							}
						}else{
							earlyStr = new String(newDate);
							if(notifyaArray.length < 5)	//沒有Note內容
								earlyText = "" + notifyaArray[3] + " Empty note.";
							else{
								earlyText = "" + notifyaArray[3] + " " + notifyaArray[4];
								for(int i = 5 ; i < notifyaArray.length ; i++)
								{
									earlyText += " " + notifyaArray[i];
								}
							}
						}
					}
						
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//關檔
			try {
				fr.close();
				fr = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.gc();
			
			if(earlyStr == null)
				return null;	//沒有提醒
			else
				return earlyStr + " " + earlyText;
		} catch (FileNotFoundException e) {
			return null;	//沒有提醒
		}
	}
	
	private int compareTime(String Str1, String Str2)
	{
		//DateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm a");
		DateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm a", Locale.US);
		Calendar c1 = java.util.Calendar.getInstance();
		Calendar c2 = java.util.Calendar.getInstance();

		try{
			//System.out.println("" + Str1 + ", " + Str2);
			//System.out.println("" + format.parse(Str1) + ", " + format.parse(Str2));
			c1.setTime(format.parse(Str1));
			c2.setTime(format.parse(Str2));
		} catch (ParseException e) {
			System.out.println("格式不正確");
		}
		
		int result = c1.compareTo(c2);
		//System.out.println("compare " + result);
		if(result == 0)
			return 0;	//一樣大
		else if(result < 0)
			return 1;	//Str1 較小
		else
			return 2;	//Str2 較小
	}
}
