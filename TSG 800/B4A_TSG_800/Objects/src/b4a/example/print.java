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

public class print extends Activity implements B4AActivity{
	public static print mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "b4a.example", "b4a.example.print");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (print).");
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
		activityBA = new BA(this, layout, processBA, "b4a.example", "b4a.example.print");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "b4a.example.print", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (print) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (print) Resume **");
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
		return print.class;
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
            BA.LogInfo("** Activity (print) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (print) Pause event (activity is not paused). **");
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
            print mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (print) Resume **");
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
public anywheresoftware.b4a.objects.SpinnerWrapper _spinnerfont = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinnersize = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinnerbarcodeheight = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinnerbarcodewidth = null;
public anywheresoftware.b4a.objects.SpinnerWrapper _spinnerbarcode = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonprintall = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonprinttext = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonstatusimpressora = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonprintbarcode = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _radioleft = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _radiocenter = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper _radioright = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _checkboxbold = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _checkboxitalic = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper _checkboxunderline = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buttonprintimage = null;
public static String _vv6 = "";
public static String _vv7 = "";
public static byte _vv0 = (byte)0;
public static boolean _vvv1 = false;
public static boolean _vvv2 = false;
public static boolean _vvv3 = false;
public static String _vvv4 = "";
public static String _vvv5 = "";
public static long _vvv7 = 0L;
public static long _vvv6 = 0L;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _vvv0 = null;
public anywheresoftware.b4a.agraham.reflection.Reflection _vv5 = null;
public anywheresoftware.b4a.Tsg800 _vv4 = null;
public b4a.example.main _vv3 = null;
public b4a.example.barcodev2 _v6 = null;
public b4a.example.starter _vv1 = null;
public b4a.example.nfc _v7 = null;
public b4a.example.bar_code _bar_code = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 58;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 61;BA.debugLine="Activity.LoadLayout(\"print\")";
mostCurrent._activity.LoadLayout("print",mostCurrent.activityBA);
 //BA.debugLineNum = 64;BA.debugLine="SpinnerFont.AddAll(Array As String(\"DEFAULT\",\"MON";
mostCurrent._spinnerfont.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"DEFAULT","MONOSPACE","SANS SERIF","SERIF"}));
 //BA.debugLineNum = 65;BA.debugLine="SpinnerSize.AddAll(Array As Int(20,30,40,50,60,70";
mostCurrent._spinnersize.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new int[]{(int) (20),(int) (30),(int) (40),(int) (50),(int) (60),(int) (70),(int) (80),(int) (90),(int) (100)}));
 //BA.debugLineNum = 66;BA.debugLine="SpinnerBarCodeHeight.AddAll(Array As Int(10,40,80";
mostCurrent._spinnerbarcodeheight.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new int[]{(int) (10),(int) (40),(int) (80),(int) (120),(int) (160),(int) (200),(int) (240),(int) (280),(int) (320),(int) (380)}));
 //BA.debugLineNum = 67;BA.debugLine="SpinnerBarCodeWidth.AddAll(Array As Int(10,40,80,";
mostCurrent._spinnerbarcodewidth.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new int[]{(int) (10),(int) (40),(int) (80),(int) (120),(int) (160),(int) (200),(int) (240),(int) (280),(int) (320),(int) (380)}));
 //BA.debugLineNum = 68;BA.debugLine="SpinnerBarCode.AddAll(Array As String(\"QR_CODE\",\"";
mostCurrent._spinnerbarcode.AddAll(anywheresoftware.b4a.keywords.Common.ArrayToList(new String[]{"QR_CODE","CODE_128","EAN_8","EAN_13","PDF_417"}));
 //BA.debugLineNum = 71;BA.debugLine="tsg.startIGEDI(r.GetActivity)";
mostCurrent._vv4.startIGEDI(mostCurrent._vv5.GetActivity(processBA));
 //BA.debugLineNum = 74;BA.debugLine="text = \"\"";
mostCurrent._vv6 = "";
 //BA.debugLineNum = 75;BA.debugLine="font = \"DEFAULT\"";
mostCurrent._vv7 = "DEFAULT";
 //BA.debugLineNum = 76;BA.debugLine="fontSize = 40";
