package com.test.banner

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.seven.easybanner.adapter.BaseAdapter
import com.seven.easybanner.adapter.OnBannerClickListener
import com.seven.easybanner.model.Data
import com.seven.easybanner.model.DataSource
import kotlinx.android.synthetic.main.activity_test.*


class TestActivity : AppCompatActivity(), OnBannerClickListener {

    override fun onBannerClicked(view: View, position: Int, model: Data) {
        Toast.makeText(this, "position: $position  txt: ${model.txt}", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val list = mutableListOf<Data>()
        list.add(Data("0", DataSource.Net, "http://ww4.sinaimg.cn/large/006uZZy8jw1faic1xjab4j30ci08cjrv.jpg"))
        list.add(Data("1", DataSource.Net, "http://ww4.sinaimg.cn/large/006uZZy8jw1faic21363tj30ci08ct96.jpg"))
        list.add(Data("2", DataSource.Net, "http://ww4.sinaimg.cn/large/006uZZy8jw1faic259ohaj30ci08c74r.jpg"))
        list.add(Data("3", DataSource.Net, "http://ww4.sinaimg.cn/large/006uZZy8jw1faic2b16zuj30ci08cwf4.jpg"))
        list.add(Data("4", DataSource.Net, "http://ww4.sinaimg.cn/large/006uZZy8jw1faic2e7vsaj30ci08cglz.jpg"))

        val adapter = MyAdapter(this, list)
        easyBanner.setAdapter(adapter)
            .setAutoPlay(true)
            .start()

        adapter.setOnBannerClickListener(this)

        btn_stop.setOnClickListener {
            easyBanner.stop()
        }

        btn_pause.setOnClickListener {
            easyBanner.pause()
        }

        btn_start.setOnClickListener {
            easyBanner.start()
        }
    }

    override fun onResume() {
        super.onResume()

        easyBanner.start()
    }

    override fun onPause() {
        super.onPause()

        easyBanner.pause()
    }
}

class MyAdapter(context: Context, data: List<Data>) : BaseAdapter<Data>(context, data) {
    override fun onCreateView(context: Context, parent: ViewGroup, position: Int, viewType: Int): View {
        return LayoutInflater.from(context).inflate(R.layout.item_image_banner, parent, false)
    }

    override fun onDisplay(holder: View, position: Int, model: Data) {
        Glide.with(holder.context)
            .load(model.url)
            .into(holder.findViewById<ImageView>(R.id.imageView))

        holder.findViewById<TextView>(R.id.txtInfo).text = model.txt
    }
}

//class MyBannerHolder(view: View) : ViewHolder(view) {
//    val img = view.findViewById<ImageView>(R.id.imageView)!!
//    val txtInfo = view.findViewById<TextView>(R.id.txtInfo)!!
//}
