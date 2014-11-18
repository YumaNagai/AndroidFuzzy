package com.fuzzymeasure;

import android.os.Handler;
import android.os.Message;
import com.neurosky.thinkgear.*;
import android.bluetooth.BluetoothAdapter;

public class MyThinkGear 
{
	private final boolean rawEnabled = true;	//measureEnnable flag 
	private boolean idlestate;					//when have connection,thread state is run or stop 
	private TGDevice tgDevice;					//ThinkGearDevice
	private String deviceStatus;				//deviceStatus String
	private double rowdata;						//rowData
	private boolean stampupdate;				//data update flag
	private long timeStamp,nowtime;				//time
	private int devicestate;					//ThinkGearDevice Status
	private BluetoothAdapter adapter;			//Bluetooth adapter 
	private int nscount;						//now sampling count
	
	//ThinkGearDevice Hander
	private final Handler thinkgearHandler = new Handler()
	{
		@Override
		//ThinkGearHaunderMethod
		public void handleMessage(Message msg)
		{
			switch (msg.what) 
	    	{
	        	case TGDevice.MSG_STATE_CHANGE:
	        		
	        		switch (msg.arg1) 
	        		{
	                	case TGDevice.STATE_IDLE:
	                	break;
	                	
	                	case TGDevice.STATE_CONNECTING:		                	
	                		deviceStatus = "TGDevice Connecting...\n";
	                		devicestate  = TGDevice.STATE_CONNECTING;
	                	break;		                    
	                	
	                	case TGDevice.STATE_CONNECTED:
	                		deviceStatus = "TGDevice Connected...\n";
	                		devicestate  = TGDevice.STATE_CONNECTING;
	                	break;
	                	
	                	case TGDevice.STATE_NOT_FOUND:
	                		deviceStatus = "Can't find...\n";
	                		devicestate  = TGDevice.STATE_CONNECTING;
	                	break;
	                	
	                	case TGDevice.STATE_NOT_PAIRED:
	                		deviceStatus = "not paired...\n";
	                		devicestate  = TGDevice.STATE_CONNECTING;
	                	break;
	                	
	                	case TGDevice.STATE_DISCONNECTED:
	                		deviceStatus = "Disconnected mang...\n";
	                		devicestate  = TGDevice.STATE_CONNECTING;
	                	break;
	        		}
	        	break;
	        	     
	    	//RawVaveData
	        case TGDevice.MSG_RAW_DATA:        	
	        	
	        	nowtime = System.currentTimeMillis();          	
	        	stampupdate = false;
	        	
	        	//sample about 2ms
	        	if((nowtime - timeStamp)>=2000)
	        	{
	        		if(stampupdate == true)
	        		{ 
	        			stampupdate = false;
	        			return;
	        		}
	        		rowdata = msg.arg1;
	        		nscount++;
	        		timeStamp = nowtime;
	        		stampupdate = true;
	        	}
	        		
	        break;
	       
	        case TGDevice.MSG_BLINK:break;
	        case TGDevice.MSG_LOW_BATTERY:break;
			
	    	}	
		}	
	};
	
	//Constructor Initialize
	private MyThinkGear()
	{
		stampupdate = false;
	    rowdata = 0;
	    timeStamp = nowtime = 0;
	    stampupdate = true;
	    nscount = 0;
	    deviceStatus = "new MyThinkGearClass";
		adapter = BluetoothAdapter.getDefaultAdapter();	//defalutAdapter get
		if(adapter == null) deviceStatus = "Bluetooth not available\n";
		else tgDevice = new TGDevice(adapter, thinkgearHandler);
	}
	
	///<@brief instance create getMethod
	///<@param sampleCount count of measuring data 
	public static MyThinkGear Load()
	{
		MyThinkGear mtg = new MyThinkGear();
		return mtg;
	}
	
	///<@brief connectMethod(call thinkgearDevice connect)
	///<@return true:OK connection false:fail connection
	public boolean connectDevice()
	{
		boolean state = false;
		
		//when not connect Device
		if( devicestate != TGDevice.STATE_CONNECTING && devicestate != TGDevice.STATE_CONNECTED)
		{
    		tgDevice.connect(rawEnabled);
    		state = true;
		}
		return state;
	}
	
	///<@brief disconnectMethod(call thinkgearDevice disconnect)
	///<@return true:OK disconnection false:fail disconnection
	public boolean disconnectDevice()
	{
		boolean state = false;
		
		//when connect Device
		if( devicestate == TGDevice.STATE_CONNECTING && devicestate == TGDevice.STATE_CONNECTED)
		{
    		tgDevice.close();
    		state = true;
		}
		return state;
	}
	
	///<@brief disconnectMethod(call thinkgearDevice stop)
	///<@return true:OK disconnection false:fail disconnection
	public boolean stopDeivce()
	{
		boolean state = false;
	
		//when connect Device
		if( devicestate == TGDevice.STATE_CONNECTING && devicestate == TGDevice.STATE_CONNECTED)
		{
    		tgDevice.stop();
    		idlestate = true;
    		state = true;
		}
		return state;
	}
	
	///<@brief threadStartMethod(call thinkgearDevice measurement start)
	///<@return true:OK disconnection false:fail disconnection
	public boolean measureStart()
	{
		boolean state = false;
		//when connect device or thread is idle
		if(devicestate == TGDevice.STATE_CONNECTING && devicestate == TGDevice.STATE_CONNECTED && idlestate == true)
		{
    		tgDevice.start();
    		state  = true;
		}
		return state;
	}
	
	///<@brief 	getDataValue
	///<@return getDataValue or DoubleMaxValue(not update) 
	public double getData()
	{
		double data = Double.MAX_VALUE;
		if(stampupdate == true)
		{
			data = rowdata;
			stampupdate = false;
		}
		return data;
	}
	
	///<@brief 	getDevicestatus
	///<@return deviceStatus String
	public String getStatus(){ return deviceStatus; }
	
	///<@brief 	now SamplingCount
	///<@return deviceStatus String
	public int getSamplingCount(){  return this.nscount;   }
	
	///<@brief 	get current Minute Method
	///<@return 
	public int getMinute()
	{	
		if(nscount%512==0) return (nscount/512);	
		else return 0;
	}
	

}
