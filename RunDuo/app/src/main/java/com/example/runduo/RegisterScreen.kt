package com.example.runduo

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.widget.EditText
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.google.firebase.database.FirebaseDatabase
import android.os.Bundle
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.MediaStore
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class RegisterScreen : AppCompatActivity() {
    private lateinit var NewUserButton: Button
    private lateinit var ExistingUserButton: Button
    private lateinit var userImages: ImageView
    var imageChoosen: Uri? = null
    private var SignupName: EditText? = null
    private var SingupEmail: EditText? = null
    private var SignupPw: EditText? = null
    private var ConfirmPw: EditText? = null
    private lateinit var sharedPref: SharedPreferences
    var Valid: AwesomeValidation? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_screen)
        SignupName = findViewById(R.id.Naming)
        SingupEmail = findViewById(R.id.Email)
        SignupPw = findViewById(R.id.Pw)
        ConfirmPw = findViewById(R.id.confirmPw)
        NewUserButton = findViewById(R.id.registerB)
        ExistingUserButton = findViewById(R.id.loginB)
        userImages = findViewById(R.id.userImages)
        sharedPref = getSharedPreferences("userData", Context.MODE_PRIVATE)
        Valid = AwesomeValidation(ValidationStyle.BASIC)
        Valid!!.addValidation(this, R.id.Naming, RegexTemplate.NOT_EMPTY, R.string.invalid_name)
        Valid!!.addValidation(this, R.id.Email, Patterns.EMAIL_ADDRESS, R.string.invalid_email)
        Valid!!.addValidation(this, R.id.Pw, ".{6,}", R.string.invalid_pw)
        Valid!!.addValidation(this, R.id.confirmPw, R.id.Pw, R.string.invalid_confirmPW)

        userImages.setOnClickListener(View.OnClickListener {
            val chooseImage = Intent(Intent.ACTION_PICK)
            chooseImage.type = "image/*"
            startActivityForResult(chooseImage, 0)
        })

        ExistingUserButton.setOnClickListener(View.OnClickListener {
            val ScreenNext = Intent(this@RegisterScreen, LoginScreen::class.java)
            startActivity(ScreenNext)
        })

        NewUserButton.setOnClickListener(View.OnClickListener {
            if (Valid!!.validate() && imageChoosen!=null) {
                UserCreation()
            } else {
                Toast.makeText(this@RegisterScreen, "Register Failed , Input Profile Image From Phone's Picture", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            imageChoosen = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageChoosen)
            userImages.setImageBitmap(bitmap)
        }
    }

    private fun imageUploaded()
    {
        val imageName = UUID.randomUUID().toString()
        val imageRef = FirebaseStorage.getInstance().getReference("/image/$imageName")
        imageRef.putFile(imageChoosen!!).addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener {
                val editing = sharedPref.edit()
                editing.putString("myImage", it.toString()).apply()
                RegisterToDatabase(it.toString())
            }
        }
    }

    private fun UserCreation() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            SingupEmail!!.text.toString(),
            SignupPw!!.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@RegisterScreen, "Validated/Success", Toast.LENGTH_SHORT)
                        .show()
                    imageUploaded()
                }
            }
    }

    private fun RegisterToDatabase(imageURL: String)
    {
        val userID = FirebaseAuth.getInstance().uid ?: ""
        val savedRef =  FirebaseDatabase.getInstance("https://runduo-82d65-default-rtdb.firebaseio.com/").getReference("/Users/$userID")
        val savedUser = SavedUsers(userID,SignupName!!.text.toString(),imageURL)
        val editing = sharedPref.edit()
        editing.putString("myName", SignupName!!.text.toString()).apply()
        savedRef.setValue(savedUser).addOnSuccessListener {
            val nextScreen = Intent(this, MainActivity::class.java)
            nextScreen.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(nextScreen)
        }
    }

}