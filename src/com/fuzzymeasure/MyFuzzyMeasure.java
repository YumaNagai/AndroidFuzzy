package com.fuzzymeasure;

import java.io.*;
import java.util.Vector;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.*;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.neurosky.thinkgear.*;

public class MyFuzzyMeasure extends Activity 
{
	private final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;		//minimum size
	private final int MP = LinearLayout.LayoutParams.MATCH_PARENT;		//maximum size
	
	//brain wave variable
	private final int sampingRate = 512;		//samplingRate
	private int measuretime = 4;				//measure time
	private MyThinkGear thinkgearDevice;		//thinkgear device 
	private double wavedata[];					//brainwave data

	//displayLayout variable
	private TableLayout layout;
	private TextView statuslabel;				//starus
	private TextView statuslog;

	//private input data
	private String gender;				//gender
	private Spinner genderType;			//gender spinner
	private String expriment;			//experiment String
	private Spinner experimentType;		//experiment type
	private int age;					//age number
	private EditText ageText;			//age inputForm
	private EditText secondText;		//exprimentTime inputForm

	private Button expStartButton;

		
	//private device status
	private Button connetButton;		//device connectbutton
	private Button disconnectButton;	//device disconnectbutton
	private Button analyzeButton;		//analyze button
	private Button clearDataButton;		//clearDataButton
	
