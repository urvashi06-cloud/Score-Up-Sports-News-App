package com.example.scoreup.adapters

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.scoreup.R
import com.example.scoreup.firestore.FirestoreClass
import com.example.scoreup.models.RemindMatch
import com.example.scoreup.models.UpcomingMatchDetails
import com.example.scoreup.receivers.NotificationReceiver
import com.example.scoreup.ui.fragments.UpcomingFragment
import kotlinx.android.synthetic.main.layout_items_upcoming_screen.view.*
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

class UpcomingNewsAdapter(
    val context: Context,
    private val upcomingMatchDetails: ArrayList<UpcomingMatchDetails>,
    private val fragment: UpcomingFragment
) : RecyclerView.Adapter<UpcomingNewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    R.layout.layout_items_upcoming_screen,
                    parent,
                    false
                )
        )
    }

    override fun getItemCount(): Int {
        return upcomingMatchDetails.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val match = upcomingMatchDetails[position]

        if (match.location != "") {
            holder.location.visibility = View.VISIBLE
            holder.location.text = match.location
        } else {
            holder.location.visibility = View.GONE
        }
        holder.matchType.text = match.match_type
        holder.startsAt.text = "Starts At: ${match.starts_at}"
        holder.teamsName.text = "${match.team_1} VS ${match.team_2}"
        //holder.team2Name.text = match.team_2
        holder.setReminderOption.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.menuInflater.inflate(R.menu.upcoming_match_item_options, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.item_set_reminder -> {
                        if (!match.reminder) {
                            setReminder(match.starts_at, match)
                        } else {
                            Toast.makeText(
                                context,
                                "Reminder for this match has already been set.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
            popupMenu.show()
        }
        holder.sportName.text = match.sport
    }

    private fun setReminder(scheduledTime: String?, matchDetails: UpcomingMatchDetails) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val calendar = Calendar.getInstance()
            val date = LocalDate.now()
            calendar.set(Calendar.YEAR, date.year)
            calendar.set(Calendar.MONTH, date.monthValue)
            calendar.set(Calendar.DAY_OF_MONTH, date.dayOfMonth)
            calendar.set(Calendar.HOUR_OF_DAY, scheduledTime?.substring(0, 2)!!.toInt())
            if (scheduledTime.substring(0, 2).toInt() > 11) {
                calendar.set(Calendar.AM_PM, Calendar.PM)
            } else {
                calendar.set(Calendar.AM_PM, Calendar.AM)
            }
            calendar.set(Calendar.MINUTE, scheduledTime.substring(3, 5).toInt())
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            Log.i("Time", Calendar.getInstance().time.toString())
            Log.i("Time", calendar.time.toString())

            if (Calendar.getInstance().time < calendar.time) {
                val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

                val intent = Intent(context, NotificationReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)


                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )

                successReminderSet(matchDetails)
            } else {
                Toast.makeText(context, "This match has already started.", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(
                context,
                "Cannot set reminders with this version of android. PLease update your android version.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun successReminderSet(matchDetails: UpcomingMatchDetails) {
        val remindMatch = RemindMatch(
            match_type = matchDetails.match_type,
            team_1 = matchDetails.team_1,
            team_2 = matchDetails.team_2,
            starts_at = matchDetails.starts_at,
            user_id = FirestoreClass().getCurrentUserId(),
            sport = matchDetails.sport
        )
        matchDetails.reminder = true

        FirestoreClass().addMatchToReminder(fragment, remindMatch)
    }

    fun clear() {
        upcomingMatchDetails.clear()
        notifyDataSetChanged()
    }

    fun addAll(upcomingUpdatedMatches: ArrayList<UpcomingMatchDetails>) {
        upcomingMatchDetails.addAll(upcomingUpdatedMatches)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sportName = view.tv_us_sport_name!!
        val teamsName = view.tv_items_us_teams_name!!
        val location = view.tv_items_us_location!!
        val startsAt = view.tv_items_us_starts_at!!
        val matchType = view.tv_items_us_match_type!!
        val setReminderOption = view.iv_items_us_options!!
    }
}