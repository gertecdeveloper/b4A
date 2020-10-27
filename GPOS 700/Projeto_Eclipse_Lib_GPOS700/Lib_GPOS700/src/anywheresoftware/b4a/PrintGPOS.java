package anywheresoftware.b4a;

import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.Paint;
import android.graphics.Typeface;

import anywheresoftware.b4a.BA.DependsOn;
import anywheresoftware.b4a.BA.ShortName;

import br.com.gertec.gedi.exceptions.GediException;
import br.com.gertec.gedi.interfaces.ICL;
import br.com.gertec.gedi.interfaces.IGEDI;
import br.com.gertec.gedi.interfaces.IPRNTR;

import br.com.gertec.gedi.structs.GEDI_PRNTR_st_BarCodeConfig;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_PictureConfig;
import br.com.gertec.gedi.structs.GEDI_PRNTR_st_StringConfig;

import br.com.gertec.gedi.enums.GEDI_PRNTR_e_BarCodeType;
import br.com.gertec.gedi.GEDI;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Alignment;
import br.com.gertec.gedi.enums.GEDI_PRNTR_e_Status;

//import testeLib.PrintDays;

@DependsOn(values = {"gpos700.jar","zxing-android-embedded-3.4.0.jar"})
@ShortName("PrintGPOS700")
public class PrintGPOS{
	
	Context context;
	
	private Typeface typeface;
	
	ICL icl = null;
	
 	private IGEDI iGedi = null;
    private IPRNTR iPrint = null;

    private GEDI_PRNTR_e_Status status;
    private GEDI_PRNTR_st_StringConfig stringConfig;
    private GEDI_PRNTR_st_PictureConfig pictureConfig;
    
    private static boolean isInitPrint = false;
	
	private ConfigPrint configPrint = new ConfigPrint();
    private final String IMPRESSORA_ERRO = "Impressora com erro.";
	
	public PrintGPOS() {
		System.out.println("64: PrintGPOS"); //
	}
	
	public void startIGEDI(Context c) {
		this.context = c;
		
    	System.out.println("72: startGEDI()");
    	System.out.println("73: context: " + context);
    	
    	//run();
    	
    	new Thread(new Runnable() {
			@Override
			public void run() {
			    GEDI.init(PrintGPOS.this.context);
			    PrintGPOS.this.iGedi = GEDI.getInstance(PrintGPOS.this.context);
			    PrintGPOS.this.iPrint = PrintGPOS.this.iGedi.getPRNTR();
			    icl = GEDI.getInstance().getCL(); //Get ICL
			    try {
			        new Thread();
					Thread.sleep(250);
			    } catch (InterruptedException e) {
			        e.printStackTrace();
			    }
			}
		}).start();
    }

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println();
	}
	
	public boolean isImpressoraOK(){
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

    public void ImpressoraInit() throws GediException{  	  
        try{
      	  
            if(this.iPrint != null && !isInitPrint){
            	this.icl.PowerOff(); // Desligar Módulo NFC - comando Mandatório antes de enviar comandos para a impressora.                
                this.iPrint.Init();
                isInitPrint = true;
            }
        }catch (GediException e){
      	  	//System.out.println("ERR INIT IMPRESSORA");
            e.printStackTrace();
            throw new GediException(e.getErrorCode());
        }
    }

    public void ImpressoraOutput() throws GediException {
        try {
            if(this.iPrint != null  ){
                this.iPrint.Output();
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
        }
        if(configPrint.getFonte().equals("DEFAULT")) {
        	this.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
        }
        if(configPrint.getFonte().equals("DEFAULT BOLD")) {
        	this.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL);
        }
		if(configPrint.getFonte().equals("MONOSPACE")){
			this.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
        }
		if(configPrint.getFonte().equals("SANS SERIF")){
			this.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
		}
		if(configPrint.getFonte().equals("SERIF")){
			this.typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL);
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
    
    public boolean imprimeImagem(Bitmap uri) throws GediException {

        //int id = 0;
    	//Bitmap bmp;
        try {
            pictureConfig = new GEDI_PRNTR_st_PictureConfig();
            // Align
            pictureConfig.alignment = GEDI_PRNTR_e_Alignment.valueOf(configPrint.getAlinhamento());


            ImpressoraInit();
            iPrint.DrawPictureExt(pictureConfig, uri);
            this.avancaLinha(50);
            
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