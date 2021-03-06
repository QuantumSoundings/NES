package testing;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import core.NESAccess;
import core.NES;
import core.NESCallback;
import core.exceptions.UnSupportedMapperException;

public class TestUtils {
	
	
	static NESAccess createNES(File rom, NESCallback sys){
		NESAccess nes = new NES(sys);
		nes.setCallback(sys);
			try {
				nes.loadRom(rom);
				return nes;
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UnSupportedMapperException e) {
				e.printStackTrace();
			}
		return null;	
	}
	static int getPixelArrayHash(int[] pixels){
		return Arrays.hashCode(pixels);
	}
	static void runTest(int framecount, NESAccess nes) {
		for(int i = 0; i<framecount;i++)
			nes.runFrame();
	}
}
