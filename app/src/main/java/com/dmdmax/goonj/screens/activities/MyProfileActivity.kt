package com.dmdmax.goonj.screens.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseActivity
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.models.Params
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.screens.fragments.paywall.PaywallGoonjFragment
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.dmdmax.goonj.utility.Toaster
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class MyProfileActivity: BaseActivity(), View.OnClickListener {

    private lateinit var mBAckArrow: ImageView;
    private lateinit var mProfileLayout: LinearLayout;
    private lateinit var mProgressBar: ProgressBar;

    private lateinit var mUsernameSmall: TextView;
    private lateinit var mUsernameEtSmall: EditText;

    private lateinit var mEmail: TextView;
    private lateinit var mEmailEt: EditText;

    private lateinit var mBirthday: TextView;
    private lateinit var mBirthdayEt: EditText;

    private lateinit var mEditProfile: TextView;
    private var isEditModelEnabled = false;

    private lateinit var mRadioButtonMale: RadioButton;
    private lateinit var mRadioButtonFemale: RadioButton;

    private lateinit var mPhone: TextView;
    private lateinit var mGenderName: TextView;
    private lateinit var mCalendar: Calendar;

    private lateinit var mGenderRadioGroup: RadioGroup;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        EventManager.getInstance(this).fireEvent("My_Profile${EventManager.Events.VIEW}");
    }

    override fun onResume() {
        super.onResume()
        init();
    }

    private fun init() {
        mProfileLayout = findViewById(R.id.profile_layout);
        mBAckArrow = findViewById(R.id.back_arrow);
        mEditProfile = findViewById(R.id.edit_profile)
        mProgressBar = findViewById(R.id.progress_bar)
        mPhone = findViewById(R.id.phone);
        mRadioButtonMale = findViewById(R.id.male);
        mRadioButtonFemale = findViewById(R.id.female);

        mGenderRadioGroup = findViewById(R.id.gender);
        mGenderName = findViewById(R.id.gender_name)

        mUsernameSmall = findViewById(R.id.username)
        mUsernameEtSmall = findViewById(R.id.usernameEt)

        mEmail = findViewById(R.id.email)
        mEmailEt = findViewById(R.id.emailEt)

        mBirthday = findViewById(R.id.birthday)
        mBirthdayEt = findViewById(R.id.birthdayEt)

        mEditProfile.setOnClickListener(this)
        mBAckArrow.setOnClickListener(this)

        mCalendar = Calendar.getInstance()

        val date = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, monthOfYear)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }

        mBirthdayEt.setOnClickListener {
            val dialog = DatePickerDialog(this@MyProfileActivity, date, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.datePicker.maxDate = System.currentTimeMillis();
            dialog.show();
        }

        val mPrefs = GoonjPrefs(this);
        if(mPrefs.getMsisdn(PaywallGoonjFragment.SLUG) != null && mPrefs.getMsisdn(PaywallGoonjFragment.SLUG) != "null"){
            loadProfileData();
        }else{
            mProgressBar.visibility = View.GONE;
            mProfileLayout.visibility = View.VISIBLE;
        }
    }

    private fun updateLabel() {
        val myFormat = "MM-dd-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        mBirthdayEt.setText(sdf.format(mCalendar.getTime()))
    }

    private fun loadProfileData(){
        RestClient(this, Constants.API_BASE_URL + Constants.Companion.EndPoints.GET_USER_PROFILE + GoonjPrefs(this).getMsisdn(PaywallGoonjFragment.SLUG), RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                try {
                    val rootObject = JSONObject(response);
                    if (rootObject.getInt("code") == 0) {
                        mUsernameSmall.text = if (rootObject.getJSONObject("data").has("fullname")) rootObject.getJSONObject("data").getString("fullname") else "";
                        mEmail.text = if (rootObject.getJSONObject("data").has("email")) rootObject.getJSONObject("data").getString("email") else "";
                        mBirthday.text = if (rootObject.getJSONObject("data").has("dateOfBirth")) rootObject.getJSONObject("data").getString("dateOfBirth") else "";
                        mPhone.text = if (rootObject.getJSONObject("data").has("msisdn")) rootObject.getJSONObject("data").getString("msisdn") else "";
                        if (rootObject.getJSONObject("data").has("gender")) {
                            if (rootObject.getJSONObject("data").getString("gender").toLowerCase().equals("female")) {
                                mGenderName.text = "Female"

                                mRadioButtonFemale.isChecked = true;
                                mRadioButtonMale.isChecked = false;
                            } else {
                                mGenderName.text = "Male"

                                mRadioButtonFemale.isChecked = false;
                                mRadioButtonMale.isChecked = true;
                            }
                        }else{
                            mGenderName.text = "Male"

                            mRadioButtonFemale.isChecked = false;
                            mRadioButtonMale.isChecked = true;
                        }

                        mGenderRadioGroup.visibility = View.GONE;
                        mGenderName.visibility = View.VISIBLE;

                        GoonjPrefs(this@MyProfileActivity).setUsername((if(mUsernameSmall.text.isEmpty()) "" else mUsernameSmall.text) as String?)
                    }

                    mProgressBar.visibility = View.GONE;
                    mProfileLayout.visibility = View.VISIBLE;
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailed(code: Int, reason: String?) {
                TODO("Not yet implemented")
            }
        }).exec();
    }

    private fun isEmailValid(email: String?): Boolean {
        val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun updateProfileData(){
        if(isEmailValid(mEmailEt.text.toString())){
            if(mRadioButtonMale.isChecked || mRadioButtonFemale.isChecked){
                hideKeyboard(this);
                mProfileLayout.visibility = View.GONE;
                mProgressBar.visibility = View.VISIBLE;

                mGenderRadioGroup.visibility = View.VISIBLE;
                mGenderName.visibility = View.GONE;


                val list: ArrayList<Params> = arrayListOf();
                list.add(Params("fullname", mUsernameEtSmall.text.toString()));
                list.add(Params("email", mEmailEt.text.toString()));
                list.add(Params("dateOfBirth", mBirthdayEt.text.toString()));
                list.add(Params("gender", if (mRadioButtonMale.isChecked) "male" else "female"));

                RestClient(this, Constants.API_BASE_URL + Constants.Companion.EndPoints.GET_USER_PROFILE + GoonjPrefs(this).getMsisdn(null), RestClient.Companion.Method.PUT, list, object : NetworkOperationListener {
                    override fun onSuccess(response: String?) {
                        Logger.println("updateProfileData - onSuccess: " + response);
                        loadProfileData();
                    }

                    override fun onFailed(code: Int, reason: String?) {
                        Logger.println("updateProfileData - onFailed: " + reason);
                    }
                }).exec();
            }else{
                Toaster.printToast(this, "Please select gender");
            }
        }else{
            Toaster.printToast(this, "Please provide valid email address");
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            mEditProfile.id -> {

                val mPrefs = GoonjPrefs(this);
                if(mPrefs.getMsisdn(PaywallGoonjFragment.SLUG) != null && mPrefs.getMsisdn(PaywallGoonjFragment.SLUG) != null){
                    isEditModelEnabled = !isEditModelEnabled;

                    mEditProfile.text = if (isEditModelEnabled) "Update Profile" else "Edit Profile"
                    if (!isEditModelEnabled) {
                        // update profile
                        updateProfileData();
                    }
                    switchMode(isEditModelEnabled);

                }else{
                    Toast.makeText(this, "Please login first to the Goonj Paywall in order to edit profile", Toast.LENGTH_LONG).show()
                }
            }

            mBAckArrow.id -> {
                onBackPressed();
            }
        }
    }

    private fun switchMode(editMode: Boolean){
        if(editMode){
            mUsernameSmall.visibility = View.GONE;
            mUsernameEtSmall.visibility = View.VISIBLE;
            mUsernameEtSmall.setText(mUsernameSmall.text);

            mEmail.visibility = View.GONE;
            mEmailEt.visibility = View.VISIBLE;
            mEmailEt.setText(mEmail.text);

            mBirthday.visibility = View.GONE;
            mBirthdayEt.visibility = View.VISIBLE;
            mBirthdayEt.setText(mBirthday.text);

            mGenderRadioGroup.visibility = View.VISIBLE;
            mGenderName.visibility = View.GONE;

        }else{
            // update mode
            mUsernameSmall.visibility = View.VISIBLE;
            mUsernameEtSmall.visibility = View.GONE;
            mUsernameSmall.setText(mUsernameEtSmall.text);

            mEmail.visibility = View.VISIBLE;
            mEmailEt.visibility = View.GONE;
            mEmail.setText(mEmailEt.text);

            mBirthday.visibility = View.VISIBLE;
            mBirthdayEt.visibility = View.GONE;
            mBirthday.setText(mBirthdayEt.text);

            mGenderRadioGroup.visibility = View.GONE;
            mGenderName.visibility = View.VISIBLE;
        }
    }
}