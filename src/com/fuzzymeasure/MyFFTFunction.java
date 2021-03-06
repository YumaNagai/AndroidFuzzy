package com.fuzzymeasure;

public class MyFFTFunction 
{
	private MyComplex[] fftArray;		//spectrumArray
	private int size;				//fft array size
	private int use;				//use size
	
	//private static MyComplex one = new MyComplex(1.0, 0.0);		//complex one
	//private static MyComplex ione = new MyComplex(0.0, 1.0);	//complex ione
	
	//WindowFunctionType
	public static final class WindowType
	{
		public static final int Rectangle = 0;		///< @brief 矩形窓
		public static final int Hanning   = 1;		///< @brief ハニング窓
		public static final int	Hamming	  = 2;		///< @brief ハミング窓
		public static final int	Blackman  = 3;		///< @brief ブラックマン窓
		public static final int	WinTypeMax = 4;		///< @brief 番兵
	}
	
	//brainWave's band
	public static final class WaveBandType
	{
		public static final int Delta = 5;			///< @brief δ波(0.1〜3.0Hz)
		public static final int	Theta = 6;			///< @brief θ波(4.0〜7.0Hz)
		public static final int	Alpha = 7;			///< @brief α波(8.0〜12.0Hz)
		public static final int	Beta  = 8;			///< @brief β波(13.0〜30.0Hz)
		public static final int	Gamma = 9;			///< @brief γ波(30.0〜100.0Hz)
		public static final int	WaveBandMax = 10;	///< @brief 番兵
	}
	
	//コンストラクタ
	private MyFFTFunction(int s)
	{
		use = s;
		this.size = NextPow2(s);
		fftArray = new MyComplex[size];
		for(int ii=0;ii<size;ii++) fftArray[ii] = new MyComplex(0, 0);
	}
	
	//波データの設定
	private void SetWaveData(double[] wave,int window)
	{
		for(int ii=0;ii<size;ii++)
		{
			MyComplex data = new MyComplex(0, 0);
				
			//波データの範囲内の場合
			if(ii < use)
			{
				switch (window) 
				{
					case WindowType.Rectangle:
						data.setdata(wave[ii],0);
					break;

					case WindowType.Hanning:
						data.setdata(wave[ii] * (0.5 - 0.5 * Math.cos(2.0 * Math.PI * ii/(size-1))),0);
					break;
					
					case WindowType.Hamming:
						data.setdata(wave[ii] * (0.54 - 0.46 * Math.cos(2.0 * Math.PI * ii/(size-1))),0);
					break;
					
					case WindowType.WinTypeMax:
					break;
					
					default:
					break;
				}
			}
			fftArray[ii] = data;
		}
	}
	
	/*!--------------------------------------------- 
	@brief s以上の最小の2のべき乗を返す関数
	@param [in] s サンプル長
	@return s以上の最小の2のべき乗
	-------------------------------------------------*/
	private int NextPow2(int s)
	{
		int n = 1;
		  
		//2の左1ビットシフトは 元の数 * 2
		while (n < s)	n <<= 1;
		return n;
	}
	
	//ビット反転
	private void BitReverse()
	{
		int k,b,a;
		
		for(int ii=0;ii<size;ii++)
		{
			k = 0;
			b = size >> 1;
			a = 1;
			
			while(b >= a)
			{
				if((b & ii)!=0) k |= a;
				if((a & ii)!=0) k |= b;
				b >>= 1;					//1/2倍
				a <<= 1;					//2倍
			}
			
			if(ii < k)
			{
				MyComplex tmp= fftArray[ii];
				fftArray[ii] = fftArray[k];
				fftArray[k] = tmp;
			}
		}
		
	}
	
