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
	'These variables can only be accessed from this module
	Private zx1 As ZxingBarcodeScanner
	
	Private ButtonFlash As Button
	Dim flashOn As Boolean
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("BarCodeV2")
	
	zx1.LaserColor = Colors.Yellow
	zx1.MaskColor = Colors.ARGB(150, 0, 0, 200)
	zx1.BorderColor = Colors.Magenta
	zx1.BorderStrokeWidth = 5
	zx1.BorderLineLength = 50
	zx1.Visible = True
	zx1.startScanner
	
	flashOn = False
End Sub

Sub Activity_Resume
	
End Sub

Sub Activity_Pause (UserClosed As Boolean)
	zx1.Visible = False
	zx1.stopScanner
End Sub


Sub ButtonFlash_Click
	
	If flashOn == False Then
		zx1.toggleFlash
		flashOn = True
	Else
		zx1.toggleFlash
		flashOn = True
	End If
End Sub

Sub zx1_scan_result (scantext As String, scanformat As String)
	zx1.stopScanner
	
	xui.MsgboxAsync(scanformat & ": " & scantext,"Código " & scanformat)
		Wait For Msgbox_Result (Result As Int)
		If Result = DialogResponse.POSITIVE Then
			zx1.startScanner
		End If
End Sub