package com.iug.palliativemedicine.ui.home


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.iug.palliativemedicine.R
import com.iug.palliativemedicine.auth.login
import com.iug.palliativemedicine.databinding.FragmentHomeBinding
import com.iug.palliativemedicine.model.topic
import com.squareup.picasso.Picasso


class HomeFragment : Fragment() {
    // Initialize FirebaseStorage instance
    val storage = FirebaseStorage.getInstance()
    private var _binding: FragmentHomeBinding? = null
    private val IMAGE_PICK_REQUEST = 1
    lateinit var imageView: ImageView
    lateinit var imageViewDialog: ImageView
    lateinit var topicEt: EditText
    lateinit var db: FirebaseFirestore
    lateinit var url: String
    var Adapter: FirestoreRecyclerAdapter<topic, topicItem>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        imageView = binding.imageView


        val recyclerView: RecyclerView = binding.recyclerViewTopic


        db = Firebase.firestore

        val query = db.collection("Topic")

        val option =
            FirestoreRecyclerOptions.Builder<topic>().setQuery(query, topic::class.java).build()

        Adapter = object : FirestoreRecyclerAdapter<topic, topicItem>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): topicItem {
                var view = LayoutInflater.from(context)
                    .inflate(R.layout.itemtopic, parent, false)
                return topicItem(view)
            }

            override fun onBindViewHolder(holder: topicItem, position: Int, model: topic) {
                val name = model.name
                val Image = model.uri


                holder.Itemname.text = name.toString()
//                Picasso.get().load(Image).into(holder.ItemImage)
                DwnloadImage(model.uri , holder.ItemImage)

                holder.Itemname.setOnClickListener {

                    val dilalog = AlertDialog.Builder(context)
                    dilalog.setTitle("حذف الحساب")
                    dilalog.setMessage("هل انت متأكد من حذف الحساب ؟؟")
                    dilalog.setPositiveButton("نعم") { dialog, which ->
                        // Do something when the positive button is clicked
                        Toast.makeText(context, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()

                    }
                    dilalog.setNegativeButton("لا") { dialog, which ->
                        // Do something when the negative button is clicked
                        dialog.dismiss()

                    }
                    dilalog.show()

                }


            }

        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = Adapter






        binding.apply {
            fab.setOnClickListener {
                showbottomDilaog()

            }
            // Sign out of the account
            btnSignout.setOnClickListener {
                startActivity(Intent(context, login::class.java))
                requireActivity().finish()
            }
        }



        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun uploadImage(imageUri: Uri) {
        val storageRef = storage.reference
        url = "images/${imageUri.lastPathSegment}"
        val imageRef = storageRef.child(url)
        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val downloadUrl = task.result?.storage?.downloadUrl
            } else {
                // Image upload failed
                // Handle the failure
                Toast.makeText(context, "failed upload image", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun createTopic(uri: String, name: String) {
        // Create a new user with a first and last name
        val Topic = hashMapOf(
            "uri" to uri,
            "name" to name
        )
        //  DwnloadImage(uri)

        val db = Firebase.firestore
        // Add a new document with a generated ID
        db.collection("Topic")
            .add(Topic)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    "ContentValues.TAG",
                    "DocumentSnapshot added with ID: ${documentReference.id}"
                )
            }
            .addOnFailureListener { e ->
                Log.w("ContentValues.TAG", "Error adding document", e)
            }
    }

    private fun showbottomDilaog() {
        val dialog = activity?.let { Dialog(it) }
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(com.iug.palliativemedicine.R.layout.bootomdialog)
        imageViewDialog = dialog.findViewById(com.iug.palliativemedicine.R.id.imageView2)
        val btnadd: Button = dialog.findViewById(com.iug.palliativemedicine.R.id.btnadd)
        topicEt = dialog.findViewById(com.iug.palliativemedicine.R.id.topicEt)
        var check = false
        imageViewDialog.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_REQUEST)
            check = true

        }
        btnadd.setOnClickListener {
            if (check) {
                if (topicEt.text.toString().isNotEmpty()) {
                    createTopic(url, topicEt.text.toString())
                    dialog.dismiss()
                    Toast.makeText(
                        context,
                        "New topic added successfully",
                        Toast.LENGTH_LONG
                    ).show()
                } else
                    Toast.makeText(context, "Enter Name Topic", Toast.LENGTH_SHORT).show()

            } else
                Toast.makeText(context, "Add a photo, please", Toast.LENGTH_SHORT).show()


        }

        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations =
            com.iug.palliativemedicine.R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
    }

    private fun DwnloadImage(uri: String  , imageView2 : ImageView) {
        val storageRef =
            storage.reference.child(uri) // Replace "images/image.jpg" with your actual image path in Firebase Storage

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()

            Picasso.get().load(imageUrl).into(imageView2)
            // Use the imageUrl as needed (e.g., display the image, store it in a database, etc.)
        }.addOnFailureListener { exception ->
            // Handle any errors that occurred while retrieving the download URL
        }
    }

    class topicItem(view: View) : RecyclerView.ViewHolder(view) {
        val Itemname = itemView.findViewById<TextView>(R.id.itemName)
        val ItemImage = itemView.findViewById<ImageView>(R.id.itemImage)

    }

    override fun onStart() {
        super.onStart()
        Adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        Adapter!!.stopListening()
    }
    fun getidDelete(nameUser : String , numberiduser : String){
        db.collection("persone")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    val name = document.get("name")
                    val numberid =  document.get("numberId")
                    if (name== nameUser && numberid == numberiduser){
                        val id = document.id
                        delete(id)
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    fun delete(id : String){
        db.collection("persone").document(id).delete()

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            imageViewDialog.setImageURI(selectedImageUri)
            // Perform the image upload
            selectedImageUri?.let { uploadImage(it) }
        }
    }

//    implementation 'com.firebaseui:firebase-ui-firestore:8.0.1'

}