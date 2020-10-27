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
	Private ion As Object
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Private ButtonNFC As Button
	Private ListViewIDs As ListView
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("nfc")
End Sub

Sub Activity_Resume
	'forces all nfc intents to be sent to this activity
	'If prevIntent.IsInitialized Then
		'prevIntent.PutExtra()
		'prevIntent = Activity.GetStartingIntent
		'ToastMessageShow("extra name : " & Activity.GetStartingIntent, True)
	'End If
End Sub

Sub Activity_Pause (UserClosed As Boolean)
	
End Sub

Sub ion_Event(MethodName As String, Args() As Object) As Object
	If Args(0) = -1 Then
		Dim i As Intent = Args(1)
		
		'Dim id As String = i.ger
		'Dim jo As JavaObject = i
		
		'Dim uri As String = jo.RunMethod("codigoNFCGEDI",Null)
		
		Dim result As String = i.GetExtra("codigoNFCGEDI")
		
		ListViewIDs.AddSingleLine("Código do Cartão (GEDI): " & result)
		ListViewIDs.SingleLineLayout.Label.TextColor = Colors.Gray
		ListViewIDs.SingleLineLayout.Label.TextSize = 13
		
		'ToastMessageShow("GetData: " & i.GetData, True) 
		'ToastMessageShow("GetExtra: " & i.GetExtra("codigoNFCGEDI"), True)
		'ToastMessageShow("HasExtra: " & i.HasExtra("codigoNFCGEDI"), True)
		'ToastMessageShow("ExtrasToString: " & i.ExtrasToString, True)
		'ToastMessageShow("Flags: " & i.Flags, True)
		'ToastMessageShow("Action: " & i.Action, True)
		
		'ToastMessageShow("CÒDIGO: " & id, True)
		'ToastMessageShow("0: " & Args(0), True)
		'ToastMessageShow("1: " & Args(1), True)
		'ToastMessageShow("2: " & Args(2), True)
		'ToastMessageShow(prevIntent.ExtrasToString, True)
		'ToastMessageShow(prevIntent.GetData, True)
	End If
	Return Null
End Sub

Sub StartActivityForResult(i As Intent)
	Dim jo As JavaObject = GetBA
	ion = jo.CreateEvent("anywheresoftware.b4a.IOnActivityResult", "ion", Null)
	jo.RunMethod("startActivityForResult", Array As Object(ion, i))
End Sub

Sub GetBA As Object
	Dim jo As JavaObject
	Dim cls As String = Me
	cls = cls.SubString("class ".Length)
	jo.InitializeStatic(cls)
	Return jo.GetField("processBA")
End Sub

Sub ButtonNFC_Click
	prevIntent.Initialize(prevIntent.ACTION_VIEW,"http://www.nfcgedi.com")
	'StartActivity(prevIntent)
	StartActivityForResult(prevIntent)
End Sub