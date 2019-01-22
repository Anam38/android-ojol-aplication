package com.udacoding.intraojolfirebasekotlin.utama.home


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth

import com.udacoding.intraojolfirebasekotlin.R
import kotlinx.android.synthetic.main.fragment_home_background.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.toast

class HomeBackgroundFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_background, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val auth = FirebaseAuth.getInstance()
        var name = auth.currentUser?.displayName

        Homeuser.text = name.toString()
        btnmotor.onClick {
            val fr = HomeFragment()
            val fm = fragmentManager
            val fragmentTransaction = fm!!.beginTransaction()
            fragmentTransaction.add(R.id.container, fr)
            fragmentTransaction.commit()
        }
    }
}
