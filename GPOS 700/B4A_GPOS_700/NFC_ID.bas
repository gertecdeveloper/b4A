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
	Private prevIntent As Intent
	Private ion As Object
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Private ButtonNfcId As Button
	Private ListViewIds As ListView
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("nfc_id")
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

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

Sub ion_Event(MethodName As String, Args() As Object) As Object
	If Args(0) = -1 Then
		
		Dim i As Intent = Args(1)
		
		Dim result As String = i.GetExtra("codigoNFCID")
		
		ListViewIds.AddSingleLine("Código do Cartão (ID): " & result)
		ListViewIds.SingleLineLayout.Label.TextColor = Colors.Gray
		ListViewIds.SingleLineLayout.Label.TextSize = 13
		
	End If
	Return Null
End Sub

Sub ButtonNfcId_Click
	prevIntent.Initialize(prevIntent.ACTION_VIEW,"http://www.nfcid.com")
	StartActivityForResult(prevIntent)
End Sub