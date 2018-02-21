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

class RoomsFragment : KodeinSupportFragment(), RoomAdapter.OnClickListener, RoomManager.RoomListener {

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
        adapter = RoomAdapter(layoutInflater, this)
        binding.roomsRecycler.layoutManager = LinearLayoutManager(context)
        binding.roomsRecycler.adapter = adapter

        binding.inEmptyState = true

        setupListeners()
    }

    private fun setupListeners() {
        val user = FirebaseAuth.getInstance().currentUser ?: throw IllegalStateException("UserID cannot be null at this point")
        roomManager.addOnRoomsListener(user.uid, this)
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

    override fun onRoomClicked(room: Room) {
        analyticsManager.logSelectItem(room)
        (activity as OnRoomsListener).onRoomClicked(room)
    }

    override fun onRoomAdded(room: Room) {
        binding.inEmptyState = false
        adapter.add(room)
    }

    override fun onRoomRemoved(room: Room) {
        adapter.remove(room)
        binding.inEmptyState = adapter.itemCount == 0
    }

    override fun onRoomChanged(room: Room) {
        adapter.update(room)
    }

    interface OnRoomsListener {
        fun onCreateRoomClicked()
        fun onRoomClicked(room: Room)
    }
}