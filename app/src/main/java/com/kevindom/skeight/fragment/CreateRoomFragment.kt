package com.kevindom.skeight.fragment

import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bind
import closeKeyBoard
import com.github.salomonbrys.kodein.android.KodeinSupportFragment
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.google.firebase.auth.FirebaseAuth
import com.kevindom.skeight.R
import com.kevindom.skeight.activity.PopupExitListener
import com.kevindom.skeight.adapter.UserAdapter
import com.kevindom.skeight.databinding.FragmentCreateRoomBinding
import com.kevindom.skeight.firebase.RoomManager
import com.kevindom.skeight.firebase.UserManager
import com.kevindom.skeight.model.User
import startAnimation
import str

class CreateRoomFragment : KodeinSupportFragment() {

    private val roomManager: RoomManager by instance()
    private val userManager: UserManager by instance()

    private lateinit var binding: FragmentCreateRoomBinding
    private lateinit var adapter: UserAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = R.layout.fragment_create_room.bind(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.adapter = UserAdapter(layoutInflater, appKodein().instance())
        binding.addContainer!!.addUserRecycler.layoutManager = LinearLayoutManager(context)
        binding.addContainer!!.addUserRecycler.adapter = adapter
        binding.addContainer!!.positiveText = R.string.create_room_text_create.str(context)

        setupListeners()
    }

    private fun setupListeners() {
        binding.createRoomContainer.setOnClickListener {
            binding.createRoomName.closeKeyBoard()
            (activity as PopupExitListener).onPopupExited()
        }
        binding.addContainer!!.addUserBtnNegative.setOnClickListener {
            binding.createRoomName.closeKeyBoard()
            (activity as PopupExitListener).onPopupExited()
        }
        binding.addContainer!!.addUserBtnPositive.setOnClickListener {
            if (validate()) {
                val selectedUsers = mutableMapOf<String, Boolean>()
                val myUserId = FirebaseAuth.getInstance().currentUser?.uid ?: throw IllegalStateException("User ID cannot be null at this point")
                val roomName = binding.createRoomName.text.toString()

                selectedUsers[myUserId] = true
                selectedUsers.putAll(adapter.selectedUsers.associate { it.first.id to it.second.get() })

                binding.addContainer!!.addUserLoader.startAnimation(R.drawable.anim_loading, loop = true)
                roomManager.addRoom(roomName, selectedUsers) {
                    binding.createRoomName.closeKeyBoard()
                    (activity as PopupExitListener).onPopupExited()
                }
            }
        }

        binding.addContainer!!.addUserLoader.startAnimation(R.drawable.anim_loading, loop = true)
        userManager.addOnUsersListener {
            val myUserId = FirebaseAuth.getInstance().currentUser?.uid ?: throw IllegalStateException("User ID cannot be null at this point")

            binding.addContainer!!.addUserLoader.visibility = View.GONE
            adapter.updateAll(it.mapNotNull {
                if (it.id != myUserId)
                it to ObservableBoolean(false)
                else null
            })
        }
    }

    private fun validate(): Boolean {
        return when {
            binding.createRoomName.text.isBlank() -> {
                binding.inErrorState = true
                binding.errorMessage = R.string.create_room_error_name.str(context)
                false
            }
            adapter.selectedUsers.isEmpty() -> {
                binding.inErrorState = true
                binding.errorMessage = R.string.create_room_error_users.str(context)
                false
            }
            else -> {
                binding.inErrorState = false
                true
            }
        }
    }
}