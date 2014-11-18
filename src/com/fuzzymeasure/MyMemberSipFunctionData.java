package com.fuzzymeasure;

public class MyMemberSipFunctionData 
{
	private double sstart;		//Smallスタート位置
	private double send;		//Small終了位置
	private double lstart;		//Largeスタート位置
	private double lend;		//Largeスタート位置

	private double sdegree;		///<@brief Smallの時の傾き
	private double sintercept;	///<@brief Smallの時の切片
	private double ldegree;		///<@brief Largeの時の傾き
	private double lintercept;	///<@brief Largeの時の切片
	
	//コンストラクタ
	public MyMemberSipFunctionData()
	{
		sstart = send = lstart = lend = 0;
		sdegree = sintercept = ldegree = lintercept = 0.0;
	}
	

	///<@brief メンバーシップ関数作成メソッド
	///<@param[in] ls Smallスタート位置
	///<@param[in] le Small終了位置
	///<@param[in] hs Largeスタート位置
	///<@param[in] he Large終了位置
	///<@param[in] lh Large or Small
	public void makeMenberSipFunction(double ss,double se,double ls,double le)
	{
		sstart = ss;
		send = se;
		lstart = ls;
		lend = le;

		//式 
		//(x1,y1) (x2,y2) から傾きを求める a=(y2-y1)/(x2-x1) 
		// b = y1 - a * x1

		//'Small'の時
		sdegree =  -1.0 / (send - sstart);
		sintercept = 1.0 - sdegree * sstart;		

		//'Large'の時
		ldegree = 1.0 / (lend - lstart);			
		lintercept = -ldegree * lstart;	
	}

	///<@brief 適合度を計算し返す関数
	///<@@aram[in] value 調べる値(包含率)
	///<@param[in] lh 'S'か'L'の文字列
	public double fuzzyset(double value,char sl)
	{	
		double grade = 0.0;

		//'Small'の時
		if(sl=='S')
		{
			// sstart以下の時
			if(sstart > value) grade = 1.0;

			// sstart <= value <= send
			else if(sstart <= value && send >= value) grade = (sdegree * value) + sintercept;

			// send以上の時
			else if(send < value) grade = 0.0;
		}
	
		//Large
		else
		{
			// lstart以下の時
			if(lstart > value) grade = 0.0;

			// lstart <= value <= lend
			else if(lstart <= value && lend >= value) grade = (ldegree * value) + lintercept;
			
			// lend以上の時
			else if(lend < value) grade = 1.0;
		}
		return grade;
	}
}
