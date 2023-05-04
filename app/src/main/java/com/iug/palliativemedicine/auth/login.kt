package com.iug.palliativemedicine.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.iug.palliativemedicine.Home
import com.iug.palliativemedicine.MainActivity
import com.iug.palliativemedicine.R
import com.iug.palliativemedicine.databinding.ActivityLoginBinding

class login : AppCompatActivity() {
private lateinit var binding: ActivityLoginBinding
private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val password = binding.passET.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()){
                login(email,password)

            }else{
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }

        }


        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this,signup::class.java))
        }

    }

    private fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful
                    val user = firebaseAuth.currentUser
                    val sheard = getSharedPreferences("user" , MODE_PRIVATE)
                    val edit = sheard.edit()
                    edit.putString("email" , email)
                    edit.putBoolean("che" , true)
                    edit.putString("password" , password)
                    edit.apply()
                    startActivity(Intent(this , Home::class.java))
                } else {
                    // Login failed
                    Toast.makeText(this, "Incorrect password or email.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}