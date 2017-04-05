package audio;

import com.jsyn.unitgen.UnitGenerator;

public class Channel {
	UnitGenerator wave;
	int tcount;
	int timer;
	//Sweep Variables
	boolean dosweep;
	int targetperiod;
	int sdivider;
	int dividerperiod;
	boolean sweepreload=false;
	int shift;
	boolean negate;
		
	//Envelope Variables
	boolean estart;
	boolean constantvolume;
	int decay;
	int edivider;
	int volume;
	boolean loop;
	
	//Linear Variables
	boolean linearhalt;
	boolean linearcontrol;
	int linearReload;
	int linearcount;
	
	
	//Length Counter Variables
	public int lengthcount;
	int[] lengthlookup= new int[]{
			10,254, 20,  2, 40,  4, 80,  6, 160,  8, 60, 10, 14, 12, 26, 14,
			12, 16, 24, 18, 48, 20, 96, 22, 192, 24, 72, 26, 16, 28, 32, 30};
	
	
	public Channel(UnitGenerator gen){
		wave = gen;
	}
	public void write(int i){
		
	}
	public void clockTimer(){
		if(tcount==0)
			tcount=timer;
		else
			tcount--;
	}
	public void lengthClock(){
		if(lengthcount!=0){
			if(!loop)
				lengthcount--;
			else
				lengthcount=0;
		}
	}
	public void envelopeClock(){
		if(estart){
			estart=false;
			decay = 15;
			edivider = volume+1;
		}
		else {
			if(edivider==0){
				edivider=volume+1;
				if(decay==0){
					if(loop)
						decay=15;

				}
				else
					decay--;
			}
			edivider--;
		}
		if(constantvolume)
			decay = volume;
	}
	/*public void sweepClock(){
		if(dosweep){
			if(sweepreload){
				sdivider = dividerperiod+1;
				targetperiod=timer;
				sweepreload=false;
			}
			else if(sdivider ==0){
				timer = targetperiod;
				//divider--;
			}
			else{
				int change = timer>>shift;
				if(negate)
					targetperiod =  timer - change;
				else
					targetperiod= timer + change;
				sdivider--;
			}
	
		}
	}*/
	public void linearClock(){
		if(linearhalt){
			linearcount = linearReload;
		}
		else{
			if(linearcount!=0){
				linearcount--;
			}
		}
		if(!linearcontrol)
			linearhalt=false;
	}
}
