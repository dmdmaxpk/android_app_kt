package com.dmdmax.goonj.screens.fragments.hometabs

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.dmdmax.goonj.R
import com.dmdmax.goonj.base.BaseFragment
import com.dmdmax.goonj.firebase_events.EventManager
import com.dmdmax.goonj.models.Channel
import com.dmdmax.goonj.models.MediaModel
import com.dmdmax.goonj.models.TabModel
import com.dmdmax.goonj.network.client.NetworkOperationListener
import com.dmdmax.goonj.network.client.RestClient
import com.dmdmax.goonj.player.ExoPlayerManager
import com.dmdmax.goonj.screens.activities.WelcomeActivity
import com.dmdmax.goonj.screens.fragments.HomeFragment
import com.dmdmax.goonj.storage.GoonjPrefs
import com.dmdmax.goonj.utility.Constants
import com.dmdmax.goonj.utility.Logger
import com.google.android.exoplayer2.ui.PlayerView
import org.json.JSONArray

class HomeTabLiveTvFragment: BaseFragment(), WelcomeActivity.FullScreenListener {

    companion object {
        fun newInstance(args: Bundle?): HomeTabLiveTvFragment {
            val fragment = HomeTabLiveTvFragment();
            if (args != null) {
                fragment.arguments = args;
            }
            return fragment
        }
    }

    private lateinit var mDescription: TextView;
    private lateinit var mPlayer: PlayerView;
    private lateinit var mPlayerManager: ExoPlayerManager;
    private lateinit var mTab: TabModel;
    private lateinit var mPlayerLayout: FrameLayout;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_tab_live_tv_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mTab = arguments?.getSerializable(GenericCategoryFragment.ARGS_TAB) as TabModel
        mDescription = view.findViewById(R.id.description);
        mPlayer = view.findViewById(R.id.video_view)
        mPlayerLayout = view.findViewById(R.id.main_layout);

        mPlayerManager = ExoPlayerManager();
        mPlayerManager.init(requireContext(), mPlayer);

        RestClient(requireContext(), Constants.API_BASE_URL + Constants.Companion.EndPoints.CHANNEL_DETAILS + mTab.getResourceId(), RestClient.Companion.Method.GET, null, object : NetworkOperationListener {
            override fun onSuccess(response: String?) {
                val model = MediaModel.getLiveMediaModel(Channel.getObject(JSONArray(response).getJSONObject(0)), GoonjPrefs(requireContext()).getGlobalBitrate()!!);
                mPlayerManager.playMedia(model);

            }

            override fun onFailed(code: Int, reason: String?) {
                Logger.println("Failed to fetch data in hometablivefragment.kt")
            }
        }).exec()


        mDescription.text = mTab.getDesc();

        EventManager.getInstance(requireContext()).fireEvent(EventManager.Events.PLAY_CONTENT + mTab.getTabName()?.replace(" ", "_"))

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        (context as WelcomeActivity).setFullScreenListener(this);
    }

    override fun onStop() {
        super.onStop()
        mPlayerManager.pause()
        (context as WelcomeActivity).setFullScreenListener(null);
    }

    override fun onFullScreen(isFull: Boolean) {
        if(isFull){
            mPlayerLayout.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            mPlayerLayout.layoutParams.height = getHeight(requireContext())
        }else{
            mPlayerLayout.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
            mPlayerLayout.layoutParams.height = dpToPx(requireContext(), 210)
        }

        (parentFragment as HomeFragment).onFullscreen(isFull)
        mPlayerManager.setFullScreen(isFull)
    }

    fun getHeight(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        val fullHeight = displayMetrics.heightPixels / displayMetrics.density
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            fullHeight,
            context.resources.displayMetrics
        )
            .toInt()
    }

    fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}