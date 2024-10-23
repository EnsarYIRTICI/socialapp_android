package com.edison.mulaki.Fragments

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.edison.mulaki.R
import com.edison.mulaki.Utils.AndyRubin
import com.edison.mulaki.ViewModels.ChatViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File


class PickerBottomSheetFragment : BottomSheetDialogFragment() {

    private val viewModel: ChatViewModel by viewModels()
    private val progressDialog = ProgressDialogFragment()
    private val selectedFiles = mutableListOf<File>()

    var _ID_ROOM:String?= null
    var _uid:String? = null
    val ID_ROOM:String get() = _ID_ROOM!!
    val uid:String get() = _uid!!

    companion object {
        const val PICK_IMAGE_REQUEST = 1
        const val PICK_VIDEO_REQUEST = 2
        const val PICK_CONTACT_REQUEST = 3

        const val KEY_UID = "uid"
        const val KEY_ID_ROOM = "roomid"

        fun instance( ID_ROOM: String, uid:String): PickerBottomSheetFragment {
            val fragment = PickerBottomSheetFragment()
            val args = Bundle()

            args.putString(KEY_ID_ROOM, ID_ROOM)
            args.putString(KEY_UID, uid)

            fragment.arguments = args

            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);

        _ID_ROOM = arguments?.getString(KEY_ID_ROOM)
        _uid = arguments?.getString(KEY_UID)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottomsheet_chat_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val contactsGallery = view.findViewById<ImageView>(R.id.contactsGallery)
        val documentGallery = view.findViewById<ImageView>(R.id.documentGallery)
        val videoGallery = view.findViewById<ImageView>(R.id.videoGallery)
        val photoGallery = view.findViewById<ImageView>(R.id.photoGallery)


        contactsGallery.setOnClickListener {
            openContactsGallery()
        }

        documentGallery.setOnClickListener {
            openFileGallery()
        }

        videoGallery.setOnClickListener {
            openVideoGallery()
        }

        photoGallery.setOnClickListener {
            openPhotoGallery()
        }

        viewModel.file.observe(viewLifecycleOwner, Observer { result->
            try {
                progressDialog.progressLiveData.value = result
                result.toInt()
            }catch (e:Exception){
                val intent = Intent(ChatFragment.UPLOAD_FILE)
                LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent)
                progressDialog.dismiss()
                dismiss()
            }
        })


        viewModel.warn.observe(viewLifecycleOwner, Observer { result->
            showResult(result)
        })

    }

    private fun openContactsGallery(){
        if(AndyRubin().readContactsPermission(requireContext())){
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            startActivityForResult(intent, PICK_CONTACT_REQUEST)
        }else{
            val permission = Manifest.permission.READ_CONTACTS
            requestPermissionLauncher.launch(permission)
        }
    }
    private fun openFileGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        galleryLauncher.launch(Intent.createChooser(intent, "Select Files"))
    }


    private fun openVideoGallery() {
        try {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_VIDEO_REQUEST)
        }catch (e:SecurityException){
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "video/*"
            startActivityForResult(intent, PICK_VIDEO_REQUEST)
        }
    }

    private fun openPhotoGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }



    private fun showProgressDialog(){
        progressDialog.show(requireActivity().supportFragmentManager, "progressDialog")
    }


    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val data: Intent? = result.data

                data?.clipData?.let { clipData ->
                    for (i in 0 until clipData.itemCount) {
                        val uri: Uri = clipData.getItemAt(i).uri
                        println(uri)
//                        val file = AndyRubin().fileFromContentUri(context, uri)
//                        selectedFiles.add(file)
                    }
                } ?: data?.data?.let { uri ->
                        println(uri)
//                    val file = AndyRubin().fileFromContentUri(context, uri)
//                    selectedFiles.add(file)
                }
                selectedFiles.forEach { file ->
                    println("Selected File: ${file.path}")
                }

            }
        }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && data != null){

            data.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    val file = AndyRubin().fileFromContentUri(context, uri)
                    selectedFiles.add(file)
                }
            }?: data.data?.let { uri ->
                    println(uri)
                    val file = AndyRubin().fileFromContentUri(context, uri)
                    selectedFiles.add(file)
            }


            when (requestCode) {
                PICK_IMAGE_REQUEST -> uploadFiles()
                PICK_VIDEO_REQUEST -> uploadFiles()
                PICK_CONTACT_REQUEST -> sendContact(data.data!!)
            }

        }

    }


    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                startActivityForResult(intent, PICK_CONTACT_REQUEST)
            } else {
                showResult("Access to the directory was denied!")
            }
        }
    private fun uploadFiles(){
        showProgressDialog()
        viewModel.uploadFiles(selectedFiles, null, uid, ID_ROOM)
    }

    private fun sendContact(uri: Uri) {
        try {
            val contentResolver: ContentResolver = requireContext().contentResolver
            val cursor = contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )

            cursor?.use {
                while (it.moveToNext()) {
                    val contactIdIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
                    val contactId = it.getString(contactIdIndex)
                    val displayNameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                    val displayName = it.getString(displayNameIndex)

                    println(contactId)
                    println(displayName)

                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(contactId),
                        null
                    )

                    phoneCursor?.use { phoneCursorInner ->
                        while (phoneCursorInner.moveToNext()) {
                            val phoneNumberIndex = phoneCursorInner.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                            val phoneNumber =
                                phoneCursorInner.getString(phoneNumberIndex)

                            println(phoneNumber)
                        }
                    }
                }
            }

        }catch (e:Exception){
            println(e)
        }

    }

    private fun showResult(result:String){
        Toast.makeText(this.context,result, Toast.LENGTH_LONG).show()
    }


}
