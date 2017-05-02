package audio;

import ui.UserSettings;

public class AudioMixer implements java.io.Serializable {

	private static final long serialVersionUID = -5418535414924993071L;
	public AudioInterface audio;
	Triangle triangle;
	Pulse pulse1;
	Pulse pulse2;
	Noise noise;
	DMC dmc;
	int samplenum;
	int lpaccum = 0;
	int dckiller = 0;
	double cyclespersample;
	static double[] pulse_table = new double[]{0,
			0.01160914,0.022939481,0.034000949,0.044803002,0.055354659,0.065664528,0.075740825,0.085591398,0.095223748,0.104645048,0.113862159,0.122881647,0.131709801,0.140352645,0.148815953,0.157105263,0.165225885,
			0.173182917,0.180981252,0.188625592,0.196120454,0.203470178,0.210678941,0.21775076,0.224689499,0.231498881,0.23818249,0.244743777,0.251186072,0.257512581,0.263726398
	};
	static double[] tnd_table = new double[]{0,
			0.006699824,0.01334502,0.019936254,0.02647418,0.032959443,0.039392675,0.045774502,0.052105535,0.058386381,0.064617632,0.070799874,0.076933683,0.083019626,0.089058261,0.095050137,0.100995796,0.10689577,0.112750584,0.118560753,0.124326788,0.130049188,0.135728448,0.141365053,0.146959482,0.152512207,0.158023692,0.163494395,0.168924767,0.174315252,0.179666289,0.184978308
			,0.190251735,0.195486988,0.200684482,0.205844623,0.210967811,0.216054444,0.22110491,0.226119593,0.231098874,0.236043125,0.240952715,0.245828007,0.250669358,0.255477124,0.260251651,0.264993283,0.269702358,0.274379212,0.279024174,0.283637568,0.288219716,0.292770934,0.297291534,0.301781823,0.306242106,0.310672683,0.315073849,0.319445896,0.323789113,0.328103783
			,0.332390186,0.336648601,0.3408793,0.345082552,0.349258625,0.35340778,0.357530277,0.361626373,0.36569632,0.369740367,0.373758762,0.377751747
			,0.381719563,0.385662446,0.389580632,0.393474351,0.397343833,0.401189302,0.405010981,0.408809091,0.412583848,0.416335468,0.420064163,0.423770142,0.427453612,0.431114778,0.434753841,0.438371001,0.441966456,0.445540399,0.449093024,0.452624521,0.456135077,0.459624878,0.463094108,0.466542949,0.469971578,0.473380175,0.476768913,0.480137965
			,0.483487503,0.486817696,0.490128711,0.493420713,0.496693865,0.499948329,0.503184264,0.506401828,0.509601178,0.512782466,0.515945847,0.51909147,0.522219486,0.52533004,0.528423279,0.531499348,0.534558388,0.537600541,0.540625946,0.543634742,0.546627063,0.549603047,0.552562825,0.55550653,0.558434293,0.561346242,0.564242506,0.56712321
			,0.569988481,0.572838441,0.575673213,0.578492918,0.581297676,0.584087605,0.586862823,0.589623445,0.592369587,0.595101363,0.597818884,0.600522262,0.603211607,0.605887028,0.608548633,0.611196528,0.61383082,0.616451613,0.61905901
			,0.621653114,0.624234026,0.626801846,0.629356675,0.63189861,0.634427748,0.636944186,0.63944802,0.641939344,0.644418251,0.646884834,0.649339185,0.651781395,0.654211552,0.656629747,0.659036068,0.661430601,0.663813433,0.66618465,0.668544336,0.670892576,0.673229451,0.675555046,0.677869441,0.680172716,0.682464952,0.684746229,0.687016623,0.689276214,0.691525078
			,0.693763291,0.695990928,0.698208065,0.700414776,0.702611133,0.70479721,0.706973079,0.709138811,0.711294476,0.713440145,0.715575887,0.71770177,0.719817864,0.721924234,0.724020949,0.726108075,0.728185676,0.730253819,0.732312567,0.734361984,0.736402134,0.73843308,0.740454883,0.744471308,0.742467605
	};
	
	public AudioMixer(Pulse p1, Pulse p2, Triangle t, Noise n, DMC d){
		audio = new AudioInterface();
		cyclespersample = 1789773.0/audio.samplerate;
		pulse1 = p1;
		pulse2 = p2;
		triangle = t;
		noise = n;
		dmc = d;
	}
	/*private int highpass_filter(int sample) {
	        //for killing the dc in the signal
	        sample += dckiller;
	        dckiller -= sample >> 8;//the actual high pass part
	        dckiller += (sample > 0 ? -1 : 1);//guarantees the signal decays to exactly zero
	        return sample;
	    }
	private int lowpass_filter(int sample) {
        sample += lpaccum;
        lpaccum -= sample * 0.9;
        return lpaccum;
    }*/
	public void sample(){	
		pulse1.buildOutput();
		pulse2.buildOutput();
		noise.buildOutput();
		triangle.buildOutput();
		dmc.buildOutput();	
		samplenum++;
		if((samplenum%cyclespersample)<1)
			sendOutput();
	}
	private void sendOutput(){
		double p1 = getAverageSample(pulse1,UserSettings.pulse1MixLevel);
		double p2 = getAverageSample(pulse2,UserSettings.pulse2MixLevel);
		double t = getAverageSample(triangle,UserSettings.triangleMixLevel);
		double n = getAverageSample(noise,UserSettings.noiseMixLevel);
		double d = getAverageSample(dmc,UserSettings.dmcMixLevel);
		double pulse_out = 0.00752 * (p1+p2);//pulse_table[p1+p2];
		double tnd_out = 0.00851*t + 0.00494*n + 0.00335*d;//tnd_table[3*t + 2*n + d];
		double sample = pulse_out + tnd_out;
		sample-=.5;
		sample = ((sample*32768)*(UserSettings.masterMixLevel/100.0));
		audio.outputSample((int)sample);
	}
	final double getAverageSample(Channel chan,int UserMix){
		double d =((chan.total/cyclespersample)*(UserMix/100.0));
		chan.total=0;
		return d;	
	}
}