	@Override
    //call when Activity create
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        thinkgearDevice = MyThinkGear.Load();		//create&init thinkGearDevice
        InitializeContent();
        
        
    }
	

    
    //content Initialize
	private void InitializeContent()
	{	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//table layout setting;
		layout = new TableLayout(this);
		setContentView(layout);
		
		//device status content setting-------------------------------------
		TableRow devicetable = new TableRow(this);
		TextView devicestatus = new TextView(this);
		devicestatus.setText("DeviceStatus:");
		devicestatus.setTextSize(25.0f);
		devicetable.addView(devicestatus);
		
		connetbutton = new Button(this);
		connetbutton.setText("connectDevice");
		connetbutton.setOnClickListener(new View.OnClickListener() 
		{			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
				{
		    		tgDevice.connect(rawEnabled);
		    		//tgDevice.start();
				}
			}
		} );
		devicetable.addView(connetbutton);
		
		disconnectbutton = new Button(this);
		disconnectbutton.setText("DisconnectDevice");
		disconnectbutton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				if(tgDevice.getState() == TGDevice.STATE_CONNECTING && tgDevice.getState() == TGDevice.STATE_CONNECTED)
				{
		    		tgDevice.close();
		    		runOnUiThread(new Runnable() 
					{
						public void run() 
						{
							statuslog.append("TGDeviceDisconnect");
						}
					});
				}
			}
		});
		devicetable.addView(disconnectbutton);
		layout.addView(devicetable);
		layout.addView(new TextView(this));
		//------------------------------------------------------------------
		
		//inputdata1 content------------------------------------------------
		TableRow inputtable = new TableRow(this);
		
		TextView eplabel = new TextView(this);
		eplabel.setText("実験の種類:");
		eplabel.setTextSize(25.0f);
		inputtable.addView(eplabel);
		ArrayAdapter<String> expadapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
		expadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		expadapter.add("Yes");
		expadapter.add("No");
		experimentType = new Spinner(this);
		experimentType.setAdapter(expadapter);
		experimentType.setSelection(0);
		experimentType.setLeft(eplabel.getRight());
		inputtable.addView(experimentType);

		TextView slabel = new TextView(this);
		slabel.setText("性別:");
		slabel.setTextSize(25.0f);
		inputtable.addView(slabel);
		ArrayAdapter<String> sexadapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
		sexadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sexadapter.add("男");
		sexadapter.add("女");
		genderType = new Spinner(this);
		genderType.setAdapter(sexadapter);
		genderType.setSelection(0);
		
		//sexType.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
		inputtable.addView(genderType);
		layout.addView(inputtable);
		layout.addView(new TextView(this));
		//-------------------------------------------------------------------
			
		//input data2---------------------------------------------------------
		TableRow inputtable2 = new TableRow(this);
		TextView agelabel = new TextView(this);
		agelabel.setText("年齢：");
		agelabel.setTextSize(25.0f);
		inputtable2.addView(agelabel);
		ageText = new EditText(this);
		ageText.setText("20");
		inputtable2.addView(ageText);
		
		TextView secondlabel = new TextView(this);
		secondlabel.setText("計測秒数:");
		secondlabel.setTextSize(25.0f);
		inputtable2.addView(secondlabel);
		secondText = new EditText(this);
		secondText.setText("4");
		inputtable2.addView(secondText);
		layout.addView(inputtable2);
		layout.addView(new TextView(this));
		//---------------------------------------------------------------------
		
		//button create----------------------------------------------------------
		TableRow buttonsTable = new TableRow(this);
		
		expStartButton = new Button(this);
		expStartButton.setText("実験開始");
		expStartButton.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				if(issetting == true && isexpriment == false && tgDevice.getState() == TGDevice.STATE_CONNECTED)
				{
					// TODO Auto-generated method stub
					gender = (genderType.getSelectedItemPosition() == 0 ) ? "男" : "女";
					age = Integer.parseInt(ageText.getText().toString());
					expriment = (experimentType.getSelectedItemPosition() == 0 ) ? "Yes" : "No";
					measuretime = Integer.parseInt(secondText.getText().toString());
					sampleCount = sampingRate * measuretime;
					if(sampleCount == 0)
					{
						statuslog.append("Can't measure 0 times\n");
						return;
					}
					wavedata = new double[sampleCount];
					nowmeasurecount = 0;
					statuslabel.setText("MeasureBrainWave");
					statuslog.setText("");
					tgDevice.start();
					isexpriment = true;
					isresultfinish = false;
					issetting = false;
					mstart = timestmp = System.currentTimeMillis();
				}
				
			}
		});
		buttonsTable.addView(expStartButton);
		
		analyzeButton = new Button(this);
		analyzeButton.setText("AnalyzeData");
		analyzeButton.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
		    	//実験終了時の処理
		    	if(issetting == false && isexpriment == false && isresultfinish == false)
		    	{  	
		    		for(int ii=0;ii<sampleCount;ii++)
		    		{
		    			wavedata[ii] = data.get(ii);
		    		}
		    		
		    		statuslog.append("FFTAnalayzeStart");
		    		
		    		//FFTデータ作成
		    		datafunction = MyFFTFunction.Load(sampleCount, wavedata,MyFFTFunction.WindowType.Hamming);
		    		
		    		double datap[] = new double[5];
		    		
		    		datap[0] = datafunction.getWaveProbability(MyFFTFunction.WaveBandType.Delta) * 100;
		    		datap[1] = datafunction.getWaveProbability(MyFFTFunction.WaveBandType.Theta) * 100;
		    		datap[2] = datafunction.getWaveProbability(MyFFTFunction.WaveBandType.Alpha) * 100;
		    		datap[3] = datafunction.getWaveProbability(MyFFTFunction.WaveBandType.Beta)  * 100;
		    		datap[4] = datafunction.getWaveProbability(MyFFTFunction.WaveBandType.Gamma) * 100;
		    		
		    		double Ngrade = 0,Ntgrade = 0;
		    		double Ygrade = 0,Ytgrade = 0;
		    		
		    		int tabelsize = ruletable.size();
		    		
		    		//ルールテール分繰り返し
		    		for(int ii=0;ii<tabelsize;ii++)
		    		{
		    			double min = Double.MAX_VALUE;
		    			MyFuzzyRule rule = (MyFuzzyRule)ruletable.get(ii);
		    			char state = '0';
		    			
		    			for(int jj=0;jj<5;jj++)
		    			{
		    				switch(jj)
		    				{
		    					case 0: state = rule.deltaState;  break;
		    					case 1: state = rule.thetaState;  break;
		    					case 2: state = rule.alphaState;  break;
		    					case 3: state = rule.betaState;   break;
		    					case 4: state = rule.gammaState;  break;
		    				}
		    				double pp = membersipdata[jj].fuzzyset(datap[jj],state);
		    				if(min >= pp) min = pp;	
		    			}
		    			
		    			//Yes出力
						Ygrade += min;
						Ytgrade += (min * rule.yesWeight);
												
						//No出力
						Ngrade += min;
						Ntgrade += (min * rule.noWeight);
		    		}
		    		Ytgrade = (Ygrade == 0) ? 0 : Ytgrade/Ygrade;
					Ntgrade = (Ngrade == 0) ? 0 : Ntgrade/Ngrade;
					
					final String delta = "delta:"+ String.format("%.2f",(datap[0])) +"%\n";
					final String theta = "theta:"+ String.format("%.2f",(datap[1])) +"%\n";
					final String alpha = "alpha:"+ String.format("%.2f",(datap[2])) +"%\n";
					final String beta =  "beta:" + String.format("%.2f",(datap[3])) +"%\n";
					final String gamma = "gamma:"+ String.format("%.2f",(datap[4])) +"%\n\n";
					final String Yesw =  "Yes: "+  String.format("%.2f",(Ytgrade * 100)) +"%\n" ;
					final String Now =   "No:  " + String.format("%.2f",(Ntgrade * 100)) +"%\n";
					
					// TODO Auto-generated method stub
					//結果表示
					
					statuslabel.setText("");
					statuslabel.append("MeasureResult");
					statuslog.setText("");
					statuslog.append(delta);
					statuslog.append(theta);
					statuslog.append(alpha);
					statuslog.append(beta);
					statuslog.append(gamma);
					statuslog.append(Yesw);
					statuslog.append(Now);
				
					//file appendMode create
					OutputStream out = null;
					Context context = getApplicationContext();
					
					try{
						out = context.openFileOutput("database.csv",Context.MODE_APPEND);
						PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));
						java.util.Date date = new java.util.Date(); 
						String data = date.toString() +","+ gender + ","+ age +","+ expriment +","+ measuretime +","+
						String.format("%.5f",datap[0]) +"," +String.format("%.5f",datap[1])+","+
						String.format("%.5f",datap[2]) +"," +String.format("%.5f",datap[3])+","+
						String.format("%.5f",datap[4]) +"," +
						String.format("%.5f",Ytgrade) + "," + String.format("%.5f",Ntgrade)+"\n";
						writer.append(data);
						writer.close();
						statuslog.append("write Data to File\n");
					}catch(IOException e){
						e.printStackTrace();
					}
					data.clear();
					nowmeasurecount = 0;
					isexpriment = false;
					issetting = true;
					isresultfinish = true;
					//mainRoutine.stop();
		    	}
		    	
		    	else if(isexpriment == true)
		    	{
		    		statuslabel.append("experimenting\n");
		    	}
		    	
		    	else if(isexpriment == false && issetting == true)
		    	{
		    		statuslog.append("Now DataSetting\n");
		    	}
			}
		});
		buttonsTable.addView(analyzeButton);
		
		/*
		clearDataButton = new Button(this);
		clearDataButton.setText("DataClear");
		clearDataButton.setOnClickListener(new View.OnClickListener() 
		{
			
			@Override
			public void onClick(View v) 
			{
				//初回 
				if(issetting == true)
				{
					sexType.setSelection(0);
					expType.setSelection(1);
					ageText.setText("20");
					secondText.setText("4");
					statuslabel.setText("StatusLog");
					statuslog.setText("");
				}
				
				//2回目
				if(issetting == true && isresultfinish == true)
				{
					sexType.setSelection(0);
					expType.setSelection(1);
					ageText.setText("20");
					secondText.setText("4");
					statuslabel.setText("StatusLog");
					statuslog.setText("");
					isresultfinish = false;
					
				}
			}
		});
		buttonsTable.addView(clearDataButton);
		*/
		layout.addView(buttonsTable);
		layout.addView(new TextView(this));
		
		//status log---------------------------------------------------------
		LinearLayout statusLayout = new LinearLayout(this);
		statusLayout.setOrientation(LinearLayout.VERTICAL);
		statuslabel = new TextView(this);
		statuslabel.setText("StatusLog");
		statuslabel.setTextSize(25.0f);
		statusLayout.addView(statuslabel);
		
		statuslog = new TextView(this);
		statuslog.setTextSize(25.0f);
		statusLayout.addView(statuslog);
		
		layout.addView(statusLayout);
		//----------------------------------------------------------------------
		
	}
    
    
        
    //public void run()
    //{	
    	/*
    	//実験終了時の処理
    	if(issetting == false && isexpriment == false)
    	{
    		//tgDevice stop
    		//tgDevice.close();
    		    		
    		//FFTデータ作成
    		datafunction = MyFFTFunction.Load(sampleCount, wavedata,MyFFTFunction.WindowType.Hamming);
    		
    		double datap[] = new double[5];
    		
    		datap[0] = datafunction.getWaveProbability(MyFFTFunction.WaveBandType.Delta) * 100;
    		datap[1] = datafunction.getWaveProbability(MyFFTFunction.WaveBandType.Theta) * 100;
    		datap[2] = datafunction.getWaveProbability(MyFFTFunction.WaveBandType.Alpha) * 100;
    		datap[3] = datafunction.getWaveProbability(MyFFTFunction.WaveBandType.Beta)  * 100;
    		datap[4] = datafunction.getWaveProbability(MyFFTFunction.WaveBandType.Gamma) * 100;
    		
    		double Ngrade = 0,Ntgrade = 0;
    		double Ygrade = 0,Ytgrade = 0;
    		
    		int tabelsize = ruletable.size();
    		
    		//ルールテール分繰り返し
    		for(int ii=0;ii<tabelsize;ii++)
    		{
    			double min = Double.MAX_VALUE;
    			FuzzyRlue rule = (FuzzyRlue)ruletable.get(ii);
    			char state = '0';
    			
    			for(int jj=0;jj<5;jj++)
    			{
    				switch(jj)
    				{
    					case 0: state = rule.deltaState;  break;
    					case 1: state = rule.thetaState;  break;
    					case 2: state = rule.alphaState;  break;
    					case 3: state = rule.betaState;   break;
    					case 4: state = rule.gammaState;  break;
    				}
    				double pp = membersipdata[jj].fuzzyset(datap[jj],state);
    				if(min >= pp) min = pp;	
    			}
    			
    			//Yes出力
				Ygrade += min;
				Ytgrade += (min * rule.yesWeight);
										
				//No出力
				Ngrade += min;
				Ntgrade += (min * rule.noWeight);
    		}
    		Ytgrade = (Ygrade == 0) ? 0 : Ytgrade/Ygrade;
			Ntgrade = (Ngrade == 0) ? 0 : Ntgrade/Ngrade;
			
			final String delta = "delta:"+ String.format("%.2f",(datap[0])) +"%\n";
			final String theta = "theta:"+ String.format("%.2f",(datap[1])) +"%\n";
			final String alpha = "alpha:"+ String.format("%.2f",(datap[2])) +"%\n";
			final String beta =  "beta:" + String.format("%.2f",(datap[3])) +"%\n";
			final String gamma = "gamma:"+ String.format("%.2f",(datap[4])) +"%\n\n";
			final String Yesw =  "Yes: "+  String.format("%.2f",(Ytgrade * 100)) +"%\n" ;
			final String Now =   "No:  " + String.format("%.2f",(Ntgrade * 100)) +"%\n";
			
			this.runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{
					// TODO Auto-generated method stub
					//結果表示
					
					statuslabel.setText("");
					statuslabel.append("MeasureResult");
					statuslog.setText("");
					statuslog.append(delta);
					statuslog.append(theta);
					statuslog.append(alpha);
					statuslog.append(beta);
					statuslog.append(gamma);
					statuslog.append(Yesw);
					statuslog.append(Now);
				}
			});
			//tgDevice.
			
			nowmeasurecount = 0;
			isexpriment = true;
			issetting = true;
			isresultfinish = true;
			//mainRoutine.stop();
    	}
    	*/
    //}
    
    @Override
    //call Activity destroy
    public void onDestroy() 
    {
    	tgDevice.close();
        super.onDestroy();
    }

    
    
}







