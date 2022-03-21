package com.lsh.cloudmusic.ui.find

import `fun`.inaction.dialog.dialogs.CommonDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.lsh.cloudmusic.databinding.FragmentFindBinding
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import kotlinx.android.synthetic.main.fragment_find.*
import com.lsh.cloudmusic.R;
import com.lsh.cloudmusic.adapter.BannerAdapter
import com.lsh.cloudmusic.adapter.DiscoverRecoPlayListAdapter
import com.lsh.cloudmusic.me.PlayListDetailActivity
import com.lsh.cloudmusic.network.baen.Banner
import com.lsh.cloudmusic.util.MyWebView


class FindFragment : Fragment() {

    private lateinit var findViewModel: FindViewModel
    private var _binding: FragmentFindBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    /**
     * 推荐歌单的 recyclerView 的adapter
     */
    private val recommendPlayListAdapter = DiscoverRecoPlayListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        findViewModel =
            ViewModelProvider(this).get(FindViewModel::class.java)

        _binding = FragmentFindBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Banner 初始配置
        setupBanner()

//        // 按钮组 初始配置
//        setBtnGroup()

        // 推荐歌单 recyclerVIew 初始配置
        initRecommendPlayListRecyclerView()

        // banner 数据监听
        findViewModel.bannerData.observe(viewLifecycleOwner) {
            (bannerViewPager as BannerViewPager<Banner, BannerAdapter.ViewHolder>).refreshData(it)
        }

        // 推荐歌单数据监听
        findViewModel.recommendPlayListData.observe(viewLifecycleOwner) {
            recommendPlayListAdapter.setNewInstance(it)
        }

    }

    /**
     * 配置Banner
     */
    private fun setupBanner() {
        bannerViewPager.apply {
            adapter = BannerAdapter()
            setAutoPlay(true)
            setLifecycleRegistry(lifecycle)
            setIndicatorStyle(IndicatorStyle.ROUND_RECT)
            setIndicatorSliderGap(getResources().getDimensionPixelOffset(R.dimen.dp_4))
//            setIndicatorMargin(0, 0, 0, resources.getDimension(R.dimen.dp_10).toInt())
            setIndicatorSlideMode(IndicatorSlideMode.SMOOTH)
            setIndicatorSliderRadius(resources.getDimension(R.dimen.dp_3).toInt(), resources.getDimension(R.dimen.dp_4_5).toInt())
            setIndicatorSliderColor(ContextCompat.getColor(context, R.color.bannerIndicatorNormalColor),
                ContextCompat.getColor(context, R.color.bannerIndicatorCheckedColor))
            setOnPageClickListener { clickedView, position ->
                val data = findViewModel.bannerData.value?.get(position)
                data?.let { bannerData ->
                    activity?.let { activity ->
                        Log.e("tag", "url = ${bannerData.url}")
                        MyWebView.start(activity, bannerData.url)
                    }
                }
            }
        }.create()
    }

    private fun showNoImplementDialog() {
        context?.let {
            val dialog = CommonDialog(it)
            with(dialog) {
                setTitle("提示")
                setContent("暂未实现，下次一定！")
                onConfirmClickListener = {
                    dialog.dismiss()
                }
                onCancelClickListener = {
                    dialog.dismiss()
                }
                show()
            }
        }
    }

    /**
     * 配置 推荐歌单 的 recyclerView
     */
    private fun initRecommendPlayListRecyclerView() {
        recommendPlayListRecyclerView.adapter = recommendPlayListAdapter
        recommendPlayListRecyclerView.layoutManager = GridLayoutManager(context, 3)
        recommendPlayListAdapter.setOnItemClickListener { _, _, position ->
            val playList = findViewModel.recommendPlayListData.value!!.get(position)
            activity?.let {
                PlayListDetailActivity.start(it, playList.id)
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}