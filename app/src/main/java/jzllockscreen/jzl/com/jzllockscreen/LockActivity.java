package jzllockscreen.jzl.com.jzllockscreen;


        import android.app.Activity;
        import android.app.admin.DevicePolicyManager;
        import android.content.ComponentName;
        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.view.KeyEvent;
        import android.view.View;
        import android.view.WindowManager;

public class LockActivity extends Activity {

    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000; //需要自己定义标志

    private DevicePolicyManager policyManager;

    private ComponentName componentName;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(FLAG_HOMEKEY_DISPATCHED, FLAG_HOMEKEY_DISPATCHED);//关键代码(屏蔽HOME键)
        setContentView(R.layout.activity_main);
    }

     public void LockScreen(View v){
        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, LockReceiver.class);
        if (policyManager.isAdminActive(componentName)) {//判断是否有权限(激活了设备管理器)
            policyManager.lockNow();// 直接锁屏
            android.os.Process.killProcess(android.os.Process.myPid());
        }else{
            activeManager();//激活设备管理器获取权限
        }
    }

    // 解除绑定
    public void Bind(View v){
        if(componentName!=null){
            policyManager.removeActiveAdmin(componentName);
            activeManager();
        }
    }
    @Override
    protected void onResume() {//重写此方法用来在第一次激活设备管理器之后锁定屏幕
        if (policyManager!=null && policyManager.isAdminActive(componentName)) {
            policyManager.lockNow();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        super.onResume();
    }

    private void activeManager() {
        //使用隐式意图调用系统方法来激活指定的设备管理器
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键锁屏");
        startActivity(intent);
    }
    // 屏蔽返回键功能
    /*
    public boolean onKeyDown(int keyCode,KeyEvent event){
        switch(keyCode){
            case KeyEvent.KEYCODE_HOME:return true;
            case KeyEvent.KEYCODE_BACK:return true;
            case KeyEvent.KEYCODE_CALL:return true;
            case KeyEvent.KEYCODE_SYM: return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN: return true;
            case KeyEvent.KEYCODE_VOLUME_UP: return true;
            case KeyEvent.KEYCODE_STAR: return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event) {    // 屏蔽HOME键
        // TODO Auto-generated method stub
        if (keyCode == event. KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}