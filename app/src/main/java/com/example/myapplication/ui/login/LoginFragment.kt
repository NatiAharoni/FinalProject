package com.example.myapplication.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLoginBinding
import com.example.myapplication.utils.Loading
import com.example.myapplication.utils.Success
import com.example.myapplication.utils.Error
import com.example.myapplication.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var binding : FragmentLoginBinding by autoCleared()
    private val viewModel : LoginViewModel by viewModels()
//    private val viewModel : LoginViewModel by viewModels {
//        LoginViewModel.LoginViewModelFactory(AuthRepositoryFirebase())
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater,container,false)

        binding.noAcountTv.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.buttonLogin.setOnClickListener {

            viewModel.signInUser(binding.editTextLoginEmail.editText?.text.toString(),
                binding.editTextLoginPass.editText?.text.toString())
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userSignInStatus.observe(viewLifecycleOwner) {

            when(it.status) {
                is Loading -> {
                    binding.loginProgressBar.isVisible = true
                    binding.buttonLogin.isEnabled = false
                }
                is Success -> {
                    Toast.makeText(requireContext(),getString(R.string.LoginSuccess), Toast.LENGTH_SHORT).show()
                    val isAdmin = checkIfAdmin(it.status.data!!.email)
                    if (isAdmin) {
                        findNavController().navigate(R.id.action_loginFragment_to_allMoviesFragment)
                    }
                    else{
                        findNavController().navigate(R.id.action_loginFragment_to_allUserMoviesFragment)
                    }

                }
                is Error -> {
                    binding.loginProgressBar.isVisible = false
                    binding.buttonLogin.isEnabled = true
                    Toast.makeText(requireContext(),it.status.message,Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.currentUser.observe(viewLifecycleOwner) {

            when(it.status) {
                is Loading -> {
                    binding.loginProgressBar.isVisible = true
                    binding.buttonLogin.isEnabled = false
                }
                is Success -> {
                    findNavController().navigate(R.id.action_loginFragment_to_allMoviesFragment)
                }
                is Error -> {
                    binding.loginProgressBar.isVisible = false
                    binding.buttonLogin.isEnabled = true
                }
            }
        }
    }
}
private fun checkIfAdmin(email:String?) : Boolean{
    return email == "admin@gmail.com"
}