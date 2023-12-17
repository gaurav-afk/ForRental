package com.example.forrental.screens

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.forrental.R
import com.example.forrental.databinding.ActivityPropertyDetailBinding
import com.example.forrental.models.Property

class PropertyDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPropertyDetailBinding
    private lateinit var property: Property
    private val tag = "Property detail"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPropertyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(this.binding.menuToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        binding.returnBtn.setOnClickListener {
            finish()
        }

        val hasProperty: Boolean = this@PropertyDetailActivity.intent.extras?.containsKey("PROPERTY") != null
        if (hasProperty) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                property = intent.getSerializableExtra("PROPERTY", Property::class.java)!!
            }else{
                property = intent.getSerializableExtra("PROPERTY") as Property
            }

            Log.i(tag, "has property $property")

            if (property != null) {
                binding.propertyAddress.setText(property.address)
                binding.propertyDescription.setText(property.description)

                binding.numberOfBathrooms.setText(property.numOfBathrooms.toString())
                binding.numberOfBedrooms.setText(property.numOfBedrooms.toString())
                binding.numberOfKitchens.setText(property.numOfKitchens.toString())

//                Glide.with(binding.root.context).load(property.imageUrl).into(binding.propertyImage)  // for online images
                val imageName = property.imageUrl ?: "default_image"
                val res = resources.getIdentifier(imageName, "drawable", this.packageName)
                this.binding.propertyImage.setImageResource(res)
            }

            // Set the availability tag
            with(binding.propertyAvailabilityTag) {
                text = if (property.availableForRent) {
                    getString(R.string.available) // "Available" string resource
                } else {
                    getString(R.string.not_available) // "Not Available" string resource
                }

                // Set background color depending on availability
                setBackgroundResource(if (property.availableForRent) {
                    R.drawable.available_tag_background // Green background drawable
                } else {
                    R.drawable.not_available_tag_background // Red background drawable
                })

                // Ensure the tag is visible
                visibility = View.VISIBLE
            }

        }
    }
}