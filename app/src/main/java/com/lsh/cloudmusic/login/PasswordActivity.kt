package com.lsh.cloudmusic.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.lsh.cloudmusic.BaseActivity
import com.lsh.cloudmusic.MainActivity
import com.lsh.cloudmusic.databinding.ActivityPasswordBinding
import com.lsh.cloudmusic.network.OkCallback
import com.lsh.cloudmusic.network.ServiceCreator
import com.lsh.cloudmusic.network.baen.LoginResult
import com.lsh.cloudmusic.service.LoginService
import com.lsh.cloudmusic.util.UserBaseDataUtil
import com.lsh.cloudmusic.util.showToast
import kotlinx.android.synthetic.main.activity_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityPasswordBinding

    /**
     * 网络请求类
     */
    private val loginService = ServiceCreator.create<LoginService>()

    /**
     * 手机号码，从上一个Activity传来
     */
    private var phoneNumber: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 登录的手机号码
        phoneNumber = intent.getStringExtra(DATA_KEY_PHONE)!!

//        // 暂时使用指定的本人手机号和密码
//        phoneNumber = "17891029962"

        // 输入框获取焦点
        passwordEditText.requestFocus()
    }

    /**
     * ”忘记密码“按钮的点击事件
     */
//    fun onClickForgetPW(v: View) {
//        // 跳转到设置密码页面
//        SetPasswordActivity.start(this, phoneNumber)
//    }

    /**
     * 点击"登录"按钮时
     */
    fun onClickOkButton(v: View) {
        // 获取输入的密码
        val password = passwordEditText.text.toString();

//        // 暂时先将密码固定为本人账号密码
//        val password : String = "lsh275017"

        // 检查是否为空
        if (password.equals("")) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
            return
        }
        // 发送登录的网络请求
        loginService.login(phoneNumber, password).enqueue(object : OkCallback<LoginResult>() {
            override fun onSuccess(result: LoginResult) {
                super.onSuccess(result)
                // 登录成功
                Toast.makeText(this@PasswordActivity, "登录成功", Toast.LENGTH_SHORT).show()
                // 保存用户基本数据
                UserBaseDataUtil.setUserBaseData(result)
                // 跳转到主页面
                val intent = Intent(this@PasswordActivity, MainActivity::class.java)
                startActivity(intent)
            }

            override fun onError(code: Int, response: Response<LoginResult>) {
                showToast("密码错误", Toast.LENGTH_LONG)
            }
        })

    }

    companion object {

        private const val DATA_KEY_PHONE = "phone"

        /**
         * 启动这个 Activity
         */
        fun start(activity: Activity, phone: String) {
            val intent = Intent(activity, PasswordActivity::class.java)
            intent.putExtra(DATA_KEY_PHONE, phone)
            activity.startActivity(intent)
        }
    }

}