package com.kevindom.skeight.fragment

import android.databinding.ObservableBoolean
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
import com.kevindom.skeight.activity.PopupExitListener
import com.kevindom.skeight.adapter.UserAdapter
import com.kevindom.skeight.databinding.FragmentEditRoomBinding
import com.kevindom.skeight.firebase.RoomManager
import com.kevindom.skeight.firebase.UserManager
import startAnimation
import str

/**
 * Created by kevindom on 26/02/2018.
 */
class EditRoomFragment : KodeinSupportFragment() {

    companion object {
        private const val EXTRA_ROOM_ID = "extra_room_id"

        fun create(roomId: String): EditRoomFragment {
            return EditRoomFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_ROOM_ID, roomId)
                }
            }
        }
    }

    private val userManager: UserManager by instance()
    private val roomManager: RoomManager by instance()

    private lateinit var adapter: UserAdapter
    private lateinit var binding: FragmentEditRoomBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = R.layout.fragment_edit_room.bind(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val roomId = arguments.getString(EXTRA_ROOM_ID)

        this.adapter = UserAdapter(layoutInflater, appKodein().instance())
        binding.addContainer!!.addUserRecycler.layoutManager = LinearLayoutManager(context)
        binding.addContainer!!.addUserRecycler.adapter = adapter
        binding.addContainer!!.positiveText = R.string.edit_room_text_add.str(context)
        binding.addContainer!!.addUserBtnPositive.setOnClickListener {
            adapter.selectedUsers.forEach { roomManager.addUser(roomId, it.first.id) }
            (activity as PopupExitListener).onPopupExited()
        }
        binding.addContainer!!.addUserBtnNegative.setOnClickListener { (activity as PopupExitListener).onPopupExited() }
        binding.editRoomContainer.setOnClickListener { (activity as PopupExitListener).onPopupExited() }

        userManager.addOnUsersListener {
            binding.addContainer!!.addUserLoader.visibility = View.GONE
            adapter.updateAll(it.map { it to ObservableBoolean(false) })
        }
    }

    override fun onStart() {
        super.onStart()
        binding.addContainer!!.addUserLoader.startAnimation(R.drawable.anim_loading, loop = true)
    }
}