package com.kevindom.skeight.activity

import android.os.Bundle
import bind
import com.github.salomonbrys.kodein.android.KodeinAppCompatActivity
import com.kevindom.skeight.R
import com.kevindom.skeight.databinding.ActivityMainBinding
import com.kevindom.skeight.fragment.CreateRoomFragment
import com.kevindom.skeight.fragment.RoomFragment
import com.kevindom.skeight.fragment.RoomsFragment
import com.kevindom.skeight.model.Room
import inTransaction

class MainActivity : KodeinAppCompatActivity(), RoomsFragment.OnRoomsListener, PopupExitListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = R.layout.activity_main.bind(this)

        supportFragmentManager.inTransaction {
            add(R.id.fragment_container, RoomsFragment())
        }
    }

    override fun onRoomClicked(room: Room) {
        supportFragmentManager.inTransaction {
            setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            add(R.id.fragment_container, RoomFragment.create(room))
        }
    }

    override fun onCreateRoomClicked() {
        supportFragmentManager.inTransaction {
            setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            add(R.id.fragment_container, CreateRoomFragment())
        }
    }

    override fun onPopupExited() = onBackPressed()

    override fun onBackPressed() {
        val popupFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (popupFragment != null && popupFragment !is RoomsFragment) {
            supportFragmentManager.inTransaction {
                remove(popupFragment)
            }
        } else super.onBackPressed()
    }
}

interface PopupExitListener {
    fun onPopupExited()
}