_vv0 = (byte) (40);
 //BA.debugLineNum = 77;BA.debugLine="bold = False";
_vvv1 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 78;BA.debugLine="italic = False";
_vvv2 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 79;BA.debugLine="underline = False";
_vvv3 = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 80;BA.debugLine="align = \"LEFT\"";
mostCurrent._vvv4 = "LEFT";
 //BA.debugLineNum = 83;BA.debugLine="typeBarCode = \"QR_CODE\"";
mostCurrent._vvv5 = "QR_CODE";
 //BA.debugLineNum = 84;BA.debugLine="widthBarCode = 120";
_vvv6 = (long) (120);
 //BA.debugLineNum = 85;BA.debugLine="heightBarCode = 120";
_vvv7 = (long) (120);
 //BA.debugLineNum = 88;BA.debugLine="imageBmp.Initialize(File.DirAssets,\"invoice.png\")";
mostCurrent._vvv0.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"invoice.png");
 //BA.debugLineNum = 90;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 96;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 98;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 92;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 94;BA.debugLine="End Sub";
return "";
}
public static String  _buttonprintall_click() throws Exception{
 //BA.debugLineNum = 189;BA.debugLine="Sub ButtonPrintAll_Click";
 //BA.debugLineNum = 190;BA.debugLine="Try";
try { //BA.debugLineNum = 192;BA.debugLine="tsg.PrintText(\"CENTRALIZADO\",\"MONOSPACE\",30,True";
mostCurrent._vv4.PrintText("CENTRALIZADO","MONOSPACE",(int) (30),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.False,"CENTER");
 //BA.debugLineNum = 196;BA.debugLine="tsg.PrintText(\"ESQUERDA\",\"MONOSPACE\",40,True,Fal";
mostCurrent._vv4.PrintText("ESQUERDA","MONOSPACE",(int) (40),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.False,"LEFT");
 //BA.debugLineNum = 200;BA.debugLine="tsg.PrintText(\"DIREITA\",\"MONOSPACE\",20,True,Fals";
mostCurrent._vv4.PrintText("DIREITA","MONOSPACE",(int) (20),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.False,"RIGHT");
 //BA.debugLineNum = 204;BA.debugLine="tsg.PrintText(\"=======[Escrita Normal]=======\",\"";
mostCurrent._vv4.PrintText("=======[Escrita Normal]=======","MONOSPACE",(int) (20),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.False,"LEFT");
 //BA.debugLineNum = 208;BA.debugLine="tsg.PrintText(\"=======[Escrita Netrigo]=======\",";
mostCurrent._vv4.PrintText("=======[Escrita Netrigo]=======","MONOSPACE",(int) (20),anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.False,"LEFT");
 //BA.debugLineNum = 212;BA.debugLine="tsg.PrintText(\"=======[Escrita Italico]=======\",";
mostCurrent._vv4.PrintText("=======[Escrita Italico]=======","MONOSPACE",(int) (20),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.True,anywheresoftware.b4a.keywords.Common.False,"LEFT");
 //BA.debugLineNum = 216;BA.debugLine="tsg.PrintText(\"======[Escrita Sublinhado]=====\",";
mostCurrent._vv4.PrintText("======[Escrita Sublinhado]=====","MONOSPACE",(int) (20),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.True,"LEFT");
 //BA.debugLineNum = 220;BA.debugLine="tsg.PrintText(\"====[Codigo Barras CODE 128]====\"";
mostCurrent._vv4.PrintText("====[Codigo Barras CODE 128]====","MONOSPACE",(int) (20),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.False,"CENTER");
 //BA.debugLineNum = 221;BA.debugLine="tsg.imprimeBarCode(\"12345678901234567890\",\"CODE_";
mostCurrent._vv4.imprimeBarCode("12345678901234567890","CODE_128",(int) (120),(int) (120));
 //BA.debugLineNum = 225;BA.debugLine="tsg.PrintText(\"====[Codigo Barras EAN13]====\",\"M";
mostCurrent._vv4.PrintText("====[Codigo Barras EAN13]====","MONOSPACE",(int) (20),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.False,"CENTER");
 //BA.debugLineNum = 226;BA.debugLine="tsg.imprimeBarCode(\"7891234567895\", \"EAN_13\", 12";
mostCurrent._vv4.imprimeBarCode("7891234567895","EAN_13",(int) (120),(int) (120));
 //BA.debugLineNum = 230;BA.debugLine="tsg.PrintText(\"===[Codigo QrCode Gertec LIB]===\"";
mostCurrent._vv4.PrintText("===[Codigo QrCode Gertec LIB]===","MONOSPACE",(int) (20),anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.False,anywheresoftware.b4a.keywords.Common.False,"CENTER");
 //BA.debugLineNum = 231;BA.debugLine="tsg.imprimeBarCode(\"Gertec Developer Partner LIB";
mostCurrent._vv4.imprimeBarCode("Gertec Developer Partner LIB","QR_CODE",(int) (240),(int) (240));
 } 
       catch (Exception e16) {
			processBA.setLastException(e16); //BA.debugLineNum = 234;BA.debugLine="Log(LastException)";
anywheresoftware.b4a.keywords.Common.LogImpl("22490413",BA.ObjectToString(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA)),0);
 };
 //BA.debugLineNum = 236;BA.debugLine="End Sub";
