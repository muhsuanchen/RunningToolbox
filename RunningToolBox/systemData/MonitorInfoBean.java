package RunningToolBox.systemData;
public class MonitorInfoBean { 
	private String osName; 	// 操作系统
	
	private long totalMemory; 	// 可使用内存 
	private long freeMemory; 	// 剩餘内存
	private long maxMemory; 	// 最大可使用内存

	private long totalPhysicalMemorySize; // 總物理内存 
	private long freePhysicalMemorySize; 	// 剩餘的物理内存 
	private long usedPhysicalMemory; 	// 已使用的物理内存
	
	private int totalThread; 	// 線程總數
	private double cpuRatio;	// cpu使用率

	public String getOsName() { 
		return osName; 
	}

	public void setOsName(String osName) { 
		this.osName = osName; 
	}
	
	public long getFreeMemory() { 
		return freeMemory; 
	}

	public void setFreeMemory(long freeMemory) { 
		this.freeMemory = freeMemory; 
	}

	public long getMaxMemory() { 
		return maxMemory; 
	}

	public void setMaxMemory(long maxMemory) { 
		this.maxMemory = maxMemory; 
	}

	public long getTotalMemory() { 
		return totalMemory; 
	}

	public void setTotalMemory(long totalMemory) { 
		this.totalMemory = totalMemory; 
	}

	public long getTotalPhysicalMemorySize() { 
		return totalPhysicalMemorySize; 
	}

	public void setTotalPhysicalMemorySize(long totalPhysicalMemorySize) { 
		this.totalPhysicalMemorySize = totalPhysicalMemorySize; 
	}

	public long getFreePhysicalMemorySize() { 
		return freePhysicalMemorySize; 
	}

	public void setFreePhysicalMemorySize(long freePhysicalMemorySize) { 
		this.freePhysicalMemorySize = freePhysicalMemorySize; 
	}
	
	public long getUsedPhysicalMemory() { 
		return usedPhysicalMemory; 
	}

	public void setUsedPhysicalMemory(long usedPhysicalMemory) { 
		this.usedPhysicalMemory = usedPhysicalMemory; 
	}

	public int getTotalThread() { 
		return totalThread; 
	}

	public void setTotalThread(int totalThread) { 
		this.totalThread = totalThread; 
	}
	
	public double getCpuRatio() { 
		return cpuRatio; 
	}

	public void setCpuRatio(double cpuRatio) { 
		this.cpuRatio = cpuRatio; 
	} 
} 