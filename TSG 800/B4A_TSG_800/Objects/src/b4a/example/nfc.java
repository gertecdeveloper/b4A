package b4a.example;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class nfc extends Activity implements B4AActivity{
	public static nfc mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.nfc");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (nfc).");
				p.finish();
			}
		}
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.nfc");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.nfc", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (nfc) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (nfc) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return nfc.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (nfc) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (nfc) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            nfc mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (nfc) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.B4XViewWrapper.XUI _v5 = null;
public static anywheresoftware.b4a.objects.IntentWrapper _vvvv2 = null;
public anywheresoftware.b4a.objects.NFC _vvvv1 = null;
public anywheresoftware.b4a.objects.NFC.TagTechnologyWrapper _vvvv3 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _textmensage = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _radioread = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _radiowrite = null;
public anywheresoftware.b4a.objects.ListViewWrapper _listviewid = null;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public b4a.example.main _vv3 = null;
public b4a.example.barcodev2 _v6 = null;
public b4a.example.print _v0 = null;
public b4a.example.starter _vv1 = null;
public b4a.example.bar_code _bar_code = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 27;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 29;BA.debugLine="Activity.LoadLayout(\"nfc\")";
mostCurrent._activity.LoadLayout("nfc",mostCurrent.activityBA);
 //BA.debugLineNum = 30;BA.debugLine="ListViewId.SingleLineLayout.Label.TextSize = 15";
mostCurrent._listviewid.getSingleLineLayout().Label.setTextSize((float) (15));
 //BA.debugLineNum = 32;BA.debugLine="Label1.Text = \"Aproxime o cartão\"";
mostCurrent._label1.setText(BA.ObjectToCharSequence("Aproxime o cartão"));
 //BA.debugLineNum = 33;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 110;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 111;BA.debugLine="nf.DisableForegroundDispatch";
mostCurrent._vvvv1.DisableForegroundDispatch(mostCurrent.activityBA);
 //BA.debugLineNum = 112;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _si = null;
anywheresoftware.b4a.objects.collections.List _techs = null;
 //BA.debugLineNum = 35;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 37;BA.debugLine="nf.EnableForegroundDispatch";
mostCurrent._vvvv1.EnableForegroundDispatch(mostCurrent.activityBA);
 //BA.debugLineNum = 39;BA.debugLine="Dim si As Intent = Activity.GetStartingIntent";
_si = new anywheresoftware.b4a.objects.IntentWrapper();
_si = mostCurrent._activity.GetStartingIntent();
 //BA.debugLineNum = 41;BA.debugLine="If si.IsInitialized = False Or si = prevIntent Th";
if (_si.IsInitialized()==anywheresoftware.b4a.keywords.Common.False || (_si).equals(_vvvv2)) { 
if (true) return "";};
 //BA.debugLineNum = 42;BA.debugLine="prevIntent = si";
_vvvv2 = _si;
 //BA.debugLineNum = 43;BA.debugLine="If si.Action.EndsWith(\"TECH_DISCOVERED\") Or si.Ac";
if (_si.getAction().endsWith("TECH_DISCOVERED") || _si.getAction().endsWith("NDEF_DISCOVERED") || _si.getAction().endsWith("TAG_DISCOVERED")) { 
 //BA.debugLineNum = 44;BA.debugLine="Dim techs As List = nf.GetTechList(si)";
_techs = new anywheresoftware.b4a.objects.collections.List();
_techs = anywheresoftware.b4a.keywords.Common.ArrayToList(mostCurrent._vvvv1.GetTechList((android.content.Intent)(_si.getObject())));
 //BA.debugLineNum = 45;BA.debugLine="Log($\"Techs: ${techs}\"$)";
anywheresoftware.b4a.keywords.Common.LogImpl("23145738",("Techs: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_techs.getObject()))+""),0);
 //BA.debugLineNum = 47;BA.debugLine="If techs.IndexOf(\"android.nfc.tech.Ndef\") > -1 T";
if (_techs.IndexOf((Object)("android.nfc.tech.Ndef"))>-1) { 
 //BA.debugLineNum = 48;BA.debugLine="TagTech.Initialize(\"TagTech\", \"android.nfc.tech";
mostCurrent._vvvv3.Initialize("TagTech","android.nfc.tech.Ndef",(android.content.Intent)(_si.getObject()));
 //BA.debugLineNum = 50;BA.debugLine="TagTech.Connect";
mostCurrent._vvvv3.Connect(processBA);
 }else {
 //BA.debugLineNum = 52;BA.debugLine="ToastMessageShow(\"Tipo de cartão não suportado.";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Tipo de cartão não suportado."),anywheresoftware.b4a.keywords.Common.True);
 };
 };
 //BA.debugLineNum = 55;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 13;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 17;BA.debugLine="Private nf As NFC";
mostCurrent._vvvv1 = new anywheresoftware.b4a.objects.NFC();
 //BA.debugLineNum = 18;BA.debugLine="Private TagTech As TagTechnology";
mostCurrent._vvvv3 = new anywheresoftware.b4a.objects.NFC.TagTechnologyWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Private TextMensage As EditText";
mostCurrent._textmensage = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private RadioRead As RadioButton";
mostCurrent._radioread = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Private RadioWrite As RadioButton";
mostCurrent._radiowrite = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Private ListViewId As ListView";
mostCurrent._listviewid = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private Label1 As Label";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Private xui As XUI";
_v5 = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 10;BA.debugLine="Private prevIntent As Intent";
_vvvv2 = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 11;BA.debugLine="End Sub";
return "";
}
public static String  _radioread_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 115;BA.debugLine="Sub RadioRead_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 116;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 117;BA.debugLine="Label1.Text = \"Aproxime o cartão\"";
mostCurrent._label1.setText(BA.ObjectToCharSequence("Aproxime o cartão"));
 };
 //BA.debugLineNum = 119;BA.debugLine="End Sub";
