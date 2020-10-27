package anywheresoftware.b4a;

import android.app.Activity;
import android.graphics.Bitmap;

import android.graphics.Paint;
import android.graphics.Typeface;

import android.os.Bundle;

import anywheresoftware.b4a.BA.ActivityObject;
import anywheresoftware.b4a.BA.DependsOn;
import anywheresoftware.b4a.BA.ShortName;

import br.com.gertec.gedi.GEDI;

import br.com.gertec.gedi.impl.Gedi;

import br.com.gertec.gedi.exceptions.GediException;

import br.com.gertec.gedi.interfaces.IGEDI;
import br.com.gertec.gedi.interfaces.IPRNTR;

import br.com.gertec.gedi.structs.GEDI_PRNTR_st_BarCodeConfig;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_PictureConfig;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_StringConfig;

import br.com.gertec.gedi.enums.GEDI_PRNTR_e_BarCodeType;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Alignment;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Status;

//import testeLib.PrintDays;

@ActivityObject
@DependsOn(values = {"classes.jar","zxing-android-embedded-3.4.0.jar"})
@ShortName("PrintTSG800")
public class Tsg800 extends Activity implements Runnable{
	
	static Activity activity;
	private Typeface typeface;
	
 	private static IGEDI iGedi = null;
    private static IPRNTR iPrint = null;

    private static GEDI_PRNTR_e_Status status;
    private static GEDI_PRNTR_st_StringConfig stringConfig;
    private GEDI_PRNTR_st_PictureConfig pictureConfig;
    
    private static boolean isInitPrint = false;
	
	//private static PrintDays printDays = new PrintDays();
	