return "";
}
public static String  _buttonprintbarcode_click() throws Exception{
 //BA.debugLineNum = 177;BA.debugLine="Sub ButtonPrintBarCode_Click";
 //BA.debugLineNum = 178;BA.debugLine="If text == \"\" Then";
if ((mostCurrent._vv6).equals("")) { 
 //BA.debugLineNum = 179;BA.debugLine="xui.MsgboxAsync(\"Preencha o campo para imprimir\"";
_v5.MsgboxAsync(processBA,BA.ObjectToCharSequence("Preencha o campo para imprimir"),BA.ObjectToCharSequence("Campo texto vazio!"));
 }else {
 //BA.debugLineNum = 181;BA.debugLine="Try";
try { //BA.debugLineNum = 182;BA.debugLine="tsg.imprimeBarCode(text, typeBarCode, heightBar";
mostCurrent._vv4.imprimeBarCode(mostCurrent._vv6,mostCurrent._vvv5,(int) (_vvv7),(int) (_vvv6));
 } 
       catch (Exception e7) {
			processBA.setLastException(e7); //BA.debugLineNum = 184;BA.debugLine="ToastMessageShow(\"Digite um código de barra vál";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Digite um código de barra válido"),anywheresoftware.b4a.keywords.Common.True);
 };
 };
 //BA.debugLineNum = 187;BA.debugLine="End Sub";
return "";
}
public static String  _buttonprintimage_click() throws Exception{
 //BA.debugLineNum = 160;BA.debugLine="Sub ButtonPrintImage_Click";
 //BA.debugLineNum = 161;BA.debugLine="tsg.imprimeImagem(imageBmp, 500, 1000)";
mostCurrent._vv4.imprimeImagem((android.graphics.Bitmap)(mostCurrent._vvv0.getObject()),(int) (500),(int) (1000));
 //BA.debugLineNum = 163;BA.debugLine="End Sub";
return "";
}
public static String  _buttonprinttext_click() throws Exception{
 //BA.debugLineNum = 152;BA.debugLine="Sub ButtonPrintText_Click";
 //BA.debugLineNum = 153;BA.debugLine="If text == \"\" Then";
if ((mostCurrent._vv6).equals("")) { 
 //BA.debugLineNum = 154;BA.debugLine="xui.MsgboxAsync(\"Preencha o campo para imprimir\"";
_v5.MsgboxAsync(processBA,BA.ObjectToCharSequence("Preencha o campo para imprimir"),BA.ObjectToCharSequence("Campo texto vazio!"));
 }else {
 //BA.debugLineNum = 156;BA.debugLine="tsg.PrintText(text, font, fontSize, bold, italic";
mostCurrent._vv4.PrintText(mostCurrent._vv6,mostCurrent._vv7,(int) (_vv0),_vvv1,_vvv2,_vvv3,mostCurrent._vvv4);
 };
 //BA.debugLineNum = 158;BA.debugLine="End Sub";
return "";
}
public static String  _buttonstatusimpressora_click() throws Exception{
 //BA.debugLineNum = 100;BA.debugLine="Sub ButtonStatusImpressora_Click";
 //BA.debugLineNum = 101;BA.debugLine="xui.MsgboxAsync(tsg.ButtonStatusImpressora(),\"Sta";
_v5.MsgboxAsync(processBA,BA.ObjectToCharSequence(mostCurrent._vv4.ButtonStatusImpressora()),BA.ObjectToCharSequence("Status Impressora"));
 //BA.debugLineNum = 102;BA.debugLine="End Sub";
return "";
}
public static String  _checkboxbold_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 120;BA.debugLine="Sub CheckBoxBold_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 121;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 122;BA.debugLine="bold = True";
_vvv1 = anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 124;BA.debugLine="bold = False";
_vvv1 = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 126;BA.debugLine="End Sub";
return "";
}
public static String  _checkboxitalic_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 136;BA.debugLine="Sub CheckBoxItalic_CheckedChange(Checked As Boolea";
 //BA.debugLineNum = 137;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 138;BA.debugLine="italic = True";
