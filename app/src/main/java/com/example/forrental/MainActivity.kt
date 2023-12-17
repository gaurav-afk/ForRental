package com.example.forrental

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.forrental.adapters.PropertyAdapter
import com.example.forrental.databinding.ActivityMainBinding
import com.example.forrental.models.Property
import com.example.forrental.models.User
import com.example.forrental.screens.LandlordPropertiesActivity
import com.example.forrental.screens.LoginActivity
import com.example.forrental.screens.ShortlistActivity
import com.example.forrental.utils.getLoggedInUser
import com.example.forrental.utils.initializeProperties
import com.google.android.material.snackbar.Snackbar


open class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var propertyAdapter: PropertyAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefEditor: SharedPreferences.Editor
    private var propertiesToBeDisplayed: MutableList<Property> = mutableListOf()
    private var allProperties: MutableList<Property> = mutableListOf()
//    private var loggedInUserName: String = ""
    private var loggedInUser: User? = null
    open val tag = "Main"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        allProperties = initializeProperties(this)
        loggedInUser = getLoggedInUser(this)

        Log.i(tag, "property to be displayed ${allProperties}")

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        val justLoggedOut = (this.intent.getStringExtra("REFERER") ?: null) == "MainActivity"
        if(justLoggedOut){
            Snackbar.make(binding.root, "Logout Successful", Snackbar.LENGTH_LONG).show()
        }
        val justClearedData = (this.intent.getStringExtra("REFERER") ?: null) == "Toolbar"
        if(justClearedData){
            Snackbar.make(findViewById(R.id.root_layout), "Data erased!", Snackbar.LENGTH_LONG).show()
        }

        propertiesToBeDisplayed.addAll(allProperties)
        propertyAdapter = PropertyAdapter(propertiesToBeDisplayed, loggedInUser?.username ?: "", false)

        binding.propertiesRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.propertiesRecyclerView.adapter = propertyAdapter

        binding.searchButton.setOnClickListener {
            performSearch(binding.searchEditText.text.toString())
        }

        binding.condoImage.setOnClickListener {
            triggerSearchWithType("Condo")
        }

        binding.houseImage.setOnClickListener {
            triggerSearchWithType("House")
        }

        binding.apartmentImage.setOnClickListener {
            triggerSearchWithType("Apartment")
        }
    }

    // Function to trigger search with type
    private fun triggerSearchWithType(type: String) {
        binding.searchEditText.setText(type)
        performSearch(type)
    }

    override fun onResume() {
        loggedInUser = getLoggedInUser(this)
        allProperties.clear()
        allProperties = initializeProperties(this)
        propertiesToBeDisplayed.clear()
        propertiesToBeDisplayed.addAll(allProperties)
        propertyAdapter.notifyDataSetChanged()

        super.onResume()
    }

    // options menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.options_menu, menu)
        if(loggedInUser == null || loggedInUser?.userType == "Landlord") {
            menu.findItem(R.id.go_to_shortlist).setVisible(false)
        }
        if(loggedInUser?.userType == "Tenant") {
            menu.findItem(R.id.add_property).setVisible(false)
        }
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.go_to_shortlist -> {
                val intent = Intent(this, ShortlistActivity::class.java)
                intent.putExtra("USER", loggedInUser?.username)
                startActivity(intent)
                return true
            }
            R.id.add_property -> {
                if(loggedInUser != null) {
                    val intent = Intent(this, LandlordPropertiesActivity::class.java)
                    intent.putExtra("USER", loggedInUser?.username)
                    startActivity(intent)
                    return true
                }
                else {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("REFERER", "MainActivity")
                    startActivity(intent)
                    return true
                }
            }
            R.id.logout -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER", "")
                intent.putExtra("REFERER", "MainActivity")
                startActivity(intent)
                return true
            }
            // for testing only. Remove this later
            R.id.delete_users -> {
                this.sharedPreferences = getSharedPreferences("USERS", MODE_PRIVATE)
                this.prefEditor = this.sharedPreferences.edit()

                prefEditor.clear()
                prefEditor.apply()

                // logs out the user after all users are deleted
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER", "")
                intent.putExtra("REFERER", "Toolbar")
                startActivity(intent)
//                Snackbar.make(findViewById(R.id.root_layout), "Data erased!", Snackbar.LENGTH_LONG).show()
                return true
            }
            R.id.delete_properties -> {
                this.sharedPreferences = getSharedPreferences("PROPERTIES", MODE_PRIVATE)
                this.prefEditor = this.sharedPreferences.edit()

                prefEditor.clear()
                prefEditor.apply()
                Snackbar.make(findViewById(R.id.root_layout), "property erased!", Snackbar.LENGTH_LONG).show()
                return true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun performSearch(query: String) {
        val filteredProperties = if(query != "") allProperties.filter { property ->
            property.matchesQuery(query)
        } else allProperties
        Log.i(tag, "filtered properties: $filteredProperties")
        propertiesToBeDisplayed.clear()
        propertiesToBeDisplayed.addAll(filteredProperties)
        propertyAdapter.notifyDataSetChanged()
    }

}
