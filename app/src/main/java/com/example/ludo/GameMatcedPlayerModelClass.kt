data class GameMatchedPlayerDetails(
    val data: List<Data>,
    val message: String,
    val status: String
)

data class Data(
   
    val email: String,
    val id: String,
    val lose_games: String,
    val otp_value: String,
    val profile_pic: String,
    val total_games: String,
    val updated_at: String,
    val user_phone: String,
    val username: String,
    val wallet: String,
    val win_games: String
)