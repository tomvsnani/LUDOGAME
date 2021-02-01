package com.example.ludo

data class GameResultModelClass(
    var id: String? = null,
    var host_id: String? = null,
    var host_name: String? = null,
    var host_result: String? = null,
    var host_screenshot: String? = null,
    var player_id: String? = null,
    var player_name: String? = null,
    var player_result: String? = null,
    var player_screenshot: String? = null,
    var game_id: String? = null,
    var final_result: String? = null

)

data class GameResultModelClassToSend(
    var id: String?,

    var username: String?,


    var player_type: String?,

    var game_result: String?,

    var game_id: String?


)

data class GameResultModelClassResponse(
    var status: String?,
    var message: String?,
    var game_code: String?,
    var data: List<GameResultModelClass>?
)