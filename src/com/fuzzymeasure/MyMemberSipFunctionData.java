package com.fuzzymeasure;

public class MyMemberSipFunctionData 
{
	private double sstart;		//Small�X�^�[�g�ʒu
	private double send;		//Small�I���ʒu
	private double lstart;		//Large�X�^�[�g�ʒu
	private double lend;		//Large�X�^�[�g�ʒu

	private double sdegree;		///<@brief Small�̎��̌X��
	private double sintercept;	///<@brief Small�̎��̐ؕ�
	private double ldegree;		///<@brief Large�̎��̌X��
	private double lintercept;	///<@brief Large�̎��̐ؕ�
	
	//�R���X�g���N�^
	public MyMemberSipFunctionData()
	{
		sstart = send = lstart = lend = 0;
		sdegree = sintercept = ldegree = lintercept = 0.0;
	}
	

	///<@brief �����o�[�V�b�v�֐��쐬���\�b�h
	///<@param[in] ls Small�X�^�[�g�ʒu
	///<@param[in] le Small�I���ʒu
	///<@param[in] hs Large�X�^�[�g�ʒu
	///<@param[in] he Large�I���ʒu
	///<@param[in] lh Large or Small
	public void makeMenberSipFunction(double ss,double se,double ls,double le)
	{
		sstart = ss;
		send = se;
		lstart = ls;
		lend = le;

		//�� 
		//(x1,y1) (x2,y2) ����X�������߂� a=(y2-y1)/(x2-x1) 
		// b = y1 - a * x1

		//'Small'�̎�
		sdegree =  -1.0 / (send - sstart);
		sintercept = 1.0 - sdegree * sstart;		

		//'Large'�̎�
		ldegree = 1.0 / (lend - lstart);			
		lintercept = -ldegree * lstart;	
	}

	///<@brief �K���x���v�Z���Ԃ��֐�
	///<@@aram[in] value ���ׂ�l(��ܗ�)
	///<@param[in] lh 'S'��'L'�̕�����
	public double fuzzyset(double value,char sl)
	{	
		double grade = 0.0;

		//'Small'�̎�
		if(sl=='S')
		{
			// sstart�ȉ��̎�
			if(sstart > value) grade = 1.0;

			// sstart <= value <= send
			else if(sstart <= value && send >= value) grade = (sdegree * value) + sintercept;

			// send�ȏ�̎�
			else if(send < value) grade = 0.0;
		}
	
		//Large
		else
		{
			// lstart�ȉ��̎�
			if(lstart > value) grade = 0.0;

			// lstart <= value <= lend
			else if(lstart <= value && lend >= value) grade = (ldegree * value) + lintercept;
			
			// lend�ȏ�̎�
			else if(lend < value) grade = 1.0;
		}
		return grade;
	}
}
