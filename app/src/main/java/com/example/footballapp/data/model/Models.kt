import com.google.gson.annotations.SerializedName

// ========== Team ==========
data class TeamResponse(
    @SerializedName("teams") val teams: List<Team>?
)

data class Team(
    @SerializedName("idTeam") val idTeam: String?,
    @SerializedName("strTeam") val strTeam: String?,
    @SerializedName("strTeamShort") val strTeamShort: String?,
    @SerializedName("strAlternate") val strAlternate: String?,
    @SerializedName("intFormedYear") val intFormedYear: String?,
    @SerializedName("strSport") val strSport: String?,
    @SerializedName("strLeague") val strLeague: String?,
    @SerializedName("idLeague") val idLeague: String?,
    @SerializedName("strLeague2") val strLeague2: String?,
    @SerializedName("strStadium") val strStadium: String?,
    @SerializedName("strStadiumLocation") val strStadiumLocation: String?,
    @SerializedName("intStadiumCapacity") val intStadiumCapacity: String?,
    @SerializedName("strWebsite") val strWebsite: String?,
    @SerializedName("strFacebook") val strFacebook: String?,
    @SerializedName("strTwitter") val strTwitter: String?,
    @SerializedName("strInstagram") val strInstagram: String?,
    @SerializedName("strDescriptionEN") val strDescriptionEN: String?,
    @SerializedName("strCountry") val strCountry: String?,
    @SerializedName("strTeamBadge") val strTeamBadge: String?,
    @SerializedName("strTeamJersey") val strTeamJersey: String?,
    @SerializedName("strTeamLogo") val strTeamLogo: String?,
    @SerializedName("strTeamFanart1") val strTeamFanart1: String?,
    @SerializedName("strTeamFanart2") val strTeamFanart2: String?,
    @SerializedName("strTeamFanart3") val strTeamFanart3: String?,
    @SerializedName("strTeamFanart4") val strTeamFanart4: String?,
    @SerializedName("strTeamBanner") val strTeamBanner: String?,
    @SerializedName("strYoutube") val strYoutube: String?
)

// ========== Event / Match ==========
data class EventResponse(
    @SerializedName("events") val events: List<Event>?,
    @SerializedName("event") val event: List<Event>? // some endpoints use this
)

data class Event(
    @SerializedName("idEvent") val idEvent: String?,
    @SerializedName("strEvent") val strEvent: String?,
    @SerializedName("strEventAlternate") val strEventAlternate: String?,
    @SerializedName("strFilename") val strFilename: String?,
    @SerializedName("strSport") val strSport: String?,
    @SerializedName("idLeague") val idLeague: String?,
    @SerializedName("strLeague") val strLeague: String?,
    @SerializedName("strSeason") val strSeason: String?,
    @SerializedName("strDescriptionEN") val strDescriptionEN: String?,
    @SerializedName("strHomeTeam") val strHomeTeam: String?,
    @SerializedName("strAwayTeam") val strAwayTeam: String?,
    @SerializedName("intHomeScore") val intHomeScore: String?,
    @SerializedName("intAwayScore") val intAwayScore: String?,
    @SerializedName("intRound") val intRound: String?,
    @SerializedName("intSpectators") val intSpectators: String?,
    @SerializedName("strOfficial") val strOfficial: String?,
    @SerializedName("strTimestamp") val strTimestamp: String?,
    @SerializedName("dateEvent") val dateEvent: String?,
    @SerializedName("dateEventLocal") val dateEventLocal: String?,
    @SerializedName("strTime") val strTime: String?,
    @SerializedName("strTimeLocal") val strTimeLocal: String?,
    @SerializedName("strTVStation") val strTVStation: String?,
    @SerializedName("idHomeTeam") val idHomeTeam: String?,
    @SerializedName("idAwayTeam") val idAwayTeam: String?,
    @SerializedName("strResult") val strResult: String?,
    @SerializedName("strVenue") val strVenue: String?,
    @SerializedName("strCountry") val strCountry: String?,
    @SerializedName("strCity") val strCity: String?,
    @SerializedName("strPoster") val strPoster: String?,
    @SerializedName("strSquare") val strSquare: String?,
    @SerializedName("strFanart") val strFanart: String?,
    @SerializedName("strThumb") val strThumb: String?,
    @SerializedName("strBanner") val strBanner: String?,
    @SerializedName("strMap") val strMap: String?,
    @SerializedName("strTweet1") val strTweet1: String?,
    @SerializedName("strTweet2") val strTweet2: String?,
    @SerializedName("strTweet3") val strTweet3: String?,
    @SerializedName("strVideo") val strVideo: String?,
    @SerializedName("strStatus") val strStatus: String?,
    @SerializedName("strPostponed") val strPostponed: String?,
    @SerializedName("strLocked") val strLocked: String?
)

