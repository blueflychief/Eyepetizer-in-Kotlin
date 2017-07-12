package com.tt.lvruheng.eyepetizer.ui

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.gyf.barlibrary.ImmersionBar
import com.tt.lvruheng.eyepetizer.R
import com.tt.lvruheng.eyepetizer.adapter.DownloadAdapter
import com.tt.lvruheng.eyepetizer.mvp.model.bean.VideoBean
import com.tt.lvruheng.eyepetizer.utils.ObjectSaveUtils
import com.tt.lvruheng.eyepetizer.utils.SPUtils
import kotlinx.android.synthetic.main.activity_watch.*

/**
 * Created by lvruheng on 2017/7/12.
 */
class CacheActivity : AppCompatActivity() {
    var mList = ArrayList<VideoBean>()
    lateinit var mAdapter: DownloadAdapter
    var mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            var list = msg?.data?.getParcelableArrayList<VideoBean>("beans")
            if(list?.size?.compareTo(0) == 0){
                tv_hint.visibility = View.VISIBLE
            }else{
                tv_hint.visibility = View.GONE
                if(mList.size>0){
                    mList.clear()
                }
                list?.let { mList.addAll(it) }
                mAdapter.notifyDataSetChanged()
            }

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch)
        ImmersionBar.with(this).transparentBar().barAlpha(0.3f).fitsSystemWindows(true).init()
        setToolbar()
        DataAsyncTask(mHandler,this).execute()
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = DownloadAdapter(this, mList)
        recyclerView.adapter = mAdapter
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        var bar = supportActionBar
        bar?.title = "我的缓存"
        bar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private class DataAsyncTask(handler: Handler, activity: CacheActivity) : AsyncTask<Void, Void, ArrayList<VideoBean>>() {
        var activity: CacheActivity = activity
        var handler = handler
        override fun doInBackground(vararg params: Void?): ArrayList<VideoBean>? {
            var list = ArrayList<VideoBean>()
            var count: Int = SPUtils.getInstance(activity, "downloads").getInt("count")
            var i = 1
            while (i.compareTo(count) <= 0) {
                var bean : VideoBean = ObjectSaveUtils.getValue(activity, "download$i") as VideoBean
                list.add(bean)
                i++
            }
            return list
        }

        override fun onPostExecute(result: ArrayList<VideoBean>?) {
            super.onPostExecute(result)
            var message = handler.obtainMessage()
            var bundle = Bundle()
            bundle.putParcelableArrayList("beans",result)
            message.data = bundle
            handler.sendMessage(message)
        }

    }

}