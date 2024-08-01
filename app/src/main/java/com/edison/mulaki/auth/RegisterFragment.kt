package com.edison.mulaki.auth

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
import com.edison.mulaki.MainActivity
import com.edison.mulaki.databinding.FragmentRegisterBinding
import org.json.JSONObject

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater,container,false)
        val root:View = binding.root

        setupRegister()

        return root
    }


    private fun setupRegister(){

        viewModel.warn.observe(viewLifecycleOwner, Observer { result->
            showResult(result)
        })

        viewModel.registerData.observe(viewLifecycleOwner, Observer { result ->
            val jsonData = JSONObject(result.toString())
            Auth.authData = jsonData
            navigateMainActivity()
        })

        binding.register.setOnClickListener {
            val displayname = binding.displayname.text.toString()
            val username = binding.username.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            viewModel.signUp(displayname,username,email,password)
        }

        binding.signin.setOnClickListener {
           findNavController().popBackStack()
        }

    }

    private fun navigateMainActivity(){
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showResult(result:String){
        Toast.makeText(this.context, result, Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}