_vvv2 = anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 140;BA.debugLine="italic = False";
_vvv2 = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 142;BA.debugLine="End Sub";
return "";
}
public static String  _checkboxunderline_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 128;BA.debugLine="Sub CheckBoxUnderline_CheckedChange(Checked As Boo";
 //BA.debugLineNum = 129;BA.debugLine="If Checked Then";
if (_checked) { 
 //BA.debugLineNum = 130;BA.debugLine="underline = True";
_vvv3 = anywheresoftware.b4a.keywords.Common.True;
 }else {
 //BA.debugLineNum = 132;BA.debugLine="underline = False";
_vvv3 = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 134;BA.debugLine="End Sub";
return "";
}
public static String  _edittext_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 104;BA.debugLine="Sub EditText_TextChanged (Old As String, New As St";
 //BA.debugLineNum = 105;BA.debugLine="text = New";
mostCurrent._vv6 = _new;
 //BA.debugLineNum = 106;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 16;BA.debugLine="Private SpinnerFont As Spinner";
mostCurrent._spinnerfont = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Private SpinnerSize As Spinner";
mostCurrent._spinnersize = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Private SpinnerBarCodeHeight As Spinner";
mostCurrent._spinnerbarcodeheight = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Private SpinnerBarCodeWidth As Spinner";
mostCurrent._spinnerbarcodewidth = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Private SpinnerBarCode As Spinner";
mostCurrent._spinnerbarcode = new anywheresoftware.b4a.objects.SpinnerWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private ButtonPrintAll As Button";
mostCurrent._buttonprintall = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Private ButtonPrintText As Button";
mostCurrent._buttonprinttext = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Private ButtonStatusImpressora As Button";
mostCurrent._buttonstatusimpressora = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private EditText As EditText";
mostCurrent._edittext = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private ButtonPrintBarCode As Button";
mostCurrent._buttonprintbarcode = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private RadioLeft As RadioButton";
mostCurrent._radioleft = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private RadioCenter As RadioButton";
mostCurrent._radiocenter = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private RadioRight As RadioButton";
mostCurrent._radioright = new anywheresoftware.b4a.objects.CompoundButtonWrapper.RadioButtonWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private CheckBoxBold As CheckBox";
mostCurrent._checkboxbold = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private CheckBoxItalic As CheckBox";
mostCurrent._checkboxitalic = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Private CheckBoxUnderline As CheckBox";
mostCurrent._checkboxunderline = new anywheresoftware.b4a.objects.CompoundButtonWrapper.CheckBoxWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Private ButtonPrintImage As Button";
mostCurrent._buttonprintimage = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 35;BA.debugLine="Dim text As String";
mostCurrent._vv6 = "";
 //BA.debugLineNum = 36;BA.debugLine="Dim font As String";
mostCurrent._vv7 = "";
 //BA.debugLineNum = 37;BA.debugLine="Dim fontSize As Byte";
_vv0 = (byte)0;
 //BA.debugLineNum = 38;BA.debugLine="Dim bold As Boolean";
_vvv1 = false;
 //BA.debugLineNum = 39;BA.debugLine="Dim italic As Boolean";
