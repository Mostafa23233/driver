package com.darbaast.driver.feature.authentication.login

import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.darbaast.driver.R
import com.darbaast.driver.custom.DarbastFragment
import com.darbaast.driver.data.sharedPreferences.DarbastSharedPreference
import com.darbaast.driver.data.token.TokenViewModel
import com.darbaast.driver.feature.authentication.AuthViewModel
import com.darbaast.driver.feature.authentication.sms.AppSignatureHelper
import com.darbaast.driver.feature.authentication.sms.SMSReceiver
import com.darbaast.driver.feature.main.MainActivity
import com.darbaast.driver.socket.SocketManager
import com.darbaast.driver.utils.DarbastAnimations
import com.darbaast.driver.utils.DarbastCounterDown
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment: DarbastFragment(),SMSReceiver.OTPReceiveListener {
    private val counterDown = DarbastCounterDown()
    private val smsReceiver = SMSReceiver()
    private lateinit var txtWrongPhone:TextView
    private lateinit var linearLayout: LinearLayout
    private lateinit var btnSendPhoneNumber:MaterialButton
    private lateinit var progressBarSendCodeFragment:CircularProgressIndicator
    private val viewModel : AuthViewModel by viewModels()
    private var code:String ?= null
    private var phone:String ?= null
    private lateinit var txtTimer:TextView
    private lateinit var txtSecond:TextView
    private lateinit var edtPhone : TextInputEditText
    private lateinit var edt1Code : TextInputEditText
    private lateinit var edt2Code : TextInputEditText
    private lateinit var edt3Code : TextInputEditText
    private lateinit var edt4Code : TextInputEditText
    private lateinit var edt5Code : TextInputEditText
    @Inject
    lateinit var socketManager: SocketManager
    @Inject
    lateinit var animation : DarbastAnimations

    @Inject
    lateinit var darbastSharedPreference: DarbastSharedPreference

    private val tokenViewModel : TokenViewModel by  viewModels()

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        smsReceiver.setOTPListener(this)
        if (android.os.Build.VERSION.SDK_INT>32){

            requireContext().registerReceiver(smsReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        }else{
            requireContext().registerReceiver(smsReceiver, intentFilter)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        val client = SmsRetriever.getClient(requireContext())
        val task = client.startSmsRetriever()

        task.addOnSuccessListener {
        }

        task.addOnFailureListener {
        }
        manageEditTexts()
        clickButton()
        authLiveData()

    }

    private fun authLiveData() {

        viewModel.progressBarLiveData.observe(viewLifecycleOwner){
            if (it){
                visibleItem(progressBar)
            }else{
                invisibleItem(progressBar)
            }
        }


        viewModel.loginMassageLiveData.observe(viewLifecycleOwner){
            if (it.type == "success"){
                phone = edtPhone.text.toString()
                    if (linearLayout.visibility == View.GONE){
                        showItemAndStartItem()
                    }
                toast("کد 5 رقمی به شماره شما ارسال شد")
            }

        }

        viewModel.sendCodeLiveData.observe(viewLifecycleOwner){
            if (it.type == "success" ){
                    tokenViewModel.insertToken(it.token.toString())
                    darbastSharedPreference.savePhone(phone.toString())
                    startActivity(MainActivity(),requireContext())
                }


        }
        viewModel.loginExceptionLiveData.observe(viewLifecycleOwner){
            if (it is HttpException){
                when(it.code()){
                    401 ->{
                        toast("این شماره در سیستم ما یافت نشد...لطفا مجدد بررسی کنید")
                        edtPhone.setText("")
                        edt5Code.setText("")
                        edt4Code.setText("")
                        edt3Code.setText("")
                        edt2Code.setText("")
                        edt1Code.setText("")
                        edtPhone.requestFocus()
                        edtPhone.isEnabled = true
                    }
                    400->{
                        toast("این شماره در سیستم ما یافت نشد...لطفا مجدد بررسی کنید")
                        edtPhone.setText("")
                        edt5Code.setText("")
                        edt4Code.setText("")
                        edt3Code.setText("")
                        edt2Code.setText("")
                        edt1Code.setText("")
                        edtPhone.requestFocus()
                        edtPhone.isEnabled = true
                    }
                }
            }
        }
        viewModel.exceptionLiveData.observe(viewLifecycleOwner){
            if (it is HttpException){
                when(it.code()){
                    401 ->{
                        toast("کد وارد شده اشتباه است...لطفا مجدد بررسی کنید")
                        edt5Code.setText("")
                        edt4Code.setText("")
                        edt3Code.setText("")
                        edt2Code.setText("")
                        edt1Code.setText("")
                        edt1Code.requestFocus()
                    }
                }
            }
        }
    }

    private fun clickButton() {

        btnSendPhoneNumber.setOnClickListener {
            val pattern = AppSignatureHelper(requireContext()).appSignatures[0]
            if (edtPhone.text?.length == 11){
                val jsonObject = JsonObject()
                jsonObject.addProperty("phone",edtPhone.text.toString())
                jsonObject.addProperty("pattern",pattern)
                viewModel.login(jsonObject)
            }else{
                toast("لطفا شماره خود را بررسی کنید!")
            }

        }
        txtWrongPhone.setOnClickListener {
            edtPhone.isEnabled = true
            edtPhone.setText("")
            invisibleItem(linearLayout)
            counterDown.stop()
            btnSendPhoneNumber.text = "ارسال کد تایید"
            btnSendPhoneNumber.isEnabled = true
            btnSendPhoneNumber.alpha = 1f
        }

    }

    private fun showItemAndStartItem(){
        visibleItem(linearLayout)
        visibleItem(txtWrongPhone)
        animation.animateFromRightToLeft(linearLayout)
        edt1Code.requestFocus()
        edtPhone.isEnabled = false
        counterDown.startCounter(
            progressBarSendCodeFragment,
            txtTimer,
            btnSendPhoneNumber,
            txtSecond
        )
    }
    private fun manageEditTexts() {

        edtPhone.doAfterTextChanged {
            if (it != null && it.toString() !=""){
                if (!it.contains("0")){
                   animation.shakeAnimation(edtPhone)
                    edtPhone.error = getString(R.string.pleaseEnterYourPhoneNumberWiithZero)
                    edtPhone.setText("")

                }else{
                    edtPhone.error = null
                }
            }
        }


        edt1Code.doOnTextChanged { _, _, _, _ ->
            edt5Code.setText("")
            if (!edt1Code.text.isNullOrEmpty()) {

                edt2Code.requestFocus()
            }

        }
        edt2Code.doOnTextChanged { _, _, _, _ ->
            if (!edt2Code.text.isNullOrEmpty()) {

                edt3Code.requestFocus()
            } else {
                edt1Code.requestFocus()
            }
        }
        edt3Code.doOnTextChanged { _, _, _, _ ->
            if (!edt3Code.text.isNullOrEmpty()) {

                edt4Code.requestFocus()
            } else {
                edt2Code.requestFocus()
            }
        }
        edt4Code.doOnTextChanged { _, _, _, _ ->
            if (!edt4Code.text.isNullOrEmpty()) {

                edt5Code.requestFocus()
            } else {
                edt3Code.requestFocus()
            }

        }
        edt5Code.doOnTextChanged { _, _, _, _ ->
            if (edt5Code.text.isNullOrEmpty()) {
                edt4Code.requestFocus()
            } else {
                val txt =
                    "" + edt1Code.text + edt2Code.text + edt3Code.text + edt4Code.text + edt5Code.text
                val code = Integer.parseInt(txt)
                val jsonObject = JsonObject()
                this.code = code.toString()
                jsonObject.addProperty("phone",this.phone)
                jsonObject.addProperty("code",this.code)
                viewModel.sendCode(jsonObject)

            }
        }
    }

    private fun init(view: View) {
        linearLayout = view.findViewById(R.id.linear_code)
        btnSendPhoneNumber = view.findViewById(R.id.btnSendPhoneNumber)
        progressBarSendCodeFragment = view.findViewById(R.id.progressBar_sendCodeFragment)
        linearLayout = view.findViewById(R.id.linear_code)
        progressBar = view.findViewById(R.id.progress_login)
        txtSecond = view.findViewById(R.id.txt_second)
        txtTimer = view.findViewById(R.id.txt_timer)
        edtPhone = view.findViewById(R.id.edtPhoneNumber)
        edt1Code = view.findViewById(R.id.edt_code1)
        edt2Code = view.findViewById(R.id.edt_code2)
        edt3Code = view.findViewById(R.id.edt_code3)
        edt4Code = view.findViewById(R.id.edt_code4)
        edt5Code = view.findViewById(R.id.edt_code5)
        txtWrongPhone = view.findViewById(R.id.txtWrongPhoneNumber)
    }
    override fun onOTPReceived(otp: String?) {
        if (otp != null){
            val num1 = otp[0].toString()
            val num2 = otp[1].toString()
            val num3 = otp[2].toString()
            val num4 = otp[3].toString()
            val num5 = otp[4].toString()
            edt1Code.setText(num1)
            edt2Code.setText(num2)
            edt3Code.setText(num3)
            edt4Code.setText(num4)
            edt5Code.setText(num5)


        }

    }

    override fun onDestroy() {
        super.onDestroy()
        requireContext().unregisterReceiver(smsReceiver)
    }

    override fun onOTPTimeOut() {
    }

    override fun onOTPReceivedError(error: String?) {
    }
}