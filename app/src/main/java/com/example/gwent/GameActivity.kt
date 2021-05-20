package com.example.gwent

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gwent.recycler_view.ImageAndTextRecycler
import kotlinx.android.synthetic.main.game_activity.*
import java.io.IOException

data class CardsCharacters( var power: Int, var cardAbilityType: String, var legend: Boolean)

class GameActivity : AppCompatActivity() {

    private val imgList: MutableList<String> = mutableListOf()
    private lateinit var imgRandom: String

    private lateinit var imgPlace: MutableList<ImageView>
    private lateinit var imgImImageView: String

    private var cardsList: MutableList<CardsCharacters> = mutableListOf()

    private var oneCardPower: Int = 0
    private var cardIsLegend: Boolean = false
    private lateinit var list: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)

        imgPlace = mutableListOf(imgCard1,imgCard2,imgCard3,imgCard4,imgCard5,imgCard6,imgCard7)
        //list = intent.getStringArrayListExtra(LIST) as ArrayList<String>
/*
        for (place in list.indices){
            Glide.with(this).load(Uri.parse("file:///android_asset/sev_cards/${list[place]}")).into(imgPlace[place])
        }
*/

        btnDisplayCard.setOnClickListener {
            val imgArray = getImage(this)
            imgArray?.forEach { img -> imgList.add(img) }
            cardsList = mutableListOf()
            txtCards.text = ""
            cutCards()
            val totalPower: Int = checkCard(cardsList)
            powerDisplay(totalPower)
            txtCards.text = "Total Cards Power is $totalPower"

            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = prefs.edit()
            editor.putString("string_id", totalPower.toString())
            editor.apply()
        }

/*
        btnDisplayCard.setOnClickListener {
            txtCards.text = ""
            cardsList = mutableListOf()
            cutSevenCards()
            val totalPower: Int = checkCard(cardsList)
            powerDisplay(totalPower)
            txtCards.text = "Total Cards Power is $totalPower"
        }
*/
        btnChooseCard.setOnClickListener{
            startActivity()
        }
    }

    @Throws(IOException::class)
    private fun getImage(context: Context): Array<String>? {
        val assetManager: AssetManager = context.assets
        return assetManager.list("sev_cards")
    }

    private fun cutCards(){
        for (place in imgPlace.indices) {
            cardIsLegend = false
            imgRandom = imgList.random()

            Glide.with(this).load(
                Uri.parse("file:///android_asset/sev_cards/$imgRandom")
            ).into(imgPlace[place])

            oneCardPower = imgRandom.substringAfter('!').substringBefore('.')
                .toInt()

            /**
             * Отслеживание способности карты и подсчёт легендарных карт, на которые
             * не действуют эффекты
             * */
            if (imgRandom.contains("-") or imgRandom.contains("+")){
                var cutTempCard = " "
                if (imgRandom.contains("-")) {
                    cutTempCard = imgRandom.substringBefore('-')
                }
                if (imgRandom.contains("+")) {
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imgList.removeIf{it.contains(imgRandom)}
            }
        }
    }

    private fun cutSevenCards(){
        for (img in list) {
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
            if ((rog) and (!ability.legend) and (ability.cardAbilityType!="r")){
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
        val intent = Intent(this, ImageAndTextRecycler::class.java)
        startActivity(intent)
        this.finish()
    }
}