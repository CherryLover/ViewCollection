package me.monster.viewcollection.page.sub.bottomsheet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.monster.viewcollection.R

/**
 * @description
 * @author: Created jiangjiwei in 2021/12/16 4:28 下午
 */
class UserAdapter : RecyclerView.Adapter<UserAdapter.Holder>() {

    companion object {
        private val avatars = intArrayOf(
            R.mipmap.avatar_girl_01,
            R.mipmap.avatar_girl_02,
            R.mipmap.avatar_girl_03,
            R.mipmap.avatar_girl_04,
            R.mipmap.avatar_girl_05,
            R.mipmap.avatar_girl_06,
            R.mipmap.avatar_girl_07
        )
        private val names = arrayOf(
            "Eva Hudson",
            "Lila Espinosa",
            "Annabelle Lynn",
            "Jason Daugherty",
            "Hayden Flowers",
            "Jaxon Carey",
            "Nathan Murphy"
        )
        private const val page_size = 20
    }
    private var itemCount = page_size
    private var page = 1

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAvatar: ImageView = itemView.findViewById(R.id.ivUserAvatar)
        val tvName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvIndex: TextView = itemView.findViewById(R.id.tvUserIndex)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user_list, parent, false)
        return Holder(itemView)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.ivAvatar.setImageResource(avatars[position % avatars.size])
        holder.tvName.text = names[position % names.size]
        holder.tvIndex.text = (position + 1).toString()
    }

    override fun getItemCount(): Int {
        return page_size * page
    }

    fun refresh() {
        page = 1
        notifyDataSetChanged()
    }

    private var isLoading = false

    fun nextPage() {
        isLoading = true
        page += 1
        notifyDataSetChanged()
    }

    fun loadNextPageFinished() {
        isLoading = false
    }


    fun getPageCount(): Int {
        return page
    }

    fun canLoadMore(visibleLast: Int): Boolean {
        if (isLoading) {
            return false
        }
        val c = getItemCount() - visibleLast
        return c < 5
    }
}