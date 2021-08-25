package com.example.scoreup.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scoreup.R
import com.example.scoreup.models.OngoingMatchDetails
import com.example.scoreup.ui.fragments.OngoingFragment
import kotlinx.android.synthetic.main.layout_items_ongoing_screen.view.*

class OngoingNewsAdapter(
    val context: Context,
    private val ongoingMatchDetails: ArrayList<OngoingMatchDetails>,
    private val fragment: OngoingFragment
) : RecyclerView.Adapter<OngoingNewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.layout_items_ongoing_screen,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return ongoingMatchDetails.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val match = ongoingMatchDetails[position]

        holder.score.text = match.score
        holder.matchType.text = match.match_type
        holder.teamsName.text = "${match.team_1} VS ${match.team_2}"
        if (match.location != "") {
            holder.location.visibility = View.VISIBLE
            holder.location.text = match.location
        } else {
            holder.location.visibility = View.GONE
        }
        if (match.other_details != "") {
            holder.otherDetails.visibility = View.VISIBLE
            holder.otherDetails.text = match.other_details
        } else {
            holder.otherDetails.visibility = View.GONE
        }
        holder.status.text = match.status
        holder.lastUpdated.text = "Last Updated: ${match.last_updated}"
        holder.sportsName.text = match.sport
    }

    fun clear() {
        ongoingMatchDetails.clear()
        notifyDataSetChanged()
    }

    fun addAll(ongoingUpdatedMatches: ArrayList<OngoingMatchDetails>) {
        ongoingMatchDetails.addAll(ongoingUpdatedMatches)

        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sportsName = view.tv_os_sport_name!!
        val score = view.tv_items_os_score!!
        val matchType = view.tv_items_os_match_type!!
        val teamsName = view.tv_items_os_teams_name!!
        val otherDetails = view.tv_items_os_recent_details!!
        val location = view.tv_items_os_location!!
        val status = view.tv_items_os_status!!
        val lastUpdated = view.tv_items_os_last_updated!!
    }
}