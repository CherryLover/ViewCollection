package me.monster.viewcollection.page.sub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.monster.viewcollection.databinding.FragmentViewBinding
import me.monster.viewcollection.page.sub.bottomsheet.BottomSheetPlusDialog

/**
 * @description
 * @author: Created jiangjiwei in 2021/12/13 11:52 上午
 */
class ViewFragment : Fragment() {

    private lateinit var vBinding: FragmentViewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vBinding = FragmentViewBinding.inflate(inflater)
        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var goblAnim = true
        vBinding.goblReady.setOnClickListener {
            if (goblAnim) {
                vBinding.goblReady.showAnimation()
            } else {
                vBinding.goblReady.stopAnimation()
            }
            goblAnim = !goblAnim
        }
        vBinding.btnBottomSheetDialog.setOnClickListener { BottomSheetPlusDialog.show(requireActivity()) }
        vBinding.lgpvLeft.setOnClickListener { vBinding.lgpvLeft.startProgress() }
        vBinding.lgpvRight.setOnClickListener { vBinding.lgpvRight.startProgress() }
        vBinding.btnLpvPlay.setOnClickListener { vBinding.lpvLight.pathAnimation(1500) }
        vBinding.btnLpvPause.setOnClickListener { vBinding.lpvLight.stopAnim() }
    }

}