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
import com.kevindom.skeight.R
import com.kevindom.skeight.adapter.UserAdapter
import com.kevindom.skeight.databinding.FragmentCreateRoomBinding
import com.kevindom.skeight.firebase.RoomManager
import com.kevindom.skeight.firebase.UserManager
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
        binding.createRoomRecycler.layoutManager = LinearLayoutManager(context)
        binding.createRoomRecycler.adapter = adapter

        setupListeners()
    }

    private fun setupListeners() {
        binding.createRoomContainer.setOnClickListener {
            (activity as CreateRoomListener).onCreateRoomExited()
        }
        binding.createRoomBtnCancel.setOnClickListener {
            (activity as CreateRoomListener).onCreateRoomExited()
        }
        binding.createRoomBtnCreate.setOnClickListener {
            if (validate()) {
                val selectedUsers = adapter.selectedItems
                val roomName = binding.createRoomName.text.toString()

                binding.createRoomLoader.startAnimation(R.drawable.anim_loading, loop = true)
                roomManager.addRoom(roomName, selectedUsers) {
                    (activity as CreateRoomListener).onCreateRoomExited()
                }
            }
        }

        userManager.addOnUsersListener {
            binding.createRoomLoader.visibility = View.GONE
            adapter.update(it)
        }
    }

    override fun onStart() {
        super.onStart()
        binding.createRoomLoader.startAnimation(R.drawable.anim_loading, loop = true)
    }

    private fun validate(): Boolean {
        return when {
            binding.createRoomName.text.isBlank() -> {
                binding.inErrorState = true
                binding.errorMessage = R.string.create_room_error_name.str(context)
                false
            }
            adapter.selectedItems.isEmpty() -> {
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

    interface CreateRoomListener {
        fun onCreateRoomExited()
    }
}