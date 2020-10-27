B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=10
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: False
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
	Private xui As XUI
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.
	'DECLARAÇÃO DOS ELEMENTOS VISUAIS DEMONSTRADOS EM TELA
	Private SpinnerFont As Spinner
	Private SpinnerSize As Spinner
	Private SpinnerBarCodeHeight As Spinner
	Private SpinnerBarCodeWidth As Spinner
	Private SpinnerBarCode As Spinner
	Private ButtonPrintAll As Button
	Private ButtonPrintText As Button
	Private ButtonStatusImpressora As Button
	Private EditText As EditText
	Private ButtonPrintBarCode As Button
	Private RadioLeft As RadioButton
	Private RadioCenter As RadioButton
	Private RadioRight As RadioButton
	Private CheckBoxBold As CheckBox
	Private CheckBoxItalic As CheckBox
	Private CheckBoxUnderline As CheckBox
	Private ButtonPrintImage As Button
	
	'DECLARAÇÃO VARIÁVEIS QUE IRÃO RECEBER AS CONFIGURAÇÕES DE IMPRESSÃO DE TEXTO
	Dim text As String
	Dim font As String
	Dim fontSize As Byte
	Dim bold As Boolean
	Dim italic As Boolean
	Dim underline As Boolean
	Dim align As String
	
	'DECLARAÇÃO VARIÁVEIS QUE IRÃO RECEBER AS CONFIGURAÇÕES DE IMPRESSÃO DE BAR CODE
	Dim typeBarCode As String
	Dim heightBarCode As Long
	Dim widthBarCode As Long
	
	'DECLARAÇÃO DA VARIÁVEL QUE IRÁ SER UTILIZADA NA CONFIGURAÇÃO DA IMAGEM
	Dim imageBmp As Bitmap
	
	'DECLARAÇÃO VARIÁVEL QUE VAI PEGAR A ACTIVITY ATUAL E ENVIAR PARA O STARTIGEDI
	Dim r As Reflector
	
	'DECLARAÇÃO DA VARIÁVEL QUE CHAMA A LIB CRIADA PARA COMUNICAR COM A GEDI
	Private tsg As PrintTSG800
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	'INICIALIZAÇÃO DO LAYOUT
	Activity.LoadLayout("print")
	
	'INICIALIZAÇÃO DOS ELEMENTOS VISUAIS DO LAYOUT
	SpinnerFont.AddAll(Array As String("DEFAULT","MONOSPACE","SANS SERIF","SERIF"))
	SpinnerSize.AddAll(Array As Int(20,30,40,50,60,70,80,90,100))
	SpinnerBarCodeHeight.AddAll(Array As Int(10,40,80,120,160,200,240,280,320,380))
	SpinnerBarCodeWidth.AddAll(Array As Int(10,40,80,120,160,200,240,280,320,380))
	SpinnerBarCode.AddAll(Array As String("QR_CODE","CODE_128","EAN_8","EAN_13", "PDF_417"))
	
	'ENVIO DA ACTIVITY A LIB DE COMUNICAÇÃO COM A GEDI
	tsg.startIGEDI(r.GetActivity)
	
	'INICIALIZAÇÃO VARIÁVEIS QUE IRÃO RECEBER AS CONFIGURAÇÕES DE IMPRESSÃO DE TEXTO
	text = ""
	font = "DEFAULT"
	fontSize = 40
	bold = False
	italic = False
	underline = False
	align = "LEFT"
	
	'INICIALIZAÇÃO VARIÁVEIS QUE IRÃO RECEBER AS CONFIGURAÇÕES DE IMPRESSÃO DE BAR CODE
	typeBarCode = "QR_CODE"
	widthBarCode = 120
	heightBarCode = 120
	
	'INICIALIZAÇÃO VARIÁVEL DA IMAGEM
	imageBmp.Initialize(File.DirAssets,"invoice.png")
	'imageBmp = LoadBitmapResize(File.DirAssets,"logo.png",100%x,100%x,True)
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub ButtonStatusImpressora_Click
	xui.MsgboxAsync(tsg.ButtonStatusImpressora(),"Status Impressora")
End Sub

Sub EditText_TextChanged (Old As String, New As String)
	text = New
End Sub

Sub RadioLeft_CheckedChange(Checked As Boolean)
	align = "LEFT"
End Sub

