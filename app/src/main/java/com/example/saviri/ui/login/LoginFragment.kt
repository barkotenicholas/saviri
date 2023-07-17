package com.example.saviri.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.saviri.R
import com.example.saviri.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {


    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater,container,false)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        // handle the up button here
        return NavigationUI.onNavDestinationSelected(
            item,
            view!!.findNavController())
                || super.onOptionsItemSelected(item)
    }

}