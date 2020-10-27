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
	Private prevIntent As Intent
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.
	
	Private nf As NFC
	Private TagTech As TagTechnology

	Private TextMensage As EditText	
	Private RadioRead As RadioButton
	Private RadioWrite As RadioButton
	Private ListViewId As ListView
	Private Label1 As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("nfc")
	ListViewId.SingleLineLayout.Label.TextSize = 15
	
	Label1.Text = "Aproxime o cartão"
End Sub

Sub Activity_Resume
	'forces all nfc intents to be sent to this activity
	nf.EnableForegroundDispatch

	Dim si As Intent = Activity.GetStartingIntent
	'check that the intent is a new intent
	If si.IsInitialized = False Or si = prevIntent Then Return
	prevIntent = si
	If si.Action.EndsWith("TECH_DISCOVERED") Or si.Action.EndsWith("NDEF_DISCOVERED") Or si.Action.EndsWith("TAG_DISCOVERED") Then
		Dim techs As List = nf.GetTechList(si)
		Log($"Techs: ${techs}"$)
		'in this case we are only accessing Ndef tags.
		If techs.IndexOf("android.nfc.tech.Ndef") > -1 Then
			TagTech.Initialize("TagTech", "android.nfc.tech.Ndef" , si)
			'Connect to the tag
			TagTech.Connect
		Else
			ToastMessageShow("Tipo de cartão não suportado.", True)
		End If
	End If
End Sub

Private Sub TagTech_Connected (Success As Boolean)
	Log($"Connected: ${Success}"$)
	If Success = False Then
		ToastMessageShow("Erro ao conectar a tag.", True)
		Log(LastException)
	Else
		If RadioRead.Checked Then
			ReadNdef
		Else
			If TextMensage.Text = "" Then
				xui.MsgboxAsync("Por favor, preencha o campo.", "Texto vazio!")
			Else
				WriteNdef(Array(nf.CreateMimeRecord("text/plain", TextMensage.Text.GetBytes("UTF8"))))
			End If			
		End If
	End If
End Sub

Private Sub ReadNdef
	TagTech.RunAsync("ReadNdef", "getNdefMessage", Null, 0)
End Sub

Private Sub WriteNdef (Records() As Object)
	Dim RecordsJO As JavaObject
	RecordsJO.InitializeArray("android.nfc.NdefRecord", Records)
	Dim message As JavaObject
	message.InitializeNewInstance("android.nfc.NdefMessage", Array(RecordsJO))
	TagTech.RunAsync("WriteNdef", "writeNdefMessage", Array(message), 0)
End Sub

Private Sub WriteNdef_RunAsync (Flag As Int, Success As Boolean, Result As Object)
	Log($"Writing completed. Success=${Success}, Flag=${Flag}"$)
	If Success Then ReadNdef
End Sub

Private Sub ReadNdef_RunAsync (Flag As Int, Success As Boolean, Result As Object)
	Log($"Reading completed. Success=${Success}, Flag=${Flag}"$)
	ListViewId.Clear
	If Success Then
		If Result = Null Then
			ToastMessageShow("No records found.", False)
		Else
			Dim message As JavaObject = Result
			Dim records() As Object = message.RunMethod("getRecords", Null)
			For Each r As NdefRecord In records
				Dim b() As Byte = r.GetPayload
				ListViewId.AddSingleLine(BytesToString(b, 0, b.Length, "utf8"))
				ListViewId.SingleLineLayout.Label.TextColor = Colors.Black:
			Next
		End If
	End If
End Sub

Sub Activity_Pause (UserClosed As Boolean)
	nf.DisableForegroundDispatch
End Sub


Sub RadioRead_CheckedChange(Checked As Boolean)
	If Checked Then
		Label1.Text = "Aproxime o cartão"
	End If
End Sub

Sub RadioWrite_CheckedChange(Checked As Boolean)
	If Checked Then
		Label1.Text = "Escreva seu texto"
	End If
End Sub