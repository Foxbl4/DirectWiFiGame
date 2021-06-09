package com.example.gwent

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gwent.chat_test.MainActivity
import kotlinx.android.synthetic.main.game_activity.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

class GameActivity : AppCompatActivity() {

    private lateinit var imgRandom: String

    private lateinit var mActivity: MainActivity
    private lateinit var imgPlace: MutableList<ImageView>
    private lateinit var imgImImageView: String
    var totalPower: Int = 0
    private var cardsList: MutableList<CardsCharacters> = mutableListOf()

    private var oneCardPower: Int = 0
    private var cardIsLegend: Boolean = false
    private lateinit var list: ArrayList<String>
    private lateinit var list10: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)

        mActivity = MainActivity()
        imgPlace = mutableListOf(imgCard1, imgCard2, imgCard3, imgCard4, imgCard5, imgCard6,
            imgCard7, imgCard8, imgCard9, imgCard10)

        list = intent.getStringArrayListExtra(LIST) as ArrayList<String>
        list10 = mutableListOf()
        for (place in imgPlace.indices){
            imgRandom = list.random()
            list10.add(imgRandom)
            Glide.with(this).load(Uri.
                parse("file:///android_asset/sev_cards/$imgRandom")).into(imgPlace[place])
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                list.removeIf{it.contains(imgRandom)}
            }
        }

        btnScoringCard.setOnClickListener {
            cardsList = mutableListOf()
            cutTenCards()
            totalPower = checkCard(cardsList)
            dialogView2(totalPower)
            PowerActivity.uTotalPower = totalPower
        }

        btnChooseCard.setOnClickListener{
            startActivity()
        }
    }

    private fun dialogView2(totalPower: Int) {
        val builder = AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
        builder.setTitle("Total Cards Power is $totalPower")
        builder.setPositiveButton(android.R.string.yes) { _, _ -> }
        builder.show()
    }

    private fun cutTenCards(){
        for (img in list10) {
            cardIsLegend = false
            imgImImageView = img
            oneCardPower = imgImImageView.substringAfter('!').substringBefore('.')
                .toInt()
            /**
             * Отслеживание способности карты и подсчёт легендарных карт, на которые
             * не действуют эффекты
             * */
            if (imgImImageView.contains("-") or imgImImageView.contains("+")){
                var cutTempCard = " "
                if (imgImImageView.contains("-")) {
                    cutTempCard = imgImImageView.substringBefore('-')
                }
                if (imgImImageView.contains("+")) {
                    cutTempCard = cutTempCard.substringAfter('+')
                    cardIsLegend = true
                    if (cutTempCard == " "){
                        cutTempCard = "o"
                    }
                }
                cardsList.add(CardsCharacters( oneCardPower,cutTempCard, cardIsLegend))
            }
            else {
                cardsList.add(CardsCharacters( oneCardPower,"o", cardIsLegend))
            }
        }
    }

    private fun checkCard(abilityType: MutableList<CardsCharacters>): Int {

        val handsConnect: MutableList<CardsCharacters> = mutableListOf()
        var plusPower = 0
        var rog = false

        /**
         * Разделение на тип особенности карты
         * */
        for (ability in abilityType){
            when (ability.cardAbilityType){
                "s"-> { handsConnect.add(ability) }
                "p" ->{ plusPower++ }
                "r" ->{ rog = true }
                "e" ->{ ability.power *= -1 }
            }
        }

        /**
         * Подсчёт очков от способности "Прочная связь"
         * */
        val mapHandsConnect : Map<CardsCharacters,Int> = handsConnect.groupingBy { it }.eachCount().filter { it.value > 1 }
        var onePower: Int
        if (mapHandsConnect.isNotEmpty()) {
            for ((key, value) in mapHandsConnect) {
                onePower = key.power
                when (value) {
                    2 -> { abilityType.filter { (it.power == key.power) and (it.cardAbilityType == "s") }.forEach { it.power += onePower } }
                    3 -> { abilityType.filter { (it.power == key.power) and (it.cardAbilityType == "s") }.forEach { it.power += onePower * 2 } }
                    4 -> { abilityType.filter { (it.power == key.power) and (it.cardAbilityType == "s") }.forEach { it.power += onePower * 3 } }
                }
            }
        }

        /**
         * Подсчёт очков от способности "Прилив сил"
         * */
        for (ability in abilityType) {
            if ((!ability.legend) and (ability.cardAbilityType != "e")) {
                if (ability.cardAbilityType == "p") {
                    ability.power += plusPower - 1
                }
                else {
                    ability.power += plusPower
                }
            }
        }

        /**
         * Подсчёт очков от способности командирский рог
         * */
        for (ability in abilityType){
            if ((rog) and (!ability.legend) and (ability.cardAbilityType!="r") and (ability.cardAbilityType != "e")){
                ability.power *= 2
            }
        }

        Log.d("abilityType", "$abilityType")

        /**
         * Подсчёт общего количества очков
         * */
        var newTotalPower = 0
        abilityType.forEach { power -> newTotalPower+=power.power }
        return newTotalPower
    }

    private fun powerDisplay(power: Int) {
        MainActivity.launch2(this, power)
        this.finish()
    }

    companion object {
        private const val LIST = "list"
        fun launch(context: Context, list: ArrayList<String>) {
            val intent = Intent(context, GameActivity::class.java)
            intent.putStringArrayListExtra(LIST, list)
            context.startActivity(intent)
        }
    }

    private fun startActivity(){
        val intent = Intent(this, RecyclerActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}