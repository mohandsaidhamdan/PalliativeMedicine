package com.iug.palliativemedicine.auth

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.iug.palliativemedicine.R
import com.iug.palliativemedicine.databinding.ActivityLoginBinding
import com.iug.palliativemedicine.databinding.ActivitySignupBinding

class signup : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    lateinit var firebaseAuth: FirebaseAuth
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val typeAcount = arrayOf("patient", "doctor")
        val adpter: ArrayAdapter<String> = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            typeAcount
        )

        binding.typeAcount.setAdapter(adpter)

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passET.text.toString()
            val confirmpass = binding.confirmPassET.text.toString()
            val name = binding.NameEt.text.toString()
            val phone = binding.phoneEt.text.toString()
            val title = binding.AddressEt.text.toString()
            val typeAcount = binding.typeAcount.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty() && confirmpass.isNotEmpty() && title.isNotEmpty() && typeAcount.isNotEmpty() && name.isNotEmpty() && phone.isNotEmpty()) {
                try {
                    if (password != confirmpass) {
                        Toast.makeText(this, "password does not match", Toast.LENGTH_SHORT).show()
                    } else if (password.length < 6) {
                        Toast.makeText(
                            this,
                            " password must be at least 6 characters long",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (!(isValidEmail(email))) {
                        Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
                    } else {
                        signUp(email, password)
                        signUpDB(name, password, phone, email , title , typeAcount)
                    }

                } catch (e: Exception) {
                    Toast.makeText(this, "try Aging", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUp(email: String, password: String) {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Signup successful
                    Toast.makeText(this, "SignUp successful.", Toast.LENGTH_SHORT).show()

                    val user = firebaseAuth.currentUser
                    startActivity(Intent(this, login::class.java))
                    finish()
                } else {
                    // Signup failed
                    Log.d("err", task.exception.toString())
                    Toast.makeText(this, "SignUp failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        return email.matches(emailPattern.toRegex())
    }

    private fun signUpDB(name: String, pass: String, phone: String, email: String ,title:String, typeAcount : String) {
        // Create a new user with a first and last name
        val user = hashMapOf(
            "name" to name,
            "phone" to phone,
            "email" to email,
            "typeAcount" to typeAcount,
            "title" to title,
            "pass" to pass
        )

        // Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}

