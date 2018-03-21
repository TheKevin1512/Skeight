package com.kevindom.skeight.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bind
import com.github.salomonbrys.kodein.android.KodeinSupportFragment
import com.github.salomonbrys.kodein.instance
import com.google.firebase.auth.FirebaseAuth
import com.kevindom.skeight.R
import com.kevindom.skeight.activity.LaunchActivity
import com.kevindom.skeight.adapter.RoomAdapter
import com.kevindom.skeight.auth.AuthManager
import com.kevindom.skeight.databinding.FragmentRoomsBinding
import com.kevindom.skeight.firebase.AnalyticsManager
import com.kevindom.skeight.firebase.RoomManager
import com.kevindom.skeight.model.Room
import com.kevindom.skeight.util.ConnectivityUtil
import startAnimation
import str

class RoomsFragment : KodeinSupportFragment(), RoomManager.RoomListener {

    private val roomManager: RoomManager by instance()
    private val analyticsManager: AnalyticsManager by instance()

    private lateinit var adapter: RoomAdapter
    private lateinit var binding: FragmentRoomsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = R.layout.fragment_rooms.bind(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = FirebaseAuth.getInstance().currentUser ?: throw IllegalStateException("User cannot be null at this point")

        adapter = RoomAdapter(layoutInflater, roomManager, user.uid) {
            analyticsManager.logSelectItem(it)
            (activity as OnRoomsListener).onRoomClicked(it)
        }
        binding.roomsRecycler.layoutManager = LinearLayoutManager(context)
        binding.roomsRecycler.adapter = adapter

        binding.inErrorState = true

        setupListeners(user.uid)
    }

    override fun onStart() {
        super.onStart()
        checkState()
    }

    private fun setupListeners(userId: String) {
        roomManager.addOnRoomsListener(userId, this)
        binding.roomsFab.setOnClickListener {
            (activity as OnRoomsListener).onCreateRoomClicked()
        }
        binding.btnSignOut.setOnClickListener {
            AuthManager.signOut(activity) {
                val intent = Intent(context, LaunchActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                activity.finish()
                startActivity(intent)
            }
        }
    }

    private fun checkState() {
        binding.inErrorState = false
        if (!ConnectivityUtil.isConnected(context)) {
            binding.inErrorState = true
            binding.errorTitle = R.string.error_state_network_title.str(context)
            binding.errorDescription = R.string.error_state_network_description.str(context)
            binding.roomsImgError.startAnimation(R.drawable.anim_no_wifi)
        } else if (adapter.itemCount == 0) {
            binding.inErrorState = true
            binding.errorTitle = R.string.error_state_rooms_title.str(context)
            binding.errorDescription = R.string.error_state_rooms_description.str(context)
            binding.roomsImgError.startAnimation(R.drawable.anim_no_rooms)
        }
    }

    override fun onRoomAdded(room: Room) {
        binding.inErrorState = false
        adapter.add(room)
    }

    override fun onRoomRemoved(room: Room) {
        adapter.remove(room)
        checkState()
    }

    override fun onRoomChanged(room: Room) {
        adapter.update(room)
    }

    interface OnRoomsListener {
        fun onCreateRoomClicked()
        fun onRoomClicked(room: Room)
    }
}