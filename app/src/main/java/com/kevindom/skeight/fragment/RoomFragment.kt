package com.kevindom.skeight.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bind
import com.github.salomonbrys.kodein.android.KodeinSupportFragment
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.google.firebase.auth.FirebaseAuth
import com.kevindom.skeight.R
import com.kevindom.skeight.adapter.ChatAdapter
import com.kevindom.skeight.adapter.ChatterAdapter
import com.kevindom.skeight.databinding.FragmentRoomBinding
import com.kevindom.skeight.firebase.MessageManager
import com.kevindom.skeight.firebase.RoomManager
import com.kevindom.skeight.firebase.StorageManager
import com.kevindom.skeight.firebase.UserManager
import com.kevindom.skeight.model.Chatter
import com.kevindom.skeight.model.Message
import com.kevindom.skeight.model.Room
import com.kevindom.skeight.util.NamingHelper
import inTransaction
import loop
import str
import java.io.File

class RoomFragment : KodeinSupportFragment() {

    companion object {
        private const val RC_FILE = 414
        private const val RC_PHOTO = 313
        private const val RC_PERMISSION = 314

        private const val EXTRA_ROOM = "extra_room"

        fun create(room: Room): RoomFragment {
            return RoomFragment().apply {
                val bundle = Bundle()
                bundle.putSerializable(EXTRA_ROOM, room)
                arguments = bundle
            }
        }
    }

    private val messageManager: MessageManager by lazy { appKodein().instance<MessageManager>() }
    private val storageManager: StorageManager by lazy { appKodein().instance<StorageManager>() }
    private val userManager: UserManager by lazy { appKodein().instance<UserManager>() }
    private val roomManager: RoomManager by lazy { appKodein().instance<RoomManager>() }

    private lateinit var binding: FragmentRoomBinding
    private lateinit var chatterAdapter: ChatterAdapter
    private lateinit var chatAdapter: ChatAdapter

    private lateinit var room: Room
    private lateinit var userId: String
    private lateinit var name: String

    private var currentPhotoPath = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = R.layout.fragment_room.bind(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser ?: throw IllegalStateException("This section requires an authenticated user.")

        this.room = arguments.getSerializable(EXTRA_ROOM) as Room
        this.userId = user.uid
        this.name = user.displayName ?: "Anonymous"

        setupAdapters()
        initUI()
        subscribeEvents()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_FILE) {
                data?.let { uploadImage(it.data) }
            } else if (requestCode == RC_PHOTO) {
                val image = File(currentPhotoPath)
                val uri = Uri.fromFile(image)
                uploadImage(uri)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == RC_PERMISSION) {
            if ((grantResults.isNotEmpty()) && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                takePhoto()
            }
        }
    }

    private fun setupAdapters() {
        chatterAdapter = ChatterAdapter(layoutInflater)
        binding.roomChatterRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.roomChatterRecycler.adapter = chatterAdapter

        chatAdapter = ChatAdapter(layoutInflater, userId)
        binding.roomChatRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        binding.roomChatRecycler.adapter = chatAdapter
    }

    private fun initUI() {
        binding.toolbarTitle.text = room.name
        binding.roomBtnBack.setOnClickListener { activity.onBackPressed() }
        binding.roomBtnAdd.setOnClickListener {
            fragmentManager.inTransaction {
                setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                add(R.id.fragment_container, EditRoomFragment.create(room.id))
            }
        }
        binding.roomBtnPhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), RC_PERMISSION)
            } else takePhoto()
        }
        binding.roomBtnFile.setOnClickListener {
            //            val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            startActivityForResult(intent, RC_FILE)
        }
        binding.roomBtnSend.setOnClickListener {
            val content = binding.roomEditMessage.text.toString()
            if (content.isNotBlank()) {
                messageManager.addMessage(Message(room.id, userId, NamingHelper.getFirstName(name) ?: name, content, System.currentTimeMillis()))
                binding.roomEditMessage.text.clear()
            }
        }
    }

    private fun subscribeEvents() {
        roomManager.addOnUserListener(room.id, addListener = {
            userManager.addOnUserListener(it) {
                chatterAdapter.add(Chatter(it.id, (NamingHelper.getFirstName(it.name) ?: it.name), it.photoUrl))
            }
        }, removeListener = { chatterAdapter.remove(it) })
        messageManager.addOnMessageListener(room.id) {
            chatAdapter.update(it)
            binding.roomChatRecycler.scrollToPosition(0)
        }
    }

    private fun takePhoto() {
        val storageDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(System.currentTimeMillis().toString(), ".jpg", storageDirectory)
        currentPhotoPath = image.absolutePath
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context, "com.kevindom.skeight.fileprovider", image))
        startActivityForResult(takePicture, RC_PHOTO)
    }

    private fun uploadImage(uri: Uri) {
        val drawable = AnimatedVectorDrawableCompat.create(context, R.drawable.anim_image_upload)
        binding.roomBtnFile.setImageDrawable(drawable)
        drawable?.loop()
        val key = messageManager.reserveMessage()
        storageManager.upload(uri, context.contentResolver, key, completeListener = {
            val content = binding.roomEditMessage.text.toString()
            messageManager.addMessage(
                    key,
                    Message(room.id,
                            userId,
                            NamingHelper.getFirstName(name) ?: name,
                            if (content.isBlank()) R.string.room_edit_text_default.str(context) else content,
                            System.currentTimeMillis(),
                            it
                    )
            )
            binding.roomEditMessage.text.clear()
            binding.roomBtnFile.setImageDrawable(context.getDrawable(R.drawable.ic_file))
            drawable?.clearAnimationCallbacks()
        }, failureListener = {
            Snackbar.make(binding.root, R.string.general_error_upload, Snackbar.LENGTH_SHORT)
            binding.roomBtnFile.setImageDrawable(context.getDrawable(R.drawable.ic_file))
            drawable?.clearAnimationCallbacks()
        })
    }
}