	private ConfigPrint configPrint = new ConfigPrint();
    private final String IMPRESSORA_ERRO = "Impressora com erro.";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
	}
	
	public Tsg800() {
		//gertecPrinter = new GertecPrinter(this.getApplicationContext());
		System.out.println("87: Days"); //
		System.out.println("VERSÃO 0.0"); //
		//activity = (Activity) getActivity();
		//startIGEDI();
	}
	
	public void startIGEDI(Activity a) {
		activity = a;
    	System.out.println("52: startGEDI()");
    	System.out.println("53: activity: " + activity);
    	run();
    }
	
	@Override
	public void run() {
		System.out.println("64: run()"); //
		// TODO Auto-generated method stub
		iGedi = new Gedi(activity);
        iGedi = GEDI.getInstance(activity);
        iPrint = iGedi.getPRNTR();
        try {
            new Thread();
			Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(activity);
		System.out.println("XXXXXXXXXXXXXXXXXXXXXX");
	}
	
	public static  boolean isImpressoraOK(){
        if( status.getValue() == 0 ){
            return true;
        }
        return false;
    }

    public String getStatusImpressora() throws GediException {
        try {
      	  ImpressoraInit(); //ERRO B4A
      	  status = iPrint.Status(); 
        } catch ( GediException e) {
            throw new GediException(e.getErrorCode());
        }
        
        return traduzStatusImpressora(status);
    }

    public static void ImpressoraInit() throws GediException{
  	  //System.out.println("IN INIT IMPRESSORA"); //CHEGA AQUI
  	  
        try{
          //System.out.println(" TRY INIT IMPRESSORA"); //CHEGA AQUI
      	  //System.out.println("iPrint: " + iPrint + "/isInitPrint: " + isInitPrint + "/iGedi: " + iGedi);
      	  
            if(iPrint != null && !isInitPrint){
                isInitPrint = true;
                iPrint.Init();
            }
        }catch (GediException e){
      	  	//System.out.println("ERR INIT IMPRESSORA");
            e.printStackTrace();
            throw new GediException(e.getErrorCode());
        }
    }

    public static void ImpressoraOutput() throws GediException {
        try {
            if(iPrint != null  ){
                iPrint.Output();
                isInitPrint = false;
            }
        } catch (GediException e) {
            e.printStackTrace();
            throw new GediException(e.getErrorCode());
        }
    }
    
    public void ImprimirTexto(String texto) throws Exception {
        try {
            getStatusImpressora();
            if (!isImpressoraOK()) {
                throw new Exception(IMPRESSORA_ERRO);
            }
            sPrintLine(texto);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
    
    private boolean sPrintLine(String texto) throws Exception {
        // Print Data
        try {
            ImpressoraInit();
            iPrint.DrawStringExt(stringConfig, texto);
            return true;
        } catch (GediException e) {
            throw new GediException(e.getErrorCode());
        }
    }
    
    public boolean imprimeBarCode(String texto, String type, int height, int width) throws GediException {
        try {

            GEDI_PRNTR_st_BarCodeConfig barCodeConfig = new GEDI_PRNTR_st_BarCodeConfig();
            // Bar Code Type
            barCodeConfig.barCodeType = GEDI_PRNTR_e_BarCodeType.valueOf(type);

            // Height
            barCodeConfig.height = height;
            // Width
            barCodeConfig.width = width;

            ImpressoraInit();
            iPrint.DrawBarCode(barCodeConfig, texto);
            this.avancaLinha(configPrint.getAvancaLinhas());
            ImpressoraOutput();
            
            return true;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        } catch (GediException e) {
            throw new GediException(e.getErrorCode());
        }
    }
    
    public void setConfigImpressao(ConfigPrint config) {
        this.configPrint = config;

        stringConfig = new GEDI_PRNTR_st_StringConfig(new Paint());
        stringConfig.paint.setTextSize(configPrint.getTamanho());
        stringConfig.paint.setTextAlign(Paint.Align.valueOf(configPrint.getAlinhamento()));
        stringConfig.offset = configPrint.getOffSet();
        stringConfig.lineSpace = configPrint.getLineSpace();
        
        if(configPrint.getFonte().equals("NORMAL")) {
        	this.typeface = Typeface.create(configPrint.getFonte(), Typeface.NORMAL);
        }else if(configPrint.getFonte().equals("DEFAULT")) {
        	this.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
        }else if(configPrint.getFonte().equals("DEFAULT BOLD")) {
        	this.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL);
        }else if(configPrint.getFonte().equals("MONOSPACE")){
			this.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
        }else if(configPrint.getFonte().equals("SANS SERIF")){
			this.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
		}else if(configPrint.getFonte().equals("SERIF")){
			this.typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL);
		}else {
			this.typeface = Typeface.createFromAsset(activity.getAssets(), configPrint.getFonte());
		}
        

        if (this.configPrint.isNegrito() && this.configPrint.isItalico()) {
            typeface = Typeface.create(typeface, Typeface.BOLD_ITALIC);
        } else if (this.configPrint.isNegrito()) {
            typeface = Typeface.create(typeface, Typeface.BOLD);
        } else if (this.configPrint.isItalico()) {
            typeface = Typeface.create(typeface, Typeface.ITALIC);
        }

        if (this.configPrint.isSublinhado()) {
            stringConfig.paint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
        }

        stringConfig.paint.setTypeface(this.typeface);
    }
    
    public boolean imprimeImagem(Bitmap uri, int width, int height) throws GediException {

        //int id = 0;
        //Bitmap bmp;
        try {
            pictureConfig = new GEDI_PRNTR_st_PictureConfig();
            // Align
            pictureConfig.alignment = GEDI_PRNTR_e_Alignment.valueOf(configPrint.getAlinhamento());

            // Height
            pictureConfig.height = height;
            // Width
            pictureConfig.width = width;

            //bmp = BitmapFactory.decodeFile(uri);

            
            ImpressoraInit();
            iPrint.DrawPictureExt(pictureConfig, uri);
            this.avancaLinha(configPrint.getAvancaLinhas());
            
            ImpressoraOutput();

            return true;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        } catch (GediException e) {
            throw new GediException(e.getErrorCode());
        }
    }
    
    public void avancaLinha(int linhas) throws GediException {
        try {
            if (linhas > 0) {
                iPrint.DrawBlankLine(linhas);
            }
        } catch (GediException e) {
            throw new GediException(e.getErrorCode());
        }
    }
    
    public void PrintText(
    		String texto,
			String fontFamily,
			int fontSize,
			boolean negrito,
			boolean italico,
			boolean sublinhado,
			String alinhamento) {
    	
    	this.configPrint.setTamanho(fontSize);
        this.configPrint.setNegrito(negrito);
        this.configPrint.setItalico(italico);
        this.configPrint.setSublinhado(sublinhado);
        this.configPrint.setFonte(fontFamily);
        this.configPrint.setAlinhamento(alinhamento);
        
        try {
        	getStatusImpressora();
        	
        	if (isImpressoraOK()) {
		        setConfigImpressao(configPrint);
		        ImprimirTexto(texto);
		        avancaLinha(150);
	            ImpressoraOutput();
        	}
        }catch (Exception e) {
        	e.printStackTrace();
	    }
    }
    
    private String traduzStatusImpressora(GEDI_PRNTR_e_Status status) {
        String retorno;
        switch (status) {
            case OK:
                retorno = "IMPRESSORA OK";
                break;

            case OUT_OF_PAPER:
                retorno = "SEM PAPEL";
                break;

            case OVERHEAT:
                retorno = "SUPER AQUECIMENTO";
                break;

            default:
                retorno = "ERRO DESCONHECIDO";
                break;
        }
        
        System.out.println("TRADUZ STATUS IMPRESSORA: " + retorno);
        return retorno;
    }
    
    public String ButtonStatusImpressora() {
    	String result = "...";
    	try {
			result = getStatusImpressora();
		} catch (GediException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return result;
    }
}