	//FFT
	private void fft()
	{
		BitReverse();
		int m = 2;
		MyComplex w, ww, t;

		//バタフライ演算
		while (m <= size)
		{
			double arg = -2.0 * Math.PI / m;
			w = new MyComplex(Math.cos(arg), Math.sin(arg));
			
			//-1乗 -(-2.0*PI/size) = 2.0*PI/size
			//if (isReverse) w = one / w; 
	    
			for(int ii=0;ii<size; ii+=m)
			{
				ww = new MyComplex(1.0, 0);
				
				for(int j=0;j<m/2;j++)
				{
					int a = ii + j;
					int b = ii + j + m/2;
		
					t = MyComplex.multiply(ww,fftArray[b]);
		
					fftArray[b] = MyComplex.subtract(fftArray[a],t);
					fftArray[a] = MyComplex.add(fftArray[a],t);
		
					ww = MyComplex.multiply(ww, w);
				}
			}
			m *= 2;
		}
	}

	//create method
	public static MyFFTFunction Load(int sample,double[] wavedata,int wintype)
	{
		MyFFTFunction fft = new MyFFTFunction(sample);
		fft.SetWaveData(wavedata, wintype);
		fft.fft();
		return fft;
	}

	///<@brief  各波の包含率を取得するメソッド(0.1〜100Hzまでを対象)
	///<@param [in] 周波数帯域の種類
	///<@return 指定した波周波数帯の包含率
	public double getWaveProbability(int wbtype)
	{
		double spectrumwidth = 512.0 / size;
		double sum = 0;
		double bandpower = 0;
		double power = 0;
		int ii=0;

		
		if((0.1/spectrumwidth) <=1.0) ii = 1;
		else ii=(int)(0.1/spectrumwidth);
		
		
		for(;ii<(int)(100.0/spectrumwidth);ii++)
		{
			power = (fftArray[ii].abs()) * fftArray[ii].abs();
		
			switch(wbtype)
			{
				//δ波
				case WaveBandType.Delta:
				{
					boolean bandmin = (ii >= (int)(0.1/spectrumwidth));
					boolean bandmax = (ii < (int)(3.0/spectrumwidth));
					
					if( bandmin && bandmax ) bandpower += power;
		
				}break;
		
				//θ波
				case WaveBandType.Theta:
				{
					boolean bandmin = (ii >= (int)(4.0/spectrumwidth));
					boolean bandmax = (ii < (int)(7.0/spectrumwidth));
					
					if( bandmin && bandmax ) bandpower += power;
				}break;
				
				//α波
				case WaveBandType.Alpha:
				{
					boolean bandmin = (ii >= (int)(8.0/spectrumwidth));
					boolean bandmax = (ii < (int)(12.0/spectrumwidth));
					
					if( bandmin && bandmax ) bandpower += power;
		
				}break;
		
				//β波
				case WaveBandType.Beta:
				{
					boolean bandmin = (ii >= (int)(13.0/spectrumwidth));
					boolean bandmax = (ii < (int)(30.0/spectrumwidth));
					
					if( bandmin && bandmax ) bandpower += power;
		
				}break;
		
				//γ波
				case WaveBandType.Gamma:
				{
					boolean bandmin = (ii >= (int)(30.0/spectrumwidth));
					boolean bandmax = (ii < (int)(100.0/spectrumwidth));
					
					if( bandmin && bandmax ) bandpower += power;
					
				}break;	
			}

			/*NeuroSky周波数定義では3.0〜4.0となど周波数間が空く場合が
			あるためその分の周波数配列要素に関しては加算を除外する*/
			boolean aa = (ii >= (int)(3.0/spectrumwidth));
			boolean bb = (ii <  (int)(4.0/spectrumwidth));
			boolean cc = (ii >= (int)(7.0/spectrumwidth));
			boolean dd = (ii <  (int)(8.0/spectrumwidth));
			boolean ee = (ii >= (int)(12.0/spectrumwidth));
			boolean ff = (ii <  (int)(13.0/spectrumwidth));
	
			if( (aa && bb) || (cc && dd) || (ee && ff) ) ;
			else sum += power;
		}
		return (bandpower/sum);
	}
	
	
}
