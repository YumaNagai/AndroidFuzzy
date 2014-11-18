package com.fuzzymeasure;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.TextView;

public class MyAnalyzeFuzzy extends Activity
{
	private final int sampingRate = 512;				//samplingRate
	private double wavedata[];							//brainwave data
	private Vector<MyFuzzyRule> ruletable;				//fuzzyruleTable
	private MyMemberSipFunctionData[] membersipdata; 	//membersipfunction
	private MyFFTFunction datafunction;					//fft&probabrity getClass
	private TextView resultLog;
	
	///<@brief call Activity create
	///<@param bundle android os bundle
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		FuzzyJudgementDataCreate();
		resultLog = new TextView(this);
	}
	
	///<@brief FuzzyRule and membershipData create 
    private void FuzzyJudgementDataCreate()
    {
    	ruletable = new Vector<MyFuzzyRule>();		//object create
    	
    	try{
    		AssetManager assets= getResources().getAssets();
    		InputStream input = assets.open("FuzzyRuletable.csv");
    		BufferedReader br = new BufferedReader(new InputStreamReader(input));
    		String str;
    		
    		while((str = br.readLine())!=null)
    		{
    			MyFuzzyRule rule = new MyFuzzyRule();
    			String []element = str.split(",");
    		    char ds = element[0].charAt(0); 
    		    char ts = element[1].charAt(0); 
    		    char as = element[2].charAt(0); 
    		    char bs = element[3].charAt(0); 
    		    char gs	= element[4].charAt(0); 
    		    char op = element[5].charAt(0); 
     		    double yw = Double.parseDouble(element[6]);
     		    double nw = Double.parseDouble(element[7]);
     		    rule.setFuzzyRule(ds, ts, as, bs, gs, op, yw, nw);
     		    ruletable.add(rule);
    		}
    		br.close();
    	}catch(Exception e){}
    	
    	membersipdata = new MyMemberSipFunctionData[5];
    	membersipdata[0] = new MyMemberSipFunctionData();
    	membersipdata[0].makeMenberSipFunction(40,50,40,50);
    	membersipdata[1] = new MyMemberSipFunctionData();
    	membersipdata[1].makeMenberSipFunction(20,30,20,30);
    	membersipdata[2] = new MyMemberSipFunctionData();
    	membersipdata[2].makeMenberSipFunction(20,40,20,40);
    	membersipdata[3] = new MyMemberSipFunctionData();
    	membersipdata[3].makeMenberSipFunction(20,30,20,30);
    	membersipdata[4] = new MyMemberSipFunctionData();
    	membersipdata[4].makeMenberSipFunction(20,30,20,30);
    }
    

}
