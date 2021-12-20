package me.monster.viewcollection.page.sub.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.monster.viewcollection.R
import me.monster.viewcollection.databinding.DialogBottomSheetBinding

/**
 * @description
 * @author: Created jiangjiwei in 2021/12/16 3:43 下午
 */
class BottomSheetPlusDialog : DialogFragment() {

    companion object {
        private const val TAG = "BottomSheetPlusDialog"

        fun show(activity: FragmentActivity) {
            BottomSheetPlusDialog().show(activity.supportFragmentManager, TAG)
        }
    }

    private lateinit var binding: DialogBottomSheetBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DialogBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vSpace.maxListener = {
            dismiss()
        }

        val userAdapter = UserAdapter()
        val userLayoutManager = LinearLayoutManager(requireContext())

        binding.rvUserList.adapter = userAdapter
        binding.rvUserList.layoutManager = userLayoutManager
        binding.rvUserList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (userAdapter.canLoadMore(userLayoutManager.findLastCompletelyVisibleItemPosition())) {
                    if (userAdapter.getPageCount() >= 5) {
                        Toast.makeText(requireContext(), "没有更多数据", Toast.LENGTH_SHORT).show()
                        return
                    }
                    userAdapter.nextPage()
                    // 模拟加载更多
                    binding.root.postDelayed({
                        userAdapter.loadNextPageFinished()
                    }, 1 * 1000)
                }
            }
        })

        binding.ivClose.setOnClickListener { dismiss() }
        binding.vSpace.setOnClickListener { dismiss() }

        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing = true
            // 模拟刷新
            binding.root.postDelayed({
                userAdapter.refresh()
                binding.refreshLayout.isRefreshing = false
            }, 1 * 1000)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.let { dia ->
            dia.setCanceledOnTouchOutside(false)
            dia.window?.let { win ->
                val tempAttr = win.attributes
                tempAttr.width = ViewGroup.LayoutParams.MATCH_PARENT
                tempAttr.height = win.decorView.resources.displayMetrics.heightPixels
                win.setBackgroundDrawableResource(android.R.color.transparent)
                tempAttr.verticalMargin = 0F
                tempAttr.horizontalMargin = 0F

                win.decorView.setPadding(0, 0, 0, 0)

                win.attributes = tempAttr
                win.setWindowAnimations(R.style.bottomIn)
            }
        }
    }
}