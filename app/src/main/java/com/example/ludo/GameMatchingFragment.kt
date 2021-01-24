package com.example.ludo

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.marginEnd
import androidx.core.view.marginLeft
import androidx.core.view.marginStart
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.ludo.databinding.FragmentGameMatchingBinding

class GameMatchingFragment : Fragment(R.layout.fragment_game_matching) {
    lateinit var binding: FragmentGameMatchingBinding
    lateinit var clipboard: ClipboardManager
    lateinit var imageIntent: ActivityResultLauncher<String>

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentGameMatchingBinding.bind(view)
        var type = (activity as MainActivity).gameType

        binding.textView7.text =
            resources.getString(R.string.askhosttosharecode, "Mr", "Ramu", type)


        var isHost = (activity as MainActivity).isHost

        clipboard =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        Glide.with(requireContext()).load(R.drawable.coinsimage).transform(CircleCrop())
            .into(binding.player1image)
        Glide.with(requireContext()).load(R.drawable.wallet).transform(CircleCrop())
            .into(binding.player2image)




        if (!isHost) {

            var code = getRoomCode()

            binding.textView7.visibility = View.GONE




            binding.roomcodeedittext.apply {


                isCursorVisible = false


                setOnTouchListener { v, event ->
                    return@setOnTouchListener true
                }

                itemWidth =
                        // dividing screen width minus margins ( available space for pin view items) with
                        // (num of pin items  plus the num of pin items formed with the space between pin items)
                    (resources.displayMetrics.widthPixels -

                            (marginStart + marginEnd)) / (itemCount + ((((itemCount - 1) * itemSpacing)) / itemWidth))



                itemHeight = itemWidth

            }
            binding.roomcodeedittext.setText("helloomg")

//            binding.roomcodeedittext.apply {
//                isFocusableInTouchMode = false
//                setText(code)
//            }


            binding.submitRoomCode.apply {
                text = "COPY THE CODE"
                setOnClickListener {
                    if (binding.roomcodeedittext.text.toString().isNotEmpty())
                        copyCode(binding.roomcodeedittext.text.toString())
                }
            }

        } else {


            binding.textView7.visibility = View.VISIBLE
            binding.roomcodeedittext.isCursorVisible = true
            binding.roomcodeedittext.isClickable = true

//            binding.roomcodeedittext.apply {
//                isFocusableInTouchMode = true
//
//            }


            binding.submitRoomCode.apply {
                text = "SUBMIT CODE"
                setOnClickListener {
                    if (binding.roomcodeedittext.text.toString().isNotEmpty())
                        sendCode(binding.roomcodeedittext.text.toString())
                    else
                        Toast.makeText(context, "Please enter room code ", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        }

        binding.choosescreenshotbutton.setOnClickListener {
            imageIntent.launch("image/*")
        }

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }


            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                (parent?.getChildAt(0) as? TextView)?.setTextColor(Color.WHITE)
            }
        }

        binding.numoffilestextview.text = resources.getString(R.string.numOfFilesChoosenString, 2)
        val rect = Rect()
        binding.roomcodeedittext.getHitRect(rect)


        binding.root.setOnTouchListener { v, event ->


            if (!binding.roomcodeedittext.hasFocus() || !rect.contains(
                    event.x.toInt(),
                    event.y.toInt()
                )
            ) {
                clearFocusEditText()
                return@setOnTouchListener false
            }
            return@setOnTouchListener true
        }

//        handleBackPress()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getRoomCode() = "12345678"

    private fun copyCode(toString: String) {
        clipboard.setPrimaryClip(ClipData.newPlainText("roomcode", toString))
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
        (activity as MainActivity).displayGeneralAlertDialog(
            getString(R.string.codeCopiedAlertString),
            src = R.drawable.game_not_in_favour
        )
    }

    private fun sendCode(toString: String) {

    }

//    private fun handleBackPress() {
//        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                clearFocusEditText()
//                isEnabled = false
//            }
//        })
//    }

    private fun clearFocusEditText() {
//        binding.roomcodeedittext.clearFocus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageIntent = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            binding.numoffilestextview.text = "number of files choosen ${it.size.toString()}"
        }
    }
}