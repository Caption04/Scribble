package com.example.scribble

import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

private  var currentMediaPlayer: MediaPlayer? = null
private var currentAudioMessage: Message? = null
private var currentPlayButton: Button? = null
private var currentSeekBar: SeekBar? = null
@Suppress("DEPRECATION")
class MessageAdapter(private val messages: MutableList<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    // Inner class to hold the TextView for each message
    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.message_text)
        val botImageView: ImageView? = itemView.findViewById(R.id.bot_image)
        val botVideoView: VideoView? = itemView.findViewById(R.id.bot_video)

//        Audio Widgets & layouts
        val audioLayout: LinearLayout? = itemView.findViewById(R.id.audioLayout)
        val playButton: Button? = itemView.findViewById(R.id.playButton)
        val audioSeekerBar: SeekBar? = itemView.findViewById(R.id.audioSeekerBar)


    }

    fun addMessage(message: Message) {
        messages.add(message)           // add new message to list
        notifyItemInserted(messages.size - 1)  // tell RecyclerView a new item is added
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        // Inflate the proper layout based on viewType (user or bot)
        val layoutId = if (viewType == 0) R.layout.item_message_user else R.layout.item_message_bot
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return MessageViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
//        Hide everything first
        holder.messageText.visibility = View.GONE
        holder.botImageView?.visibility = View.GONE
        holder.botVideoView?.visibility = View.GONE

        message.text?.let{
            holder.messageText.text = it
            holder.messageText.visibility = View.VISIBLE
        }
        message.imageResId?.let{
            resId -> holder.botImageView?.setImageResource(resId)
            holder.botImageView?.visibility= View.VISIBLE
        }
        message.videoResId?.let{
            resId -> holder.botVideoView?.setVideoPath("android.resource://${holder.itemView.context.packageName}/$resId")
            holder.botVideoView?.visibility = View.VISIBLE
            holder.botVideoView?.start()
        }

        if(message.audioResId != null){
           holder.audioLayout?.visibility = View.VISIBLE
            val playButton = holder.playButton
            val seekBar = holder.audioSeekerBar

//            Created media player
            val mediaPlayer = MediaPlayer.create(holder.itemView.context, message.audioResId)

//            Audio focus to pause device audio
            val audioManager = holder.itemView.context.getSystemService(android.content.Context.AUDIO_SERVICE) as AudioManager
            val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)


                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
                )
                .setOnAudioFocusChangeListener { focusChange -> when (focusChange){
                    AudioManager.AUDIOFOCUS_LOSS,
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                        if(mediaPlayer.isPlaying){
                            mediaPlayer.pause()
                            playButton?.text = "Play"
                        }
                    }
                    AudioManager.AUDIOFOCUS_GAIN -> {}
                } }.build()
//            Play and Pause Button
            playButton?.setOnClickListener {
                if(mediaPlayer.isPlaying){
                    mediaPlayer.pause()
                    playButton?.text = "Play"
                }else{
                    val result = audioManager.requestAudioFocus(focusRequest)
                    if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                        mediaPlayer.start()
                        playButton?.text = "Pause"
                    }
                }
            }
//            Update Seek Bar while playing
            seekBar?.max = mediaPlayer.duration
            val handler = android.os.Handler()
            val updateSeekBar = object : Runnable{
                override fun run() {
                    seekBar?.progress = mediaPlayer.currentPosition
                    handler.postDelayed(this, 500)
                }
            }
            mediaPlayer.setOnPreparedListener {
                handler.post(updateSeekBar)
            }

            mediaPlayer.setOnCompletionListener {
                playButton?.text = "Play"
                seekBar?.progress = 0
                audioManager.abandonAudioFocusRequest(focusRequest)
            }

//            SeekBar fast forward and Rewind
            seekBar?.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if(fromUser) mediaPlayer.seekTo(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {  }
            })
        }else {
            holder.audioLayout?.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) 0 else 1
    }
}

private fun AudioFocusRequest.Builder.setAudioAttributes(attributes: AudioAttributes.Builder) {}
