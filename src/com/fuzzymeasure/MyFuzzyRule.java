package com.fuzzymeasure;

public class MyFuzzyRule 
{
	private char deltaState;
	private char thetaState;
	private char alphaState;
	private char betaState;
	private char gammaState;
	private char output;
	private double yesWeight;
	private double noWeight;

	//コンストラクタ
	public MyFuzzyRule()
	{
		deltaState = ' ';
		thetaState = ' ';
		alphaState = ' ';
		betaState = ' ';
		gammaState = ' ';
		output = ' '; 
		noWeight = yesWeight = 0;
	}
	
	//setter method
	public void setFuzzyRule(char ds,char ts,char as,char bs,char gs,char op,double yw,double nw)
	{
		this.deltaState = ds;
		this.thetaState = ts;
		this.alphaState = as;
		this.betaState  = bs;
		this.gammaState = gs;
		this.output = op;
		this.yesWeight = yw;
		this.noWeight = nw;
	}
	
	//Get BandState
	public char getState(int type)
	{
		char state='\0';
		
		switch (type) 
		{
			//delta
			case MyFFTFunction.WaveBandType.Delta:
				state = deltaState;
			break;
			
			//theta
			case MyFFTFunction.WaveBandType.Theta:
				state = thetaState;
			break;
			
			//alpha
			case MyFFTFunction.WaveBandType.Alpha:
				state = alphaState;
			break;
			
			//beta
			case MyFFTFunction.WaveBandType.Beta:
				state = betaState;
			break;
			
			//gamma
			case MyFFTFunction.WaveBandType.Gamma:
				state = gammaState;
			break;

			default:
				break;
		}
		
		return state;
	}
	
	//get Weight Method
	public double getYesWeight(){ 	return this.yesWeight; 	}
	public double getNoWeight()	{ 	return this.noWeight; 	}
	public char   outputState(){	return this.output;		}
}
