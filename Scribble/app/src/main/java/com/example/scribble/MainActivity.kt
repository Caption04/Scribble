package com.example.scribble

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView

    private lateinit var inputLayout: LinearLayout
    private lateinit var userInput: EditText
    private lateinit var sendButton: Button
    //Dynamic Buttons
    private lateinit var buttonContainer: LinearLayout
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1) Find views
        buttonContainer = findViewById(R.id.buttonContainer)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        inputLayout = findViewById(R.id.inputLayout)
        userInput = findViewById(R.id.userInput)
        sendButton = findViewById(R.id.sendButton)

        // 2) Setup RecyclerView (stack from end so it behaves like chat)
        val layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        chatRecyclerView.layoutManager = layoutManager

        // 3) Adapter
        adapter = MessageAdapter(messages)
        chatRecyclerView.adapter = adapter

        sendButton.setOnClickListener {
            val text = userInput.text.toString().trim()
            if (text.isNotEmpty()){
                adapter.addMessage(Message(text = text, isUser = true))
                val reply = getBotResponse(text)
                adapter.addMessage(Message(text = reply, isUser = false))
                chatRecyclerView.scrollToPosition(messages.size - 1)
                userInput.text.clear()
                inputLayout.visibility = View.GONE
                responseButtons()
            }

        }

    }


    val options = listOf("Great", "Okay", "Horrible")
    val options2 = mapOf(
        "Great" to listOf("I accomplished something", "I love my physique"),
        "Okay" to listOf("Nothing Much broski", "Just a normal day", "A bit Stressed"),
        "Horrible" to listOf("I feel like quitting", "I feel lost and scared", "I feel alone", "I feel far from God")
    )

    val positive = mapOf(
        "I accomplished something" to listOf("Reached 100 posts on IG", "Built 100 Websites", "Finished a Book", "Worked out today", "Prayed and read my bible")
    )

    val mild = mapOf(
        "A bit Stressed" to listOf("Trying to reach IG Goal", "Trying to reach Website Goal", "Struggling to focus","Don't feel like going to gym")
    )

    val negative = mapOf(
        "I feel Like quitting" to listOf("No outreach replies", "pressure from family", "I don't feel like I'm enough"),
        "I feel lost and scared" to listOf("There are no results", "There's no support", "No one cares"),
        "I feel alone" to listOf("I don't have any support", "Burden of failure feels heavy", "No one to confide in"),
        "I feel far from God" to listOf("Nothing is working out")
    )


    private fun getBotResponse(message : String): String{
       return "Yo my G, how are you feeling in this moment? Saw your message"
    }

    private fun responses(message:String){
        val botReply = when(message){
            options[0] -> "I'm glad to hear Brudda. What's got you so elevated?"
            options[1] -> "Average day? I hear you. What's on the agenda?"
            options[2] -> "It's okay to feel low some days bro , what's the problem?"
//            Positive Responses
            options2["Great"]?.get(0) -> "What did you accomplish King?"
            options2["Great"]?.get(1) -> "That's whats up, get that body in shape!!"
            positive["I accomplished something"]?.get(0) -> "Dude 100 fucking posts!! I remember how stressful that was, keep pushing man God bless you. You're on track trust me!!"
            positive["I accomplished something"]?.get(1) -> "Nah you did it bro. 100 Websites you animal. That's the confidence you need. Dude you're on the way brudda!!"
            positive["I accomplished something"]?.get(2) -> "Nice broski, get that wisdom!"
            positive["I accomplished something"]?.get(3) -> "Yessirr gotta maintain that temple"
            positive["I accomplished something"]?.get(4) -> "Yes seek His heart Kuhlinji and He'll make your ways straight. Love that for you king!"
//            Mild responses
            options2["Okay"]?.get(0) -> "Ay, standard day, keep pushing"
            options2["Okay"]?.get(1) -> "That's how it is bro keep going"
            options2["Okay"]?.get(2) -> "What's up?"
            mild["A bit Stressed"]?.get(0) -> "Ay dude I remember how hard that was, the algorithm doesn't push your content and no engagement. Nah that doesn't faze us brudda it keeps better, trust me!"
            mild["A bit Stressed"]?.get(1) -> "I remember the difficulty. Trying to stay focused as well as not receiving replies on outreaches but this is the hard we picked. There's so much life here on the other side bro just keep going!!"
            mild["A bit Stressed"]?.get(2) -> "Ey man, ey man. It do be like that on some days. But you're a household leader bro so we show up and keep moving. Just use the time auditing method and stick to the plan"
            mild["A bit Stressed"]?.get(3) -> "nah nah nah, fuck that bro. Go gym dude and be the best version of yourself. Listen to '9 Shots' by 50 and remember we are all we got"
//            Negative responses
            options2["Horrible"]?.get(0) -> "Why?"
            options2["Horrible"]?.get(1) -> "I hear you, what's up?"
            options2["Horrible"]?.get(2) -> "It be like that dude, what's the real issue"
            options2["Horrible"]?.get(3) -> "Damn bro, why so?"
            options2["Horrible"]?.get(0) -> "Ay, standard day, keep pushing"
            negative["I feel Like quitting"]?.get(0) -> "Dude"
            negative["I feel Like quitting"]?.get(0) -> "Dude"
            else -> "What's the word?"


        }

        val mess = Message("Typing...", false)
        adapter.addMessage(mess)
        lifecycleScope.launch{
            delay(2000)
            mess.text = botReply
            adapter.notifyDataSetChanged()
        }

        chatRecyclerView.scrollToPosition(messages.size - 1)
        responseButtons(message)
    }

    fun responseButtons(message : String = ""){
        buttonContainer.removeAllViews()
//        Positive Inputs
        if(message.lowercase() == "great"){
            val responses = options2["Great"]
            responses?.forEach { response ->
                val button = Button(this)
                button.text = response
                buttonContainer.addView(button)
                button.setOnClickListener {
                    adapter.addMessage(Message(response, true))
                    responses(response)
                }
            }
        }else if(message.trim().lowercase() == "i accomplished something"){
            val responses = positive["I accomplished something"]
            responses?.forEach { response ->
            val button = Button(this)
                button.text = response
                buttonContainer.addView(button)
                button.setOnClickListener {
                    adapter.addMessage(Message(response, true))
                    responses(response)
                    buttonContainer.removeAllViews()
                    inputLayout.visibility = View.VISIBLE

                }
            }
        }else if(message.trim().lowercase() == "i love my physique"){
                adapter.addMessage(Message(null, false, null, null, R.raw.motion))
                buttonContainer.removeAllViews()
                inputLayout.visibility = View.VISIBLE
    }

//    Mild Inputs
    else if(message.trim().lowercase() == "okay"){
            val responses = options2["Okay"]
            responses?.forEach { response ->
                val button = Button(this)
                button.text = response
                buttonContainer.addView(button)
                button.setOnClickListener {
                    adapter.addMessage(Message(response, true))
                    responses(response)
                }
            }
    }else if(message.trim().lowercase() == "nothing much broski"){
            buttonContainer.removeAllViews()
            inputLayout.visibility = View.VISIBLE
    }else if(message.trim().lowercase() == "just a normal day"){
            buttonContainer.removeAllViews()
            inputLayout.visibility = View.VISIBLE
    }else if(message.trim().lowercase() == "a bit stressed"){
            val responses = mild["A bit Stressed"]
            responses?.forEach { response ->
                val button = Button(this)
                button.text = response
                buttonContainer.addView(button)
                button.setOnClickListener {
                    adapter.addMessage(Message(response, true))
                    responses(response)
                }
            }
        }else if(message.trim().lowercase() == "trying to reach ig goal"){
            buttonContainer.removeAllViews()
            inputLayout.visibility = View.VISIBLE
        }else if(message.trim().lowercase() == "trying to reach website goal"){
            buttonContainer.removeAllViews()
            inputLayout.visibility = View.VISIBLE
        }else if(message.trim().lowercase() == "struggling to focus"){
            buttonContainer.removeAllViews()
            inputLayout.visibility = View.VISIBLE
        }else if(message.trim().lowercase() == "don't feel like going to gym") {
            buttonContainer.removeAllViews()
            inputLayout.visibility = View.VISIBLE
        }
//Negative inputs
        else if(message.trim().lowercase() == "horrible"){
            val responses = options2["Horrible"]
            responses?.forEach { response ->
                val button = Button(this)
                button.text = response
                buttonContainer.addView(button)
                button.setOnClickListener {
                    adapter.addMessage(Message(response, true))
                    responses(response)
                }
            }
        }else if(message.trim().lowercase() == "i feel like quitting"){
            val responses = negative["I feel Like quitting"]
            responses?.forEach { response ->
                val button = Button(this)
                button.text = response
                buttonContainer.addView(button)
                button.setOnClickListener {
                    adapter.addMessage(Message(response, true))
                    responses(response)
                }
            }
        }else {
            for (option in options) {
                val button = Button(this)
                button.text = option.lowercase().replaceFirstChar { it.uppercase() }
                buttonContainer.addView(button)
                button.setOnClickListener {
                    adapter.addMessage(Message(option, true))
                    responses(option)
                }

            }
        }



    }
}
