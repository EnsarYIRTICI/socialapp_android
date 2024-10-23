package com.edison.mulaki.Fragments

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.edison.mulaki.ViewModels.ChatViewModel
import com.edison.mulaki.Utils.AndreyBreslav
import com.edison.mulaki.Utils.AndyRubin
import com.edison.mulaki.Utils.RyanDahl
import com.edison.mulaki.databinding.FragmentChatBinding
import com.edison.mulaki.Utils.Auth
import com.edison.mulaki.Utils.SocketKeys
import com.edison.mulaki.Adapters.BubbleAdapter
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class ChatFragment: Fragment(){

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private var _inboxSocket: Socket? = null
    private  val inboxSocket get() = _inboxSocket!!

    private val viewModel: ChatViewModel by viewModels()
    private val itemList = arrayListOf<JSONObject>()

    private val socketKeys = SocketKeys()

    private val lim:Int = 20
    private val set:Int = 0
    private var count:Int? = null
    private var countPrev:Int? = null
    private val AUTH_UID get() = Auth.authData.get("uid").toString()

    private var DATA_ROOM:JSONObject? = null
    private val ID_ROOM get() = DATA_ROOM?.getString("id")!!
    private val USERNAME get() = DATA_ROOM?.getString("username")!!

    private var globalPosition:Int? = null

    lateinit var adapter: BubbleAdapter

    private val progressDialog = ProgressDialogFragment()

    companion object {
        const val BUBBLE_ON_LONG_CLICK = "BUBBLE_ON_LONG_CLICK"
        const val BUBBLE_IMAGE_ON_CLICK = "BUBBLE_IMAGE_ON_CLICK"
        const val UPLOAD_FILE = "UPLOAD_FILE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DATA_ROOM = JSONObject(arguments?.getString("roomData").toString())
        joinRoomSocket()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater,container,false)
        val root:View = binding.root

        roomViewSetup()

        return root
    }

    private fun roomViewSetup(){
        registerForContextMenu(binding.bubbleListView)

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver,
            IntentFilter(UPLOAD_FILE)
        )
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver,
            IntentFilter(BUBBLE_ON_LONG_CLICK)
        )
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver,
            IntentFilter(BUBBLE_IMAGE_ON_CLICK)
        )

        viewModel.resultLiveData.observe(viewLifecycleOwner, Observer { result ->
            if(count==countPrev) {
                createList(JSONArray(result.toString()))
                createAdapter(ID_ROOM)
            } else {
                updateList(JSONArray(result.toString()))
                createAdapter(ID_ROOM)
                countPrev = count
            }
        })

        viewModel.count.observe(viewLifecycleOwner, Observer { result->
            if(count == null) {
                count = result.toInt()
                countPrev = count
            }
            else {
                count = result.toInt()
                viewModel.findAll(ID_ROOM, count!!-countPrev!!, set)
            }

        })

        viewModel.bubble.observe(viewLifecycleOwner, Observer { result->
            viewModel.count(ID_ROOM)
        })

        viewModel.file.observe(viewLifecycleOwner, Observer { result->
            viewModel.count(ID_ROOM)
            socketSendMessage()
        })

        viewModel.warn.observe(viewLifecycleOwner, Observer { result->
            showResult(result)
        })

        viewModel.progress.observe(viewLifecycleOwner, Observer { result->
            try {
                progressDialog.progressLiveData.value = result
                result.toInt()
            }catch (e:Exception){
                hideProgressDialog()
            }
        })


        viewModel.findAll(ID_ROOM, lim, set)
        viewModel.count(ID_ROOM)

        binding.sendBubble.setOnClickListener {
            sendBubble()
        }

        binding.openGallery.setOnClickListener{
            if(AndyRubin().writeExternalStoragePermission(requireContext())) {
              showPickerSheet()
            }else{
                val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
                requestPermissionLauncher.launch(permission)
            }
        }

    }

    private fun joinRoomSocket(){
        try {
            val opts = IO.Options()
            opts.path = "/socket/inbox"

            _inboxSocket = IO.socket(RyanDahl().SOCKET_URL, opts)

            inboxSocket.connect()
            inboxSocket.emit(socketKeys.JOIN_ROOM,ID_ROOM);
            inboxSocket.on(socketKeys.ON_MESSAGE, onGetMessage)
        } catch (e: Exception) {
            showResult(e.message!!)
        }
    }

    private fun updateList(jsonArray: JSONArray){
        for(i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            itemList.add(0, jsonObject)
        }
    }


    private fun createList(jsonArray: JSONArray){
        for(i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            itemList.add(jsonObject)
        }
    }

    private fun createAdapter(ID_ROOM:String) {
        adapter =  BubbleAdapter(requireContext(), itemList.reversed(), ID_ROOM, AUTH_UID)
        binding.bubbleListView.adapter = adapter
        if(count == countPrev) {
            binding.bubbleListView.setSelection(20)
        }
        adapter.onEndOfListReached={
            if(itemList.count() < (count ?: 0)) {
                viewModel.findAll(ID_ROOM, lim, itemList.count())
            }
        }
    }


    private val onGetMessage = Emitter.Listener { args ->
        requireActivity().runOnUiThread {
            try {
                viewModel.count(ID_ROOM)
            } catch (e: JSONException) {
                return@runOnUiThread
            }
        }
    }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                showPickerSheet()
            } else {
                showResult("Galeriye erişim izni verilmedi!")
            }
        }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when(intent?.action){
                UPLOAD_FILE ->{
                    viewModel.count(ID_ROOM)
                    socketSendMessage()
                }
                BUBBLE_IMAGE_ON_CLICK ->{
                    val ID_BUBBLE = intent.getStringExtra("_id").toString()
                    showGallerySheet(ID_BUBBLE)
                }
                BUBBLE_ON_LONG_CLICK ->{
                    val position = intent.getIntExtra("position", -1)
                    globalPosition = position
                    try {
                        binding.bubbleListView.showContextMenu()
                    }catch (e:Exception){
                        println(e)
                    }
                }
            }
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)

        val item:JSONObject = binding.bubbleListView.getItemAtPosition(globalPosition!!) as JSONObject
        val file:JSONObject? = item.opt("file") as JSONObject?
        if(file != null){ menu.add(Menu.NONE, 1, Menu.NONE, "Save to gallery") }
        menu.add(Menu.NONE, 2, Menu.NONE, "Exit")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> {
                downloadFileAndSave(globalPosition)
            }
            2 -> {}
        }
        return super.onContextItemSelected(item)
    }

    private fun downloadFileAndSave (position:Int?){
        try {
            val item:JSONObject = binding.bubbleListView.getItemAtPosition(position!!) as JSONObject
            val file:JSONObject = item.opt("file") as JSONObject
            val fileName = file.get("name") as String
            val fileUrl = RyanDahl().URL_ROOM_MEDIA + ID_ROOM + "/" + fileName
            viewModel.downloadFileAndSave(requireContext(), fileUrl, fileName)
            showProgressDialog()
        }  catch (e:Exception){
            showResult(e.toString())
        }
    }

    private fun sendBubble(){
        val message = binding.inputMessage.text.toString()
        if(AndreyBreslav().emptyFilter(message)){
            val cleanedMessage = message.trim().replace("\\s+".toRegex(), " ")
            viewModel.send(cleanedMessage, AUTH_UID, ID_ROOM)
            binding.inputMessage.text.clear()
            socketSendMessage()
        }else{
            binding.inputMessage.text.clear()
        }
    }

    private fun socketSendMessage(){
        inboxSocket.emit(socketKeys.SEND_MESSAGE, JSONObject().apply {
            put("roomid",ID_ROOM)
            put("fid", AUTH_UID)
        })

    }

    private fun showPickerSheet(){
        val bottomSheetFragment: PickerBottomSheetFragment = PickerBottomSheetFragment.instance(ID_ROOM, AUTH_UID)
        bottomSheetFragment.show(parentFragmentManager, "chatBottomSheet")
    }

    private fun showGallerySheet(ID_BUBBLE:String){
        try {
            val bottomSheetFragment: GalleryBottomSheetFragment = GalleryBottomSheetFragment.instance(itemList.toString(), ID_ROOM, ID_BUBBLE)
            bottomSheetFragment.enterTransition = 0
            bottomSheetFragment.exitTransition = 0
            bottomSheetFragment.show(parentFragmentManager, "galleryBottomSheet")
        }catch (e:IllegalStateException){
            println(e)
        }
    }


    private fun showProgressDialog(){
        progressDialog.show(requireActivity().supportFragmentManager,"progressDialog")
    }

    private fun hideProgressDialog(){
        progressDialog.dismiss()
    }


    private fun showResult(result:String){
        Toast.makeText(this.context,result,Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        inboxSocket.disconnect()
    }


}

