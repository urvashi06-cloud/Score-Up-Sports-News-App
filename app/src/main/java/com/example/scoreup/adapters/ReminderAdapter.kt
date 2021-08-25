package com.example.scoreup.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.scoreup.R
import com.example.scoreup.firestore.FirestoreClass
import com.example.scoreup.models.RemindMatch
import com.example.scoreup.ui.activities.ReminderActivity
import kotlinx.android.synthetic.main.layout_items_reminder_screen.view.*

class ReminderAdapter(
    val context: Context,
    private val matchDetails: ArrayList<RemindMatch>,
    private val activity: ReminderActivity
) : RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.layout_items_reminder_screen,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val match = matchDetails[position]

        holder.sportsName.text = match.sport
        holder.matchType.text = match.match_type
        holder.teamsName.text = "${match.team_1} VS ${match.team_2}"
        holder.startsAt.text = "Starts At: ${match.starts_at}"
        holder.cancelButton.setOnClickListener {
            FirestoreClass().removeMatchFromRemindMatch(activity, match)
        }
    }

    override fun getItemCount(): Int {
        return matchDetails.size
    }

    fun clear() {
        matchDetails.clear()
        notifyDataSetChanged()
    }

    fun addAll(remindMatches: ArrayList<RemindMatch>) {
        matchDetails.addAll(remindMatches)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sportsName = view.tv_rs_sport_name!!
        val matchType = view.tv_items_rs_match_type!!
        val teamsName = view.tv_items_rs_teams_name!!
        val startsAt = view.tv_items_rs_starts_at!!
        val cancelButton = view.btn_items_rs_reminder_cancel!!
    }

}