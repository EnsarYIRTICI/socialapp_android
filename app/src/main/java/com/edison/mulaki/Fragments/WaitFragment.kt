package com.edison.mulaki.Fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.edison.mulaki.Activities.MainActivity
import com.edison.mulaki.R
import com.edison.mulaki.Utils.Auth
import com.edison.mulaki.ViewModels.AuthViewModel
import com.edison.mulaki.Utils.TimBernersLee
import com.edison.mulaki.databinding.FragmentWaitBinding
import org.json.JSONObject


class WaitFragment : Fragment(){

    private var _binding: FragmentWaitBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWaitBinding.inflate(inflater,container,false)
        val root:View = binding.root

        this.context?.let { Auth.init(it) }

        viewModel.connection.observe(viewLifecycleOwner, Observer { result->
            showConnectionAlert()
        })

        viewModel.warn.observe(viewLifecycleOwner, Observer { result->
            navigateLoginFragment()
        })

        viewModel.resultLiveData.observe(viewLifecycleOwner, Observer { result->
            val jsonData = JSONObject(result.toString())
            Auth.authData = jsonData
            navigateMainActivity()
        })

        autoLogin()

        return root
    }



    private fun autoLogin(){
        try {
            val username = Auth.authData.get("username").toString()
            val password = Auth.authData.get("password").toString()
            viewModel.signInWithUsernameAndPassword(username, password)
        }catch (e:Exception){
            navigateLoginFragment()
        }
    }


    private fun showConnectionAlert() {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setMessage(TimBernersLee().ioException)
            .setPositiveButton("Try Again") { _, _ ->
                autoLogin()
            }
            .create()
        alertDialog.show()
    }


    private fun navigateMainActivity(){
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateLoginFragment(){
      val navController = findNavController()
      navController.navigate(R.id.navigation_login)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

