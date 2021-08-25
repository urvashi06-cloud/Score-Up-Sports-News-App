package com.example.scoreup.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android.volley.Request
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.scoreup.R
import com.example.scoreup.adapters.OngoingNewsAdapter
import com.example.scoreup.firestore.FirestoreClass
import com.example.scoreup.models.OngoingMatchDetails
import com.example.scoreup.utils.MySingleton
import kotlinx.android.synthetic.main.fragment_news_ongoing.*
import kotlinx.android.synthetic.main.fragment_news_ongoing.view.*
import kotlinx.android.synthetic.main.fragment_news_upcoming.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class OngoingFragment : BaseFragment() {

    private var mMatchDetailsArray = ArrayList<OngoingMatchDetails>()
    private lateinit var mAdapter: OngoingNewsAdapter
    private lateinit var mSwipeContainer: SwipeRefreshLayout
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
        Log.i("LifecycleCheck", "OnCreateView")
        val root = inflater.inflate(R.layout.fragment_news_ongoing, container, false)
        val rv = root.rv_ongoing_items
        mSwipeContainer = root.srl_items_os

        mSwipeContainer.setOnRefreshListener {
            refreshData()
        }
        showProgressDialog(resources.getString(R.string.please_wait_text))
        rv.layoutManager = LinearLayoutManager(activity)
        FirestoreClass().getSportsPreferenceDetails(
            requireActivity(),
            fragment = this@OngoingFragment,
            sportsSelected = mSportsSelectedFromPopupMenu
        )
        mAdapter = OngoingNewsAdapter(requireActivity(), mMatchDetailsArray, this)
        rv.adapter = mAdapter
        return root
    }

    private fun refreshData() {
        fetchData(mCurrentSport)
        mSwipeContainer.isRefreshing = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.ongoing_matches_menu, menu)
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
                    fragment = this@OngoingFragment,
                    popupMenu = mPopupMenu,
                    sportsSelected = mSportsSelectedFromPopupMenu
                )
            }

        }
        return super.onOptionsItemSelected(item)
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
        tv_ongoing_nothing_to_display_yet.visibility = View.GONE
        srl_items_os.visibility = View.GONE
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

                    val matchDetails = OngoingMatchDetails(
                        sport = sportName,
                        team_1 = detailsJsonObject.getString(""),
                        team_2 = detailsJsonObject.getString(""),
                        match_type = detailsJsonObject.getString("")
                    )
                    mMatchDetailsArray.add(matchDetails)
                }
                if (mMatchDetailsArray.isEmpty()) {
                    tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                    srl_items_os.visibility = View.GONE

                } else {
                    tv_ongoing_nothing_to_display_yet.visibility = View.GONE
                    srl_items_os.visibility = View.VISIBLE
                    mAdapter.addAll(mMatchDetailsArray)
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

                tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                srl_items_os.visibility = View.GONE
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
        val url =
            "https://www.thesportsdb.com/api/v2/json/40130162/livescore.php?s=Ice_Hockey"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            {
                mAdapter.clear()
                val detailsJson = it.get("events")
                if (detailsJson.toString() != "null") {
                    val detailsJsonArray = it.getJSONArray("events")
                    for (i in 0 until detailsJsonArray.length()) {
                        val detailsJsonObject = detailsJsonArray.getJSONObject(i)
                        val matchDetails = OngoingMatchDetails(
                            match_type = detailsJsonObject.getString("strLeague"),
                            team_1 = detailsJsonObject.getString("strHomeTeam"),
                            team_2 = detailsJsonObject.getString("strAwayTeam"),
                            score = "${detailsJsonObject.getString("intHomeScore")} - ${
                                detailsJsonObject.getString(
                                    "intAwayScore"
                                )
                            }",
                            //location = detailsJsonObject.getString("location"),
                            status = detailsJsonObject.getString("strStatus"),
                            //other_details = detailsJsonObject.getString("events"),
                            last_updated = detailsJsonObject.getString("updated"),
                            sport = sportName
                        )
                        mMatchDetailsArray.add(matchDetails)
                    }
                    if (mMatchDetailsArray.isEmpty()) {
                        tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                        srl_items_os.visibility = View.GONE
                    } else {
                        tv_ongoing_nothing_to_display_yet.visibility = View.GONE
                        srl_items_os.visibility = View.VISIBLE
                        mAdapter.addAll(mMatchDetailsArray)
                    }
                }
                else{
                    tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                    srl_items_os.visibility = View.GONE
                }
                hideProgressDialog()
            }, {

                hideProgressDialog()
                Toast.makeText(
                    activity,
                    "Error while loading matches.",
                    Toast.LENGTH_LONG
                )
                    .show()
                Log.i("ErrorLoadingMatches", it.message.toString())
                tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                srl_items_os.visibility = View.GONE
            }
        )
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

                    val matchDetails = OngoingMatchDetails(
                        sport = sportName,
                        team_1 = detailsJsonObject.getString(""),
                        team_2 = detailsJsonObject.getString(""),
                        match_type = detailsJsonObject.getString("")
                    )
                    mMatchDetailsArray.add(matchDetails)
                }
                if (mMatchDetailsArray.isEmpty()) {
                    tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                    srl_items_os.visibility = View.GONE

                } else {
                    tv_ongoing_nothing_to_display_yet.visibility = View.GONE
                    srl_items_os.visibility = View.VISIBLE
                    mAdapter.addAll(mMatchDetailsArray)
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
                tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                srl_items_os.visibility = View.GONE
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

                    val matchDetails = OngoingMatchDetails(
                        sport = sportName,
                        team_1 = detailsJsonObject.getString(""),
                        team_2 = detailsJsonObject.getString(""),
                        match_type = detailsJsonObject.getString("")
                    )
                    mMatchDetailsArray.add(matchDetails)
                }
                if (mMatchDetailsArray.isEmpty()) {
                    tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                    srl_items_os.visibility = View.GONE

                } else {
                    tv_ongoing_nothing_to_display_yet.visibility = View.GONE
                    srl_items_os.visibility = View.VISIBLE
                    mAdapter.addAll(mMatchDetailsArray)
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
                tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                srl_items_os.visibility = View.GONE
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

    private fun fetchDataCricket(sportName: String) {
        mAdapter.clear()

        val url = "https://livescore6.p.rapidapi.com/matches/v2/list-live?Category=cricket"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener<JSONObject> {
                val detailsJsonArray = it.getJSONArray("Stages")
                for (i in 0 until detailsJsonArray.length()) {
                    val detailsJsonObject = detailsJsonArray.getJSONObject(i)

                    val matchDetails = OngoingMatchDetails(
                        sport = sportName,
                        team_1 = detailsJsonObject.getString(""),
                        team_2 = detailsJsonObject.getString(""),
                        match_type = detailsJsonObject.getString("")
                    )
                    mMatchDetailsArray.add(matchDetails)
                }
                if (mMatchDetailsArray.isEmpty()) {
                    tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                    srl_items_os.visibility = View.GONE

                } else {
                    tv_ongoing_nothing_to_display_yet.visibility = View.GONE
                    srl_items_os.visibility = View.VISIBLE
                    mAdapter.addAll(mMatchDetailsArray)
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
                tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                srl_items_os.visibility = View.GONE
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()
                headers["x-rapidapi-key"] = "85a6f660aemsha65c7b13a0273c7p19bb3djsn68604244df35"
                headers["x-rapidapi-host"] = "livescore6.p.rapidapi.com"


                return super.getHeaders()
            }
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

                    val matchDetails = OngoingMatchDetails(
                        sport = sportName,
                        team_1 = detailsJsonObject.getString(""),
                        team_2 = detailsJsonObject.getString(""),
                        match_type = detailsJsonObject.getString("")
                    )
                    mMatchDetailsArray.add(matchDetails)
                }
                if (mMatchDetailsArray.isEmpty()) {
                    tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                    srl_items_os.visibility = View.GONE

                } else {
                    tv_ongoing_nothing_to_display_yet.visibility = View.GONE
                    srl_items_os.visibility = View.VISIBLE
                    mAdapter.addAll(mMatchDetailsArray)
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
                tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                srl_items_os.visibility = View.GONE
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
        val url =
            "https://www.thesportsdb.com/api/v2/json/40130162/livescore.php?s=Basketball"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            {

                val detailsJson = it.get("events")
                if (detailsJson.toString() != "null") {
                    val detailsJsonArray = it.getJSONArray("events")
                    for (i in 0 until detailsJsonArray.length()) {
                        val detailsJsonObject = detailsJsonArray.getJSONObject(i)
                        val matchDetails = OngoingMatchDetails(
                            match_type = detailsJsonObject.getString("strLeague"),
                            team_1 = detailsJsonObject.getString("strHomeTeam"),
                            team_2 = detailsJsonObject.getString("strAwayTeam"),
                            score = "${detailsJsonObject.getString("intHomeScore")} - ${
                                detailsJsonObject.getString(
                                    "intAwayScore"
                                )
                            }",
                            //location = detailsJsonObject.getString("location"),
                            status = detailsJsonObject.getString("strStatus"),
                            //other_details = detailsJsonObject.getString("events"),
                            last_updated = detailsJsonObject.getString("updated"),
                            sport = sportName
                        )
                        mMatchDetailsArray.add(matchDetails)
                    }
                    if (mMatchDetailsArray.isEmpty()) {
                        tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                        srl_items_os.visibility = View.GONE
                    } else {
                        tv_ongoing_nothing_to_display_yet.visibility = View.GONE
                        srl_items_os.visibility = View.VISIBLE
                        mAdapter.addAll(mMatchDetailsArray)
                    }
                }
                else{
                    tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                    srl_items_os.visibility = View.GONE
                }
                hideProgressDialog()
            }, {

                hideProgressDialog()
                Toast.makeText(
                    activity,
                    "Error while loading matches.",
                    Toast.LENGTH_LONG
                )
                    .show()
                Log.i("ErrorLoadingMatches", it.message.toString())
                tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                srl_items_os.visibility = View.GONE
            }
        )
        MySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest)
    }

    private fun fetchDataFootball(sportName: String) {
        mAdapter.clear()
        val url =
            "https://www.thesportsdb.com/api/v2/json/40130162/livescore.php?s=Soccer"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            {
                val detailsJson = it.get("events")
                if(detailsJson.toString() != "null") {
                    val detailsJsonArray = it.getJSONArray("events")
                    for (i in 0 until detailsJsonArray.length()) {
                        val detailsJsonObject = detailsJsonArray.getJSONObject(i)
                        val matchDetails = OngoingMatchDetails(
                            match_type = detailsJsonObject.getString("strLeague"),
                            team_1 = detailsJsonObject.getString("strHomeTeam"),
                            team_2 = detailsJsonObject.getString("strAwayTeam"),
                            score = "${detailsJsonObject.getString("intHomeScore")} - ${
                                detailsJsonObject.getString(
                                    "intAwayScore"
                                )
                            }",
                            //location = detailsJsonObject.getString("location"),
                            status = detailsJsonObject.getString("strStatus"),
                            //other_details = detailsJsonObject.getString("events"),
                            last_updated = detailsJsonObject.getString("updated"),
                            sport = sportName
                        )
                        mMatchDetailsArray.add(matchDetails)
                    }


                    if (mMatchDetailsArray.isEmpty()) {
                        tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                        srl_items_os.visibility = View.GONE
                    } else {
                        tv_ongoing_nothing_to_display_yet.visibility = View.GONE
                        srl_items_os.visibility = View.VISIBLE
                        mAdapter.addAll(mMatchDetailsArray)
                    }
                } else{
                    tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                    srl_items_os.visibility = View.GONE
                }
                hideProgressDialog()
            }, {

                hideProgressDialog()
                Toast.makeText(
                    activity,
                    "Error while loading matches.",
                    Toast.LENGTH_LONG
                )
                    .show()
                Log.i("ErrorLoadingMatches", it.message.toString())
                tv_ongoing_nothing_to_display_yet.visibility = View.VISIBLE
                srl_items_os.visibility = View.GONE
            }
        )
        MySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest)
    }

}