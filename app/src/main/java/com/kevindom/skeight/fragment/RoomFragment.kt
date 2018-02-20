package com.kevindom.skeight.fragment

import android.os.Bundle
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
import com.kevindom.skeight.firebase.UserManager
import com.kevindom.skeight.model.Message
import com.kevindom.skeight.model.Room
import com.kevindom.skeight.util.NamingHelper

class RoomFragment : KodeinSupportFragment() {

    companion object {
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
    private val userManager: UserManager by lazy { appKodein().instance<UserManager>() }

    private lateinit var binding: FragmentRoomBinding
    private lateinit var chatterAdapter: ChatterAdapter
    private lateinit var chatAdapter: ChatAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = R.layout.fragment_room.bind(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser ?: throw IllegalStateException("This section requires an authenticated user.")
        val name = user.displayName ?: throw IllegalStateException("Name cannot be null at this point")
        val room = arguments.getSerializable(EXTRA_ROOM) as Room

        setupAdapters(user.uid)
        setupListeners(user.uid, name, room)
        subscribeEvents(room)
    }

    private fun setupAdapters(userId: String) {
        chatterAdapter = ChatterAdapter(layoutInflater)
        binding.roomChatterRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.roomChatterRecycler.adapter = chatterAdapter

        chatAdapter = ChatAdapter(layoutInflater, userId)
        binding.roomChatRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        binding.roomChatRecycler.adapter = chatAdapter
    }

    private fun setupListeners(userId: String, name: String, room: Room) {
        binding.toolbarTitle.text = room.name
        binding.roomBtnBack.setOnClickListener { activity.onBackPressed() }
        binding.roomBtnSend.setOnClickListener {
            val content = binding.roomEditMessage.text.toString()
            if (content.isNotBlank()) {
                messageManager.addMessage(Message(room.id, userId, NamingHelper.getFirstName(name), content, System.currentTimeMillis()))
                binding.roomEditMessage.text.clear()
            }
        }
    }

    private fun subscribeEvents(room: Room) {
        room.userIds.keys.forEach {
            userManager.addOnUserListener(it) {
                chatterAdapter.add(it.photoUrl to NamingHelper.getFirstName(it.name))
            }
        }
        messageManager.addOnMessageListener(room.id) {
            chatAdapter.update(it)
            binding.roomChatRecycler.scrollToPosition(0)
        }
    }
}