package com.example.gwent.recycler_view

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gwent.R

class ListAdapter(textAndImagesList: MutableList<String>) : RecyclerView.Adapter<ListAdapter.CardsViewHolder>() {

    private val mTextAndImagesList = textAndImagesList
    private val txtArray = listOf(
        "Гюнтер о'Дим",
        "Гюнтер о'Дим: Тьма",
        "Гюнтер о'Дим: Тьма",
        "Гюнтер о'Дим: Тьма",
        "Сигизмунд Дийкстра",
        "Принц Стеннис",
        "Талер",
        "Виллентретенмерт",
        "Таинственный эльф",
        "Йеннифэр из Вергенберга",
        "Трисс Меригольд",
        "Цирилла",
        "Геральт из Ривии",
        "Вернон Роше",
        "Эстерад Тиссен",
        "Ян Наталис",
        "Филиппа Эйльхарт",
        "Лекарь Бурой Хоругви",
        "Золтан Хивай",
        "Эмиель Регис",
        "Весемир",
        "Ольгерд фон Эверек",
        "Каэдвенский осадный мастер",
        "Каэдвенский осадный мастер",
        "Каэдвенский осадный мастер",
        "Лютик",
        "Гребаная петруха",
        "Боец Синих Полосок",
        "Боец Синих Полосок",
        "Боец Синих Полосок",
        "Боец Синих Полосок",
        "Рубайлы из Кринфрида",
        "Рубайлы из Кринфрида",
        "Рубайлы из Кринфрида",
        "Гребаная петруха",
        "Гребаная петруха",
        "Катапульта",
        "Катапульта",
        "Сабрина Глевиссик",
        "Бьянка",
        "Зигфрид из Денесле",
        "Кейра Мец",
        "Шеала де Тансервилль",
        "Реданский пехотинец",
        "Детмольд",
        "Требушет",
        "Требушет",
        "Баллиста",
        "Осадная башня",
        "Реданский пехотинец",
        "Ярпен Зигрин",
        "Шелдон Скаггс",
    )
    lateinit var tracker: SelectionTracker<Long>

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder =
        CardsViewHolder(LayoutInflater.from(parent.context).
        inflate(R.layout.recycler_view_item, parent, false))

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        val listImage: String = mTextAndImagesList[position]
        val listTxt: String = txtArray[position]
        tracker.let {
            holder.bind(listTxt, listImage, it.isSelected(position.toLong()))
        }
    }

    override fun getItemCount(): Int = mTextAndImagesList.size

    override fun getItemId(position: Int): Long = position.toLong()

    inner class CardsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mView = view
        private var mTextView: TextView? = itemView.findViewById(R.id.list_text_in_RV)
        private var mImageView: ImageView = itemView.findViewById(R.id.list_image_in_RV)

        fun bind(mTxt: String, mImage: String, isActivated: Boolean = false) {
            mTextView?.text = mTxt
            Glide.with(mView).load(Uri.parse("file:///android_asset/sev_cards/$mImage")).into(mImageView)
            itemView.isActivated = isActivated
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long = itemId
            }
    }
}