Sub RadioCenter_CheckedChange(Checked As Boolean)
	align = "CENTER"
End Sub

Sub RadioRight_CheckedChange(Checked As Boolean)
	align = "RIGHT"
End Sub

Sub CheckBoxBold_CheckedChange(Checked As Boolean)
	If Checked Then
		bold = True
	Else
		bold = False
	End If
End Sub

Sub CheckBoxUnderline_CheckedChange(Checked As Boolean)
	If Checked Then
		underline = True
	Else
		underline = False
	End If
End Sub

Sub CheckBoxItalic_CheckedChange(Checked As Boolean)
	If Checked Then
		italic = True
	Else
		italic = False
	End If
End Sub

Sub SpinnerFont_ItemClick (Position As Int, Value As Object)
	font = Value
End Sub

Sub SpinnerSize_ItemClick (Position As Int, Value As Object)
	fontSize = Value
End Sub

Sub ButtonPrintText_Click
	If text == "" Then
		xui.MsgboxAsync("Preencha o campo para imprimir","Campo texto vazio!")
	Else
		tsg.PrintText(text, font, fontSize, bold, italic, underline, align)
	End If
End Sub

Sub ButtonPrintImage_Click
	tsg.imprimeImagem(imageBmp, 500, 1000)
	'tsg.imprimeImagem(imageBmp, 200, 100)
End Sub

Sub SpinnerBarCodeHeight_ItemClick (Position As Int, Value As Object)
	heightBarCode = Value
End Sub

Sub SpinnerBarCodeWidth_ItemClick (Position As Int, Value As Object)
	widthBarCode = Value
End Sub

Sub SpinnerBarCode_ItemClick (Position As Int, Value As Object)
	typeBarCode = Value
End Sub

Sub ButtonPrintBarCode_Click
	If text == "" Then
		xui.MsgboxAsync("Preencha o campo para imprimir","Campo texto vazio!")
	Else
		Try
			tsg.imprimeBarCode(text, typeBarCode, heightBarCode, widthBarCode)
		Catch
			ToastMessageShow("Digite um código de barra válido",True)
		End Try
	End If
End Sub

Sub ButtonPrintAll_Click
	Try
		'Impressão Centralizada
		tsg.PrintText("CENTRALIZADO","MONOSPACE",30,True,False,False,"CENTER")
		'Fim Impressão Centralizada
		
		'Impressão Esquerda
		tsg.PrintText("ESQUERDA","MONOSPACE",40,True,False,False,"LEFT")
		'Fim Impressão Esquerda
		
		'Impressão Direita
		tsg.PrintText("DIREITA","MONOSPACE",20,True,False,False,"RIGHT")
		'Fim Impressão Direita
		
		'Impressão Normal
		tsg.PrintText("=======[Escrita Normal]=======","MONOSPACE",20,False,False,False,"LEFT")
		'Fim Impressão Normal
		
		'Impressão Negrito
		tsg.PrintText("=======[Escrita Netrigo]=======","MONOSPACE",20,True,False,False,"LEFT")
		'Fim Impressão Negrito
		
		'Impressão Italico
		tsg.PrintText("=======[Escrita Italico]=======","MONOSPACE",20,False,True,False,"LEFT")
		'Fim Impressão Italico
		
		'Impressão Sublinhado
		tsg.PrintText("======[Escrita Sublinhado]=====","MONOSPACE",20,False,False,True,"LEFT")
		'Fim Impressão Sublinhado
		
		'Impressão BarCode 128
		tsg.PrintText("====[Codigo Barras CODE 128]====","MONOSPACE",20,False,False,False,"CENTER")
		tsg.imprimeBarCode("12345678901234567890","CODE_128", 120, 120)
		'Fim Impressão BarCode 128
		
		'Impressão BarCode 13
		tsg.PrintText("====[Codigo Barras EAN13]====","MONOSPACE",20,False,False,False,"CENTER")
		tsg.imprimeBarCode("7891234567895", "EAN_13", 120, 120)
		'Fim Impressão BarCode 13
		
		'Impressão BarCode 13
		tsg.PrintText("===[Codigo QrCode Gertec LIB]===","MONOSPACE",20,False,False,False,"CENTER")
		tsg.imprimeBarCode("Gertec Developer Partner LIB","QR_CODE", 240, 240)
		'Fim Impressão BarCode 13
	Catch
		Log(LastException)
	End Try
End Sub