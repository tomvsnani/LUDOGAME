package com.example.ludo.ui.fragments.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.ludo.ui.activities.MainActivity
import com.example.ludo.R
import com.example.ludo.ResultSealedClass
import com.example.ludo.data.UserRegistrationResponseModel
import com.example.ludo.databinding.FragmentLoginBinding
import com.example.ludo.ui.fragments.SelectAGameFragment
import com.example.ludo.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    lateinit var binding: FragmentLoginBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding = FragmentLoginBinding.bind(view)

        val viewmodel by viewModels<LoginViewModel>()

        setUpClickListeners(viewmodel)

        addBackPressedHandler()

        setUpToolBar()

        super.onViewCreated(view, savedInstanceState)
    }



    private fun setUpToolBar() {
        (activity as? MainActivity)?.setUpFragmentsToolbarProperties(
            resources.getString(R.string.app_name),
            true, null
        )
    }

    private fun setUpClickListeners(viewmodel: LoginViewModel) {
        binding.sednooverifyotpButton.setOnClickListener {

            if (!binding.sednooverifyotpButton.text.toString().toLowerCase().contains("verify")) {

                sendOtp(viewmodel)


            } else {

                verifyOtp(viewmodel)

            }
        }
    }

    private fun addBackPressedHandler() {
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    activity?.finish()
                }
            })
    }


    private fun sendOtp(viewmodel: LoginViewModel) {
        if (areFieldsValid()) {
            (activity as MainActivity).makeProgressVisible()

            viewmodel.loginUser(
                binding.mobilenumberEditText.text.toString(),
                binding.usernameEditText.text.toString()
            ).observe(viewLifecycleOwner, Observer { it1 ->

                (activity as MainActivity).makeProgresssHide()

                when (it1) {
                    is ResultSealedClass.Success -> {


                        handleOtpSentSuccess()
                    }

                    is ResultSealedClass.Failure -> {
                        handleResponseFailure(it1)

                    }

                }

            })
        } else
            (activity as MainActivity).showToast("Please enter all fields with valid data")
    }

    private fun handleResponseFailure(it1: ResultSealedClass.Failure<UserRegistrationResponseModel>) {
        try {
            if (it1.status == Constants.NETWORKFAIL) {
                (activity as MainActivity).showToast(it1.throwable.toString())
            } else {
                it1.message?.let { it2 ->
                    (activity as MainActivity).showToast(
                        it2
                    )
                }
            }
        } catch (e: Exception) {


        }
    }

    private fun handleOtpSentSuccess() {
        (activity as? MainActivity)?.let {

            binding.sednooverifyotpButton.text = "Verify Otp"
            binding.apply {
                pinView.visibility = View.VISIBLE


                beforeOtpConstraint.visibility = View.GONE


                afterOtpConstraint.visibility = View.VISIBLE

                usernametextview.text = usernameEditText.text
                mobilenumbertextview.text = mobilenumberEditText.text
            }

        }
    }

    private fun verifyOtp(viewmodel: LoginViewModel) {
        (activity as? MainActivity)?.makeProgressVisible()
        if (areFieldsValid() && binding.pinView.text.toString().isNotEmpty()) {

            viewmodel.verifyOtp(
                binding.mobilenumberEditText.text.toString(),
                binding.pinView.text.toString()
            ).observe(
                viewLifecycleOwner, Observer { resultSealedClass ->
                    try {
                        when (resultSealedClass) {
                            is ResultSealedClass.Success -> {
                                handleOtpVerifiedresponse(resultSealedClass)

                            }

                            is ResultSealedClass.Failure -> {
                                handleResponseFailure(resultSealedClass)
                            }
                        }
                    } catch (e: Exception) {

                    }
                }
            )


        } else {
            (activity as MainActivity).showToast("Please enter all required fields")
        }
    }

    private fun handleOtpVerifiedresponse(resultSealedClass: ResultSealedClass.Success<UserRegistrationResponseModel>) {
        (activity as MainActivity).let {
            it.showToast("Otp Verified")
            it.sessionManageMent(resultSealedClass.data?.data)
            it.loadFragment(SelectAGameFragment(), true, "home")
        }
    }


    private fun areFieldsValid(): Boolean {
        return binding.let {
            it.usernameEditText.text.isNotEmpty() && it.mobilenumberEditText.text.isNotEmpty() && it.mobilenumberEditText.text.toString().length == 10
        }
    }

    override fun onStop() {
        try {


            (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                binding.root.windowToken,
                0

            )
        } catch (e: Exception) {

        }

        (activity as? MainActivity)?.setUpFragmentsToolbarProperties(
            resources.getString(R.string.app_name),
            true, ResourcesCompat.getDrawable(resources, R.drawable.ic_hamburger, null)
        )
        super.onStop()
    }

}


