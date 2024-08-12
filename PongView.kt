package com.example.ponggame

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.res.ResourcesCompat
import kotlinx.coroutines.*

class PongView(context: Context, attrs: AttributeSet? = null) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private val maxBalls = 3  // Set maximum number of balls to 3
    private var balls = mutableListOf<Ball>()
    private var paddleX =  0f
    private var paddleWidth = 270f
    private var paddleHeight = 50f
    private var screenWidth = 0f
    private var screenHeight = 0f
    private var score = 0
    private var isGameOver = false
    private var bounceCount = 0

    private lateinit var backgroundBitmap: Bitmap

    private val trailList = mutableListOf<Pair<Float, Float>>()
    private val maxTrailSize = 20
    private var trailAlpha = 70

    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    private val ballGlowPaint = Paint().apply {
        isAntiAlias = true
        maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
    }

    private val trailPaint = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        alpha = trailAlpha
    }

    private val paddleGradientPaint = Paint().apply {
        isAntiAlias = true
    }

    private val scorePaint = Paint().apply {
        typeface = ResourcesCompat.getFont(context, R.font.silkscreen_regular)  // Use the font from res/font
        color = Color.YELLOW
        textSize = 100f
        textAlign = Paint.Align.CENTER
    }

    private val gameOverPaint = Paint().apply {
        typeface = ResourcesCompat.getFont(context, R.font.silkscreen_bold)  // Use the font from res/font
        color = Color.RED
        textSize = 100f
        textAlign = Paint.Align.CENTER
    }

    private val restartPaint = Paint().apply {
        typeface = ResourcesCompat.getFont(context, R.font.silkscreen_bold)  // Use the font from res/font
        color = Color.YELLOW
        textSize = 50f
        textAlign = Paint.Align.CENTER
    }

    private val paddleRect = RectF()

    private var gameJob: Job? = null

    private val scoreMarginTop = 150f  // Adjust this value to increase or decrease the empty space above the score

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        screenWidth = width.toFloat()
        screenHeight = height.toFloat()

        try {
            backgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.background)
            backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, width, height, true)
        } catch (e: Exception) {
            Log.e("PongView", "Error loading background bitmap", e)
        }

        resetBalls()
        resetPaddle()
        startGame()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        stopGame()
    }

    private fun resetBalls() {
        balls.clear()
        balls.add(Ball(screenWidth / 2, screenHeight / 2))
    }

    private fun resetPaddle() {
        paddleX = (screenWidth - paddleWidth) / 2
    }

    private fun startGame() {
        gameJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                if (isGameOver) {
                    drawGame()
                    delay(100)
                } else {
                    updateGame()
                    drawGame()
                    delay(16)
                }
            }
        }
    }

    private fun stopGame() {
        gameJob?.cancel()
    }

    private fun updateGame() {
        if (isGameOver) return

        balls.forEach { ball ->
            ball.x += ball.speedX
            ball.y += ball.speedY

            if (trailAlpha > 0) {
                trailList.add(Pair(ball.x, ball.y))
                if (trailList.size > maxTrailSize) {
                    trailList.removeAt(0)
                }
            }

            if (ball.x < ball.radius || ball.x > screenWidth - ball.radius) {
                ball.speedX = -ball.speedX
            }
            if (ball.y < ball.radius) {
                ball.speedY = -ball.speedY
            }

            val paddleTop = screenHeight - paddleHeight * 7
            val paddleBottom = paddleTop + paddleHeight

            if (ball.y + ball.radius >= paddleTop && ball.y - ball.radius <= paddleBottom &&
                ball.x in paddleX..(paddleX + paddleWidth)) {

                if (ball.speedY > 0) {
                    ball.speedY = -ball.speedY
                    if (ball.y > ball.lastPaddleHitY) {
                        score++
                        bounceCount++
                        ball.lastPaddleHitY = ball.y
                        increaseBallsIfNeeded()
                        trailAlpha = 255
                        increaseBallSpeed()
                    }
                }
            } else {
                ball.lastPaddleHitY = -1f
            }

            if (ball.y > screenHeight) {
                isGameOver = true
                balls.forEach { it.speedX = 0f; it.speedY = 0f }
            }
        }

        if (trailAlpha > 20) {
            trailAlpha -= 5
        }
    }

    private fun increaseBallsIfNeeded() {
        if (bounceCount % 5 == 0 && bounceCount > 0 && balls.size < maxBalls) {
            balls.add(Ball(screenWidth / 2, screenHeight / 2))
        }
    }

    private fun increaseBallSpeed() {
        if (bounceCount % 5 == 0 && bounceCount > 0) {
            balls.forEach { ball ->
                ball.speedX *= 1.1f  // Increase speed by 10%
                ball.speedY *= 1.1f
            }
        }
    }

    private fun drawGame() {
        val canvas: Canvas? = try {
            holder.lockCanvas()
        } catch (e: Exception) {
            Log.e("PongView", "Error locking canvas", e)
            null
        }
        canvas?.let {
            try {
                synchronized(holder) {
                    it.drawBitmap(backgroundBitmap, 0f, 0f, null)

                    for (i in trailList.indices) {
                        val (x, y) = trailList[i]
                        trailPaint.alpha = trailAlpha
                        val trailLength = (i + 1) * 3
                        it.drawCircle(x, y, trailLength.toFloat(), trailPaint)
                    }

                    balls.forEach { ball ->
                        ballGlowPaint.shader = RadialGradient(ball.x, ball.y, ball.radius * 2,
                            Color.WHITE, Color.TRANSPARENT, Shader.TileMode.CLAMP)
                        it.drawCircle(ball.x, ball.y, ball.radius + 15, ballGlowPaint)
                        it.drawCircle(ball.x, ball.y, ball.radius, paint)
                    }

                    val paddleTop = screenHeight - paddleHeight * 7
                    paddleRect.set(paddleX, paddleTop, paddleX + paddleWidth, paddleTop + paddleHeight)
                    val paddleShader = LinearGradient(paddleX, paddleTop, paddleX + paddleWidth, paddleTop + paddleHeight,
                        Color.CYAN, Color.BLUE, Shader.TileMode.CLAMP)
                    paddleGradientPaint.shader = paddleShader
                    it.drawRoundRect(paddleRect, 30f, 30f, paddleGradientPaint)

                    // Adjust the y position to include margin
                    it.drawText("Score: $score", screenWidth / 2, scoreMarginTop + scorePaint.textSize, scorePaint)

                    if (isGameOver) {
                        it.drawText("Game Over", screenWidth / 2, screenHeight / 2 - 20, gameOverPaint)
                        it.drawText("Tap anywhere to restart", screenWidth / 2, screenHeight / 2 + 60, restartPaint)
                    }
                }
            } catch (e: Exception) {
                Log.e("PongView", "Error drawing game", e)
            } finally {
                holder.unlockCanvasAndPost(it)
            }
        }
    }

    fun updatePaddlePosition(x: Float) {
        if (isGameOver) return

        paddleX = x - paddleWidth / 2
        paddleX = paddleX.coerceIn(0f, screenWidth - paddleWidth)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                if (!isGameOver) {
                    updatePaddlePosition(event.x)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isGameOver) {
                    resetGame()
                }
            }
        }
        return true
    }

    private fun resetGame() {
        resetBalls()
        resetPaddle()
        score = 0
        bounceCount = 0
        isGameOver = false
        startGame()
    }

    fun pause() {
        stopGame()
    }

    fun resume() {
        if (isGameOver) return
        startGame()
    }

    private data class Ball(
        var x: Float,
        var y: Float,
        val radius: Float = 30f,
        var speedX: Float = 9f,
        var speedY: Float = 9f,
        var lastPaddleHitY: Float = -1f
    )
}
