package com.example.myapplication.ui.register

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
import com.example.myapplication.data.repository.firebaseImpl.AuthRepositoryFirebase
import com.example.myapplication.databinding.FragmentRegisterBinding
import com.example.myapplication.utils.Loading
import com.example.myapplication.utils.Success
import com.example.myapplication.utils.Error
import com.example.myapplication.utils.autoCleared

class RegisterFragment : Fragment(){

    private var binding : FragmentRegisterBinding by autoCleared()
    private val viewModel : RegisterViewModel by viewModels() {
        RegisterViewModel.RegisterViewModelFactory(AuthRepositoryFirebase())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)

        binding.userRegisterButton.setOnClickListener {

            viewModel.createUser(binding.edxtEmailAddress.editText?.text.toString(),
                binding.edxtPassword.editText?.text.toString())
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userRegistrationStatus.observe(viewLifecycleOwner) {

            when(it.status) {
                is Loading -> {
                    binding.registerProgress.isVisible = true
                    binding.userRegisterButton.isEnabled = false
                }
                is Success -> {
                    Toast.makeText(requireContext(),"Registration successful",Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
                is Error -> {
                    binding.registerProgress.isVisible = false
                    binding.userRegisterButton.isEnabled = true
                }
            }
        }

    }
}