package com.edison.mulaki.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.edison.mulaki.Activities.MainActivity
import com.edison.mulaki.R
import com.edison.mulaki.Utils.Auth
import com.edison.mulaki.ViewModels.AuthViewModel
import com.edison.mulaki.databinding.FragmentLoginBinding
import org.json.JSONObject

class LoginFragment : Fragment(){

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        val root:View = binding.root

        loginSetup()

        return root
    }

    private fun loginSetup(){

        viewModel.warn.observe(viewLifecycleOwner, Observer { result->
            showResult(result)
        })

        viewModel.resultLiveData.observe(viewLifecycleOwner, Observer { result->
            val jsonData = JSONObject(result.toString())
            Auth.authData = jsonData
            navigateMainActivity()
        })

        binding.login.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()
            viewModel.signInWithUsernameAndPassword(username,password)
        }

        binding.signup.setOnClickListener {
            navigateRegisterFragment()
        }

    }

    private fun navigateMainActivity(){
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun navigateRegisterFragment(){
        findNavController().navigate(R.id.navigation_register)
    }

    private fun showResult(result: Any){
        Toast.makeText(this.context, result.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}