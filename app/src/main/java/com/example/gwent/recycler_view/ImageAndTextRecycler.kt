package com.example.gwent.recycler_view

import android.content.Context
import android.content.res.AssetManager
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gwent.GameActivity
import com.example.gwent.R
import kotlinx.android.synthetic.main.recycler_activity.*
import java.io.IOException


class ImageAndTextRecycler : AppCompatActivity() {

    private  var imgList: MutableList<String> = mutableListOf()
    private lateinit var adapter: ListAdapter
    private lateinit var tracker: SelectionTracker<Long>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler_activity)

        val imgArray = getImage(this)
        imgArray?.forEach { img -> imgList.add(img) }

        adapter = ListAdapter(imgList)
        cards_recycler_view.layoutManager = LinearLayoutManager(this)
        cards_recycler_view.adapter = adapter

        setupTracker()
        adapter.notifyDataSetChanged()
    }

    private fun setupTracker() {
        tracker = SelectionTracker.Builder(
            "mySelection",
            cards_recycler_view,
            StableIdKeyProvider(cards_recycler_view),
            MyItemDetailsLookup(cards_recycler_view),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    val nItems: Int = tracker.selection.size()
                    val listItems = tracker.selection.toList()
                    if (nItems == 7) {
                        val imgList7: ArrayList<String> = ArrayList()
                        listItems.forEach { num -> imgList7.add(imgList[num.toInt()]) }
                        cardDisplay(imgList7)
                    }
                }
            })
        adapter.tracker = tracker
    }

    private fun cardDisplay(selection: ArrayList<String>) {
        GameActivity.launch(this, selection)
        this.finish()
    }

    @Throws(IOException::class)
    private fun getImage(context: Context): Array<String>? {
        val assetManager: AssetManager = context.assets
        return assetManager.list("sev_cards")
    }
}
