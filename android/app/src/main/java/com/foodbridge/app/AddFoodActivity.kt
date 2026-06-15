package com.foodbridge.app

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class AddFoodActivity : AppCompatActivity() {

    private var selectedImageBase64: String? = null
    private lateinit var ivFoodPhoto: ImageView

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val uri: Uri? = result.data?.data
            uri?.let {
                ivFoodPhoto.setImageURI(it)
                encodeImageToBase64(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val etFoodName = findViewById<EditText>(R.id.etFoodName)
        val etFoodPrice = findViewById<EditText>(R.id.etFoodPrice)
        val btnSaveFood = findViewById<Button>(R.id.btnSaveFood)
        val btnSelectPhoto = findViewById<Button>(R.id.btnSelectPhoto)
        ivFoodPhoto = findViewById(R.id.ivFoodPhoto)

        btnSelectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        btnSaveFood.setOnClickListener {
            val name = etFoodName.text.toString().trim()
            val price = etFoodPrice.text.toString().trim()

            if (name.isNotEmpty() && price.isNotEmpty()) {
                btnSaveFood.isEnabled = false
                btnSaveFood.text = "Saving..."

                val session = SessionManager(this)
                val hotelId = session.getUserDetails()["id"]

                val json = JSONObject()
                json.put("hotel_id", hotelId)
                json.put("item_name", name)
                json.put("price", price)
                if (selectedImageBase64 != null) {
                    json.put("image_base64", selectedImageBase64)
                }

                lifecycleScope.launch {
                    try {
                        val response = ApiClient.post("add_food.php", json)
                        if (response.has("status") && response.getString("status") == "success") {
                            Toast.makeText(this@AddFoodActivity, "Food added successfully!", Toast.LENGTH_SHORT).show()
                            finish() // Go back to Home
                        } else {
                            val msg = if (response.has("message")) response.getString("message") else "Unknown error"
                            Toast.makeText(this@AddFoodActivity, msg, Toast.LENGTH_SHORT).show()
                            btnSaveFood.isEnabled = true
                            btnSaveFood.text = "Save Food Item"
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@AddFoodActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        btnSaveFood.isEnabled = true
                        btnSaveFood.text = "Save Food Item"
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun encodeImageToBase64(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            
            // Resize image to reduce payload size (max 800x800)
            val maxWidth = 800
            val maxHeight = 800
            val scale = Math.min(maxWidth.toFloat() / bitmap.width, maxHeight.toFloat() / bitmap.height)
            
            val scaledBitmap = if (scale < 1) {
                Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), true)
            } else {
                bitmap
            }

            val baos = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
            val imageBytes = baos.toByteArray()
            selectedImageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show()
        }
    }
}
