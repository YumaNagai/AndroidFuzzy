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
		public static final int Rectangle = 0;		///< @brief ‹éŒ`‘‹
		public static final int Hanning   = 1;		///< @brief ƒnƒjƒ“ƒO‘‹
		public static final int	Hamming	  = 2;		///< @brief ƒnƒ~ƒ“ƒO‘‹
		public static final int	Blackman  = 3;		///< @brief ƒuƒ‰ƒbƒNƒ}ƒ“‘‹
		public static final int	WinTypeMax = 4;		///< @brief ”Ô•º
	}
	
	//brainWave's band
	public static final class WaveBandType
	{
		public static final int Delta = 5;			///< @brief ƒÂ”g(0.1`3.0Hz)
		public static final int	Theta = 6;			///< @brief ƒÆ”g(4.0`7.0Hz)
		public static final int	Alpha = 7;			///< @brief ƒ¿”g(8.0`12.0Hz)
		public static final int	Beta  = 8;			///< @brief ƒÀ”g(13.0`30.0Hz)
		public static final int	Gamma = 9;			///< @brief ƒÁ”g(30.0`100.0Hz)
		public static final int	WaveBandMax = 10;	///< @brief ”Ô•º
	}
	
	//ƒRƒ“ƒXƒgƒ‰ƒNƒ^
	private MyFFTFunction(int s)
	{
		use = s;
		this.size = NextPow2(s);
		fftArray = new MyComplex[size];
		for(int ii=0;ii<size;ii++) fftArray[ii] = new MyComplex(0, 0);
	}
	
	//”gƒf[ƒ^‚ÌÝ’è
	private void SetWaveData(double[] wave,int window)
	{
		for(int ii=0;ii<size;ii++)
		{
			MyComplex data = new MyComplex(0, 0);
				
			//”gƒf[ƒ^‚Ì”ÍˆÍ“à‚Ìê‡
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
	@brief sˆÈã‚ÌÅ¬‚Ì2‚Ì‚×‚«æ‚ð•Ô‚·ŠÖ”
	@param [in] s ƒTƒ“ƒvƒ‹’·
	@return sˆÈã‚ÌÅ¬‚Ì2‚Ì‚×‚«æ
	-------------------------------------------------*/
	private int NextPow2(int s)
	{
		int n = 1;
		  
		//2‚Ì¶1ƒrƒbƒgƒVƒtƒg‚Í Œ³‚Ì” * 2
		while (n < s)	n <<= 1;
		return n;
	}
	
	//ƒrƒbƒg”½“]
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
				b >>= 1;					//1/2”{
				a <<= 1;					//2”{
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

		//ƒoƒ^ƒtƒ‰ƒC‰‰ŽZ
		while (m <= size)
		{
			double arg = -2.0 * Math.PI / m;
			w = new MyComplex(Math.cos(arg), Math.sin(arg));
			
			//-1æ -(-2.0*PI/size) = 2.0*PI/size
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

	///<@brief  Še”g‚Ì•ïŠÜ—¦‚ðŽæ“¾‚·‚éƒƒ\ƒbƒh(0.1`100Hz‚Ü‚Å‚ð‘ÎÛ)
	///<@param [in] Žü”g”‘Ñˆæ‚ÌŽí—Þ
	///<@return Žw’è‚µ‚½”gŽü”g”‘Ñ‚Ì•ïŠÜ—¦
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
				//ƒÂ”g
				case WaveBandType.Delta:
				{
					boolean bandmin = (ii >= (int)(0.1/spectrumwidth));
					boolean bandmax = (ii < (int)(3.0/spectrumwidth));
					
					if( bandmin && bandmax ) bandpower += power;
		
				}break;
		
				//ƒÆ”g
				case WaveBandType.Theta:
				{
					boolean bandmin = (ii >= (int)(4.0/spectrumwidth));
					boolean bandmax = (ii < (int)(7.0/spectrumwidth));
					
					if( bandmin && bandmax ) bandpower += power;
				}break;
				
				//ƒ¿”g
				case WaveBandType.Alpha:
				{
					boolean bandmin = (ii >= (int)(8.0/spectrumwidth));
					boolean bandmax = (ii < (int)(12.0/spectrumwidth));
					
					if( bandmin && bandmax ) bandpower += power;
		
				}break;
		
				//ƒÀ”g
				case WaveBandType.Beta:
				{
					boolean bandmin = (ii >= (int)(13.0/spectrumwidth));
					boolean bandmax = (ii < (int)(30.0/spectrumwidth));
					
					if( bandmin && bandmax ) bandpower += power;
		
				}break;
		
				//ƒÁ”g
				case WaveBandType.Gamma:
				{
					boolean bandmin = (ii >= (int)(30.0/spectrumwidth));
					boolean bandmax = (ii < (int)(100.0/spectrumwidth));
					
					if( bandmin && bandmax ) bandpower += power;
					
				}break;	
			}

			/*NeuroSkyŽü”g”’è‹`‚Å‚Í3.0`4.0‚Æ‚È‚ÇŽü”g”ŠÔ‚ª‹ó‚­ê‡‚ª
			‚ ‚é‚½‚ß‚»‚Ì•ª‚ÌŽü”g””z—ñ—v‘f‚ÉŠÖ‚µ‚Ä‚Í‰ÁŽZ‚ðœŠO‚·‚é*/
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
