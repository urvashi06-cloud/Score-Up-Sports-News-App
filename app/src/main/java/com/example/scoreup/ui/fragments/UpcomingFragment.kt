package com.example.scoreup.ui.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.scoreup.R
import com.example.scoreup.adapters.UpcomingNewsAdapter
import com.example.scoreup.firestore.FirestoreClass
import com.example.scoreup.models.UpcomingMatchDetails
import com.example.scoreup.utils.Constants
import com.example.scoreup.utils.MySingleton
import kotlinx.android.synthetic.main.fragment_news_ongoing.*
import kotlinx.android.synthetic.main.fragment_news_upcoming.*
import kotlinx.android.synthetic.main.fragment_news_upcoming.view.*
import kotlinx.android.synthetic.main.layout_items_upcoming_screen.*
import kotlinx.android.synthetic.main.layout_items_upcoming_screen.view.*
import org.json.JSONObject

class UpcomingFragment : BaseFragment() {

    private lateinit var mAdapter: UpcomingNewsAdapter
    private val mMatchDetails = ArrayList<UpcomingMatchDetails>()
    private lateinit var mSwipeRefreshContainer: SwipeRefreshLayout
    private var mSportsArray = ArrayList<String>()
    private lateinit var mPopupMenu: PopupMenu
    private var mSportsSelectedFromPopupMenu: Boolean = false
    private lateinit var mCurrentSport: String

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_news_upcoming, container, false)
        mSwipeRefreshContainer = root.srl_items_us
        mSwipeRefreshContainer.setOnRefreshListener {
            refreshData()
        }
        root.rv_upcoming_items.layoutManager = LinearLayoutManager(requireActivity())
        showProgressDialog(resources.getString(R.string.please_wait_text))
        FirestoreClass().getSportsPreferenceDetails(
            requireActivity(),
            fragment = this@UpcomingFragment,
            sportsSelected = mSportsSelectedFromPopupMenu
        )
        mAdapter = UpcomingNewsAdapter(requireActivity(), mMatchDetails, this)
        root.rv_upcoming_items.adapter = mAdapter

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        createNotificationChannel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.upcoming_matches_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_refresh -> {
                showProgressDialog(resources.getString(R.string.refreshing_text))
                refreshData()
            }
            R.id.item_sports_option_dropdown -> {

                mPopupMenu = PopupMenu(activity, activity?.findViewById(item.itemId))
                mPopupMenu.menuInflater.inflate(R.menu.name_of_selected_sports, mPopupMenu.menu)
                mSportsSelectedFromPopupMenu = true
                FirestoreClass().getSportsPreferenceDetails(
                    requireActivity(),
                    fragment = this@UpcomingFragment,
                    popupMenu = mPopupMenu,
                    sportsSelected = mSportsSelectedFromPopupMenu
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refreshData() {
        fetchDataFootball(mCurrentSport)
        mSwipeRefreshContainer.isRefreshing = false
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun successGetSportsPreferenceList(
        sports: ArrayList<ArrayList<String>>,
        popupMenu: PopupMenu?
    ) {
        mSportsArray = ArrayList(0)
        for (i in sports[0]) {
            mSportsArray.add(i)
        }
        showPopupMenu(mSportsArray, popupMenu)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun showPopupMenu(sports: ArrayList<String>, popupMenu: PopupMenu?) {

        popupMenu!!.menu.apply {
            for (i in 0 until sports.size) {
                val sportName: String = sports[i]
                val words = sportName.split(" ").toMutableList()

                var sport = ""

                for (word in words) {
                    sport += word.capitalize() + " "
                }

                sport = sport.trim()

                this.add(
                    Menu.NONE,
                    i,
                    i,
                    sport

                )
            }
        }
        popupMenu.setOnMenuItemClickListener { menuItem ->

            mCurrentSport = menuItem.title.toString()
            showProgressDialog(resources.getString(R.string.please_wait_text))
            fetchData(menuItem.title.toString())
            true
        }
        popupMenu.show()
    }

    private fun fetchData(sportName: String) {
        tv_upcoming_nothing_to_display_yet.visibility = View.GONE
        srl_items_us.visibility = View.GONE
        when (sportName) {
            "Basketball" -> {
                fetchDataBasketball(sportName)
            }
            "Football" -> {
                fetchDataFootball(sportName)
            }
            "Badminton" -> {
                fetchDataBadminton(sportName)
            }
            "Cricket" -> {
                fetchDataCricket(sportName)
            }
            "Swimming" -> {
                fetchDataSwimming(sportName)
            }
            "Table Tennis" -> {
                fetchDataTableTennis(sportName)
            }
            "Ice Hockey" -> {
                fetchDataIceHockey(sportName)
            }
            "Tennis" -> {
                fetchDataTennis(sportName)
            }
        }
    }

    fun successSportsNotSelected(sports: ArrayList<ArrayList<String>>) {
        val firstSport: String = sports[0][0]
        val words = firstSport.split(" ").toMutableList()

        var sport = ""

        for (word in words) {
            sport += word.capitalize() + " "
        }

        sport = sport.trim()

        mCurrentSport = sport

        fetchData(sport)
    }


    private fun fetchDataFootball(sportName: String) {
        val url =
            "https://livescore-api.com/api-client/scores/live.json&key=my0719jZg9oEVmD6&secret=ZpG81NFVNFDq0arnJsvLCOKSX8xTvaZv"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            {
                val detailsJsonArray = it.getJSONObject("data").getJSONArray("match")
                mAdapter.clear()
                for (i in 0 until detailsJsonArray.length()) {
                    val detailsJsonObject = detailsJsonArray.getJSONObject(i)
                    if (detailsJsonObject.getString("status") == "FINISHED") {
                        val matchDetails = UpcomingMatchDetails(
                            location = detailsJsonObject.getString("location"),
                            match_type = detailsJsonObject.getString("competition_name"),
                            team_1 = detailsJsonObject.getString("home_name"),
                            team_2 = detailsJsonObject.getString("away_name"),
                            starts_at = detailsJsonObject.getString("scheduled"),
                            sport = sportName
                        )
                        mMatchDetails.add(matchDetails)
                    }
                }
                if (mMatchDetails.isEmpty()) {
                    tv_upcoming_nothing_to_display_yet.visibility = View.VISIBLE
                    srl_items_us.visibility = View.GONE
                } else {
                    tv_upcoming_nothing_to_display_yet.visibility = View.GONE
                    srl_items_us.visibility = View.VISIBLE
                    mAdapter.addAll(mMatchDetails)
                }
                hideProgressDialog()
            },
            {
                hideProgressDialog()
                Toast.makeText(
                    activity,
                    "Matches cannot be loaded right now. Try again later.",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
        MySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest)
    }

    private fun fetchDataCricket(sportName: String) {
        mAdapter.clear()

        val url = ""

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener<JSONObject> {
                val detailsJsonArray = it.getJSONArray("Stages")
                for (i in 0 until detailsJsonArray.length()) {
                    val detailsJsonObject = detailsJsonArray.getJSONObject(i)

                    val matchDetails = UpcomingMatchDetails(
                        sport = sportName,
                        team_1 = detailsJsonObject.getString(""),
                        team_2 = detailsJsonObject.getString(""),
                        match_type = detailsJsonObject.getString("")
                    )
                    mMatchDetails.add(matchDetails)
                }
                if (mMatchDetails.isEmpty()) {
                    tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                    srl_items_os.visibility = View.GONE

                } else {
                    tv_ongoing_nothing_to_display_yet.visibility = View.GONE
                    srl_items_os.visibility = View.VISIBLE
                    mAdapter.addAll(mMatchDetails)
                }
                hideProgressDialog()
            },
            Response.ErrorListener {
                hideProgressDialog()
                Toast.makeText(
                    activity,
                    "Error while loading matches.",
                    Toast.LENGTH_LONG
                )
                    .show()
                Log.i("ErrorLoadingMatches", it.message.toString())
            }
        ) {
            //if any headers are required for the url, they have to be mentioned here.
            /*override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()
                headers["x-rapidapi-key"] = "85a6f660aemsha65c7b13a0273c7p19bb3djsn68604244df35"
                headers["x-rapidapi-host"] = "livescore6.p.rapidapi.com"

                return super.getHeaders()
            }*/
        }
        MySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest)
    }

    private fun fetchDataTennis(sportName: String) {
        mAdapter.clear()

        val url = ""

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener<JSONObject> {
                val detailsJsonArray = it.getJSONArray("Stages")
                for (i in 0 until detailsJsonArray.length()) {
                    val detailsJsonObject = detailsJsonArray.getJSONObject(i)

                    val matchDetails = UpcomingMatchDetails(
                        sport = sportName,
                        team_1 = detailsJsonObject.getString(""),
                        team_2 = detailsJsonObject.getString(""),
                        match_type = detailsJsonObject.getString("")
                    )
                    mMatchDetails.add(matchDetails)
                }
                if (mMatchDetails.isEmpty()) {
                    tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                    srl_items_os.visibility = View.GONE

                } else {
                    tv_ongoing_nothing_to_display_yet.visibility = View.GONE
                    srl_items_os.visibility = View.VISIBLE
                    mAdapter.addAll(mMatchDetails)
                }
                hideProgressDialog()
            },
            Response.ErrorListener {
                hideProgressDialog()
                Toast.makeText(
                    activity,
                    "Error while loading matches.",
                    Toast.LENGTH_LONG
                )
                    .show()
                Log.i("ErrorLoadingMatches", it.message.toString())
            }
        ) {
            //if any headers are required for the url, they have to be mentioned here.
            /*override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()
                headers["x-rapidapi-key"] = "85a6f660aemsha65c7b13a0273c7p19bb3djsn68604244df35"
                headers["x-rapidapi-host"] = "livescore6.p.rapidapi.com"

                return super.getHeaders()
            }*/
        }
        MySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest)
    }

    private fun fetchDataTableTennis(sportName: String) {
        mAdapter.clear()

        val url = ""

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener<JSONObject> {
                val detailsJsonArray = it.getJSONArray("Stages")
                for (i in 0 until detailsJsonArray.length()) {
                    val detailsJsonObject = detailsJsonArray.getJSONObject(i)

                    val matchDetails = UpcomingMatchDetails(
                        sport = sportName,
                        team_1 = detailsJsonObject.getString(""),
                        team_2 = detailsJsonObject.getString(""),
                        match_type = detailsJsonObject.getString("")
                    )
                    mMatchDetails.add(matchDetails)
                }
                if (mMatchDetails.isEmpty()) {
                    tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                    srl_items_os.visibility = View.GONE

                } else {
                    tv_ongoing_nothing_to_display_yet.visibility = View.GONE
                    srl_items_os.visibility = View.VISIBLE
                    mAdapter.addAll(mMatchDetails)
                }
                hideProgressDialog()
            },
            Response.ErrorListener {
                hideProgressDialog()
                Toast.makeText(
                    activity,
                    "Error while loading matches.",
                    Toast.LENGTH_LONG
                )
                    .show()
                Log.i("ErrorLoadingMatches", it.message.toString())
            }
        ) {
            //if any headers are required for the url, they have to be mentioned here.
            /*override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()
                headers["x-rapidapi-key"] = "85a6f660aemsha65c7b13a0273c7p19bb3djsn68604244df35"
                headers["x-rapidapi-host"] = "livescore6.p.rapidapi.com"

                return super.getHeaders()
            }*/
        }
        MySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest)
    }

    private fun fetchDataIceHockey(sportName: String) {
        mAdapter.clear()

        val url = ""

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener<JSONObject> {
                val detailsJsonArray = it.getJSONArray("Stages")
                for (i in 0 until detailsJsonArray.length()) {
                    val detailsJsonObject = detailsJsonArray.getJSONObject(i)

                    val matchDetails = UpcomingMatchDetails(
                        sport = sportName,
                        team_1 = detailsJsonObject.getString(""),
                        team_2 = detailsJsonObject.getString(""),
                        match_type = detailsJsonObject.getString("")
                    )
                    mMatchDetails.add(matchDetails)
                }
                if (mMatchDetails.isEmpty()) {
                    tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                    srl_items_os.visibility = View.GONE

                } else {
                    tv_ongoing_nothing_to_display_yet.visibility = View.GONE
                    srl_items_os.visibility = View.VISIBLE
                    mAdapter.addAll(mMatchDetails)
                }
                hideProgressDialog()
            },
            Response.ErrorListener {
                hideProgressDialog()
                Toast.makeText(
                    activity,
                    "Error while loading matches.",
                    Toast.LENGTH_LONG
                )
                    .show()
                Log.i("ErrorLoadingMatches", it.message.toString())
            }
        ) {
            //if any headers are required for the url, they have to be mentioned here.
            /*override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()
                headers["x-rapidapi-key"] = "85a6f660aemsha65c7b13a0273c7p19bb3djsn68604244df35"
                headers["x-rapidapi-host"] = "livescore6.p.rapidapi.com"

                return super.getHeaders()
            }*/
        }
        MySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest)
    }

    private fun fetchDataBadminton(sportName: String) {
        mAdapter.clear()

        val url = ""

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener<JSONObject> {
                val detailsJsonArray = it.getJSONArray("Stages")
                for (i in 0 until detailsJsonArray.length()) {
                    val detailsJsonObject = detailsJsonArray.getJSONObject(i)

                    val matchDetails = UpcomingMatchDetails(
                        sport = sportName,
                        team_1 = detailsJsonObject.getString(""),
                        team_2 = detailsJsonObject.getString(""),
                        match_type = detailsJsonObject.getString("")
                    )
                    mMatchDetails.add(matchDetails)
                }
                if (mMatchDetails.isEmpty()) {
                    tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                    srl_items_os.visibility = View.GONE

                } else {
                    tv_ongoing_nothing_to_display_yet.visibility = View.GONE
                    srl_items_os.visibility = View.VISIBLE
                    mAdapter.addAll(mMatchDetails)
                }
                hideProgressDialog()
            },
            Response.ErrorListener {
                hideProgressDialog()
                Toast.makeText(
                    activity,
                    "Error while loading matches.",
                    Toast.LENGTH_LONG
                )
                    .show()
                Log.i("ErrorLoadingMatches", it.message.toString())
            }
        ) {
            //if any headers are required for the url, they have to be mentioned here.
            /*override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()
                headers["x-rapidapi-key"] = "85a6f660aemsha65c7b13a0273c7p19bb3djsn68604244df35"
                headers["x-rapidapi-host"] = "livescore6.p.rapidapi.com"

                return super.getHeaders()
            }*/
        }
        MySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest)
    }

    private fun fetchDataBasketball(sportName: String) {
        mAdapter.clear()

        val url = ""

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener<JSONObject> {
                val detailsJsonArray = it.getJSONArray("Stages")
                for (i in 0 until detailsJsonArray.length()) {
                    val detailsJsonObject = detailsJsonArray.getJSONObject(i)

                    val matchDetails = UpcomingMatchDetails(
                        sport = sportName,
                        team_1 = detailsJsonObject.getString(""),
                        team_2 = detailsJsonObject.getString(""),
                        match_type = detailsJsonObject.getString("")
                    )
                    mMatchDetails.add(matchDetails)
                }
                if (mMatchDetails.isEmpty()) {
                    tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                    srl_items_os.visibility = View.GONE

                } else {
                    tv_ongoing_nothing_to_display_yet.visibility = View.GONE
                    srl_items_os.visibility = View.VISIBLE
                    mAdapter.addAll(mMatchDetails)
                }
                hideProgressDialog()
            },
            Response.ErrorListener {
                hideProgressDialog()
                Toast.makeText(
                    activity,
                    "Error while loading matches.",
                    Toast.LENGTH_LONG
                )
                    .show()
                Log.i("ErrorLoadingMatches", it.message.toString())
            }
        ) {
            //if any headers are required for the url, they have to be mentioned here.
            /*override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()
                headers["x-rapidapi-key"] = "85a6f660aemsha65c7b13a0273c7p19bb3djsn68604244df35"
                headers["x-rapidapi-host"] = "livescore6.p.rapidapi.com"

                return super.getHeaders()
            }*/
        }
        MySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest)
    }

    private fun fetchDataSwimming(sportName: String) {
        mAdapter.clear()

        val url = ""

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener<JSONObject> {
                val detailsJsonArray = it.getJSONArray("Stages")
                for (i in 0 until detailsJsonArray.length()) {
                    val detailsJsonObject = detailsJsonArray.getJSONObject(i)

                    val matchDetails = UpcomingMatchDetails(
                        sport = sportName,
                        team_1 = detailsJsonObject.getString(""),
                        team_2 = detailsJsonObject.getString(""),
                        match_type = detailsJsonObject.getString("")
                    )
                    mMatchDetails.add(matchDetails)
                }
                if (mMatchDetails.isEmpty()) {
                    tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                    srl_items_os.visibility = View.GONE

                } else {
                    tv_ongoing_nothing_to_display_yet.visibility = View.GONE
                    srl_items_os.visibility = View.VISIBLE
                    mAdapter.addAll(mMatchDetails)
                }
                hideProgressDialog()
            },
            Response.ErrorListener {
                hideProgressDialog()
                Toast.makeText(
                    activity,
                    "Error while loading matches.",
                    Toast.LENGTH_LONG
                )
                    .show()
                Log.i("ErrorLoadingMatches", it.message.toString())
            }
        ) {
            //if any headers are required for the url, they have to be mentioned here.
            /*override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()
                headers["x-rapidapi-key"] = "85a6f660aemsha65c7b13a0273c7p19bb3djsn68604244df35"
                headers["x-rapidapi-host"] = "livescore6.p.rapidapi.com"

                return super.getHeaders()
            }*/
        }
        MySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: String = resources.getString(R.string.notification_channel_name)
            val description: String = resources.getString(R.string.notification_channel_description)
            val importance: Int = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance)
            channel.description = description

            val notificationManager: NotificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun successAddMatchToReminder() {
        Toast.makeText(context, "Reminder has been set.", Toast.LENGTH_SHORT).show()
    }
}