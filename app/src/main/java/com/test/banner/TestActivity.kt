package com.test.banner

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.seven.easybanner.adapter.BaseAdapter
import com.seven.easybanner.model.Data
import com.seven.easybanner.model.DataSource
import kotlinx.android.synthetic.main.activity_test.*


class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val list = mutableListOf<Data>()
        list.add(Data("0", DataSource.Net, "http://ww4.sinaimg.cn/large/006uZZy8jw1faic1xjab4j30ci08cjrv.jpg"))
        list.add(Data("1", DataSource.Net, "http://ww4.sinaimg.cn/large/006uZZy8jw1faic21363tj30ci08ct96.jpg"))
        list.add(Data("2", DataSource.Net, "http://ww4.sinaimg.cn/large/006uZZy8jw1faic259ohaj30ci08c74r.jpg"))
        list.add(Data("3", DataSource.Net, "http://ww4.sinaimg.cn/large/006uZZy8jw1faic2b16zuj30ci08cwf4.jpg"))
        list.add(Data("4", DataSource.Net, "http://ww4.sinaimg.cn/large/006uZZy8jw1faic2e7vsaj30ci08cglz.jpg"))

        easyBanner.setAdapter(MyAdapter(this, list))
    }
}

class MyAdapter(context: Context, data: List<Data>) : BaseAdapter(context, data) {
    override fun onCreatView(context: Context, parent: ViewGroup, position: Int, viewType: Int): View {
        return LayoutInflater.from(context).inflate(R.layout.item_image_banner, parent, false)
    }

    override fun onDisplay(view: View, position: Int, model: Data) {
        Glide.with(view.context)
            .load(model.url)
            .into(view.findViewById(R.id.imageView))

        view.findViewById<TextView>(R.id.txtInfo).text = model.txt
    }
}
