package com.example.forrental.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.forrental.adapters.PropertyAdapter
import com.example.forrental.databinding.ActivityShortlistBinding
import com.example.forrental.models.Property
import com.example.forrental.models.User
import com.example.forrental.utils.getLoggedInUser

class ShortlistActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShortlistBinding
    private var loggedInUser: User? = null
    private val tag = "Shortlist"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShortlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        binding.returnBtn.setOnClickListener {
            finish()
        }

        loggedInUser = getLoggedInUser(this)
        if(loggedInUser != null){
            var shortlistedProperties:MutableList<Property> = mutableListOf()
            if (loggedInUser != null) {
                shortlistedProperties = loggedInUser!!.shortlistedProperties
            }

            var adapter = PropertyAdapter(shortlistedProperties, loggedInUser?.username ?: "", true)
            this.binding.shortlistRv.adapter = adapter
            this.binding.shortlistRv.layoutManager = LinearLayoutManager(this)
            this.binding.shortlistRv.addItemDecoration(
                DividerItemDecoration(
                    this,
                    LinearLayoutManager.VERTICAL
                )
            )
        }

    }

}