_vvv2 = false;
 //BA.debugLineNum = 40;BA.debugLine="Dim underline As Boolean";
_vvv3 = false;
 //BA.debugLineNum = 41;BA.debugLine="Dim align As String";
mostCurrent._vvv4 = "";
 //BA.debugLineNum = 44;BA.debugLine="Dim typeBarCode As String";
mostCurrent._vvv5 = "";
 //BA.debugLineNum = 45;BA.debugLine="Dim heightBarCode As Long";
_vvv7 = 0L;
 //BA.debugLineNum = 46;BA.debugLine="Dim widthBarCode As Long";
_vvv6 = 0L;
 //BA.debugLineNum = 49;BA.debugLine="Dim imageBmp As Bitmap";
mostCurrent._vvv0 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 52;BA.debugLine="Dim r As Reflector";
mostCurrent._vv5 = new anywheresoftware.b4a.agraham.reflection.Reflection();
 //BA.debugLineNum = 55;BA.debugLine="Private tsg As PrintTSG800";
mostCurrent._vv4 = new anywheresoftware.b4a.Tsg800();
 //BA.debugLineNum = 56;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 9;BA.debugLine="Private xui As XUI";
_v5 = new anywheresoftware.b4a.objects.B4XViewWrapper.XUI();
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _radiocenter_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 112;BA.debugLine="Sub RadioCenter_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 113;BA.debugLine="align = \"CENTER\"";
mostCurrent._vvv4 = "CENTER";
 //BA.debugLineNum = 114;BA.debugLine="End Sub";
return "";
}
public static String  _radioleft_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 108;BA.debugLine="Sub RadioLeft_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 109;BA.debugLine="align = \"LEFT\"";
mostCurrent._vvv4 = "LEFT";
 //BA.debugLineNum = 110;BA.debugLine="End Sub";
return "";
}
public static String  _radioright_checkedchange(boolean _checked) throws Exception{
 //BA.debugLineNum = 116;BA.debugLine="Sub RadioRight_CheckedChange(Checked As Boolean)";
 //BA.debugLineNum = 117;BA.debugLine="align = \"RIGHT\"";
mostCurrent._vvv4 = "RIGHT";
 //BA.debugLineNum = 118;BA.debugLine="End Sub";
return "";
}
public static String  _spinnerbarcode_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 173;BA.debugLine="Sub SpinnerBarCode_ItemClick (Position As Int, Val";
 //BA.debugLineNum = 174;BA.debugLine="typeBarCode = Value";
mostCurrent._vvv5 = BA.ObjectToString(_value);
 //BA.debugLineNum = 175;BA.debugLine="End Sub";
return "";
}
public static String  _spinnerbarcodeheight_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 165;BA.debugLine="Sub SpinnerBarCodeHeight_ItemClick (Position As In";
 //BA.debugLineNum = 166;BA.debugLine="heightBarCode = Value";
_vvv7 = BA.ObjectToLongNumber(_value);
 //BA.debugLineNum = 167;BA.debugLine="End Sub";
return "";
}
public static String  _spinnerbarcodewidth_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 169;BA.debugLine="Sub SpinnerBarCodeWidth_ItemClick (Position As Int";
 //BA.debugLineNum = 170;BA.debugLine="widthBarCode = Value";
_vvv6 = BA.ObjectToLongNumber(_value);
 //BA.debugLineNum = 171;BA.debugLine="End Sub";
return "";
}
public static String  _spinnerfont_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 144;BA.debugLine="Sub SpinnerFont_ItemClick (Position As Int, Value";
 //BA.debugLineNum = 145;BA.debugLine="font = Value";
mostCurrent._vv7 = BA.ObjectToString(_value);
 //BA.debugLineNum = 146;BA.debugLine="End Sub";
return "";
}
public static String  _spinnersize_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 148;BA.debugLine="Sub SpinnerSize_ItemClick (Position As Int, Value";
 //BA.debugLineNum = 149;BA.debugLine="fontSize = Value";
_vv0 = (byte)(BA.ObjectToNumber(_value));
 //BA.debugLineNum = 150;BA.debugLine="End Sub";
return "";
}
}
