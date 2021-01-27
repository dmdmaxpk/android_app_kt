package com.dmdmax.goonj.screens.implements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.ContentLoadingProgressBar
import com.dmdmax.goonj.R
import com.dmdmax.goonj.adapters.UserPrefsContentGridAdapter
import com.dmdmax.goonj.base.BaseObservableView
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.network.responses.Category
import com.dmdmax.goonj.payments.PaymentHelper
import com.dmdmax.goonj.screens.views.LoginView
import com.dmdmax.goonj.screens.views.SigninView
import com.dmdmax.goonj.screens.views.UserContentPrefsView
import com.dmdmax.goonj.screens.views.VerificationView
import com.dmdmax.goonj.utility.Constants
import org.json.JSONArray
import org.json.JSONObject

class UserContentPrefsImpl: BaseObservableView<UserContentPrefsView.Listener>, UserContentPrefsView, View.OnClickListener {

    private lateinit var mNext: FrameLayout;
    private lateinit var mBAckArrow: ImageButton;

    private lateinit var mScreenTitle: TextView;
    private lateinit var mCategories: ArrayList<Category>;

    private lateinit var mCategoriesGrid: GridView;

    private lateinit var mProgressBar: ProgressBar;

    private lateinit var mAdapter: UserPrefsContentGridAdapter;

    constructor(inflater: LayoutInflater, parent: ViewGroup?) {
        setRootView(inflater.inflate(R.layout.activity_user_prefs, parent, false));
    }

    override fun initialize() {
        mScreenTitle = findViewById(R.id.screen_title);
        mScreenTitle.text = "choose topics that\ninterest you"

        mNext = findViewById(R.id.next);
        mNext.setOnClickListener(this);

        mBAckArrow = findViewById(R.id.back_arrow);
        mBAckArrow.setOnClickListener(this);

        mCategoriesGrid = findViewById(R.id.categories_grid);

        mProgressBar = findViewById(R.id.progress_bar);

        fetchCategories();
    }

    private fun fetchCategories(){
        RestClient(getContext(), Constants.API_BASE_URL + Constants.Companion.EndPoints.CATEGORY, RestClient.Companion.Method.GET, null, object: NetworkOperationListener{
            override fun onSuccess(response: String?) {
                getLogger().println("UserContentPrefs - fetchCategories - onSuccess - "+response);
                if(response != null){
                    mCategories = parseCategories(response);
                    getLogger().println("Categories length: "+mCategories.size)
                    mAdapter = UserPrefsContentGridAdapter(getContext(), mCategories);
                    mCategoriesGrid.adapter = mAdapter
                    mProgressBar.visibility = View.GONE

                    mCategoriesGrid.setOnItemClickListener(object: AdapterView.OnItemClickListener{
                        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            mCategories[position].setSelected(!mCategories[position].isSelected());
                            mAdapter.notifyDataSetChanged();
                        }
                    })
                }
            }

            override fun onFailed(code: Int, reason: String?) {

            }
        }).exec();
    }

    private fun parseCategories(response: String): ArrayList<Category>{
        val mCats: ArrayList<Category> = arrayListOf();
        val rootArr = JSONArray(response);
        for(i in 0 until rootArr.length()){
            val item: JSONObject = rootArr.getJSONObject(i);
            mCats.add(Category(item.getString("_id"), item.getString("name"), arrayListOf()))
        }
        return mCats;
    }

    override fun onClick(v: View?) {
        when(v){
            mNext -> {
                for (listener in getListeners()) {
                    listener.next();
                }
            }

            mBAckArrow -> {
                for (listener in getListeners()) {
                    listener.goBack();
                }
            }
        }
    }
}