// ========== League Table ==========
data class TableResponse(
    @SerializedName("table") val table: List<TableEntry>?
)

data class TableEntry(
    @SerializedName("idStanding") val idStanding: String?,
    @SerializedName("intRank") val intRank: String?,
    @SerializedName("idTeam") val idTeam: String?,
    @SerializedName("strTeam") val strTeam: String?,
    @SerializedName("strBadge") val strBadge: String?,
    @SerializedName("idLeague") val idLeague: String?,
    @SerializedName("strLeague") val strLeague: String?,
    @SerializedName("strSeason") val strSeason: String?,
    @SerializedName("strForm") val strForm: String?,
    @SerializedName("strDescription") val strDescription: String?,
    @SerializedName("intPlayed") val intPlayed: String?,
    @SerializedName("intWin") val intWin: String?,
    @SerializedName("intLoss") val intLoss: String?,
    @SerializedName("intDraw") val intDraw: String?,
    @SerializedName("intGoalsFor") val intGoalsFor: String?,
    @SerializedName("intGoalsAgainst") val intGoalsAgainst: String?,
    @SerializedName("intGoalDifference") val intGoalDifference: String?,
    @SerializedName("intPoints") val intPoints: String?,
    @SerializedName("dateUpdated") val dateUpdated: String?
)

// ========== Player ==========
data class PlayerResponse(
    @SerializedName("player") val player: List<Player>?
)

data class Player(
    @SerializedName("idPlayer") val idPlayer: String?,
    @SerializedName("strPlayer") val strPlayer: String?,
    @SerializedName("strNationality") val strNationality: String?,
    @SerializedName("strTeam") val strTeam: String?,
    @SerializedName("idTeam") val idTeam: String?,
    @SerializedName("strSport") val strSport: String?,
    @SerializedName("dateBorn") val dateBorn: String?,
    @SerializedName("strNumber") val strNumber: String?,
    @SerializedName("dateSigned") val dateSigned: String?,
    @SerializedName("strSigning") val strSigning: String?,
    @SerializedName("strWage") val strWage: String?,
    @SerializedName("strOutfitter") val strOutfitter: String?,
    @SerializedName("strKit") val strKit: String?,
    @SerializedName("strAgent") val strAgent: String?,
    @SerializedName("strBirthLocation") val strBirthLocation: String?,
    @SerializedName("strDescriptionEN") val strDescriptionEN: String?,
    @SerializedName("strGender") val strGender: String?,
    @SerializedName("strSide") val strSide: String?,
    @SerializedName("strPosition") val strPosition: String?,
    @SerializedName("strCollege") val strCollege: String?,
    @SerializedName("strFacebook") val strFacebook: String?,
    @SerializedName("strWebsite") val strWebsite: String?,
    @SerializedName("strTwitter") val strTwitter: String?,
    @SerializedName("strInstagram") val strInstagram: String?,
    @SerializedName("strYoutube") val strYoutube: String?,
    @SerializedName("strHeight") val strHeight: String?,
    @SerializedName("strWeight") val strWeight: String?,
    @SerializedName("intLoved") val intLoved: String?,
    @SerializedName("strThumb") val strThumb: String?,
    @SerializedName("strCutout") val strCutout: String?,
    @SerializedName("strRender") val strRender: String?,
    @SerializedName("strBanner") val strBanner: String?,
    @SerializedName("strFanart1") val strFanart1: String?,
    @SerializedName("strFanart2") val strFanart2: String?,
    @SerializedName("strFanart3") val strFanart3: String?,
    @SerializedName("strFanart4") val strFanart4: String?,
    @SerializedName("strCreativeCommons") val strCreativeCommons: String?,
    @SerializedName("strLocked") val strLocked: String?
)

// ========== League ==========
data class LeagueResponse(
    @SerializedName("leagues") val leagues: List<League>?
)

data class League(
    @SerializedName("idLeague") val idLeague: String?,
    @SerializedName("strLeague") val strLeague: String?,
    @SerializedName("strSport") val strSport: String?,
    @SerializedName("strLeagueAlternate") val strLeagueAlternate: String?
)
