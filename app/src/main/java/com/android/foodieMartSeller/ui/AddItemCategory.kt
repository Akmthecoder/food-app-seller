package com.android.foodieMartSeller.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.foodieMartSeller.R
import com.android.foodieMartSeller.data.request.Feed
import com.android.foodieMartSeller.data.request.FeedType
import com.android.foodieMartSeller.dialog.ViewDialog
import com.android.foodieMartSeller.network.firebase.Connection
import com.android.foodieMartSeller.network.firebase.FirebaseMethods
import com.android.foodieMartSeller.network.firebase.RequestCallback
import com.android.foodieMartSeller.utils.CommonUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.chip.Chip
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import kotlinx.android.synthetic.main.activity_item_category.*
import kotlinx.android.synthetic.main.app_header.*


class AddItemCategory : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {
    private lateinit var loadingDialog: ViewDialog
    private var fileUri: Uri? = null
    private var country = arrayOf(
        "Select Category",
        "Beverages", "Pizza", "Veg Food", "Non-Veg Food", "Cake",
        "Snacks",
        "Others"
    )
    private var weightArray =
        arrayOf("Select Weight", "500gm", "1kg", "1.5kg", "2kg", "2.5kg", "3kg", "3.5kg", "4kg")
    private var category = ""
    private var weight_category = ""
    var feedType = ArrayList<FeedType>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_category)
        tvHeader.text = "Add Item"
        loadingDialog = ViewDialog(this)
        onClickListener()
        spinner.onItemSelectedListener = this
        val aa: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, country)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = aa

        spinner_weight.onItemSelectedListener = this
        val aaWeight: ArrayAdapter<*> =
            ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, weightArray)
        aaWeight.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_weight.adapter = aaWeight

    }


    private fun onClickListener() {
        ivBackHeader.setOnClickListener(this)
        fab_add_photo.setOnClickListener(this)
        bt_proceed.setOnClickListener(this)
        tv_add_weight.setOnClickListener(this)
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    fileUri = data?.data
                    tv_cake_add.visibility = View.GONE
                    var requestOptions = RequestOptions()
                    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(20))
                    Glide.with(this)
                        .load(fileUri)
                        .apply(requestOptions)
                        .into(imgProfile)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackHeader -> {
                onBackPressed()
            }
            R.id.fab_add_photo -> {
                ImagePicker.with(this)
                    .compress(1024)
                    .cropSquare()
                    .maxResultSize(1080, 1080)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            }
            R.id.bt_proceed -> {
                addItem()
            }
            R.id.tv_add_weight -> {
                if (checkWeightValidation()) {
                    feedType.forEach {
                        if (it.weight == weight_category) {
                            CommonUtils.showSnackBar(this, "Please add different Weight", root)
                            return
                        }
                    }

                    val chip = Chip(this)
                    chip.text =
                        weight_category + " : ₹" + Integer.parseInt(tiEtPrice.text.toString())
                    chip.setChipBackgroundColorResource(R.color.colorAccent)
                    chip.isCloseIconVisible = true
                    chip.setTextColor(resources.getColor(R.color.white))
                    chip.setTextAppearance(R.style.ChipTextAppearance)
                    chip.isCheckedIconVisible = false
                    chip.isCloseIconVisible = false
                    chip.checkedIcon = null
                    chipGroup.addView(chip)

                    val feedTypeItem = FeedType().apply {
                        this.price = Integer.parseInt(tiEtPrice.text.toString())
                        this.weight = weight_category
                    }
                    feedType.add(feedTypeItem)

                }
            }
        }
    }


    fun checkWeightValidation(): Boolean {
        if (spinner_weight.selectedItem.toString() == "Select Weight") {
            CommonUtils.showSnackBar(this, "Please select weight", root)
            return false
        } else if (tiEtPrice.text.toString() == "") {
            CommonUtils.showSnackBar(this, "Please add price", root)
            return false
        }
        return true
    }

    private fun addItem() {
        if (checkValidation()) {
            loadingDialog.showDialog()
            val name = tiEtCakeName.text.toString()
            val price = Integer.parseInt(tiEtPrice.text.toString())
            val description = tiEtDescription.text.toString()
            val maxItems = Integer.parseInt(tiEtQuantity.text.toString())
            val totalItemStock = Integer.parseInt(tiEtStock.text.toString())
            val category = category
            val feed = Feed().apply {
                this.category = category
                this.name = name
                this.description = description
                this.feed_types = feedType
                this.maxItems = maxItems
                this.total_stock_size = totalItemStock
            }
            val feedJsonStr = Gson().toJson(feed)
            val databaseReference =
                FirebaseDatabase.getInstance().reference.child(Connection.FEED).push()
            databaseReference.setValue(feedJsonStr).addOnCompleteListener {
                if (it.isSuccessful) {
                    databaseReference.key?.let { it1 -> UploadImage(it1) }
                } else {
                    loadingDialog.hideDialog()
                    CommonUtils.showSnackBar(this, "Unable to Add Items.Please try again", root)
                }
            }
        }
    }

    private fun UploadImage(feedId: String) {
        val storageRef = FirebaseStorage.getInstance().getReference("feeds/${feedId}.jpg")
        val uploadTask = storageRef.putFile(fileUri!!)
        uploadTask.addOnSuccessListener {
            uploadTask.continueWithTask {
                if (!it.isSuccessful) {
                    loadingDialog.hideDialog()
                    clearScreen()
                    var errorMsg = ""
                    if (it.exception?.message != null) {
                        errorMsg = it.exception?.message.toString()
                    } else {
                        errorMsg = "Something went wrong.Please try again after sometime"
                    }
                    val mDialog = MaterialDialog.Builder(this)
                        .setMessage(errorMsg)
                        .setCancelable(false)
                        .setPositiveButton(
                            "Okay"
                        ) { dialogInterface, which ->
                            dialogInterface.dismiss()
                        }
                        .build()
                    mDialog.show()
                }
                return@continueWithTask storageRef.downloadUrl
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    val url = it.result
                    updateFeed(feedId, url.toString())
                }
            }
        }
    }

    private fun updateFeed(feedId: String, imageUrl: String) {
        FirebaseMethods.singleValueEventChild(Connection.FEED, feedId, object : RequestCallback {
            override fun onDataChanged(dataSnapshot: DataSnapshot) {
                val feedStr = dataSnapshot.getValue(String::class.java)
                val feed = Gson().fromJson(feedStr, Feed::class.java)
                feed.imageUrl = imageUrl
                feed.id = feedId
                val updatedFeedStr = Gson().toJson(feed)
                FirebaseDatabase.getInstance().reference.child(Connection.FEED).child(feedId)
                    .setValue(updatedFeedStr).addOnCompleteListener {
                        loadingDialog.hideDialog()
                        if (it.isSuccessful) {
                            val mDialog = MaterialDialog.Builder(this@AddItemCategory)
                                .setMessage("Item Added successfully.")
                                .setCancelable(false)
                                .setPositiveButton(
                                    "Go Back"
                                ) { dialogInterface, which ->
                                    onBackPressed()
                                    dialogInterface.dismiss()
                                }
                                .setNegativeButton("Add More") { dialogInterface, which ->
                                    clearScreen()
                                    dialogInterface.dismiss()
                                }
                                .build()
                            mDialog.show()

                        }
                    }
            }
        })
    }

    fun clearScreen() {
        imgProfile.setImageDrawable(null)
        fileUri = null
        spinner.setSelection(0)
        category = ""
        tiEtCakeName.setText("")
        tiEtDescription.setText("")
        tiEtPrice.setText("")
        tiEtQuantity.setText("")
        tv_cake_add.visibility = View.VISIBLE
        feedType = ArrayList()
        chipGroup.removeAllViews()
        spinner_weight.setSelection(0)
    }

    private fun checkValidation(): Boolean {
        if (fileUri == null) {
            CommonUtils.showSnackBar(this, "Please upload item image", root)
            return false
        } else if (spinner.selectedItem.toString() == "Select Category") {
            CommonUtils.showSnackBar(this, "Please choose category", root)
            return false
        } else if (tiEtCakeName.text.toString() == "" && tiEtCakeName.text.toString().length < 5) {
            CommonUtils.showSnackBar(this, "Item Name should be greater than 5", root)
            return false
        } else if (tiEtDescription.text.toString() == "" && tiEtDescription.text.toString().length < 5) {
            CommonUtils.showSnackBar(this, "Item Description should be greater than 5", root)
            return false
        } else if (feedType.size == 0) {
            CommonUtils.showSnackBar(this, "Item Weight is mandatory", root)
            return false
        } else if (tiEtPrice.text.toString() == "") {
            CommonUtils.showSnackBar(this, "Price is mandatory", root)
            return false
        } else if (tiEtQuantity.text.toString() == "") {
            CommonUtils.showSnackBar(this, "Max Quantity  is mandatory", root)
            return false
        }
        else if (tiEtStock.text.toString() == "") {
            CommonUtils.showSnackBar(this, "Item in Stock is mandatory", root)
            return false
        }
        return true
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p0?.id == R.id.spinner) {
            category = country[p2]
        } else if (p0?.id == R.id.spinner_weight) {
            weight_category = weightArray[p2]
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }
}