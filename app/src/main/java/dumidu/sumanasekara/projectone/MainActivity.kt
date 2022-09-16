package dumidu.sumanasekara.projectone

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private var colorBackground: ConstraintLayout? = null
    private var colorFragment: LinearLayout? = null
    private var seasonImage: ImageView? = null
    private var wheelOfLife: ImageView? = null
    lateinit var mediaPlayer: MediaPlayer
    private var ANIMAITION_DURATION = 5000
    private var FRAGMENT_ANIMATION_DURATION = 60000
    private var index = -1
    private var imageIndex = -1
    lateinit var sunImage: ImageView
    lateinit var cloudImage: ImageView
    lateinit var birdImage: ImageView
    val dateformat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    var timer: Timer? = null
    val seasonColors = arrayOf("#FF4500", "#8FBC8F", "#FFFF00", "#FFFFFF")
    val seasonImages = arrayOf(R.drawable.spring, R.drawable.summer, R.drawable.autumn, R.drawable.winter)
    val seasonsounds = arrayOf(R.raw.spring_song, R.raw.summer_song, R.raw.autumn_song, R.raw.winter_song)

    lateinit var seasonFragment: SeasonFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        colorBackground = findViewById<View>(R.id.colorBackground) as ConstraintLayout

        var moveSun : Animation? = null
        var moveClouds : Animation? = null
        var moveBirds : Animation? = null
        var rotate: Animation? = null

        moveSun = AnimationUtils.loadAnimation(applicationContext, R.anim.movesun)
        moveClouds = AnimationUtils.loadAnimation(applicationContext, R.anim.moveclouds)
        moveBirds = AnimationUtils.loadAnimation(applicationContext, R.anim.movebirds)
        rotate = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate)

        // get components
        sunImage = findViewById(R.id.sunImage)
        cloudImage = findViewById(R.id.cloudImage)
        birdImage = findViewById(R.id.birdImage)
        val startButton = findViewById<Button>(R.id.buttonStart)
        val stopButton = findViewById<Button>(R.id.buttonStop)

        val colorAnim: ValueAnimator = ObjectAnimator.ofInt(
            colorBackground, "backgroundColor",
            Color.parseColor("#50b8e7"),
            Color.parseColor("#84cdee") ,
            Color.parseColor("#b9e2f5"),
            Color.parseColor("#dcf0fa"),
            Color.parseColor("#edf7fc")
        )
        colorAnim.duration = ANIMAITION_DURATION.toLong()
        colorAnim.repeatCount = ValueAnimator.INFINITE
        colorAnim.repeatMode = ValueAnimator.REVERSE
        colorAnim.setEvaluator(ArgbEvaluator())
        colorAnim.start()


        startButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View?) {
                sunImage.startAnimation(moveSun)
                cloudImage.startAnimation(moveClouds)
                birdImage.startAnimation(moveBirds)
                wheelOfLife?.startAnimation(rotate)
                sendTimeData()
                updateFragmentColor()
            }
        })

        stopButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(view: View?) {
                sunImage.clearAnimation()
                cloudImage.clearAnimation()
                birdImage.clearAnimation()
                wheelOfLife?.clearAnimation()
                stopTimer()
                stopMusic()
            }
        })

    }

    override fun onStart() {
        super.onStart()
        seasonFragment = supportFragmentManager.findFragmentById(R.id.seasonFragment) as SeasonFragment
        colorFragment = findViewById<LinearLayout>(R.id.colorFragment)
        seasonImage = findViewById<ImageView>(R.id.imageSeason)
        wheelOfLife = findViewById<ImageView>(R.id.imageWheel)
    }

    // update time function
    private fun sendTimeData(){

        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                updateTimer()
            }
        }, 0, 1000)

    }

    private fun updateTimer() {
        runOnUiThread {
            seasonFragment.timeView.text = dateformat.format(Date())
        }
    }

    private fun stopTimer() {
        if (timer != null) {
            timer?.cancel()
            timer?.purge()
            timer = null
        }
    }

    private fun updateFragmentColor(){

        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {

                index ++
                imageIndex ++

                if(index >= seasonColors.size){
                    index = 0
                }

                if(index >= seasonImages.size){
                    imageIndex = 0
                }

                var seasonColor = seasonColors.get(index)
                playMusic(index)
                fragmentColorChanger(seasonColor)
                changeFragmentImage(imageIndex)

            }
        }, 0, 15000)

    }

    private fun fragmentColorChanger(seasonColor: String){

        runOnUiThread{
            val colorAnim: ValueAnimator = ObjectAnimator.ofInt(
                colorFragment, "backgroundColor",
                Color.parseColor(seasonColor),
                Color.parseColor(seasonColor)

            )
            colorAnim.duration = FRAGMENT_ANIMATION_DURATION.toLong()
            colorAnim.repeatCount = ValueAnimator.INFINITE
            colorAnim.repeatMode = ValueAnimator.RESTART
            colorAnim.setEvaluator(ArgbEvaluator())
            colorAnim.start()
        }

    }

    private fun changeFragmentImage(position: Int){

        runOnUiThread{
            seasonImage?.setImageResource(seasonImages.get(position))
        }

    }

    private fun playMusic(position: Int){

        mediaPlayer = MediaPlayer.create(applicationContext, seasonsounds.get(position))

        if(mediaPlayer.isPlaying){
            mediaPlayer.pause()
        }

        if(!mediaPlayer.isPlaying){
            mediaPlayer.start()
        }

    }

    private fun stopMusic(){

        if(mediaPlayer != null){
            mediaPlayer.stop()
        }

    }


}