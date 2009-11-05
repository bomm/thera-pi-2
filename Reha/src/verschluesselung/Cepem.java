package verschluesselung;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallFunctionMapper;
import com.sun.jna.win32.StdCallLibrary;

public class Cepem {
	//	sString := "-e -u"+"IK510841109"+" -P"+sCepemPW+" -O -Y* -S -r IK"+sCepemIK+" -i "+sEsolOrg+;
	//" -o "+sEsolSls+" -ZZufallszeichen -k"
	//L_Ret := CEPEM_Call(String2Psz(sString))
	//_DLL FUNCTION CEPEM_Call(cmd AS PSZ) AS LONG PASCAL:CepemDLL._CEPEM_Call@4
	interface CepemDLL extends StdCallLibrary { 

		 StdCallFunctionMapper mapper = new StdCallFunctionMapper();

		 CepemDLL INSTANCE = (CepemDLL) Native.loadLibrary("CEPEM", CepemDLL.class);

		 public int Cepem_Call(
				 String Cmd 
		 		);

	} 

}
