package com.example.ludo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.ludo.databinding.FragmentGameMatchingBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class GameMatchingFragment : Fragment(R.layout.fragment_game_matching) {
    lateinit var binding: FragmentGameMatchingBinding
    lateinit var clipboard: ClipboardManager
    lateinit var imageIntent: ActivityResultLauncher<String>
    var screenshot_uri = Uri.parse("")
    var gameResultSpinnerValue = -1

    var player1Image: String = ""
    var player2Image: String = ""

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentGameMatchingBinding.bind(view)
        var type = (activity as MainActivity).gameType


        val gameId = arguments?.getString(Constants.GAMEIDCONSTANT)
        val isHost = arguments?.getBoolean(Constants.ISHOSTCONSTANT)
        (activity as MainActivity).isHost = isHost!!
        var array =
            listOf<String>(
                "Select a option",
                "I won the game",
                "I lost the game",
                "cancel the game"
            )


        var adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            array
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter


        binding.submitResultButton.setOnClickListener {
            if ((activity as MainActivity).gameType == Constants.LUDOGAMETYPE)
                sendLudoGameResult(isHost, gameId)
            else
                sendSnakeGameResult(isHost, gameId)
        }




        clipboard =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        Glide.with(requireContext()).load(R.drawable.coinsimage).transform(CircleCrop())
            .into(binding.player1image)
        Glide.with(requireContext()).load(R.drawable.wallet).transform(CircleCrop())
            .into(binding.player2image)


        (activity as MainActivity).getPlayersDetails(gameId)

        getGameCode(gameId, isHost)


        if (!isHost) {


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

            binding.textView7.text =
                resources.getString(
                    R.string.askhosttosharecode,
                    requireActivity().getPreferences(Activity.MODE_PRIVATE)
                        .getString(Constants.USERNAMECONSTANT, ""),
                    type
                )
            binding.roomcodeedittext.isCursorVisible = true
            binding.roomcodeedittext.isClickable = true

            binding.roomcodeedittext.apply {


                itemWidth =
                        // dividing screen width minus margins ( available space for pin view items) with
                        // (num of pin items  plus the num of pin items formed with the space between pin items)
                    (resources.displayMetrics.widthPixels -

                            (marginStart + marginEnd)) / (itemCount + ((((itemCount - 1) * itemSpacing)) / itemWidth))



                itemHeight = itemWidth

            }

            binding.submitRoomCode.apply {
                text = "SUBMIT CODE"
                setOnClickListener {
                    if (binding.roomcodeedittext.text.toString().isNotEmpty())
                        sendCode(binding.roomcodeedittext.text.toString(), gameId)
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
                gameResultSpinnerValue = position
                Log.d("errotttr", "ddd")
            }
        }

        binding.numoffilestextview.text = resources.getString(R.string.numOfFilesChoosenString, 0)
        val rect = Rect()
        binding.roomcodeedittext.getHitRect(rect)


//        binding.root.setOnTouchListener { v, event ->
//
//
//            if (!binding.roomcodeedittext.hasFocus() || !rect.contains(
//                    event.x.toInt(),
//                    event.y.toInt()
//                )
//            ) {
//                clearFocusEditText()
//                return@setOnTouchListener false
//            }
//            return@setOnTouchListener true
//        }

//        handleBackPress()

        (activity as MainActivity).gameFeeMutableList.observe(viewLifecycleOwner, Observer {
            binding.betcoinstextview.text = it!!
        })


        (activity as MainActivity).gameDetailsLiveData.observe(viewLifecycleOwner, Observer {
            for (i in it) {

                if (i.id == (activity as MainActivity).getUserId()) {
                    player1Image = i.profile_pic ?: ""
                    Glide.with(context!!).load(player1Image)
                        .transform(CircleCrop()).placeholder(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.coinsimage,
                                null
                            )
                        ).into(binding.player1image)
                    binding.hostnametextview.text = i.username

                } else {
                    player2Image = i.profile_pic ?: ""
                    Glide.with(context!!).load(player2Image)
                        .transform(CircleCrop()).placeholder(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.coinsimage,
                                null
                            )
                        ).into(binding.player2image)
                    binding.playernametextview.text = i.username
                }
            }
        })


        super.onViewCreated(view, savedInstanceState)
    }

    private fun sendLudoGameResult(isHost: Boolean?, gameId: String?) {
        if (screenshot_uri.toString() != "" || gameResultSpinnerValue != -1) {
            (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
            var preferences = activity?.getPreferences(Activity.MODE_PRIVATE)!!
            var byteOutputStream = ByteArrayOutputStream()
            (activity as MainActivity).retrofit?.resultsApi(
                GameResultModelClassToSend(
                    preferences.getString(
                        Constants.USERIDCONSTANT,
                        ""
                    )!!,
                    preferences.getString(
                        Constants.USERNAMECONSTANT,
                        ""
                    )!!,


                    if (isHost!!) "host" else "player",
                    gameResultSpinnerValue.toString(),
                    gameId!!
                ),
                MultipartBody.Part.createFormData(
                    "screenshot",
                    gameId + "." + MimeTypeMap.getSingleton()
                        .getExtensionFromMimeType(
                            activity?.contentResolver?.getType(
                                screenshot_uri
                            )
                        ),
                    RequestBody.create(
                        MediaType.parse("image/*"),
                        activity?.contentResolver?.openInputStream(screenshot_uri)
                            ?.readBytes()!!
                    )
                )
            )?.enqueue(object : Callback<GameResultModelClassResponse> {
                override fun onFailure(call: Call<GameResultModelClassResponse>, t: Throwable) {
                    (activity as MainActivity).apply {
                        showToast(t.toString())
                        binding.progressbar.visibility = View.GONE
                    }
                }

                override fun onResponse(
                    call: Call<GameResultModelClassResponse>,
                    response: Response<GameResultModelClassResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.status == "1") {
                            (activity as MainActivity).isGamesultSubmitted = true
                            var body = response.body()!!
                            if(body.data!=null && body?.data?.isNotEmpty()!!) {
                                (activity as MainActivity).gameResultModel = body.data?.get(0)
                                (activity as MainActivity).loadFragment(GameResultFragment().apply {


                                    arguments = Bundle().apply {
                                        putString(Constants.GAMECODECONSTANT, body.game_code)
                                        putString(Constants.GAMEIDCONSTANT, gameId)
                                        putString("player2image", player2Image)
                                    }

                                })
                            }
                        } else {
                            if(response.body()?.message!=null)
                            (activity as MainActivity).showToast(response.body()?.message!!)
                        }
                    } else {
                        (activity as MainActivity).showToast(response.toString())
                    }
                    (activity as MainActivity).binding.progressbar.visibility = View.GONE
                }
            })

        } else
            (activity as MainActivity).showToast("Please enter all fields")
    }


    private fun sendSnakeGameResult(isHost: Boolean?, gameId: String?) {
        if (screenshot_uri.toString() != "" || gameResultSpinnerValue != -1) {
            (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
            var preferences = activity?.getPreferences(Activity.MODE_PRIVATE)!!

            (activity as MainActivity).retrofit?.resultsApi_snake(
                GameResultModelClassToSend(
                    preferences.getString(
                        Constants.USERIDCONSTANT,
                        ""
                    )!!,
                    preferences.getString(
                        Constants.USERNAMECONSTANT,
                        ""
                    )!!,


                    if (isHost!!) "host" else "player",
                    gameResultSpinnerValue.toString(),
                    gameId!!
                ),
                MultipartBody.Part.createFormData(
                    "screenshot",
                    gameId + "." + MimeTypeMap.getSingleton()
                        .getExtensionFromMimeType(
                            activity?.contentResolver?.getType(
                                screenshot_uri
                            )
                        ),
                    RequestBody.create(
                        MediaType.parse("image/*"),
                        activity?.contentResolver?.openInputStream(screenshot_uri)
                            ?.readBytes()!!
                    )
                )
            )?.enqueue(object : Callback<GameResultModelClassResponse> {
                override fun onFailure(call: Call<GameResultModelClassResponse>, t: Throwable) {
                    (activity as MainActivity).apply {
                        showToast(t.toString())
                        binding.progressbar.visibility = View.GONE
                    }
                }

                override fun onResponse(
                    call: Call<GameResultModelClassResponse>,
                    response: Response<GameResultModelClassResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.status == "1") {

                            (activity as MainActivity).isGamesultSubmitted = true
                            val body = response.body()!!
                            if (body?.data != null && body?.data?.isNotEmpty()!!){
                                (activity as MainActivity).gameResultModel = body.data?.get(0)
                            (activity as MainActivity).loadFragment(GameResultFragment().apply {


                                arguments = Bundle().apply {
                                    putString(Constants.GAMECODECONSTANT, body.game_code)
                                    putString(Constants.GAMEIDCONSTANT, gameId)
                                    putString("player2image", player2Image)
                                }

                            })
                        }


                        } else {
                            if(response.body()?.message!=null)
                            (activity as MainActivity).showToast(response.body()?.message!!)
                        }
                    } else {
                        (activity as MainActivity).showToast(response.toString())
                    }
                    (activity as MainActivity).binding.progressbar.visibility = View.GONE
                }
            })

        } else
            (activity as MainActivity).showToast("Please enter all fields")
    }


    private fun getGameCode(gameId: String?, isHost: Boolean) {
        if ((activity as MainActivity).gameType == Constants.LUDOGAMETYPE)
            getLudoGameCode(gameId, isHost)
        else
            getSnakeGameCode(gameId, isHost)
    }

    private fun getLudoGameCode(gameId: String?, isHost: Boolean) {
        (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
        (activity as MainActivity).retrofit?.getGameCodePlayer(gameId!!)?.enqueue(
            object : Callback<UserRegistrationResponseModel> {
                override fun onFailure(
                    call: Call<UserRegistrationResponseModel>,
                    t: Throwable
                ) {
                    (activity as MainActivity).apply {
                        showToast(t.toString())
                        binding.progressbar.visibility = View.GONE
                    }
                }

                override fun onResponse(
                    call: Call<UserRegistrationResponseModel>,
                    response: Response<UserRegistrationResponseModel>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.status == "1") {
                            response.body()?.message?.let {
                                binding.roomcodeedittext.setText(it)
                                if (isHost && it != null && it.isNotEmpty()) {
                                    binding.roomcodeedittext.apply {
                                        isClickable = false
                                        isFocusableInTouchMode = false
                                    }

                                } else {

                                }

                            }
                        } else
                            (activity as MainActivity).showToast(response.body()?.message!!)
                    } else {
                        (activity as MainActivity).showToast(response.toString())
                    }

                    (activity as MainActivity).binding.progressbar.visibility = View.GONE
                }
            }
        )
    }


    private fun getSnakeGameCode(gameId: String?, isHost: Boolean) {
        (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
        (activity as MainActivity).retrofit?.getGameCodePlayer_snake(gameId!!)?.enqueue(
            object : Callback<UserRegistrationResponseModel> {
                override fun onFailure(
                    call: Call<UserRegistrationResponseModel>,
                    t: Throwable
                ) {
                    (activity as MainActivity).apply {
                        showToast(t.toString())
                        binding.progressbar.visibility = View.GONE
                    }
                }

                override fun onResponse(
                    call: Call<UserRegistrationResponseModel>,
                    response: Response<UserRegistrationResponseModel>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.status == "1") {
                            response.body()?.message?.let {
                                binding.roomcodeedittext.setText(it)
                                if (isHost && it != null && it.isNotEmpty()) {
                                    binding.roomcodeedittext.apply {
                                        isClickable = false
                                        isFocusableInTouchMode = false
                                    }

                                } else {

                                }

                            }
                        } else
                            (activity as MainActivity).showToast(response.body()?.message!!)
                    } else {
                        (activity as MainActivity).showToast(response.toString())
                    }

                    (activity as MainActivity).binding.progressbar.visibility = View.GONE
                }
            }
        )
    }


    private fun copyCode(toString: String) {
        clipboard.setPrimaryClip(ClipData.newPlainText("roomcode", toString))
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
        (activity as MainActivity).displayGeneralAlertDialog(
            getString(R.string.codeCopiedAlertString),
            src = R.drawable.game_not_in_favour
        )
    }

    private fun sendCode(toString: String, gameId: String?) {
        if ((activity as MainActivity).gameType == Constants.LUDOGAMETYPE)
            sendLudoGameCode(toString, gameId)
        else

            sendSnakeGameCode(toString, gameId)

    }

    private fun sendLudoGameCode(toString: String, gameId: String?) {
        (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
        (activity as MainActivity).retrofit?.submitGameCodeHost(
            gameCode = toString,
            gameId = gameId!!
        )?.enqueue(
            object : Callback<UserRegistrationResponseModel> {
                override fun onFailure(call: Call<UserRegistrationResponseModel>, t: Throwable) {
                    (activity as MainActivity).apply {
                        showToast(t.toString())
                        binding.progressbar.visibility = View.GONE
                    }
                }

                override fun onResponse(
                    call: Call<UserRegistrationResponseModel>,
                    response: Response<UserRegistrationResponseModel>
                ) {
                    if (response.isSuccessful) {
                        (activity as MainActivity).showToast(response.body()?.message!!)
                    } else {
                        (activity as MainActivity).showToast(response.toString())
                    }
                    (activity as MainActivity).binding.progressbar.visibility = View.GONE
                }
            }
        )
    }


    private fun sendSnakeGameCode(toString: String, gameId: String?) {
        (activity as MainActivity).binding.progressbar.visibility = View.VISIBLE
        (activity as MainActivity).retrofit?.submitGameCodeHost_snake(
            gameCode = toString,
            gameId = gameId!!
        )?.enqueue(
            object : Callback<UserRegistrationResponseModel> {
                override fun onFailure(call: Call<UserRegistrationResponseModel>, t: Throwable) {
                    (activity as MainActivity).apply {
                        showToast(t.toString())
                        binding.progressbar.visibility = View.GONE
                    }
                }

                override fun onResponse(
                    call: Call<UserRegistrationResponseModel>,
                    response: Response<UserRegistrationResponseModel>
                ) {
                    if (response.isSuccessful) {
                        (activity as MainActivity).showToast(response.body()?.message!!)
                    } else {
                        (activity as MainActivity).showToast(response.toString())
                    }
                    (activity as MainActivity).binding.progressbar.visibility = View.GONE
                }
            }
        )
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
        imageIntent = registerForActivityResult(ActivityResultContracts.GetContent()) {

            screenshot_uri = it
            binding.numoffilestextview.text = "number of files choosen 1"
        }
    }
}