return "";
}
public static String  _radiowrite_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 121;BA.debugLine="Sub RadioWrite_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 122;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 123;BA.debugLine="Label1.Text = \"Escreva seu texto\"";
mostCurrent._label1.setText(BA.ObjectToCharSequence("Escreva seu texto"));
 };
 //BA.debugLineNum = 125;BA.debugLine="End Sub";
return "";
}
public static String  _vvvv4() throws Exception{
 //BA.debugLineNum = 75;BA.debugLine="Private Sub ReadNdef";
 //BA.debugLineNum = 76;BA.debugLine="TagTech.RunAsync(\"ReadNdef\", \"getNdefMessage\", Nu";
mostCurrent._vvvv3.RunAsync(processBA,"ReadNdef","getNdefMessage",(Object[])(anywheresoftware.b4a.keywords.Common.Null),(int) (0));
 //BA.debugLineNum = 77;BA.debugLine="End Sub";
return "";
}
public static String  _readndef_runasync(int _flag,boolean _success,Object _result) throws Exception{
anywheresoftware.b4j.object.JavaObject _message = null;
Object[] _records = null;
anywheresoftware.b4a.objects.NFC.NdefRecordWrapper _r = null;
byte[] _b = null;
 //BA.debugLineNum = 92;BA.debugLine="Private Sub ReadNdef_RunAsync (Flag As Int, Succes";
 //BA.debugLineNum = 93;BA.debugLine="Log($\"Reading completed. Success=${Success}, Flag";
anywheresoftware.b4a.keywords.Common.LogImpl("23473409",("Reading completed. Success="+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_success))+", Flag="+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_flag))+""),0);
 //BA.debugLineNum = 94;BA.debugLine="ListViewId.Clear";
mostCurrent._listviewid.Clear();
 //BA.debugLineNum = 95;BA.debugLine="If Success Then";
if (_success) { 
 //BA.debugLineNum = 96;BA.debugLine="If Result = Null Then";
if (_result== null) { 
 //BA.debugLineNum = 97;BA.debugLine="ToastMessageShow(\"No records found.\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("No records found."),anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 99;BA.debugLine="Dim message As JavaObject = Result";
_message = new anywheresoftware.b4j.object.JavaObject();
_message = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(_result));
 //BA.debugLineNum = 100;BA.debugLine="Dim records() As Object = message.RunMethod(\"ge";
_records = (Object[])(_message.RunMethod("getRecords",(Object[])(anywheresoftware.b4a.keywords.Common.Null)));
 //BA.debugLineNum = 101;BA.debugLine="For Each r As NdefRecord In records";
_r = new anywheresoftware.b4a.objects.NFC.NdefRecordWrapper();
{
final Object[] group9 = _records;
final int groupLen9 = group9.length
;int index9 = 0;
;
for (; index9 < groupLen9;index9++){
_r = (anywheresoftware.b4a.objects.NFC.NdefRecordWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.NFC.NdefRecordWrapper(), (android.nfc.NdefRecord)(group9[index9]));
 //BA.debugLineNum = 102;BA.debugLine="Dim b() As Byte = r.GetPayload";
_b = _r.GetPayload();
 //BA.debugLineNum = 103;BA.debugLine="ListViewId.AddSingleLine(BytesToString(b, 0, b";
mostCurrent._listviewid.AddSingleLine(BA.ObjectToCharSequence(anywheresoftware.b4a.keywords.Common.BytesToString(_b,(int) (0),_b.length,"utf8")));
 //BA.debugLineNum = 104;BA.debugLine="ListViewId.SingleLineLayout.Label.TextColor =";
mostCurrent._listviewid.getSingleLineLayout().Label.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Black);
 }
};
 };
 };
 //BA.debugLineNum = 108;BA.debugLine="End Sub";
