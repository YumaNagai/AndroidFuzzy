package com.fuzzymeasure;

public class MyComplex 
{
	private double real;
	private double imaginary;
	
	public MyComplex(double r,double i)
	{
		this.real = r; imaginary = i;
	}
	
	public double real()	 	{ return this.real;		 }
	public double imaginary()	{ return this.imaginary; }		
	public void setdata(double r,double i){  this.real = r; this.imaginary = i; }
	
	//‰ÁZ
	public static MyComplex add(MyComplex a, MyComplex b){
		return new MyComplex(a.real + b.real, a.imaginary + b.imaginary);
	}
	
	//Œ¸Z
	public static MyComplex subtract(MyComplex a, MyComplex b){
		return new MyComplex(a.real - b.real, a.imaginary - b.imaginary);
	}
	
	//•¡‘f”‚ÌæZ
	public static MyComplex multiply(MyComplex a,MyComplex b){
		return new MyComplex(a.real * b.real - a.imaginary * b.imaginary, 
							 a.real * b.imaginary + a.imaginary * b.real);
	}
	
	public double abs()
	{
		return Math.sqrt((this.real * this.real) + (this.imaginary * this.imaginary));
	}
	
}
