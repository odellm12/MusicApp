package com.example.soundhero

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_title.*

class TitleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        mainButt.setOnClickListener{
            var playerText = player.text.toString()
            val intent = Intent(this@TitleActivity, MainActivity::class.java)
            intent.putExtra("player", playerText)
            startActivity(intent)
        }
    }

}