return "";
}
public static String  _tagtech_connected(boolean _success) throws Exception{
 //BA.debugLineNum = 57;BA.debugLine="Private Sub TagTech_Connected (Success As Boolean)";
 //BA.debugLineNum = 58;BA.debugLine="Log($\"Connected: ${Success}\"$)";
anywheresoftware.b4a.keywords.Common.LogImpl("23211265",("Connected: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_success))+""),0);
 //BA.debugLineNum = 59;BA.debugLine="If Success = False Then";
if (_success==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 60;BA.debugLine="ToastMessageShow(\"Erro ao conectar a tag.\", True";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Erro ao conectar a tag."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 61;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("23211268",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 }else {
 //BA.debugLineNum = 63;BA.debugLine="If RadioRead.Checked Then";
if (mostCurrent._radioread.getChecked()) { 
 //BA.debugLineNum = 64;BA.debugLine="ReadNdef";
_vvvv4();
 }else {
 //BA.debugLineNum = 66;BA.debugLine="If TextMensage.Text = \"\" Then";
if ((mostCurrent._textmensage.getText()).equals("")) { 
 //BA.debugLineNum = 67;BA.debugLine="xui.MsgboxAsync(\"Por favor, preencha o campo.\"";
_v5.MsgboxAsync(processBA,BA.ObjectToCharSequence("Por favor, preencha o campo."),BA.ObjectToCharSequence("Texto vazio!"));
 }else {
 //BA.debugLineNum = 69;BA.debugLine="WriteNdef(Array(nf.CreateMimeRecord(\"text/plai";
_vvvv5(new Object[]{mostCurrent._vvvv1.CreateMimeRecord("text/plain",mostCurrent._textmensage.getText().getBytes("UTF8"))});
 };
 };
 };
 //BA.debugLineNum = 73;BA.debugLine="End Sub";
return "";
}
public static String  _vvvv5(Object[] _records) throws Exception{
anywheresoftware.b4j.object.JavaObject _recordsjo = null;
anywheresoftware.b4j.object.JavaObject _message = null;
 //BA.debugLineNum = 79;BA.debugLine="Private Sub WriteNdef (Records() As Object)";
 //BA.debugLineNum = 80;BA.debugLine="Dim RecordsJO As JavaObject";
_recordsjo = new anywheresoftware.b4j.object.JavaObject();
 //BA.debugLineNum = 81;BA.debugLine="RecordsJO.InitializeArray(\"android.nfc.NdefRecord";
_recordsjo.InitializeArray("android.nfc.NdefRecord",_records);
 //BA.debugLineNum = 82;BA.debugLine="Dim message As JavaObject";
_message = new anywheresoftware.b4j.object.JavaObject();
 //BA.debugLineNum = 83;BA.debugLine="message.InitializeNewInstance(\"android.nfc.NdefMe";
_message.InitializeNewInstance("android.nfc.NdefMessage",new Object[]{(Object)(_recordsjo.getObject())});
 //BA.debugLineNum = 84;BA.debugLine="TagTech.RunAsync(\"WriteNdef\", \"writeNdefMessage\",";
mostCurrent._vvvv3.RunAsync(processBA,"WriteNdef","writeNdefMessage",new Object[]{(Object)(_message.getObject())},(int) (0));
 //BA.debugLineNum = 85;BA.debugLine="End Sub";
return "";
}
public static String  _writendef_runasync(int _flag,boolean _success,Object _result) throws Exception{
 //BA.debugLineNum = 87;BA.debugLine="Private Sub WriteNdef_RunAsync (Flag As Int, Succe";
 //BA.debugLineNum = 88;BA.debugLine="Log($\"Writing completed. Success=${Success}, Flag";
anywheresoftware.b4a.keywords.Common.LogImpl("23407873",("Writing completed. Success="+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_success))+", Flag="+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_flag))+""),0);
 //BA.debugLineNum = 89;BA.debugLine="If Success Then ReadNdef";
if (_success) { 
_vvvv4();};
 //BA.debugLineNum = 90;BA.debugLine="End Sub";
return "";
}
}
