package czh.fast.lib.base


import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.czh.library.LoadingDialog
import com.vise.xsnow.http.ViseHttp
import com.zhy.autolayout.AutoLayoutActivity
import czh.fast.lib.R
import czh.fast.lib.utils.AppManager
import czh.fast.lib.utils.LightStatusBarUtils
import czh.fast.lib.utils.RomUtils
import czh.fast.lib.utils.StatusBarUtil

//Activity基类
abstract class BaseActivity : AutoLayoutActivity(), View.OnClickListener {
    lateinit var mContext: Context

    override fun onStart() {
        super.onStart()
        Log.d("当前Activity", "==》 (${javaClass.simpleName}.kt:1)")
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doBeforeSetcontentView()
        setContentView(layoutId)
        //设置状态栏颜色
        StatusBarUtil.setColor(this, ContextCompat.getColor(applicationContext, R.color.colorPrimary), 0)
        mContext = this
        views?.forEach { it.setOnClickListener(this) }
        afterInitView()
    }

    /**
     * 设置layout前配置
     */
    private fun doBeforeSetcontentView() {
        // 把actvity放到application栈中管理
        AppManager.appManager.addActivity(this)
        // 无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        // 设置竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // 默认不自动弹起软键盘
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }


    //获取布局文件
    abstract val layoutId: Int

    //初始化view
    abstract fun afterInitView()


    //点击事件view列表
    protected abstract val views: List<View>?

    //浅色状态栏
    fun setLightStatusBar() {
        StatusBarUtil.setColor(this, 0xffffffff.toInt(), 0)
        if (RomUtils.isLightStatusBarAvailable) {
            LightStatusBarUtils.setLightStatusBar(this, true)
        } else {
            StatusBarUtil.setColor(this, 0xff363636.toInt())
        }
    }

    private var mLoading: LoadingDialog? = null
    fun showLoading() {
        if (mLoading == null) {
            mLoading = LoadingDialog(this)
        }
        mLoading?.show()
    }

    fun stopLoading() {
        mLoading?.dismiss()
    }


    override fun onDestroy() {
        super.onDestroy()
        ViseHttp.cancelAll()
        AppManager.appManager.finishActivity(this)
    }
}
