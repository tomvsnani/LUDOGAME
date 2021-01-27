package com.example.ludo

data class GameResultModelClass(
    var id: String,
    var host_id: String,
    var host_name: String,
    var host_result: String,
    var host_screenshot: String,
    var player_id: String,
    var player_name: String,
    var player_result: String,
    var player_screenshot: String,
    var game_id: String,
    var final_result: String

)

data class GameResultModelClassToSend(
    var id: String,

    var username: String,


    var player_type: String,

  var game_result:String,

    var game_id:String



)

data class GameResultModelClassResponse(
    var status: String,
    var message: String,
    var game_code: String,
    var data: List<GameResultModelClass>
)