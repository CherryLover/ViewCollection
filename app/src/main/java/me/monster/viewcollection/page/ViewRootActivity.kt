package me.monster.viewcollection.page

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import me.monster.viewcollection.R
import me.monster.viewcollection.databinding.ActivityViewRootBinding

class ViewRootActivity : AppCompatActivity() {

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, ViewRootActivity::class.java)
            context.startActivity(starter)
        }
    }

    private val vBinding: ActivityViewRootBinding by lazy { ActivityViewRootBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vBinding.root)
    }
}