package com.example.forrental.screens

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.forrental.databinding.ActivityAddPropertyBinding
import com.example.forrental.models.Owner
import com.example.forrental.models.Property
import com.example.forrental.utils.checkDuplicatedProperty
import com.example.forrental.utils.saveDataToSharedPref
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class AddPropertyActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddPropertyBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var prefEditor: SharedPreferences.Editor
    private var loggedInUserName: String = ""
    val tag = "Add Property"

    var savedProperties: MutableList<Property> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        this.binding = ActivityAddPropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loggedInUserName = this.intent.getStringExtra("USER") ?: ""
        Log.i(tag, "In AddProperty, user: ${loggedInUserName}")

        val selectedProperty = intent.getSerializableExtra("PROPERTY_DATA") as Property?
        if (selectedProperty != null) {
            binding.address.setText(selectedProperty.address)
            binding.city.setText(selectedProperty.city)
            binding.postalCode.setText(selectedProperty.postalCode)
            binding.type.setText(selectedProperty.type)
            binding.ownerName.setText(selectedProperty.owner.name)
            binding.ownerEmail.setText(selectedProperty.owner.email)
            binding.ownerPhone.setText(selectedProperty.owner.phone)
            binding.description.setText(selectedProperty.description)
            binding.bedrooms.setText(selectedProperty.numOfBedrooms.toString())
            binding.kitchens.setText(selectedProperty.numOfKitchens.toString())
            binding.bathrooms.setText(selectedProperty.numOfBathrooms.toString())
            binding.availableForRent.isChecked = selectedProperty.availableForRent
        }

        this.sharedPreferences = getSharedPreferences("PROPERTIES", MODE_PRIVATE)
        this.prefEditor = this.sharedPreferences.edit()

        var resultsFromSP = sharedPreferences.getString(loggedInUserName, "")
        if (resultsFromSP != "") {
            val gson = Gson()
            val typeToken = object : TypeToken<List<Property>>() {}.type
            val tempPropertyList = gson.fromJson<List<Property>>(resultsFromSP, typeToken)

            savedProperties = tempPropertyList.toMutableList()
        }

        this.binding.saveBtn.setOnClickListener {
            this.saveData()
        }

        this.binding.cancelBtn.setOnClickListener {
            finish()
        }
    }

    private fun saveData() {
        var address: String? = this.binding.address.text.toString()
        var type: String? = this.binding.type.text.toString()
        var city: String? = this.binding.city.text.toString()
        var postalCode: String? = this.binding.postalCode.text.toString()
        var ownerName: String? = this.binding.ownerName.text.toString()
        var ownerEmail: String? = this.binding.ownerEmail.text.toString()
        var ownerPhone: String? = this.binding.ownerPhone.text.toString()
        var desc: String? = this.binding.description.text.toString()
        var bedrooms: Int? = if(this.binding.bedrooms.text.toString() != "") this.binding.bedrooms.text.toString().toInt() else null
        var kitchens: Int? = if(this.binding.kitchens.text.toString() != "") this.binding.kitchens.text.toString().toInt() else null
        var bathrooms: Int? = if(this.binding.bathrooms.text.toString() != "") this.binding.bathrooms.text.toString().toInt() else null
        var availableForRent: Boolean? = this.binding.availableForRent.isChecked

        // error check
        var hasEmptyField = false
        if (address.isNullOrEmpty()) {
            this.binding.address.setError("Address cannot be empty")
            hasEmptyField = true
        }
        if (type.isNullOrEmpty()) {
            this.binding.type.setError("Type cannot be empty")
            hasEmptyField = true
        }
        if (city.isNullOrEmpty()) {
            this.binding.city.setError("City cannot be empty")
            hasEmptyField = true
        }
        if (postalCode.isNullOrEmpty()) {
            this.binding.postalCode.setError("Postal code cannot be empty")
            hasEmptyField = true
        }
        if (ownerName.isNullOrEmpty()) {
            this.binding.ownerName.setError("Owner name cannot be empty")
            hasEmptyField = true
        }
        if (ownerPhone.isNullOrEmpty()) {
            this.binding.ownerPhone.setError("Owner phone cannot be empty")
            hasEmptyField = true
        }
        if (ownerEmail.isNullOrEmpty()) {
            this.binding.ownerEmail.setError("Owner email cannot be empty")
            hasEmptyField = true
        }
        if (desc.isNullOrEmpty()) {
            this.binding.description.setError("Description cannot be empty")
            hasEmptyField = true
        }
        if (bedrooms == null) {
            this.binding.bedrooms.setError("Bedrooms cannot be empty")
            hasEmptyField = true
        }
        if (kitchens == null) {
            this.binding.kitchens.setError("Kitchens cannot be empty")
            hasEmptyField = true
        }
        if (bathrooms == null) {
            this.binding.bathrooms.setError("Bathrooms cannot be empty")
            hasEmptyField = true
        }
        if(hasEmptyField){
            Snackbar.make(binding.addPropertyParentLayout, "All fields are required.", Snackbar.LENGTH_LONG).show()
            return
        }

        val owner = Owner(ownerName!!, ownerEmail!!, ownerPhone!!)

        val selectedProperty = intent.getSerializableExtra("PROPERTY_DATA") as Property?
        val index = intent.getIntExtra("INDEX", -1)
        Log.i(tag, "selected property ${index}: ${selectedProperty}")

        // update property
        if(selectedProperty != null){
            val propertyToEdit = Property(
                address!!,
                city!!,
                postalCode!!,
                type!!,
                owner,
                desc!!,
                bedrooms!!,
                kitchens!!,
                bathrooms!!,
                availableForRent!!
            )

//            if(checkDuplicatedProperty(propertyToEdit, this)){
//                Snackbar.make(binding.addPropertyParentLayout, "Property already exist!!", Snackbar.LENGTH_LONG).show()
//                return
//            }
            savedProperties[index] = propertyToEdit
        }
        // create new property
        else {
            var propertyToAdd = Property(address!!, city!!, postalCode!!, type!!, owner, desc!!, bedrooms!!, kitchens!!, bathrooms!!, availableForRent!!)
            if(checkDuplicatedProperty(propertyToAdd, this)){
                Snackbar.make(binding.addPropertyParentLayout, "Property already exist!!", Snackbar.LENGTH_LONG).show()
                return
            }
            savedProperties.add(propertyToAdd)
        }
        saveDataToSharedPref(this, "PROPERTIES", loggedInUserName, savedProperties, true)
        Snackbar.make(binding.addPropertyParentLayout, "Data Saved to SharedPrefs", Snackbar.LENGTH_LONG).show()
        finish()
    }

}