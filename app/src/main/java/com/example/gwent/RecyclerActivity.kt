package com.example.gwent

import android.content.Context
import android.content.res.AssetManager
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gwent.chat_test.MainActivity
import com.example.gwent.recycler_view.ListAdapter
import com.example.gwent.recycler_view.MyItemDetailsLookup
import kotlinx.android.synthetic.main.recycler_activity.*
import java.io.IOException


class RecyclerActivity : AppCompatActivity() {

    private  var imgList: MutableList<String> = mutableListOf()
    private lateinit var adapter: ListAdapter
    private lateinit var tracker: SelectionTracker<Long>
    private lateinit var mActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler_activity)

        mActivity = MainActivity()
        dialogView()

        txtDescription.text = "Выбранное количество карт: 0"
        val imgArray = getImage(this)
        imgArray?.forEach { img -> imgList.add(img) }

        adapter = ListAdapter(imgList)
        cards_recycler_view.layoutManager = LinearLayoutManager(this)
        cards_recycler_view.adapter = adapter

        setupTracker()
        adapter.notifyDataSetChanged()
    }

    private fun dialogView() {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
        builder.setTitle(getString(R.string.description))
        builder.setPositiveButton(android.R.string.yes) { _, _ -> }
        builder.show()
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
                    txtDescription.text = "Выбранное количество карт: $nItems"
                    val listItems = tracker.selection.toList()
                    if (nItems == 22) {
                        val imgList22: ArrayList<String> = ArrayList()
                        listItems.forEach { num -> imgList22.add(imgList[num.toInt()]) }
                        cardDisplay(imgList22)
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
