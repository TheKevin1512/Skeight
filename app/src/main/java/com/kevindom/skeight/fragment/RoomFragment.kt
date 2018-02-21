package com.kevindom.skeight.fragment

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
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
import com.kevindom.skeight.firebase.StorageManager
import com.kevindom.skeight.firebase.UserManager
import com.kevindom.skeight.model.Message
import com.kevindom.skeight.model.Room
import com.kevindom.skeight.util.NamingHelper
import loop
import str

class RoomFragment : KodeinSupportFragment() {

    companion object {
        private const val RC_FILE = 414
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

    private lateinit var binding: FragmentRoomBinding
    private lateinit var chatterAdapter: ChatterAdapter
    private lateinit var chatAdapter: ChatAdapter

    private lateinit var room: Room
    private lateinit var userId: String
    private lateinit var name: String

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
        setupListeners()
        subscribeEvents()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_FILE) {
            data?.let {
                val drawable = AnimatedVectorDrawableCompat.create(context, R.drawable.anim_image_upload)
                binding.roomBtnFile.setImageDrawable(drawable)
                drawable?.loop()
                val key = messageManager.reserveMessage()
                storageManager.upload(it.data, context.contentResolver, key, completeListener = {
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
    }

    private fun setupAdapters() {
        chatterAdapter = ChatterAdapter(layoutInflater)
        binding.roomChatterRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.roomChatterRecycler.adapter = chatterAdapter

        chatAdapter = ChatAdapter(layoutInflater, userId)
        binding.roomChatRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        binding.roomChatRecycler.adapter = chatAdapter
    }

    private fun setupListeners() {
        binding.toolbarTitle.text = room.name
        binding.roomBtnBack.setOnClickListener { activity.onBackPressed() }
        binding.roomBtnFile.setOnClickListener {
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
        room.userIds.keys.forEach {
            userManager.addOnUserListener(it) {
                chatterAdapter.add(it.photoUrl to (NamingHelper.getFirstName(it.name) ?: it.name))
            }
        }
        messageManager.addOnMessageListener(room.id) {
            chatAdapter.update(it)
            binding.roomChatRecycler.scrollToPosition(0)
        }
    }
}